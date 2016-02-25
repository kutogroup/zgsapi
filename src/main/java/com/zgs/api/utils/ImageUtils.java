package com.zgs.api.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by simon on 15-12-16.
 */
public class ImageUtils {
    public static int getImageSampleFitSize(InputStream input, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, options);

        float scaleX = options.outWidth / width;
        float scaleY = options.outHeight / height;

        float min = scaleX < scaleY ? scaleX : scaleY;
        try {
            input.close();
        } catch (IOException e) {

        }
        return (int) Math.floor(min);
    }

    public static Bitmap getImageBitmap(InputStream input, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
        try {
            input.close();
        } catch (IOException e) {

        }

        return bitmap;
    }

    public static Bitmap getImageBitmapFromFile(String file, int width, int height) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = getImageSampleFitSize(new FileInputStream(file), width, height);
            return BitmapFactory.decodeStream(new FileInputStream(file), null, options);
        } catch (IOException e) {
            LogUtils.error(e);
            return null;
        }
    }
}
