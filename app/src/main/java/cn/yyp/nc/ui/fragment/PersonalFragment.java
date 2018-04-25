package cn.yyp.nc.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.yyp.nc.BaseApplication;
import cn.yyp.nc.R;
import cn.yyp.nc.base.ImageLoaderFactory;
import cn.yyp.nc.base.ParentWithNaviFragment;
import cn.yyp.nc.bean.User;
import cn.yyp.nc.event.AvatarUpdateEvent;
import cn.yyp.nc.event.RetUsernameEvent;
import cn.yyp.nc.model.UserModel;
import cn.yyp.nc.ui.LoginActivity;
import cn.yyp.nc.ui.MyStarNoteActivity;
import cn.yyp.nc.ui.UserInfoActivity;
import cn.yyp.nc.ui.SetActivity;
import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobUser;

/**
 * 个人
 */
public class PersonalFragment extends ParentWithNaviFragment {

    @Bind(R.id.personal_avatar)
    ImageView avatar;
    @Bind(R.id.tv_set_name)
    TextView tv_set_name;

    @Override
    protected String title() {
        return "个人";
    }

    public static PersonalFragment newInstance() {
        PersonalFragment fragment = new PersonalFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PersonalFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_personal, container, false);
        initNaviView();
        ButterKnife.bind(this, rootView);
        EventBus.getDefault().register(this);
        User user = UserModel.getInstance().getCurrentUser();
        ImageLoaderFactory.getLoader().loadAvator(avatar, user.getAvatar(), R.mipmap.head);
        String username = user.getUsername();
        tv_set_name.setText(TextUtils.isEmpty(username) ? "" : username);
        return rootView;
    }

    @OnClick({R.id.layout_info,R.id.layout_sign_note,R.id.personal_set})
    public void click(View view) {
        switch (view.getId()){
            case R.id.layout_info:
                Bundle bundle = new Bundle();
                bundle.putSerializable("u", BmobUser.getCurrentUser(User.class));
                startActivity(UserInfoActivity.class, bundle);
                break;
            case R.id.layout_sign_note:
                startActivity(MyStarNoteActivity.class, null);
                break;
            case R.id.personal_set:
                startActivity(new Intent(getActivity(), SetActivity.class));
                break;
        }

    }

    @OnClick(R.id.btn_logout)
    public void onLogoutClick(View view) {
        UserModel.getInstance().logout();
        //TODO 连接：3.2、退出登录需要断开与IM服务器的连接
        BmobIM.getInstance().disConnect();
        BaseApplication.clearActivity();
        getActivity().finish();
        startActivity(LoginActivity.class, null);
    }

    @Subscribe
    public void onEventMainThread(RetUsernameEvent event){
        //更新用户名
        String username = UserModel.getInstance().getCurrentUser().getUsername();
        tv_set_name.setText(TextUtils.isEmpty(username) ? "" : username);
    }

    @Subscribe
    public void onEventMainThread(AvatarUpdateEvent event){
        //更新头像
        ImageLoaderFactory.getLoader().loadAvator(avatar, event.getAvatarUrl(), R.mipmap.head);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
