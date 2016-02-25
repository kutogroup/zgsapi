package com.zgs.api.appwidget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.zgs.api.R;
import com.zgs.api.listener.ToggleListener;
import com.zgs.api.utils.LogUtils;

/**
 * Created by simon on 15-12-10.
 */
public class MYRightOptionView extends LinearLayout implements Runnable {
    /**
     * min touch distance to check if can scroll
     */
    final int MIN_TOUCH = 20;
    /**
     * toggle speed of finger, if reach do show/hide option view
     */
    final int TOGGLE_SPEED = 600;
    /**
     * anim duration
     */
    final int ANIM_DURATION = 200;
    /**
     * last touch x
     */
    private float lastActionX;
    /**
     * offset x of action
     */
    private float offsetX;
    /**
     * current scroll x
     */
    private int currentScrollX = 0;
    /**
     * if can toggle scroll
     */
    private boolean canScroll = false;
    /**
     * scroll tracker
     */
    private MYScrollTracker tracker = null;
    /**
     * boolean toggle listener
     */
    private ToggleListener toggleListener = null;
    /**
     * check if option is show
     */
    private boolean isShow = false;
    /**
     * if disable scroll
     */
    private boolean isDisabled = false;

    public MYRightOptionView(Context context) {
        this(context, null, 0);
    }

    public MYRightOptionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MYRightOptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.MYRightOptionView);
        for (int n = 0; n < types.getIndexCount(); n++) {
            int attr = types.getIndex(n);

            if (attr == R.styleable.MYRightOptionView_disable) {
                isDisabled = types.getBoolean(attr, false);
            }
        }
        types.recycle();

        setOrientation(HORIZONTAL);
        tracker = new MYScrollTracker();

        setClickable(true);
        setClipChildren(false);
        setClipToPadding(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isDisabled) {
            return super.onTouchEvent(event);
        }

        tracker.addTracker(event);

        if (getChildCount() == 2) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    canScroll = false;
                    lastActionX = event.getX() + currentScrollX;
                    break;
                case MotionEvent.ACTION_MOVE:
                    offsetX = event.getX() - lastActionX;

                    if (canScroll || Math.abs(offsetX) > MIN_TOUCH) {
                        setPressed(false);
                        setCurrentScrollX((int) -offsetX);
                        canScroll = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    lastActionX = 0;
                    if (canScroll) {
                        if (Math.abs(tracker.getScrollVelocity(true)) > TOGGLE_SPEED) {
                            //手指移动速度大于触发速度，执行
                            LogUtils.info("speed reach");
                            if (tracker.getScrollVelocity(true) > 0) {
                                hideRightOption();
                            } else {
                                showRightOption();
                            }
                        } else {
                            if (currentScrollX > getChildAt(1).getWidth() / 2) {
                                showRightOption();
                            } else {
                                hideRightOption();
                            }
                        }

                        canScroll = false;

                        //恢复点击事件
                        postDelayed(this, ANIM_DURATION);
                        return true;
                    }
                    break;
            }
        }

        return super.onTouchEvent(event);
    }

    public void setCurrentScrollX(int currentScrollX) {
        if (getChildCount() != 2) {
            LogUtils.error("child count must be 2");
            return;
        }

        if (currentScrollX < 0) {
            currentScrollX = 0;
        }

        if (currentScrollX > getChildAt(1).getWidth()) {
            currentScrollX = getChildAt(1).getWidth();
        }

        this.currentScrollX = currentScrollX;
        scrollTo(currentScrollX, getScrollY());
    }

    public void showRightOption() {
        if (getChildCount() != 2) {
            LogUtils.error("child count must be 2");
            return;
        }

        isShow = true;
        if (toggleListener != null) {
            toggleListener.onToggle(this, isShow);
        }
        ObjectAnimator anim = ObjectAnimator.ofInt(this, "currentScrollX", currentScrollX, getChildAt(1).getWidth());
        anim.setDuration(ANIM_DURATION);
        anim.start();
    }

    public void hideRightOption() {
        if (getChildCount() != 2) {
            LogUtils.error("child count must be 2");
            return;
        }

        isShow = false;
        if (toggleListener != null) {
            toggleListener.onToggle(this, isShow);
        }
        ObjectAnimator anim = ObjectAnimator.ofInt(this, "currentScrollX", currentScrollX, 0);
        anim.setDuration(ANIM_DURATION);
        anim.start();
    }

    public void reset() {
        setCurrentScrollX(0);
    }

    public View getRightOptionView() {
        if (getChildCount() == 2) {
            return getChildAt(1);
        }

        return null;
    }

    @Override
    public void run() {
        setPressed(false);
    }

    public void setToggleListener(ToggleListener toggleListener) {
        this.toggleListener = toggleListener;
    }

    public void setOptionDisable(boolean disable) {
        isDisabled = disable;
    }
}
