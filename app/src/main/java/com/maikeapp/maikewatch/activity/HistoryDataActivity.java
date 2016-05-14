package com.maikeapp.maikewatch.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.bean.OneDayData;
import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.business.IUserBusiness;
import com.maikeapp.maikewatch.business.imp.UserBusinessImp;
import com.maikeapp.maikewatch.config.CommonConstants;
import com.maikeapp.maikewatch.exception.ServiceException;
import com.maikeapp.maikewatch.util.CommonUtil;
import com.maikeapp.maikewatch.util.JsonUtils;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONObject;

import java.util.List;

public class HistoryDataActivity extends AppCompatActivity {

    private ImageView mIvCommonBack;//返回
    private TextView mTvCommonTitle;//标题

    private LinearLayout mLineChartWeek;
    private LinearLayout mLineChartMonth;

    private String m_title="历史数据";
    /**
     * 业务层
     */
    private IUserBusiness mUserBusiness = new UserBusinessImp();
    private static ProgressDialog mProgressDialog = null;
    private User mUser;//用户信息

    private List<OneDayData> m_day_datas_for_week;//一个周的总步数
    private List<OneDayData> m_day_datas_for_month;//一个月的总步数
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_data);
        initView();
        initData();
        setListener();

    }

    private void initView() {
        //通用控件
        mIvCommonBack = (ImageView)findViewById(R.id.iv_common_back);
        mTvCommonTitle = (TextView)findViewById(R.id.tv_common_title);
        //两个布局
        mLineChartWeek = (LinearLayout)findViewById(R.id.line_history_data_chart_week);
        mLineChartMonth = (LinearLayout)findViewById(R.id.line_history_data_chart_month);
    }

    private void initData() {
        //通用控件
        mTvCommonTitle.setText(m_title);
        //用户信息
        mUser = CommonUtil.getUserInfo(this);
        //查询最近7天的数据
        queryRecentlySportDataForWeekFromNetWork();
        //查询最近30天的数据
        queryRecentlySportDataForMonthFromNetWork();

    }

    /**
     * 查询最近30天的数据
     */
    private void queryRecentlySportDataForMonthFromNetWork() {

        //开启副线程-从网络查询最近7天的数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String _days_data_result = mUserBusiness.queryRecentlySportsData(mUser,30);
                    Log.d(CommonConstants.LOGCAT_TAG_NAME+"_days7_result",_days_data_result);

                    JSONObject _json_obj_result = new JSONObject(_days_data_result);
                    boolean _Success = JsonUtils.getBoolean(_json_obj_result,"Success");
                    if (_Success){
                        String _json_datas = JsonUtils.getString(_json_obj_result,"Datas");
                        m_day_datas_for_month = new Gson().fromJson(_json_datas,new TypeToken<List<OneDayData>>(){}.getType());
                        Log.d(CommonConstants.LOGCAT_TAG_NAME+"_day7_datas",m_day_datas_for_week.toString());
                        // 同步完成
                        handler.sendEmptyMessage(CommonConstants.FLAG_GET_RECENT_DATAS_FOR_MONTH_SUCCESS);
                    }else{
                        String _Message = JsonUtils.getString(_json_obj_result,"Message");
                        CommonUtil.sendErrorMessage(_Message,handler);
                    }

                }catch (ServiceException e){
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage(e.getMessage(),handler);
                }catch(Exception e){
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage(CommonConstants.MSG_GET_ERROR,handler);
                }
            }
        }).start();
    }

    /**
     * 查询最近7天的数据
     */
    private void queryRecentlySportDataForWeekFromNetWork() {
        //开启副线程-从网络查询最近7天的数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String _days_data_result = mUserBusiness.queryRecentlySportsData(mUser,7);
                    Log.d(CommonConstants.LOGCAT_TAG_NAME+"_days7_result",_days_data_result);

                    JSONObject _json_obj_result = new JSONObject(_days_data_result);
                    boolean _Success = JsonUtils.getBoolean(_json_obj_result,"Success");
                    if (_Success){
                        String _json_datas = JsonUtils.getString(_json_obj_result,"Datas");
                        m_day_datas_for_week = new Gson().fromJson(_json_datas,new TypeToken<List<OneDayData>>(){}.getType());
                        Log.d(CommonConstants.LOGCAT_TAG_NAME+"_day7_datas",m_day_datas_for_week.toString());
                        // 同步完成
                        handler.sendEmptyMessage(CommonConstants.FLAG_GET_RECENT_DATAS_FOR_WEEK_SUCCESS);
                    }else{
                        String _Message = JsonUtils.getString(_json_obj_result,"Message");
                        CommonUtil.sendErrorMessage(_Message,handler);
                    }

                }catch (ServiceException e){
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage(e.getMessage(),handler);
                }catch(Exception e){
                    e.printStackTrace();
                    CommonUtil.sendErrorMessage(CommonConstants.MSG_GET_ERROR,handler);
                }
            }
        }).start();
    }

    private void setListener() {
        //通用控件
        mIvCommonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryDataActivity.this.finish();
            }
        });
    }

    //折线图
    public void lineView(List pAllData, LinearLayout pLineChart){
        //同样是需要数据dataset和视图渲染器renderer
        XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
        XYSeries series = new XYSeries("步数");
        //再画每个时刻的步数
        if (pAllData!=null&&pAllData.size()>0){
            for (int i = 0; i <pAllData.size() ; i++) {
                OneDayData _one_day_data = (OneDayData) pAllData.get(i);
//                int hour = _one_day_data.getCompleteHour();
                //小时，步数
                series.add(i,_one_day_data.getCompletedSteps());

            }
        }
        mDataset.addSeries(series);


        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        //设置图表的X轴的当前方向
        mRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
        mRenderer.setXTitle("日期");//设置为X轴的标题
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
        mRenderer.setXAxisMax(pAllData.size());//x轴刻度的个数
        mRenderer.setShowGrid(true);//显示网格


        //将x标签栏目显示如：1,2,3,4替换为显示1月，2月，3月，4月
        for (int i = 0; i < pAllData.size(); i++) {
            OneDayData _one_day_data = (OneDayData) pAllData.get(i);
            String _sprots_time = _one_day_data.getSportsTime();
            String _x_name = _sprots_time.substring(_sprots_time.indexOf("/")+1);
            mRenderer.addXTextLabel(i, _x_name);
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


        GraphicalView view = ChartFactory.getLineChartView(this, mDataset, mRenderer);
        view.setBackgroundColor(Color.WHITE);
        view.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 500));


        pLineChart.removeAllViews();
        pLineChart.addView(view);
    }

