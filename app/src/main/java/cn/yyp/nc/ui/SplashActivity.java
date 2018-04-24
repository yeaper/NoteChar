package cn.yyp.nc.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import cn.yyp.nc.R;
import cn.yyp.nc.bean.User;
import cn.yyp.nc.base.BaseActivity;
import cn.yyp.nc.model.UserModel;

/**
 * 启动界面
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler handler =new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                User user = UserModel.getInstance().getCurrentUser();
                if (user == null) {
                    startActivity(LoginActivity.class,null,true);
                }else{
                    startActivity(MainActivity.class,null,true);
                }
            }
        },2000);

    }
}
