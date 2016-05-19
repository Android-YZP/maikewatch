package com.maikeapp.maikewatch.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	/**
	 * 提示，停留时间较短
	 * @param str
	 */
	public static void showTipShort(Context context,String str){
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 提示，停留时间较短
	 * @param str
	 */
	public static void showTipLong(Context context,String str){
		Toast.makeText(context, str, Toast.LENGTH_LONG).show();
	}
}
