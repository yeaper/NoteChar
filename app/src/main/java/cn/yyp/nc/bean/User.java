package cn.yyp.nc.bean;

import cn.yyp.nc.db.NewFriend;
import cn.bmob.v3.BmobUser;

/**
 * 用户类
 */
public class User extends BmobUser {

    private String avatar;

    public User(){}

    public User(NewFriend friend){
        setObjectId(friend.getUid());
        setUsername(friend.getName());
        setAvatar(friend.getAvatar());
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
