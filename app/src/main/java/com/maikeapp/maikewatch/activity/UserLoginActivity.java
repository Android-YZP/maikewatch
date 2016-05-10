package com.maikeapp.maikewatch.activity;

import java.lang.ref.WeakReference;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.business.IUserBusiness;
import com.maikeapp.maikewatch.business.imp.UserBusinessImp;
import com.maikeapp.maikewatch.config.CommonConstants;
import com.maikeapp.maikewatch.exception.ServiceException;
import com.maikeapp.maikewatch.util.CommonUtil;
import com.maikeapp.maikewatch.util.JsonUtils;


public class UserLoginActivity extends Activity {
	
	private EditText mEtAccount;//用户名
	private EditText mEtPassword;//密码

	private Button mBtnLogin;//登录按钮
	private TextView mTvGoRegister;//去注册
	private TextView mTvGoForgot;//去忘记密码
	private Button mBtnVisitByEasy;//随便看看

	//业务层
	private IUserBusiness mUserBusiness = new UserBusinessImp();
	
	private static ProgressDialog mProgressDialog = null;
	private User mUser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(checkUserLogin()){
			UserLoginActivity.this.finish();
			return;
		}
		setContentView(R.layout.activity_user_login);
		initView();
		initData();
		setListener();
	}

	private boolean checkUserLogin() {
		User mUser = CommonUtil.getUserInfo(this);
		if(mUser!=null){
			Intent _intent = new Intent(this,MainActivity.class);
			startActivity(_intent);
			return true;
		}else{
			return false;
		}
	}

	private void initView() {
		mEtAccount = (EditText)findViewById(R.id.et_user_login_account);
		mEtPassword = (EditText)findViewById(R.id.et_user_login_password);

		mBtnLogin = (Button)findViewById(R.id.btn_user_login_commit);
		
		mTvGoRegister = (TextView)findViewById(R.id.tv_user_login_reg);
		mTvGoForgot = (TextView)findViewById(R.id.tv_user_login_forget);
		
		mBtnVisitByEasy = (Button)findViewById(R.id.btn_user_login_visit_by_easy);
	}
	private void initData() {

	}
	private void setListener() {
		mBtnLogin.setOnClickListener(new UserLoginOnClickListener());
		mTvGoRegister.setOnClickListener(new UserLoginOnClickListener());
		mTvGoForgot.setOnClickListener(new UserLoginOnClickListener());
		mBtnVisitByEasy.setOnClickListener(new UserLoginOnClickListener());
	}
	
	private static class MyHandler extends Handler{
		private final WeakReference<Activity> mActivity;
		public MyHandler(UserLoginActivity activity) {
			mActivity = new WeakReference<Activity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			if(mProgressDialog!=null){
				mProgressDialog.dismiss();
			}
			int flag = msg.what;
			switch (flag) {
			case 0:
				String errorMsg = (String)msg.getData().getSerializable("ErrorMsg");
				((UserLoginActivity)mActivity.get()).showTip(errorMsg);
				break;
			case CommonConstants.FLAG_GET_REG_USER_LOGIN_SUCCESS:
				((UserLoginActivity)mActivity.get()).saveUserInfo();
				break;
			default:
				break;
			}
		}
	}
	
	private MyHandler handler = new MyHandler(this);
	
	private void showTip(String str){
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 保存用户信息，提醒
	 */
	public void saveUserInfo() {
		//保存用户信息，并关闭该界面
		CommonUtil.saveUserInfo(mUser,this);
		Toast.makeText(UserLoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
		UserLoginActivity.this.finish();
		Intent _intent = new Intent(this,MainActivity.class);
		startActivity(_intent);
	}
	
	private class UserLoginOnClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			Intent _intent = null;
			switch (view.getId()) {
			case R.id.btn_user_login_visit_by_easy:
				//随便看看
				_intent = new Intent(UserLoginActivity.this,MainActivity.class);
				startActivity(_intent);
				UserLoginActivity.this.finish();
				break;
			case R.id.btn_user_login_commit:
				//用户登录提交
				String account = mEtAccount.getText().toString();
				String password = mEtPassword.getText().toString();
				if(account==null||account.equals("")){
					Toast.makeText(UserLoginActivity.this, "您未填写用户名", Toast.LENGTH_SHORT).show();
					return;
				}
				if(password==null||password.equals("")){
					Toast.makeText(UserLoginActivity.this, "您未填写密码", Toast.LENGTH_SHORT).show();
					return;
				}
				
				//弹出加载进度条
				mProgressDialog = ProgressDialog.show(UserLoginActivity.this, "请稍等", "正在玩命登录中...",true,true);
				//开启副线程-发起登录
				userLoginFromNet(account,password);
				break;
			case R.id.tv_user_login_reg:
				//点击用户注册
				_intent = new Intent(UserLoginActivity.this,UserRegPhoneActivity.class);
				startActivity(_intent);
				break;
			case R.id.tv_user_login_forget:
				//点击忘记密码
				_intent = new Intent(UserLoginActivity.this,UserForgotPhoneActivity.class);
				startActivity(_intent);
				break;
			default:
				break;
			}
		}
		/**
		 * 开启副线程-发起登录
		 */
		private void userLoginFromNet(final String account, final String password) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String result = mUserBusiness.getUserLogin(account,password);//获取网络数据
						Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_getUserLogin", result);
						//解析result
						JSONObject jsonObj = new JSONObject(result);
						boolean Success = JsonUtils.getBoolean(jsonObj,"Success");
						if(Success){
							//填充用户信息
							fullUserInfo(jsonObj);
							//获取成功
							handler.sendEmptyMessage(CommonConstants.FLAG_GET_REG_USER_LOGIN_SUCCESS);
						}else{
							//获取错误代码，并查询出错误文字
							String errorMsg = JsonUtils.getString(jsonObj, "Message");
							CommonUtil.sendErrorMessage(errorMsg,handler);
						}
					} catch (ServiceException e) {
						e.printStackTrace();
						CommonUtil.sendErrorMessage(e.getMessage(),handler);
					} catch (Exception e) {
						//what = 0;sendmsg 0;
						CommonUtil.sendErrorMessage("用户登录："+CommonConstants.MSG_GET_ERROR,handler);
					}
				}
				/**
				 * 填充用户信息
				 * @param jsonObj
				 */
				private void fullUserInfo(JSONObject jsonObj) {
					JSONObject _json_user = JsonUtils.getObj(jsonObj,"Datas");//把Datas里面的数据存于user对象
					Log.d(CommonConstants.LOGCAT_TAG_NAME+"_json_user_datas",_json_user.toString());
					Gson gson = new Gson();
					mUser = gson.fromJson(_json_user.toString(),User.class);
					Log.d(CommonConstants.LOGCAT_TAG_NAME+"_user_info",mUser.toString());
				}
			}).start();
		}
		
	}
}
