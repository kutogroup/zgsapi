package com.zgs.api.appwidget.list;

import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.zgs.api.CommonConfig;
import com.zgs.api.appwidget.MYToast;
import com.zgs.api.base.BaseActivity;
import com.zgs.api.http.HttpEngine;
import com.zgs.api.http.HttpObjectListener;
import com.zgs.api.http.HttpObjectRequester;
import com.zgs.api.listener.DoneListener;
import com.zgs.api.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simon on 15-12-17.
 */
public class MYListLoader<T> {
    public ArrayList<T> list = new ArrayList<>();
    public JSONObject src = null;
    public String url = null;
    public Class<T[]> cls;
    public MYListView lv_list;
    public int total = 0;
    public List<DoneListener> listener = new ArrayList<>();
    public int topPageIndex = 0;
    public boolean isCache = false;
    public int errorCode = 0;
    private JSONObject extraData;
    private boolean hideAllError = false;

    public MYListLoader(String url, JSONObject params, Class<T[]> cls, MYListView lv_list) {
        this(false, url, params, cls, lv_list);
    }

    public MYListLoader(boolean isCache, String url, JSONObject params, Class<T[]> cls, MYListView lv_list) {
        this.src = params;
        this.url = url;
        this.cls = cls;
        this.lv_list = lv_list;
        this.isCache = isCache;

        if (src == null) {
            src = new JSONObject();
        }
    }

    public void loadFirstPage() {
        if (list.size() == 0) {
            lv_list.setVisibility(View.INVISIBLE);
        }

        load(1);
    }

    public void loadNextPage() {
        if (total != 0 && total <= topPageIndex * CommonConfig.LIST_PAGE_SIZE) {
            LogUtils.info("list load finish");
            lv_list.loadComplete(true);
            return;
        } else {
            load(topPageIndex + 1);
        }
    }

    public int getPages() {
        return total / CommonConfig.LIST_PAGE_SIZE + (total % CommonConfig.LIST_PAGE_SIZE == 0 ? 0 : 1);
    }

    public void addLoadDoneListener(DoneListener listener) {
        this.listener.add(listener);
    }

    public void load(final int page) {

        JSONObject params = new JSONObject(src);
        params.put("page_no", page);
        params.put("page_size", CommonConfig.LIST_PAGE_SIZE);

        HttpObjectRequester<T[]> requester = new HttpObjectRequester<>((page == 1 && list.size() == 0) ? isCache : false, url,
                params, new HttpObjectListener<T[]>() {
            @Override
            public void onFilter(int code) {
                super.onFilter(code);

                errorCode = code;
                for (DoneListener l : listener) {
                    l.onDone();
                }

                lv_list.setVisibility(View.VISIBLE);
            }

            @Override
            public void onResult(boolean isCache, int code, String msg, T[] data, int total) {
                if (code == 0) {
                    extraData = getExtraData();

                    if (page == 1) {
                        list.clear();
                        topPageIndex = page;
                    }

                    if (page == topPageIndex + 1) {
                        topPageIndex = page;
                    }

                    MYListLoader.this.total = total;
                    if (data != null) {
                        if (page < list.size() / CommonConfig.LIST_PAGE_SIZE) {
                            //更新
                            for (int n = 0; n < data.length; n++) {
                                list.set(page * CommonConfig.LIST_PAGE_SIZE + n, data[n]);
                            }
                        } else {
                            //新增
                            for (int n = 0; n < data.length; n++) {
                                list.add(data[n]);
                            }
                        }
                    }

                    if (!isCache && listener != null) {
                        //非缓存才会执行
                        for (DoneListener l : listener) {
                            l.onDone();
                        }
                    }

                    if (getPages() <= 1) {
                        ((MYDefaultAdapter) lv_list.getAdapter()).forceHideFootView = true;
                    } else {
                        ((MYDefaultAdapter) lv_list.getAdapter()).forceHideFootView = false;
                    }

                    if (list.size() == total) {
                        lv_list.loadComplete(true);
                    } else {
                        lv_list.loadComplete(false);
                    }
                } else {
                    if (!hideAllError) {
                        MYToast.show(msg);
                    }

                    errorCode = code;
                    for (DoneListener l : listener) {
                        l.onDone();
                    }
                }

                lv_list.setVisibility(View.VISIBLE);
            }
        }, cls);

        HttpEngine.getInstance().addRequest(requester, (BaseActivity) lv_list.getContext(), hideAllError ? null : CommonConfig.httpGlobalFilter);
    }

    public JSONObject getExtraData() {
        return extraData;
    }

    public int getTotalCount() {
        return total;
    }

    public ArrayList<T> getList() {
        return list;
    }

    public void setHideAllError(boolean hideAllError) {
        this.hideAllError = hideAllError;
    }
}
