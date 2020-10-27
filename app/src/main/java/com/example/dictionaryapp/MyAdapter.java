package com.example.dictionaryapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dictionaryapp.model.Word;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private static ArrayList<Word> mWords;
    private static Context mContext;

    public MyAdapter(ArrayList<Word> mWords, Context mContext) {
        this.mWords = mWords;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_word, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvWord.setText(mWords.get(position).getWord());

        //Xử lí chuỗi in ra nghĩa
        String meaning = "";
        if(mWords.get(position).getContent().split("<ul>|</ul>").length > 1){
            String[] meaningsArray = mWords.get(position).getContent().split("<ul>|</ul>")[1].split("<li>|</li>");
            for(String item : meaningsArray){
                if(item.length() != 0) {
                    meaning += item + ", ";
                }
            }
            meaning = meaning.substring(0, meaning.length()-2);
        }
        holder.tvDefinition.setText(meaning);
    }

    @Override
    public int getItemCount() {
        return mWords.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public View view;
        private TextView tvWord;
        private TextView tvDefinition;
        public MyViewHolder(View view){
            super(view);
            this.view = view;
            tvWord = view.findViewById(R.id.tv_word);
            tvDefinition = view.findViewById(R.id.tv_definition);

            tvWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, DefinitionActivity.class);
                    intent.putExtra("word", tvWord.getText().toString());
                    mContext.startActivity(intent);
                }
            });
        }
    }
}

