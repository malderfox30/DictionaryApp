package com.example.dictionaryapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.example.dictionaryapp.model.Word;

import java.util.ArrayList;

public class SplashActivity extends Activity {
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 18000;
    private ProgressBar pbLoading;
    public static ArrayList<Word> anhVietWords;
    public static ArrayList<Word> vietAnhWords;
    public static ArrayList<String> favoriteAnhVietWordsId;
    public static ArrayList<String> favoriteVietAnhWordsId;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash_screen);
        pbLoading = findViewById(R.id.pb_loading);
        pbLoading.setVisibility(View.VISIBLE);


        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

        //Load dữ liệu từ databases
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final DatabaseAccess databaseAccess1 = DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess1.setOpenHelperAnhViet();
                anhVietWords = databaseAccess1.getWordsAnhViet();
                favoriteAnhVietWordsId = databaseAccess1.getFavoriteWordsId();
                databaseAccess1.close();
            }
        });

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final DatabaseAccess databaseAccess2 = DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess2.setOpenHelperVietAnh();
                vietAnhWords = databaseAccess2.getWordsVietAnh();
                favoriteVietAnhWordsId = databaseAccess2.getFavoriteWordsId();
                databaseAccess2.close();
            }
        });
    }
}
