package com.maikeapp.maikewatch.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gzgamut.sdk.global.Global;
import com.gzgamut.sdk.helper.NoConnectException;
import com.gzgamut.sdk.model.Maike;
import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.config.CommonConstants;
import com.maikeapp.maikewatch.config.MyApplication;
import com.maikeapp.maikewatch.util.CommonUtil;
import com.maikeapp.maikewatch.util.ConvertUtil;
import com.maikeapp.maikewatch.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class AlertAlarmActivity extends AppCompatActivity {

    public static final int TIME_CLICK_ONE = 1;
    public static final int TIME_CLICK_TWO = 2;
    public static final int TIME_CLICK_THREE = 3;
    private ImageView mIvCommonBack;//返回
    private TextView mTvCommonTitle;//标题
    private TextView mTvCommonAction;//编辑

    private String m_title="闹钟设定";
    private String m_action = "完成";

    private TextView mTvAlarmOne;//第1个闹钟时间
    private TextView mTvAlarmTwo;//第2个闹钟时间
    private TextView mTvAlarmThree;//第3个闹钟时间

    private TextView mTvDaysOne;//第1个闹钟的重复日期
    private TextView mTvDaysTwo;//第2个闹钟的重复日期
    private TextView mTvDaysThree;//第3个闹钟的重复日期

    private TextView mTvDaysShowOne;//第1个闹钟的重复日期-显示
    private TextView mTvDaysShowTwo;//第2个闹钟的重复日期-显示
    private TextView mTvDaysShowThree;//第3个闹钟的重复日期-显示

    private CheckBox mCbIsOnOne;//第1个闹钟的是否开启
    private CheckBox mCbIsOnTwo;//第2个闹钟的是否开启
    private CheckBox mCbIsOnThree;//第3个闹钟的是否开启

    //加载进度条
    private static ProgressDialog mProgressDialog = null;
    //sdk
    private Maike device = null;
    /**
     * 是否正在运行打电话
     */
    private boolean running;

    private User mUser;
    private String macAddress;

    private String m_click_one;
    private String m_click_two;
    private String m_click_three;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_alarm);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        //通用控件
        mIvCommonBack = (ImageView)findViewById(R.id.iv_common_back);
        mTvCommonTitle = (TextView)findViewById(R.id.tv_common_title);
        mTvCommonAction = (TextView) findViewById(R.id.tv_common_action);
        //每个闹钟的时间
        mTvAlarmOne = (TextView)findViewById(R.id.tv_alert_alarm_time1);//第1个闹钟时间
        mTvAlarmTwo = (TextView)findViewById(R.id.tv_alert_alarm_time2);//第2个闹钟时间
        mTvAlarmThree = (TextView)findViewById(R.id.tv_alert_alarm_time3);//第3个闹钟时间
        //重复星期
        mTvDaysOne = (TextView)findViewById(R.id.tv_alert_alarm_date1);//第1个闹钟时间
        mTvDaysTwo = (TextView)findViewById(R.id.tv_alert_alarm_date2);//第2个闹钟时间
        mTvDaysThree = (TextView)findViewById(R.id.tv_alert_alarm_date3);//第3个闹钟时间
        //重复星期-显示
        mTvDaysShowOne = (TextView)findViewById(R.id.tv_alert_alarm_date1_show);//第1个闹钟时间
        mTvDaysShowTwo = (TextView)findViewById(R.id.tv_alert_alarm_date2_show);//第2个闹钟时间
        mTvDaysShowThree = (TextView)findViewById(R.id.tv_alert_alarm_date3_show);//第3个闹钟时间
        //是否开启
        mCbIsOnOne = (CheckBox)findViewById(R.id.cb_alert_alarm_ison1);
        mCbIsOnTwo = (CheckBox)findViewById(R.id.cb_alert_alarm_ison2);
        mCbIsOnThree = (CheckBox)findViewById(R.id.cb_alert_alarm_ison3);
    }

    private void initData() {
        //通用控件
        mTvCommonTitle.setText(m_title);
        mTvCommonAction.setText(m_action);
        mUser = CommonUtil.getUserInfo(this);
        if (mUser!=null){
            //初始化所有闹钟信息
            initAllAlarmData();
        }
    }



    private void setListener() {
        //通用控件
        mIvCommonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertAlarmActivity.this.finish();
            }
        });
        mTvAlarmOne.setOnClickListener(new OneTimeOnClickListener());
        mTvAlarmTwo.setOnClickListener(new OneTimeOnClickListener());
        mTvAlarmThree.setOnClickListener(new OneTimeOnClickListener());
        mTvCommonAction.setOnClickListener(new OneTimeOnClickListener());
    }

    private class OneTimeOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent _intent = null;
            switch (v.getId()){
                case R.id.tv_alert_alarm_time1:
                    fullClockTimeData();

                    _intent = new Intent(AlertAlarmActivity.this,OneAlarmActivity.class);
                    _intent.putExtra("_which_clock",1);
                    _intent.putExtra("m_clock_time",m_click_one);
                    AlertAlarmActivity.this.startActivity(_intent);
                    break;
                case R.id.tv_alert_alarm_time2:
                    fullClockTimeData();

                    _intent = new Intent(AlertAlarmActivity.this,OneAlarmActivity.class);
                    _intent.putExtra("_which_clock",2);
                    _intent.putExtra("m_clock_time",m_click_two);
                    AlertAlarmActivity.this.startActivity(_intent);
                    break;
                case R.id.tv_alert_alarm_time3:
                    fullClockTimeData();

                    _intent = new Intent(AlertAlarmActivity.this,OneAlarmActivity.class);
                    _intent.putExtra("_which_clock",3);
                    _intent.putExtra("m_clock_time",m_click_three);
                    AlertAlarmActivity.this.startActivity(_intent);
                    break;
                case R.id.tv_common_action:
                    //点击了完成
                    if (running){
                        ToastUtil.showTipShort(AlertAlarmActivity.this,"正在设置中...");
                        return;
                    }
                    //弹出加载进度条
                    mProgressDialog = ProgressDialog.show(AlertAlarmActivity.this, "请稍等", "正在玩命设置中...",true,true);
                    //设置闹钟
                    clickAction();

                    break;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle _bundle = intent.getExtras();
        if (_bundle!=null){
            String _repeat_day = _bundle.getString("_repeat_day");
            String _hourAndTime = _bundle.getString("_hourAndTime");
            int _which_time = _bundle.getInt("_which_time");
            Log.d(CommonConstants.LOGCAT_TAG_NAME,_which_time+","+_repeat_day+","+_hourAndTime);
            //设置完单个日期和时间后，显示在界面上
            updateUIForATime(_which_time,_repeat_day,_hourAndTime);


        }
    }

    /**
     * 更新UI-每个日期和时间
     * @param which_time
     * @param repeat_day
     * @param hourAndTime
     */
    private void updateUIForATime(int which_time, String repeat_day, String hourAndTime) {
        switch (which_time) {
            case TIME_CLICK_ONE:
                mTvAlarmOne.setText(hourAndTime);
                mTvDaysOne.setText(repeat_day);

                convertDataToDay(mTvDaysShowOne,repeat_day);

                break;
            case TIME_CLICK_TWO:
                mTvAlarmTwo.setText(hourAndTime);
                mTvDaysTwo.setText(repeat_day);

                convertDataToDay(mTvDaysShowTwo,repeat_day);

                break;
            case TIME_CLICK_THREE:
                mTvAlarmThree.setText(hourAndTime);
                mTvDaysThree.setText(repeat_day);

                convertDataToDay(mTvDaysShowThree,repeat_day);

                break;
        }
    }

    /**
     * 转换二进制数据到星期数
     * @param tvDaysShow 控件
     * @param repeat_day 重复星期二进制数据
     */
    private void convertDataToDay(TextView tvDaysShow,String repeat_day) {
        Log.d(CommonConstants.LOGCAT_TAG_NAME,"repeat_day="+repeat_day);
        StringBuffer _sb = new StringBuffer();
        char[] repeat_days =  repeat_day.toCharArray();
        if (1==Integer.parseInt(String.valueOf(repeat_days[1]))){
            _sb.append("六、");
        }
        if (1==Integer.parseInt(String.valueOf(repeat_days[2]))){
            _sb.append("五、");
        }
        if (1==Integer.parseInt(String.valueOf(repeat_days[3]))){
            _sb.append("四、");
        }
        if (1==Integer.parseInt(String.valueOf(repeat_days[4]))){
            _sb.append("三、");
        }
        if (1==Integer.parseInt(String.valueOf(repeat_days[5]))){
            _sb.append("二、");
        }
        if (1==Integer.parseInt(String.valueOf(repeat_days[6]))){
            _sb.append("一、");
        }
        if (1==Integer.parseInt(String.valueOf(repeat_days[7]))){
            _sb.append("日");
        }
        Log.d(CommonConstants.LOGCAT_TAG_NAME,"tvDaysShow="+_sb.toString());
        String _repeat_days_show = _sb.toString();
        if (_repeat_days_show==null||_repeat_days_show.equals("")){
            tvDaysShow.setText("无");
        }else{
            tvDaysShow.setText(_repeat_days_show);
        }

    }



    /**
     * 初始化所有闹钟信息
     */
    private void initAllAlarmData() {
        //获取保存的时间、重复星期数、是否开启
        String _click_saved_1 = mUser.getClickOne();
        String _click_saved_2 = mUser.getClickTwo();
        String _click_saved_3 = mUser.getClickThree();
        //闹钟1-界面显示
        if (_click_saved_1!=null&&!_click_saved_1.equals("")){
            String[] _click_1_strs = _click_saved_1.split(",");//小时，分钟，重复星期数，是否开启
            String _click_1_hour = ConvertUtil.convertTimeToShow(_click_1_strs[0]);
            String _click_1_minute = ConvertUtil.convertTimeToShow(_click_1_strs[1]);
            String _click_1_days = _click_1_strs[2];
            String _click_1_ison = _click_1_strs[3];
            mTvAlarmOne.setText(_click_1_hour+":"+_click_1_minute);
            mTvDaysOne.setText(_click_1_days);
            convertDataToDay(mTvDaysShowOne,_click_1_days);

            int ischecked = Integer.parseInt(_click_1_ison);
            if (ischecked==1){
                mCbIsOnOne.setChecked(true);
            }else{
                mCbIsOnOne.setChecked(false);
            }

        }else{
            mTvAlarmOne.setText("08:00");
            mTvDaysOne.setText("00000000");
            convertDataToDay(mTvDaysShowOne,"00000000");
            mCbIsOnOne.setChecked(false);
        }

        //闹钟2-界面显示
        if (_click_saved_2!=null&&!_click_saved_2.equals("")){
            String[] _click_2_strs = _click_saved_2.split(",");//小时，分钟，重复星期数，是否开启
            String _click_2_hour = ConvertUtil.convertTimeToShow(_click_2_strs[0]);
            String _click_2_minute = ConvertUtil.convertTimeToShow(_click_2_strs[1]);
            String _click_2_days = _click_2_strs[2];
            String _click_2_ison = _click_2_strs[3];
            mTvAlarmTwo.setText(_click_2_hour+":"+_click_2_minute);
            mTvDaysTwo.setText(_click_2_days);
            convertDataToDay(mTvDaysShowTwo,_click_2_days);

            int ischecked = Integer.parseInt(_click_2_ison);
            if (ischecked==1){
                mCbIsOnTwo.setChecked(true);
            }else{
                mCbIsOnTwo.setChecked(false);
            }

        }else{
            mTvAlarmTwo.setText("08:00");
            mTvDaysTwo.setText("00000000");
            convertDataToDay(mTvDaysShowTwo,"00000000");
            mCbIsOnTwo.setChecked(false);
        }

        //闹钟3-界面显示
        if (_click_saved_3!=null&&!_click_saved_3.equals("")){
            String[] _click_3_strs = _click_saved_3.split(",");//小时，分钟，重复星期数，是否开启
            String _click_3_hour = ConvertUtil.convertTimeToShow(_click_3_strs[0]);
            String _click_3_minute = ConvertUtil.convertTimeToShow(_click_3_strs[1]);
            String _click_3_days = _click_3_strs[2];
            String _click_3_ison = _click_3_strs[3];
            mTvAlarmThree.setText(_click_3_hour+":"+_click_3_minute);
            mTvDaysThree.setText(_click_3_days);
            convertDataToDay(mTvDaysShowThree,_click_3_days);

            int ischecked = Integer.parseInt(_click_3_ison);
            if (ischecked==1){
                mCbIsOnThree.setChecked(true);
            }else{
                mCbIsOnThree.setChecked(false);
            }

        }else{
            mTvAlarmThree.setText("08:00");
            mTvDaysThree.setText("00000000");
            convertDataToDay(mTvDaysShowThree,"00000000");
            mCbIsOnThree.setChecked(false);
        }
    }


    /**
     * 点击了完成
     */
    private void clickAction() {
        //获取目前的该界面的闹钟日期和时间值
        boolean _ison_time1 = mCbIsOnOne.isChecked();
        boolean _ison_time2 = mCbIsOnTwo.isChecked();
        boolean _ison_time3 = mCbIsOnThree.isChecked();

        //第1个闹钟填充时、分、是否重复
        String _time1 = mTvAlarmOne.getText().toString();
        int _alarm_hour_1 = Integer.parseInt(_time1.substring(0,_time1.indexOf(":")));
        int _alarm_minute_1 = Integer.parseInt(_time1.substring(_time1.indexOf(":")+1));
        String _alarm_repeat_str_1="00000000";
        int _alarm_repeat_1 = 0;
        int _ison_time1_int1 = 0;
        if (_ison_time1){
            String _hex_str = mTvDaysOne.getText().toString();
            _alarm_repeat_str_1=_hex_str;
            String _hex_converted = Integer.toString (Integer.parseInt (_hex_str, 2), 16);
            _alarm_repeat_1 = Integer.parseInt(_hex_converted,16);
            _ison_time1_int1 = 1;
            Log.d(CommonConstants.LOGCAT_TAG_NAME,"--------------------1="+_alarm_hour_1+","+_alarm_minute_1+","+_alarm_repeat_1);
        }
        //第2个闹钟填充时、分、是否重复
        String _time2 = mTvAlarmTwo.getText().toString();
        int _alarm_hour_2 = Integer.parseInt(_time2.substring(0,_time2.indexOf(":")));
        int _alarm_minute_2 = Integer.parseInt(_time2.substring(_time2.indexOf(":")+1));
        String _alarm_repeat_str_2="00000000";
        int _alarm_repeat_2 = 0;
        int _ison_time1_int2 = 0;
        if (_ison_time2){
            String _hex_str = mTvDaysTwo.getText().toString();
            _alarm_repeat_str_2=_hex_str;
            String _hex_converted = Integer.toString (Integer.parseInt (_hex_str, 2), 16);
            _alarm_repeat_2 = Integer.parseInt(_hex_converted,16);
            _ison_time1_int2 = 1;
            Log.d(CommonConstants.LOGCAT_TAG_NAME,"--------------------2="+_alarm_hour_2+","+_alarm_minute_2+","+_alarm_repeat_2);
        }

        //第3个闹钟填充时、分、是否重复
        String _time3 = mTvAlarmThree.getText().toString();
        int _alarm_hour_3 = Integer.parseInt(_time3.substring(0,_time3.indexOf(":")));
        int _alarm_minute_3 = Integer.parseInt(_time3.substring(_time3.indexOf(":")+1));
        String _alarm_repeat_str_3="00000000";
        int _alarm_repeat_3 = 0;
        int _ison_time1_int3 = 0;
        if (_ison_time3){
            String _hex_str = mTvDaysTwo.getText().toString();
            _alarm_repeat_str_3=_hex_str;
            String _hex_converted = Integer.toString (Integer.parseInt (_hex_str, 2), 16);
            _alarm_repeat_3 = Integer.parseInt(_hex_converted,16);
            _ison_time1_int3 = 1;
            Log.d(CommonConstants.LOGCAT_TAG_NAME,"--------------------3="+_alarm_hour_3+","+_alarm_minute_3+","+_alarm_repeat_3);
        }
        //时，分，重复星期数，是否开启
        m_click_one = _alarm_hour_1+","+_alarm_minute_1+","+_alarm_repeat_str_1+","+_ison_time1_int1;
        m_click_two = _alarm_hour_2+","+_alarm_minute_2+","+_alarm_repeat_str_2+","+_ison_time1_int2;
        m_click_three = _alarm_hour_3+","+_alarm_minute_3+","+_alarm_repeat_str_3+","+_ison_time1_int3;
        //设置闹钟到手表端
        setAlarmToWatch(_alarm_hour_1,_alarm_minute_1,_alarm_repeat_1,_alarm_hour_2,_alarm_minute_2,_alarm_repeat_2,_alarm_hour_3,_alarm_minute_3,_alarm_repeat_3);
    }

    /**
     * 设置闹钟到手表端
     * @param _alarm_hour_1 时1
     * @param _alarm_minute_1 分1
     * @param _alarm_repeat_1 重复星期1
     * @param _alarm_hour_2 时2
     * @param _alarm_minute_2 分2
     * @param _alarm_repeat_2 重复星期2
     * @param _alarm_hour_3 时3
     * @param _alarm_minute_3 分3
     * @param _alarm_repeat_3 重复星期3
     */
    private void setAlarmToWatch(final int _alarm_hour_1,final int _alarm_minute_1,final int _alarm_repeat_1,final int _alarm_hour_2,final int _alarm_minute_2,final int _alarm_repeat_2,final int _alarm_hour_3,final int _alarm_minute_3,final int _alarm_repeat_3) {
        Log.d(CommonConstants.LOGCAT_TAG_NAME,_alarm_hour_1+":"+_alarm_minute_1+","+_alarm_repeat_1+";"+_alarm_hour_2+":"+_alarm_minute_2+","+_alarm_repeat_2+";"+_alarm_hour_3+":"+_alarm_minute_3+","+_alarm_repeat_3);

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
                        Log.i(CommonConstants.LOGCAT_TAG_NAME + "_alarm_connect_no", "--------no = " + k);
                        //连接手表mac
                        JSONObject objectMac = device.scanDevice(Global.TYPE_DEVICE_Wristband, macAddress);
                        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_alarm_conn_isSuc", "objectMac，Result = " + objectMac);
                        if (objectMac == null) {
                            //断开设备
                            JSONObject object = device.disconnectDevice(false);        // 断开设备
                            Log.i(CommonConstants.LOGCAT_TAG_NAME + "_set_alarm", "disconncet = " + object);        // 如果为result = 0，则成功，否则失败
                            continue;
                        }

                        //再沉睡0.5s
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

// 设置闹钟开始 》》》
                        if (macAddress != null) {
                            JSONObject datetimeResult = device.setDateTime(Calendar.getInstance());	// 设置手环的日期和时间
                            Log.i("setAlarm", "datetimeResult = " + datetimeResult);		// 如果为result = 0，则成功，否则失败
                            // 设置手环的闹钟
                            JSONObject alarmResult = device.setAlarm(_alarm_hour_1, _alarm_minute_1, _alarm_repeat_1, _alarm_hour_2, _alarm_minute_2, _alarm_repeat_2, _alarm_hour_3, _alarm_minute_3, _alarm_repeat_3);
                            Log.i("setAlarm", "alarmResult = " + alarmResult);				// 如果为result = 0，则成功，否则失败
                            if (alarmResult != null) {
                                String result = alarmResult.getString("result");
                                if (result.equals("0")) {
                                    // 设置完成
                                    handler.sendEmptyMessage(CommonConstants.FLAG_SET_ALARM_SUCCESS);

                                    //连接成功后断开设备
                                    JSONObject object = device.disconnectDevice(false);        // 断开设备
                                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_set_alarm", "disconncet = " + object);        // 如果为result = 0，则成功，否则失败

                                    running = false;//提前结束运行，不提示错误信息
                                    break;//结束循环
                                }
                            }
                        }
// 设置闹钟结束 《《《

                        //每次循环连接都断开设备
                        JSONObject object = device.disconnectDevice(false);        // 断开设备
                        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_set_alarm", "disconncet = " + object);        // 如果为result = 0，则成功，否则失败

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
                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_set_alarm", "-------------set_alarm_fail--------------");
                    //设置闹钟失败
                    String errorMsg = "设置闹钟失败，请重试";
                    CommonUtil.sendErrorMessage(errorMsg, handler);
                    running = false;//结束运行
                }
            }
        }).start();
    }


    /**
     * 填充clock时间信息
     */
    private void fullClockTimeData(){
        //获取目前的该界面的闹钟日期和时间值
        boolean _ison_time1 = mCbIsOnOne.isChecked();
        boolean _ison_time2 = mCbIsOnTwo.isChecked();
        boolean _ison_time3 = mCbIsOnThree.isChecked();

        //第1个闹钟填充时、分、是否重复
        String _time1 = mTvAlarmOne.getText().toString();
        int _alarm_hour_1 = Integer.parseInt(_time1.substring(0,_time1.indexOf(":")));
        int _alarm_minute_1 = Integer.parseInt(_time1.substring(_time1.indexOf(":")+1));
        String _alarm_repeat_str_1="00000000";
        int _alarm_repeat_1 = 0;
        int _ison_time1_int1 = 0;
        if (_ison_time1){
            String _hex_str = mTvDaysOne.getText().toString();
            _alarm_repeat_str_1=_hex_str;
            String _hex_converted = Integer.toString (Integer.parseInt (_hex_str, 2), 16);
            _alarm_repeat_1 = Integer.parseInt(_hex_converted,16);
            _ison_time1_int1 = 1;
            Log.d(CommonConstants.LOGCAT_TAG_NAME,"--------------------1="+_alarm_hour_1+","+_alarm_minute_1+","+_alarm_repeat_1);
        }
        //第2个闹钟填充时、分、是否重复
        String _time2 = mTvAlarmTwo.getText().toString();
        int _alarm_hour_2 = Integer.parseInt(_time2.substring(0,_time2.indexOf(":")));
        int _alarm_minute_2 = Integer.parseInt(_time2.substring(_time2.indexOf(":")+1));
        String _alarm_repeat_str_2="00000000";
        int _alarm_repeat_2 = 0;
        int _ison_time1_int2 = 0;
        if (_ison_time2){
            String _hex_str = mTvDaysTwo.getText().toString();
            _alarm_repeat_str_2=_hex_str;
            String _hex_converted = Integer.toString (Integer.parseInt (_hex_str, 2), 16);
            _alarm_repeat_2 = Integer.parseInt(_hex_converted,16);
            _ison_time1_int2 = 1;
            Log.d(CommonConstants.LOGCAT_TAG_NAME,"--------------------2="+_alarm_hour_2+","+_alarm_minute_2+","+_alarm_repeat_2);
        }

        //第3个闹钟填充时、分、是否重复
        String _time3 = mTvAlarmThree.getText().toString();
        int _alarm_hour_3 = Integer.parseInt(_time3.substring(0,_time3.indexOf(":")));
        int _alarm_minute_3 = Integer.parseInt(_time3.substring(_time3.indexOf(":")+1));
        String _alarm_repeat_str_3="00000000";
        int _alarm_repeat_3 = 0;
        int _ison_time1_int3 = 0;
        if (_ison_time3){
            String _hex_str = mTvDaysTwo.getText().toString();
            _alarm_repeat_str_3=_hex_str;
            String _hex_converted = Integer.toString (Integer.parseInt (_hex_str, 2), 16);
            _alarm_repeat_3 = Integer.parseInt(_hex_converted,16);
            _ison_time1_int3 = 1;
            Log.d(CommonConstants.LOGCAT_TAG_NAME,"--------------------3="+_alarm_hour_3+","+_alarm_minute_3+","+_alarm_repeat_3);
        }
        //时，分，重复星期数，是否开启
        m_click_one = _alarm_hour_1+","+_alarm_minute_1+","+_alarm_repeat_str_1+","+_ison_time1_int1;
        m_click_two = _alarm_hour_2+","+_alarm_minute_2+","+_alarm_repeat_str_2+","+_ison_time1_int2;
        m_click_three = _alarm_hour_3+","+_alarm_minute_3+","+_alarm_repeat_str_3+","+_ison_time1_int3;
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
                        Toast.makeText(AlertAlarmActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CommonConstants.FLAG_SET_ALARM_SUCCESS:
                    setAlarmCompleted();//操作完成
                    break;
                default:
                    break;
            }
        }


    };

    /**
     * 设置闹钟完成
     */
    private void setAlarmCompleted() {
        //保存本地信息，并提示设置成功
        mUser.setClickOne(m_click_one);
        mUser.setClickTwo(m_click_two);
        mUser.setClickThree(m_click_three);
        CommonUtil.saveUserInfo(mUser,this);

        ToastUtil.showTipShort(this,"设置成功");

    }
}
