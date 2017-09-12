package com.java.no16.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/** Database Helper. */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;

    private static final String DATABASE_NAME = "test.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("Create database:", "");
        db.execSQL("CREATE TABLE IF NOT EXISTS news" +
                "(id TEXT PRIMARY KEY, category TEXT, title TEXT, author TEXT, content TEXT, date TEXT, favorite INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
