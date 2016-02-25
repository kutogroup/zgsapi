package com.zgs.api.utils;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;

/**
 * Created by simon on 15-11-30.
 */
public class ViewUtils {
    public static boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())) {
            return false;
        }

        return true;
    }

    public static <T> T searchViewByClass(ViewGroup layout, Class<T> type) {
        if (layout == null || type == null) {
            return null;
        }

        T child, result;
        for (int n = 0; n < layout.getChildCount(); n++) {
            child = (T) layout.getChildAt(n);

            if (child.getClass() == type) {
                return child;
            }

            if (child instanceof ViewGroup) {
                result = searchViewByClass((ViewGroup) child, type);

                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    public static <T> ArrayList<T> searchViewsByClass(ViewGroup layout, Class<T> type) {
        if (layout == null || type == null) {
            return null;
        }

        ArrayList<T> viewList = new ArrayList<>();
        View child;
        for (int n = 0; n < layout.getChildCount(); n++) {
            child = layout.getChildAt(n);

            if (child.getClass() == type) {
                viewList.add((T) child);
                continue;
            }

            if (child instanceof ViewGroup) {
                for (T v : searchViewsByClass((ViewGroup) child, type)) {
                    viewList.add((T) v);
                }
            }
        }

        return viewList;
    }

    public static <T> T searchParentViewByClass(View view, Class<T> type) {
        if (view == null || type == null) {
            return null;
        }

        ViewParent parent = view.getParent();

        while (parent != null) {
            if (parent.getClass() == type) {
                return (T) parent;
            }

            parent = parent.getParent();
        }

        return null;
    }

    public static <T> T searchViewByClass(Activity activity, Class<T> type) {
        if (activity == null || type == null) {
            return null;
        }

        ViewGroup layout = getRootView(activity);

        T child, result;
        for (int n = 0; n < layout.getChildCount(); n++) {
            child = (T) layout.getChildAt(n);

            if (child.getClass() == type) {
                return child;
            }

            if (child instanceof ViewGroup) {
                result = searchViewByClass((ViewGroup) child, type);

                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    public static ViewGroup getRootView(Activity activity) {
        return (ViewGroup) activity.getWindow().getDecorView().getRootView();
    }

    public static int dp2px(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
