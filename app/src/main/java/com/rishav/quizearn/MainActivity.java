package com.rishav.quizearn;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    private static final int NUM_PAGES = 3;
    private ViewPager viewPager;
    private ScreenSlidePageAdapter pageAdapter;
    SharedPreferences settings;
    boolean b_music;
    View bg;
    ImageView logo;
    Button skip,next;
    LinearLayout layoutOnboardingIndicators;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bg=findViewById(R.id.bg);
        logo=findViewById(R.id.logo);
        skip=findViewById(R.id.skip);
        next=findViewById(R.id.next);
        next.setVisibility(View.GONE);
        skip.setVisibility(View.GONE);
        layoutOnboardingIndicators=findViewById(R.id.layoutOnBoardingIndicator);
        layoutOnboardingIndicators.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                settings= getSharedPreferences("MUSIC_SETTING",MODE_PRIVATE);
                b_music=settings.getBoolean("MUSIC",true);
                if (b_music){
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finish();
                    Toast.makeText(MainActivity.this, "music on", Toast.LENGTH_SHORT).show();
                }else {
                    openOnboarding();
                    Toast.makeText(MainActivity.this, "music off", Toast.LENGTH_SHORT).show();
                }
              //  mSharedPref = getSharedPreferences("SharedPref",MODE_PRIVATE);
               // boolean isFirstTime = mSharedPref.getBoolean("firstTime",true);
                //if(isFirstTime){
                   // onboarding
                   //SharedPreferences.Editor editor = mSharedPref.edit();
                   //editor.putBoolean("firstTime",false);
                   //editor.commit();
                //}else {
                  //  startActivity(new Intent(MainActivity.this, Login.class));
                    //finish();
                //}
            }
        },1500);

    }

    public void openOnboarding(){
        bg.setVisibility(View.GONE);
        logo.setVisibility(View.GONE);
        next.setVisibility(View.VISIBLE);
        skip.setVisibility(View.VISIBLE);
        layoutOnboardingIndicators.setVisibility(View.VISIBLE);

        viewPager=findViewById(R.id.pager);
        pageAdapter = new ScreenSlidePageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageAdapter);
        setOnboardingIndicator();
        setCurrentOnboardingIndicator(0);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setCurrentOnboardingIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() + 1 < pageAdapter.getCount()){
                    viewPager.setCurrentItem(getItem(+1),true);
                }else{
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finish();
                }
            }
        });
    }
    private int getItem(int i){
        return viewPager.getCurrentItem()+i;
    }


    private class ScreenSlidePageAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePageAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(final int position) {
            switch (position){
                case 0:
                OnBoardingFragment1 tab1 = new OnBoardingFragment1();
                return tab1;
                case 1:
                    OnBoardingFragment2 tab2 = new OnBoardingFragment2();
                    return tab2;
                case 2:
                    OnBoardingFragment3 tab3 = new OnBoardingFragment3();
                    return tab3;
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    private void setOnboardingIndicator(){
        ImageView[] indicators = new ImageView[pageAdapter.getCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8,0,8,0);
        for (int i =0; i<indicators.length; i++){
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.onboarding_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentOnboardingIndicator(int index){
        int childCount = layoutOnboardingIndicators.getChildCount();
        for(int i= 0; i<childCount; i++){
            ImageView imageView=(ImageView) layoutOnboardingIndicators.getChildAt(i);
            if(i == index){
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(),R.drawable.onboarding_indicator_active)
                );
            }else{
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(),R.drawable.onboarding_indicator_inactive)
                );
            }
        }
        if(index == pageAdapter.getCount() - 1){
            next.setText("");
            skip.setVisibility(View.GONE);
        }

    }
}