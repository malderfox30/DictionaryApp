package com.example.dictionaryapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.dictionaryapp.MainActivity;
import com.example.dictionaryapp.MyAdapter;
import com.example.dictionaryapp.R;
import com.example.dictionaryapp.SplashActivity;
import com.example.dictionaryapp.model.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class favoriteFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<Word> anhVietWords;
    private ArrayList<Word> anhVietStore;
    private ArrayList<Word> vietAnhWords;
    private ArrayList<Word> vietAnhStore;
    private MyAdapter myAdapter, myAdapter2;
    private SearchView svSearch;
    private ImageButton btnVoice;
    private static final int REQUEST_CODE = 3003;
    private static final int MAX_WORDS = 20;

    public favoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_eng_vie:
                MainActivity.isAnhViet = true;
                myAdapter = new MyAdapter(anhVietWords, getContext());
                recyclerView.setAdapter(myAdapter);
                myAdapter.notifyDataSetChanged();
                break;

            case R.id.item_vie_eng:
                MainActivity.isAnhViet = false;
                myAdapter2 = new MyAdapter(vietAnhWords, getContext());
                recyclerView.setAdapter(myAdapter2);
                myAdapter2.notifyDataSetChanged();
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_favorites, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.recyclerView = view.findViewById(R.id.rv_favorites);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MainActivity.fab.setVisibility(View.INVISIBLE);

        //Mac dinh Anh-Viet favorites
        MainActivity.isAnhViet = true;
        anhVietWords = new ArrayList<Word>();
        for(String item : SplashActivity.favoriteAnhVietWordsId){
            anhVietWords.add(SplashActivity.anhVietWords.get(Integer.parseInt(item) - 1));
        }

        anhVietStore = new ArrayList<Word>();
        anhVietStore.addAll(anhVietWords);
        myAdapter = new MyAdapter(anhVietWords, getContext());


        //add Viet-anh favorites
        vietAnhWords = new ArrayList<Word>();
        for(String item : SplashActivity.favoriteVietAnhWordsId){
            vietAnhWords.add(SplashActivity.vietAnhWords.get(Integer.parseInt(item) - 1));
        }
        vietAnhStore = new ArrayList<Word>();
        vietAnhStore.addAll(vietAnhWords);

        this.recyclerView.setAdapter(myAdapter);

        btnVoice =  view.findViewById(R.id.btn_voice);
        svSearch =  view.findViewById(R.id.sv_search);

        // Disable button if no recognition service is present
        PackageManager pm = getContext().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        if (activities.size() == 0) {
            btnVoice.setEnabled(false);
        }

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String content = newText.toLowerCase(Locale.getDefault());
                if(MainActivity.isAnhViet){
                    anhVietWords.clear();
                    if(content.isEmpty()){
                        anhVietWords.addAll(anhVietStore);
                    }
                    else{
                        int countWords = 0;
                        int size = content.length();
                        for(Word word : anhVietStore){
                            String formattedWord = (word.getWord().toLowerCase(Locale.getDefault()).length() <= size)? word.getWord().toLowerCase(Locale.getDefault()) : word.getWord().toLowerCase(Locale.getDefault()).substring(0, size);
                            if(formattedWord.equals(content) && countWords < MAX_WORDS){
                                anhVietWords.add(word);
                                countWords++;
                            }
                            //if(word.getWord().toLowerCase(Locale.getDefault()).contains(content)) anhVietWords.add(word);
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                }
                else{
                    vietAnhWords.clear();
                    if(content.isEmpty()){
                        vietAnhWords.addAll(vietAnhStore);
                    }
                    else{
                        int countWords = 0;
                        int size = content.length();
                        for(Word word : vietAnhStore){
                            String formattedWord = (word.getWord().toLowerCase(Locale.getDefault()).length() <= size)? word.getWord().toLowerCase(Locale.getDefault()) : word.getWord().toLowerCase(Locale.getDefault()).substring(0, size);
                            if(formattedWord.equals(content) && countWords < MAX_WORDS){
                                vietAnhWords.add(word);
                                countWords++;
                            }
                        }
                    }
                    myAdapter2.notifyDataSetChanged();
                }
                return false;
            }
        });

        btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakButtonClicked(view);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    /**
     * Handle the action of the button being clicked
     */
    public void speakButtonClicked(View v)
    {
        startVoiceRecognitionActivity();
    }
    /**
     * Fire an intent to start the voice recognition activity.
     */
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice searching...");
        startActivityForResult(intent, REQUEST_CODE);
    }
    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            final ArrayList< String > matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (!matches.isEmpty())
            {
                String Query = matches.get(0);
                svSearch.setQuery(Query, false);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}