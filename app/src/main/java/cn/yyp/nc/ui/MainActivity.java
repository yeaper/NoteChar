package cn.yyp.nc.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import cn.yyp.nc.R;
import cn.yyp.nc.base.BaseActivity;
import cn.yyp.nc.bean.User;
import cn.yyp.nc.db.NewFriendManager;
import cn.yyp.nc.event.RefreshEvent;
import cn.yyp.nc.ui.fragment.FriendFragment;
import cn.yyp.nc.ui.fragment.ConversationFragment;
import cn.yyp.nc.ui.fragment.NoteFragment;
import cn.yyp.nc.ui.fragment.PersonalFragment;
import cn.yyp.nc.util.IMMLeaks;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity {

    @Bind(R.id.btn_note)
    TextView btn_note;
    @Bind(R.id.btn_conversation)
    TextView btn_conversation;
    @Bind(R.id.btn_friend)
    TextView btn_friend;
    @Bind(R.id.btn_personal)
    TextView btn_personal;

    @Bind(R.id.iv_conversation_tips)
    ImageView iv_conversation_tips;
    @Bind(R.id.iv_friend_tips)
    ImageView iv_friend_tips;

    private TextView[] mTabs;
    private NoteFragment noteFragment;
    private ConversationFragment conversationFragment;
    FriendFragment friendFragment;
    private PersonalFragment personalFragment;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final User user = BmobUser.getCurrentUser(User.class);
        //TODO 连接：3.1、登录成功、注册成功或处于登录状态重新打开应用后执行连接IM服务器的操作
        //判断用户是否登录，并且连接状态不是已连接，则进行连接操作
        if (!TextUtils.isEmpty(user.getObjectId()) &&
                BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    if (e == null) {
                        //服务器连接成功就发送一个更新事件，同步更新会话及主页的小红点
                        //TODO 会话：2.7、更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                        BmobIM.getInstance().
                                updateUserInfo(new BmobIMUserInfo(user.getObjectId(),
                                        user.getUsername(), user.getAvatar()));
                        EventBus.getDefault().post(new RefreshEvent());
                    } else {
                        toast(e.getMessage());
                    }
                }
            });
            //TODO 连接：3.3、监听连接状态，可通过BmobIM.getInstance().getCurrentStatus()来获取当前的长连接状态
            BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                @Override
                public void onChange(ConnectionStatus status) {
                    Logger.i(BmobIM.getInstance().getCurrentStatus().getMsg());
                }
            });
        }
        //解决leancanary提示InputMethodManager内存泄露的问题
        IMMLeaks.fixFocusedViewLeak(getApplication());
    }


    @Override
    protected void initView() {
        super.initView();
        mTabs = new TextView[4];
        mTabs[0] = btn_note;
        mTabs[1] = btn_conversation;
        mTabs[2] = btn_friend;
        mTabs[3] = btn_personal;
        mTabs[0].setSelected(true);
        initTab();
    }

    private void initTab() {
        noteFragment = new NoteFragment();
        conversationFragment = new ConversationFragment();
        personalFragment = new PersonalFragment();
        friendFragment = new FriendFragment();
        fragments = new Fragment[]{noteFragment, conversationFragment, friendFragment, personalFragment};
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, noteFragment)
                .add(R.id.fragment_container, conversationFragment)
                .add(R.id.fragment_container, friendFragment)
                .add(R.id.fragment_container, personalFragment)
                .hide(personalFragment)
                .hide(friendFragment)
                .hide(conversationFragment)
                .show(noteFragment).commit();
    }

    public void onTabSelect(View view) {
        switch (view.getId()) {
            case R.id.btn_note:
                index = 0;
                break;
            case R.id.btn_conversation:
                index = 1;
                break;
            case R.id.btn_friend:
                index = 2;
                break;
            case R.id.btn_personal:
                index = 3;
                break;
            default:
                break;
        }
        onTabIndex(index);
    }

    private void onTabIndex(int index) {
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        mTabs[currentTabIndex].setSelected(false);
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //每次进来应用都检查会话和好友请求的情况
        checkRedPoint();
        //进入应用后，通知栏应取消
        BmobNotificationManager.getInstance(this).cancelNotification();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清理导致内存泄露的资源
        BmobIM.getInstance().clear();
    }

    /**
     * 注册消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：8.3、通知有在线消息接收
    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        checkRedPoint();
    }

    /**
     * 注册离线消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：8.4、通知有离线消息接收
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event) {
        checkRedPoint();
    }

    /**
     * 注册自定义消息接收事件
     *
     * @param event
     */
    //TODO 消息接收：8.5、通知有自定义消息接收
    @Subscribe
    public void onEventMainThread(RefreshEvent event) {
        checkRedPoint();
    }

    /**
     *
     */
    private void checkRedPoint() {
        //TODO 会话：4.4、获取全部会话的未读消息数量
        int count = (int) BmobIM.getInstance().getAllUnReadCount();
        if (count > 0) {
            iv_conversation_tips.setVisibility(View.VISIBLE);
        } else {
            iv_conversation_tips.setVisibility(View.GONE);
        }
        //TODO 好友管理：是否有好友添加的请求
        if (NewFriendManager.getInstance(this).hasNewFriendInvitation()) {
            iv_friend_tips.setVisibility(View.VISIBLE);
        } else {
            iv_friend_tips.setVisibility(View.GONE);
        }
    }

}
