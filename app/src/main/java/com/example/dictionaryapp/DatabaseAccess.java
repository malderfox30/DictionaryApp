package com.example.dictionaryapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dictionaryapp.model.Word;

import java.util.ArrayList;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelperAnhViet;
    private SQLiteOpenHelper openHelperVietAnh;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    private DatabaseAccess(Context context) {
        this.openHelperAnhViet = new DatabaseOpenHelper(context, true);
        this.openHelperVietAnh = new DatabaseOpenHelper(context, false);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void setOpenHelperAnhViet() {
        this.database = openHelperAnhViet.getWritableDatabase();
    }

    public void setOpenHelperVietAnh() { this.database = openHelperVietAnh.getWritableDatabase(); }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    /**
     * Read all words from anh_viet dictionary
     *
     * @return a List of word from dictionary
     */
    public ArrayList<Word> getWordsAnhViet() {
        ArrayList<Word> words = new ArrayList<Word>();
        Cursor cursor = database.rawQuery("SELECT * FROM anh_viet", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Word word = new Word(0, null, null);
            word.setId(cursor.getInt(cursor.getColumnIndex("id")));
            word.setWord(cursor.getString(cursor.getColumnIndex("word")));
            word.setContent(cursor.getString(cursor.getColumnIndex("content")));
            words.add(word);
            cursor.moveToNext();
        }
        cursor.close();
        return words;
    }

    public ArrayList<Word> getWordsVietAnh() {
        ArrayList<Word> words = new ArrayList<Word>();
        Cursor cursor = database.rawQuery("SELECT * FROM viet_anh", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Word word = new Word(0, null, null);
            word.setId(cursor.getInt(cursor.getColumnIndex("id")));
            word.setWord(cursor.getString(cursor.getColumnIndex("word")));
            word.setContent(cursor.getString(cursor.getColumnIndex("content")));
            words.add(word);
            cursor.moveToNext();
        }
        cursor.close();
        return words;
    }
    public ArrayList<Word> searchWordsAnhViet (String filter) {
        ArrayList<Word> words = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM anh_viet where word like '"+ filter +"%' limit 30", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Word word = new Word(0, null, null);
            word.setId(cursor.getInt(cursor.getColumnIndex("id")));
            word.setWord(cursor.getString(cursor.getColumnIndex("word")));
            word.setContent(cursor.getString(cursor.getColumnIndex("content")));
            words.add(word);
            cursor.moveToNext();
        }
        cursor.close();
        return words;
    }

    public String getDefinition(String word) {
        String definition = "";
        Cursor cursor = database.rawQuery("SELECT * FROM anh_viet where word='"+ word +"'", null);
        cursor.moveToFirst();
        definition  = cursor.getString(2);
        cursor.close();

        /*
        if(definition.isEmpty()){
            cursor = database.rawQuery("SELECT * FROM viet_anh where word='"+ word +"'", null);
            cursor.moveToFirst();
            definition  = cursor.getString(2);
            cursor.close();
        }

         */
        return definition;
    }

}
