package com.zgs.api.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.zgs.api.http.HttpEngine;

/**
 * Created by simon on 15-11-24.
 */
public class BaseService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * @return get instance of current service
     */
    public Service getService(){
        return BaseService.this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        HttpEngine.getInstance().cancelAll(this);
    }
}
