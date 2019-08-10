package com.rishav.quizearn;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.SwitchPreference;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.rishav.quizearn.databinding.ActivityQuizBinding;

import java.util.ArrayList;
import java.util.Random;

public class Quiz extends AppCompatActivity {

    ActivityQuizBinding binding;

    ArrayList<Question> questions;
    int index=0;
    Question question;
    CountDownTimer timer;
    int correctAnswers = 0;
    ProgressDialog dialog;

    FirebaseFirestore database;
    private InterstitialAd mInterstitialAd;

    CircularProgressBar circularProgressBar;
    SegmentedProgressBar mSegmentedProgressBar;
    ArrayList<Integer> arrayList=new ArrayList<>();;
    int progress;
    int i=0;
    boolean isPressedOnce=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        circularProgressBar = findViewById(R.id.circularProgressBar);
        mSegmentedProgressBar = findViewById(R.id.segmented_pb_1);
        mSegmentedProgressBar.setBackgroundColor(getResources().getColor(R.color.txtbox_bor));
        arrayList.add(progress);
        addprogressBar();
        //mSegmentedProgressBar.setEnabledDivisions(arrayList);

        hideCorrectAndeWrongAnimations();

        //with gradient
        circularProgressBar.setProgressBarColorStart(getResources().getColor(R.color.greenblue));
        circularProgressBar.setProgressBarColorEnd(getResources().getColor(R.color.bluishgreen));
        circularProgressBar.setProgressBarColorDirection(CircularProgressBar.GradientDirection.RIGHT_TO_LEFT);
        circularProgressBar.setProgressMax(100);
        circularProgressBar.setBackgroundProgressBarColor(getResources().getColor(R.color.txtbox_bor));

       // Set Width
        circularProgressBar.setProgressBarWidth(7f); // in DP
        circularProgressBar.setBackgroundProgressBarWidth(7f); // in DP

       // Other
        circularProgressBar.setRoundBorder(true);
        //circularProgressBar.setStartAngle(180f);
        //circularProgressBar.setProgressDirection(CircularProgressBar.ProgressDirection.TO_RIGHT);





