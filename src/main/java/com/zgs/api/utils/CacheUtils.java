package com.zgs.api.utils;

import com.zgs.api.CommonConfig;

import java.io.File;

/**
 * Created by simon on 1/4/16.
 */
public class CacheUtils {
    private static CacheUtils cacheInstance = null;

    public static CacheUtils getInstance() {
        if (cacheInstance == null) {
            cacheInstance = new CacheUtils();
        }

        if (!FileUtils.getFile(CommonConfig.cacheFolder).exists()) {
            FileUtils.getFile(CommonConfig.cacheFolder).mkdir();
        }

        return cacheInstance;
    }

    public void saveCacheByKey(String key, String value) {
        if (key == null || key.length() == 0) {
            LogUtils.error("md5 key is null");
            return;
        }

        String cacheName = SysUtils.stringToMD5(key);
        File cache = FileUtils.getFile(CommonConfig.cacheFolder + File.separator + cacheName);
        FileUtils.copyStringToFile(value, cache);
    }

    public String getCacheByKey(String key) {
        if (key == null || key.length() == 0) {
            LogUtils.error("md5 key is null");
            return null;
        }

        String cacheName = SysUtils.stringToMD5(key);
        File cache = FileUtils.getFile(CommonConfig.cacheFolder + File.separator + cacheName);
        if (!cache.exists()) {
            return null;
        }

        return FileUtils.getStringFromFile(cache);
    }

    public void clearAll() {
        File cache = FileUtils.getFile(CommonConfig.cacheFolder);
        FileUtils.delete(cache);
    }
}
