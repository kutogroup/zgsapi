package com.zgs.api.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by simon on 15-11-24.
 */
public class BaseReceiver extends BroadcastReceiver{
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
    }

    /**
     * @return get context of receiver
     */
    public Context getContext(){
        return context;
    }
}
