package com.example.youhe.youhecheguanjiaplus.db.biz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/9/19 0019.
 */
public class LoginStatusOpenHelp extends SQLiteOpenHelper {
    private static String NAME = "statusqwq.db";
    private static int VERSION = 1;

    public LoginStatusOpenHelp(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String sql = "create table status( eid integer primary key autoincrement not null,statu varchar(10) not null);";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
