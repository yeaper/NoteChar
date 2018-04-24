package cn.yyp.nc.model;

import cn.bmob.v3.BmobObject;

/**
 * 意见实体
 */
public class Feedback extends BmobObject {

    private String authorId;
    private String content;

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
