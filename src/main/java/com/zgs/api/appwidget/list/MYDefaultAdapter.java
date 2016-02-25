package com.zgs.api.appwidget.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.zgs.api.R;
import com.zgs.api.utils.LogUtils;

/**
 * Created by simon on 15-12-1.
 * <p/>
 * T1 header view holder, T2 content view holder
 */
public abstract class MYDefaultAdapter<T1 extends MYDefaultViewHolder, T2 extends MYDefaultViewHolder> extends RecyclerView.Adapter implements View.OnClickListener {
    /**
     * view of listview when content count is 0
     */
    private View emptyView = null;

    /**
     * default footview
     */
    protected View footView = null;

    /**
     * force hide footview;
     */
    public boolean forceHideFootView = false;

    /**
     * if list has more
     */
    public boolean hasMore = true;

    /**
     * if set, no more footer text will show as it
     */
    private String listFooterString = null;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case MYViewHolderTypes.TYPE_HEAD:
                return onCreateHeaderHolder(parent);
            case MYViewHolderTypes.TYPE_FOOT:
                if (footView == null) {
                    footView = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_widget_listview_footer, parent, false);
                }

                return new MYDefaultFootViewHolder(footView);
            case MYViewHolderTypes.TYPE_EMPTY:
                return new MYDefaultViewHolder(emptyView);
            default:
                return onCreateContentHolder(parent);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case MYViewHolderTypes.TYPE_CONTENT:
                ((MYDefaultViewHolder) holder).position = position - getHeaderCount();
                onBindContent((T2) holder);
                return;
            case MYViewHolderTypes.TYPE_FOOT:
                MYDefaultFootViewHolder foot_holder = (MYDefaultFootViewHolder) holder;
                ((MYDefaultViewHolder) holder).position = position - getHeaderCount();

                if (!forceHideFootView) {
                    holder.itemView.setVisibility(View.VISIBLE);

                    if (hasMore) {
                        foot_holder.tv_loading.setText(foot_holder.tv_loading.getResources().getString(R.string.str_common_loading_more));
                        foot_holder.iv_loading.setVisibility(View.VISIBLE);
                        foot_holder.iv_loading.startAnimation(AnimationUtils.loadAnimation(foot_holder.iv_loading.getContext(), R.anim.rotate));
                    } else {
                        foot_holder.iv_loading.clearAnimation();
                        foot_holder.iv_loading.setVisibility(View.GONE);
                        if (listFooterString == null) {
                            foot_holder.tv_loading.setText(foot_holder.tv_loading.getResources().getString(R.string.str_common_no_more));
                        } else {
                            foot_holder.tv_loading.setText(listFooterString);
                        }
                    }
                } else {
                    ViewGroup.LayoutParams params = foot_holder.itemView.getLayoutParams();
                    params.height = 0;
                    foot_holder.itemView.setLayoutParams(params);
                }
                return;
            case MYViewHolderTypes.TYPE_HEAD:
                ((MYDefaultViewHolder) holder).position = position;
                onBindHeader((T1) holder);
                return;
            case MYViewHolderTypes.TYPE_EMPTY:
                LogUtils.info("listview empty, show the default view");
                return;
        }
    }

    @Override
    public int getItemCount() {
        return getHeaderCount() +
                getContentCount() +
                (emptyView != null && getContentCount() == 0 ? 1 : 0) +
                (getContentCount() > 0 && !forceHideFootView ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getHeaderCount()) {
            return MYViewHolderTypes.TYPE_HEAD;
        } else if (position == getHeaderCount() + getContentCount()) {
            if (getContentCount() == 0 && emptyView != null) {
                return MYViewHolderTypes.TYPE_EMPTY;
            }

            return MYViewHolderTypes.TYPE_FOOT;
        } else {
            return MYViewHolderTypes.TYPE_CONTENT;
        }
    }

    public void setListFooterString(String listFooterString) {
        this.listFooterString = listFooterString;
    }

    public String getListFooterString() {
        return listFooterString;
    }

    class MYDefaultFootViewHolder extends MYTableViewHolder {
        public ImageView iv_loading;
        public TextView tv_loading;

        public MYDefaultFootViewHolder(View root) {
            super(root);

            iv_loading = (ImageView) root.findViewById(R.id.iv_listview_loading);
            tv_loading = (TextView) root.findViewById(R.id.tv_listview_loading);
        }
    }

    /**
     * @param emptyView set empty view
     */
    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    public void addListener(View v, MYDefaultViewHolder holder) {
        v.setTag(holder);
        v.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * used in grid type
     *
     * @param position
     * @return
     */
    public int getContentSpanSize(int position) {
        return 0;
    }

    public abstract T2 onCreateContentHolder(ViewGroup parent);

    public abstract T1 onCreateHeaderHolder(ViewGroup parent);

    public abstract void onBindHeader(T1 holder);

    public abstract void onBindContent(T2 holder);

    public abstract int getHeaderCount();

    public abstract int getContentCount();

    public abstract void onLoadMore();
}
