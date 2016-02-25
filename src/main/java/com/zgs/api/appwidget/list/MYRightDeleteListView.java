package com.zgs.api.appwidget.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.zgs.api.appwidget.MYToast;

/**
 * Created by simon on 15-12-18.
 */
public class MYRightDeleteListView extends MYListView {
    MYRightDeleteListViewAdapter adapter = null;

    public MYRightDeleteListView(Context context) {
        this(context, null, 0);
    }

    public MYRightDeleteListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MYRightDeleteListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (adapter != null) {
                    if (adapter.isInDeleting) {
                        if (adapter.currentOptionView != null) {
                            adapter.currentOptionView.reset();
                            adapter.currentOptionView = null;
                        }
                    } else {
                        if (adapter.currentOptionView != null) {
                            adapter.currentOptionView.hideRightOption();
                            adapter.currentOptionView = null;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter instanceof MYRightDeleteListViewAdapter) {
            this.adapter = (MYRightDeleteListViewAdapter) adapter;
            super.setAdapter(adapter);
        } else {
            MYToast.show("wrong adapter");
        }
    }
}
