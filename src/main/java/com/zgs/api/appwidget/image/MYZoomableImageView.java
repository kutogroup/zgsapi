package com.zgs.api.appwidget.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.*;
import android.widget.ImageView;

import com.zgs.api.R;
import com.zgs.api.utils.LogUtils;

/**
 * Created by sam on 14-10-16.
 */
public class MYZoomableImageView extends ImageView implements ScaleGestureDetector.OnScaleGestureListener,
        View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {

    private static float SCALE_MAX = 4.0f;

    /**
     * 初始缩放比例。如果图片宽度或高度大于屏幕，此值将小于0
     */
    private float initScale = 1.0f;

    /**
     * visible width&height
     */
    private int visibleWidth = 0, visibleHeight = 0;

    private final float[] matrixValues = new float[9]; //用于存放矩阵的9个值

    private boolean once = true;

    private boolean fitMax = true;

    private ScaleGestureDetector mScaleGestureDetector = null; //缩放手势的检测

    private final Matrix mScaleMatrix = new Matrix();

    private OnTouchListener customListener = null;

    public MYZoomableImageView(Context context) {
        this(context, null);
    }

    public MYZoomableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MYZoomableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setScaleType(ScaleType.MATRIX);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        super.setOnTouchListener(this);

        TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.MYZoomableImageView);
        for (int n = 0; n < types.getIndexCount(); n++) {
            int attr = types.getIndex(n);

            if (attr == R.styleable.MYZoomableImageView_visibleWidth) {
                visibleWidth = (int) types.getDimension(attr, 0);
            } else if (attr == R.styleable.MYZoomableImageView_visibleHeight) {
                visibleHeight = (int) types.getDimension(attr, 0);
            } else if (attr == R.styleable.MYZoomableImageView_fitMax) {
                fitMax = types.getBoolean(attr, true);
            }
        }
        types.recycle();
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        customListener = l;
    }

    @Override
    public void onGlobalLayout() {
        if (once) {
            Drawable d = getDrawable();
            if (d == null) {
                return;
            }
            LogUtils.error("drawable.intrinsicWidth:" + d.getIntrinsicWidth() +
                    ",drawable.intrinsicHeight:" + d.getIntrinsicHeight());
            int width = getWidth();
            int height = getHeight();

            if (visibleWidth == 0 || !fitMax) {
                visibleWidth = width;
            }

            if (visibleHeight == 0 || !fitMax) {
                visibleHeight = height;
            }

            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();

            float scaleX = visibleWidth * 1.0f / dw;
            float scaleY = visibleHeight * 1.0f / dh;

            if (fitMax) {
                initScale = Math.max(scaleX, scaleY);
            } else {
                initScale = Math.min(scaleX, scaleY);
            }

            mScaleMatrix.reset();
            mScaleMatrix.postTranslate((getWidth() - dw) / 2, (getHeight() - dh) / 2);
            mScaleMatrix.postScale(initScale, initScale, getWidth() / 2, getHeight() / 2);
            setImageMatrix(mScaleMatrix);
            once = false;
        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        if (getDrawable() == null) {
            return true;
        }

        LogUtils.info("scale=" + scale);
        if ((scale < SCALE_MAX && scaleFactor > 1.0f) || (scale > initScale && scaleFactor < 1.0f)) {
            if (scaleFactor * scale < initScale) {
                scaleFactor = initScale / scale;
            }

            if (scaleFactor * scale > SCALE_MAX) {
                scaleFactor = SCALE_MAX / scale;
            }

            //mScaleMatrix.postScale(scaleFactor, scaleFactor, getWidth() / 2, getHeight() / 2);
            mScaleMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            fitBorder(getMatrixRectF());
            setImageMatrix(mScaleMatrix);
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    private float mLastX, mLastY;
    private int mLastPointerCount;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        float x = 0, y = 0;

        final int pointerCount = event.getPointerCount();

        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);

        }

        x = x / pointerCount;
        y = y / pointerCount;

        if (pointerCount != mLastPointerCount) {
            mLastX = x;
            mLastY = y;
        }

        mLastPointerCount = pointerCount;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mLastX;
                float dy = y - mLastY;

                if (getDrawable() != null) {
                    mScaleMatrix.postTranslate(dx, dy);
                    fitBorder(getMatrixRectF());
                    setImageMatrix(mScaleMatrix);
                }

                mLastY = y;
                mLastX = x;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;
                break;

        }

        if (customListener != null) {
            customListener.onTouch(v, event);
        }

        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LogUtils.info("zoom image detached from window");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        } else {
            getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LogUtils.info("zoom image attached to window");
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public final float getScale() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    /**
     * 在缩放时，进行图片显示范围的控制
     */
    private void fitBorder(RectF rectF) {
        float dx = 0, dy = 0;

        if (fitMax) {
            if (rectF.left > (getWidth() - visibleWidth) / 2) {
                dx = (getWidth() - visibleWidth) / 2 - rectF.left;
            }

            if (rectF.top > (getHeight() - visibleHeight) / 2) {
                dy = (getHeight() - visibleHeight) / 2 - rectF.top;
            }

            if (rectF.right < (getWidth() + visibleWidth) / 2) {
                dx = (getWidth() + visibleWidth) / 2 - rectF.right;
            }

            if (rectF.bottom < (getHeight() + visibleHeight) / 2) {
                dy = (getHeight() + visibleHeight) / 2 - rectF.bottom;
            }

            mScaleMatrix.postTranslate(dx, dy);
        } else {
            float currWidth = rectF.right - rectF.left + 1;
            float currHeight = rectF.bottom - rectF.top + 1;

            if (currWidth < getWidth()) {
                dx = (getWidth() - currWidth) / 2 - rectF.left;

                if (rectF.top > (getHeight() - visibleHeight) / 2) {
                    dy = (getHeight() - visibleHeight) / 2 - rectF.top;
                }

                if (rectF.bottom < (getHeight() + visibleHeight) / 2) {
                    dy = (getHeight() + visibleHeight) / 2 - rectF.bottom;
                }
            } else if (currHeight < getHeight()) {
                dy = (getHeight() - currHeight) / 2 - rectF.top;

                if (rectF.left > (getWidth() - visibleWidth) / 2) {
                    dx = (getWidth() - visibleWidth) / 2 - rectF.left;
                }

                if (rectF.right < (getWidth() + visibleWidth) / 2) {
                    dx = (getWidth() + visibleWidth) / 2 - rectF.right;
                }
            } else {
                if (rectF.left > (getWidth() - visibleWidth) / 2) {
                    dx = (getWidth() - visibleWidth) / 2 - rectF.left;
                }

                if (rectF.top > (getHeight() - visibleHeight) / 2) {
                    dy = (getHeight() - visibleHeight) / 2 - rectF.top;
                }

                if (rectF.right < (getWidth() + visibleWidth) / 2) {
                    dx = (getWidth() + visibleWidth) / 2 - rectF.right;
                }

                if (rectF.bottom < (getHeight() + visibleHeight) / 2) {
                    dy = (getHeight() + visibleHeight) / 2 - rectF.bottom;
                }
            }

            LogUtils.info("x=" + dx + ",y=" + dy);
            mScaleMatrix.postTranslate(dx, dy);
        }
    }

    public RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (d != null) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        once = true;
    }

    /**
     * @return 0:正常状态, 1:边缘靠左，2：边缘靠右，4：边缘靠上，8：边缘靠下
     */
    public int getImagePositionStatus() {
        int result = 0;

        RectF rectF = getMatrixRectF();
        if (rectF.left >= (getWidth() - visibleWidth) / 2) {
            result |= 1;
        }

        if (rectF.top >= (getHeight() - visibleHeight) / 2) {
            result |= 4;
        }

        if (rectF.right <= (getWidth() + visibleWidth) / 2) {
            result |= 2;
        }

        if (rectF.bottom <= (getHeight() + visibleHeight) / 2) {
            result |= 8;
        }

        return result;
    }
}
