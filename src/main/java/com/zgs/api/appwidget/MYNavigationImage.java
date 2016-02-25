package com.zgs.api.appwidget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.zgs.api.CommonConfig;
import com.zgs.api.R;
import com.zgs.api.appwidget.image.MYImageView;
import com.zgs.api.listener.ItemClickListener;
import com.zgs.api.utils.LogUtils;

import java.util.HashMap;

/**
 * Created by simon on 15-12-1.
 */
public class MYNavigationImage extends RelativeLayout implements View.OnClickListener {
    ViewPager vp_navigation_image;
    MYPagerCircleIndicator pi_navigation_image;

    /**
     * view cache of each page
     */
    private HashMap<Integer, MYImageView> viewCacheMap = new HashMap();
    /**
     * navi images
     */
    private String[] images;
    /**
     * item click listener
     */
    private ItemClickListener itemClickListener;
    /**
     * check if touched
     */
    private boolean isTouched = false;
    /**
     * auto scroll
     */
    Runnable timer = new Runnable() {
        @Override
        public void run() {
            if (images == null || images.length == 0) {
                return;
            }

            if (!isTouched) {
                vp_navigation_image.setCurrentItem((vp_navigation_image.getCurrentItem() + 1) % images.length, true);
            }

            postDelayed(timer, CommonConfig.NAVI_IMAGE_TIMER);
        }
    };

    /**
     * pager adapter
     */
    private PagerAdapter adapter = new PagerAdapter() {
        @Override
        public int getCount() {
            if (images != null) {
                return images.length;
            }

            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewCacheMap.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = viewCacheMap.get(position);
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.image_default, container, false);
                viewCacheMap.put(position, (MYImageView) view);
            }

            ((MYImageView) view).setImage(images[position]);

            container.addView(view);
            view.setTag(position);
            view.setOnClickListener(MYNavigationImage.this);

            return view;
        }
    };

    public MYNavigationImage(Context context) {
        this(context, null, 0);
    }

    public MYNavigationImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MYNavigationImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.common_widget_navigation_image, this, true);
        vp_navigation_image = (ViewPager) findViewById(R.id.vp_navigation_image);
        pi_navigation_image = (MYPagerCircleIndicator) findViewById(R.id.pi_navigation_image);

        vp_navigation_image.setAdapter(adapter);

        vp_navigation_image.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pi_navigation_image.setCurrentIndex(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        postDelayed(timer, CommonConfig.NAVI_IMAGE_TIMER);
    }

    public void setImageUrls(String[] urls) {
        if (urls == null) {
            LogUtils.error("image url cant be null");
            return;
        }

        images = urls;
        pi_navigation_image.setCircleCount(images.length);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();

        if (itemClickListener != null) {
            itemClickListener.onItemClick(v, position);
        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                isTouched = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isTouched = false;
                break;
        }

        return super.dispatchTouchEvent(ev);
    }
}
