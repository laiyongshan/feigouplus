package com.example.youhe.youhecheguanjiaplus.db.biz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.youhe.youhecheguanjiaplus.app.AppContext;

/**
 * Created by Administrator on 2016/9/19 0019.
 * token数据库操作类
 */
public class TokenSQLUtils {

    private TokenSqlOpenHelp sqlOp;
    private static SQLiteDatabase db;
    private Context context;

    public TokenSQLUtils(Context context) {
        sqlOp = new TokenSqlOpenHelp(context);
        if (db == null)
            db = sqlOp.getWritableDatabase();
        this.context = context;
    }

//    public void close(){
//        if (db!=null)
//            db.close();
//    }

    /**
     * 存储
     * 增
     */
    public void addDate(String name) {
        ContentValues values = new ContentValues();

        values.put("token", name);
        if (db != null) {
            db.insert("tokens", null, values);
        }

    }

    /**
     * 删除
     */
    public static void delete() {
        if (db != null) {
            db.delete("tokens", null, null);
        }
        AppContext.isLogin = false;

//        if(db!=null) {
//            db.close();
//        }
    }

    /**
     * 改
     */
    public void undateApi(String token) {
        ContentValues values = new ContentValues();
        values.put("token", token);
        if (db != null) {
            db.insert("tokens", null, values);
        }
    }

    /**
     * 查
     */
    public static String check() {
        String token = "";
        Cursor cursor = null;
        try {

            if (db != null) {
                cursor = db.query("tokens", null, null, null, null, null, null);

                while (cursor.moveToNext()) {
                    token = cursor.getString(cursor.getColumnIndex("token"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return token;
    }

}
