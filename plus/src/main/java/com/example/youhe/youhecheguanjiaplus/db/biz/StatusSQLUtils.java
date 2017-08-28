package com.example.youhe.youhecheguanjiaplus.db.biz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2016/9/19 0019.
 * 保存登录状态数据库操作类
 */
public class StatusSQLUtils {

    private static LoginStatusOpenHelp sqlOp;
    private static SQLiteDatabase db;
    private Context context;

    public StatusSQLUtils(Context context) {
        sqlOp = new LoginStatusOpenHelp(context);
        if (db == null)
            db = sqlOp.getWritableDatabase();
        this.context = context;

    }

    public void close() {
        if (db != null)
            db.close();
    }

    /**
     * 存储
     * 增
     */
    public void addDate(String name) {
        ContentValues values = new ContentValues();

        values.put("statu", name);
        if (db != null)
            db.insert("status", null, values);

    }

    /**
     * 删除
     */
    public void delete() {
        if (db != null)
            db.delete("status", null, null);
    }

    /**
     * 改
     */
    public static void undateApi(String statu) {
        ContentValues values = new ContentValues();
        values.put("statu", statu);
        if (db != null)
            db.insert("status", null, values);
    }

    /**
     * 查
     */
    public static String check() {
        String statu = "";
        Cursor cursor = null;
        try {
            cursor = db.query("status", null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                statu = cursor.getString(cursor.getColumnIndex("statu"));
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
