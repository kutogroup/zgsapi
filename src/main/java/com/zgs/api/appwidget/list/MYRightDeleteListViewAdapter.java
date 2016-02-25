package com.zgs.api.appwidget.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zgs.api.appwidget.MYDeleteWidget;
import com.zgs.api.appwidget.MYRightOptionView;
import com.zgs.api.listener.DoneListener;
import com.zgs.api.listener.ToggleListener;

/**
 * Created by simon on 15-12-1.
 * <p/>
 * T1 header view holder, T2 content view holder
 */
public abstract class MYRightDeleteListViewAdapter<T1 extends MYDefaultViewHolder, T2 extends MYDefaultViewHolder> extends MYDefaultAdapter<T1, T2> implements ToggleListener {
    public boolean isInDeleting = false;
    public MYRightOptionView currentOptionView = null;

    @Override
    public final void onToggle(View v, boolean flag) {
        if (flag) {
            if (currentOptionView != null && !currentOptionView.equals(v)) {
                currentOptionView.hideRightOption();
            }

            currentOptionView = (MYRightOptionView) v;
            currentOptionView.getRightOptionView().setTag(v.getTag());
            currentOptionView.getRightOptionView().setOnClickListener(this);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == MYViewHolderTypes.TYPE_CONTENT) {
            getRightOptionView((T2) holder).setTag(holder);
            getRightOptionView((T2) holder).setToggleListener(this);
        }

        super.onBindViewHolder(holder, position);
    }

    @Override
    public void onClick(View v) {
        if (currentOptionView != null && v.equals(currentOptionView.getRightOptionView())) {
            deleteItemByHolder((T2) v.getTag());
        } else {
            super.onClick(v);
        }
    }

    public final void deleteItemByHolder(final T2 holder) {
        isInDeleting = true;
        new MYDeleteWidget(holder.itemView, new DoneListener() {
            @Override
            public void onDone() {
                isInDeleting = false;
                onDelete(holder);
                notifyDataSetChanged();
                getRightOptionView(holder).reset();
            }
        }).startDelete();
    }

    public abstract MYRightOptionView getRightOptionView(T2 holder);

    public abstract void onDelete(T2 holder);
}
