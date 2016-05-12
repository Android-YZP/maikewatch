package com.maikeapp.maikewatch.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.business.imp.BlueToothController;
import com.maikeapp.maikewatch.fragment.HomeFragment;
import com.maikeapp.maikewatch.fragment.PscenterFragment;
import com.maikeapp.maikewatch.util.CommonUtil;
import com.maikeapp.maikewatch.view.IndexTabBarLayout;

public class MainActivity extends FragmentActivity {
    private IndexTabBarLayout mIndexTabBarLayout;//底部整个控件
    private final static int FLAG_HOME=0;
    private final static int FLAG_PSCENTER=1;

    private Fragment mHomeFragment;
    private Fragment mPscenterFragment;

    private BlueToothController mController = new BlueToothController();//蓝牙帮助类
    private Toast mToast;
    public static final int REQUEST_CODE = 0;
    public static final int LOGIN_BACK_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        setListener();

        //1-判断是否开启了蓝牙；未开启蓝牙，弹出是否开启蓝牙；已开启蓝牙，判断用户是否登录
        //2-已登录用户：判断用户是否绑定过手表，未绑定过，跳转到手表绑定界面（绑定之后把绑定的手表mac地址，上传服务端），
        //3-若已绑定手表，在首页直接同步一次数据，并上传服务器
        checkBlueTooth();
    }


    /**
     * 判断蓝牙是否开启
     * @return
     */
    private void checkBlueTooth() {
        boolean isSupportBlueTooth = mController.isSupportBlueTooth();
        if(isSupportBlueTooth){
            boolean isBlueToothOn = mController.getBlueToothStatus();
            if (isBlueToothOn){
                //已开启蓝牙，判断用户是否登录和是否绑定手表
                checkUserLoginAndBind();//检查用户是否登录和是否绑定手表
            }else{
                //未开启蓝牙，询问是否开启蓝牙
                new AlertDialog.Builder(this)
                        .setTitle("您未开启蓝牙")
                        .setMessage("是否开启蓝牙？")
                        .setPositiveButton("开启", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               //开启蓝牙
                                mController.turnOnBlueTooth(MainActivity.this, REQUEST_CODE);
                            }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }

        }else{
            showToast("不支持蓝牙");
            return;
        }
    }

    /**
     * 检查用户是否登录和是否绑定手表
     */
    private void checkUserLoginAndBind() {
        User mUser = CommonUtil.getUserInfo(this);
        if (mUser!=null){
            //用户已登录，判断该用户是否绑定手表，未绑定，跳转到绑定手表界面；已绑定，直接同步一次数据
            boolean isBindWatch = mUser.isBindWatch();
            if(isBindWatch){
                //同步手表数据（获取手表端数据，并上传到服务端，并在当前界面展示）
//                syncWatchData();
            }else{
                //去绑定
                Intent _intent = new Intent(MainActivity.this,BindWatchActivity.class);
                startActivity(_intent);
            }
        }

    }

    /**
     * 同步手表数据（获取手表端数据，并上传到服务端，并在当前界面展示）
     */
    private void syncWatchData() {
        showToast("正在同步数据...");
    }

    /**
     * 弹出提示信息
     * @param text
     */
    private void showToast(String text) {
        if( mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        }
        else {
            mToast.setText(text);
        }
        mToast.show();
    }
    private void initView() {
        mHomeFragment = new HomeFragment();
        initPageContent(mHomeFragment);
        findView();
    }

    private void findView() {
        mIndexTabBarLayout=(IndexTabBarLayout)findViewById(R.id.myIndexTabBarLayout);
    }

    private void initData() {

    }

    private void setListener() {
        //点击了某个菜单后
        mIndexTabBarLayout.setOnItemClickListener(new IndexTabBarLayout.IIndexTabBarCallBackListener() {
            @Override
            public void clickItem(int id) {
                switch (id) {
                    case R.id.index_home_item:
                        if(mHomeFragment==null){
                            Log.d("jlj-maikewatch","new mHomeFragment");
                            mHomeFragment = new HomeFragment();
                        }
					    initPageContent(mHomeFragment);
                        break;

                    case R.id.index_pscenter_item:
                        if(mPscenterFragment==null){
                            Log.d("jlj-maikewatch","new pscenterfragment");
                            mPscenterFragment = new PscenterFragment();
                        }
					    initPageContent(mPscenterFragment);
                        break;
                }
            }
        });
    }

    /**
     * 初始化第一页初始页(动态切换页面)
     */
	private void initPageContent(Fragment fragment) {
		FragmentManager _manager = getSupportFragmentManager();
		FragmentTransaction _ft = _manager.beginTransaction();
		_ft.replace(R.id.layout_main_frag, fragment);
		_ft.commit();
	}

    private long exitTime=0;//上次系統退出時間
    /**
     * 第二次点击返回，退出
     */
    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出麦客watch", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
            return;
        }
        MainActivity.this.finish();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE){
            //当发起请求蓝牙打开事件时，会告诉你用户选择的结果
            if( resultCode == RESULT_OK) {
                showToast("打开成功");
                checkUserLoginAndBind();//检查用户是否绑定手表
            }
            else {
                showToast("打开失败");
            }
        }

    }
}
