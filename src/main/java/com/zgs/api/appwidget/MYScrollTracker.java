package com.zgs.api.appwidget;

import android.view.MotionEvent;
import android.view.VelocityTracker;

/**
 * Created by simon on 15-12-10.
 */
public class MYScrollTracker {
    VelocityTracker mTracker;

    public void addTracker(MotionEvent event) {
        if (mTracker == null) {
            mTracker = VelocityTracker.obtain();
        }

        mTracker.addMovement(event);
    }

    public void recycleTracker() {
        if (mTracker != null) {
            mTracker.recycle();
            mTracker = null;
        }
    }

    public int getScrollVelocity(boolean ifx) {
        if (mTracker == null) {
            return 0;
        }

        mTracker.computeCurrentVelocity(1000);
        if (ifx) {
            return (int) mTracker.getXVelocity();
        } else {
            return (int) mTracker.getYVelocity();
        }
    }
}
