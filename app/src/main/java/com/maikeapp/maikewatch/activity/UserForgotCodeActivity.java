package com.maikeapp.maikewatch.activity;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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


public class UserForgotCodeActivity extends Activity {
    private ImageView mIvBack;
    private TextView mTvTitle;
    private Button mBtnCommitCode;

    //手机号和验证码
    private String mPhone;
    private String mSmsCode;

    //手机介绍信息
    private TextView mTvPhoneInfo;
    //再次获取验证码
    private TextView mTvGetSmsAgain;

    //验证码
    private EditText mEtSmsCode;
    private EditText mEtPassword;
    private EditText mEtPassAgain;

    //业务层
    private IUserBusiness mUserBusiness = new UserBusinessImp();
    private static ProgressDialog mProgressDialog = null;

    //定义倒计时handler
    private static Handler getcodeHandler;
    private int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_forgot_code);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_common_topbar_back);
        mTvTitle = (TextView) findViewById(R.id.tv_common_topbar_title);
        mBtnCommitCode = (Button) findViewById(R.id.btn_user_forgot_commit);

        //手机号介绍信息
        mTvPhoneInfo = (TextView) findViewById(R.id.tv_user_forgot_intro);
        //验证码
        mEtSmsCode = (EditText) findViewById(R.id.et_user_forgot_code);
        //新密码和确认密码
        mEtPassword = (EditText) findViewById(R.id.et_user_forgot_password);
        mEtPassAgain = (EditText) findViewById(R.id.et_user_forgot_password_again);
    }

    private void initData() {
        mTvTitle.setText("密码重置");
        //初始化phone字符串和显示说明信息
        Bundle _bundle = getIntent().getExtras();
        if (_bundle != null) {
            mPhone = _bundle.getString("_phone");
            mTvPhoneInfo.setText(Html.fromHtml("已向您的手机<font color='#d81759'>" + mPhone + "</font>发送验证码"));
        }
        //60s后重新获取验证码
        canGetSmsCodeAgain();
    }

    /**
     * 是否可以再次发送验证码
     */
    private void canGetSmsCodeAgain() {
        //倒计时获取验证码
        mTvGetSmsAgain = (TextView) findViewById(R.id.tv_user_forgot_code_getcode_again);
        getcodeHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //接文字更新页面
                int flag = msg.what;
                if (flag == CommonConstants.FLAG_GET_USER_FORGOT_SMS_CAN_GET_AGAIN_SUCCESS) {
                    int num = msg.getData().getInt("number");
                    if (num == 0) {
                        //倒计时结束
                        mTvGetSmsAgain.setText("重新发送验证码");
                    } else {
                        //倒计时
                        mTvGetSmsAgain.setText("重新发送验证码(" + num + "s)");
                    }
                }

            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i;
                for (i = 119; i >= 0; i--) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putInt("number", i);
                    msg.what = CommonConstants.FLAG_GET_USER_FORGOT_SMS_CAN_GET_AGAIN_SUCCESS;
                    msg.setData(data);
                    getcodeHandler.sendMessage(msg);
                }

            }
        }).start();
    }

    /**
     * 发送消息到手机
     */
    private String sendSMSToPhone() {
        try {
            String result = mUserBusiness.getTokenID();
            Log.e("tokenID", result + "YZP");
            JSONObject _token = new JSONObject(result);
            String datas = JsonUtils.getString(_token, "Datas");
            JSONObject _datas = new JSONObject(datas);
            String _tokenID = JsonUtils.getString(_datas, "TokenID");

            String PicNumber = null;
            String _result = mUserBusiness.getMobilemsgRegister(mPhone, _tokenID, PicNumber);
            Log.e("_result-------------", _result);
            handler.sendEmptyMessage(CommonConstants.FLAG_GET_REG_MOBILEMGS_REGISTER_SUCCESS);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setListener() {
        mIvBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                UserForgotCodeActivity.this.finish();
            }
        });
        mTvGetSmsAgain.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                String getcodeText = mTvGetSmsAgain.getText().toString();
                if (getcodeText != null && getcodeText.equals("重新发送验证码")) {
                    //重新发送验证码到该手机
                    checkPhoneFromNetGetSmsCodeAgain(mPhone);
                } else {
                    Toast.makeText(UserForgotCodeActivity.this, "短信验证码正在发送,请耐心等待", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //提交-密码重置
        mBtnCommitCode.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                mSmsCode = mEtSmsCode.getText().toString();
                String newpwd = mEtPassword.getText().toString();
                String newpwdAgain = mEtPassAgain.getText().toString();

                if (mSmsCode == null || mSmsCode.equals("")) {
                    Toast.makeText(UserForgotCodeActivity.this, "您未填写短信验证码", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (newpwd == null || newpwd.equals("")) {
                    Toast.makeText(UserForgotCodeActivity.this, "您未填写新密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newpwdAgain == null || newpwdAgain.equals("")) {
                    Toast.makeText(UserForgotCodeActivity.this, "您未填写确认密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newpwd.equals(newpwdAgain)) {
                    Toast.makeText(UserForgotCodeActivity.this, "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newpwd.length() < 6 || newpwd.length() > 15 || !CheckUtil.checkPassword(newpwd)) {
                    Toast.makeText(UserForgotCodeActivity.this, "密码格式是6到15位的字母数字组成", Toast.LENGTH_SHORT).show();
                    return;
                }
                //弹出加载进度条
                mProgressDialog = ProgressDialog.show(UserForgotCodeActivity.this, null, "正在玩命提交中...", true, true);

                //开启副线程-提交重置密码
                UserForgotPwdUpdateFromNet(mSmsCode, newpwd);
                //若检查不通过、提示报错信息；检查通过，提示密码重置成功
            }

            private void UserForgotPwdUpdateFromNet(final String smsCode, final String newpwd) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String result = mUserBusiness.getUserForgotPasswordTwo(mPhone,newpwd,smsCode);
                            JSONObject jsonObj = new JSONObject(result);
                            boolean Success = jsonObj.getBoolean("Success");
                            if (Success) {
                                //获取成功
                                handler.sendEmptyMessage(CommonConstants.FLAG_GET_USER_FORGOT_PWD_UPDATE_SUCCESS);
                            } else {
                                //获取错误代码，并查询出错误文字
                                String errorMsg = jsonObj.getString("Message");
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
                            CommonUtil.sendErrorMessage("密码重置：" + CommonConstants.MSG_GET_ERROR, handler);
                        }
                    }
                }).start();
            }
        });
    }


    private static class MyHandler extends Handler {
        private final WeakReference<Activity> mActivity;

        public MyHandler(UserForgotCodeActivity activity) {
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
                    ((UserForgotCodeActivity) mActivity.get()).showTip(errorMsg);
                    break;
                case CommonConstants.FLAG_GET_USER_FORGOT_PWD_ONE_SUCCESS:
                    //重新获取验证码成功
                    ((UserForgotCodeActivity) mActivity.get()).showTip("已重新发送验证码");
                    break;
                case CommonConstants.FLAG_GET_USER_FORGOT_PWD_UPDATE_SUCCESS:
                    //重置密码成功
                    ((UserForgotCodeActivity) mActivity.get()).goNextActivity();
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

    public void goNextActivity() {
        this.showTip("密码已重置,请重新登录");
        Intent _intent = new Intent(UserForgotCodeActivity.this, UserLoginActivity.class);
        startActivity(_intent);
        UserForgotCodeActivity.this.finish();
    }

    //重新获取验证码
    private void checkPhoneFromNetGetSmsCodeAgain(final String phone) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = sendSMSToPhone();
                    JSONObject jsonObj = new JSONObject(result);
                    boolean Success = JsonUtils.getBoolean(jsonObj,"Success");
                    if (Success) {
                        //获取成功
                        handler.sendEmptyMessage(CommonConstants.FLAG_GET_USER_FORGOT_PWD_ONE_SUCCESS);
                    } else {
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        //获取错误代码，并查询出错误文字
                        String errorMsg = jsonObj.getString("errorMsg");
                        data.putSerializable("ErrorMsg", errorMsg);
                        msg.setData(data);
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    //what = 0;sendmsg 0;
                    e.printStackTrace();
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putSerializable("ErrorMsg", "密码重置-重新获取验证码：" + CommonConstants.MSG_GET_ERROR);
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
}
