package com.maikeapp.maikewatch.activity;

import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.BuildConfig;
import com.loopj.android.image.SmartImageView;
import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.business.IUserBusiness;
import com.maikeapp.maikewatch.business.imp.UserBusinessImp;
import com.maikeapp.maikewatch.config.CommonConstants;
import com.maikeapp.maikewatch.exception.ServiceException;
import com.maikeapp.maikewatch.util.CheckUtil;
import com.maikeapp.maikewatch.util.CommonUtil;
import com.maikeapp.maikewatch.util.JsonUtils;


public class UserForgotPhoneActivity extends Activity {
    private static final int PIC_OK = 88;
    private static final int CHANGE_PICNUMBER = 89;
    private static final int NO_TOKENID = 90;
    private static final int TIP_TOKEN_ERROR = 91;
    private static final int TOKEN_ERROR_COUNT = 92;
    private ImageView mIvBack;
    private TextView mTvTitle;
    private Button mBtnCommitPhone;
    //手机号
    private EditText mEtPhone;
    //是否选中checkbox
    private CheckBox mCbIsRead;

    //业务层
    private IUserBusiness mUserBusiness = new UserBusinessImp();
    private String mPhone;
    private static ProgressDialog mProgressDialog = null;
    private static SmartImageView mSivPicNumber;
    private EditText mEtPicNumber;
    private SharedPreferences mSP;
    private static String mPicCodePath;
    private boolean isCheckPicNumber = false;
    private String mMobilemsgRegisterResult;
    private String mPicNumber;
    private String mToken;
    private String mPicNextPath;

    private User mUser;
    private int mTokenCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_forgot_phone);
        mSP = getSharedPreferences("UserInfo", MODE_PRIVATE);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        mIvBack = (ImageView) findViewById(R.id.iv_common_topbar_back);
        mTvTitle = (TextView) findViewById(R.id.tv_common_topbar_title);
        mBtnCommitPhone = (Button) findViewById(R.id.btn_user_reg_getcode_phone_commit2);
        mSivPicNumber = (SmartImageView) findViewById(R.id.siv_pic_number2);
        mEtPicNumber = (EditText) findViewById(R.id.et_pic_number2);
        mEtPhone = (EditText) findViewById(R.id.et_user_reg_phone2);
        mCbIsRead = (CheckBox) findViewById(R.id.cb_user_reg_phone_ischecked2);
    }

    private void initData() {

        mTvTitle.setText("忘记密码");
        mCbIsRead.setChecked(true);
        mCbIsRead.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mSP.getString("mToken", ""))) {
            mSivPicNumber.setVisibility(View.GONE);
            mEtPicNumber.setVisibility(View.GONE);
            isCheckPicNumber = false;
        } else {
            mSivPicNumber.setVisibility(View.VISIBLE);
            mEtPicNumber.setVisibility(View.VISIBLE);
            isCheckPicNumber = true;
        }
        getToken();//拿到token保存本地
    }

    //获取token信息
    private void getToken() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mToken = mSP.getString("mToken", "");
