package com.zgs.api.http;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.zgs.api.CommonConfig;
import com.zgs.api.base.BaseActivity;
import com.zgs.api.base.BaseFragment;
import com.zgs.api.utils.LogUtils;
import com.zgs.api.utils.SysUtils;

/**
 * Created by simon on 15-12-22.
 */
public class HttpFileUploader extends HttpBaseRequester {
    private static final String twoHyphens = "--";
    private static final String lineEnd = "\r\n";
    private static final String boundary = "maiyamall-" + System.currentTimeMillis();
    private static final String mimeType = "multipart/form-data; boundary=" + boundary + "; charset=UTF-8";

    private final HttpListener<String> listener;
    private final byte[] multipartBody;

    private URL url = null;

    public HttpFileUploader(String url, final File file, HttpListener<String> listener) {
        this(url, buildFileBody(new HashMap<String, File>() {
            {
                put("image", file);
            }
        }), listener);
    }

    public HttpFileUploader(String url, byte[] multipartBody, HttpListener<String> listener) {
        super(Method.POST, url, listener);
        this.listener = listener;
        this.multipartBody = multipartBody;

        try {
            this.url = new URL(getUrl());
        } catch (MalformedURLException e) {
            this.url = null;
            cancel();

            LogUtils.error("wrong format url");
            return;
        }

        //时间设长点，防止重复提交
        setRetryPolicy(new DefaultRetryPolicy(
                15000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>(super.getHeaders());

        String app_secret = CommonConfig.httpSecret;
        if (app_secret == null) {
            LogUtils.error("app_secret not found");
            return headers;
        }

        String queries = url.getQuery();

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
    public String getBodyContentType() {
        return mimeType;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return multipartBody;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String parsed;
            try {
                parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            } catch (UnsupportedEncodingException e) {
                parsed = new String(response.data);
            }

            JSONObject jsonObject = null;
            try {
                jsonObject = JSON.parseObject(parsed);
            } catch (JSONException e) {
                LogUtils.error(e);
                Response.error(new VolleyError("wrong format"));
            }

            return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        LogUtils.info("http response: " + response);
        int statusCode = response.getInteger("code");

        BaseActivity activity;
        if (getTag() instanceof BaseActivity) {
            activity = (BaseActivity) getTag();
        } else if (getTag() instanceof BaseFragment) {
            activity = (BaseActivity) ((BaseFragment) getTag()).getActivity();
        } else {
            throw new IllegalArgumentException("illegal tag");
        }

        if (getFilter() != null && getFilter().onResult(activity, statusCode, response)) {
            if (listener != null) {
                listener.onFilter(statusCode);
            }
            return;
        }

        if (listener != null) {
            if (statusCode == 0) {
                listener.onResult(false, statusCode, response.getString("message"), response.getJSONObject("data").getString("image_url"));
            } else {
                listener.onResult(false, statusCode, response.getString("message"), null);
            }
        }
    }

    public static byte[] buildFileBody(HashMap<String, File> files) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try {
            for (String key : files.keySet()) {
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"image\"; filename=\""
                        + key + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                FileInputStream fis = new FileInputStream(files.get(key));
                int bytesAvailable = fis.available();

                int maxBufferSize = 1024 * 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];

                int bytesRead = fis.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fis.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fis.read(buffer, 0, bufferSize);
                }

                dos.writeBytes(lineEnd);
                fis.close();
            }

            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            return bos.toByteArray();
        } catch (Exception e) {
            LogUtils.error(e);
        }

        return null;
    }
}
