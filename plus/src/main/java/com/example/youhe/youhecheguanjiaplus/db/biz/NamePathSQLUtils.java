package com.example.youhe.youhecheguanjiaplus.db.biz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2016/9/19 0019.
 * 版本号保存数据库操作类
 */
public class NamePathSQLUtils {

    private NamePathOpenHelp sqlOp;
    private static SQLiteDatabase db;
    private Context context;

    public NamePathSQLUtils(Context context) {
        sqlOp = new NamePathOpenHelp(context);
        if (db == null)
            db = sqlOp.getWritableDatabase();
        this.context = context;

    }

    /**
     * 存储
     * 增
     */
    public void addDate(String name) {
        ContentValues values = new ContentValues();

        values.put("versionid", name);
        if (db != null)
            db.insert("version", null, values);

    }

    /**
     * 删除
     */
    public void delete() {
        if (db != null)
            db.delete("version", null, null);
    }

    /**
     * 改
     */
    public void undateApi(String statu) {
        ContentValues values = new ContentValues();
        values.put("versionid", statu);
        if (db != null)
            db.insert("version", null, values);
    }

    /**
     * 查
     */
    public static String check() {
        String statu = "";
        Cursor cursor = null;
        try {

            cursor = db.query("version", null, null, null, null, null, null);

            while (cursor.moveToNext()) {
                statu = cursor.getString(cursor.getColumnIndex("versionid"));
//            Log.i("WU",statu);
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return statu;
    }

}
