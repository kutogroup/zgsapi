package com.zgs.api.base;

import android.os.Bundle;

import de.greenrobot.event.EventBus;

/**
 * Created by simon on 16-1-12.
 */
public abstract class BaseEventActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public abstract void onEvent(BaseEvent event);
}
