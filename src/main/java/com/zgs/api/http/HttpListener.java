package com.zgs.api.http;

import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.zgs.api.R;
import com.zgs.api.CommonConfig;
import com.zgs.api.utils.LogUtils;

/**
 * Created by simon on 15-11-30.
 */
public abstract class HttpListener<T> implements Response.ErrorListener {
    @Override
    public void onErrorResponse(VolleyError error) {
        if (error.networkResponse == null) {
            if (error instanceof TimeoutError) {
                LogUtils.error("http timeout");
            } else {
                LogUtils.error(error.getMessage() == null ? "unknown error" : error.getMessage());
            }

            onResult(false, HttpStatusCodes.STATUS_NETWORK_ERROR,
                    CommonConfig.globalContext.getResources().getString(R.string.str_common_network_error),
                    null);
        } else {
            String msg = new String(error.networkResponse.data);
            LogUtils.error("http error = " + msg);
            onResult(false, error.networkResponse.statusCode,
                    msg,
                    null);
        }
    }

    public void onFilter(int code) {
    }

    public abstract void onResult(boolean isCache, int code, String msg, T data);
}
