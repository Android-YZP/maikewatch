package com.maikeapp.maikewatch.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.maikeapp.maikewatch.R;
import com.maikeapp.maikewatch.bean.WatchMac;


public class WatchMacListAdapter extends BaseAdapter  implements ListAdapter {
	private List<WatchMac> mWatchMacs;
	private Context mContext;
	
	public WatchMacListAdapter() {
	}

	public WatchMacListAdapter(Context context, List<WatchMac> mWatchMacs) {
		this.mContext = context;
		this.mWatchMacs = mWatchMacs;
	}
	@Override
	public int getCount() {
		return mWatchMacs.size();
	}

	@Override
	public Object getItem(int position) {
		return mWatchMacs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		view = LayoutInflater.from(mContext).inflate(R.layout.lv_item_watch_mac, null);
		if(mWatchMacs.size()>0){
			WatchMac _watch_mac = mWatchMacs.get(position);
			ImageView ivMacRssi = (ImageView)view.findViewById(R.id.iv_item_watch_mac_rssi);
			int _rssi = _watch_mac.getRssi();
			if(_rssi<=-80){
				ivMacRssi.setBackgroundResource(R.drawable.bind_watch_one);
			}else if(_rssi>-80&&_rssi<=-60){
				ivMacRssi.setBackgroundResource(R.drawable.bind_watch_two);
			}else if(_rssi>-60&&_rssi<=-40){
				ivMacRssi.setBackgroundResource(R.drawable.bind_watch_three);
			}else if(_rssi>-40&&_rssi<=-20){
				ivMacRssi.setBackgroundResource(R.drawable.bind_watch_four);
			}else {
				ivMacRssi.setBackgroundResource(R.drawable.bind_watch_five);
			}

			TextView tvRssi = (TextView)view.findViewById(R.id.tv_item_watch_mac_rssi);
			tvRssi.setText(""+_watch_mac.getRssi());
			TextView tvMac = (TextView)view.findViewById(R.id.tv_item_watch_mac_address);
			tvMac.setText(_watch_mac.getMac());
		}
		return view;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	private static class ViewHolder{
		private static ImageView ivHomeCreativeItemTitle;
		private static TextView tvHomeCreativeDatatime;

	}
}
