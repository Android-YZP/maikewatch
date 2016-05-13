package com.maikeapp.maikewatch.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.bean.OneDayData;
import com.maikeapp.maikewatch.bean.User;
import com.maikeapp.maikewatch.business.IUserBusiness;
import com.maikeapp.maikewatch.business.imp.UserBusinessImp;
import com.maikeapp.maikewatch.util.CommonUtil;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
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

    private User mUser;//用户信息

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
        lineView(null,7,mLineChartWeek);
        lineView2(null,30,mLineChartMonth);

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
    public void lineView(List pAllData,int days,LinearLayout pLineChart){
        //同样是需要数据dataset和视图渲染器renderer
        XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
        XYSeries series = new XYSeries("步数");
        //先画所有的数据为0
        for (int i = 0; i <7 ; i++) {
            series.add(i, 100);
        }
        //再画每个时刻的步数
        if (pAllData!=null&&pAllData.size()>0){
            for (int i = 0; i <pAllData.size() ; i++) {
                OneDayData _one_day_data = (OneDayData) pAllData.get(i);
                int hour = _one_day_data.getTime();
                //小时，步数
                series.add(hour,_one_day_data.getStep());

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
        mRenderer.setXAxisMax(days);
        mRenderer.setShowGrid(true);//显示网格

        List<String> _date_list = new ArrayList<String>();
        _date_list.add("05-07");
        _date_list.add("05-08");
        _date_list.add("05-09");
        _date_list.add("05-10");
        _date_list.add("05-11");
        _date_list.add("05-12");
        _date_list.add("05-13");
        //将x标签栏目显示如：1,2,3,4替换为显示1月，2月，3月，4月
        for (int i = 0; i < 7; i++) {
            mRenderer.addXTextLabel(i, _date_list.get(i));
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

    //折线图
    public void lineView2(List pAllData,int days,LinearLayout pLineChart){
        //同样是需要数据dataset和视图渲染器renderer
        XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
        XYSeries series = new XYSeries("步数");
        //先画所有的数据为0
        for (int i = 0; i <30 ; i++) {
            series.add(i, 100);
        }
        //再画每个时刻的步数
        if (pAllData!=null&&pAllData.size()>0){
            for (int i = 0; i <pAllData.size() ; i++) {
                OneDayData _one_day_data = (OneDayData) pAllData.get(i);
                int hour = _one_day_data.getTime();
                //小时，步数
                series.add(hour,_one_day_data.getStep());

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
        mRenderer.setXAxisMax(days);
        mRenderer.setShowGrid(true);//显示网格

        List<String> _date_list = new ArrayList<String>();
        _date_list.add("05-07");
        _date_list.add("05-07");
        _date_list.add("05-07");
        _date_list.add("05-07");
        _date_list.add("05-07");
        _date_list.add("05-07");
        _date_list.add("05-07");
        _date_list.add("05-07");
        _date_list.add("05-07");
        _date_list.add("05-08");
        _date_list.add("05-07");
        _date_list.add("05-07");
        _date_list.add("05-07");
        _date_list.add("05-07");
        _date_list.add("05-07");
        _date_list.add("05-07");
        _date_list.add("05-07");
        _date_list.add("05-07");
        _date_list.add("05-07");
        _date_list.add("05-08");
        _date_list.add("05-07");
        _date_list.add("05-07");
        _date_list.add("05-07");
        _date_list.add("05-07");
        _date_list.add("05-08");
        _date_list.add("05-09");
        _date_list.add("05-10");
        _date_list.add("05-11");
        _date_list.add("05-12");
        _date_list.add("05-13");
        //将x标签栏目显示如：1,2,3,4替换为显示1月，2月，3月，4月
        for (int i = 0; i < 7; i++) {
            mRenderer.addXTextLabel(i, _date_list.get(i));
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
}
