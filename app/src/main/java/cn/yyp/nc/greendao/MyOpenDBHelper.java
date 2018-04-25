package cn.yyp.nc.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;


/**
 * 数据库助手
 */
public class MyOpenDBHelper extends DaoMaster.OpenHelper {


    public MyOpenDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        NoteDao.createTable(db, true);
    }
}
