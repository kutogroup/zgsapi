package com.zgs.api.appwidget.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

import com.zgs.api.R;
import com.zgs.api.utils.LogUtils;
import com.zgs.api.utils.ViewUtils;

/**
 * Created by simon on 15-11-30.
 */
public class MYListView extends RecyclerView {
    protected int touchSlop = 0;
    protected AttributeSet attrs;

    /**
     * orientation of listview
     */
    private int orientation = VERTICAL;
    /**
     * layout manager of listview
     */
    private LinearLayoutManager layoutManager;

    /**
     * adapter of listview
     */
    protected MYDefaultAdapter adapter;
    /**
     * touch event
     */
    private int downX, downY, offsetX, offsetY, scrollFlag;
    /**
     * check whether view can be touched
     */
    private boolean scrollAble;

    /**
     * if revert layout
     */
    private boolean isReversal = false;

    /**
     * if in loading more mode
     */
    public boolean loadingMore = false;

    /**
     * touch listener
     */
    private OnTouchListener listener;

    /**
     * scroll listener
     */
    private RecyclerView.OnScrollListener defaultScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (!loadingMore) {
                if (layoutManager.findLastVisibleItemPosition() == adapter.getItemCount() - 1
                        && adapter != null && adapter.getContentCount() != 0 && adapter.hasMore) {
                    loadingMore = true;

                    // delay the load more opration
                    postDelayed(loadMoreRunnable, 500);
                }
            }
        }
    };

    /**
     * load more runnable
     */
    private Runnable loadMoreRunnable = new Runnable() {
        @Override
        public void run() {
            if (adapter != null) {
                adapter.onLoadMore();
            }
        }
    };

    public MYListView(Context context) {
        this(context, null, 0);
    }

    public MYListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public MYListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        scrollAble = true;
        this.attrs = attrs;

        if (layoutManager == null) {
            layoutManager = getLayoutManager();
        }
        setLayoutManager(layoutManager);

        if (ViewUtils.searchParentViewByClass(this, ScrollView.class) != null) {
            // 如果上层存在scrollview
            setFocusable(false);
        } else {
            addOnScrollListener(defaultScrollListener);
            setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        if (attrs != null) {
            TypedArray types = getContext().obtainStyledAttributes(attrs, R.styleable.MYListView);
            for (int n = 0; n < types.getIndexCount(); n++) {
                int attr = types.getIndex(n);

                if (attr == R.styleable.MYListView_orientation) {
                    orientation = types.getInt(attr, VERTICAL);
                } else if (attr == R.styleable.MYListView_isReversal) {
                    isReversal = types.getBoolean(attr, false);
                }
            }

            types.recycle();
        }

        return new LinearLayoutManager(getContext(), orientation, isReversal);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (!(adapter instanceof MYDefaultAdapter)) {
            LogUtils.error("adapter must be instanceof MYDefaultAdapter");
            throw new IllegalArgumentException("adapter must be instanceof MYDefaultAdapter");
        }

        this.adapter = (MYDefaultAdapter) adapter;
        super.setAdapter(adapter);
    }

    public void loadComplete(boolean complete) {
        loadingMore = false;

        if (adapter != null) {
            adapter.hasMore = !complete;
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (listener != null) {
            if (listener.onTouch(this, ev)) {
                return false;
            }
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) ev.getX();
                downY = (int) ev.getY();
                offsetX = 0;
                offsetY = 0;
                scrollFlag = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                offsetX = (int) Math.abs(downX - ev.getX());
                offsetY = (int) Math.abs(downY - ev.getY());

                if (scrollFlag == 0) {
                    if (offsetX > offsetY && offsetX > touchSlop) {
                        //横项滑动
                        scrollFlag = 1;
                    } else if (offsetX < offsetY && offsetY > touchSlop) {
                        //纵向滑动
                        scrollFlag = 2;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                downX = -1;
                downY = -1;
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (!scrollAble) {
            return false;
        }

        if (scrollFlag == 1 && layoutManager.getOrientation() != HORIZONTAL) {
            return false;
        } else if (scrollFlag == 2 && layoutManager.getOrientation() != VERTICAL) {
            return false;
        }

        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (!scrollAble) {
            return false;
        }

        if (scrollFlag == 1 && layoutManager.getOrientation() != HORIZONTAL) {
            return true;
        } else if (scrollFlag == 2 && layoutManager.getOrientation() != VERTICAL) {
            return true;
        }

        return super.onTouchEvent(e);
    }

    public boolean isScrollable() {
        return scrollAble;
    }

    public void setScrollable(boolean scrollAble) {
        this.scrollAble = scrollAble;
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        listener = l;
    }

    public void setListFooterString(String listFooterString) {
        adapter.setListFooterString(listFooterString);
    }
}
