package com.zgs.api.appwidget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.zgs.api.R;
import com.zgs.api.listener.ClickListener;
import com.zgs.api.utils.LogUtils;
import com.zgs.api.utils.ResourceUtils;
import com.zgs.api.utils.SysUtils;

/**
 * Created by simon on 15-12-4.
 */
public class MYEditText extends EditText implements View.OnTouchListener, View.OnFocusChangeListener, TextWatcher {
    public static final int INPUT_TYPE_ALL = 1; // 全部字符
    public static final int INPUT_TYPE_NUM = 2; // 只支持数字
    public static final int INPUT_TYPE_DECIMAL_NUM = 3; // 支持浮点数
    public static final int INPUT_TYPE_SIMPLE_TEXT = 4; // 支持数字和英文字母
    public static final int INPUT_TYPE_ALL_WITHOUT_SYMBOL = 5; // 全部字符(不包括字符)

    private Drawable rightIcon;
    private Drawable leftIcon;
    private ClickListener clickListener = null;
    private boolean autoShowSoftInput = false;
    private int inputType = INPUT_TYPE_ALL;
    protected int validateType = 0;

    public MYEditText(Context context) {
        this(context, null, 0);
    }

    public MYEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public MYEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        leftIcon = null;
        rightIcon = null;

        TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.MYEditText);
        for (int n = 0; n < types.getIndexCount(); n++) {
            int attr = types.getIndex(n);

            if (attr == R.styleable.MYEditText_rightIcon) {
                rightIcon = ResourceUtils.getDrawable(types.getResourceId(attr, 0));
            } else if (attr == R.styleable.MYEditText_leftIcon) {
                leftIcon = ResourceUtils.getDrawable(types.getResourceId(attr, 0));
            } else if (attr == R.styleable.MYEditText_autoShowSoftInput) {
                autoShowSoftInput = types.getBoolean(attr, false);
            } else if (attr == R.styleable.MYEditText_inputType) {
                inputType = types.getInteger(attr, INPUT_TYPE_ALL);
            } else if (attr == R.styleable.MYEditText_validateType) {
                validateType = types.getInteger(attr, 0);
            }
        }
        types.recycle();

        switch (inputType) {
            case INPUT_TYPE_ALL:
                break;
            case INPUT_TYPE_NUM:
                setInputType(InputType.TYPE_CLASS_NUMBER);
                setKeyListener(new DigitsKeyListener(false, false));
                break;
            case INPUT_TYPE_DECIMAL_NUM:
                setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                setKeyListener(new DigitsKeyListener(false, true));
                break;
            case INPUT_TYPE_SIMPLE_TEXT:
                setKeyListener(DigitsKeyListener.getInstance("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
                break;
            case INPUT_TYPE_ALL_WITHOUT_SYMBOL:
                setFilters(new InputFilter[]{
                        new InputFilter() {
                            @Override
                            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                                if (source != null && SysUtils.ifStringHasSymbol(String.valueOf(source))) {
                                    return "";
                                }

                                return null;
                            }
                        }
                });
                break;
        }

        setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.margin_4));

        if (leftIcon != null) {
            leftIcon.setBounds(0, 0, leftIcon.getIntrinsicWidth(), leftIcon.getIntrinsicHeight());
        }

        setCompoundDrawables(leftIcon,
                getCompoundDrawables()[1], rightIcon, getCompoundDrawables()[3]);

        setOnTouchListener(this);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        LogUtils.info("x=" + event.getX() + ",y=" + event.getY());
        if (getCompoundDrawables()[2] != null) {
            if (event.getX() > (getWidth() - getPaddingRight() - getCompoundDrawables()[2].getIntrinsicWidth())) {
                // right icon click
                if (clickListener != null) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        getCompoundDrawables()[2].setState(new int[]{});
                        clickListener.onClick(this, false);
                    } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        getCompoundDrawables()[2].setState(new int[]{android.R.attr.state_selected});
                        clickListener.onClick(this, true);
                    }

                    setCompoundDrawables(getCompoundDrawables()[0],
                            getCompoundDrawables()[1], getCompoundDrawables()[2], getCompoundDrawables()[3]);
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            if (autoShowSoftInput) {
                ((Activity) getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }

            setRightIconVisiable(!TextUtils.isEmpty(getText()));
        } else {
            if (autoShowSoftInput) {
                ((Activity) getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }

            setRightIconVisiable(false);
        }
    }

    /**
     * @param visible set right icon visiable
     */
    public void setRightIconVisiable(boolean visible) {
        if (rightIcon != null) {
            boolean wasVisible = (getCompoundDrawables()[2] != null);

            if (visible != wasVisible) {
                rightIcon.setBounds(0, 0, rightIcon.getIntrinsicWidth(), rightIcon.getIntrinsicHeight());
                setCompoundDrawables(getCompoundDrawables()[0],
                        getCompoundDrawables()[1], visible ? rightIcon : null, getCompoundDrawables()[3]);
            }
        }
    }

    /**
     * show password
     */
    public void showPassword() {
        setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
    }

    /**
     * hide password
     */
    public void hidePassword() {
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    /**
     * clear all text
     */
    public void clearAll() {
        setText("");
    }

    /**
     * @param rightIcon set right icon
     */
    public void setRightIcon(Drawable rightIcon) {
        this.rightIcon = rightIcon;
    }

    public void updateRightIcon(Drawable rightIcon) {
        this.rightIcon = rightIcon;
        boolean wasVisible = (getCompoundDrawables()[2] != null);

        if (wasVisible) {
            rightIcon.setBounds(0, 0, rightIcon.getIntrinsicWidth(), rightIcon.getIntrinsicHeight());
            setCompoundDrawables(getCompoundDrawables()[0],
                    getCompoundDrawables()[1], rightIcon, getCompoundDrawables()[3]);
        }
    }

    /**
     * @param leftIcon set left icon
     */
    public void setLeftIcon(Drawable leftIcon) {
        this.leftIcon = leftIcon;
    }

    /**
     * @param listener set click listener
     */
    public void setRightIconClickListener(ClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isFocused()) {
            setRightIconVisiable(!TextUtils.isEmpty(s));
        }
    }

    public int getValidateType() {
        return validateType;
    }
}
