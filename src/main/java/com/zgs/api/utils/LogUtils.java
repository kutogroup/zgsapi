package com.zgs.api.utils;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zgs.api.CommonConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by simon on 15-11-24.
 */
public class LogUtils {
    public static final int LOG_NULL = 0;
    public static final int LOG_TO_CONSOLE = 1;
    public static final int LOG_TO_FILE = LOG_TO_CONSOLE << 1;
    public static final int LOG_ALL = (LOG_TO_CONSOLE | LOG_TO_FILE);

    /**
     * log mode, 0: log to console, 1: log to console and file, 2: no log
     */
    public static int defaultLogMode = LOG_TO_CONSOLE;

    /**
     * instance of log file
     */
    private static File logFile = null;

    /**
     * log time format
     */
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

    /**
     * @param mode set log mode
     */
    public static void setLogMode(final int mode) {
        defaultLogMode = mode;

        if ((defaultLogMode & LOG_TO_FILE) > 0) {
            logFile = FileUtils.getFile(CommonConfig.logFileName);
        }
    }

    /**
     * @param tag
     * @param msg log info message
     */
    public static void info(String tag, String msg) {
        if (defaultLogMode > 0) {
            Log.i("==!== " + tag + " ==!==", msg);

            if ((defaultLogMode & LOG_TO_FILE) > 0) {
                log2File(tag, msg);
            }

        }
    }

    /**
     * @param tag
     * @param msg log error message
     */
    public static void error(String tag, String msg) {
        if (defaultLogMode > 0) {
            Log.e("==!== " + tag + " ==!==", msg);

            if ((defaultLogMode & LOG_TO_FILE) > 0) {
                log2File(tag, msg);
            }

        }
    }

    /**
     * @param json log json message
     */
    public static void json(JSON json) {
        if (json == null) {
            return;
        }

        info("json", getJSONLogString(json, 1));
    }

    private static String getJSONLogString(JSON json, int level) {
        String lineExt = SysUtils.getRepeatString("    ", level);
        String result = "";

        if (json != null) {
            if (json instanceof JSONArray) {
                result += lineExt + "[\r\n";

                for (int n = 0; n < ((JSONArray) json).size(); n++) {
                    Object value = ((JSONArray) json).get(n);

                    if (value instanceof JSON) {
                        result += lineExt + "    \r\n" + getJSONLogString((JSON) value, level + 1) + ",\r\n";
                    } else if (value instanceof String) {
                        result += lineExt + "    \"" + value + "\",\r\n";
                    } else {
                        result += lineExt + "    " + value + ",\r\n";
                    }
                }

                result += lineExt + "]";
            } else if (json instanceof JSONObject) {
                result += lineExt + "{\r\n";

                for (String key : ((JSONObject) json).keySet()) {
                    result += lineExt + "    \"" + key + "\": ";
                    Object value = ((JSONObject) json).get(key);

                    if (value instanceof JSON) {
                        result += lineExt + "    \r\n" + getJSONLogString((JSON) value, level + 1) + ",\r\n";
                    } else if (value instanceof String) {
                        result += "\"" + value + "\",\r\n";
                    } else {
                        result += value + ",\r\n";
                    }
                }

                result += lineExt + "}";
            }
        }

        return result;
    }

    /**
     * @param msg log info message
     */
    public static void info(String msg) {
        if (defaultLogMode > 0) {
            Log.i("==!== " + getCallerClassName() + " ==!==", msg);

            if ((defaultLogMode & LOG_TO_FILE) > 0) {
                log2File(getCallerClassName(), "info: " + msg);
            }
        }
    }

    /**
     * @param msg log error message
     */
    public static void error(String msg) {
        if (defaultLogMode > 0) {
            Log.e("==!== " + getCallerClassName() + " ==!==", msg);

            if ((defaultLogMode & LOG_TO_FILE) > 0) {
                log2File(getCallerClassName(), "normal error: " + msg);
            }
        }
    }

    /**
     * @param e log error exception
     */
    public static void error(Throwable e) {
        if (defaultLogMode > 0) {
            String msg = exceptionToString(e);
            Log.e("==!== " + getCallerClassName() + " ==!==", msg);

            if ((defaultLogMode & LOG_TO_FILE) > 0) {
                log2File(getCallerClassName(), "throwable error: " + msg);
            }
        }
    }

    private static String getCallerClassName() {
        StackTraceElement ele = new Throwable().getStackTrace()[2];
        return ele.getClassName() + ",lineno=" + ele.getLineNumber();
    }

    /**
     * @param tag
     * @param msg log to file
     */
    private static void log2File(String tag, String msg) {
        FileUtils.appendFile(logFile, format.format(new Date()) + " ==!== " + tag + " ==!==: " + msg + "\r\n");
    }

    /**
     * @param t
     * @return
     * @throws IOException exception to string
     */
    private static String exceptionToString(Throwable t) {
        if (t == null)
            return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            t.printStackTrace(new PrintStream(baos));
        } finally {
            try {
                baos.close();
            } catch (IOException e) {

            }
        }

        return baos.toString();
    }
}
