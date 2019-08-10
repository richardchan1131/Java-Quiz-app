package com.rishav.quizearn;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

public class Settings extends Activity {
    ImageButton back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        back=findViewById(R.id.back);

        if (findViewById(R.id.settingsContent)!=null){
            if (savedInstanceState!=null)
                return;
            getFragmentManager().beginTransaction().add(R.id.settingsContent,new settingsFragment()).commit();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
