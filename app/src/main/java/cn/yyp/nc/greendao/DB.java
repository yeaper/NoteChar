package cn.yyp.nc.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 *
 */
public class DB {
    private static DaoSession instance = null;

    /**
     *
     * @param ctx
     * @param DbName
     * @return
     */
    public static DaoSession getInstance(Context ctx, String DbName) {
        if (null == instance) {
            MyOpenDBHelper helper = new MyOpenDBHelper(ctx, DbName + "-db", null);
            SQLiteDatabase db = helper.getWritableDatabase();

            DaoMaster daoMaster = new DaoMaster(db);
            instance = daoMaster.newSession();
        }

        return instance;
    }
}
