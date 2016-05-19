package com.maikeapp.maikewatch.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gzgamut.sdk.global.Global;
import com.gzgamut.sdk.helper.NoConnectException;
import com.gzgamut.sdk.model.Maike;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.adapter.WatchMacListAdapter;
import com.maikeapp.maikewatch.bean.OneDayData;
import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.bean.WatchMac;
import com.maikeapp.maikewatch.business.IUserBusiness;
import com.maikeapp.maikewatch.business.imp.UserBusinessImp;
import com.maikeapp.maikewatch.config.CommonConstants;
import com.maikeapp.maikewatch.config.MyApplication;
import com.maikeapp.maikewatch.util.CommonUtil;
import com.maikeapp.maikewatch.util.JsonUtils;
import com.maikeapp.maikewatch.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BindWatchActivity extends AppCompatActivity {

    private ImageView mIvCommonBack;//返回
    private TextView mTvCommonTitle;//标题
    private ImageView mIvBindQuestion;//帮助
    private ImageView mIvBindedMac;//已绑定的图标

    private String m_title = "绑定手表";
    /**
     * 已绑定时
     */
    private LinearLayout mLineBinded;//已绑定
    private TextView mTvBindWatchMacAddress;//已绑定时显示的MAC地址

    /**
     * 未绑定时
     */
//    private ListView mLvMacList;//listview-mac列表
    private PullToRefreshListView mPullToRefreshListView;
    private WatchMacListAdapter mWatchMacListAdapter;//适配器-mac列表
    private List<WatchMac> mWatchMacs;//数据-mac列表

    /**
     * 业务层
     */
    private IUserBusiness mUserBusiness = new UserBusinessImp();

    private User mUser;//用户信息
    //sdk
    private Maike device = null;
    //同步时获取每天的数据
    private List<OneDayData> allDayData;
    private int mTargetStep;//个人目标
    private String mWatchVersion;//固件版本号
    private int mBattery;//电量数值

    /**
     * 是否正在同步
     */
    private boolean running;

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
        mIvCommonBack = (ImageView) findViewById(R.id.iv_bind_watch_back);
        mTvCommonTitle = (TextView) findViewById(R.id.tv_bind_watch_title);
        mIvBindQuestion = (ImageView) findViewById(R.id.iv_bind_watch_question);
//        mLvMacList = (ListView)findViewById(R.id.lv_bind_watch_maclist);
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_bind_watch_maclist);
        mLineBinded = (LinearLayout) findViewById(R.id.line_bind_watch_binded);//已绑定状态下显示
        mTvBindWatchMacAddress = (TextView) findViewById(R.id.tv_bind_watch_mac_address);//已绑定的macAddress
        mIvBindedMac = (ImageView) findViewById(R.id.iv_bind_watch_binding);//已绑定的macAddress
    }

    private void initData() {
        //通用控件
        mTvCommonTitle.setText(m_title);
        //初始化device
        device = MyApplication.newMaikeInstance();

        mUser = CommonUtil.getUserInfo(this);
        if (mUser != null) {
            if (mUser.isBindWatch()) {
                //已绑定手表
                mPullToRefreshListView.setVisibility(View.GONE);
                mLineBinded.setVisibility(View.VISIBLE);
                mTvBindWatchMacAddress.setText("已绑定 " + mUser.getMacAddress());
            } else {
                //未绑定手表
                mPullToRefreshListView.setVisibility(View.VISIBLE);
                mLineBinded.setVisibility(View.GONE);

                if (device != null) {
                    initVisibleWatchMacList();//初始化mac列表

                }
            }

        }
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
                Intent _intent = new Intent(BindWatchActivity.this, BindHelpActivity.class);
                BindWatchActivity.this.startActivity(_intent);
            }
        });
        //下拉刷新控件
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase refreshView) {
                //加载数据
                initVisibleWatchMacList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase refreshView) {
                //加载数据
                initVisibleWatchMacList();
            }


        });

        //点击某只手表进行绑定
        mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (running) {
                    ToastUtil.showTipShort(BindWatchActivity.this,"正在绑定中...");
                    return;
                }

                //绑定某只手表
                WatchMac _watch_mac = mWatchMacs.get(position - 1);
                Log.d(CommonConstants.LOGCAT_TAG_NAME + "_watch_mac", _watch_mac.getMac());
                //进行绑定操作-并保存已绑定信息
                bindWatch(_watch_mac.getMac());
            }
        });

        //解绑手表
        mIvBindedMac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (running) {
                    ToastUtil.showTipShort(BindWatchActivity.this,"正在解绑中...");
                    return;
                }

                new AlertDialog.Builder(BindWatchActivity.this)
                        .setTitle("提示")
                        .setMessage("您确定解绑手表吗？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String _macAddress = mUser.getMacAddress();
                                //进行解绑
                                unBindWatch(_macAddress);

                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();

            }
        });
    }

    /**
     * 初始化mac列表
     */
    private void initVisibleWatchMacList() {
        Toast.makeText(this, "正在搜索手表，请耐心等待...", Toast.LENGTH_LONG).show();
        //开启副线程-获取mac列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                // v1.3
// 这里演示搜索所有设备，返回了一个Map，里面包含了所有搜索到的设备的 mac 和 rssi值，需要连接哪个设备，就调用scanDevice(int deviceName, String mac)，把需要连接的设备的mac传进去。
// 开始搜索所有设备 》》》
                Map<String, Integer> deviceMap = device.scanDeviceAll(Global.TYPE_DEVICE_Wristband, 10);    // 传入需要搜索的设备名和需要搜索的秒数，这里传入10，即搜索10秒后返回搜索的结果。
                if (deviceMap != null) {
                    Set<String> keySet = deviceMap.keySet();
                    mWatchMacs = new ArrayList<WatchMac>();
                    if (keySet.size() > 0) {

                        for (String deviceMac : keySet) {
                            Integer rssi = deviceMap.get(deviceMac);
                            Log.d(CommonConstants.LOGCAT_TAG_NAME + "_scanDevice", "deviceMac = " + deviceMac + ",    rssi = " + rssi);
                            WatchMac _watch_mac = new WatchMac(rssi, deviceMac);
                            mWatchMacs.add(_watch_mac);
                        }
                        //获取成功
                        handler.sendEmptyMessage(CommonConstants.FLAG_GET_MAC_ADDRESS_SUCCESS);
                    } else {
                        //未查找到手表
                        handler.sendEmptyMessage(CommonConstants.FLAG_GET_MAC_NONE);
                    }

                }
// 结束搜索所有设备 《《《
            }
        }).start();


    }


    /**
     * 进行绑定操作-并保存已绑定信息
     *
     * @param macAddress
     */
    private void bindWatch(final String macAddress) {
        Toast.makeText(this, "手表灯亮时，请双击表盘进行绑定", Toast.LENGTH_LONG).show();
        //开启副线程-进行绑定操作
        new Thread(new Runnable() {
            @Override
            public void run() {

                running = true;//正在运行

                //循环5次连接，若连接不成功给予用户提醒
                for (int k = 0; k < 5; k++) {
                    try {
                        //暂时先沉睡2s
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.i(CommonConstants.LOGCAT_TAG_NAME + "_bind_connect_no", "--------no = " + k);
                        //连接手表mac
                        JSONObject objectMac = device.scanDevice(Global.TYPE_DEVICE_Wristband, macAddress);
                        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_bind_connect_isSuc", "objectMac，Result = " + objectMac);
                        if (objectMac == null) {
                            //断开设备
                            JSONObject object = device.disconnectDevice(false);        // 断开设备
                            Log.i(CommonConstants.LOGCAT_TAG_NAME + "_bind_disconnect", "disconncet = " + object);        // 如果为result = 0，则成功，否则失败
                            continue;
                        }

                        //再沉睡0.5s
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                        // 这里演示一下第一次绑定新手环的步骤，即连接成功后向手环发送绑定指令，等待手环双击结果返回，返回成功的结果后就算是绑定成功。如果用户不双击，则绑定失败，需断开手环的连接。
// 绑定开始 》》》
                        if (macAddress != null) {
                            JSONObject setBoundResult = device.setBoundState(Global.TYPE_BOUND_ON);    // 设置手环绑定状态，发完这条指令之后，需双击手环后才收到返回结果。
                            Log.d(CommonConstants.LOGCAT_TAG_NAME + "_bind", "绑定setBoundResult = " + setBoundResult);        // 如果为result = 0，则成功，否则失败
                            if (setBoundResult != null) {
                                String result = setBoundResult.getString("result");
                                if (result.equals("0")) {
                                    //覆盖用户信息
                                    mUser.setBindWatch(true);
                                    mUser.setMacAddress(macAddress);
                                    CommonUtil.saveUserInfo(mUser, BindWatchActivity.this);
                                    //绑定成功
                                    handler.sendEmptyMessage(CommonConstants.FLAG_BIND_MAC_ADDRESS_SUCCESS);

                                    //连接成功后断开设备
                                    JSONObject object = device.disconnectDevice(false);        // 断开设备
                                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_bind_disconnect", "disconncet = " + object);        // 如果为result = 0，则成功，否则失败

                                    running = false;//提前结束运行，不提示错误信息
                                    break;//结束循环

                                }
                            }
                        }


// 绑定结束 《《《
                        //每次循环连接都断开设备
                        JSONObject object = device.disconnectDevice(false);        // 断开设备
                        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_bind_disconnect", "disconncet = " + object);        // 如果为result = 0，则成功，否则失败


                    } catch (NoConnectException e) {
                        e.printStackTrace();
                        //发现连接异常，结束本次循环，进入下一次连接
                        continue;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                //循环5次依然没连上，提示错误信息，并running未false
                if (running) {
                    //绑定失败
                    String errorMsg = "绑定手表失败，请检查重连蓝牙，并查看手表是否亮灯";
                    CommonUtil.sendErrorMessage(errorMsg, handler);
                    running = false;//结束运行
                }

            }
        }).start();
    }


    /**
     * 解绑手表操作-并保存已解绑信息
     *
     * @param macAddress mac地址
     */
    private void unBindWatch(final String macAddress) {
        //开启副线程-进行解绑操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                running = true;//正在运行

                //循环5次连接，若连接不成功给予用户提醒
                for (int k = 0; k < 5; k++) {
                    try {
                        //暂时先沉睡2s
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.i(CommonConstants.LOGCAT_TAG_NAME + "_unbind_connect_no", "--------no = " + k);
                        //连接手表mac
                        JSONObject objectMac = device.scanDevice(Global.TYPE_DEVICE_Wristband, macAddress);
                        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_unbind_conn_isSuc", "objectMac，Result = " + objectMac);
                        if (objectMac == null) {
                            //断开设备
                            JSONObject object = device.disconnectDevice(false);        // 断开设备
                            Log.i(CommonConstants.LOGCAT_TAG_NAME + "_unbind_disconnect", "disconncet = " + object);        // 如果为result = 0，则成功，否则失败
                            continue;
                        }

                        //再沉睡0.5s
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

// 解绑开始 》》》
                        if (macAddress != null) {
                            JSONObject setBoundResult = device.setBoundState(Global.TYPE_BOUND_OFF);
                            Log.d(CommonConstants.LOGCAT_TAG_NAME + "_unbind", "解绑setBoundResult = " + setBoundResult);        // 如果为result = 0，则成功，否则失败
                            if (setBoundResult != null) {
                                String result = setBoundResult.getString("result");
                                if (result.equals("0")) {

                                    //覆盖用户信息
                                    mUser.setBindWatch(false);
                                    mUser.setMacAddress("");
                                    CommonUtil.saveUserInfo(mUser, BindWatchActivity.this);
                                    //绑定成功
                                    handler.sendEmptyMessage(CommonConstants.FLAG_UNBIND_MAC_ADDRESS_SUCCESS);
                                    //连接成功后断开设备
                                    JSONObject object = device.disconnectDevice(false);        // 断开设备
                                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_unbind_disconnect", "disconncet = " + object);        // 如果为result = 0，则成功，否则失败

                                    running = false;//提前结束运行，不提示错误信息
                                    break;//结束循环
                                }
                            }
                        }
// 解绑结束 《《《

                        //每次循环连接都断开设备
                        JSONObject object = device.disconnectDevice(false);        // 断开设备
                        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_unbind_disconnect", "disconncet = " + object);        // 如果为result = 0，则成功，否则失败

                    } catch (NoConnectException e) {
                        e.printStackTrace();
                        //发现连接异常，结束本次循环，进入下一次连接
                        continue;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //循环5次依然没连上，提示错误信息，并running未false
                if (running) {
                    //解绑失败
                    String errorMsg = "解绑手表失败";
                    CommonUtil.sendErrorMessage(errorMsg, handler);
                    running = false;//结束运行
                }
            }
        }).start();
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
                    String errorMsg = (String) msg.getData().getSerializable("ErrorMsg");
                    ((BindWatchActivity) mActivity.get()).showErrorMessage(errorMsg);
                    break;
                case CommonConstants.FLAG_GET_MAC_ADDRESS_SUCCESS:
                    ((BindWatchActivity) mActivity.get()).updateUIForMacAddress();
                    break;
                case CommonConstants.FLAG_GET_MAC_NONE:
                    ((BindWatchActivity) mActivity.get()).notifyUIForMacAddress();
                    break;
                case CommonConstants.FLAG_BIND_MAC_ADDRESS_SUCCESS:
                    ((BindWatchActivity) mActivity.get()).updateUIForBindSuccess();
                    break;
                case CommonConstants.FLAG_UNBIND_MAC_ADDRESS_SUCCESS:
                    ((BindWatchActivity) mActivity.get()).updateUIForUnBindSuccess();
                    break;
                default:
                    break;
            }

            if (((BindWatchActivity) mActivity.get()).mPullToRefreshListView != null) {
                // Call onRefreshComplete when the list has been refreshed.
                ((BindWatchActivity) mActivity.get()).mPullToRefreshListView.onRefreshComplete();
            }

        }
    }

    /**
     * 提示错误信息
     * @param errorMsg 错误信息
     */
    private void showErrorMessage(String errorMsg) {
        ToastUtil.showTipShort(this,errorMsg);
    }

    private MyHandler handler = new MyHandler(this);

    //搜索手表=======================================start

    /**
     * 搜索失败-提醒用户未搜索到手表
     */
    private void notifyUIForMacAddress() {
        ToastUtil.showTipShort(BindWatchActivity.this,"未搜索到手表");
        updateUIForMacAddress();

    }

    /**
     * 搜索成功-更新UI-更新mac列表
     */
    private void updateUIForMacAddress() {
        //适配器
        mWatchMacListAdapter = new WatchMacListAdapter(this, mWatchMacs);
        //设置到listview
        mPullToRefreshListView.setAdapter(mWatchMacListAdapter);
    }
    //搜索手表=======================================end

    /**
     * 绑定成功-更新UI
     */
    private void updateUIForBindSuccess() {
        String _macAddress = mUser.getMacAddress();
        mTvBindWatchMacAddress.setText("已绑定 " + _macAddress);
        //显示已绑定状态下布局，隐藏未绑定状态下布局
        mLineBinded.setVisibility(View.VISIBLE);
        mPullToRefreshListView.setVisibility(View.GONE);

    }

    /**
     * 解绑成功-更新UI
     */
    private void updateUIForUnBindSuccess() {
        ToastUtil.showTipShort(BindWatchActivity.this,"解绑成功");
        //显示已绑定状态下布局，隐藏未绑定状态下布局
        mLineBinded.setVisibility(View.GONE);
        mPullToRefreshListView.setVisibility(View.VISIBLE);
    }


}
