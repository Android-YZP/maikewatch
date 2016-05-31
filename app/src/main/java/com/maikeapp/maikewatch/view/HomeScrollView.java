package com.maikeapp.maikewatch.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.maikeapp.maikewatch.config.CommonConstants;

/**
 * Created by SunnyJiang on 2016/5/31.
 */
public class HomeScrollView extends ScrollView{
    private Context mContext;

    public HomeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(CommonConstants.LOGCAT_TAG_NAME,"_____HomeScrollView_dispatchKeyEvent");
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(CommonConstants.LOGCAT_TAG_NAME,"_____HomeScrollView_onInterceptTouchEvent");
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d(CommonConstants.LOGCAT_TAG_NAME,"_____HomeScrollView_onTouchEvent");
        return super.onTouchEvent(ev);
    }
}
