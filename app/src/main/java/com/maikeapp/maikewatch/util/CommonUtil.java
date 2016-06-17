package com.maikeapp.maikewatch.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.bean.AppVersion;
import com.maikeapp.maikewatch.bean.User;

import java.text.NumberFormat;
import java.util.List;


public class CommonUtil {
	/**
	 * 判断一个服务是否在运行
	 * @param mContext
	 * @param className
     * @return
     */

	public static boolean isServiceRunning(Context mContext,String className) {

		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager)
				mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList
				= activityManager.getRunningServices(30);

		if (!(serviceList.size()>0)) {
			return false;
		}

		for (int i=0; i<serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}




	/**
	 * 保存用户信息
	 * @param user
	 * @param mContext
	 */
	public static void saveUserInfo(User user, Context mContext){
		//构建对象
		Gson gson = new Gson();
		String gsonUser = gson.toJson(user);
		//获取指定Key的SharedPreferences对象
		SharedPreferences _SP = mContext.getSharedPreferences("UserInfo", mContext.MODE_PRIVATE);
		//获取编辑
		SharedPreferences.Editor _Editor = _SP.edit();
		//按照指定Key放入数据
		_Editor.putString("user", gsonUser);
		//提交保存数据
		_Editor.commit();
	}
	
	/**
	 * 获取登录用户的信息
	 * @param mContext
	 * @return
	 */
	public static User getUserInfo(Context mContext){
		SharedPreferences _SP = mContext.getSharedPreferences("UserInfo", mContext.MODE_PRIVATE);
		if(_SP==null){
			return null;
		}else{
			String jsonUser = _SP.getString("user", "");
			Gson gson = new Gson();
			User user = gson.fromJson(jsonUser, User.class);
			return user;
		}
		
	}
	
	/**
	 * 清除用户信息
	 * @param mContext
	 */
	public static void clearUserInfo(Context mContext){
		SharedPreferences _SP = mContext.getSharedPreferences("UserInfo", mContext.MODE_PRIVATE);
		if(_SP!=null){
			SharedPreferences.Editor _Editor = _SP.edit();
			_Editor.clear();
			_Editor.commit();
		}
	}

	
	/**
	 * 设置子回复listview的高度，以适应布局
	 * @param listView
	 */
	public static void setReplyListViewHeight(ListView listView) {    
        // 获取ListView对应的Adapter    
        ListAdapter listAdapter = listView.getAdapter();    
        
        if (listAdapter == null) {    
            return;    
        }    
        int totalHeight = 0;    
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目    
            View listItem = listAdapter.getView(i, null, listView);    
            listItem.measure(0, 0); // 计算子项View 的宽高    
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度    
        }    
        
        ViewGroup.LayoutParams params = listView.getLayoutParams();    
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));    
        listView.setLayoutParams(params);    
    }   
	
	/**
	 * 设置listview的高度，以适应布局
	 * @param listView
	 */
	public static void setListViewHeight(ListView listView) {    
        // 获取ListView对应的Adapter    
        ListAdapter listAdapter = listView.getAdapter();    
        
        if (listAdapter == null) {    
            return;    
        }    
        int totalHeight = 0;    
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目    
            View listItem = listAdapter.getView(i, null, listView);    
            listItem.measure(0, 0); // 计算子项View 的宽高    
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度    
        }    
        
        ViewGroup.LayoutParams params = listView.getLayoutParams();    
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));    
        listView.setLayoutParams(params);    
    }   
	
	/**
	 * 发送错误信息到消息队列
	 * @param errorMsg
	 */
	public static void sendErrorMessage(String errorMsg,Handler handler){
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putSerializable("ErrorMsg", errorMsg);
		msg.setData(data);
		handler.sendMessage(msg);
	}
	
	/**
	 * 获取app版本
	 * @param context
	 * @return
	 */
	public static AppVersion getAppVersion(Context context){
		AppVersion version = null;
		
		PackageManager pm = context.getPackageManager();
		PackageInfo info = null;
		if(pm != null){
			try {
				info = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(info != null){
			version = new AppVersion();
			version.setVersionCode(info.versionCode);
			version.setVersionName(info.versionName);
		}
		return version;
	}
	
	/**
	 * 创建luncher图标logo
	 */
	public static void createShortCut(Context context) {
        //创建快捷方式的Intent
        Intent addShortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT"); 
        //不允许重复创建
        addShortcut.putExtra("duplicate", false);
        //<span><span class="comment">指定当前的Activity为快捷方式启动的对象: 如 com.android.music.</span>MusicBrowserActivity<span> </span></span>        
        //<span><span class="comment">注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程序</span></span>
        ComponentName comp = new ComponentName(context.getPackageName(), "."
                + ((Activity)context).getLocalClassName());
        addShortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT,new Intent(Intent.ACTION_MAIN).setComponent(comp));
        //设置快捷方式的图标
        ShortcutIconResource icon = ShortcutIconResource.fromContext(context,
                R.mipmap.ic_launcher);
        addShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
       // 设置快捷方式的名字
       addShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
       context.sendBroadcast(addShortcut);
  	}

	/**
	 * 计算百分比
	 * @param diliverNum 除数
	 * @param queryMailNum 被除数
	 * @return
	 */
	public static String calcPercent(int diliverNum,int queryMailNum){
//        int diliverNum=3;//举例子的变量
//        int queryMailNum=9;//举例子的变量
		// 创建一个数值格式化对象
		NumberFormat numberFormat = NumberFormat.getInstance();
		// 设置精确到小数点后0位
		numberFormat.setMaximumFractionDigits(0);
		String result = numberFormat.format((float)diliverNum/(float)queryMailNum*100);
		System.out.println("diliverNum和queryMailNum的百分比为:" + result + "%");
		return result;
	}

	/**
	 * 格式化数据
	 * @param d 原数据
	 * @param savenum 保留小数位数
	 * @return
	 */
	public static String formatData(double d,int savenum){
		// 创建一个数值格式化对象
		NumberFormat numberFormat = NumberFormat.getInstance();
		// 设置精确到小数点后2位
		numberFormat.setMaximumFractionDigits(savenum);
		String result = numberFormat.format(d);
		return result;
	}

	/**
	 * 判断网络是否可用
	 * @param context
	 * @return
	 */
	public static boolean isnetWorkAvilable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(connectivityManager == null) {
			Log.e("FlyleafActivity", "couldn't get connectivity manager");
		} else {
			NetworkInfo [] networkInfos = connectivityManager.getAllNetworkInfo();
			if(networkInfos != null){
				for (int i = 0, count = networkInfos.length; i < count; i++) {
					if(networkInfos[i].getState() == NetworkInfo.State.CONNECTED){
						return true;
					}
				}
			}
		}
		return false;
	}



}
