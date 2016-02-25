package com.zgs.api.appwidget;

import android.widget.Toast;

import com.zgs.api.CommonConfig;

/**
 * Created by simon on 15-11-30.
 */
public class MYToast {
    public static void show(String msg) {
        if (msg != null) {
            Toast.makeText(CommonConfig.globalContext, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
