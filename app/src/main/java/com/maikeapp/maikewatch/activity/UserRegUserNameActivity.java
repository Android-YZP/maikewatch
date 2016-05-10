package com.maikeapp.maikewatch.activity;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.business.IUserBusiness;
import com.maikeapp.maikewatch.business.imp.UserBusinessImp;
import com.maikeapp.maikewatch.config.CommonConstants;
import com.maikeapp.maikewatch.exception.ServiceException;
import com.maikeapp.maikewatch.util.CheckUtil;
import com.maikeapp.maikewatch.util.CommonUtil;
import com.maikeapp.maikewatch.util.JsonUtils;

public class UserRegUserNameActivity extends Activity {
	private ImageView mIvBack;
	private TextView mTvTitle;
	
	//用户名、密码和确认密码
	private EditText mEtUsername;
	private EditText mEtPassword;
	private EditText mEtPassAgain;
	private Button mBtnRegister;
	
	private String mPhone;
	private String mCode;
	private String mPassword;
	
	//业务层
	private IUserBusiness mUserBusiness = new UserBusinessImp();
	
	private static ProgressDialog mProgressDialog = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_reg_username);
		initView();
		initData();
		setListener();
	}

	private void initView() {
		mIvBack = (ImageView)findViewById(R.id.iv_common_topbar_back);
		mTvTitle = (TextView)findViewById(R.id.tv_common_topbar_title);
		//用户名、密码和确认密码
		mEtUsername = (EditText)findViewById(R.id.et_user_reg_username);
		mEtPassword = (EditText)findViewById(R.id.et_user_reg_password);
		mEtPassAgain = (EditText)findViewById(R.id.et_user_reg_password_again);
		
		mBtnRegister = (Button)findViewById(R.id.btn_user_reg_register_commit);
	}

	private void initData() {
		mTvTitle.setText("注册");
		//初始化手机号和验证码
		Bundle _bundle = getIntent().getExtras();
		if(_bundle!=null){
			mPhone = _bundle.getString("_phone");
			mCode = _bundle.getString("_code");
		}
	}

	private void setListener() {
		mIvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				UserRegUserNameActivity.this.finish();
			}
		});
		//注册
		mBtnRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				String username = mEtUsername.getText().toString();
				String password = mEtPassword.getText().toString();
				String passagain = mEtPassAgain.getText().toString();
				if(username==null||username.equals("")){
					Toast.makeText(UserRegUserNameActivity.this, "您未填写用户名", Toast.LENGTH_SHORT).show();
					return;
				}
				if(password==null||password.equals("")){
					Toast.makeText(UserRegUserNameActivity.this, "您未填写密码", Toast.LENGTH_SHORT).show();
					return;
				}
				if(passagain==null||passagain.equals("")){
					Toast.makeText(UserRegUserNameActivity.this, "您未填写确认密码", Toast.LENGTH_SHORT).show();
					return;
				}
				if(!password.equals(passagain)){
					Toast.makeText(UserRegUserNameActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
					return;
				}
				//检查用户名和密码、确认密码的输入规则是否输入有误
				if(username.length()<3||username.length()>15||!CheckUtil.checkUsername(username)){
					Toast.makeText(UserRegUserNameActivity.this, "用户名格式是3到15位的中英文数字组成", Toast.LENGTH_SHORT).show();
					return;
				}
				if(passagain.length()<6||passagain.length()>15||!CheckUtil.checkPassword(password)){
					Toast.makeText(UserRegUserNameActivity.this, "密码格式是6到15位的字母数字组成", Toast.LENGTH_SHORT).show();
					return;
				}
				mPassword = password;//全局变量
				userRegisterFromNet(username,password);//用户注册
			}

			private void userRegisterFromNet(final String username,final String password) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							String result = mUserBusiness.getUserRegister(mPhone,mCode,username,password);
							JSONObject jsonObj = new JSONObject(result);
							boolean Success = jsonObj.getBoolean("success");
							if(Success){
								//获取成功
								handler.sendEmptyMessage(CommonConstants.FLAG_GET_REG_USER_REGISTER_SUCCESS);
							}else{
								//获取错误代码，并查询出错误文字
								String errorMsg = jsonObj.getString("errorMsg");
								CommonUtil.sendErrorMessage(errorMsg,handler);
							}
						} catch (ConnectTimeoutException e) {
							e.printStackTrace();
							CommonUtil.sendErrorMessage(CommonConstants.MSG_REQUEST_TIMEOUT,handler);
						}catch (SocketTimeoutException e) {
							e.printStackTrace();
							CommonUtil.sendErrorMessage(CommonConstants.MSG_SERVER_RESPONSE_TIMEOUT,handler);
						}
						catch (ServiceException e) {
							e.printStackTrace();
							CommonUtil.sendErrorMessage(e.getMessage(),handler);
						} catch (Exception e) {
							//what = 0;sendmsg 0;
							CommonUtil.sendErrorMessage("注册-验证验证码："+CommonConstants.MSG_GET_ERROR,handler);
						}
					}
				}).start();
			}
		});
	}
	
	private static class MyHandler extends Handler{
		private final WeakReference<Activity> mActivity;
		public MyHandler(UserRegUserNameActivity activity) {
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
				((UserRegUserNameActivity)mActivity.get()).showTip(errorMsg);
				break;
			case CommonConstants.FLAG_GET_REG_USER_REGISTER_SUCCESS:
				((UserRegUserNameActivity)mActivity.get()).regUserLogin();
				break;
			case CommonConstants.FLAG_GET_REG_USER_LOGIN_SUCCESS:
				((UserRegUserNameActivity)mActivity.get()).saveUserInfo();
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

	public void regUserLogin(){
		//开启副线程-发起登录
		userLoginFromNet(mPhone,mPassword);
	}
	
	public void saveUserInfo() {
		//保存用户信息，并关闭该界面
		Log.d(CommonConstants.LOGCAT_TAG_NAME+"_user_login_info", mUser.toString());
		CommonUtil.saveUserInfo(mUser,this);
		Toast.makeText(UserRegUserNameActivity.this, "注册成功", Toast.LENGTH_LONG).show();
		UserRegUserNameActivity.this.finish();
	}

	public void saveUserInfo(User user){
		//构建对象
		Gson gson = new Gson();
		String gsonUser = gson.toJson(user);
		//获取指定Key的SharedPreferences对象
		SharedPreferences _SP = getSharedPreferences("UserInfo", MODE_PRIVATE);
		//获取编辑
		SharedPreferences.Editor _Editor = _SP.edit();
		//按照指定Key放入数据
		_Editor.putString("user", gsonUser);
		//提交保存数据
		_Editor.commit();
	}
	
	private User mUser;
	private void userLoginFromNet(final String pPhone,final String pPassword) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String result = mUserBusiness.getUserLogin(pPhone,pPassword);
					JSONObject jsonObj = new JSONObject(result);
					boolean Success = jsonObj.getBoolean("success");
					if(Success){
						//填充用户信息
						fullUserInfo(jsonObj);
						//获取成功
						handler.sendEmptyMessage(CommonConstants.FLAG_GET_REG_USER_LOGIN_SUCCESS);
					}else{
						Message msg = new Message();
						Bundle data = new Bundle();
						//获取错误代码，并查询出错误文字
						String errorMsg = jsonObj.getString("errorMsg");
						data.putSerializable("ErrorMsg", errorMsg);
						msg.setData(data);
						handler.sendMessage(msg);
					}
				} catch (ConnectTimeoutException e) {
					e.printStackTrace();
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putSerializable("ErrorMsg", CommonConstants.MSG_REQUEST_TIMEOUT);
					msg.setData(data);
					handler.sendMessage(msg);
				}catch (SocketTimeoutException e) {
					e.printStackTrace();
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putSerializable("ErrorMsg", CommonConstants.MSG_SERVER_RESPONSE_TIMEOUT);
					msg.setData(data);
					handler.sendMessage(msg);
				}
				catch (ServiceException e) {
					e.printStackTrace();
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putSerializable("ErrorMsg", e.getMessage());
					msg.setData(data);
					handler.sendMessage(msg);
				} catch (Exception e) {
					//what = 0;sendmsg 0;
					e.printStackTrace();
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putSerializable("ErrorMsg", "注册-用户登录："+CommonConstants.MSG_GET_ERROR);
					msg.setData(data);
					handler.sendMessage(msg);
				}
			}

			private void fullUserInfo(JSONObject jsonObj) {
				mUser = new User();
//				mUser.setAccount(JsonUtils.getString(jsonObj, "account"));
//				mUser.setPassword(JsonUtils.getString(jsonObj, "password"));
//				mUser.setId(JsonUtils.getInt(jsonObj, "id"));
//				mUser.setUserImg(JsonUtils.getString(jsonObj, "userImg"));
//				mUser.setUsername(JsonUtils.getString(jsonObj, "username"));
//				mUser.setMobile(JsonUtils.getString(jsonObj, "mobile"));
//				mUser.setVerifyCode(JsonUtils.getString(jsonObj, "verifyCode"));
			}
		}).start();
	}
}
