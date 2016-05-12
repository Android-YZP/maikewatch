package com.maikeapp.maikewatch.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
    //个人目标、绑定手表、个人信息、设置闹钟、关于我们、退出登录
    private LinearLayout mLineGlobal;
    private LinearLayout mLineBindWatch;
    private LinearLayout mLinePsInfo;
    private LinearLayout mLineAlertAlarm;
    private LinearLayout mLineAboutUs;
    private LinearLayout mLineUserLogout;
    //用户登录
    private SmartImageView mIvUserLogin;
    private TextView mTvUsername;
    private ImageView mIvBindWatchIsLock;

    private TextView mTvGlobalStep;
    private TextView mTvBatteryStatus;

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
            //更新界面信息
            mLineUserLogout.setVisibility(View.VISIBLE);
            mTvUsername.setText(mUser.getLoginName());
            mIvUserLogin.setImageUrl(mUser.getPortraits(),R.drawable.pscenter_userinfo_headpic);
            mTvGlobalStep.setText(""+mUser.getSportsTarget());
            int _battery = mUser.getBattery();
            if(_battery >= 56){
                mTvBatteryStatus.setText("充足");
            }else if(_battery >= 5 && _battery <= 56){
                mTvBatteryStatus.setText("正常");
            }else if (_battery < 5){
                mTvBatteryStatus.setText("- -");
            }

            if (mUser.isBindWatch()){
                //显示已绑定手表的图标
                mIvBindWatchIsLock.setBackgroundResource(R.drawable.bangding_watch);
            }else{
                //显示未绑定手表的图标
                mIvBindWatchIsLock.setBackgroundResource(R.drawable.pscenter_unbind_lock);
            }
        }else{
            mLineUserLogout.setVisibility(View.GONE);
        }
    }

    private void findView(View view) {
        mLineGlobal = (LinearLayout)view.findViewById(R.id.line_pscenter_global);
        mLineBindWatch = (LinearLayout)view.findViewById(R.id.line_pscenter_bindwatch);
        mLinePsInfo = (LinearLayout)view.findViewById(R.id.line_pscenter_psinfo);
        mLineAlertAlarm = (LinearLayout)view.findViewById(R.id.line_pscenter_alertalarm);
        mLineAboutUs = (LinearLayout)view.findViewById(R.id.line_pscenter_aboutus);
        mLineUserLogout = (LinearLayout)view.findViewById(R.id.line_pscenter_login_out);
        //用户登录
        mIvUserLogin = (SmartImageView)view.findViewById(R.id.iv_pscenter_login);
        mTvUsername = (TextView)view.findViewById(R.id.tv_pscenter_username);
        mTvGlobalStep = (TextView)view.findViewById(R.id.tv_pscenter_global_step);
        mTvBatteryStatus = (TextView)view.findViewById(R.id.tv_pscenter_battery_status);
        //是否已绑定图标
        mIvBindWatchIsLock = (ImageView) view.findViewById(R.id.iv_pscenter_bindwatch_islock);

        initData();
        setListener();
    }

    private void initData() {
        mUser = CommonUtil.getUserInfo(getActivity());
    }

    private void setListener() {
        mLineGlobal.setOnClickListener(new PsCenterOnClickListener());
        mLineBindWatch.setOnClickListener(new PsCenterOnClickListener());
        mLinePsInfo.setOnClickListener(new PsCenterOnClickListener());
        mLineAlertAlarm.setOnClickListener(new PsCenterOnClickListener());
        mLineAboutUs.setOnClickListener(new PsCenterOnClickListener());
        mLineUserLogout.setOnClickListener(new PsCenterOnClickListener());
        //未登录用户-需要登录
        if (mUser==null){
            mIvUserLogin.setOnClickListener(new PsCenterOnClickListener());//用户登录
        }

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
                case R.id.line_pscenter_login_out:
                    //用户注销登录
                    userLoginOut();
                    break;
            }
        }
    }

    /**
     * 用户注销登录
     */
    private void userLoginOut() {
        //是否退出
        new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("您确定退出登录吗？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CommonUtil.clearUserInfo(getActivity());//清除本地数据
                        mLineUserLogout.setVisibility(View.GONE);//隐藏控件
                        mTvUsername.setText("未登录");//更改未登录头像和文字
                        mIvUserLogin.setImageUrl(null, R.drawable.pscenter_userinfo_headpic);
                        mIvUserLogin.setOnClickListener(new PsCenterOnClickListener());//用户登录设置监听事件
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();


    }
}
