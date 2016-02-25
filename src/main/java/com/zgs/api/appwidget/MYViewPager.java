package com.zgs.api.appwidget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by simon on 15-12-1.
 */
public class MYViewPager extends ViewPager {
    private boolean scrollEnable = true;

    public MYViewPager(Context context) {
        this(context, null);
    }

    public MYViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isScrollEnable() {
        return scrollEnable;
    }

    public void setScrollEnable(boolean scrollEnable) {
        this.scrollEnable = scrollEnable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(!scrollEnable){
            return false;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(!scrollEnable){
            return false;
        }

        return super.onTouchEvent(ev);
    }
}
