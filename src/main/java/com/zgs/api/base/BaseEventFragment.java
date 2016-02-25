package com.zgs.api.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import de.greenrobot.event.EventBus;

/**
 * Created by simon on 16-1-12.
 */
public abstract class BaseEventFragment extends BaseFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public abstract void onEvent(BaseEvent event);
}
