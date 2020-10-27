package com.example.dictionaryapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dictionaryapp.model.Word;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.PropertyPermission;

public class DefinitionActivity extends AppCompatActivity {
    private TextView tvDefinition, tvWord;
    private FloatingActionButton fabFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definition);

        tvDefinition = findViewById(R.id.tv_definition);
        tvWord = findViewById(R.id.tv_word);
        fabFavorite = findViewById(R.id.fab_favorite);

        //Lấy dữ liệu form kia gởi qua
        Intent intent = getIntent();
        final String word = intent.getStringExtra("word");
        tvWord.setText(word);

        //query định nghĩa của từ từ csdl
        final DatabaseAccess dbAccess = DatabaseAccess.getInstance(this);
        if(MainActivity.isAnhViet){
            dbAccess.setOpenHelperAnhViet();
        }
        else{
            dbAccess.setOpenHelperVietAnh();
        }
        String definition = dbAccess.getDefinition(word, MainActivity.isAnhViet);
        dbAccess.close();

        //Hiển thị trên textView
        tvDefinition.setText(Html.fromHtml(definition));

        fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Added to favorites", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                final DatabaseAccess dbAccess = DatabaseAccess.getInstance(getApplicationContext());
                int wordId = 0;
                if (MainActivity.isAnhViet == true) {
                    for(Word item : SplashActivity.anhVietWords){
                        if(item.getWord().equals(word)){
                            wordId = item.getId();
                            break;
                        }
                    }
                    SplashActivity.favoriteAnhVietWordsId.add(Integer.toString(wordId));
                    dbAccess.setOpenHelperAnhViet();
                }
                else{
                    for(Word item : SplashActivity.vietAnhWords){
                        if(item.getWord().equals(word)){
                            wordId = item.getId();
                            break;
                        }
                    }
                    SplashActivity.favoriteVietAnhWordsId.add(Integer.toString(wordId));
                    dbAccess.setOpenHelperVietAnh();
                }
                dbAccess.addToFavorite(wordId);
                dbAccess.close();
            }
        });
    }

}
