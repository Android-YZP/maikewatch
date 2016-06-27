package com.maikeapp.maikewatch.activity;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gzgamut.sdk.global.Global;
import com.gzgamut.sdk.helper.NoConnectException;
import com.gzgamut.sdk.model.Maike;
import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.bean.OneDayData;
import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.business.IUserBusiness;
import com.maikeapp.maikewatch.business.imp.UserBusinessImp;
import com.maikeapp.maikewatch.config.CommonConstants;
import com.maikeapp.maikewatch.config.MyApplication;
import com.maikeapp.maikewatch.exception.ServiceException;
import com.maikeapp.maikewatch.util.CommonUtil;
import com.maikeapp.maikewatch.util.JsonUtils;
import com.maikeapp.maikewatch.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class PsGlobalActivity extends AppCompatActivity {
    private ImageView mIvCommonBack;//返回
    private TextView mTvCommonTitle;//标题
    private TextView mTvCommonAction;//编辑

    private TextView mTvSportsTarget;//个人目标步数
    private SeekBar mSbSportsTarget;//个人目标拖动条

    private String m_title="个人目标";
    private String m_action="完成";

    //sdk
    private Maike device = null;

    /**
     * 业务层
     */
    private IUserBusiness mUserBusiness = new UserBusinessImp();
    private static ProgressDialog mProgressDialog = null;
    private User mUser;//用户信息

    /**
     * 是否正在同步
     */
    private boolean running;

    private int mBattery;//电量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ps_global);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        //通用控件
        mIvCommonBack = (ImageView)findViewById(R.id.iv_common_back);
        mTvCommonTitle = (TextView)findViewById(R.id.tv_common_title);
        mTvCommonAction = (TextView)findViewById(R.id.tv_common_action);
        //目标步数和设置目标步数
        mTvSportsTarget = (TextView)findViewById(R.id.tv_ps_global_sports_target);
        mSbSportsTarget = (SeekBar)findViewById(R.id.sb_ps_global_set_sports_target);

    }

    private void initData() {
        //通用控件
        mTvCommonTitle.setText(m_title);
        mTvCommonAction.setText(m_action);

        mUser = CommonUtil.getUserInfo(this);
        if (mUser!=null){
            int _step = (mUser.getSportsTarget()==0)?2000:(mUser.getSportsTarget());
            mTvSportsTarget.setText("" + _step);
            int _progress = (_step-2000)/1000;
            mSbSportsTarget.setProgress(_progress);
        }

    }

    private void setListener() {
        //通用控件
        mIvCommonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PsGlobalActivity.this.finish();
            }
        });

        //拖动目标步数的时候
        mSbSportsTarget.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(CommonConstants.LOGCAT_TAG_NAME+"_progress",""+progress);//0~28
                mTvSportsTarget.setText((2000 + progress*1000)+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //点击完成的时候
        mTvCommonAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running){
                    ToastUtil.showTipShort(PsGlobalActivity.this,"正在设置中...");
                    return;
                }

                if (mUser!=null){
                    String _sportsTarget = mTvSportsTarget.getText().toString();
                    //弹出加载进度条
                    mProgressDialog = ProgressDialog.show(PsGlobalActivity.this, null, "正在玩命设置中...",true,true);
                    //设置个人目标信息
                    setWatchPsTarget(_sportsTarget);
                }else{
                    ToastUtil.showTipShort(PsGlobalActivity.this,"您未登录");
                    return;
                }

            }
        });
    }

    /**
     * 设置个人目标信息
     * @param sportsTarget
     */
    private void setWatchPsTarget(final String sportsTarget) {
        //初始化device
        try{
            device = MyApplication.newMaikeInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        //开启副线程-设置个人目标步数到手表，并在设置完成后更新服务端
        new Thread(new Runnable() {
            @Override
            public void run() {
                //设置个人目标到手表并上传服务器
                setWatchTargetData(sportsTarget);
            }
        }).start();
    }

    /**
     * 同步数据-设置手表个人目标
     * @param sportsTarget
     */
    private void setWatchTargetData(final String sportsTarget) {
        //开启副线程-同步数据-设置个人目标
        new Thread(new Runnable() {
            @Override
            public void run() {

                running = true;//正在运行
                int _seconds = 500;
                //循环5次连接，若连接不成功给予用户提醒
                for (int k = 0; k < 5; k++) {
                    try {
                        //暂时先沉睡2s
                        try {
                            Thread.sleep(_seconds);
                            _seconds+=500;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.i(CommonConstants.LOGCAT_TAG_NAME + "_set_connect_no", "--------no = " + k);
                        //连接手表mac
                        JSONObject objectMac = device.scanDevice(Global.TYPE_DEVICE_Wristband,  mUser.getMacAddress());
                        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_set_conn_isSuc", "objectMac，Result = " + objectMac);
                        if (objectMac == null) {
                            //断开设备
                            JSONObject object = device.disconnectDevice(false);        // 断开设备
                            Log.i(CommonConstants.LOGCAT_TAG_NAME + "_set_disconnect", "disconncet = " + object);        // 如果为result = 0，则成功，否则失败
                            continue;
                        }

                        //再沉睡0.5s
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }


//》》设置个人目标的步数start

                        int _target = Integer.parseInt(sportsTarget);
                        JSONObject targetResult = device.setTarget(_target);        // 设置手环的步数目标
                        Log.i("sync", "targetResult = " + targetResult);            // 如果为result = 0，则成功，否则失败

                        JSONObject versionResult = device.getVersion();		// 获取手环的固件版本号
                        Log.i("sync", "versionResult = " + versionResult);			// result返回是一串字符，即版本号
                        String _version_str = JsonUtils.getString(versionResult,"result");
                        if (_version_str!=null&&!_version_str.equals("")){
                            mUser.setWatchVersion(_version_str);
                        }

                        JSONObject batteryResult = device.getBattery();        // 获取手环的电量
                        Log.d("sync", "batteryResult= " + batteryResult);            // result 里面的数值就是电量
                        mBattery = JsonUtils.getInt(batteryResult, "result", -1);
                        if (mBattery != -1) {
                            mUser.setBattery(mBattery);
                        }

                        int _target_result = JsonUtils.getInt(targetResult, "result", -1);
                        if (_target_result == 0) {
                            // 表示同步成功
                            mUser.setSportsTarget(_target);
                            CommonUtil.saveUserInfo(mUser, PsGlobalActivity.this);//覆盖用户个人目标信息/覆盖用户电量/用户固件版本号
                            // 同步完成
                            handler.sendEmptyMessage(CommonConstants.FLAG_SET_TARGET_SUCCESS);
                            //连接成功后断开设备
                            JSONObject object = device.disconnectDevice(false);        // 断开设备
                            Log.d(CommonConstants.LOGCAT_TAG_NAME + "_set_disconnect", "disconncet = " + object);        // 如果为result = 0，则成功，否则失败

                            running = false;//提前结束运行，不提示错误信息
                            break;//结束循环



                        } else {
                            CommonUtil.sendErrorMessage("同步到手表失败，请重试", handler);
                        }
//》》设置个人目标的步数end

                        //每次循环连接都断开设备
                        JSONObject object = device.disconnectDevice(false);        // 断开设备
                        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_set_disconnect", "disconncet = " + object);        // 如果为result = 0，则成功，否则失败

                    } catch (NoConnectException e) {
                        e.printStackTrace();
                        //发现连接异常，结束本次循环，进入下一次连接
                        continue;
                    } catch (Exception e) {
                        e.printStackTrace();
                        CommonUtil.sendErrorMessage("同步失败，数据异常，请重试", handler);
                        return;
                    }
                }
                //循环5次依然没连上，提示错误信息，并running未false
                if (running) {
                    //设置个人目标失败
                    String errorMsg = "设置个人目标失败，请重试";
                    CommonUtil.sendErrorMessage(errorMsg, handler);
                    running = false;//结束运行
                }

            }
        }).start();

    }


    //处理消息队列
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(mProgressDialog!=null){
                mProgressDialog.dismiss();
            }
            int flag = msg.what;
            switch (flag) {
                case 0:
                    String errorMsg = (String)msg.getData().getSerializable("ErrorMsg");
                    try {
                        Toast.makeText(PsGlobalActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CommonConstants.FLAG_SET_TARGET_SUCCESS:
                    setTargetCompleted();//操作完成
                    break;
                default:
                    break;
            }
        }


    };

    /**
     * 操作完成
     */
    private void setTargetCompleted() {
        Toast.makeText(PsGlobalActivity.this,"设置成功",Toast.LENGTH_SHORT).show();

        /**
         * 开启副线称-上传运动目标到服务端
         */
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //上传信息到服务端
                    String _set_result = mUserBusiness.setSportsTarget(mUser);
                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_set_result", "set_result="+_set_result);
                    JSONObject _json_result = new JSONObject(_set_result);
                    boolean _success = JsonUtils.getBoolean(_json_result, "Success");
                    if (_success) {
                        Log.d(CommonConstants.LOGCAT_TAG_NAME, "upSportsTargetToServer Success");



                    } else {
                        //提示服务端给出的错误信息
                        String _errorMsg = JsonUtils.getString(_json_result,"Message");
                        if (_errorMsg==null||_errorMsg.equals("")){
                            _errorMsg = "设置个人目标失败，请检查网络";
                        }
                        CommonUtil.sendErrorMessage(_errorMsg, handler);
                    }
                } catch (ServiceException e) {
                    e.printStackTrace();
                    //CommonUtil.sendErrorMessage(e.getMessage(), handler);
//                return;
                } catch (Exception e) {
                    e.printStackTrace();
                    //CommonUtil.sendErrorMessage("同步失败，数据异常，请重试", handler);
//                return;
                }
            }
        }).start();

        PsGlobalActivity.this.finish();
    }


}
