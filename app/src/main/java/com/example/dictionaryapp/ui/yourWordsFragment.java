package com.example.dictionaryapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dictionaryapp.MainActivity;
import com.example.dictionaryapp.R;
import com.example.dictionaryapp.SplashActivity;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class yourWordsFragment extends Fragment {

    private static final int REQUEST_CODE = 3005;
    private TextView tvRandomWord, tvLabel, tvScore, tvAnswer;
    private ImageButton ibVoice;
    private Random random;
    private boolean isWordChanged;

    public yourWordsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.fab.setVisibility(View.INVISIBLE);
        isWordChanged = true;
        tvRandomWord = view.findViewById(R.id.tv_random_word);
        tvLabel = view.findViewById(R.id.tv_score_label);
        tvScore = view.findViewById(R.id.tv_score);
        tvAnswer = view.findViewById(R.id.tv_your_answer);
        ibVoice = view.findViewById(R.id.ib_voice);

        ibVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakButtonClicked(view);
                isWordChanged = false;
            }
        });

        random = new Random();
        tvRandomWord.setText(SplashActivity.anhVietWords.get(random.nextInt(SplashActivity.anhVietWords.size() - 257) + 256).getWord());
        tvRandomWord.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                tvRandomWord.setText(SplashActivity.anhVietWords.get(random.nextInt(SplashActivity.anhVietWords.size() - 257) + 256).getWord());
                tvAnswer.setText("Your answer");
                tvAnswer.setTextColor(Color.BLACK);
                tvScore.setText("-");
                isWordChanged = true;
                return false;
            }
        });

        tvAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Check whether the answer is correct or not
                if(isWordChanged){
                    tvScore.setText("-");
                }
                else{
                    //old condition : tvAnswer.getText().toString().equals(tvRandomWord.getText().toString())
                    if(checkPronunciation(tvRandomWord, tvAnswer) == true){
                        tvScore.setText("10đ");
                        tvAnswer.setTextColor(Color.GREEN);
                    }
                    else{
                        tvScore.setText("0đ");
                        tvAnswer.setTextColor(Color.RED);
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_your_words, container, false);
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
                tvAnswer.setText(Query);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean checkPronunciation(TextView word, TextView  input){
        String formattedInput = input.getText().toString().toLowerCase(Locale.getDefault()).trim();
        if(formattedInput.substring(formattedInput.length()).equals("s")){
            formattedInput = formattedInput.substring(0, formattedInput.length() - 1);
        }

        String formattedWord = word.getText().toString().toLowerCase(Locale.getDefault()).trim();
        formattedWord.replace("-", " ");
        if(formattedWord.equals(formattedInput)){
            return true;
        }
        else{
            return false;
        }
    }
}