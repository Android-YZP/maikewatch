package com.maikeapp.maikewatch.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.gzgamut.sdk.global.Global;
import com.gzgamut.sdk.helper.NoConnectException;
import com.gzgamut.sdk.model.Maike;
import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.config.CommonConstants;
import com.maikeapp.maikewatch.config.MyApplication;
import com.maikeapp.maikewatch.util.CommonUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class PhoneStatReceiver extends BroadcastReceiver {

    private static final String TAG = "PhoneStatReceiver";
    private static boolean incomingFlag = false;
    private static String incoming_number = null;

    //sdk
    private Maike device = null;
    /**
     * 是否正在运行打电话
     */
    private boolean running;

    private User mUser;
    private String macAddress;

    @Override
    public void onReceive(Context context, Intent intent) {
        //初始化用户信息
        mUser = CommonUtil.getUserInfo(context);

        if (!intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            //如果是来电
            TelephonyManager tm =
                    (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING://来电
                    incomingFlag = true;//标识当前是来电
                    incoming_number = intent.getStringExtra("incoming_number");
                    Log.e(TAG, "电话来了:" + incoming_number);

                    //通知手表电话来了
                    if (!running){
                        notifyWatchCalling();

                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://正在通话中
                    if (incomingFlag) {
                        Log.e(TAG, "正在通话中:" + incoming_number);
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE://空闲
                    if (incomingFlag) {
                        Log.e(TAG, "电话空闲状态");
                    }
                    break;
            }
        }
    }

    /**
     * 通知手表电话来了
     */
    private void notifyWatchCalling(){
        //初始化device
        device = MyApplication.newMaikeInstance();
        if (mUser==null||mUser.getMacAddress()==null||mUser.getMacAddress().equals("")){
            return;
        }
        macAddress = mUser.getMacAddress();

        //开启副线程-进行解绑操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                running = true;//正在运行

                //循环5次连接，若连接不成功给予用户提醒
                for (int k = 0; k < 5; k++) {
                    try {
                        //暂时先沉睡2s
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.i(CommonConstants.LOGCAT_TAG_NAME + "_call_connect_no", "--------no = " + k);
                        //连接手表mac
                        JSONObject objectMac = device.scanDevice(Global.TYPE_DEVICE_Wristband, macAddress);
                        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_call_conn_isSuc", "objectMac，Result = " + objectMac);
                        if (objectMac == null) {
                            //断开设备
                            JSONObject object = device.disconnectDevice(false);        // 断开设备
                            Log.i(CommonConstants.LOGCAT_TAG_NAME + "_call_disconnect", "disconncet = " + object);        // 如果为result = 0，则成功，否则失败
                            continue;
                        }

                        //再沉睡0.5s
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

// 提醒开始 》》》
                        if (macAddress != null) {
                            //来电
                            JSONObject callResult = device.writeCallReminder();		// 向手环写来电提醒指令，有来电时才发这个指令
                            Log.i("call", "callResult = " + callResult);		// 如果为result = 0，则成功，否则失败
                            if (callResult != null) {
                                String result = callResult.getString("result");
                                if (result.equals("0")) {

                                    //连接成功后断开设备
                                    JSONObject object = device.disconnectDevice(false);        // 断开设备
                                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_call_disconnect", "disconncet = " + object);        // 如果为result = 0，则成功，否则失败

                                    running = false;//提前结束运行，不提示错误信息
                                    break;//结束循环
                                }
                            }
                        }
// 提醒结束 《《《

                        //每次循环连接都断开设备
                        JSONObject object = device.disconnectDevice(false);        // 断开设备
                        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_call_disconnect", "disconncet = " + object);        // 如果为result = 0，则成功，否则失败

                    } catch (NoConnectException e) {
                        e.printStackTrace();
                        //发现连接异常，结束本次循环，进入下一次连接
                        continue;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //循环5次依然没连上，提示错误信息，并running未false
                if (running) {
                    //提醒失败
                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_set_alarm", "-------------call_fail--------------");
                    running = false;//结束运行
                }
            }
        }).start();
    }

}