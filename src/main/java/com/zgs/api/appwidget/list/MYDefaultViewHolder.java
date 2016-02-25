package com.zgs.api.appwidget.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by simon on 15-12-1.
 */
public class MYDefaultViewHolder extends RecyclerView.ViewHolder {
    public int position = 0;

    public MYDefaultViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
