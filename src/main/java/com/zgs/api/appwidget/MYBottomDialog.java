package com.zgs.api.appwidget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import com.zgs.api.R;
import com.zgs.api.utils.ViewUtils;

/**
 * Created by simon on 15-12-8.
 */
public class MYBottomDialog implements Runnable {
    public PopupWindow popupWindow = null;
    private boolean isShow = false;
    private View bottomLayout = null;

    public MYBottomDialog(Context context, int layoutID, int bottom_layout_id) {
        this(LayoutInflater.from(context).inflate(layoutID, ViewUtils.getRootView((Activity) context), false));

        if (bottom_layout_id == 0) {
            bottomLayout = popupWindow.getContentView();
        } else {
            bottomLayout = popupWindow.getContentView().findViewById(bottom_layout_id);
        }
    }

    public MYBottomDialog(View v) {
        popupWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x88000000));
    }

    public View getContentView() {
        return popupWindow.getContentView();
    }

    public void show() {
        AnimationSet anim = (AnimationSet) AnimationUtils.loadAnimation(bottomLayout.getContext(), R.anim.comm_slide_in_top);
        anim.setFillAfter(true);
        bottomLayout.startAnimation(anim);

        popupWindow.showAtLocation(ViewUtils.getRootView((Activity) bottomLayout.getContext()), Gravity.BOTTOM, 0, 0);
        isShow = true;
    }

    public void hide() {
        if (popupWindow != null) {
            AnimationSet anim = (AnimationSet) AnimationUtils.loadAnimation(bottomLayout.getContext(), R.anim.comm_slide_out_bottom);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    popupWindow.getContentView().post(MYBottomDialog.this);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            bottomLayout.startAnimation(anim);
        }

        isShow = false;
    }

    public boolean isShow() {
        return isShow;
    }

    @Override
    public void run() {
        popupWindow.dismiss();
    }
}
