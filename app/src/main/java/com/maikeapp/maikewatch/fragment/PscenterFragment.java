package com.maikeapp.maikewatch.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.activity.AboutUsActivity;
import com.maikeapp.maikewatch.activity.AlertAlarmActivity;
import com.maikeapp.maikewatch.activity.BindWatchActivity;
import com.maikeapp.maikewatch.activity.PsGlobalActivity;
import com.maikeapp.maikewatch.activity.PsInfoActivity;
import com.maikeapp.maikewatch.activity.UserLoginActivity;
import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.util.CommonUtil;


public class PscenterFragment extends Fragment {
    private LinearLayout mLineGlobal;
    private LinearLayout mLineBindWatch;
    private LinearLayout mLinePsInfo;
    private LinearLayout mLineAlertAlarm;
    private LinearLayout mLineAboutUs;
    //用户登录
    private SmartImageView mIvUserLogin;
    private TextView mTvUsername;

    private User mUser;
    public PscenterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pscenter, container, false);
        findView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mUser = CommonUtil.getUserInfo(getActivity());
        if (mUser!=null){
            mTvUsername.setText(mUser.getUsername());
            mIvUserLogin.setImageUrl(mUser.getUserImg(),R.drawable.pscenter_userinfo_headpic);
        }
    }

    private void findView(View view) {
        mLineGlobal = (LinearLayout)view.findViewById(R.id.line_pscenter_global);
        mLineBindWatch = (LinearLayout)view.findViewById(R.id.line_pscenter_bindwatch);
        mLinePsInfo = (LinearLayout)view.findViewById(R.id.line_pscenter_psinfo);
        mLineAlertAlarm = (LinearLayout)view.findViewById(R.id.line_pscenter_alertalarm);
        mLineAboutUs = (LinearLayout)view.findViewById(R.id.line_pscenter_aboutus);
        //用户登录
        mIvUserLogin = (SmartImageView)view.findViewById(R.id.iv_pscenter_login);
        mTvUsername = (TextView)view.findViewById(R.id.tv_pscenter_username);

        initData();
        setListener();
    }

    private void initData() {

    }

    private void setListener() {
        mLineGlobal.setOnClickListener(new PsCenterOnClickListener());
        mLineBindWatch.setOnClickListener(new PsCenterOnClickListener());
        mLinePsInfo.setOnClickListener(new PsCenterOnClickListener());
        mLineAlertAlarm.setOnClickListener(new PsCenterOnClickListener());
        mLineAboutUs.setOnClickListener(new PsCenterOnClickListener());
        mIvUserLogin.setOnClickListener(new PsCenterOnClickListener());//用户登录
    }


    private class PsCenterOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent _intent = null;
            switch (v.getId()){
                case R.id.line_pscenter_global:
                    //个人目标
                    _intent = new Intent(getActivity(), PsGlobalActivity.class);
                    startActivity(_intent);
                    break;
                case R.id.line_pscenter_bindwatch:
                    //绑定手表
                    _intent = new Intent(getActivity(), BindWatchActivity.class);
                    startActivity(_intent);
                    break;
                case R.id.line_pscenter_psinfo:
                    //个人信息
                    _intent = new Intent(getActivity(), PsInfoActivity.class);
                    startActivity(_intent);
                    break;
                case R.id.line_pscenter_alertalarm:
                    //智能闹钟
                    _intent = new Intent(getActivity(), AlertAlarmActivity.class);
                    startActivity(_intent);
                    break;
                case R.id.line_pscenter_aboutus:
                    //关于软件
                    _intent = new Intent(getActivity(), AboutUsActivity.class);
                    startActivity(_intent);
                    break;
                case R.id.iv_pscenter_login:
                    //用户登录
                    _intent = new Intent(getActivity(), UserLoginActivity.class);
                    startActivity(_intent);
                    break;
            }
        }
    }
}
