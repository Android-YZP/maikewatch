package com.maikeapp.maikewatch.DBOpenHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by aaa on 2016/6/12.
 */
public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(Context context) {
        super(context, "WatchData.db", null, 1);
    }

        @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table userinfo (_id integer primary key autoincrement,user varchar(30)"+
                ",mac varchar(30)" +
                ",phone varchar(30)" +
                ",height int" +
                ",weight int" +
                ",age int)");
        db.execSQL("create table datainfo (_id integer primary key autoincrement,date date" +
                ",percent int" +
                ",targetstep int" +
                ",totalstep int" +
                ",calorie float" +
                ",mile float" +
                ",userid int"+
                ",status int)");
        db.execSQL("create table hourstepinfo (_id integer primary key autoincrement,hour int" +
                ",step int"+
                ",dataid int"+
                ",status int)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