//                    mToken = mUser.getmToken();
                    if (TextUtils.isEmpty(mToken)) {   //不显示图片验证,直接提交号码验证
                        String _result = mUserBusiness.getTokenID();
                        Log.e("tokenID", _result + "YZP");
                        JSONObject _token = new JSONObject(_result);
                        String datas = JsonUtils.getString(_token, "Datas");
                        JSONObject _datas = new JSONObject(datas);
                        String _tokenID = JsonUtils.getString(_datas, "TokenID");
                        Log.e("_tokenID", _tokenID + "YZP12345678");
                        //保存token信息
                        mSP.edit().putString("mToken", _tokenID).apply();
//                        mUser.setmToken(_tokenID);
                    } else { //显示图片验证,提交号码,图片验证码
                        getPicNumber(mToken);//显示图片
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //获取PICnumber
    public void getPicNumber(String token) {
        try {
            String _numberPicResult = mUserBusiness.getNumberPic(token);
            JSONObject object = new JSONObject(_numberPicResult);
            String datas = JsonUtils.getString(object, "Datas");
            String _ErrorCode = JsonUtils.getString(object, "ErrorCode");
            if (_ErrorCode.equals("1008")){
                mTokenCount ++;
                if (mTokenCount >= 5){
                    handler.sendEmptyMessage(TOKEN_ERROR_COUNT);
                    return;
                }

                handler.sendEmptyMessage(NO_TOKENID);
                if (BuildConfig.DEBUG) Log.d("UserRegPhoneActivity", "zoudaozheli");
                return;
            }

            Log.e("haha", datas);
            if (datas != null) {
                JSONObject _picLocation = new JSONObject(datas);
                mPicCodePath = JsonUtils.getString(_picLocation, "PicCodePath");
                Message msg = new Message();
                msg.what = PIC_OK;
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListener() {
        mIvBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                UserForgotPhoneActivity.this.finish();
            }
        });
        mBtnCommitPhone.setOnClickListener(new UserRegPhoneOnClickListener());

        mSivPicNumber.setOnClickListener(new UserRegPhoneOnClickListener());
    }

    public class MyHandler extends Handler {
        private final WeakReference<Activity> mActivity;

        public MyHandler(UserForgotPhoneActivity activity) {
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
                    ((UserForgotPhoneActivity) mActivity.get()).showTip(errorMsg);
                    break;
                case CommonConstants.FLAG_GET_REG_MOBILEMGS_REGISTER_SUCCESS://验证手机号码成功
                    //跳转页面.发送请求验证码
                    ((UserForgotPhoneActivity) mActivity.get()).goNextActivity();
                    break;
                case PIC_OK:
                    //显示图片验证码.
                    mSivPicNumber.setImageUrl(CommonConstants.PIC_LOCATION + mPicCodePath);
                    break;
                case CHANGE_PICNUMBER:
                    //显示图片验证码.
                    mSivPicNumber.setImageUrl(CommonConstants.PIC_LOCATION + mPicNextPath);
                    break;
                case NO_TOKENID:
                    //显示图片验证码.
                    //重新拉去tokenid保存数据
                    getTokenAndShowPic();
                    break;
                case TIP_TOKEN_ERROR:
                    //重新拉去tokenid保存数据
//                    Toast.makeText(UserForgotPhoneActivity.this, "令牌失效,请重新切换图片验证码", Toast.LENGTH_SHORT).show();
                    getPicNumber(mToken);
                    break;
                case TOKEN_ERROR_COUNT:
                    //重新拉去tokenid保存数据
                    Toast.makeText(UserForgotPhoneActivity.this, "令牌数据异常,请重新进入本界面", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }
    public void getTokenAndShowPic(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String _result = null;
                try {
                    _result = mUserBusiness.getTokenID();
                    Log.e("tokenID", _result + "YZP");
                    JSONObject _token = new JSONObject(_result);
                    String datas = JsonUtils.getString(_token, "Datas");
                    JSONObject _datas = new JSONObject(datas);
                    String _tokenID = JsonUtils.getString(_datas, "TokenID");
                    Log.e("_tokenID", _tokenID +"YZP");
                    //保存token信息
                    mSP.edit().putString("mToken", _tokenID).apply();
                    mToken = mSP.getString("mToken", "");//重新拉去Token覆盖原先错误的token
                    handler.sendEmptyMessage(TIP_TOKEN_ERROR);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private MyHandler handler = new MyHandler(this);

    private void showTip(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * 进入下一个注册流程
     */
    public void goNextActivity() {
        Intent _intent = new Intent(UserForgotPhoneActivity.this, UserForgotCodeActivity.class);
        _intent.putExtra("_phone", mPhone);
        startActivity(_intent);
        UserForgotPhoneActivity.this.finish();
    }

    private class UserRegPhoneOnClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_user_reg_getcode_phone_commit2:
                    //提交
                    UserCommitPhoneNumer();
                    break;
                case R.id.siv_pic_number2:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getPicNumber(mToken);
                        }
                    }).start();
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
        mPicNumber = mEtPicNumber.getText().toString();
        boolean ischecked = mCbIsRead.isChecked();
        if (!ischecked) {
            Toast.makeText(UserForgotPhoneActivity.this, "您未同意注册协议", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mPicNumber) && isCheckPicNumber) {
            Toast.makeText(UserForgotPhoneActivity.this, "填写验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mPhone != null && !mPhone.equals("")) {
            if (!CheckUtil.checkMobile(mPhone)) {
                Toast.makeText(UserForgotPhoneActivity.this, "手机号格式输入有误", Toast.LENGTH_LONG).show();
                return;
            }

            //弹出加载进度条
            mProgressDialog = ProgressDialog.show(UserForgotPhoneActivity.this, null, "正在玩命获取中...", true, true);
            //开启副线程-检查手机号码是否存在
            checkPhoneFromNet(mPhone, mPicNumber);
            //若检查不通过、提示报错信息；检查通过，
            // 进度条消失

        } else {
            Toast.makeText(UserForgotPhoneActivity.this, "您未填写手机号", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * 发送消息到手机
     */
    private String sendSMSToPhone(String PicNumber) {
        try {
            String _mToken = mSP.getString("mToken", "");
            Log.e("_register", _mToken);
            String _result = mUserBusiness.getMobilemsgRegister(mPhone, _mToken, PicNumber);
            Log.e("_result-------------", _result);
            return _result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 开启副线程-检查手机号码,并发送信息
     *
     * @param phone
     */
    private void checkPhoneFromNet(final String phone, final String picNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = mUserBusiness.getMobileNumberIsVaild(phone);
                    Log.e("result", result);
                    JSONObject jsonObj = new JSONObject(result);
                    Boolean Success = JsonUtils.getBoolean(jsonObj, "Success");
                    if (!Success) {//检查手机是否存在
                        //当手机号码存在之后,发送数据到服务器
                        if (!isCheckPicNumber) {
                            mMobilemsgRegisterResult = sendSMSToPhone(null);
                            Log.e("这是没有图片验证码的", "mErrorCode");
                        } else {
                            mMobilemsgRegisterResult = sendSMSToPhone(picNumber);
                            Log.e("这是有图片验证码的", mMobilemsgRegisterResult);
                        }
                        JSONObject _object = new JSONObject(mMobilemsgRegisterResult);
                        boolean _success = JsonUtils.getBoolean(_object, "Success");
                        if (_success) {
                            handler.sendEmptyMessage(CommonConstants.FLAG_GET_REG_MOBILEMGS_REGISTER_SUCCESS);//获取成功
                        } else {
                            //发送错误消息
                            String errorMsg = JsonUtils.getString(_object, "Message");
                            Log.e("errorMsg", errorMsg);
                            CommonUtil.sendErrorMessage(errorMsg, handler);
                            //拿到切换后的Pic路劲
                            if (JsonUtils.getString(_object, "Datas") != null) {
                                String _datas = JsonUtils.getString(_object, "Datas");
                                JSONObject _PicPath = new JSONObject(_datas);
                                mPicNextPath = JsonUtils.getString(_PicPath, "PicCodePath");
                            }
                            handler.sendEmptyMessage(CHANGE_PICNUMBER);
                        }
                    } else {
                        //手机号码不存在
                        CommonUtil.sendErrorMessage("手机号码不存在,请先注册", handler);
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
//                    CommonUtil.sendErrorMessage("服务器错误", handler);
                }
            }
        }).start();
    }
}
