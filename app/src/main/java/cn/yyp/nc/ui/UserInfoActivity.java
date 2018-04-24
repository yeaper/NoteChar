package cn.yyp.nc.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.config.ISListConfig;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.yyp.nc.R;
import cn.yyp.nc.base.ImageLoaderFactory;
import cn.yyp.nc.base.ParentWithNaviActivity;
import cn.yyp.nc.bean.AddFriendMessage;
import cn.yyp.nc.bean.Friend;
import cn.yyp.nc.bean.User;
import cn.yyp.nc.event.AvatarUpdateEvent;
import cn.yyp.nc.event.RetUsernameEvent;
import cn.yyp.nc.model.UserModel;
import cn.yyp.nc.model.i.IUploadFileListener;
import cn.yyp.nc.util.FileUtil;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 用户资料
 */
public class UserInfoActivity extends ParentWithNaviActivity {

    @Bind(R.id.iv_avatar)
    ImageView iv_avatar;
    @Bind(R.id.tv_name)
    TextView tv_name;
    @Bind(R.id.layout_reset_pwd)
    RelativeLayout reset_pwd;
    @Bind(R.id.btn_add_friend)
    Button btn_add_friend;
    @Bind(R.id.btn_chat)
    Button btn_chat;

    ProgressDialog progressDialog;

    //用户
    User user;
    //用户信息
    BmobIMUserInfo info;
    boolean isSendFocusRequest = false;//是否已经发送了关注请求