//    //折线图
//    public void lineView2(List pAllData,int days,LinearLayout pLineChart){
//        //同样是需要数据dataset和视图渲染器renderer
//        XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
//        XYSeries series = new XYSeries("步数");
//        //先画所有的数据为0
//        for (int i = 0; i <30 ; i++) {
//            series.add(i, 100);
//        }
//        //再画每个时刻的步数
//        if (pAllData!=null&&pAllData.size()>0){
//            for (int i = 0; i <pAllData.size() ; i++) {
//                OneDayData _one_day_data = (OneDayData) pAllData.get(i);
//                int hour = _one_day_data.getCompleteHour();
//                //小时，步数
//                series.add(hour,_one_day_data.getSteps());
//
//            }
//        }
//        mDataset.addSeries(series);
//
//
//        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
//        //设置图表的X轴的当前方向
//        mRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.HORIZONTAL);
//        mRenderer.setXTitle("日期");//设置为X轴的标题
//        mRenderer.setYTitle("步数");//设置y轴的标题
//        mRenderer.setAxisTitleTextSize(20);//设置轴标题文本大小
////			mRenderer.setChartTitle("今日数据");//设置图表标题
////			mRenderer.setChartTitleTextSize(30);//设置图表标题文字的大小
//        mRenderer.setLabelsTextSize(18);//设置标签的文字大小
//        mRenderer.setLegendTextSize(20);//设置图例文本大小
//        mRenderer.setPointSize(10f);//设置点的大小
//        mRenderer.setYAxisMin(0);//设置y轴最小值是0
//        mRenderer.setYAxisMax(600);//y轴最大值600
//        mRenderer.setYLabels(3);//设置Y轴刻度个数（貌似不太准确）
//        mRenderer.setXAxisMax(days);
//        mRenderer.setShowGrid(true);//显示网格
//
//        List<String> _date_list = new ArrayList<String>();
//        _date_list.add("05-07");
//        _date_list.add("05-07");
//        _date_list.add("05-07");
//        _date_list.add("05-07");
//        _date_list.add("05-07");
//        _date_list.add("05-07");
//        _date_list.add("05-07");
//        _date_list.add("05-07");
//        _date_list.add("05-07");
//        _date_list.add("05-08");
//        _date_list.add("05-07");
//        _date_list.add("05-07");
//        _date_list.add("05-07");
//        _date_list.add("05-07");
//        _date_list.add("05-07");
//        _date_list.add("05-07");
//        _date_list.add("05-07");
//        _date_list.add("05-07");
//        _date_list.add("05-07");
//        _date_list.add("05-08");
//        _date_list.add("05-07");
//        _date_list.add("05-07");
//        _date_list.add("05-07");
//        _date_list.add("05-07");
//        _date_list.add("05-08");
//        _date_list.add("05-09");
//        _date_list.add("05-10");
//        _date_list.add("05-11");
//        _date_list.add("05-12");
//        _date_list.add("05-13");
//        //将x标签栏目显示如：1,2,3,4替换为显示1月，2月，3月，4月
//        for (int i = 0; i < 7; i++) {
//            mRenderer.addXTextLabel(i, _date_list.get(i));
//        }
//
//        mRenderer.setXLabels(0);//设置只显示如1月，2月等替换后的东西，不显示1,2,3等
//        mRenderer.setMargins(new int[] { 20, 30, 15, 20 });//设置视图位置
//
//        XYSeriesRenderer r = new XYSeriesRenderer();//(类似于一条线对象)
//        r.setColor(Color.RED);//设置颜色
//        r.setPointStyle(PointStyle.CIRCLE);//设置点的样式
//        r.setFillPoints(true);//填充点（显示的点是空心还是实心）
//        r.setDisplayChartValues(true);//将点的值显示出来
//        r.setChartValuesSpacing(20);//显示的点的值与图的距离
//        r.setChartValuesTextSize(25);//点的值的文字大小
//
//        //  r.setFillBelowLine(true);//是否填充折线图的下方
//        //  r.setFillBelowLineColor(Color.GREEN);//填充的颜色，如果不设置就默认与线的颜色一致
//        r.setLineWidth(3);//设置线宽
//        mRenderer.addSeriesRenderer(r);
//
//        mRenderer.setApplyBackgroundColor(true);//必须设置为true，颜色值才生效
//        mRenderer.setBackgroundColor(Color.WHITE);//设置表格背景色
//        mRenderer.setMarginsColor(Color.WHITE);//设置周边背景色
//        mRenderer.setAxesColor(getResources().getColor(R.color.common_content_gray_text_font_color));
//
//        mRenderer.setLegendHeight(60);//设置图例高度
//        mRenderer.setPanEnabled(false);//设置xy轴是否可以拖动
//        mRenderer.setZoomEnabled(true);
//
//
//        GraphicalView view = ChartFactory.getLineChartView(this, mDataset, mRenderer);
//        view.setBackgroundColor(Color.WHITE);
//        view.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 500));
//
//
//        pLineChart.removeAllViews();
//        pLineChart.addView(view);
//    }

    //处理消息队列
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(mProgressDialog!=null){
                mProgressDialog.dismiss();
            }
            int flag = msg.what;
            switch (flag) {
                case 0:
                    String errorMsg = (String)msg.getData().getSerializable("ErrorMsg");
                    try {
                        Toast.makeText(HistoryDataActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CommonConstants.FLAG_GET_RECENT_DATAS_FOR_WEEK_SUCCESS:
                    updateUIAfterGetRecentDatasForWeek();
                    break;
                case CommonConstants.FLAG_GET_RECENT_DATAS_FOR_MONTH_SUCCESS:
                    updateUIAfterGetRecentDatasForMonth();
                    break;
                default:
                    break;
            }
        }


    };



    /**
     * 更新UI-更新一周的折线图
     */
    private void updateUIAfterGetRecentDatasForWeek() {
        //显示折线图
        lineView(m_day_datas_for_week,mLineChartWeek);
    }

    /**
     * 更新UI-更新一周的折线图
     */
    private void updateUIAfterGetRecentDatasForMonth() {
        //显示折线图
        lineView(m_day_datas_for_month,mLineChartMonth);
    }
}
