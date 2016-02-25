package com.zgs.api.appwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.zgs.api.R;

/**
 * Created by simon on 15-12-2.
 */
public class MYPagerCircleIndicator extends View implements ViewTreeObserver.OnGlobalLayoutListener {
    /**
     * default color of circle
     */
    private int defaultColor = Color.WHITE;
    /**
     * default gap between circle
     */
    private int defaultGap;
    /**
     * default paint
     */
    private Paint defaultPaint = new Paint();
    /**
     * circle count
     */
    private int circleCount = 0;
    /**
     * index of selected item
     */
    private int currentIndex = 0;

    public MYPagerCircleIndicator(Context context) {
        this(context, null, 0);
    }

    public MYPagerCircleIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MYPagerCircleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setWillNotDraw(false);

        defaultGap = (int) getResources().getDimension(R.dimen.margin_1);

        TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.MYPagerCirCleIndicator);
        for (int n = 0; n < types.getIndexCount(); n++) {
            int attr = types.getIndex(n);

            if (attr == R.styleable.MYPagerCirCleIndicator_color) {
                defaultColor = types.getColor(attr, defaultColor);
            } else if (attr == R.styleable.MYPagerCirCleIndicator_gap) {
                defaultGap = types.getInt(attr, defaultGap);
            }
        }

        types.recycle();
        defaultPaint.setColor(defaultColor);
        defaultPaint.setAntiAlias(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public void setColor(int color) {
        defaultColor = color;
        defaultPaint.setColor(defaultColor);
        postInvalidate();
    }

    public void setCircleCount(int count) {
        if (count <= 1) {
            count = 0;
        }

        circleCount = count;
        requestLayout();
    }

    public void setCurrentIndex(int index) {
        currentIndex = index;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int x, y;

        y = getHeight() >> 1;
        for (int n = 0; n < circleCount; n++) {
            x = (getHeight() + defaultGap) * n + (getHeight() >> 1);

            if (n == currentIndex) {
                defaultPaint.setAlpha(0xFF);
            } else {
                defaultPaint.setAlpha(0x88);
            }

            canvas.drawCircle(x, y, (getHeight() >> 1), defaultPaint);
        }
    }

    @Override
    public void onGlobalLayout() {
        int nextWidth;
        ViewGroup.LayoutParams lp = getLayoutParams();

        if (circleCount == 0) {
            nextWidth = 0;
        } else {
            nextWidth = (circleCount * getHeight() + (circleCount - 1) * defaultGap);
        }

        if (lp.width != nextWidth) {
            lp.width = nextWidth;
            setLayoutParams(lp);
        }
    }
}
