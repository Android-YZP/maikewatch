package com.maikeapp.maikewatch.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by aaa on 2016/7/27.
 */
public class NoTouchLinearLayout extends LinearLayout {
    public NoTouchLinearLayout(Context context) {
        super(context);
    }

    public NoTouchLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoTouchLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return true;
    }
}
