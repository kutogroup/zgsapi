package com.zgs.api.appwidget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.zgs.api.listener.ScrollableCheckListener;
import com.zgs.api.utils.LogUtils;

/**
 * Created by simon on 15-12-29.
 */
public class MYTopBottomPageLayout extends FrameLayout implements Animator.AnimatorListener {
    final int STATUS_IDLE = 0;
    final int STATUS_MOVE = STATUS_IDLE + 1;
    final int STATUS_SCROLL_TO_TOP = STATUS_MOVE + 1;
    final int STATUS_SCROLL_TO_BOTTOM = STATUS_SCROLL_TO_TOP + 1;

    final int MAX_TOGGLE_SPEED = 1000;

    private ScrollableCheckListener listener;
    private float lastActionY = -1;
    private float lastOffsetY = 0f;
    private View topView, bottomView;
    private int touchSlop;
    private float offsetY = 0f;
    private int status = STATUS_IDLE;
    private MYScrollTracker tracker = null;
    private boolean isTop = true;

    public MYTopBottomPageLayout(Context context) {
        this(context, null, 0);
    }

    public MYTopBottomPageLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MYTopBottomPageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        tracker = new MYScrollTracker();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getChildCount() != 2) {
                    throw new IllegalArgumentException("child count must be 2");
                }

                topView = getChildAt(1);
                bottomView = getChildAt(0);

                bottomView.setVisibility(GONE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    public void setOffsetY(float y) {
        LogUtils.info("y=" + y);
        if (y > 0) {
            y = 0;
        }

        if (y < -topView.getHeight()) {
            y = -topView.getHeight();
        }

        offsetY = y;
        topView.setTranslationY(y);
        bottomView.setTranslationY(y + topView.getHeight());

        if (bottomView.getVisibility() == GONE) {
            bottomView.setVisibility(VISIBLE);
        }
    }

    public void setScrollableCheckListener(ScrollableCheckListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        status = STATUS_IDLE;

        if (offsetY == -topView.getHeight()) {
            isTop = false;
        } else if (offsetY == 0) {
            isTop = true;
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    public boolean isTop() {
        return isTop;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (listener != null && listener.canScroll() && onTouchEvent(ev)) {
            return true;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        tracker.addTracker(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastActionY = ev.getY();
                lastOffsetY = offsetY;
                break;
            case MotionEvent.ACTION_MOVE:
                if (status == STATUS_IDLE || status == STATUS_MOVE) {
                    float offset = ev.getY() - lastActionY;

                    if (status == STATUS_MOVE ||
                            status == STATUS_IDLE &&
                                    Math.abs(offset) > touchSlop &&
                                    ((isTop && offset < 0) || (!isTop && offset > 0)) &&
                                    (listener == null || listener.canScroll())) {
                        status = STATUS_MOVE;
                        setOffsetY(lastOffsetY + (int) (ev.getY() - lastActionY) / 3);
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastActionY = 0;
                lastOffsetY = 0;

                if (status == STATUS_MOVE) {
                    int speedy = tracker.getScrollVelocity(false);

                    if (speedy < -MAX_TOGGLE_SPEED) {
                        //向上
                        scrollToBottomPage();
                    } else if (speedy > MAX_TOGGLE_SPEED) {
                        //向下
                        scrollToTopPage();
                    } else {
                        if (isTop()) {
                            scrollToTopPage();
                        } else {
                            scrollToBottomPage();
                        }
                    }
                }

                break;
        }

        return false;
    }

    private void scrollToTopPage() {
        LogUtils.info("scroll to top");
        status = STATUS_SCROLL_TO_TOP;
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "offsetY", offsetY, 0);
        anim.setDuration(300);
        anim.addListener(this);
        anim.start();
    }

    private void scrollToBottomPage() {
        LogUtils.info("scroll to bottom");
        status = STATUS_SCROLL_TO_BOTTOM;
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, "offsetY", offsetY, -topView.getHeight());
        anim.setDuration(300);
        anim.addListener(this);
        anim.start();
    }
}
