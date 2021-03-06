package com.example.dictionaryapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import com.example.dictionaryapp.model.Word;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;
import java.util.UUID;


public class DefinitionActivity extends AppCompatActivity {
    private WebView wvDefinition;
    private FloatingActionButton fabFavorite;
    private boolean isFavorite;
    private AppCompatImageButton ibPronounce;
    private TextToSpeech textToSpeech;
    private String word;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definition);

        isFavorite = false;
        wvDefinition = findViewById(R.id.wv_definition);
        fabFavorite = findViewById(R.id.fab_favorite);
        ibPronounce = findViewById(R.id.ib_pronounce);

        //Lấy dữ liệu form kia gởi qua
        Intent intent = getIntent();
        word = intent.getStringExtra("word");
        this.setTitle(word);

        //query định nghĩa của từ từ csdl
        final DatabaseAccess dbAccess = DatabaseAccess.getInstance(this);
        if(MainActivity.isAnhViet){
            dbAccess.setOpenHelperAnhViet();
            //Kiểm tra từ này đã được thích chưa
            for(String id : SplashActivity.favoriteAnhVietWordsId){
                int index = Integer.parseInt(id) - 1;
                if(SplashActivity.anhVietWords.get(index).getWord().equals(word)){
                    System.out.println("already liked");
                    fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_24);
                    isFavorite = true;
                    break;
                }
            }
        }
        else{
            dbAccess.setOpenHelperVietAnh();
            //Kiểm tra từ này đã được thích chưa
            for(String id : SplashActivity.favoriteVietAnhWordsId){
                int index = Integer.parseInt(id) - 1;
                if(SplashActivity.vietAnhWords.get(index).getWord().equals(word)){
                    fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_24);
                    isFavorite = true;
                    break;
                }
            }
        }
        String definition = dbAccess.getDefinition(word, MainActivity.isAnhViet);
        dbAccess.close();

        //Hiển thị trên textView
        //wvDefinition.setText(Html.fromHtml(definition));
        String html = "<html><head>"
        + "<style type=\"text/css\">" +
                "body{padding: 10px; color: black;} " +
                ".title{color: blue; font-size: 20px; font-weight: bold}" +
                "body>span{font-size: 14px;}"+
                "body>ul>li{}"+
                "li>span{}"+
                "</style></head><body>"+
                definition+
                "</body></html>";

        wvDefinition.loadData(html, "text/html", null);

        fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseAccess dbAccess = DatabaseAccess.getInstance(getApplicationContext());
                int wordId = 0;
                if (MainActivity.isAnhViet == true) {
                    for(Word item : SplashActivity.anhVietWords){
                        if(item.getWord().equals(word)){
                            wordId = item.getId();
                            break;
                        }
                    }
                    dbAccess.setOpenHelperAnhViet();
                    if(isFavorite){
                        SplashActivity.favoriteAnhVietWordsId.remove(SplashActivity.favoriteAnhVietWordsId.indexOf(Integer.toString(wordId)));
                        dbAccess.removeFromFavorite(wordId);
                        fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                        Snackbar.make(view, "Removed from ENG-VIE favorites", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        isFavorite = false;
                    }
                    else{
                        SplashActivity.favoriteAnhVietWordsId.add(Integer.toString(wordId));
                        dbAccess.addToFavorite(wordId);
                        fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_24);
                        Snackbar.make(view, "Added to ENG-VIE favorites", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        isFavorite = true;
                    }

                }
                else{
                    for(Word item : SplashActivity.vietAnhWords){
                        if(item.getWord().equals(word)){
                            wordId = item.getId();
                            break;
                        }
                    }
                    dbAccess.setOpenHelperVietAnh();
                    if(isFavorite){
                        SplashActivity.favoriteVietAnhWordsId.remove(SplashActivity.favoriteVietAnhWordsId.indexOf(Integer.toString(wordId)));
                        dbAccess.removeFromFavorite(wordId);
                        fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                        Snackbar.make(view, "Removed from VIE-ENG favorites", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        isFavorite = false;
                    }
                    else{
                        SplashActivity.favoriteVietAnhWordsId.add(Integer.toString(wordId));
                        dbAccess.addToFavorite(wordId);
                        fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_24);
                        Snackbar.make(view, "Added to VIE-ENG favorites", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        isFavorite = true;
                    }
                }
                dbAccess.close();
            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                setTextToSpeechLanguage();
            }
        });

        ibPronounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut();
            }
        });
    }

    private Locale getUserSelectedLanguage() {
        if(MainActivity.isAnhViet){
            return Locale.ENGLISH;
        }
        else{
            return Locale.getDefault();
        }
    }

    private boolean ready;

    private void setTextToSpeechLanguage() {
        Locale language = this.getUserSelectedLanguage();
        if (language == null) {
            this.ready = false;
            return;
        }
        int result = textToSpeech.setLanguage(language);
        if (result == TextToSpeech.LANG_MISSING_DATA) {
            this.ready = false;
            return;
        } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
            this.ready = false;
            return;
        } else {
            this.ready = true;
            Locale currentLanguage = textToSpeech.getVoice().getLocale();
        }
    }

    private void speakOut() {
        if (!ready) {
            Toast.makeText(this, "Text to Speech not ready", Toast.LENGTH_LONG).show();
            return;
        }
        // Văn bản cần đọc.
        String toSpeak = word;
        String utteranceId = UUID.randomUUID().toString();
        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }
}