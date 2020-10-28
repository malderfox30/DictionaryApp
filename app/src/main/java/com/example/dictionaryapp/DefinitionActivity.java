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
    private TextView tvDefinition;
    private FloatingActionButton fabFavorite;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definition);

        isFavorite = false;
        tvDefinition = findViewById(R.id.tv_definition);
        fabFavorite = findViewById(R.id.fab_favorite);

        //Lấy dữ liệu form kia gởi qua
        Intent intent = getIntent();
        final String word = intent.getStringExtra("word");
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
        tvDefinition.setText(Html.fromHtml(definition));

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
                        SplashActivity.favoriteAnhVietWordsId.remove(SplashActivity.favoriteAnhVietWordsId.indexOf(Integer.toString(wordId)));  //TODO: FIX index
                        dbAccess.removeFromFavorite(wordId);
                        fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                        Snackbar.make(view, "Removed to favorites", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        isFavorite = false;
                    }
                    else{
                        SplashActivity.favoriteAnhVietWordsId.add(Integer.toString(wordId));
                        dbAccess.addToFavorite(wordId);
                        fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_24);
                        Snackbar.make(view, "Added to favorites", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
                        SplashActivity.favoriteAnhVietWordsId.remove(SplashActivity.favoriteAnhVietWordsId.indexOf(Integer.toString(wordId)));
                        dbAccess.removeFromFavorite(wordId);
                        fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                        Snackbar.make(view, "Removed to favorites", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        isFavorite = false;
                    }
                    else{
                        SplashActivity.favoriteVietAnhWordsId.add(Integer.toString(wordId));
                        dbAccess.addToFavorite(wordId);
                        fabFavorite.setImageResource(R.drawable.ic_baseline_favorite_24);
                        Snackbar.make(view, "Added to favorites", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        isFavorite = true;
                    }

                }
                dbAccess.close();
            }
        });
    }

}
