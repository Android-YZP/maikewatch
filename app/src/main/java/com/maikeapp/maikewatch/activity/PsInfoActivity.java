package com.maikeapp.maikewatch.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.util.CommonUtil;
import com.maikeapp.maikewatch.view.CustomSmartImageView;

import org.w3c.dom.Text;

public class PsInfoActivity extends AppCompatActivity {
    private ImageView mIvCommonBack;//返回
    private TextView mTvCommonTitle;//标题
    private TextView mTvCommonAction;//编辑

    private CustomSmartImageView mUserHead;//用户头像
    private TextView mTvLoginName;//用户名

    private String m_title="个人信息";
    private String m_action="完成";

    private User mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ps_info);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        //通用控件
        mIvCommonBack = (ImageView)findViewById(R.id.iv_common_back);
        mTvCommonTitle = (TextView)findViewById(R.id.tv_common_title);
        mTvCommonAction = (TextView)findViewById(R.id.tv_common_action);

        mUserHead = (CustomSmartImageView)findViewById(R.id.iv_ps_info_userhead);
        mTvLoginName = (TextView)findViewById(R.id.tv_ps_info_loginname);
    }

    private void initData() {
        //通用控件
        mTvCommonTitle.setText(m_title);
        mTvCommonAction.setText(m_action);

        mUser = CommonUtil.getUserInfo(this);
        if (mUser!=null){
            mUserHead.setImageUrl(mUser.getPortraits(),R.drawable.pscenter_userinfo_headpic);
            mTvLoginName.setText(mUser.getLoginName());
        }

    }

    private void setListener() {
        //通用控件
        mIvCommonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PsInfoActivity.this.finish();
            }
        });
    }
}
