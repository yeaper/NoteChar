package cn.yyp.nc.util;

import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 图片加载工具
 */
public class ImageLoadUtil {

    /**
     * 加载手机图片
     * @param url
     * @param view
     */
    public static void loadLocalImage(String url, ImageView view){
        ImageLoader.getInstance().displayImage("file://"+url, view);
    }

    /**
     * 加载资源图片
     * @param resId
     * @param view
     */
    public static void loadResImage(int resId, ImageView view){
        ImageLoader.getInstance().displayImage("drawable://"+resId, view);
    }

    /**
     * 加载网络图片
     * @param url
     * @param view
     */
    public static void loadNetImage(String url, ImageView view){
        ImageLoader.getInstance().displayImage(url, view);
    }
}