    @Override
    protected String title() {
        return "个人资料";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        //导航栏
        initNaviView();
        //用户
        user = (User) getBundle().getSerializable("u");
        if (user.getObjectId().equals(getCurrentUid())) {//用户为登录用户
            reset_pwd.setVisibility(View.VISIBLE);
            btn_add_friend.setVisibility(View.GONE);
            btn_chat.setVisibility(View.GONE);
        } else {//用户为非登录用户
            reset_pwd.setVisibility(View.GONE);
            btn_chat.setVisibility(View.VISIBLE);

            UserModel.getInstance().queryFriends(
                    new FindListener<Friend>() {
                        @Override
                        public void done(List<Friend> friends, BmobException e) {
                            if (e == null) {
                                int count = 0;
                                for(Friend friend: friends){//好友不显示关注按钮
                                    if(user.getObjectId().equals(friend.getFriendUser().getObjectId())) {
                                        btn_add_friend.setVisibility(View.GONE);
                                        break;
                                    }
                                    count++;
                                }
                                if(count >= friends.size()){
                                    btn_add_friend.setVisibility(View.VISIBLE);
                                }
                            }else{
                                btn_add_friend.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }
        //构造聊天方的用户信息:传入用户id、用户名和用户头像三个参数
        info = new BmobIMUserInfo(user.getObjectId(), user.getUsername(), user.getAvatar());
        //加载头像
        ImageLoaderFactory.getLoader().loadAvator(iv_avatar, user.getAvatar(), R.mipmap.head);
        //显示名称
        tv_name.setText(user.getUsername());
    }


    @OnClick({R.id.iv_avatar,R.id.layout_name,R.id.layout_reset_pwd,R.id.btn_add_friend, R.id.btn_chat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_avatar:
                selectAvatar();
                break;
            case R.id.layout_name:
                retName();
                break;
            case R.id.layout_reset_pwd:
                startActivity(ResetPwdActivity.class, null, false);
                break;
            case R.id.btn_add_friend:
                if(!isSendFocusRequest) sendAddFriendMessage();
                break;
            case R.id.btn_chat:
                chat();
                break;
        }
    }

    /**
     * 选择头像
     */
    public void selectAvatar(){
        // 配置图片选择器
        ISListConfig config = new ISListConfig.Builder()
                // 是否多选, 默认true
                .multiSelect(false)
                // 是否记住上次选中记录, 仅当multiSelect为true的时候配置，默认为true
                .rememberSelected(false)
                // “确定”按钮背景色
                .btnBgColor(Color.GRAY)
                // “确定”按钮文字颜色
                .btnTextColor(Color.BLUE)
                // 使用沉浸式状态栏
                .statusBarColor(getResources().getColor(R.color.colorPrimaryDark))
                // 返回图标ResId
                .backResId(R.mipmap.base_action_bar_back_bg_n)
                // 标题
                .title("选择头像")
                // 标题文字颜色
                .titleColor(Color.WHITE)
                // TitleBar背景色
                .titleBgColor(getResources().getColor(R.color.colorPrimary))
                // 裁剪大小。needCrop为true的时候配置
                .cropSize(1, 1, 200, 200)
                .needCrop(true)
                // 第一个是否显示相机，默认true
                .needCamera(true)
                // 最大选择图片数量，默认9
                .maxNum(1)
                .build();

        // 跳转到图片选择器
        ISNav.getInstance().toListActivity(this, config, 0x11);
    }

    /**
     * 上传头像
     */
    private void uploadAvatar(String path){
        showPD();
        FileUtil.uploadFile(path, new IUploadFileListener() {
            @Override
            public void uploading(int progress) {
            }

            @Override
            public void uploadSuccess(String fileName, final String fileUrl) {
                User newUser = new User();
                newUser.setAvatar(fileUrl);
                BmobUser bmobUser = UserModel.getInstance().getCurrentUser();
                newUser.update(bmobUser.getObjectId(),new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        progressDialog.dismiss();
                        if(e==null){
                            showToast("头像修改成功");
                            // 本地更新头像
                            ImageLoaderFactory.getLoader().loadAvator(iv_avatar, fileUrl, R.mipmap.head);
                            EventBus.getDefault().post(new AvatarUpdateEvent(fileUrl));
                        }else{
                            showToast("头像修改失败："+ e.getMessage());
                        }
                    }
                });
            }

            @Override
            public void uploadError(String errorMsg) {
                progressDialog.dismiss();
                showToast("头像修改失败："+ errorMsg);
            }
        });
    }

    private void showPD() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//转盘
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("正在上传，请稍后……");
        progressDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 图片选择结果回调
        if (requestCode == 0x11 && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra("result");
            for (String path : pathList) {
                uploadAvatar(path);
            }
        }
    }

    /**
     * 更改用户名
     */
    private void retName(){
        final EditText et = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("修改用户名")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(et.getText().toString().trim().length() > 0){
                            BmobUser newUser = new BmobUser();
                            newUser.setUsername(et.getText().toString().trim());
                            BmobUser bmobUser = UserModel.getInstance().getCurrentUser();
                            newUser.update(bmobUser.getObjectId(),new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        tv_name.setText(et.getText().toString().trim());
                                        EventBus.getDefault().post(new RetUsernameEvent());
                                    }else{
                                        toast("修改失败:" + e.getMessage());
                                    }
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    /**
     * 发送关注好友的请求
     */
    //TODO 好友管理：9.7、发送添加好友请求
    private void sendAddFriendMessage() {
        if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            toast("尚未连接IM服务器");
            return;
        }
        //TODO 会话：4.1、创建一个暂态会话入口，发送请求
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, true, null);
        //TODO 消息：5.1、根据会话入口获取消息管理，发送请求
        BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
        AddFriendMessage msg = new AddFriendMessage();
        User currentUser = BmobUser.getCurrentUser(User.class);
        msg.setContent("很高兴认识你，可以加你好友吗?");//给对方的一个留言信息
        Map<String, Object> map = new HashMap<>();
        map.put("name", currentUser.getUsername());//发送者姓名
        map.put("avatar", currentUser.getAvatar());//发送者的头像
        map.put("uid", currentUser.getObjectId());//发送者的uid
        msg.setExtraMap(map);
        messageManager.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if (e == null) {//发送成功
                    toast("好友请求发送成功，等待验证");
                    isSendFocusRequest = true;
                } else {//发送失败
                    toast("发送失败:" + e.getMessage());
                }
            }
        });
    }

    /**
     * 与陌生人聊天
     */
    private void chat() {
        if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            toast("尚未连接IM服务器");
            return;
        }
        //TODO 会话：4.1、创建一个常态会话入口，陌生人聊天
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, null);
        Bundle bundle = new Bundle();
        bundle.putSerializable("c", conversationEntrance);
        startActivity(ChatActivity.class, bundle, false);
    }
}