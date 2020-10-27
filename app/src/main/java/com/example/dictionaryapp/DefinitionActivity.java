package com.example.dictionaryapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DefinitionActivity extends AppCompatActivity {
    private TextView tvDefinition, tvWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definition);

        tvDefinition = findViewById(R.id.tv_definition);
        tvWord = findViewById(R.id.tv_word);

        //Lấy dữ liệu form kia gởi qua
        Intent intent = getIntent();
        String word = intent.getStringExtra("word");
        tvWord.setText(word);

        //query định nghĩa của từ từ csdl
        DatabaseAccess dbAccess = DatabaseAccess.getInstance(this);
        dbAccess.setOpenHelperAnhViet();
        String definition = dbAccess.getDefinition(word);
        dbAccess.close();

        //Hiển thị trên textView
        tvDefinition.setText(Html.fromHtml(definition));
    }

}
