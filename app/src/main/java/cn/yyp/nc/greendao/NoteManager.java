package cn.yyp.nc.greendao;

import android.content.Context;

import java.util.List;

/**
 * 笔记数据管理
 */
public class NoteManager {

    private static NoteManager instance;
    private static NoteDao noteDao;

    private NoteManager(){}

    public static NoteManager getInstance(Context c){
        if(instance == null){
            instance = new NoteManager();
        }
        noteDao = DB.getInstance(c, "note").getNoteDao();
        return instance;
    }

    /**
     * 插入笔记
     * @param note
     */
    public void insertNote(Note note){
        noteDao.insert(note);
    }

    /**
     * 删除笔记
     * @param id 笔记id
     */
    public void deleteNote(long id){
        List<Note> list = noteDao.queryBuilder().build().list();
        for (int i=0;i<list.size();i++) {
            if (list.get(i).getId() == id) {
                noteDao.deleteByKey(list.get(0).getId());//通过 Id 来删除数据
            }
        }
    }

    /**
     * 查询笔记
     * @param title 标题
     */
    public List<Note> queryNote(String title){
        return noteDao.queryBuilder()
                .orderDesc(NoteDao.Properties.CreateTime)//通过 创建时间 这个属性进行降序排序
                .where(NoteDao.Properties.Title.like("%"+title+"%"))//数据筛选,模糊查询
                .build()
                .list();
    }

    /**
     * 查询加星笔记
     *
     */
    public List<Note> queryStarNote(){
        return noteDao.queryBuilder()
                .orderDesc(NoteDao.Properties.CreateTime)//通过 创建时间 这个属性进行降序排序
                .where(NoteDao.Properties.IsStar.eq(true))//数据筛选
                .build()
                .list();
    }

    /**
     * 查询全部笔记
     *
     */
    public List<Note> queryAllNote(){
        return noteDao.queryBuilder()
                .orderDesc(NoteDao.Properties.CreateTime)//通过 创建时间 这个属性进行降序排序
                .build()
                .list();
    }

    /**
     * 更新笔记
     * @param id
     * @param isTop 置顶
     * @param isStar 加星
     */
    public void updateNote(long id, boolean isTop, boolean isStar){
        List<Note> list = noteDao.queryBuilder().build().list();
        for (int i=0;i<list.size();i++) {
            if (list.get(i).getId() == id) {
                list.get(i).setIsTop(isTop);
                list.get(i).setIsStar(isStar);
                noteDao.update(list.get(i));
            }
        }
    }
}
