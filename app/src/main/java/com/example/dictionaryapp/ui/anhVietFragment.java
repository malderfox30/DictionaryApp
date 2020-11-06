package com.example.dictionaryapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
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

public class anhVietFragment extends Fragment {
    private RecyclerView anhVietRecyclerView;
    private ArrayList<Word> anhVietWords;
    private ArrayList<Word> anhVietStore;
    private MyAdapter myAdapter;
    private SearchView svSearch;
    private ImageButton btnVoice;
    private static final int REQUEST_CODE = 3001;
    private static final int MAX_WORDS = 20;

    public anhVietFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.anhVietRecyclerView = view.findViewById(R.id.rv_anh_viet);
        this.anhVietRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MainActivity.fab.setVisibility(View.VISIBLE);

        MainActivity.isAnhViet = true;
        anhVietWords = new ArrayList<Word>(SplashActivity.anhVietWords);
        //Remove credits
        for(int i = 0; i < 3; i++){
            anhVietWords.remove(57);
        }

        anhVietStore = new ArrayList<Word>();
        anhVietStore.addAll(anhVietWords);
        myAdapter = new MyAdapter(anhVietWords, getContext());
        this.anhVietRecyclerView.setAdapter(myAdapter);

        btnVoice =  view.findViewById(R.id.btn_voice);
        svSearch = view.findViewById(R.id.sv_search);

        // Disable button if no recognition service is present
        PackageManager pm = getContext().getPackageManager();
        List <ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        if (activities.size() == 0) {
            btnVoice.setEnabled(false);
            //btnVoice.setText("Recognizer not present");
        }
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String content = s.toLowerCase(Locale.getDefault());
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
        return inflater.inflate(R.layout.fragment_anh_viet, container, false);
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