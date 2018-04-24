package cn.yyp.nc.event;

/**
 * 头像更新事件
 */
public class AvatarUpdateEvent {

    public String avatarUrl;

    public AvatarUpdateEvent(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
