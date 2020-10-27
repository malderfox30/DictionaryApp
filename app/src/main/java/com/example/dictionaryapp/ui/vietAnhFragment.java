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

import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.dictionaryapp.DatabaseAccess;
import com.example.dictionaryapp.MainActivity;
import com.example.dictionaryapp.MyAdapter;
import com.example.dictionaryapp.R;
import com.example.dictionaryapp.SplashActivity;
import com.example.dictionaryapp.model.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class vietAnhFragment extends Fragment {
    private RecyclerView vietAnhRecyclerView;
    private MyAdapter myAdapter;
    private ArrayList<Word> vietAnhWords;
    private ArrayList<Word> vietAnhStore;
    private EditText edtSearch;
    private ImageButton btnVoice;
    private static final int REQUEST_CODE = 3002;
    private static final int MAX_WORDS = 20;

    public vietAnhFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.vietAnhRecyclerView = view.findViewById(R.id.rv_viet_anh);
        this.vietAnhRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        vietAnhWords = new ArrayList<Word>(SplashActivity.vietAnhWords);
        //Remove credits
        for(int i = 0; i < 3; i++){
            vietAnhWords.remove(0);
        }

        vietAnhStore = new ArrayList<Word>();
        vietAnhStore.addAll(vietAnhWords);
        myAdapter = new MyAdapter(vietAnhWords, getContext());
        this.vietAnhRecyclerView.setAdapter(myAdapter);


        btnVoice =  view.findViewById(R.id.btn_voice);
        edtSearch = (EditText) view.findViewById(R.id.edt_search);

        // Disable button if no recognition service is present
        PackageManager pm = getContext().getPackageManager();
        List <ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        if (activities.size() == 0) {
            btnVoice.setEnabled(false);
            //btnVoice.setText("Recognizer not present");
        }
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = s.toString().toLowerCase(Locale.getDefault());
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
                myAdapter.notifyDataSetChanged();
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
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
        return inflater.inflate(R.layout.fragment_viet_anh, container, false);
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
                edtSearch.setText(Query);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}