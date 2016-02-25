package com.zgs.api.appwidget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zgs.api.R;
import com.zgs.api.listener.SelectListener;
import com.zgs.api.utils.LogUtils;

/**
 * Created by simon on 15-12-14.
 */
public class MYBottomLineTabWidget extends RelativeLayout implements View.OnClickListener, Animator.AnimatorListener {
    LinearLayout ly_bottomline_container;
    View v_bottomline_indicator;

    /**
     * tab text color
     */
    ColorStateList colorStateList;

    /**
     * select listener
     */
    SelectListener selectListener = null;

    /**
     * current tab index
     */
    private int tabIndex = 0;

    /**
     * tab views
     */
    private View[] tabViews = null;

    /**
     * tab item width
     */
    private int tabItemWidth = 0;
    /**
     * pager bind
     */
    private MYViewPager pager = null;

    public MYBottomLineTabWidget(Context context) {
        this(context, null, 0);
    }

    public MYBottomLineTabWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MYBottomLineTabWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (isInEditMode()) {
            return;
        }

        LayoutInflater.from(context).inflate(R.layout.common_widget_bottomline_tabwidget, this, true);
        ly_bottomline_container = (LinearLayout) findViewById(R.id.ly_bottomline_container);
        v_bottomline_indicator = findViewById(R.id.v_bottomline_indicator);

        int color = -1;
        TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.MYBottomLineTabWidget);
        for (int n = 0; n < types.length(); n++) {
            int attr = types.getIndex(n);

            if (attr == R.styleable.MYBottomLineTabWidget_tabStringItems) {
                String[] items = getResources().getStringArray(types.getResourceId(attr, 0));
                if (items != null || items.length > 0) {
                    tabViews = new View[items.length];

                    for (int m = 0; m < items.length; m++) {
                        tabViews[m] = LayoutInflater.from(getContext()).inflate(R.layout.common_widget_bottomline_tabwidget_default_item, ly_bottomline_container, false);
                        ((TextView) tabViews[m]).setText(items[m]);
                        ly_bottomline_container.addView(tabViews[m], new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                    }
                }
            } else if (attr == R.styleable.MYBottomLineTabWidget_indicatorColor) {
                v_bottomline_indicator.setBackgroundColor(types.getColor(attr, Color.BLACK));
            } else if (attr == R.styleable.MYBottomLineTabWidget_tabTextColor) {
                if (color == -1) {
                    color = types.getColor(attr, 0);
                } else {
                    colorStateList = new ColorStateList(new int[][]{
                            {android.R.attr.state_selected}, {}
                    }, new int[]{
                            color, types.getColor(attr, 0)
                    });
                }
            } else if (attr == R.styleable.MYBottomLineTabWidget_tabTextHoverColor) {
                if (color == -1) {
                    color = types.getColor(attr, 0);
                } else {
                    colorStateList = new ColorStateList(new int[][]{
                            {android.R.attr.state_selected}, {}
                    }, new int[]{
                            types.getColor(attr, 0), color
                    });
                }
            }
        }
        types.recycle();

        if (tabViews != null && tabViews.length > 0) {
            for (int n = 0; n < tabViews.length; n++) {
                ((TextView) tabViews[n]).setTextColor(colorStateList);

                tabViews[n].setTag(n);
                tabViews[n].setOnClickListener(this);
            }

            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    ViewGroup.LayoutParams vlp = v_bottomline_indicator.getLayoutParams();
                    vlp.width = ly_bottomline_container.getWidth() / ly_bottomline_container.getChildCount();
                    v_bottomline_indicator.setLayoutParams(vlp);

                    tabItemWidth = ly_bottomline_container.getWidth() / tabViews.length;
                    tabViews[tabIndex].setSelected(true);
                    if (selectListener != null) {
                        selectListener.onSelected(tabIndex);
                    }
                    v_bottomline_indicator.setTranslationX(tabIndex * tabItemWidth);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
        } else {
            LogUtils.error("items cant be null");
        }
    }

    @Override
    public void onClick(View v) {
        int index = (int) v.getTag();
        setTabIndex(index, true);
    }

    public void setSelectListener(SelectListener listener) {
        this.selectListener = listener;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex, boolean smooth) {
        if (this.tabIndex != tabIndex) {
            tabViews[this.tabIndex].setSelected(false);

            if (pager == null) {
                if (smooth) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(
                            v_bottomline_indicator, "translationX",
                            this.tabIndex * tabItemWidth, tabIndex * tabItemWidth);

                    animator.setDuration(200);
                    animator.start();
                    animator.addListener(this);
                } else {
                    v_bottomline_indicator.setTranslationX(tabIndex * tabItemWidth);
                    if (selectListener != null) {
                        selectListener.onSelected(tabIndex);
                    }
                }
            } else {
                pager.setCurrentItem(tabIndex, true);
            }

            this.tabIndex = tabIndex;
            tabViews[this.tabIndex].setSelected(true);
        } else {
            if (selectListener != null) {
                selectListener.onSelected(tabIndex);
            }
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (selectListener != null) {
            selectListener.onSelected(tabIndex);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    public void bindViewPager(MYViewPager pager) {
        this.pager = pager;
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                v_bottomline_indicator.setTranslationX((position + positionOffset) * tabItemWidth);
            }

            @Override
            public void onPageSelected(int position) {
                setTabIndex(position, false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
