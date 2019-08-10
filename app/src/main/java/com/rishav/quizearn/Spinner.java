package com.rishav.quizearn;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rishav.quizearn.SpinWheel.LuckyWheelView;
import com.rishav.quizearn.SpinWheel.model.LuckyItem;
import com.rishav.quizearn.databinding.ActivitySpinnerBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Spinner extends AppCompatActivity {
    ActivitySpinnerBinding binding;
    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpinnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        check=true;


        List<LuckyItem> data = new ArrayList<>();

        LuckyItem item1 = new LuckyItem();
        item1.topText = "10";
        item1.secondaryText = "Coins";
        item1.color= Color.parseColor("#ffffff");
        item1.textColor=Color.parseColor("#000000");
        data.add(item1);

        LuckyItem item2 = new LuckyItem();
        item2.topText = "50";
        item2.secondaryText = "Coins";
        item2.color= Color.parseColor("#FF0000");
        item2.textColor=Color.parseColor("#FFFFFF");
        data.add(item2);

        LuckyItem item3 = new LuckyItem();
        item3.topText = "5";
        item3.secondaryText = "Coins";
        item3.color= Color.parseColor("#ffffff");
        item3.textColor=Color.parseColor("#000000");
        data.add(item3);

        LuckyItem item4 = new LuckyItem();
        item4.topText = "20";
        item4.secondaryText = "Coins";
        item4.color= Color.parseColor("#FFFF00");
        item4.textColor=Color.parseColor("#FFFFFF");
        data.add(item4);

        LuckyItem item5 = new LuckyItem();
        item5.topText = "10";
        item5.secondaryText = "Coins";
        item5.color= Color.parseColor("#ffffff");
        item5.textColor=Color.parseColor("#000000");
        data.add(item5);

        LuckyItem item6 = new LuckyItem();
        item6.topText = "30";
        item6.secondaryText = "Coins";
        item6.color= Color.parseColor("#0000FF");
        item6.textColor=Color.parseColor("#FFFFFF");
        data.add(item6);

        LuckyItem item7 = new LuckyItem();
        item7.topText = "0";
        item7.secondaryText = "Coins";
        item7.color= Color.parseColor("#ffffff");
        item7.textColor=Color.parseColor("#000000");
        data.add(item7);

        LuckyItem item8 = new LuckyItem();
        item8.topText = "25";
        item8.secondaryText = "Coins";
        item8.color= Color.parseColor("#00FF00");
        item8.textColor=Color.parseColor("#FFFFFF");
        data.add(item8);

        binding.wheelview.setData(data);
        binding.wheelview.setRound(5);

        binding.spinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();
                int random = r. nextInt(8);

                binding.wheelview.startLuckyWheelWithTargetIndex(random);
            }
        });

        binding.wheelview.setLuckyRoundItemSelectedListener(new LuckyWheelView.LuckyRoundItemSelectedListener() {
            @Override
            public void LuckyRoundItemSelected(int index) {
                if(check==true){
                    updateCash(index);
                }else{
                    finish();
                }
            }
        });

    }


    void updateCash(int index){
        long cash = 0;
        switch (index){
            case 0:
                cash=10;
                break;
            case 1:
                cash=50;
                break;
            case 2:
                cash=5;
                break;
            case 3:
                cash=20;
                break;
            case 4:
                cash=10;
                break;
            case 5:
                cash=30;
                break;
            case 6:
                cash=0;
                break;
            case 7:
                cash=25;
                break;

        }

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .update("coins", FieldValue.increment(cash)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Spinner.this, "Coins added to your Account", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        check=false;
        finish();
    }

}