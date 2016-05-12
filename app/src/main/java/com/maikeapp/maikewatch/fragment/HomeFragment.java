package com.maikeapp.maikewatch.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gzgamut.sdk.global.Global;
import com.gzgamut.sdk.helper.NoConnectException;
import com.gzgamut.sdk.model.Maike;
import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.bean.OneDayData;
import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.business.IUserBusiness;
import com.maikeapp.maikewatch.business.imp.UserBusinessImp;
import com.maikeapp.maikewatch.config.CommonConstants;
import com.maikeapp.maikewatch.util.CommonUtil;
import com.maikeapp.maikewatch.util.JsonUtils;
import com.maikeapp.maikewatch.view.CirclePercentView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.util.MathHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private CirclePercentView mCirclePercentView;//总进度
    private TextView mTvDate;//日期

    private ImageView mIvHistoryData;//历史数据
    private ImageView mIvShare;//分享

    private TextView mTvSportsTarget;//个人目标
    private TextView mTvSumSteps;//总步数
    private TextView mTvSumCarolies;//总热量
    private TextView mTvSumDistance;//里程数

    private LinearLayout mLinearChart;//图表
    /**
     * 业务层
     */
    private IUserBusiness mUserBusiness = new UserBusinessImp();
    private User mUser;//用户信息
    //sdk
    private Maike device = null;

    //同步时获取每天的数据
    private List<OneDayData> allDayData;

    private int mBattery;//电量数值

    /**
     * 是否正在同步
     */
    private boolean running;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        findView(view);
        return view;
    }


    void findView(View view) {
        initView(view);
        initData();
        lineView(null);//显示折线图
        setListener();
    }

    private void initView(View view) {

        mIvHistoryData = (ImageView)view.findViewById(R.id.iv_home_history_data);
        mIvShare = (ImageView)view.findViewById(R.id.iv_home_history_data);

        mTvDate = (TextView)view.findViewById(R.id.tv_home_one_day_date);
        mCirclePercentView = (CirclePercentView) view.findViewById(R.id.circleView);


        mTvSportsTarget = (TextView)view.findViewById(R.id.tv_home_one_day_sports_target);
        mTvSumSteps = (TextView)view.findViewById(R.id.tv_home_one_day_sum_steps);
        mTvSumCarolies = (TextView)view.findViewById(R.id.tv_home_one_day_sum_calories);
        mTvSumDistance = (TextView)view.findViewById(R.id.tv_home_one_day_sum_distance);


//        mTvSportsTarget.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int n = 101;
//                mCirclePercentView.setPercent(n);
//            }
//        });

        mLinearChart = (LinearLayout) view.findViewById(R.id.line_other_chart);
    }

    private void initData() {
        //初始化日期
        Date _today = new Date();
        SimpleDateFormat _sdf = new SimpleDateFormat("MM月dd日");
        String _TodayStr = _sdf.format(_today);
        mTvDate.setText(_TodayStr);
        //获取用户信息
        mUser = CommonUtil.getUserInfo(getActivity());
        if (mUser!=null){
            mTvSportsTarget.setText("目标:"+mUser.getSportsTarget());
            //从服务端初始化当日同步前的数据
            // TODO: 2016/5/12
        }else{
            mTvSportsTarget.setText("");
            mTvSumSteps.setText("0步");
            mTvSumCarolies.setText("0千卡");
            mTvSumDistance.setText("0公里");
            mCirclePercentView.setPercent(0);
        }


        //初始化device
        try{
            device = new Maike(getActivity());
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void setListener() {
        /**
         * 点击了百分比进度条，同步数据，并刷新界面
         */
        mCirclePercentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncWatchData();//同步手表数据
            }
        });
        /**
         * 查看历史数据
         */
        mIvHistoryData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/5/12  
            }
        });
        /**
         * 分享今日数据
         */
        mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016/5/12  
            }
        });
    }

    /**
     * 同步手表数据（获取手表端数据，并上传到服务端，并在当前界面展示）
     */
    private void syncWatchData() {
        Toast.makeText(getActivity(),"正在同步数据...",Toast.LENGTH_LONG).show();
        //开启副线程-同步数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                String _macAddress = mUser.getMacAddress();
                //连接某只手表mac
                JSONObject objectMac = device.scanDevice(Global.TYPE_DEVICE_Wristband, _macAddress);
                Log.i(CommonConstants.LOGCAT_TAG_NAME+"_reconnect", "objectMac，Result = " + objectMac);
                if (objectMac==null){
                    //同步失败，未连接手表
                    CommonUtil.sendErrorMessage("同步失败，请重试",handler);
                }else{
                    syncWatchData(_macAddress);//同步数据
                }

            }
        }).start();
    }


    /**
     * 同步数据
     */
    private void syncWatchData(final String macAddress) {
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

                    JSONObject datetimeResult = device.setDateTime(Calendar.getInstance());	// 设置手环的日期和时间
                    Log.d("sync", "datetimeResult = " + datetimeResult);		// 如果为result = 0，则成功，否则失败

                    JSONObject batteryResult = device.getBattery();		// 获取手环的电量
                    Log.d("sync", "batteryResult= " + batteryResult);			// result 里面的数值就是电量
                    mBattery = JsonUtils.getInt(batteryResult,"result",-1);
                    if (mBattery!=-1){
                        mUser.setBattery(mBattery);
                        CommonUtil.saveUserInfo(mUser,getActivity());//覆盖用户电量
                    }


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

                    if (allDayData!=null&&allDayData.size()>0){
                        //有数据
//                        //上传数据到服务器-上传个人目标
//                        String _setResult = mUserBusiness.setSportsTarget(mUser);
//                        Log.d(CommonConstants.LOGCAT_TAG_NAME+"_setTarget_result",_setResult);
//                        JSONObject _json_result = new JSONObject(_setResult);
//                        boolean Success = JsonUtils.getBoolean(_json_result,"Success");
//                        if (Success){
//                            Log.d(CommonConstants.LOGCAT_TAG_NAME+"_Success","true");
//                        }else{
//                            Log.d(CommonConstants.LOGCAT_TAG_NAME+"_Success","false");
//                        }
                        Log.d(CommonConstants.LOGCAT_TAG_NAME+"_Success",""+allDayData.size());
                    }

                    // 设置手环同步完成
                    device.setFinishSync();
                    // 同步完成
                    handler.sendEmptyMessage(CommonConstants.FLAG_HOME_SYNC_SUCCESS);

                } catch (NoConnectException e) {
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage("同步失败，请重试",handler);
                } catch (JSONException e) {
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage("同步失败，请重试",handler);
                } catch (Exception e){
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage("同步失败，请重试",handler);
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
     * 断开连接设备
     */
    private void disConnectWatch() {
        try {
            boolean isDestroy = false;
            JSONObject object = device.disconnectDevice(isDestroy);		// 断开设备
            Log.d("disconnect", "disconncet = " + object);		// 如果为result = 0，则成功，否则失败
        } catch (NoConnectException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //处理消息队列
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int flag = msg.what;
            switch (flag) {
                case 0:
                    String errorMsg = (String)msg.getData().getSerializable("ErrorMsg");
                    try {
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CommonConstants.FLAG_HOME_SYNC_SUCCESS:
                    updateUIAfterSync();//更新界面信息
                    break;
                default:
                    break;
            }
        }


    };

    /**
     * 更新UI数据-当天总步数、并计算出进度百分比、热量、里程数、当天每个小时的步数
     */
    private void updateUIAfterSync() {

         Collections.sort(allDayData, new Comparator<OneDayData>() {
            @Override
            public int compare(OneDayData lhs, OneDayData rhs) {
                return new Integer(lhs.getTime()).compareTo(new Integer(rhs.getTime()));
            }
        });

        //初始化日期
        Date _today = new Date();
        SimpleDateFormat _sdf = new SimpleDateFormat("yyyy-MM-dd");
        String _TodayStr = _sdf.format(_today);

        int _sum_step = 0;//总步数
        List<OneDayData> _todayData = new ArrayList<OneDayData>();
        for (int i = 0; i < allDayData.size(); i++) {
            Log.d(CommonConstants.LOGCAT_TAG_NAME+"_oneDay",allDayData.get(i).getDate()+","+allDayData.get(i).getTime()+","+allDayData.get(i).getStep()+","+allDayData.get(i).getType());
            try{
                Date _the_date = _sdf.parse(allDayData.get(i).getDate());
                String _the_date_str = _sdf.format(_the_date);
                //当天的数据叠加
                if (_TodayStr.equals(_the_date_str)){
                    _sum_step += allDayData.get(i).getStep();
                    _todayData.add(allDayData.get(i));
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        int _height = mUser.getHeight()==0?175:mUser.getHeight();
        int _weight = mUser.getWeight()==0?70:mUser.getWeight();
        Log.d("jlj_height_and_weight",_height+","+_weight);
        double _distance = ((0.45*_height*_sum_step)/100)/1000;//里程数
//        double _calories = 0.53*_height+0.58*_weight+0.04*_sum_step-135;//热量
        double _calories = _weight*_distance*1.036;//热量
        String percent = CommonUtil.calcPercent(_sum_step,mUser.getSportsTarget());//百分比
        Log.d(CommonConstants.LOGCAT_TAG_NAME+"_result","总步数："+_sum_step+",百分比："+percent+",热量："+_calories+",里程数："+_distance);
        //更新界面
        mTvSumSteps.setText(_sum_step+"步");
        mTvSumCarolies.setText(CommonUtil.formatData(Double.valueOf(_calories),2)+"千卡");
        mTvSumDistance.setText(CommonUtil.formatData(Double.valueOf(_distance),2)+"公里");
        mCirclePercentView.setPercent(Integer.parseInt(percent) + 1);

        lineView(_todayData);//更新折线图
        Toast.makeText(getActivity(),"同步已完成", Toast.LENGTH_SHORT).show();
    }



    //折线图
    public void lineView(List pTodayData){
        //同样是需要数据dataset和视图渲染器renderer
        XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
        XYSeries  series = new XYSeries("步数");
//        //先画所有的数据为0
//        for (int i = 0; i <24 ; i++) {
//            series.add(i, MathHelper.NULL_VALUE);
//        }
        //再画每个时刻的步数
        if (pTodayData!=null&&pTodayData.size()>0){
            for (int i = 0; i <pTodayData.size() ; i++) {
                OneDayData _one_day_data = (OneDayData) pTodayData.get(i);
                int hour = _one_day_data.getTime();
                //小时，步数
                series.add(hour,_one_day_data.getStep());

            }
        }


        mDataset.addSeries(series);
//	        XYSeries  seriesTwo = new XYSeries("第二条线");
//	        seriesTwo.add(1, 4);
//	        seriesTwo.add(2, 6);
//	        seriesTwo.add(3, 3);
//	        seriesTwo.add(4, 7);
//	        mDataset.addSeries(seriesTwo);


        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        //设置图表的X轴的当前方向
        mRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
        mRenderer.setXTitle("小时");//设置为X轴的标题
        mRenderer.setYTitle("步数");//设置y轴的标题
        mRenderer.setAxisTitleTextSize(20);//设置轴标题文本大小
//			mRenderer.setChartTitle("今日数据");//设置图表标题
//			mRenderer.setChartTitleTextSize(30);//设置图表标题文字的大小
        mRenderer.setLabelsTextSize(18);//设置标签的文字大小
        mRenderer.setLegendTextSize(20);//设置图例文本大小
        mRenderer.setPointSize(10f);//设置点的大小
        mRenderer.setYAxisMin(0);//设置y轴最小值是0
        mRenderer.setYAxisMax(600);//y轴最大值600
        mRenderer.setYLabels(3);//设置Y轴刻度个数（貌似不太准确）
        mRenderer.setXAxisMax(23);
        mRenderer.setShowGrid(true);//显示网格


        //将x标签栏目显示如：1,2,3,4替换为显示1月，2月，3月，4月
        for (int i = 0; i < 24; i++) {
            mRenderer.addXTextLabel(i, String.valueOf(i));
        }

        mRenderer.setXLabels(0);//设置只显示如1月，2月等替换后的东西，不显示1,2,3等
        mRenderer.setMargins(new int[] { 20, 30, 15, 20 });//设置视图位置

        XYSeriesRenderer r = new XYSeriesRenderer();//(类似于一条线对象)
        r.setColor(Color.RED);//设置颜色
        r.setPointStyle(PointStyle.CIRCLE);//设置点的样式
        r.setFillPoints(true);//填充点（显示的点是空心还是实心）
        r.setDisplayChartValues(true);//将点的值显示出来
        r.setChartValuesSpacing(20);//显示的点的值与图的距离
        r.setChartValuesTextSize(25);//点的值的文字大小

        //  r.setFillBelowLine(true);//是否填充折线图的下方
        //  r.setFillBelowLineColor(Color.GREEN);//填充的颜色，如果不设置就默认与线的颜色一致
        r.setLineWidth(3);//设置线宽
        mRenderer.addSeriesRenderer(r);

        mRenderer.setApplyBackgroundColor(true);//必须设置为true，颜色值才生效
        mRenderer.setBackgroundColor(Color.WHITE);//设置表格背景色
        mRenderer.setMarginsColor(Color.WHITE);//设置周边背景色
        mRenderer.setAxesColor(getResources().getColor(R.color.common_content_gray_text_font_color));

        mRenderer.setLegendHeight(60);//设置图例高度
	        mRenderer.setPanEnabled(false);//设置xy轴是否可以拖动
        mRenderer.setZoomEnabled(true);

//	        XYSeriesRenderer rTwo = new XYSeriesRenderer();//(类似于一条线对象)
//	        rTwo.setColor(Color.GRAY);//设置颜色
//	        rTwo.setPointStyle(PointStyle.CIRCLE);//设置点的样式
//	        rTwo.setFillPoints(true);//填充点（显示的点是空心还是实心）
//	        rTwo.setDisplayChartValues(true);//将点的值显示出来
//	        rTwo.setChartValuesSpacing(10);//显示的点的值与图的距离
//	        rTwo.setChartValuesTextSize(25);//点的值的文字大小
//
//	      //  rTwo.setFillBelowLine(true);//是否填充折线图的下方
//	      //  rTwo.setFillBelowLineColor(Color.GREEN);//填充的颜色，如果不设置就默认与线的颜色一致
//	        rTwo.setLineWidth(3);//设置线宽
//
//	        mRenderer.addSeriesRenderer(rTwo);



        GraphicalView view = ChartFactory.getLineChartView(getActivity(), mDataset, mRenderer);
        view.setBackgroundColor(Color.WHITE);
        view.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 500));


//	        setContentView(view);
        mLinearChart.removeAllViews();
        mLinearChart.addView(view);
    }
}
