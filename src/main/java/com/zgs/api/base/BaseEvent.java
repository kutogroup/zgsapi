package com.zgs.api.base;

import android.os.Bundle;

/**
 * Created by simon on 15-12-5.
 */
public class BaseEvent {
    public int id;
    public Object other;
    public Bundle bundle;

    public BaseEvent(int id, Bundle bundle) {
        this.bundle = bundle;
        this.id = id;
    }

    public BaseEvent(int id, Object obj) {
        this.other = obj;
        this.id = id;
    }
}
