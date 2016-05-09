package com.maikeapp.maikewatch.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.maikeapp.maikewatch.R;

public class PsGlobalActivity extends AppCompatActivity {
    private ImageView mIvCommonBack;//返回
    private TextView mTvCommonTitle;//标题
    private TextView mTvCommonAction;//编辑

    private String m_title="个人目标";
    private String m_action="完成";
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
    }

    private void initData() {
        //通用控件
        mTvCommonTitle.setText(m_title);
        mTvCommonAction.setText(m_action);

    }

    private void setListener() {
        //通用控件
        mIvCommonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PsGlobalActivity.this.finish();
            }
        });
    }
}
