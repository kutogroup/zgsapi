package com.zgs.api.appwidget.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by simon on 15-12-25.
 */
public abstract class MYSimpleListViewAdapter<T extends MYDefaultViewHolder> extends MYDefaultAdapter<MYDefaultViewHolder, T> {
    private int itemLayoutID = 0;

    @Override
    public void onBindHeader(MYDefaultViewHolder holder) {

    }

    @Override
    public MYDefaultViewHolder onCreateHeaderHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public int getHeaderCount() {
        return 0;
    }

    @Override
    public T onCreateContentHolder(ViewGroup parent) {
        return getHolderByView(LayoutInflater.from(parent.getContext()).inflate(getItemLayoutID(), parent, false));
    }

    public int getItemLayoutID() {
        return itemLayoutID;
    }

    public void setItemLayoutID(int itemLayoutID) {
        this.itemLayoutID = itemLayoutID;
    }

    public abstract T getHolderByView(View v);
}
