package com.napoleao.alphabeto.activity.util;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.napoleao.alphabeto.R;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Class responsible for loading images from a url.
 */
public class ImageLoadUtil {
    private final String TAG = "ImageLoadUtil";
    private static ImageLoadUtil instance;

    private ImageLoadUtil() {
    }

    /**
     * Returns an instance of the class.
     *
     * @return instance of class
     */
    public static ImageLoadUtil getInstance() {
        if (instance == null) {
            instance = new ImageLoadUtil();
        }

        return instance;
    }

    /**
     * Load an image from a url.
     *
     * @param imageUrl       url of image
     * @param themeImageLeft ImageView to receive the image
     * @param context        context of activity
     */
    public void loadImage(String imageUrl, ImageView themeImageLeft, Context context) {
        DiskCacheStrategy diskCacheStrategy = new DiskCacheStrategy() {
            @Override
            public boolean isDataCacheable(DataSource dataSource) {
                return true;
            }

            @Override
            public boolean isResourceCacheable(boolean isFromAlternateCacheKey, DataSource dataSource, EncodeStrategy encodeStrategy) {
                return true;
            }

            @Override
            public boolean decodeCachedResource() {
                return true;
            }

            @Override
            public boolean decodeCachedData() {
                return true;
            }
        };

        int erroImg = -1;
        try {
            erroImg = Integer.parseInt(imageUrl);
        } catch (NumberFormatException e) {
            erroImg = R.drawable.error;
        }

        Glide.with(context)
                .load(imageUrl)
                .error(erroImg)
                .diskCacheStrategy(diskCacheStrategy)
                .into(themeImageLeft);
    }
}
