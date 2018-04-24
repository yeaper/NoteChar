package cn.yyp.nc;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.orhanobut.logger.Logger;
import com.yuyh.library.imgsel.ISNav;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import cn.yyp.nc.base.UniversalImageLoader;
import cn.bmob.newim.BmobIM;

//TODO 集成：1.7、自定义Application，并在AndroidManifest.xml中配置
public class BaseApplication extends Application {

    private static BaseApplication INSTANCE;

    public static BaseApplication INSTANCE() {
        return INSTANCE;
    }

    private void setInstance(BaseApplication app) {
        setApplication(app);
    }

    private static void setApplication(BaseApplication a) {
        BaseApplication.INSTANCE = a;
    }

    private static List<Activity> lists = new ArrayList<>();

    public static void addActivity(Activity activity) {
        lists.add(activity);
    }

    public static void clearActivity() {
        if (lists != null) {
            for (Activity activity : lists) {
                activity.finish();
            }
            lists.clear();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
        //TODO 集成：1.8、初始化IM SDK，并注册消息接收器，只有主进程运行的时候才需要初始化
        if (getApplicationInfo().packageName.equals(getMyProcessName())) {
            BmobIM.init(this);
            BmobIM.registerDefaultMessageHandler(new DemoMessageHandler(this));
        }
        Logger.init("Demo");
        UniversalImageLoader.initImageLoader(this);
        // 自定义图片加载器
        ISNav.getInstance().init(new com.yuyh.library.imgsel.common.ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                ImageLoader.getInstance().displayImage("file://" + path, imageView);
            }
        });
    }

    /**
     * 获取当前运行的进程名
     *
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
