package com.zgs.api.appwidget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;

import com.zgs.api.listener.DoneListener;

/**
 * Created by simon on 15-12-10.
 */
public class MYDeleteWidget implements Runnable {
    private View deleteView;
    private DoneListener deleteListener;
    private int viewHeight;
    private int srcHeight = 0;

    public MYDeleteWidget(View v, DoneListener listener) {
        deleteView = v;
        deleteListener = listener;
    }

    public void setViewHeight(int height) {
        viewHeight = height;
        ViewGroup.LayoutParams vlp = deleteView.getLayoutParams();
        vlp.height = height;
        deleteView.setLayoutParams(vlp);
    }

    public void startDelete() {
        srcHeight = deleteView.getHeight();

        ObjectAnimator anim = ObjectAnimator.ofInt(this, "viewHeight", deleteView.getHeight(), 0);
        anim.setDuration(200);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (deleteListener != null) {
                    deleteListener.onDone();
                    resetHeight();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        anim.start();
    }

    public void resetHeight() {
        setViewHeight(srcHeight);
    }

    @Override
    public void run() {

    }
}
