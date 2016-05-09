package com.maikeapp.maikewatch.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.maikeapp.maikewatch.R;


public class IndexTabBarLayout extends LinearLayout {
	//tabbar的回调接口
	public interface IIndexTabBarCallBackListener{
		public void clickItem(int id);//按了某一项后
	}
	IIndexTabBarCallBackListener indexTabBarCallBackListener=null;
	public void setOnItemClickListener(IIndexTabBarCallBackListener indexTabBarCallBackListener){
		this.indexTabBarCallBackListener = indexTabBarCallBackListener;
	}
	
	RelativeLayout mHomeLayout;
	RelativeLayout mPsCenterLayout;
	
	LayoutInflater inflater;
	
	private final static int FLAG_HOME=0;
	private final static int FLAG_PSCENTER=1;
	
	public IndexTabBarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	private void initView() {
		inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.layout_index_tabbar, this);
		findView(view);
		initData();
		setListener();
	}
	
	/**
	 * 找到所有layout控件
	 * @param view
	 */
	private void findView(View view) {
		mHomeLayout = (RelativeLayout)view.findViewById(R.id.index_home_item);
		mPsCenterLayout = (RelativeLayout)view.findViewById(R.id.index_pscenter_item);
		
	}

	/**
	 * 初始化底部每个item的数据
	 */
	private void initData() {
		//首页
		mHomeLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.buttom_home_pre);
		TextView _tvHome = (TextView)mHomeLayout.findViewById(R.id.tv_index_tab_item_title);
		_tvHome.setText("首页");

		//我的
		mPsCenterLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.buttom_me);
		TextView _tvPsCenter = (TextView)mPsCenterLayout.findViewById(R.id.tv_index_tab_item_title);
		_tvPsCenter.setText("我");
	}
	
	/**
	 * 点击事件监听
	 */
	private void setListener() {
		mHomeLayout.setOnClickListener(new MyItemClickListener());
		mPsCenterLayout.setOnClickListener(new MyItemClickListener());
	}
	
	private class MyItemClickListener implements OnClickListener{

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.index_home_item:
				//1-改变图片;改变文字的颜色;改变layout的背景
				changeTabBarItems(FLAG_HOME);
				//2-实现页面的切换
				break;
			case R.id.index_pscenter_item:
				changeTabBarItems(FLAG_PSCENTER);
				break;
			}
			if(indexTabBarCallBackListener!=null){
				indexTabBarCallBackListener.clickItem(view.getId());
			}
		}

		
		
	}
	
	/**
	 * 点击某个tabitem，切换以下内容：改变图片;改变文字的颜色;改变layout的背景
	 * @param index 索引值
	 */
	public void changeTabBarItems(int index) {
		TextView _tvHome;
		TextView _tvPsCenter;
		
		switch (index) {
		case FLAG_HOME:
			//首页
			mHomeLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.buttom_home_pre);
			_tvHome = (TextView)mHomeLayout.findViewById(R.id.tv_index_tab_item_title);

			//我的
			mPsCenterLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.buttom_me);
			_tvPsCenter = (TextView)mPsCenterLayout.findViewById(R.id.tv_index_tab_item_title);
			break;

		case FLAG_PSCENTER:
			//首页
			mHomeLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.buttom_home);
			_tvHome = (TextView)mHomeLayout.findViewById(R.id.tv_index_tab_item_title);

			//我的
			mPsCenterLayout.findViewById(R.id.iv_index_tab_item_pic).setBackgroundResource(R.drawable.buttom_me_pre);
			_tvPsCenter = (TextView)mPsCenterLayout.findViewById(R.id.tv_index_tab_item_title);
			break;
		}
	}
	
}
