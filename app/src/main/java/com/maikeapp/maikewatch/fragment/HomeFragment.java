package com.maikeapp.maikewatch.fragment;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzgamut.sdk.global.Global;
import com.gzgamut.sdk.helper.NoConnectException;
import com.gzgamut.sdk.model.Maike;
import com.handmark.pulltorefresh.library.BuildConfig;
import com.maikeapp.maikewatch.DBOpenHelper.DBDao;
import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.activity.HistoryDataActivity;
import com.maikeapp.maikewatch.activity.MainActivity;
import com.maikeapp.maikewatch.bean.OneDayData;
import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.business.IUserBusiness;
import com.maikeapp.maikewatch.business.imp.UserBusinessImp;
import com.maikeapp.maikewatch.config.CommonConstants;
import com.maikeapp.maikewatch.config.MyApplication;
import com.maikeapp.maikewatch.exception.ServiceException;
import com.maikeapp.maikewatch.util.CommonUtil;
import com.maikeapp.maikewatch.util.JsonUtils;
import com.maikeapp.maikewatch.util.ScreenShotUtil;
import com.maikeapp.maikewatch.util.ToastUtil;
import com.maikeapp.maikewatch.view.CirclePercentView;
import com.maikeapp.maikewatch.view.LineChartView;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
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
    private static final int SCREEN_SHOT_SUCCESS = 150;
    private static final int UPLOAD_SUCCESS = 151;
    private static final int HIS_DATA_SUCCESS = 152;
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
    private static ProgressDialog mProgressDialog = null;
    private User mUser;//用户信息
    //sdk
    private Maike device = null;

    //同步时获取每天的数据
    private List<OneDayData> allDayData;
    //一天的数据
    private List<OneDayData> m_day_datas;

    private int mBattery;//电量数值

    private UMSocialService mController;
    //剪切图片的存放的路径
    private String mPicPath = Environment.getExternalStorageDirectory().getPath() + "/tempFile.jpg";
    /**
     * 是否正在同步
     */
    private boolean running;
    private DBDao mDbDao;
    private int sum_step;
    private ArrayList<OneDayData> todayOnDayDays;
    private boolean isRunning = false;
    private boolean isUpdataUI = false;
    private String mPickTime;
    private boolean isNetWorkAvilable;
    private ImageView mLeftDay;
    private ImageView mRightDay;
    private int mCount = 0;
    private int mCount1 = 0;
    private boolean isShow = true;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNetWorkAvilable = CommonUtil.isnetWorkAvilable(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        findView(view);
        mDbDao = new DBDao(getActivity());
        isShow = true;
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isShow = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        //再次初始化界面数据
        mCount = 0;
        initData();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    void findView(View view) {
        initView(view);
        setListener();
    }

    private void initView(View view) {
        mIvHistoryData = (ImageView) view.findViewById(R.id.iv_home_history_data);
        mIvShare = (ImageView) view.findViewById(R.id.iv_home_share);

        mLeftDay = (ImageView) view.findViewById(R.id.iv_left_day);
        mRightDay = (ImageView) view.findViewById(R.id.iv_right_day);

        mTvDate = (TextView) view.findViewById(R.id.tv_home_one_day_date);
        mCirclePercentView = (CirclePercentView) view.findViewById(R.id.circleView);

        mTvSportsTarget = (TextView) view.findViewById(R.id.tv_home_one_day_sports_target);
        mTvSumSteps = (TextView) view.findViewById(R.id.tv_home_one_day_sum_steps);
        mTvSumCarolies = (TextView) view.findViewById(R.id.tv_home_one_day_sum_calories);
        mTvSumDistance = (TextView) view.findViewById(R.id.tv_home_one_day_sum_distance);

        mLinearChart = (LinearLayout) view.findViewById(R.id.line_other_chart);
    }

    private void initData() {
        //初始化日期
        Date _today = new Date();
        setTodayDate(_today);
        //获取用户信息
        mUser = CommonUtil.getUserInfo(getActivity());
        if (mUser != null) {
            mTvSportsTarget.setText("目标:" + mUser.getSportsTarget());

            if (mUser.isBindWatch()) {

                //从服务端初始化当日同步前的数据(可以从数据库调用)
                Date myDate = new Date();
                int thisYear = myDate.getYear() + 1900;//thisYear = 2003
                int thisMonth = myDate.getMonth() + 1;//thisMonth = 5
                int thisDate = myDate.getDate();//thisDate = 30
                String _CurrentTime = String.valueOf(thisYear) + "-" + String.valueOf(thisMonth) + "-" + String.valueOf(thisDate);
                Log.d("thisDate的数据", String.valueOf(thisYear) + "-" + thisMonth + "-" + thisDate);

               /*测试数据*/
/////////////////////////////////////////////////////////////////////////////////////////////////////
//                mDbDao.addHourStep(18,3000,2);
//                mDbDao.addData("2016-5-19", 20, 2000, 2000, 25.5f, 123.3f, 1);
//                mDbDao.addData("2016-5-21", 20, 2000, 2000, 25.5f, 123.3f, 1);
//                mDbDao.addData("2016-5-20", 20, 2000, 2000, 25.5f, 123.3f, 1);
//                mDbDao.addData("2016-5-02", 20, 2000, 2000, 25.5f, 123.3f, 1);
//                mDbDao.addData("2016-5-11", 20, 2000, 2000, 25.5f, 123.3f, 1);
//                mDbDao.addData("2016-5-09", 20, 2000, 2000, 25.5f, 123.3f, 1);
//                mDbDao.addData("2016-5-07", 20, 2000, 2000, 25.5f, 123.3f, 1);
/////////////////////////////////////////////////////////////////////////////////////////////////////
//                if (!isRunning){
//                    todayOnDayDays = mDbDao.findTodayHourStep2(mUser.getLoginName(), _CurrentTime);
//                }
//                todayOnDayDays = mDbDao.findTodayHourStep2(mUser.getLoginName(), _CurrentTime);
//                showUI(todayOnDayDays);
                //显示本地数据之后,同步手表数据

                if (!isRunning) {//不在同步的时候.查找数据显示界面
                    todayOnDayDays = mDbDao.findTodayHourStep2(mUser.getLoginName(), _CurrentTime);
                    showUI(todayOnDayDays);//只查找一次,存在内存中
                    MainActivity mainUI = (MainActivity) getActivity();
                    boolean sync = mainUI.getSync();
                    if (!sync) {//保证每次进入APP只同步一次
                        isRunning = true;//同步数据刷新界面开始运行
                        mainUI.setSync(true);
                        Log.d("在更新了", "在更新了" + mainUI.getSync() + "");
                        syncWatchData();
                    }
                } else {//在同步的时候,只显示界面,不做任何操作
                    showUI(todayOnDayDays);//不在同步的时候,只显示界面
                }
                Log.d("_todayOnDayDays的数据", "_todayOnDayDays:" + todayOnDayDays);
            } else {
                ToastUtil.showTipShort(getActivity(), "请先绑定手表");
                //显示折线图
                LineChartView _line_chart_view = new LineChartView(getActivity());
                _line_chart_view.setmListDatas(null);
                mLinearChart.removeAllViews();
                mLinearChart.addView(_line_chart_view);
            }
        } else {
            mTvSportsTarget.setText("");
            mTvSumSteps.setText("0步");
            mTvSumCarolies.setText("0千卡");
            mTvSumDistance.setText("0公里");
            mCirclePercentView.setPercent(0);
            //显示折线图
            LineChartView _line_chart_view = new LineChartView(getActivity());
            _line_chart_view.setmListDatas(null);
            mLinearChart.removeAllViews();
            mLinearChart.addView(_line_chart_view);
        }
        //社会化分享
        socialShare();
    }


    /**
     * 设置今日日期
     *
     * @param _today
     */
    private void setTodayDate(Date _today) {
        SimpleDateFormat _sdf = new SimpleDateFormat("MM月dd日");
        String _TodayStr = _sdf.format(_today);
        mTvDate.setText(_TodayStr);
    }

    /**
     * 从网络获取某个日期的数据
     */
    private void getOnedayDataFromNetWork(final String _day_time) {
        Log.d("_day_timed的格式", _day_time);

        //弹出加载进度条
        mProgressDialog = ProgressDialog.show(getActivity(), null, "正在获取数据中...", true, true);
        //开启副线程-从网络查询某个日期的数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String _one_day_data_result = mUserBusiness.querySportsDataByDay(mUser, _day_time);
                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_oneday_result", _one_day_data_result);

                    JSONObject _json_obj_result = new JSONObject(_one_day_data_result);
                    boolean _Success = JsonUtils.getBoolean(_json_obj_result, "Success");
                    if (_Success) {
                        String _json_datas = JsonUtils.getString(_json_obj_result, "Datas");
                        m_day_datas = new Gson().fromJson(_json_datas, new TypeToken<List<OneDayData>>() {
                        }.getType());
                        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_day_datas", m_day_datas.toString());
                        // 同步完成,更新获取的新界面显示的数据
                        //
                        handler.sendEmptyMessage(CommonConstants.FLAG_HOME_GET_ONE_DAY_DATA_SUCCESS);

                    } else {
                        String _Message = JsonUtils.getString(_json_obj_result, "Message");
                        CommonUtil.sendErrorMessage(_Message, handler);
                    }
                } catch (ServiceException e) {
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage(e.getMessage(), handler);
                } catch (Exception e) {
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage(CommonConstants.MSG_GET_ERROR, handler);
                }
            }
        }).start();
    }


    private void setListener() {
        /**
         * 点击了百分比进度条，同步数据，并刷新界面
         */
        mCirclePercentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUser == null) {
                    ToastUtil.showTipShort(getActivity(), "请先登录");
                    return;
                }
                if (!mUser.isBindWatch()) {
                    ToastUtil.showTipShort(getActivity(), "请先绑定手表");
                    return;
                }

                //如果后台正在同步数据,直接显示进度条
                if (isRunning) {
                    mProgressDialog = ProgressDialog.show(getActivity(), null, "正在同步中，请稍后...", true, true);
                    return;
                }

                //弹出加载进度条
                mProgressDialog = ProgressDialog.show(getActivity(), null, "正在同步中，请稍后...", true, true);
                //初始化日期
                Date _today = new Date();
                if (BuildConfig.DEBUG) Log.d("HomeFragment", "_today:" + _today);
                setTodayDate(_today);

                //同步手表数据
                isRunning = true;
                syncWatchData();
            }
        });
        /**
         * 查看历史数据
         */
        mIvHistoryData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUser == null) {
                    ToastUtil.showTipShort(getActivity(), "请先登录");
                    return;
                }

                if (!mUser.isBindWatch()) {
                    ToastUtil.showTipShort(getActivity(), "请先绑定手表");
                    return;
                }
                // 用子线程实时监测isRuning
                mProgressDialog = ProgressDialog.show(getActivity(), null, "正在加载中，请稍后...", true, true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        while (isRunning) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(HIS_DATA_SUCCESS);
                        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_onclick_history", "click_history");
                    }
                }).start();
            }

        });

        /**
         * 点击日期切换日期
         */
        mTvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUser == null) {
                    ToastUtil.showTipShort(getActivity(), "请先登录");
                    return;
                }
                if (!mUser.isBindWatch()) {
                    ToastUtil.showTipShort(getActivity(), "请先绑定手表");
                    return;
                }
                showDatePickerDialogToQueryOneDayData();
            }
        });
        /**
         * 分享今日数据
         */
        mIvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //子线程剪切图片
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = Message.obtain();
                        File file = new File(mPicPath);
                        ScreenShotUtil.shoot(getActivity(), file);
                        msg.what = SCREEN_SHOT_SUCCESS;
                        handler.sendMessage(msg);
                    }
                }).start();
                mProgressDialog = ProgressDialog.show(getActivity(), null, "正在玩命截屏中...", true, true);

            }
        });

        //查看前一天的数据
        mLeftDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _CurrentDate = mTvDate.getText().toString();
                SimpleDateFormat _SDF = new SimpleDateFormat("MM月dd日");
                try {
                    Date date = new Date();//获得当前是哪一年
                    Date parseDate = _SDF.parse(_CurrentDate);
                    int thisYear = datePlus(date, -1).getYear() + 1900;//thisYear = 2003
                    int thisMonth = datePlus(parseDate, -1).getMonth() + 1;//thisMonth = 5
                    int thisDate = datePlus(parseDate, -1).getDate();//thisDate = 30

                    String _CurrentTime = String.valueOf(thisYear) + "-" + String.valueOf(thisMonth) + "-" + String.valueOf(thisDate);
                    Log.d("_CurrentTime减掉的数据", _CurrentTime);
                    oneDayShow(_CurrentTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        //查看后一天的数据
        mRightDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    String _CurrentDate = mTvDate.getText().toString();
                    SimpleDateFormat _SDF = new SimpleDateFormat("MM月dd日");
                    try {
                        Date date = new Date();//获得当前是哪一年
                        Date parseDate = _SDF.parse(_CurrentDate);
                        if (date.getDate() <= parseDate.getDate()) {
                            Toast.makeText(getContext(), "超出当前日期，没有数据", Toast.LENGTH_SHORT).show();
                            return;
                        }

//                    Date _date1 = datePlus(date, mCount);
//                    int thisYear = _date1.getYear() + 1900;//thisYear = 2003
//                    int thisMonth = _date1.getMonth() + 1;//thisMonth = 5
//                    int thisDate = _date1.getDate();//thisDate = 30

                        int thisYear = datePlus(date, +1).getYear() + 1900;//thisYear = 2003
                        int thisMonth = datePlus(parseDate, +1).getMonth() + 1;//thisMonth = 5
                        int thisDate = datePlus(parseDate, +1).getDate();//thisDate = 30

                        String _CurrentTime = String.valueOf(thisYear) + "-" + String.valueOf(thisMonth) + "-" + String.valueOf(thisDate);
                        Log.d("_CurrentTime减掉的数据", _CurrentTime);
                        oneDayShow(_CurrentTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void oneDayShow(String time) throws ParseException {


        if (!isRunning) {//防止并行更改数据库发生崩溃
            int id = 0;
            int _userid = mDbDao.findUser(mUser.getLoginName());
            Log.d("_one_datetime的数据", time);
            int dataid = mDbDao.findData(_userid, time);
            for (int i = 0; i < 23; i++) {//判断数据库中是否存在当天的数据
                int _hourStepID = mDbDao.findHourStep(i, dataid);
                if (_hourStepID != 0) {
                    id = _hourStepID;
                }
            }

            if (id != 0) {//本地有数据从本地查找
                todayOnDayDays = mDbDao.findTodayHourStep2(mUser.getLoginName(), time);
                showUI(todayOnDayDays);

                //解析时间并显示
                SimpleDateFormat _SDF2 = new SimpleDateFormat("yyyy-MM-dd");
                Date _parse = _SDF2.parse(time);
                int _month = _parse.getMonth() + 1;
                int _date1 = _parse.getDate();
                mPickTime = _month + "月" + _date1 + "日";

                mTvDate.setText(mPickTime);
            } else {

                //解析时间并显示
                SimpleDateFormat _SDF2 = new SimpleDateFormat("yyyy-MM-dd");
                Date _parse = _SDF2.parse(time);
                int _month = _parse.getMonth() + 1;
                int _date1 = _parse.getDate();
                mPickTime = _month + "月" + _date1 + "日";

                if (CommonUtil.isnetWorkAvilable(getContext())) {//判断当前有没有网络,
                    getOnedayDataFromNetWork(time);
                } else {
                    Toast.makeText(getContext(), "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getContext(), "正在同步中请稍后...", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 时间计算公式
     *
     * @param base 基础日期
     * @param days 增加天数(减天数则用负数)
     */
    public Date datePlus(Date base, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(base);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    /**
     * 将日期转换成指定格式的字符串。
     *
     * @param date   日期
     * @param format 输出格式
     * @return 日期字符串
     */
    public String convDate2Str(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat _sdf = new SimpleDateFormat(format);
        String _TodayStr = _sdf.format(date);
        return _TodayStr;
    }


    /**
     * 查询某个日期的所有数据
     */
    private void showDatePickerDialogToQueryOneDayData() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog _data_picker_dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                mTvDate.setText((monthOfYear + 1) + "月" + dayOfMonth + "日");
                mPickTime = (monthOfYear + 1) + "月" + dayOfMonth + "日";
                String _one_datetime = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                //判断选择的日期是否超出当前日期
                SimpleDateFormat _SDF = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                try {
                    Date parseTime = _SDF.parse(_one_datetime);
                    if (parseTime.compareTo(date) > 0) {
                        Toast.makeText(getContext(), "超出当前日期，没有数据", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                if (!isRunning) {//防止并行更改数据库发生崩溃
                    //先从本地获取数据显示界面没有则从网络获取
                    int id = 0;
                    int _userid = mDbDao.findUser(mUser.getLoginName());
                    Log.d("_one_datetime的数据", _one_datetime);
                    //dataid有很多的数据,在hourStep中是没有的.以hourStep表中的数据为主
                    int dataid = mDbDao.findData(_userid, _one_datetime);//需要修改的地方
                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    for (int i = 0; i < 23; i++) {
                        int _hourStepID = mDbDao.findHourStep(i, dataid);
                        if (_hourStepID != 0) {
                            id = _hourStepID;
                        }
                    }

                    if (id != 0) {//本地有数据从本地查找
                        todayOnDayDays = mDbDao.findTodayHourStep2(mUser.getLoginName(), _one_datetime);
                        showUI(todayOnDayDays);
                        mTvDate.setText(mPickTime);
                    } else {
                        if (CommonUtil.isnetWorkAvilable(getContext())) {//判断当前有没有网络,
                            getOnedayDataFromNetWork(_one_datetime);
                        } else {
                            Toast.makeText(getContext(), "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    Toast.makeText(getContext(), "正在同步中请稍后...", Toast.LENGTH_SHORT).show();
                }


            }
        }, year, monthOfYear, dayOfMonth);
        _data_picker_dialog.show();
    }


    /**
     * 同步手表数据（获取手表端数据，并上传到服务端，并在当前界面展示）
     */
    private void syncWatchData() {

        //初始化device
        try {
            device = MyApplication.newMaikeInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }

        //开启副线程-同步数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                String _macAddress = mUser.getMacAddress();
                //检查是否通过
                boolean Success = true;
                try {
                    String _check_mac_Result = mUserBusiness.checkMacAddress(_macAddress);
                    Log.d(CommonConstants.LOGCAT_TAG_NAME, "_check_mac_Result is " + _check_mac_Result);
                    JSONObject _json_result = new JSONObject(_check_mac_Result);
                    try {
                        Success = _json_result.getBoolean("Success");
                    } catch (Exception e) {
                        isRunning = false;
                        e.printStackTrace();
                    }

                    if (!Success) {
                        // 数据异常
                        String errorMsg = _json_result.getString("Message");
                        CommonUtil.sendErrorMessage(errorMsg, handler);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    isRunning = false;
                }
                //通过
                if (Success) {
                    //同步数据-并上传信息到服务器
                    syncWatchData(_macAddress);
                }


            }
        }).start();
    }


    /**
     * 同步数据
     */
    private void syncWatchData(final String macAddress) {
        running = true;//正在运行
        int _seconds = 500;
        //循环5次连接，若连接不成功给予用户提醒
        for (int k = 0; k < 5; k++) {
            try {
                //暂时先沉睡2s
                try {
                    Thread.sleep(_seconds);
                    _seconds += 500;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    isRunning = false;
                }
                Log.i(CommonConstants.LOGCAT_TAG_NAME + "_sync_connect_no", "--------no = " + k);
                //连接手表mac
                JSONObject objectMac = device.scanDevice(Global.TYPE_DEVICE_Wristband, macAddress);
                Log.d(CommonConstants.LOGCAT_TAG_NAME + "_sync_conn_isSuc", "objectMac，Result = " + objectMac);
                if (objectMac == null) {
                    //断开设备
                    JSONObject object = device.disconnectDevice(false);        // 断开设备
                    Log.i(CommonConstants.LOGCAT_TAG_NAME + "_sync_disconnect", "disconncet = " + object);        // 如果为result = 0，则成功，否则失败
                    continue;
                }

                //再沉睡0.5s
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    isRunning = false;
                }

                //同步数据开始》》
                JSONObject boundInfoResult = device.getBoundInfo();    // 获取手环的绑定信息
                Log.d("sync", "boundInfoResult = " + boundInfoResult);        // 如果result = 0，则未绑定，如果result = 1，则已绑定

                JSONObject datetimeResult = device.setDateTime(Calendar.getInstance());    // 设置手环的日期和时间
                Log.d("sync", "datetimeResult = " + datetimeResult);        // 如果为result = 0，则成功，否则失败

                JSONObject versionResult = this.device.getVersion();        // 获取手环的固件版本号
                Log.i("sync", "versionResult = " + versionResult);            // result返回是一串字符，即版本号
                String _version_str = JsonUtils.getString(versionResult, "result");
                if (_version_str != null && !_version_str.equals("")) {
                    mUser.setWatchVersion(_version_str);
                }

                JSONObject batteryResult = device.getBattery();        // 获取手环的电量
                Log.d("sync", "batteryResult= " + batteryResult);            // result 里面的数值就是电量
                mBattery = JsonUtils.getInt(batteryResult, "result", -1);
                if (mBattery != -1) {
                    mUser.setBattery(mBattery);
                    CommonUtil.saveUserInfo(mUser, getActivity());//覆盖用户电量/用户固件版本号
                }


                int count = 0;
                int sn = 0;

                if (running) {
                    // 获取手环存储数据的总天数和当天数据所在的位置
                    JSONObject objectCount = device.getActivityCount();
                    count = objectCount.getInt("count");    // 总天数
                    if (count > 0) {
                        allDayData = new ArrayList<OneDayData>();
                    } else {
                        //无数据
                        Log.d("sync", "no data");
                        return;
                    }

                    sn = objectCount.getInt("sn");        // 当天数据所在的位置
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

                // 设置手环同步完成
                device.setFinishSync();

                //同步数据结束》》将数据加入数据库
                Log.d("allDayData的数据", "allDayData:" + allDayData);

                mDbDao.addallDayDatas(allDayData, mUser.getMobile(), mUser);

                // 同步完成
                if (allDayData != null && allDayData.size() > 0) {
                    handler.sendEmptyMessage(CommonConstants.FLAG_HOME_SYNC_SUCCESS);//allData里面有数据界面再更新
                }
                //每次循环连接都断开设备
                JSONObject object = device.disconnectDevice(false);        // 断开设备
                Log.d(CommonConstants.LOGCAT_TAG_NAME + "_sync_disconnect", "disconncet = " + object);        // 如果为result = 0，则成功，否则失败
                running = false;//提前结束运行，不提示错误信息
                break;//结束循环

            } catch (NoConnectException e) {
                e.printStackTrace();
                //发现连接异常，结束本次循环，进入下一次连接
                continue;
            } catch (JSONException e) {
                e.printStackTrace();
                CommonUtil.sendErrorMessage("同步失败，数据异常", handler);
                isRunning = false;
            } catch (Exception e) {
                e.printStackTrace();
                CommonUtil.sendErrorMessage("同步失败", handler);
                isRunning = false;
            }
        }
        //循环5次依然没连上，提示错误信息，并running未false
        if (running) {
            //解绑失败
            String errorMsg = "同步失败，请重试";
            CommonUtil.sendErrorMessage(errorMsg, handler);
            running = false;//结束运行

        }


    }

    /**
     * 解析步数
     */
    private void parserActivityValue(JSONObject object) {
        if (running) {
            try {
                if (object != null) {
                    JSONArray jsonArray = object.getJSONArray("ActivityData");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.opt(i);

                        String date = jsonObject.getString("year") + "-" + jsonObject.getString("month") + "-" + jsonObject.getString("day");// 日期
                        int time = Integer.parseInt(jsonObject.getString("hour"));//小时
                        String value = jsonObject.getString("value");// 步数或睡眠分数
                        int step = Integer.parseInt(value);
                        String type = jsonObject.getString("type");// 类型，如果是"step"，则表明value为步数，如果是"sleep"，则表明value是睡眠分数

                        //添加到allDayData中
                        OneDayData _one_day_data = new OneDayData(date, time, step, type);
                        _one_day_data.setLoginName(mUser.getLoginName());
                        _one_day_data.setMacAddress(mUser.getMacAddress());
                        _one_day_data.setCompletedSteps(0);//服务端要求
                        allDayData.add(_one_day_data);

//                        Log.i("sync", "data = " + object);
//                        Log.i("sync", date + ", " + time + ", " + value + ", " + type);
                    }
                    Log.d("allDayData的数据", allDayData.toString() + "YZP");
                }


            } catch (JSONException e) {
                e.printStackTrace();
                isRunning = false;
            }
        }
    }


    //处理消息队列
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            int flag = msg.what;
            switch (flag) {
                case 0:
                    String errorMsg = (String) msg.getData().getSerializable("ErrorMsg");
                    try {
                        isRunning = false;
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CommonConstants.FLAG_HOME_SYNC_SUCCESS:
                    isRunning = false;
                    if (isShow) {//可以显示则显示
                        updateUIAfterSync();//更新界面信息
                    }
                    break;
                case CommonConstants.FLAG_HOME_GET_ONE_DAY_DATA_SUCCESS:
                    if (isAdded()) {
                        //Return true if the fragment is currently added to its activity.
                        // 刷新日期
                        mTvDate.setText(mPickTime);
                        updateUIOfOneDayData();//更新界面上一天的数据
                    }
                    break;
                case CommonConstants.FLAG_HOME_DATA_EXCEPTION_SUCCESS:
                    disableApp();//app不可用
                    break;
                case UPLOAD_SUCCESS:
                    if (isShow) {//可以显示则显示
                        Toast.makeText(getActivity(), "同步完成", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case SCREEN_SHOT_SUCCESS:
                    mController.openShare(getActivity(), false);
                    break;
                case HIS_DATA_SUCCESS:
                    if (isShow) {
                        Intent _intent = new Intent(getActivity(), HistoryDataActivity.class);
                        getActivity().startActivity(_intent);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * app不可用
     */
    private void disableApp() {
        ToastUtil.showTipShort(getActivity(), "数据异常");
    }

    /**
     * 更新UI-一天的数据```````````````````````````````````````````````````````````````````````````````````````````````````````````
     */
    private void updateUIOfOneDayData() {
        isUpdataUI = true;//正在跟新界面

        //总步数
        int steps = 0;
        for (int i = 0; i < m_day_datas.size(); i++) {
            steps += m_day_datas.get(i).getSteps();
//            Log.d("sum_step的数据", "sum_step:" + sum_step);
        }
        int _height = mUser.getHeight() == 0 ? 175 : mUser.getHeight();
        int _weight = mUser.getWeight() == 0 ? 70 : mUser.getWeight();
        Log.d("jlj_height_and_weight", _height + "," + _weight);
        double _distance = ((0.45 * _height * steps) / 100) / 1000;//里程数
//        double _calories = 0.53*_height+0.58*_weight+0.04*_sum_step-135;//热量
//        double _calories2 = _weight*_distance*1.036;//热量
        double _calories = steps * _weight * 0.0006564;//热量
        String percent = CommonUtil.calcPercent(steps, mUser.getSportsTarget() == 0 ? 2000 : mUser.getSportsTarget());//百分比(个人目标没有，默认取2000)
        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result", "总步数：" + steps + ",百分比：" + percent + ",热量：" + _calories + ",里程数：" + _distance);

        if (m_day_datas != null && m_day_datas.size() > 0) {

            //更新界面
            mTvSumSteps.setText(steps + "步");
            mTvSumCarolies.setText(CommonUtil.formatData(Double.valueOf(_calories), 2) + "千卡");
            mTvSumDistance.setText(CommonUtil.formatData(Double.valueOf(_distance), 2) + "公里");

            int _percent = Integer.parseInt(percent);
            if (_percent > 100) {
                _percent = 100;
            }
            mCirclePercentView.setPercent(_percent + 1);
//
            OneDayData _head_one_day_data = m_day_datas.get(0);
            mTvSportsTarget.setText("目标:" + (_head_one_day_data.getTargetSteps() == 0 ? mUser.getSportsTarget() : _head_one_day_data.getTargetSteps()));

            //显示折线图
            LineChartView _line_chart_view = new LineChartView(getActivity());
            _line_chart_view.setmListDatas(m_day_datas);
            mLinearChart.removeAllViews();
            mLinearChart.addView(_line_chart_view);
//            lineView(m_day_datas);
        } else {
            mCirclePercentView.setPercent(0 + 1);
            mTvSportsTarget.setText("目标:" + (mUser.getSportsTarget() == 0 ? 2000 : mUser.getSportsTarget()));
            mTvSumSteps.setText("0步");
            mTvSumCarolies.setText("0千卡");
            mTvSumDistance.setText("0公里");
//            lineView(null);
            //显示折线图
            LineChartView _line_chart_view = new LineChartView(getActivity());
            _line_chart_view.setmListDatas(null);
            mLinearChart.removeAllViews();
            mLinearChart.addView(_line_chart_view);
        }
        isUpdataUI = false;//跟新界面完成

    }

    /**
     * 更新UI数据-当天总步数、并计算出进度百分比、热量、里程数、当天每个小时的步数
     */
    private void updateUIAfterSync() {
        isUpdataUI = true;//正在更更新界面


//        ToastUtil.showTipLong(getActivity(), "正在同步中...");

        Collections.sort(allDayData, new Comparator<OneDayData>() {
            @Override
            public int compare(OneDayData lhs, OneDayData rhs) {
                String _datetime_1 = lhs.getSportsTime() + " " + lhs.getCompleteHour() + ":00:00";
                String _datetime_2 = rhs.getSportsTime() + " " + rhs.getCompleteHour() + ":00:00";
                SimpleDateFormat _sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date _date_1 = null;
                Date _date_2 = null;
                try {
                    _date_1 = _sdf.parse(_datetime_1);
                    _date_2 = _sdf.parse(_datetime_2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return _date_1.compareTo(_date_2);
            }
        });

        //初始化日期
        Date _today = new Date();
        SimpleDateFormat _sdf = new SimpleDateFormat("yyyy-MM-dd");
        String _TodayStr = _sdf.format(_today);

        int _sum_step = 0;//总步数
        List<OneDayData> _todayData = new ArrayList<OneDayData>();
        for (int i = 0; i < allDayData.size(); i++) {

            Log.d(CommonConstants.LOGCAT_TAG_NAME + "_oneDay", allDayData.get(i).getSportsTime() + "," + allDayData.get(i).getCompleteHour() + "," + allDayData.get(i).getSteps() + "," + allDayData.get(i).getType());
            try {
                Date _the_date = _sdf.parse(allDayData.get(i).getSportsTime());
                String _the_date_str = _sdf.format(_the_date);
                //当天的数据叠加
                if (_TodayStr.equals(_the_date_str)) {
                    _sum_step += allDayData.get(i).getSteps();
                    _todayData.add(allDayData.get(i));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        int _height = mUser.getHeight() == 0 ? 175 : mUser.getHeight();
        int _weight = mUser.getWeight() == 0 ? 70 : mUser.getWeight();
        Log.d("jlj_height_and_weight", _height + "," + _weight);
        double _distance = ((0.45 * _height * _sum_step) / 100) / 1000;//里程数
//        double _calories = 0.53*_height+0.58*_weight+0.04*_sum_step-135;//热量
//        double _calories2 = _weight*_distance*1.036;//热量
        double _calories = _sum_step * _weight * 0.0006564;//热量
        String percent = CommonUtil.calcPercent(_sum_step, mUser.getSportsTarget() == 0 ? 2000 : mUser.getSportsTarget());//百分比(个人目标没有，默认取2000)
        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_result", "总步数：" + _sum_step + ",百分比：" + percent + ",热量：" + _calories + ",里程数：" + _distance);
        //更新界面
        mTvSumSteps.setText(_sum_step + "步");
        mTvSumCarolies.setText(CommonUtil.formatData(Double.valueOf(_calories), 2) + "千卡");
        mTvSumDistance.setText(CommonUtil.formatData(Double.valueOf(_distance), 2) + "公里");
        mTvSportsTarget.setText("目标:" + (mUser.getSportsTarget() == 0 ? 2000 : mUser.getSportsTarget()));

        int _percent = Integer.parseInt(percent);
        if (_percent > 100) {
            _percent = 100;
        }
        mCirclePercentView.setPercent(_percent + 1);

        //显示折线图
        LineChartView _line_chart_view = new LineChartView(getContext());
        _line_chart_view.setmListDatas(_todayData);


        mLinearChart.removeAllViews();
        mLinearChart.addView(_line_chart_view);

        //上传当天以及最近7天的所有数据到服务端
        if (CommonUtil.isnetWorkAvilable(getContext())) {
            uploadAllDataToServer(CommonUtil.formatData(Double.valueOf(_calories), 2), CommonUtil.formatData(Double.valueOf(_distance), 2));
        } else {
            ToastUtil.showTipShort(getContext(), "同步已完成");
            isRunning = false;//没网的情况下到这里结束,
        }
    }

    /**
     * 传入一天的数据,计算并且显示界面
     *
     * @param oneDayDatas
     */
    private void showUI(ArrayList<OneDayData> oneDayDatas) {

        isUpdataUI = true;//正在界面完成
        //从新设置界面
        sum_step = 0;
        for (int i = 0; i < oneDayDatas.size(); i++) {
            sum_step += oneDayDatas.get(i).getSteps();
//            Log.d("sum_step的数据", "sum_step:" + sum_step);
        }
        int _height = mUser.getHeight() == 0 ? 175 : mUser.getHeight();
        int _weight = mUser.getWeight() == 0 ? 70 : mUser.getWeight();
        double _distance = ((0.45 * _height * sum_step) / 100) / 1000;//里程数
        double _calories = sum_step * _weight * 0.0006564;//热量
        String percent = CommonUtil.calcPercent(sum_step, mUser.getSportsTarget() == 0 ? 2000 : mUser.getSportsTarget());//百分比(个人目标没有，默认取2000)
        //更新界面
        mTvSumSteps.setText(sum_step + "步");

        mTvSumCarolies.setText(CommonUtil.formatData(Double.valueOf(_calories), 2) + "千卡");
        mTvSumDistance.setText(CommonUtil.formatData(Double.valueOf(_distance), 2) + "公里");
        mTvSportsTarget.setText("目标:" + (mUser.getSportsTarget() == 0 ? 2000 : mUser.getSportsTarget()));
        int _percent = Integer.parseInt(percent);//百分比
        if (_percent > 100) {
            _percent = 100;
        }
        mCirclePercentView.setPercent(_percent + 1);
        //显示折线图
        LineChartView _line_chart_view = new LineChartView(getContext());
        _line_chart_view.setmListDatas(oneDayDatas);
        mLinearChart.removeAllViews();
        mLinearChart.addView(_line_chart_view);
        isUpdataUI = false;//跟新界面完成
    }


    /**
     * 上传当天以及最近7天的所有数据到服务端
     *
     * @param calories 卡路里千卡
     * @param distance 公里数
     */
    private void uploadAllDataToServer(final String calories, final String distance) {
        //开启副线程-上传数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //上传个人目标、身高、体重
                    String _set_sports_target_Result = mUserBusiness.setSportsTarget(mUser);
                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_setTarget_result", _set_sports_target_Result);
                    JSONObject _json_result = new JSONObject(_set_sports_target_Result);
                    boolean Success = JsonUtils.getBoolean(_json_result, "Success");
                    if (Success) {
                        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_set_sports_target_r", "true");

                        //上传卡路里和公里数
                        String _sync_sports_data_today_result = mUserBusiness.syncSportsDataToday(mUser, calories, distance);
                        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_up_today_data1_r", _sync_sports_data_today_result);
                        JSONObject _json_upload_today_data_result = new JSONObject(_sync_sports_data_today_result);
                        boolean _upload_today_Success = JsonUtils.getBoolean(_json_upload_today_data_result, "Success");
                        if (_upload_today_Success) {
                            Log.d(CommonConstants.LOGCAT_TAG_NAME + "_set_today_data_r", "true");
                            //上传最近7天数据
                            if (allDayData != null && allDayData.size() > 0) {

                                String _upload_recent_week_data_result = mUserBusiness.uploadRecentWeekData(allDayData);
                                JSONObject _json_upload_recent_data_result = new JSONObject(_upload_recent_week_data_result);
                                boolean _upload_recent_data_Success = JsonUtils.getBoolean(_json_upload_recent_data_result, "Success");
                                if (_upload_recent_data_Success) {
                                    //数据上传成功之后更新数据库的状态信息
                                    for (int i = 0; i < allDayData.size(); i++) {
//                                        Log.d("时间数据", allDayData.get(i).getSportsTime());
                                        mDbDao.updataStatus(mUser, allDayData.get(i).getSportsTime());
                                    }
                                    //数据上传完成
                                    handler.sendEmptyMessage(UPLOAD_SUCCESS);
                                    Thread.sleep(50);
                                    isRunning = false;//结束线程运行
                                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_upload_recent_r", "true");
                                } else {
                                    isRunning = false;//结束线程运行
                                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_upload_recent_r", "false");
                                }

                            }

                        } else {
                            isRunning = false;//结束线程运行
                            Log.d(CommonConstants.LOGCAT_TAG_NAME + "_set_today_data_r", "false");
                        }
                    } else {
                        isRunning = false;//结束线程运行
                        Log.d(CommonConstants.LOGCAT_TAG_NAME + "_set_sports_target_r", "false");
                    }
                } catch (ServiceException e) {
                    isRunning = false;//结束线程运行
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage(e.getMessage(), handler);
                } catch (Exception e) {
                    isRunning = false;//结束线程运行
                    e.printStackTrace();
                    Log.d(CommonConstants.LOGCAT_TAG_NAME + "_exception", "出现异常错误");
                }

            }

        }).start();

    }


    /**
     * 社会化分享
     */
    private void socialShare() {
        /**
         * 设置分享的数据
         */
        // 首先在您的Activity中添加如下成员变量
        mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        // 设置分享图片, 参数2为图片的url地址
        mController.setShareMedia(new UMImage(getContext(),
                mPicPath));
        mController.getConfig().removePlatform(SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN);


        /**
         * 初始化分享平台
         */
        //参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.////qq初始化OK
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(), "1105372474",
                "Rp6xFMrsPiMZj35a");
        qqSsoHandler.addToSocialSDK();

        //参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.//qq空间初始化OK
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(getActivity(), "1105372474",
                "Rp6xFMrsPiMZj35a");
        qZoneSsoHandler.addToSocialSDK();

        //微信初始化OK。
        UMWXHandler umwxHandler = new UMWXHandler(getActivity(), "wx827cc5ed5072bcde",
                "6946df73f1e5c25e8a751749090d973d ");
        umwxHandler.addToSocialSDK();

        //微信朋友圈初始化OK
        UMWXHandler wxCircleHandler = new UMWXHandler(getActivity(), "wx827cc5ed5072bcde", "6946df73f1e5c25e8a751749090d973d");
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }
}
