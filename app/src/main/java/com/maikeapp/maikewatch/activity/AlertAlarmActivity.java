package com.maikeapp.maikewatch.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.maikeapp.maikewatch.R;

public class AlertAlarmActivity extends AppCompatActivity {

    private ImageView mIvCommonBack;//返回
    private TextView mTvCommonTitle;//标题

    private String m_title="闹钟设定";

    private TextView mTvAlarmOne;//第一个闹钟时间
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
        mTvAlarmOne = (TextView)findViewById(R.id.tv_alert_alarm_time1);//第一个闹钟时间
    }

    private void initData() {
        //通用控件
        mTvCommonTitle.setText(m_title);

    }

    private void setListener() {
        //通用控件
        mIvCommonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertAlarmActivity.this.finish();
            }
        });
        mTvAlarmOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent _intent = new Intent(AlertAlarmActivity.this,OneAlarmActivity.class);
                AlertAlarmActivity.this.startActivity(_intent);
            }
        });
    }
}
