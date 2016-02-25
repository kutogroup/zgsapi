package com.zgs.api.utils;

import android.content.Context;

/**
 * Created by simon on 15-12-1.
 */
public class TimeUtils {
    public static String getTimeOffsetStringByMills(long mills) {
        if (mills < 0) {
            return "00:00:00";
        }

        long onehour = 1000 * 60 * 60;

        long days = mills / (onehour * 24);
        long hours = (mills - days * (onehour * 24)) / onehour;
        long minutes = (mills - days * (onehour * 24) - hours * onehour) / (1000 * 60);
        long sec = (mills - days * (onehour * 24) - hours * onehour - minutes * (1000 * 60)) / 1000;

        return String.format("%02d:%02d:%02d",
                days * 24 + hours, minutes, sec);
    }

    /**
     * @param context
     * @param key
     * @return check if key time is expired
     */
    public static boolean isKeyTimeExpired(Context context, String key) {
        if (System.currentTimeMillis() > SysUtils.getSystemSettings(context).getLong(key, 0l)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param context
     * @param key     store key time by system time
     */
    public static void storeKeyTime(Context context, String key) {
        SysUtils.getSystemSettings(context).edit().putLong(key, System.currentTimeMillis()).commit();
    }

    /**
     * @param context
     * @param key
     * @param time    store key time by custom time
     */
    public static void storeKeyTime(Context context, String key, long time) {
        SysUtils.getSystemSettings(context).edit().putLong(key, time).commit();
    }
}
