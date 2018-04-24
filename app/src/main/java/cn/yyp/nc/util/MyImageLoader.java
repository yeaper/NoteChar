package cn.yyp.nc.util;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.imnjh.imagepicker.ImageLoader;

/**
 *
 */
public class MyImageLoader implements ImageLoader {
    @Override
    public void bindImage(ImageView imageView, Uri uri, int width, int height) {
        com.nostra13.universalimageloader.core.ImageLoader.getInstance()
                .displayImage("file://" + uri.getPath(), imageView);
    }

    @Override
    public void bindImage(ImageView imageView, Uri uri) {
        com.nostra13.universalimageloader.core.ImageLoader.getInstance()
                .displayImage("file://" + uri.getPath(), imageView);
    }

    @Override
    public ImageView createImageView(Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    @Override
    public ImageView createFakeImageView(Context context) {
        return new ImageView(context);
    }
}