        final AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    timer.cancel();
                }
                //resetTimer();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                timer.start();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mInterstitialAd.loadAd(adRequest);
            }
        });
        binding.adView.loadAd(adRequest);


        dialog = new ProgressDialog(this);
        dialog.setMessage("Starting Your Quiz..Wait.");
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();

        database = FirebaseFirestore.getInstance();
        questions=new ArrayList<>();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        final String catId = extras.getString("catId");
        final String catName = extras.getString("catName");
        Random random = new Random();
        final int rand = random.nextInt(5);
        binding.categoryName.setText(catName+" "+"Quiz");
        database.collection("categories")
                .document(catId)
                .collection("questions")
                .whereGreaterThanOrEqualTo("index",rand)
                .orderBy("index")
                .limit(10)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() < 5){
                    database.collection("categories")
                            .document(catId)
                            .collection("questions")
                            .whereLessThanOrEqualTo("index",rand)
                            .orderBy("index")
                            .limit(10)
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(DocumentSnapshot snapshot : queryDocumentSnapshots){
                                    Question question= snapshot.toObject(Question.class);
                                    questions.add(question);
                                }
                            setNextQuestion();
                            dialog.dismiss();
                            System.out.println(rand);
                        }
                    });
                }else{
                           for(DocumentSnapshot snapshot : queryDocumentSnapshots){
                               Question question= snapshot.toObject(Question.class);
                               questions.add(question);
                           }
                    setNextQuestion();
                    dialog.dismiss();
                    System.out.println(rand);

                    }
                }
        });
        resetTimer();

        binding.quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                finish();
            }
        });


    }

    private void hideCorrectAndeWrongAnimations() {
        binding.option1correct.setVisibility(View.GONE);
        binding.option2correct.setVisibility(View.GONE);
        binding.option3correct.setVisibility(View.GONE);
        binding.option4correct.setVisibility(View.GONE);
        binding.option1wrong.setVisibility(View.GONE);
        binding.option2wrong.setVisibility(View.GONE);
        binding.option3wrong.setVisibility(View.GONE);
        binding.option4wrong.setVisibility(View.GONE);

    }

    void resetTimer(){
        //Pause the progress
        i=0;
        circularProgressBar.setProgressMax(100);
        circularProgressBar.setProgress(i);
        timer = new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                i++;
                binding.timer.setText(String.valueOf(millisUntilFinished/1000));
                circularProgressBar.setProgressWithAnimation((float)i*100/(30000/1000), (long) 1000);

            }

            @Override
            public void onFinish() {
                addprogressBar();
                i=0;
                circularProgressBar.setProgress(0);
                reset();
                if((index+1) < questions.size()){
                    index++;
                    timer.cancel();
                    setNextQuestion();
                }else{
                    timer.cancel();
                    Intent intent = new Intent(Quiz.this,Result.class);
                    intent.putExtra("correct",correctAnswers);
                    intent.putExtra("total",questions.size());
                    startActivity(intent);
                    finish();
                }

            }
        };
    }

    void showAnswer(){
        mSegmentedProgressBar.setProgressBarColor(getResources().getColor(R.color.light_green));
        mSegmentedProgressBar.setEnabledDivisions(arrayList);
        if(question.getAnswer().equals(binding.option1.getText().toString())){
            binding.option1.setBackground(getResources().getDrawable(R.drawable.right_opt));
            binding.option1.setTextColor(getResources().getColor(R.color.light_green));
        }
        else if(question.getAnswer().equals(binding.option2.getText().toString())){
            binding.option2.setBackground(getResources().getDrawable(R.drawable.right_opt));
            binding.option2.setTextColor(getResources().getColor(R.color.light_green));
        }
        else if(question.getAnswer().equals(binding.option3.getText().toString())){
            binding.option3.setBackground(getResources().getDrawable(R.drawable.right_opt));
            binding.option3.setTextColor(getResources().getColor(R.color.light_green));
        }
        else if(question.getAnswer().equals(binding.option4.getText().toString())){
        binding.option4.setBackground(getResources().getDrawable(R.drawable.right_opt));
            binding.option4.setTextColor(getResources().getColor(R.color.light_green));
        }
    }

    void setNextQuestion(){
        isPressedOnce=true;
        if(timer!=null){
            timer.cancel();
        }
        timer.start();
        if(index < questions.size()){
            mSegmentedProgressBar.setDivisions(questions.size());
            binding.quixcounter.setText("/"+questions.size());
           // binding.quixcounter.setText(String.format("%d/%d",(index+1),questions.size()));
            binding.currentNo.setText(""+(index+1));
            binding.questionNo.setText("Q"+(index+1)+".");
            question = questions.get(index);
            binding.question.setText(question.getQuestion());
            binding.option1.setText(question.getOption1());
            binding.option2.setText(question.getOption2());
            binding.option3.setText(question.getOption3());
            binding.option4.setText(question.getOption4());

        }
    }
    void checkAnswer(TextView textview){
        String selectedAnswer = textview.getText().toString();
        if(selectedAnswer.equals(question.getAnswer())){
            correctAnswers++;
            textview.setBackground(getResources().getDrawable(R.drawable.right_opt));
            textview.setTextColor(getResources().getColor(R.color.light_green));
            mSegmentedProgressBar.setProgressBarColor(getResources().getColor(R.color.light_green));
            mSegmentedProgressBar.setEnabledDivisions(arrayList);
            int id = textview.getId();
            if (id==R.id.option1){
                binding.option1correct.setVisibility(View.VISIBLE);
            }else if(id==R.id.option2){
                binding.option2correct.setVisibility(View.VISIBLE);
            }else if(id==R.id.option3){
                binding.option3correct.setVisibility(View.VISIBLE);
            }else if(id==R.id.option4){
                binding.option4correct.setVisibility(View.VISIBLE);
            }
        }else {
            showAnswer();
            textview.setBackground(getResources().getDrawable(R.drawable.wrong_opt));
            textview.setTextColor(getResources().getColor(R.color.light_red));
            mSegmentedProgressBar.setProgressBarColor(getResources().getColor(R.color.light_red));
            mSegmentedProgressBar.setEnabledDivisions(arrayList);
            int id = textview.getId();
            if (id==R.id.option1){
                binding.option1wrong.setVisibility(View.VISIBLE);
            }else if(id==R.id.option2){
                binding.option2wrong.setVisibility(View.VISIBLE);
            }else if(id==R.id.option3){
                binding.option3wrong.setVisibility(View.VISIBLE);
            }else if(id==R.id.option4){
                binding.option4wrong.setVisibility(View.VISIBLE);
            }
        }
    }

    void reset(){
        hideCorrectAndeWrongAnimations();
        binding.option1.setBackground(getResources().getDrawable(R.drawable.quiz_text_box));
        binding.option2.setBackground(getResources().getDrawable(R.drawable.quiz_text_box));
        binding.option3.setBackground(getResources().getDrawable(R.drawable.quiz_text_box));
        binding.option4.setBackground(getResources().getDrawable(R.drawable.quiz_text_box));
        binding.option1.setTextColor(getResources().getColor(R.color.opt_text));
        binding.option2.setTextColor(getResources().getColor(R.color.opt_text));
        binding.option3.setTextColor(getResources().getColor(R.color.opt_text));
        binding.option4.setTextColor(getResources().getColor(R.color.opt_text));
        mSegmentedProgressBar.setProgressBarColor(getResources().getColor(R.color.bluishgreen));
    }

    public void onClick (View view){
              switch (view.getId()){
                  case R.id.option1:
                  case R.id.option2:
                  case R.id.option3:
                  case R.id.option4:
                      if (isPressedOnce){
                          isPressedOnce=false;
                          if (timer!=null){
                              timer.cancel();
                          }
                          TextView selected = (TextView)view;
                          checkAnswer(selected);
                      }
                      break;
                  case R.id.next:
                      addprogressBar();
                      reset();
                      timer.cancel();
                      if((index+1) < questions.size()){
                          index++;
                          resetTimer();
                          setNextQuestion();
                      }else{
                          Intent intent = new Intent(Quiz.this,Result.class);
                          intent.putExtra("correct",correctAnswers);
                          intent.putExtra("total",questions.size());
                          startActivity(intent);
                          finish();
                      }
                      break;
              }
    }

    public void addprogressBar(){
        if(progress==0)
        {
            arrayList.add(progress);
        }
        else
        {
            arrayList.add(progress);
        }
        progress=progress+1;
        mSegmentedProgressBar.setEnabledDivisions(arrayList);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        timer.cancel();
        finish();
    }
}