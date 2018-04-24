package cn.yyp.nc.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.OnClick;
import cn.yyp.nc.R;
import cn.yyp.nc.base.BaseActivity;
import cn.yyp.nc.model.UserModel;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * 登陆界面
 *
 */
public class LoginActivity extends BaseActivity {

    @Bind(R.id.et_username)
    EditText et_username;
    @Bind(R.id.et_password)
    EditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }


    /**
     * 登录
     *
     * @param view
     */
    @OnClick(R.id.btn_login)
    public void onLoginClick(View view) {
        UserModel.getInstance().login(et_username.getText().toString(), et_password.getText().toString(), new LogInListener() {

            @Override
            public void done(Object o, BmobException e) {
                if (e == null) {
                    //登录成功
                    startActivity(MainActivity.class, null, true);
                } else {
                    toast(e.getMessage() + "(" + e.getErrorCode() + ")");
                }
            }
        });
    }

    /**
     * 去注册
     *
     * @param view
     */
    @OnClick(R.id.tv_register)
    public void onRegisterClick(View view) {
        startActivity(RegisterActivity.class, null, false);
    }
}
