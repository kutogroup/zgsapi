package com.zgs.api.appwidget.list;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.zgs.api.R;

/**
 * Created by simon on 15-11-30.
 */
public class MYSimpleListView extends MYListView {
    private MYSimpleListViewAdapter adapter;
    private int itemLayoutID = 0;

    public MYSimpleListView(Context context) {
        this(context, null, 0);
    }

    public MYSimpleListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MYSimpleListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.MYSimpleListView);
        for (int n = 0; n < types.getIndexCount(); n++) {
            int attr = types.getIndex(n);

            if (attr == R.styleable.MYSimpleListView_itemView) {
                itemLayoutID = types.getResourceId(attr, 0);
            }
        }
        types.recycle();

        if (itemLayoutID == 0) {
            throw new IllegalArgumentException("itemView must be set");
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter instanceof MYSimpleListViewAdapter) {
            this.adapter = (MYSimpleListViewAdapter) adapter;
        } else {
            throw new IllegalArgumentException("adapter must extends MYSimpleListViewAdapter");
        }

        ((MYSimpleListViewAdapter) adapter).setItemLayoutID(itemLayoutID);
        super.setAdapter(adapter);
    }
}
