package com.omotion.contentsx.android.licon.ui.player.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.omotion.contentsx.android.licon.R;


public class ARActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        ImageButton backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left); // slide down 애니메이션
            }
        });
    }
}