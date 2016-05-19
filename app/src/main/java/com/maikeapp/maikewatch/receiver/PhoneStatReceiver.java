package com.maikeapp.maikewatch.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneStatReceiver extends BroadcastReceiver {

    private static final String TAG = "PhoneStatReceiver";
    private static boolean incomingFlag = false;
    private static String incoming_number = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            //如果是来电
            TelephonyManager tm =
                    (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING://来电
                    incomingFlag = true;//标识当前是来电
                    incoming_number = intent.getStringExtra("incoming_number");
                    Log.e(TAG, "电话来了:" + incoming_number);
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
}