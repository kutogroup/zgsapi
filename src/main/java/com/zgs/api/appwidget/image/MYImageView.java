package com.zgs.api.appwidget.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.zgs.api.R;
import com.zgs.api.utils.ResourceUtils;
import com.zgs.api.utils.SysUtils;

/**
 * Created by simon on 15-11-27.
 */
public class MYImageView extends SimpleDraweeView {
    private int baseScreenWidth = 0;
    private int defaultImage = 0;
    float xWeight = 0;
    float yWeight = 0;
    boolean isCenterCrop = false;
    boolean isGif = false;

    public MYImageView(Context context) {
        this(context, null, 0);
    }

    public MYImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MYImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (isInEditMode()) {
            return;
        }

        TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.MYImageView);
        for (int n = 0; n < types.getIndexCount(); n++) {
            int attr = types.getIndex(n);

            if (attr == R.styleable.MYImageView_xWeight) {
                xWeight = types.getFloat(attr, 1);
            } else if (attr == R.styleable.MYImageView_yWeight) {
                yWeight = types.getFloat(attr, 1);
            } else if (attr == R.styleable.MYImageView_defaultImage) {
                defaultImage = types.getResourceId(attr, 0);
            } else if (attr == R.styleable.MYImageView_isCenterCrop) {
                isCenterCrop = types.getBoolean(attr, isCenterCrop);
            } else if (attr == R.styleable.MYImageView_isGif) {
                isGif = types.getBoolean(attr, false);
            }
        }
        types.recycle();

        getHierarchy().setActualImageScaleType(isCenterCrop ? ScalingUtils.ScaleType.CENTER_CROP : ScalingUtils.ScaleType.FIT_CENTER);
        if (defaultImage == 0) {
            defaultImage = R.drawable.none_image;
        }

        if (isGif) {
            ImageRequest request = ImageRequestBuilder.newBuilderWithResourceId(defaultImage)
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setAutoPlayAnimations(true)
                    .setImageRequest(request)
                    .build();

            setController(controller);
        } else {
            getHierarchy().setPlaceholderImage(ResourceUtils.getDrawable(defaultImage), isCenterCrop ? ScalingUtils.ScaleType.CENTER_CROP : ScalingUtils.ScaleType.FIT_CENTER);
        }

        if (getHierarchy().getRoundingParams() == null) {
            setBackgroundColor(ResourceUtils.getColor(R.color.window_background_color));
        }
    }

    public void setImage(String uri_string) {
        if (uri_string == null || uri_string.length() == 0) {
            setImageURI(null);
            return;
        }


        setImageURI(Uri.parse(uri_string));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            throw new IllegalArgumentException("width cant be wrap_content");
        }

        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (baseScreenWidth != 0) {
            int screenWidth = SysUtils.getScreenWidth(getContext());
            width = screenWidth * width / baseScreenWidth;
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        }

        if (xWeight != 0 && yWeight != 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (width * yWeight / xWeight), MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
