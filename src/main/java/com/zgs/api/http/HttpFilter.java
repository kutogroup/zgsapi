package com.zgs.api.http;

import com.zgs.api.base.BaseActivity;

/**
 * Created by simon on 15-11-27.
 */
public abstract class HttpFilter {
    public abstract boolean onResult(BaseActivity activity, int status_code, Object data);
}
