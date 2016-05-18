package com.maikeapp.maikewatch.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.config.CommonConstants;
import com.maikeapp.maikewatch.util.CommonUtil;

/**
 * 关于我们
 */
public class AboutUsActivity extends AppCompatActivity {

    private ImageView mIvCommonBack;//返回
    private TextView mTvCommonTitle;//标题

    private String m_title="关于软件";

    private User mUser;//用户信息

    private TextView mTvWatchVersion;//固件版本
    private TextView mTvAppVersion;//当前app版本
    private LinearLayout mLineCheckNewVersion;//检查新版本
    private CheckBox mCBIsAutoCheck;//是否检查新版本

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        //通用控件
        mIvCommonBack = (ImageView)findViewById(R.id.iv_common_back);
        mTvCommonTitle = (TextView)findViewById(R.id.tv_common_title);
        mTvWatchVersion = (TextView)findViewById(R.id.tv_about_us_watch_version);
        mTvAppVersion = (TextView)findViewById(R.id.tv_app_version);
        mLineCheckNewVersion = (LinearLayout)findViewById(R.id.line_about_us_check_new_version);
        mCBIsAutoCheck = (CheckBox)findViewById(R.id.cb_about_us_isauto_check_update);
    }

    private void initData() {
        //通用控件
        mTvCommonTitle.setText(m_title);
        mUser = CommonUtil.getUserInfo(this);
        if (mUser!=null){
            String _watchVersion = mUser.getWatchVersion();
            mTvWatchVersion.setText(_watchVersion);
        }
        //获取版本信息
        String _versionName = CommonUtil.getAppVersion(this).getVersionName();
        getVersionFromService();
        mTvAppVersion.setText("v"+_versionName);
    }
    //获取网络版本信息
    private void getVersionFromService() {
    }
    private void setListener() {
        //通用控件
        mIvCommonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutUsActivity.this.finish();
            }
        });
    }
}
