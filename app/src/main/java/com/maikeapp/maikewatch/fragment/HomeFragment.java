package com.maikeapp.maikewatch.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.view.CirclePercentView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private Button mButton;
    private CirclePercentView mCirclePercentView;
    private LinearLayout mLinearChart;

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
        mCirclePercentView = (CirclePercentView) view.findViewById(R.id.circleView);
//        circleViewmButton = (Button) view.findViewById(R.id.button);
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int n = (int)(Math.random()*100);
//                mCirclePercentView.setPercent(n);
//            }
//        });

        mLinearChart = (LinearLayout) view.findViewById(R.id.line_other_chart);
        lineView();//显示折线图
    }

    //折线图
    public void lineView(){
        //同样是需要数据dataset和视图渲染器renderer
        XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
        XYSeries  series = new XYSeries("步数");
        series.add(0, 200);
        series.add(1, 155);
        series.add(2, 145);
        series.add(3, 127);
        series.add(4, 144);
        series.add(5, 116);
        series.add(6, 115);
        series.add(7, 137);
        series.add(8, 124);
        series.add(9, 116);
        series.add(10, 125);
        series.add(11, 177);
        series.add(12, 224);
        series.add(13, 406);
        series.add(14, 525);
        series.add(15, 417);
        series.add(16, 430);
        series.add(17, 236);
        series.add(18, 418);
        series.add(19, 424);
        series.add(20, 442);
        series.add(21, 434);
        series.add(22, 432);
        series.add(23, 426);

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
        mRenderer.setYAxisMax(600);
        mRenderer.setYLabels(3);//设置Y轴刻度个数（貌似不太准确）
        mRenderer.setXAxisMax(23);
        mRenderer.setShowGrid(true);//显示网格


        //将x标签栏目显示如：1,2,3,4替换为显示1月，2月，3月，4月
        mRenderer.addXTextLabel(0, "0");
        mRenderer.addXTextLabel(1, "1");
        mRenderer.addXTextLabel(2, "2");
        mRenderer.addXTextLabel(3, "3");
        mRenderer.addXTextLabel(4, "4");
        mRenderer.addXTextLabel(5, "5");
        mRenderer.addXTextLabel(6, "6");
        mRenderer.addXTextLabel(7, "7");
        mRenderer.addXTextLabel(8, "8");
        mRenderer.addXTextLabel(9, "9");
        mRenderer.addXTextLabel(10, "10");
        mRenderer.addXTextLabel(11, "11");
        mRenderer.addXTextLabel(12, "12");
        mRenderer.addXTextLabel(13, "13");
        mRenderer.addXTextLabel(14, "14");
        mRenderer.addXTextLabel(15, "15");
        mRenderer.addXTextLabel(16, "16");
        mRenderer.addXTextLabel(17, "17");
        mRenderer.addXTextLabel(18, "18");
        mRenderer.addXTextLabel(19, "19");
        mRenderer.addXTextLabel(20, "20");
        mRenderer.addXTextLabel(21, "21");
        mRenderer.addXTextLabel(22, "22");
        mRenderer.addXTextLabel(23, "23");
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
        mLinearChart.addView(view);
    }
}
