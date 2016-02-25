package com.zgs.api.appwidget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.RotateDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zgs.api.R;
import com.zgs.api.utils.LogUtils;
import com.zgs.api.utils.SysUtils;

/**
 * pull refresh
 */
public class MYPullRefreshLayout extends FrameLayout {
    final int STATUS_IDLE = 0;
    final int STATUS_MOVE = STATUS_IDLE + 1;
    final int STATUS_SCROLL_TOP = STATUS_MOVE + 1;
    final int STATUS_SCROLL_TO_REFRESH = STATUS_SCROLL_TOP + 1;
    final int STATUS_REFRESHING = STATUS_SCROLL_TO_REFRESH + 1;

    /**
     * content view
     */
    private View contentView;
    /**
     * header view
     */
    private View headerView;
    /**
     * if can touch
     */
    private boolean touchable = true;
    /**
     * coordinate y value of last touch
     */
    float lastActionY = -1;
    /**
     * touch status
     */
    int status = STATUS_IDLE;
    /**
     * rotate ticks, control the rotate angle
     */
    int rotateTicks = 0;
    /**
     * header view height
     */
    int headerViewHeight = 0;
    /**
     * refresh listener
     */
    OnRefreshListener refreshListener;
    /**
     * the min distance to judge if toggle pull action
     */
    int touchSlop;
    /**
     * the loading anim bg
     */
    RotateDrawable loadingDrawable;
    /**
     * the pull offset y
     */
    float offsetY;
    /**
     * animation of scroll top
     */
    ObjectAnimator topAnim;
    /**
     * animation of scroll refresh
     */
    ObjectAnimator refreshAnim;
    /**
     * handle of rotary image
     */
    Handler rotateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            rotateTicks -= 200;

            loadingDrawable.setLevel(rotateTicks);
            rotateHandler.sendEmptyMessageDelayed(0, 1);
        }
    };
    /**
     * refresh runnable
     */
    Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            if (refreshListener != null) {
                refreshListener.onRefresh();
            }
        }
    };

    ImageView iv_pullrefresh_loading;
    TextView tv_pullrefresh_loading;

    public MYPullRefreshLayout(Context context) {
        this(context, null, 0);
    }

    public MYPullRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MYPullRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                if (getChildCount() > 1 || getChildCount() == 0) {
                    LogUtils.error("child count must be 1, now=" + getChildCount());
                } else {
                    contentView = getChildAt(0);
                }

                LayoutInflater.from(getContext()).inflate(R.layout.common_widget_pullrefreshlist_header, MYPullRefreshLayout.this, true);
                headerView = getChildAt(1);
                headerView.measure(View.MeasureSpec.makeMeasureSpec(SysUtils.getScreenWidth(getContext()), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                headerViewHeight = headerView.getMeasuredHeight();
                headerView.setY(-headerViewHeight);

                iv_pullrefresh_loading = (ImageView) headerView.findViewById(R.id.iv_pullrefresh_loading);
                tv_pullrefresh_loading = (TextView) headerView.findViewById(R.id.tv_pullrefresh_loading);

                loadingDrawable = (RotateDrawable) iv_pullrefresh_loading.getBackground();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean handled = false;

        if (!touchable) {
            return false;
        }

        int refresh = 0;
        if (refreshListener != null && refreshListener.canRefresh() > 0) {
            refresh = refreshListener.canRefresh();
        }

        LogUtils.info("can scroll=" + ViewCompat.canScrollVertically(contentView, -1));
        if ((refresh == 0 && !ViewCompat.canScrollVertically(contentView,
                -1)) || (refresh == 1)) {
            handled = onTouchEvent(ev);
        }

        return !handled ? super.onInterceptTouchEvent(ev) : handled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;

        if (!touchable) {
            return false;
        }

        rotateHandler.removeMessages(0);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastActionY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (lastActionY > 0) {
                    float yDiff = event.getY() - lastActionY;

                    if (yDiff > touchSlop) {
                        status = STATUS_MOVE;
                        setOffsetY((yDiff - touchSlop) / 3);
                        handled = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (offsetY > headerViewHeight) {
                    scrollToRefresh();
                } else {
                    scrollToTop();
                }

                lastActionY = -1;
                break;
        }

        return handled;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
        headerView.setY(-headerViewHeight + offsetY);
        contentView.setY(offsetY);

        if (status == STATUS_MOVE) {
            if (offsetY > headerViewHeight) {
                tv_pullrefresh_loading.setText(R.string.str_common_release_to_refresh);
            } else {
                tv_pullrefresh_loading.setText(R.string.str_common_pull_to_refresh);
            }
        }

        rotateTicks = (int) (offsetY * 50);
        loadingDrawable.setLevel(rotateTicks);

        if (offsetY == 0 && status == STATUS_SCROLL_TOP) {
            status = STATUS_IDLE;
            touchable = true;
            return;
        }

        if (offsetY == headerViewHeight && status == STATUS_SCROLL_TO_REFRESH) {
            status = STATUS_REFRESHING;

            if (!refreshListener.ifFakeRefresh()) {
                tv_pullrefresh_loading.setText(R.string.str_common_refreshing);
                rotateHandler.sendEmptyMessageDelayed(0, 1);
                postDelayed(refreshRunnable, 500);
            } else {
                tv_pullrefresh_loading.setText("刚刚更新过");
                scrollToTop();
            }
            return;
        }
    }

    public void reset() {
        if (headerView == null) {
            return;
        }

        rotateHandler.removeMessages(0);
        scrollToTop();
    }

    public void setRefreshListener(OnRefreshListener listener) {
        refreshListener = listener;
    }

    private void scrollToTop() {
        touchable = false;
        status = STATUS_SCROLL_TOP;

        topAnim = ObjectAnimator.ofFloat(MYPullRefreshLayout.this, "offsetY", offsetY, 0);
        topAnim.setDuration(200);
        topAnim.start();
    }

    private void scrollToRefresh() {
        if (refreshListener != null) {
            touchable = false;
            status = STATUS_SCROLL_TO_REFRESH;

            refreshAnim = ObjectAnimator.ofFloat(MYPullRefreshLayout.this, "offsetY", offsetY, headerViewHeight);
            refreshAnim.setDuration(200);
            refreshAnim.start();
        } else {
            scrollToTop();
        }
    }

    public interface OnRefreshListener {
        void onRefresh();

        // 是否假刷新
        boolean ifFakeRefresh();

        // 0: 调用默认，1:强制下拉，2:强制禁用
        int canRefresh();
    }
}