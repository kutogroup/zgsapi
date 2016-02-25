package com.zgs.api.http;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zgs.api.R;
import com.zgs.api.CommonConfig;
import com.zgs.api.utils.LogUtils;

/**
 * Created by simon on 15-12-17.
 */
public abstract class HttpObjectListener<T> implements Response.ErrorListener {
    private JSONObject extraData = null;

    @Override
    public void onErrorResponse(VolleyError error) {
        if (error.networkResponse == null) {
            LogUtils.error(error.getMessage() == null ? "unknown error" : error.getMessage());
            onResult(false, HttpStatusCodes.STATUS_NETWORK_ERROR,
                    CommonConfig.globalContext.getResources().getString(R.string.str_common_network_error),
                    null, 0);
        } else {
            String msg = new String(error.networkResponse.data);
            LogUtils.error("http error = " + msg);
            onResult(false, error.networkResponse.statusCode,
                    msg,
                    null, 0);
        }
    }

    public void onFilter(int code) {
    }

    public abstract void onResult(boolean isCache, int code, String msg, T data, int total);

    public final JSONObject getExtraData() {
        return extraData;
    }

    public final void addExtraData(String key, Object value) {
        if (extraData == null) {
            extraData = new JSONObject();
        }

        extraData.put(key, value);
    }
}
