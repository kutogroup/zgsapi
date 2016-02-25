package com.zgs.api.storage;

import android.content.SharedPreferences;

import com.zgs.api.CommonConfig;
import com.zgs.api.utils.LogUtils;

import java.util.Set;

/**
 * Created by simon on 15-11-27.
 */
public class StorageApi {
    public static void save(String key, Object value) {
        if (key == null) {
            LogUtils.error("save params absent.");
            return;
        }

        SharedPreferences sp = getStorage();
        if (value == null) {
            sp.edit().remove(key).apply();
            return;
        }

        if (value instanceof String) {
            sp.edit().putString(key, (String) value).apply();
        } else if (value instanceof Boolean) {
            sp.edit().putBoolean(key, (Boolean) value).apply();
        } else if (value instanceof Float) {
            sp.edit().putFloat(key, (Float) value).apply();
        } else if (value instanceof Integer) {
            sp.edit().putInt(key, (Integer) value).apply();
        } else if (value instanceof Long) {
            sp.edit().putLong(key, (Long) value).apply();
        } else if (value instanceof Set) {
            sp.edit().putStringSet(key, (Set<String>) value).apply();
        }
    }

    public static SharedPreferences getStorage() {
        return CommonConfig.globalContext.getSharedPreferences(CommonConfig.preferenceName, 0);
    }

    public static void clearAll() {
        StorageApi.getStorage().edit().clear().commit();
    }

    public static int getInt(String key, int default_value) {
        return getStorage().getInt(key, default_value);
    }

    public static int getInt(String key) {
        return getStorage().getInt(key, 0);
    }

    public static String getString(String key, String default_value) {
        return getStorage().getString(key, default_value);
    }

    public static String getString(String key) {
        return getStorage().getString(key, null);
    }

    public static float getFloat(String key, float default_value) {
        return getStorage().getFloat(key, default_value);
    }

    public static float getFloat(String key) {
        return getStorage().getFloat(key, 0);
    }

    public static boolean getBoolean(String key, boolean default_value) {
        return getStorage().getBoolean(key, default_value);
    }

    public static boolean getBoolean(String key) {
        return getStorage().getBoolean(key, false);
    }

    public static Long getLong(String key, Long default_value) {
        return getStorage().getLong(key, default_value);
    }

    public static Long getLong(String key) {
        return getStorage().getLong(key, 0);
    }

    public static Set<String> getSet(String key) {
        return getStorage().getStringSet(key, null);
    }
}
