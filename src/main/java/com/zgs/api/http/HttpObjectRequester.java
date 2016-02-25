package com.zgs.api.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.zgs.api.CommonConfig;
import com.zgs.api.base.BaseActivity;
import com.zgs.api.base.BaseFragment;
import com.zgs.api.utils.LogUtils;
import com.zgs.api.utils.SysUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by simon on 15-11-26.
 */
public class HttpObjectRequester<T> extends HttpBaseRequester {
    private HttpObjectListener<T> listener = null;
    private JSONObject params = null;
    private Class<T> cls = null;

    /**
     * @param url
     * @param params
     * @param listener
     */
    public HttpObjectRequester(String url, JSONObject params, HttpObjectListener<T> listener, Class<T> cls) {
        this(false, url, params, listener, cls);
    }

    /**
     * @param cached,  false: always download data from server;  true: get data from cache if exists
     * @param url
     * @param params
     * @param listener
     */
    public HttpObjectRequester(boolean cached, String url, JSONObject params, HttpObjectListener<T> listener, Class<T> cls) {
        super(cached, Method.POST, url + String.format("&lang=cn&format=json&timestamp=%s&appid=%s",
                String.valueOf(System.currentTimeMillis()), SysUtils.getMetaData("app_id")), listener);

        this.listener = listener;
        this.params = params != null ? params : new JSONObject();
        this.cls = cls;

        LogUtils.info(String.format("http request:url=%s\r\nparams=%s", url, params == null ? "" : params.toJSONString()));
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return params.toJSONString().getBytes();
    }

    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>(super.getHeaders());

        String app_secret = CommonConfig.httpSecret;
        if (app_secret == null) {
            LogUtils.error("app_secret not found");
            return headers;
        }

        String queries = getFormattedUrl().getQuery();

        TreeMap<String, String> map = new TreeMap<>();

        String[] sub_queries = queries.split("&");
        for (String sub : sub_queries) {
            String[] kv = sub.split("=");

            if (kv == null || kv.length != 2) {
                return headers;
            }

            map.put(kv[0], kv[1]);
        }

        String sorted = "", key;
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            key = (String) it.next();
            sorted = sorted + key + "=" + map.get(key) + "&";
        }

        sorted = sorted.substring(0, sorted.length() - 1);
        headers.put("SIGNATURE", SysUtils.signature(sorted, app_secret));

        return headers;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = JSON.parseObject(parsed);
            LogUtils.json(jsonObject);
        } catch (JSONException e) {
            LogUtils.error(e);
            Response.error(new VolleyError("wrong format"));
        }

        Cache.Entry entry;

        entry = HttpHeaderParser.parseCacheHeaders(response);

        Response result = Response.success(jsonObject, entry);
        jsonObject.put(INTERMEDIATE_KEY, false);
        return result;
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        int statusCode = response.getInteger("code");
        boolean intermediate = false;

        if (response.containsKey(INTERMEDIATE_KEY)) {
            intermediate = response.getBoolean(INTERMEDIATE_KEY);
        }

        BaseActivity activity;
        if (getTag() instanceof BaseActivity) {
            activity = (BaseActivity) getTag();
        } else if (getTag() instanceof BaseFragment) {
            activity = (BaseActivity) ((BaseFragment) getTag()).getActivity();
        } else {
            throw new IllegalArgumentException("illegal tag");
        }

        if (!intermediate &&
                getFilter() != null && getFilter().onResult(activity, statusCode, response)) {
            if (listener != null) {
                listener.onFilter(statusCode);
            }
            return;
        }

        if (intermediate && statusCode != 0) {
            //如果连接不成功的cache，则不处理
            return;
        }

        if (isCached() && !intermediate && statusCode == 0) {
            //不缓存错误信息
            saveCache(response);
        }

        if (listener != null) {
            JSONObject json = response.getJSONObject("data");
            T obj = null;
            int total = 0;
            if (statusCode == 0) {
                if (json.containsKey("total")) {
                    total = json.getInteger("total");
                    if (total != 0) {
                        for (String key : json.keySet()) {
                            if (key.equals("total")) {
                                continue;
                            } else {
                                if (cls.isArray() && !(json.get(key) instanceof JSONArray)) {
                                    listener.addExtraData(key, json.get(key));
                                    continue;
                                }

                                obj = json.getObject(key, cls);
                            }
                        }
                    }
                } else {
                    if (cls.isArray()) {
                        Set<String> keys = json.keySet();
                        String[] keys_str = keys.toArray(new String[keys.size()]);
                        obj = json.getObject(keys_str[0], cls);
                        total = json.getJSONArray(keys_str[0]) == null ? 0 : json.getJSONArray(keys_str[0]).size();
                    } else {
                        obj = response.getObject("data", cls);
                    }
                }
            }

            listener.onResult(intermediate, statusCode, response.getString("message"), obj, total);
        }
    }

    @Override
    public void cancel() {
        if (!isCanceled()) {
            super.cancel();
        }
    }
}
