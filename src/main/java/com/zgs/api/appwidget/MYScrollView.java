package com.zgs.api.appwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.zgs.api.listener.ScrollListener;

/**
 * Created by simon on 15-12-22.
 */
public class MYScrollView extends ScrollView {
    private ScrollListener listener;

    public MYScrollView(Context context) {
        super(context);
    }

    public MYScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MYScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollListener(ScrollListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (listener != null) {
            listener.onScroll(this, l, t, oldl, oldt);
        }
    }
}
