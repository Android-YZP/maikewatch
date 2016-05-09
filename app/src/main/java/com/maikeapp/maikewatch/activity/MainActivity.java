package com.maikeapp.maikewatch.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.util.Log;
import android.widget.Toast;

import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.fragment.HomeFragment;
import com.maikeapp.maikewatch.fragment.PscenterFragment;
import com.maikeapp.maikewatch.view.IndexTabBarLayout;

public class MainActivity extends FragmentActivity {
    private IndexTabBarLayout mIndexTabBarLayout;//底部整个控件
    private final static int FLAG_HOME=0;
    private final static int FLAG_PSCENTER=1;

    private Fragment mHomeFragment;
    private Fragment mPscenterFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        setListener();
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
}
