package com.zgs.api.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.zgs.api.R;

/**
 * Created by simon on 15-12-4.
 */
public class ActivityUtils {
    public static void startActivity(Activity activity, Class cls, Bundle bundle) {
        Intent intent = new Intent(activity, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivity(intent);

        activity.overridePendingTransition(R.anim.comm_slide_from_right, R.anim.comm_slide_to_left);
    }

    public static void startActivityForResult(Activity activity, Class cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent(activity, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        activity.startActivityForResult(intent, requestCode);

        activity.overridePendingTransition(R.anim.comm_slide_from_right, R.anim.comm_slide_to_left);
    }
}
