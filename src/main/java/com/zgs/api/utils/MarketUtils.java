package com.zgs.api.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

/**
 * Created by simon on 16-2-25.
 */
public class MarketUtils {
    public static Intent getIntent(Context paramContext) {
        StringBuilder localStringBuilder = new StringBuilder()
                .append("market://details?id=");
        String str = paramContext.getPackageName();
        localStringBuilder.append(str);
        Uri localUri = Uri.parse(localStringBuilder.toString());
        return new Intent("android.intent.action.VIEW", localUri);
    }

    public static Intent getIntent(String packageName) {
        StringBuilder localStringBuilder = new StringBuilder()
                .append("market://details?id=");
        String str = packageName;
        localStringBuilder.append(str);
        Uri localUri = Uri.parse(localStringBuilder.toString());
        return new Intent("android.intent.action.VIEW", localUri);
    }

    public static void gotoPage(Context context) {
        Intent localIntent = getIntent(context);
        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(localIntent);
    }

    public static void gotoPage(Context context, String packageName) {
        Intent localIntent = getIntent(packageName);
        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(localIntent);
    }

    public static boolean gotoPage(Context context, String shopPackage,
                                   String appPackage) {
        Intent localIntent = getIntent(appPackage);

        ComponentName cn = null;
        List<ResolveInfo> aInfos = context.getPackageManager()
                .queryIntentActivities(localIntent, 0);
        for (ResolveInfo ri : aInfos) {
            if (ri.activityInfo.packageName.equals(shopPackage)) {
                cn = new ComponentName(ri.activityInfo.packageName,
                        ri.activityInfo.name);
                break;
            }
        }

        if (cn == null) {
            return false;
        } else {
            localIntent.setComponent(cn);
        }

        localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(localIntent);

        return true;
    }
}
