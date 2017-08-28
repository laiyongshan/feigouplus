package com.example.youhe.youhecheguanjiaplus.db.biz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.youhe.youhecheguanjiaplus.app.AppContext;

/**
 * Created by Administrator on 2016/9/19 0019.
 * 版本号保存数据库操作类
 */
public class VersionSQLUtils {

    private VersionOpenHelp sqlOp;
    private static SQLiteDatabase db;
    private Context context;

    public VersionSQLUtils(Context context) {
        sqlOp = new VersionOpenHelp(AppContext.getContext());
        if (db == null)
            db = sqlOp.getWritableDatabase();
        this.context = AppContext.getContext();

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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return statu;
    }

}
