package com.maikeapp.maikewatch.activity;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.business.IUserBusiness;
import com.maikeapp.maikewatch.business.imp.UserBusinessImp;
import com.maikeapp.maikewatch.config.CommonConstants;
import com.maikeapp.maikewatch.exception.ServiceException;
import com.maikeapp.maikewatch.util.CheckUtil;
import com.maikeapp.maikewatch.util.CommonUtil;
import com.maikeapp.maikewatch.util.JsonUtils;


public class UserRegPhoneActivity extends Activity {
    private ImageView mIvBack;
    private TextView mTvTitle;
    private Button mBtnCommitPhone;
    //手机号
    private EditText mEtPhone;
    //是否选中checkbox
    private CheckBox mCbIsRead;
    private TextView mTvIsRead;
    private TextView mTvProtocal;

    //业务层
    private IUserBusiness mUserBusiness = new UserBusinessImp();
    private String mPhone;

    private static ProgressDialog mProgressDialog = null;
    private String mTokenID;
    private String tokenID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reg_phone);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_common_topbar_back);
        mTvTitle = (TextView) findViewById(R.id.tv_common_topbar_title);
        mBtnCommitPhone = (Button) findViewById(R.id.btn_user_reg_getcode_phone_commit);

        mEtPhone = (EditText) findViewById(R.id.et_user_reg_phone);
        mCbIsRead = (CheckBox) findViewById(R.id.cb_user_reg_phone_ischecked);

        mTvIsRead = (TextView) findViewById(R.id.tv_user_reg_phone_isread);
        mTvProtocal = (TextView) findViewById(R.id.tv_user_reg_phone_read_protocal);
    }

    private void initData() {
        mTvTitle.setText("注册");
        getToken();
    }

    //获取token信息
    private void getToken() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mTokenID = mUserBusiness.getTokenID();
                    Log.e("tokenID", mTokenID + "YZP");

                    JSONObject _token = new JSONObject(mTokenID);
                    String datas = JsonUtils.getString(_token, "Datas");
//                    Log.e("datas",datas+"YZP");
                    JSONObject _datas = new JSONObject(datas);
                    tokenID = JsonUtils.getString(_datas, "TokenID");
                    Log.e("_tokenID", tokenID +"YZP");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setListener() {
        mIvBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                UserRegPhoneActivity.this.finish();
            }
        });
        mBtnCommitPhone.setOnClickListener(new UserRegPhoneOnClickListener());
        mTvIsRead.setOnClickListener(new UserRegPhoneOnClickListener());
        mTvProtocal.setOnClickListener(new UserRegPhoneOnClickListener());
    }


    private static class MyHandler extends Handler {
        private final WeakReference<Activity> mActivity;

        public MyHandler(UserRegPhoneActivity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            int flag = msg.what;
            switch (flag) {
                case 0:
                    String errorMsg = (String) msg.getData().getSerializable("ErrorMsg");
                    ((UserRegPhoneActivity) mActivity.get()).showTip(errorMsg);
                    break;
                case CommonConstants.FLAG_GET_REG_MOBILEMGS_REGISTER_SUCCESS:
                    ((UserRegPhoneActivity) mActivity.get()).goNextActivity();
                    break;
                default:
                    break;
            }
        }
    }

    private MyHandler handler = new MyHandler(this);

    private void showTip(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * 进入下一个注册流程
     */
    public void goNextActivity() {
        Intent _intent = new Intent(UserRegPhoneActivity.this, UserRegCodeActivity.class);
        _intent.putExtra("_phone", mPhone);
        startActivity(_intent);
        UserRegPhoneActivity.this.finish();
    }

    private class UserRegPhoneOnClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_user_reg_getcode_phone_commit:
                    //提交
                    UserCommitPhoneNumer();
                    break;
                case R.id.tv_user_reg_phone_isread:
                    //是否已读
                    if (mCbIsRead.isChecked()) {
                        mCbIsRead.setChecked(false);
                    } else {
                        mCbIsRead.setChecked(true);
                    }
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * 用户提交手机号码
     */
    public void UserCommitPhoneNumer() {
        mPhone = mEtPhone.getText().toString();
        boolean ischecked = mCbIsRead.isChecked();
        if (!ischecked) {
            Toast.makeText(UserRegPhoneActivity.this, "您未同意注册协议", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mPhone != null && !mPhone.equals("")) {
            if (!CheckUtil.checkMobile(mPhone)) {
                Toast.makeText(UserRegPhoneActivity.this, "手机号格式输入有误", Toast.LENGTH_LONG).show();
                return;
            }

            //弹出加载进度条
            mProgressDialog = ProgressDialog.show(UserRegPhoneActivity.this, "请稍等", "正在玩命获取中...", true, true);

//			mProgressDialog = new ProgressDialog(UserRegPhoneActivity.this, R.style.progress_dialog);
//			mProgressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//			mProgressDialog.show();

            //开启副线程-检查手机号码是否存在
            checkPhoneFromNet(mPhone);
            //若检查不通过、提示报错信息；检查通过，跳转到下一个界面
            //进度条消失
        } else {
            Toast.makeText(UserRegPhoneActivity.this, "您未填写手机号", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 开启副线程-检查手机号码是否存在
     *
     * @param phone
     */
    private void checkPhoneFromNet(final String phone) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = mUserBusiness.getMobilemsgRegister(phone);
                    Log.e("result",result);
                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result_reg_phone_getMobilemsgRegister", result);
                    JSONObject jsonObj = new JSONObject(result);
                    boolean Success = jsonObj.getBoolean("success");
                    if (Success) {
                        //获取成功
                        handler.sendEmptyMessage(CommonConstants.FLAG_GET_REG_MOBILEMGS_REGISTER_SUCCESS);
                    } else {
                        //获取错误代码，并查询出错误文字
                        String errorMsg = jsonObj.getString("errorMsg");
                        CommonUtil.sendErrorMessage(errorMsg, handler);
                    }
                } catch (ConnectTimeoutException e) {
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage(CommonConstants.MSG_REQUEST_TIMEOUT, handler);
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage(CommonConstants.MSG_SERVER_RESPONSE_TIMEOUT, handler);
                } catch (ServiceException e) {
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage(e.getMessage(), handler);
                } catch (Exception e) {
                    //what = 0;sendmsg 0;
                    CommonUtil.sendErrorMessage("注册-获取验证码：" + CommonConstants.MSG_GET_ERROR, handler);
                }
            }
        }).start();
    }
}
