package com.example.dictionaryapp;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseOpenHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME_1 = "anh_viet.db";
    private static final String DATABASE_NAME_2 = "viet_anh.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseOpenHelper(Context context, boolean target) {
        super(context, target?  DATABASE_NAME_1 : DATABASE_NAME_2, null, DATABASE_VERSION);
    }
}