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
 * Created by simon on 16-1-28.
 */
public abstract class MYTableListViewAdapter extends RecyclerView.Adapter<MYTableViewHolder> implements View.OnClickListener {
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
    public MYTableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case MYViewHolderTypes.TYPE_EMPTY:
                return new MYTableViewHolder(emptyView);
            case MYViewHolderTypes.TYPE_FOOT:
                if (footView == null) {
                    footView = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_widget_listview_footer, parent, false);
                }

                return new MYDefaultFootViewHolder(footView);
            default:
                return getViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(MYTableViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case MYViewHolderTypes.TYPE_EMPTY:
                LogUtils.info("listview empty, show the default view");
                break;
            case MYViewHolderTypes.TYPE_FOOT:
                MYDefaultFootViewHolder foot_holder = (MYDefaultFootViewHolder) holder;

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
                break;
            default:
                int count = 0;
                for (int n = 0; n < getViewTypeCount(); n++) {
                    if (position >= count && position < count + getCountByType(n)) {
                        holder.type = n;
                        holder.position = position - count;
                        bindContent(holder);
                        break;
                    }
                }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getContentCount() == 0) {
            return MYViewHolderTypes.TYPE_EMPTY;
        }

        return MYViewHolderTypes.TYPE_FOOT;
    }

    @Override
    public int getItemCount() {
        int count = getContentCount();
        return count +
                (emptyView != null && count == 0 ? 1 : 0) +
                (count > 0 && !forceHideFootView ? 1 : 0);
    }

    public int getContentCount() {
        int count = 0;
        for (int n = 0; n < getViewTypeCount(); n++) {
            count += getCountByType(n);
        }

        return count;
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

    public void setListFooterString(String listFooterString) {
        this.listFooterString = listFooterString;
    }

    public String getListFooterString() {
        return listFooterString;
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

    public abstract int getViewTypeCount();

    public abstract int getCountByType(int type_index);

    public abstract MYTableViewHolder getViewHolder(ViewGroup parent, int type_index);

    public abstract void bindContent(MYTableViewHolder holder);

    public abstract void onLoadMore();
}
