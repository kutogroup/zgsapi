package com.zgs.api.listener;

import android.view.View;

/**
 * Created by simon on 15-12-23.
 */
public interface DataListener<T> {
    void onData(View view, T value);
}
