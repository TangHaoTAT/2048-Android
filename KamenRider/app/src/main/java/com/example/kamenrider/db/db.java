package com.example.kamenrider.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class db extends SQLiteOpenHelper {
    private final static int DATABASE_VERSION=1;
    private final static String DATABASES_NAME="game2048.db";
    private static Context context;

    public static void setContext(Context context) {
        db.context = context;
    }

    public db(){
        super(context,DATABASES_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table local_data(id integer primary key autoincrement," +
                                           "score varchar(30) not null," +
                                           "best_score varchar(30) not null," +
                                           "card_set varchar(200) not null)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) { }

    public SQLiteDatabase getConnection(){
        SQLiteDatabase db=getWritableDatabase();
        return db;
    }

    public void close(SQLiteDatabase db) {
        db.close();
    }
}
