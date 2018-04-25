package cn.yyp.nc.greendao;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.List;

import cn.yyp.nc.util.StringConverter;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 笔记
 */
@Entity
public class Note {

    @Id
    private long id;
    private String title;
    private String content;
    private int noteType;//笔记类型

    private String createTime;
    private boolean isTop;//是否置顶
    private boolean isStar;//是否加星

    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> imageList;
    private String voiceUrl;
    private String videoUrl;

    @Generated(hash = 815523434)
    public Note(long id, String title, String content, int noteType,
            String createTime, boolean isTop, boolean isStar,
            List<String> imageList, String voiceUrl, String videoUrl) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.noteType = noteType;
        this.createTime = createTime;
        this.isTop = isTop;
        this.isStar = isStar;
        this.imageList = imageList;
        this.voiceUrl = voiceUrl;
        this.videoUrl = videoUrl;
    }

    @Generated(hash = 1272611929)
    public Note() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getNoteType() {
        return noteType;
    }

    public void setNoteType(int noteType) {
        this.noteType = noteType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public boolean isStar() {
        return isStar;
    }

    public void setStar(boolean star) {
        isStar = star;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public boolean getIsStar() {
        return this.isStar;
    }

    public void setIsStar(boolean isStar) {
        this.isStar = isStar;
    }

    public boolean getIsTop() {
        return this.isTop;
    }

    public void setIsTop(boolean isTop) {
        this.isTop = isTop;
    }
}
