package com.zgs.api.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.zgs.api.CommonConfig;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by simon on 15-11-24.
 */
public class SysUtils {
    /**
     * @param context
     * @return system screen width
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * @param context
     * @return system screen height
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels - getStatusbarHeight(context);
    }

    public static float getSystemDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * @param context
     * @return system status bar height
     */
    public static int getStatusbarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * @param context
     * @return get system settings sharedpreferences
     */
    public static SharedPreferences getSystemSettings(Context context) {
        return context.getSharedPreferences(CommonConfig.preferenceName, 0);
    }

    /**
     * @param s      string need to be encoded
     * @param secret secret string
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static String signature(String s, String secret) {
        byte[] bytes = null;

        try {
            String type = "HmacSHA1";
            SecretKeySpec key = new SecretKeySpec(secret.getBytes(), type);
            Mac mac = Mac.getInstance(type);
            mac.init(key);
            bytes = mac.doFinal(s.getBytes());

        } catch (NoSuchAlgorithmException e) {
            LogUtils.error(e);
        } catch (InvalidKeyException e) {
            LogUtils.error(e);
        }

        return bytesToHex(bytes);
    }

    public static Object getMetaData(String key) {
        try {
            ApplicationInfo appInfo = CommonConfig.globalContext.getPackageManager()
                    .getApplicationInfo(CommonConfig.globalContext.getPackageName(),
                            PackageManager.GET_META_DATA);
            return appInfo.metaData.get(key);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.error(e);
        }

        return null;
    }

    public static String getVersionName() {
        try {
            String pkName = CommonConfig.globalContext.getPackageName();
            return CommonConfig.globalContext.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
        } catch (Exception e) {
            LogUtils.error(e);
        }

        return null;
    }

    public static String VariableToName(String var_name) {
        char[] chrs = var_name.toCharArray();
        StringBuilder builder = new StringBuilder();
        builder.append(Character.toLowerCase(chrs[0]));

        for (int n = 1; n < chrs.length; n++) {
            if (Character.isUpperCase(chrs[n])) {
                builder.append('_');
                builder.append(Character.toLowerCase(chrs[n]));
            } else {
                builder.append(chrs[n]);
            }
        }

        return builder.toString();
    }

    public static String toUpperCaseFirstOne(String str) {
        StringBuilder sb = new StringBuilder(str);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    public static String stringToMD5(String data) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(data.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }

        return null;
    }

    public static boolean isChinese(char c) {
//        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
//        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
//                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
//                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
//                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
//                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
//                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
//                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
//            return true;
//        }
        return c >= 0x4E00 && c <= 0x9fA5;
    }

    public static boolean ifStringHasSymbol(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }

        char[] chars = str.toCharArray();

        for (char c : chars) {
            if (!isChinese(c) && !(c >= '0' && c <= '9') && !(c >= 'a' && c <= 'z') && !(c >= 'A' && c <= 'Z')) {
                return true;
            }
        }

        return false;
    }

    public static String getRepeatString(String s, int num) {
        String result = "";
        while (num > 0) {
            result += s;
            num--;
        }

        return result;
    }

    public static String getCardEncryptedString(String card_no) {
        if (card_no == null || card_no.length() < 8) {
            return "";
        }

        char[] chrs = card_no.toCharArray();

        for (int n = 4; n < chrs.length - 4; n++) {
            chrs[n] = '*';
        }

        return new String(chrs);
    }

    // ==================================== private static ============================================
    private final static char[] hexArray = "0123456789abcdef".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        if (bytes == null) {
            return "";
        }

        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
