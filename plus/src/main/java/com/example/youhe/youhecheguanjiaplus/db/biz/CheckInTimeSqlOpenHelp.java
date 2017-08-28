package com.example.youhe.youhecheguanjiaplus.db.biz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/9/19 0019.
 */
public class CheckInTimeSqlOpenHelp extends SQLiteOpenHelper{
    private static String NAME = "checkintime.db";
    private static int VERSION = 1;

    public CheckInTimeSqlOpenHelp(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String sql = "create table version( eid integer primary key autoincrement not null,versionid varchar(1000) not null);";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
