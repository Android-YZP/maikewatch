package com.maikeapp.maikewatch.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.config.CommonConstants;
import com.maikeapp.maikewatch.util.ConvertUtil;

public class OneAlarmActivity extends AppCompatActivity {

    private ImageView mIvCommonBack;//返回
    private TextView mTvCommonTitle;//标题
    private TextView mTvCommonAction;//编辑

    //周日-周六
    private CheckBox mCbSunday;
    private CheckBox mCbMonday;
    private CheckBox mCbTuesday;
    private CheckBox mCbWednesday;
    private CheckBox mCbThusday;
    private CheckBox mCbFriday;
    private CheckBox mCbSatarday;
    private TimePicker mTpTime;


    private String m_title = "闹钟设定";
    private String m_action = "完成";

    private int mWhichClock;//哪个时钟
    private String m_clock_time;//初始化界面用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_alarm);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        //通用控件
        mIvCommonBack = (ImageView) findViewById(R.id.iv_common_back);
        mTvCommonTitle = (TextView) findViewById(R.id.tv_common_title);
        mTvCommonAction = (TextView) findViewById(R.id.tv_common_action);

        mCbSunday = (CheckBox) findViewById(R.id.cb_one_alarm_sunday);
        mCbMonday = (CheckBox) findViewById(R.id.cb_one_alarm_monday);
        mCbTuesday = (CheckBox) findViewById(R.id.cb_one_alarm_tuesday);
        mCbWednesday = (CheckBox) findViewById(R.id.cb_one_alarm_wednesday);
        mCbThusday = (CheckBox) findViewById(R.id.cb_one_alarm_thusday);
        mCbFriday = (CheckBox) findViewById(R.id.cb_one_alarm_friday);
        mCbSatarday = (CheckBox) findViewById(R.id.cb_one_alarm_satarday);
        mTpTime = (TimePicker) findViewById(R.id.tp_one_alarm_time);
        mTpTime.setIs24HourView(true);
    }

    private void initData() {
        //通用控件
        mTvCommonTitle.setText(m_title);
        mTvCommonAction.setText(m_action);
        //获取需要设置哪个手表
        Bundle _bundle = getIntent().getExtras();
        if (_bundle != null) {
            mWhichClock = _bundle.getInt("_which_clock");
            m_clock_time = _bundle.getString("m_clock_time");
            //初始化界面信息
            initDataOfOneAlarm();
        }

    }

    /**
     * 初始化界面的闹钟信息
     */
    private void initDataOfOneAlarm() {
        //闹钟1-界面显示
        if (m_clock_time!=null&&!m_clock_time.equals("")){
            String[] _click_1_strs = m_clock_time.split(",");//小时，分钟，重复星期数，是否开启
            String _click_1_hour = ConvertUtil.convertTimeToShow(_click_1_strs[0]);
            String _click_1_minute = ConvertUtil.convertTimeToShow(_click_1_strs[1]);
            String _click_1_days = _click_1_strs[2];
            mTpTime.setCurrentHour(Integer.parseInt(_click_1_hour));
            mTpTime.setCurrentMinute(Integer.parseInt(_click_1_minute));
            convertDataToDay(_click_1_days);

        }


    }

    /**
     * UI显示-填充闹钟控件的信息
     * @param click_1_days
     */
    private void convertDataToDay(String click_1_days) {
        char[] repeat_days =  click_1_days.toCharArray();
        if (1==Integer.parseInt(String.valueOf(repeat_days[1]))){
            mCbSatarday.setChecked(true);//周六
        }
        if (1==Integer.parseInt(String.valueOf(repeat_days[2]))){
            mCbFriday.setChecked(true);//周五
        }
        if (1==Integer.parseInt(String.valueOf(repeat_days[3]))){
            mCbThusday.setChecked(true);//周四
        }
        if (1==Integer.parseInt(String.valueOf(repeat_days[4]))){
            mCbWednesday.setChecked(true);//周三
        }
        if (1==Integer.parseInt(String.valueOf(repeat_days[5]))){
            mCbTuesday.setChecked(true);//周二
        }
        if (1==Integer.parseInt(String.valueOf(repeat_days[6]))){
            mCbMonday.setChecked(true);//周一
        }
        if (1==Integer.parseInt(String.valueOf(repeat_days[7]))){
            mCbSunday.setChecked(true);//周日
        }
    }

    private void setListener() {
        //通用控件
        mIvCommonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneAlarmActivity.this.finish();
            }
        });

        mTvCommonAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer _sb = new StringBuffer();
                //占位、六、五、四、三、二、一、日
                _sb.append("0");

                if (mCbSatarday.isChecked()) {
                    _sb.append("1");
                } else {
                    _sb.append("0");
                }
                if (mCbFriday.isChecked()) {
                    _sb.append("1");
                } else {
                    _sb.append("0");
                }
                if (mCbThusday.isChecked()) {
                    _sb.append("1");
                } else {
                    _sb.append("0");
                }
                if (mCbWednesday.isChecked()) {
                    _sb.append("1");
                } else {
                    _sb.append("0");
                }
                if (mCbTuesday.isChecked()) {
                    _sb.append("1");
                } else {
                    _sb.append("0");
                }
                if (mCbMonday.isChecked()) {
                    _sb.append("1");
                } else {
                    _sb.append("0");
                }
                if (mCbSunday.isChecked()) {
                    _sb.append("1");
                } else {
                    _sb.append("0");
                }

                String _repeat_day = _sb.toString();
                int _hour = mTpTime.getCurrentHour();
                int _minute = mTpTime.getCurrentMinute();

                String _hourAndTime = (_hour < 10 ? "0" + _hour : _hour) + ":" + (_minute < 10 ? "0" + _minute : _minute);
                Log.d(CommonConstants.LOGCAT_TAG_NAME, "day is " + _repeat_day + ",time is " + _hourAndTime);

                Intent _intent = new Intent(OneAlarmActivity.this,AlertAlarmActivity.class);
                _intent.putExtra("_repeat_day",_repeat_day);
                _intent.putExtra("_hourAndTime",_hourAndTime);
                _intent.putExtra("_which_time",mWhichClock);
                startActivity(_intent);
            }
        });
    }
}
