package com.maikeapp.maikewatch.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gzgamut.sdk.global.Global;
import com.gzgamut.sdk.model.Maike;
import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.adapter.WatchMacListAdapter;
import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.bean.WatchMac;
import com.maikeapp.maikewatch.config.CommonConstants;
import com.maikeapp.maikewatch.util.CommonUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BindWatchActivity extends AppCompatActivity {

    private ImageView mIvCommonBack;//返回
    private TextView mTvCommonTitle;//标题
    private ImageView mIvBindQuestion;//帮助

    private String m_title="绑定手表";

    private LinearLayout mLineBinded;//已绑定

    private ListView mLvMacList;//listview-mac列表
    private WatchMacListAdapter mWatchMacListAdapter;//适配器-mac列表
    private List<WatchMac> mWatchMacs;//数据-mac列表

    private User mUser;
    //sdk
    private Maike device = null;

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
        mLineBinded = (LinearLayout)findViewById(R.id.line_bind_watch_binded);//已绑定状态下显示
    }

    private void initData() {
        //通用控件
        mTvCommonTitle.setText(m_title);

        device = new Maike(BindWatchActivity.this);

        mUser = CommonUtil.getUserInfo(this);
        if (mUser!=null){
            if (mUser.isBindWatch()){
                //已绑定手表
                mLineBinded.setVisibility(View.VISIBLE);
            }else{
                mLineBinded.setVisibility(View.GONE);
                initVisibleWatchMacList();//初始化mac列表
            }

        }


    }

    /**
     * 初始化mac列表
     */
    private void initVisibleWatchMacList() {
        //开启副线程-获取mac列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                // v1.3
// 这里演示搜索所有设备，返回了一个Map，里面包含了所有搜索到的设备的 mac 和 rssi值，需要连接哪个设备，就调用scanDevice(int deviceName, String mac)，把需要连接的设备的mac传进去。
// 开始搜索所有设备 》》》
                Map<String, Integer> deviceMap = device.scanDeviceAll(Global.TYPE_DEVICE_Wristband, 10);	// 传入需要搜索的设备名和需要搜索的秒数，这里传入10，即搜索10秒后返回搜索的结果。
                if (deviceMap != null) {
                    Set<String> keySet = deviceMap.keySet();

                    mWatchMacs = new ArrayList<WatchMac>();
                    for (String deviceMac : keySet) {
                        Integer rssi = deviceMap.get(deviceMac);
                        Log.d(CommonConstants.LOGCAT_TAG_NAME+"_scanDevice", "deviceMac = " + deviceMac + ",    rssi = " + rssi);
                        WatchMac _watch_mac = new WatchMac(rssi,deviceMac);
                        mWatchMacs.add(_watch_mac);
                    }
                    //获取成功
                    handler.sendEmptyMessage(CommonConstants.FLAG_GET_MAC_ADDRESS_SUCCESS);
                }else{
                    String errorMsg = "未查找到手表";
                    CommonUtil.sendErrorMessage(errorMsg,handler);
                }
// 结束搜索所有设备 《《《
            }
        }).start();

//        //数据
//        mWatchMacs = new ArrayList<WatchMac>();
//        WatchMac _watch_mac1 = new WatchMac(-90,"00:00:00:00:5E");
//        WatchMac _watch_mac2 = new WatchMac(-60,"00:00:00:00:5E");
//        mWatchMacs.add(_watch_mac1);
//        mWatchMacs.add(_watch_mac2);
//        //适配器
//        mWatchMacListAdapter = new WatchMacListAdapter(this,mWatchMacs);
//        //设置到listview
//        mLvMacList.setAdapter(mWatchMacListAdapter);

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


    private static class MyHandler extends Handler {
        private final WeakReference<Activity> mActivity;
        public MyHandler(BindWatchActivity activity) {
            mActivity = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            int flag = msg.what;
            switch (flag) {
                case 0:
                    String errorMsg = (String)msg.getData().getSerializable("ErrorMsg");
                    ((BindWatchActivity)mActivity.get()).showTip(errorMsg);
                    break;
                case CommonConstants.FLAG_GET_MAC_ADDRESS_SUCCESS:
                    ((BindWatchActivity)mActivity.get()).updateUIForMacAddress();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 更新UI-更新mac列表
     */
    private void updateUIForMacAddress() {
        //适配器
        mWatchMacListAdapter = new WatchMacListAdapter(this,mWatchMacs);
        //设置到listview
        mLvMacList.setAdapter(mWatchMacListAdapter);
    }

    private MyHandler handler = new MyHandler(this);

    private void showTip(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
