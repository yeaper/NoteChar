package cn.yyp.nc.event;

import cn.bmob.newim.bean.BmobIMUserInfo;

/**
 * 聊天事件
 */
public class ChatEvent {

    public BmobIMUserInfo info;

    public ChatEvent(BmobIMUserInfo info){
        this.info=info;
    }
}
