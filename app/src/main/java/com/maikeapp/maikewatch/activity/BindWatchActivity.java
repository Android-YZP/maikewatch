package com.maikeapp.maikewatch.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.adapter.WatchMacListAdapter;
import com.maikeapp.maikewatch.bean.WatchMac;

import java.util.ArrayList;
import java.util.List;

public class BindWatchActivity extends AppCompatActivity {

    private ImageView mIvCommonBack;//返回
    private TextView mTvCommonTitle;//标题
    private ImageView mIvBindQuestion;//帮助

    private String m_title="绑定手表";

    private ListView mLvMacList;//listview-mac列表
    private WatchMacListAdapter mWatchMacListAdapter;//适配器-mac列表
    private List<WatchMac> mWatchMacs;//数据-mac列表
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_watch);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        //通用控件
        mIvCommonBack = (ImageView)findViewById(R.id.iv_bind_watch_back);
        mTvCommonTitle = (TextView)findViewById(R.id.tv_bind_watch_title);
        mIvBindQuestion = (ImageView)findViewById(R.id.iv_bind_watch_question);
        mLvMacList = (ListView)findViewById(R.id.lv_bind_watch_maclist);
    }

    private void initData() {
        //通用控件
        mTvCommonTitle.setText(m_title);

        initVisibleWatchMacList();//初始化mac列表
    }

    /**
     * 初始化mac列表
     */
    private void initVisibleWatchMacList() {
        //数据
        mWatchMacs = new ArrayList<WatchMac>();
        WatchMac _watch_mac1 = new WatchMac(-90,"00:00:00:00:5E");
        WatchMac _watch_mac2 = new WatchMac(-60,"00:00:00:00:5E");
        mWatchMacs.add(_watch_mac1);
        mWatchMacs.add(_watch_mac2);
        //适配器
        mWatchMacListAdapter = new WatchMacListAdapter(this,mWatchMacs);
        //设置到listview
        mLvMacList.setAdapter(mWatchMacListAdapter);

    }

    private void setListener() {
        //通用控件
        mIvCommonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BindWatchActivity.this.finish();
            }
        });
        //获取帮助
        mIvBindQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent _intent = new Intent(BindWatchActivity.this,BindHelpActivity.class);
                BindWatchActivity.this.startActivity(_intent);
            }
        });
    }
}
