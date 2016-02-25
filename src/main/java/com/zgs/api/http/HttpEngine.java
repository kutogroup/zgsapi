package com.zgs.api.http;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.zgs.api.CommonConfig;
import com.zgs.api.base.BaseActivity;
import com.zgs.api.base.BaseFragment;

/**
 * Created by simon on 15-11-25.
 */
public class HttpEngine {
    private static HttpEngine meEngine = null;
    private static RequestQueue requestsQueue;

    public static HttpEngine getInstance() {
        if (meEngine == null) {
            meEngine = new HttpEngine();
            requestsQueue = Volley.newRequestQueue(CommonConfig.globalContext);
        }

        return meEngine;
    }

    /**
     * @param requester
     * @param tag       add request to queue
     */
    public void addRequest(HttpBaseRequester requester, BaseActivity tag, HttpFilter filter) {
        requester.setTag(tag);
        requester.setFilter(filter);
        requestsQueue.add(requester);
    }

    /**
     * @param requester
     * @param tag       add request to queue
     */
    public void addRequest(HttpBaseRequester requester, BaseFragment tag, HttpFilter filter) {
        requester.setTag(tag);
        requester.setFilter(filter);
        requestsQueue.add(requester);
    }

    /**
     * @param requester
     * @param tag       add request to queue
     */
    public void addRequest(HttpBaseRequester requester, BaseActivity tag) {
        if (tag == null) {
            throw new IllegalArgumentException("http activity null");
        }

        requester.setTag(tag);
        requester.setFilter(CommonConfig.httpGlobalFilter);
        requestsQueue.add(requester);
    }

    /**
     * @param requester
     * @param tag       add request to queue
     */
    public void addRequest(HttpBaseRequester requester, BaseFragment tag) {
        if (tag == null) {
            throw new IllegalArgumentException("http fragment null");
        }

        requester.setTag(tag);
        requester.setFilter(CommonConfig.httpGlobalFilter);
        requestsQueue.add(requester);
    }

    /**
     * @param tag cancel all request
     */
    public void cancelAll(Object tag) {
        requestsQueue.cancelAll(tag);
    }
}
