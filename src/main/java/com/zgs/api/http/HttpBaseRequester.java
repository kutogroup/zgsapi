package com.zgs.api.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zgs.api.utils.CacheUtils;
import com.zgs.api.utils.LogUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by simon on 15-12-11.
 */
public abstract class HttpBaseRequester extends Request<JSONObject> {
    public final String INTERMEDIATE_KEY = "intermediate";

    private HttpFilter filter;
    private URL formattedUrl;
    private boolean cached = false;

    public HttpBaseRequester(int method, String url, Response.ErrorListener listener) {
        this(false, method, url, listener);

        try {
            this.formattedUrl = new URL(getUrl());
        } catch (MalformedURLException e) {
            this.formattedUrl = null;
            cancel();

            if (listener != null) {
                listener.onErrorResponse(new VolleyError("wrong format url"));
            }
            LogUtils.error("wrong format url");
            return;
        }
    }

    public HttpBaseRequester(boolean cached, int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);

        this.cached = cached;

        try {
            this.formattedUrl = new URL(getUrl());
        } catch (MalformedURLException e) {
            this.formattedUrl = null;
            cancel();

            if (listener != null) {
                listener.onErrorResponse(new VolleyError("wrong format url"));
            }
            LogUtils.error("wrong format url");
            return;
        }
    }

    @Override
    public final Request<?> setRequestQueue(RequestQueue requestQueue) {
        if (cached) {
            String cache = CacheUtils.getInstance().getCacheByKey(getCacheKey());
            if (cache != null) {
                JSONObject obj = JSON.parseObject(cache);
                obj.put(INTERMEDIATE_KEY, true);

                try {
                    deliverResponse(obj);
                } catch (Exception e) {
                    //缓存引发错误则自动清除缓存
                    LogUtils.error("cache response error");
                    CacheUtils.getInstance().clearAll();
                }
            }
        }

        return super.setRequestQueue(requestQueue);
    }

    public final void saveCache(JSONObject cache) {
        CacheUtils.getInstance().saveCacheByKey(getCacheKey(), cache.toJSONString());
    }

    public void setFilter(HttpFilter filter) {
        this.filter = filter;
    }

    public HttpFilter getFilter() {
        return filter;
    }

    public String getCacheKey() {
        String queries = formattedUrl.getQuery();
        String key = null;

        String[] sub_queries = queries.split("&");
        for (String sub : sub_queries) {
            String[] kv = sub.split("=");

            if ("method".equals(kv[0])) {
                key = kv[1];
                break;
            }
        }

        if (key != null) {
            try {
                key = key + new String(getBody());
            } catch (AuthFailureError authFailureError) {
                key = null;
            }
        }

        return key;
    }

    public URL getFormattedUrl() {
        return formattedUrl;
    }

    public boolean isCached() {
        return cached;
    }
}
