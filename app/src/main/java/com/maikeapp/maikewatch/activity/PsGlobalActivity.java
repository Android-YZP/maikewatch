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
import com.maikeapp.maikewatch.exception.ServiceException;
import com.maikeapp.maikewatch.util.CommonUtil;
import com.maikeapp.maikewatch.util.JsonUtils;

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
                Log.d(CommonConstants.LOGCAT_TAG_NAME+"_progress",""+progress);
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
                    Toast.makeText(PsGlobalActivity.this,"正在设置中...,请耐心等待",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mUser!=null){
                    String _sportsTarget = mTvSportsTarget.getText().toString();
                    //弹出加载进度条
                    mProgressDialog = ProgressDialog.show(PsGlobalActivity.this, "请稍等", "正在玩命设置中...",true,true);
                    //设置个人目标信息
                    setWatchPsTarget(_sportsTarget);
                }else{
                    Toast.makeText(PsGlobalActivity.this,"您未登录",Toast.LENGTH_SHORT).show();
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
            device = new Maike(this);

        }catch (Exception e){
            e.printStackTrace();
        }
        //开启副线程-设置个人目标步数到手表，并在设置完成后更新服务端
        new Thread(new Runnable() {
            @Override
            public void run() {
                String _macAddress = mUser.getMacAddress();
                //连接某只手表mac
                JSONObject objectMac = device.scanDevice(Global.TYPE_DEVICE_Wristband, _macAddress);
                Log.i(CommonConstants.LOGCAT_TAG_NAME+"_reconnect", "objectMac，Result = " + objectMac);
                if (objectMac==null){
                    //同步失败，未连接手表
                    CommonUtil.sendErrorMessage("同步失败，请重试",handler);
                }else{
                    setWatchTargetData(sportsTarget);//同步数据
                }
            }
        }).start();
    }

    /**
     * 同步数据-设置手表个人目标
     * @param sportsTarget
     */
    private void setWatchTargetData(final String sportsTarget) {
        //开启副线程-同步数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    running = true;

                    JSONObject datetimeResult = device.setDateTime(Calendar.getInstance());	// 设置手环的日期和时间
                    Log.i("sync", "datetimeResult = " + datetimeResult);		// 如果为result = 0，则成功，否则失败

                    int _target = Integer.parseInt(sportsTarget);
                    JSONObject targetResult = device.setTarget(_target);		// 设置手环的步数目标
                    Log.i("sync", "targetResult = " + targetResult);			// 如果为result = 0，则成功，否则失败

                    int _target_result = JsonUtils.getInt(targetResult,"result",-1);
                    if (_target_result==0){
                        // 表示同步成功
                        mUser.setSportsTarget(_target);
                        CommonUtil.saveUserInfo(mUser,PsGlobalActivity.this);//覆盖用户个人目标信息
                        //上传信息到服务端
                        String _set_result = mUserBusiness.setSportsTarget(mUser);
                        Log.d(CommonConstants.LOGCAT_TAG_NAME+"_set_result",_set_result);
                        JSONObject _json_result = new JSONObject(_set_result);
                        boolean _success = JsonUtils.getBoolean(_json_result,"Success");
                        if (_success){
                            // 同步完成
                            handler.sendEmptyMessage(CommonConstants.FLAG_SET_TARGET_SUCCESS);
                        }else{
                            CommonUtil.sendErrorMessage("设置个人目标失败，请检查网络",handler);
                        }


                    }else{
                        CommonUtil.sendErrorMessage("设置个人目标失败，请重试",handler);
                    }

                    // 设置手环同步完成
                    device.setFinishSync();


                } catch (NoConnectException e) {
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage("同步失败，请重试",handler);
                }catch (ServiceException e){
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage(e.getMessage(),handler);
                } catch (Exception e){
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage("同步失败，数据异常，请重试",handler);
                }
                running = false;
                //断开连接
                disConnectWatch();
            }
        }).start();

    }


    /**
     * 断开连接设备
     */
    private void disConnectWatch() {
        try {
            boolean isDestroy = true;
            JSONObject object = device.disconnectDevice(isDestroy);		// 断开设备
            Log.d("disconnect", "disconncet = " + object);		// 如果为result = 0，则成功，否则失败
            device = null;
        } catch (NoConnectException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
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
        PsGlobalActivity.this.finish();
    }


}
