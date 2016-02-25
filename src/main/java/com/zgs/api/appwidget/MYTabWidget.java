package com.zgs.api.appwidget;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.zgs.api.listener.SelectListener;

/**
 * Created by simon on 15-12-7.
 */
public class MYTabWidget implements View.OnClickListener {
    /**
     * index of tab
     */
    private int tabIndex = 0;
    /**
     * select listener
     */
    private SelectListener selectListener = null;
    /**
     * tab views
     */
    private View[] tabViews = null;

    public MYTabWidget(View[] views) {
        if (views == null || views.length == 0) {
            return;
        }

        tabViews = views;
        for (int n = 0; n < views.length; n++) {
            views[n].setTag(n);
            views[n].setOnClickListener(this);
        }

        setTabIndex(0);
    }

    public MYTabWidget(final ViewGroup parent) {
        if (parent == null) {
            return;
        }

        parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tabViews = new View[parent.getChildCount()];
                for (int n = 0; n < parent.getChildCount(); n++) {
                    tabViews[n] = parent.getChildAt(n);
                    tabViews[n].setTag(n);
                    tabViews[n].setOnClickListener(MYTabWidget.this);
                }

                setTabIndex(0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    parent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        int index = (int) v.getTag();

        if (tabIndex != index) {
            setTabIndex(index);
        }
    }

    public void setSelectListener(SelectListener listener) {
        selectListener = listener;
    }

    public void setTabIndex(int index) {
        tabViews[tabIndex].setSelected(false);
        tabIndex = index;
        tabViews[tabIndex].setSelected(true);

        if (selectListener != null) {
            selectListener.onSelected(index);
        }
    }

    public int getTabIndex() {
        return tabIndex;
    }
}
