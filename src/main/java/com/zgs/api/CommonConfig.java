package com.zgs.api;

import android.content.Context;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.zgs.api.http.HttpFilter;
import com.zgs.api.utils.FileUtils;
import com.zgs.api.utils.LogUtils;

/**
 * Created by simon on 15-11-25.
 */
public abstract class CommonConfig {
    /**
     * 项目log输出文件名
     */
    public static final String logFileName = "log.txt";

    /**
     * 项目属性存储名
     */
    public static final String preferenceName = "settings";

    /**
     * cache folder
     */
    public static final String cacheFolder = "cache";
    /**
     * 导航图片切换时间间隔
     */
    public static final int NAVI_IMAGE_TIMER = 3000;
    /**
     * 默认列表分页数
     */
    public static final int LIST_PAGE_SIZE = 20;
    /**
     * global context
     */
    public static Context globalContext = null;

    /**
     * http通信秘钥
     */
    public static String httpSecret = "";

    /**
     * http全局筛选器
     */
    public static HttpFilter httpGlobalFilter = null;

    /**
     * init config
     *
     * @param context
     * @param httpSecret
     * @param isDebug
     */
    public static void init(Context context, String httpSecret, HttpFilter httpGlobalFilter, boolean isDebug) {
        globalContext = context;
        CommonConfig.httpSecret = httpSecret;
        CommonConfig.httpGlobalFilter = httpGlobalFilter;

        //设置注释级别
        LogUtils.setLogMode(isDebug ? LogUtils.LOG_TO_CONSOLE : LogUtils.LOG_NULL);

        //设置缓存大小及路径
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(globalContext)
                .setBaseDirectoryPath(FileUtils.getFile(""))
                .setBaseDirectoryName("images")
                .setMaxCacheSize(50 * 1024 * 1024)
                .build();
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig.newBuilder(context)
                .setMainDiskCacheConfig(diskCacheConfig)
                .build();
        Fresco.initialize(context, imagePipelineConfig);
    }
}
