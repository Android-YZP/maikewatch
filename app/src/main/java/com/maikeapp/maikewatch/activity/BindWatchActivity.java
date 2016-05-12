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
import com.maikeapp.maikewatch.util.CommonUtil;
import com.maikeapp.maikewatch.util.JsonUtils;

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

    private String m_title="绑定手表";
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
        mIvCommonBack = (ImageView)findViewById(R.id.iv_bind_watch_back);
        mTvCommonTitle = (TextView)findViewById(R.id.tv_bind_watch_title);
        mIvBindQuestion = (ImageView)findViewById(R.id.iv_bind_watch_question);
//        mLvMacList = (ListView)findViewById(R.id.lv_bind_watch_maclist);
        mPullToRefreshListView = (PullToRefreshListView)findViewById(R.id.lv_bind_watch_maclist);
        mLineBinded = (LinearLayout)findViewById(R.id.line_bind_watch_binded);//已绑定状态下显示
        mTvBindWatchMacAddress = (TextView)findViewById(R.id.tv_bind_watch_mac_address);//已绑定的macAddress
        mIvBindedMac = (ImageView) findViewById(R.id.iv_bind_watch_binding);//已绑定的macAddress
    }

    private void initData() {
        //通用控件
        mTvCommonTitle.setText(m_title);
        //初始化device
        try{
            device = new Maike(BindWatchActivity.this);
        }catch (Exception e){
            e.printStackTrace();
        }


        mUser = CommonUtil.getUserInfo(this);
        if (mUser!=null){
            if (mUser.isBindWatch()){
                //已绑定手表
                mPullToRefreshListView.setVisibility(View.GONE);
                mLineBinded.setVisibility(View.VISIBLE);
                mTvBindWatchMacAddress.setText("已绑定 "+mUser.getMacAddress());
            }else{
                //未绑定手表
                mPullToRefreshListView.setVisibility(View.VISIBLE);
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
                //绑定某只手表
                WatchMac _watch_mac = mWatchMacs.get(position-1);
                Log.d(CommonConstants.LOGCAT_TAG_NAME+"_watch_mac",_watch_mac.getMac());
                //进行绑定操作-并保存已绑定信息
                bindWatch(_watch_mac.getMac());
            }
        });

        //解绑手表
        mIvBindedMac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(BindWatchActivity.this)
                        .setTitle("提示")
                        .setMessage("您确定解绑手表吗？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //确认解绑
                                String _macAddress = mUser.getMacAddress();
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
     * 解绑手表操作-并保存已解绑信息
     * @param macAddress
     */
    private void unBindWatch(final String macAddress) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 解绑某只手表mac
                JSONObject objectMac = device.scanDevice(Global.TYPE_DEVICE_Wristband, macAddress);
                Log.d(CommonConstants.LOGCAT_TAG_NAME+"_unbind", "objectMac，Result = " + objectMac);
                try {

//			return true;		// 如果不需要绑定，直接return true;

                    //v1.2
                    // 这里演示一下第一次绑定新手环的步骤，即连接成功后向手环发送绑定指令，等待手环双击结果返回，返回成功的结果后就算是绑定成功。如果用户不双击，则绑定失败，需断开手环的连接。
                    // 如果已经绑定过手环，就不需要执行绑定指令，直接return true就可以了
// 绑定开始 》》》
                    if (macAddress != null) {
                        try {
                            Thread.sleep(2000);//沉睡2s
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        JSONObject setBoundResult = device.setBoundState(Global.TYPE_BOUND_OFF);	// 设置手环绑定状态，发完这条指令之后，需双击手环后才收到返回结果。
                        Log.d(CommonConstants.LOGCAT_TAG_NAME+"_unbind", "解绑setBoundResult = " + setBoundResult);		// 如果为result = 0，则成功，否则失败
                        if (setBoundResult != null) {
                            String result = setBoundResult.getString("result");
                            if (result.equals("0")) {

                                //覆盖用户信息
                                mUser.setBindWatch(false);
                                mUser.setMacAddress("");
                                CommonUtil.saveUserInfo(mUser,BindWatchActivity.this);
                                //绑定成功
                                handler.sendEmptyMessage(CommonConstants.FLAG_UNBIND_MAC_ADDRESS_SUCCESS);
                                return; // 解绑成功
                            }
                        }
                    }
// 绑定结束 《《《

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NoConnectException e) {
                    e.printStackTrace();
                }
                //解绑失败
                String errorMsg = "解绑手表失败";
                CommonUtil.sendErrorMessage(errorMsg,handler);
            }
        }).start();
    }

    /**
     * 进行绑定操作-并保存已绑定信息
     * @param macAddress
     */
    private void bindWatch(final String macAddress) {
        Toast.makeText(this,"手表灯亮时，请双击表盘进行绑定",Toast.LENGTH_LONG).show();
        //开启副线程-进行绑定操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                //绑定某只手表mac
                JSONObject objectMac = device.scanDevice(Global.TYPE_DEVICE_Wristband, macAddress);
                Log.d(CommonConstants.LOGCAT_TAG_NAME+"_bind", "objectMac，Result = " + objectMac);
                try {

//			return true;		// 如果不需要绑定，直接return true;

                    //v1.2
                    // 这里演示一下第一次绑定新手环的步骤，即连接成功后向手环发送绑定指令，等待手环双击结果返回，返回成功的结果后就算是绑定成功。如果用户不双击，则绑定失败，需断开手环的连接。
                    // 如果已经绑定过手环，就不需要执行绑定指令，直接return true就可以了
// 绑定开始 》》》
                    if (macAddress != null) {
                        JSONObject setBoundResult = device.setBoundState(Global.TYPE_BOUND_ON);	// 设置手环绑定状态，发完这条指令之后，需双击手环后才收到返回结果。
                        Log.d(CommonConstants.LOGCAT_TAG_NAME+"_bind", "绑定setBoundResult = " + setBoundResult);		// 如果为result = 0，则成功，否则失败
                        if (setBoundResult != null) {
                            String result = setBoundResult.getString("result");
                            if (result.equals("0")) {
                                //覆盖用户信息
                                mUser.setBindWatch(true);
                                mUser.setMacAddress(macAddress);
                                CommonUtil.saveUserInfo(mUser,BindWatchActivity.this);
                               //绑定成功
                                handler.sendEmptyMessage(CommonConstants.FLAG_BIND_MAC_ADDRESS_SUCCESS);
                                return;
                            }
                        }
                    }
// 绑定结束 《《《

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NoConnectException e) {
                    e.printStackTrace();
                }
                //绑定失败
                String errorMsg = "绑定手表失败，请检查蓝牙是否开启，手表是否亮灯";
                CommonUtil.sendErrorMessage(errorMsg,handler);
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
                    String errorMsg = (String)msg.getData().getSerializable("ErrorMsg");
                    ((BindWatchActivity)mActivity.get()).showTip(errorMsg);
                    break;
                case CommonConstants.FLAG_GET_MAC_ADDRESS_SUCCESS:
                    ((BindWatchActivity)mActivity.get()).updateUIForMacAddress();
                    break;
                case CommonConstants.FLAG_BIND_MAC_ADDRESS_SUCCESS:
                    ((BindWatchActivity)mActivity.get()).updateUIForBindSuccess();
                    break;
                case CommonConstants.FLAG_UNBIND_MAC_ADDRESS_SUCCESS:
                    ((BindWatchActivity)mActivity.get()).updateUIForUnBindSuccess();
                    break;
                case CommonConstants.FLAG_SYNC_COMPLETED_SUCCESS:
                    ((BindWatchActivity)mActivity.get()).showTip("同步完成");
                    break;
                default:
                    break;
            }

            if (((BindWatchActivity)mActivity.get()).mPullToRefreshListView!=null){
                // Call onRefreshComplete when the list has been refreshed.
                ((BindWatchActivity)mActivity.get()).mPullToRefreshListView.onRefreshComplete();
            }

        }
    }

    /**
     * 更新UI-解绑成功
     */
    private void updateUIForUnBindSuccess() {
        showTip("解绑成功");
        //显示已绑定状态下布局，隐藏未绑定状态下布局
        mLineBinded.setVisibility(View.GONE);
        mPullToRefreshListView.setVisibility(View.VISIBLE);
    }

    /**
     * 更新UI-绑定成功
     */
    private void updateUIForBindSuccess() {
        String _macAddress = mUser.getMacAddress();
        mTvBindWatchMacAddress.setText("已绑定 "+_macAddress);
        //显示已绑定状态下布局，隐藏未绑定状态下布局
        mLineBinded.setVisibility(View.VISIBLE);
        mPullToRefreshListView.setVisibility(View.GONE);

        //同步一次数据
        syncWatchData(_macAddress);

    }

    /**
     * 连接设备
     * @param macAddress
     */
    private void connectWacth(final String macAddress) {
        //连接某只手表mac
        JSONObject objectMac = this.device.scanDevice(Global.TYPE_DEVICE_Wristband, macAddress);
        Log.i("bind", "objectMac，Result = " + objectMac);
    }

    /**
     * 断开连接设备
     */
    private void disConnectWatch() {
        try {
            boolean isDestroy = true;
            JSONObject object = device.disconnectDevice(isDestroy);		// 断开设备
            Log.d("disconnect", "disconncet = " + object);		// 如果为result = 0，则成功，否则失败
        } catch (NoConnectException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 同步数据
     */
    private void syncWatchData(final String macAddress) {
        Toast.makeText(this,"正在同步数据...",Toast.LENGTH_SHORT).show();
        //连接设备
//        connectWacth(macAddress);
        //开启副线程-同步数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    running = true;
                    JSONObject boundInfoResult = device.getBoundInfo();	// 获取手环的绑定信息
                    Log.d("sync", "boundInfoResult = " + boundInfoResult); 		// 如果result = 0，则未绑定，如果result = 1，则已绑定

//			JSONObject setBoundNoConfirmResult = this.device.setBoundStateNoConfirm();		// 跳过验证设置手环绑定状态，从获取手环的绑定信息的结果中来判断是否需要发这条指令，如果未绑定，就发这个指令。
//			Log.i("sync", "setBoundNoConfirmResult = " + setBoundNoConfirmResult);		// 如果为result = 0，则成功，否则失败

                    JSONObject datetimeResult = device.setDateTime(Calendar.getInstance());	// 设置手环的日期和时间
                    Log.d("sync", "datetimeResult = " + datetimeResult);		// 如果为result = 0，则成功，否则失败

//                    JSONObject alarmResult = device.setAlarm(8, 0, 0x1f, 8, 0, 0, 8, 0, 0);	// 设置手环的闹钟
//                    Log.d("sync", "alarmResult = " + alarmResult);				// 如果为result = 0，则成功，否则失败

                    JSONObject targetResult = device.setTarget(2000);		// 设置手环的步数目标
                    Log.d("sync", "targetResult = " + targetResult);			// 如果为result = 0，则成功，否则失败
                    mTargetStep = 2000;//默认目标2000
                    mUser.setSportsTarget(mTargetStep);

                    JSONObject versionResult = device.getVersion();		// 获取手环的固件版本号
                    Log.d("sync", "versionResult = " + versionResult);			// result返回是一串字符，即版本号
                    mWatchVersion = JsonUtils.getString(versionResult,"result");
                    mUser.setWatchVersion(mWatchVersion);

                    JSONObject batteryResult = device.getBattery();		// 获取手环的电量
                    Log.d("sync", "batteryResult= " + batteryResult);			// result 里面的数值就是电量
                    mBattery = JsonUtils.getInt(batteryResult,"result");
                    mUser.setBattery(mBattery);

//			JSONObject callResult = this.device.writeCallReminder();		// 向手环写来电提醒指令，有来电时才发这个指令
//			Log.i("sync", "callResult = " + callResult);		// 如果为result = 0，则成功，否则失败

                    int count = 0;
                    int sn = 0;

                    if (running) {
                        // 获取手环存储数据的总天数和当天数据所在的位置
                        JSONObject objectCount = device.getActivityCount();
                        count = objectCount.getInt("count");	// 总天数
                        if (count>0){
                            allDayData = new ArrayList<OneDayData>();
                        }else{
                            //无数据
                            return;
                        }

                        sn = objectCount.getInt("sn");		// 当天数据所在的位置
                        Log.d("sync", "count = " + count + ", sn = " + sn);
                    }

                    // 获取手环所有天数的步数，从最旧的日期开始获取，一直到最新的日期
                    for (int i = 1; i <= count; i++) {
                        if (running) {
                            JSONObject objectActivity_0 = device.getActivityBySn((sn + i) % count, 0);
                            parserActivityValue(objectActivity_0);
                        }
                        if (running) {
                            JSONObject objectActivity_6 = device.getActivityBySn((sn + i) % count, 6);
                            parserActivityValue(objectActivity_6);
                        }
                        if (running) {
                            JSONObject objectActivity_12 = device.getActivityBySn((sn + i) % count, 12);
                            parserActivityValue(objectActivity_12);
                        }
                        if (running) {
                            JSONObject objectActivity_18 = device.getActivityBySn((sn + i) % count, 18);
                            parserActivityValue(objectActivity_18);
                        }
                    }
                    //上传数据到服务器-上传个人目标
                    String _setResult = mUserBusiness.setSportsTarget(mUser);
                    Log.d(CommonConstants.LOGCAT_TAG_NAME+"_setTarget_result",_setResult);
                    JSONObject _json_result = new JSONObject(_setResult);
                    boolean Success = JsonUtils.getBoolean(_json_result,"Success");
                    if (Success){
                        Log.d(CommonConstants.LOGCAT_TAG_NAME+"_Success","true");
                    }else{
                        Log.d(CommonConstants.LOGCAT_TAG_NAME+"_Success","false");
                    }
//                    if (allDayData!=null&&allDayData.size()>0){
//                        //有数据
//
//                    }

                    // 设置手环同步完成
                    device.setFinishSync();
                    // 同步完成
                    handler.sendEmptyMessage(CommonConstants.FLAG_SYNC_COMPLETED_SUCCESS);

                } catch (NoConnectException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
                running = false;
                //断开连接
                disConnectWatch();
            }
        }).start();


    }

    /**
     * 解析步数
     */
    private void parserActivityValue(JSONObject object) {
        if (running) {
            try {
                if (object!=null){
                    JSONArray jsonArray = object.getJSONArray("ActivityData");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.opt(i);

                        String date = jsonObject.getString("year") + "-" + jsonObject.getString("month") + "-" + jsonObject.getString("day");// 日期
                        int time = Integer.parseInt(jsonObject.getString("hour"));//小时
                        String value = jsonObject.getString("value");// 步数或睡眠分数
                        int step = Integer.parseInt(value);
                        String type = jsonObject.getString("type");// 类型，如果是"step"，则表明value为步数，如果是"sleep"，则表明value是睡眠分数

                        //添加到allDayData中
                        OneDayData _one_day_data = new OneDayData(date,time,step,type);
                        allDayData.add(_one_day_data);

                        Log.i("sync", "data = " + object);
                        Log.i("sync", date + ", " +time + ", " + value + ", " + type);
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
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
//        mLvMacList.setAdapter(mWatchMacListAdapter);
        mPullToRefreshListView.setAdapter(mWatchMacListAdapter);
    }

    private MyHandler handler = new MyHandler(this);

    private void showTip(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 退出时，断开设备，传入true
        actionDisconnect(true);



    }


    /**
     * 点击了断开连接任务
     */
    private void actionDisconnect(boolean isDestroy) {
        try {
            JSONObject object = device.disconnectDevice(isDestroy);		// 断开设备
            Log.i("disconnect", "disconncet = " + object);		// 如果为result = 0，则成功，否则失败
        } catch (NoConnectException e) {
            e.printStackTrace();
        }
    }

}
