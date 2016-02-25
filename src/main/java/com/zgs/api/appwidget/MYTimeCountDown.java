package com.zgs.api.appwidget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

import com.zgs.api.utils.TimeUtils;

/**
 * Created by simon on 15-12-1.
 */
public class MYTimeCountDown extends TextView {
    /**
     * expire millis time
     */
    private Long expireTime = 0l;

    /**
     * time ticks handler
     */
    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            setTextByTime();
            timeHandler.sendEmptyMessageDelayed(0, 1000);
        }
    };

    public MYTimeCountDown(Context context) {
        this(context, null, 0);
    }

    public MYTimeCountDown(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MYTimeCountDown(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setText("");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        timeHandler.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        timeHandler.removeMessages(0);
    }


    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;

        setTextByTime();
    }

    private void setTextByTime() {
        long offset = expireTime - System.currentTimeMillis();

        if (offset < 0) {
            setText("00 : 00 : 00");
        } else {
            setText(TimeUtils.getTimeOffsetStringByMills(offset).replaceAll(":", " : "));
        }
    }
}
