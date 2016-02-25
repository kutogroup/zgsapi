package com.zgs.api.utils;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.zgs.api.CommonConfig;

/**
 * Created by simon on 15-12-14.
 */
public class ResourceUtils {
    public static int getColor(int resid) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return CommonConfig.globalContext.getResources().getColor(resid, null);
        } else {
            return CommonConfig.globalContext.getResources().getColor(resid);
        }
    }

    public static ColorStateList getColorStateList(int resid) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return CommonConfig.globalContext.getResources().getColorStateList(resid, null);
        } else {
            return CommonConfig.globalContext.getResources().getColorStateList(resid);
        }
    }

    public static Drawable getDrawable(int resid) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return CommonConfig.globalContext.getResources().getDrawable(resid, null);
        } else {
            return CommonConfig.globalContext.getResources().getDrawable(resid);
        }
    }

    public static Drawable getDrawableWithDefaultBound(int resid) {
        Drawable drawable = getDrawable(resid);

        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }

        return drawable;
    }

    public static String getString(int resid) {
        return CommonConfig.globalContext.getResources().getString(resid);
    }
}
