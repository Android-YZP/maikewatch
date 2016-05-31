package com.maikeapp.maikewatch.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.bean.OneDayData;
import com.maikeapp.maikewatch.config.CommonConstants;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.List;

/**
 * Created by SunnyJiang on 2016/5/31.
 */
public class LineChartView extends LinearLayout{
    private Context mContext;
    private List<OneDayData> mListDatas;

    public LineChartView(Context context) {
        super(context);
        this.mContext = context;
    }

    public LineChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public void setmListDatas(List<OneDayData> mListDatas) {
        this.mListDatas = mListDatas;
        lineView(mListDatas);
    }

    //折线图
    public void lineView(List pTodayData) {
        //同样是需要数据dataset和视图渲染器renderer
        XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
        XYSeries series = new XYSeries("步数");
//        //先画所有的数据为0
//        for (int i = 0; i <24 ; i++) {
//            series.add(i, MathHelper.NULL_VALUE);
//        }
        int _max = 0;
        //再画每个时刻的步数
        if (pTodayData != null && pTodayData.size() > 0) {
            for (int i = 0; i < pTodayData.size(); i++) {
                OneDayData _one_day_data = (OneDayData) pTodayData.get(i);
                int hour = _one_day_data.getCompleteHour();
                //小时，步数
                int _steps = _one_day_data.getSteps();
                if (_steps>_max){
                    _max = _steps;
                }
                series.add(hour, _steps);

            }
        }
        mDataset.addSeries(series);

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
        Log.d(CommonConstants.LOGCAT_TAG_NAME,"pTodayData size is "+pTodayData.size()+",max is "+_max);
        int _y_value = (_max/1000)*1000+1000;
        mRenderer.setYAxisMax(_y_value);//y轴最大值600
        mRenderer.setYLabels(3);//设置Y轴刻度个数（貌似不太准确）
        mRenderer.setXAxisMax(23);
        mRenderer.setShowGrid(true);//显示网格


        //将x标签栏目显示如：1,2,3,4替换为显示1月，2月，3月，4月
        for (int i = 0; i < 24; i++) {
            mRenderer.addXTextLabel(i, String.valueOf(i));
        }

        mRenderer.setXLabels(0);//设置只显示如1月，2月等替换后的东西，不显示1,2,3等
        mRenderer.setMargins(new int[]{20, 30, 15, 20});//设置视图位置

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
        mRenderer.setInScroll(true);//解决与ScrollView焦点冲突
        mRenderer.setZoomEnabled(false);


        GraphicalView view = ChartFactory.getLineChartView(mContext, mDataset, mRenderer);
        view.setBackgroundColor(Color.WHITE);
        view.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 500));


//	        setContentView(view);
        this.removeAllViews();
        this.addView(view);
    }


}
