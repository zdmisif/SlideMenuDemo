package com.hzz.slidemenudemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View mRedBtn = findViewById(R.id.red_btn);
        View mBlueBtn = findViewById(R.id.blue_btn);
        LinearLayout content = (LinearLayout) findViewById(R.id.content);
        SlideMenuLayout slideMenu = (SlideMenuLayout) findViewById(R.id.slide_menu);
        mBlueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Blue");
            }
        });
        mRedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Red");
            }
        });
        slideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Slide Menu Click");
            }
        });
    }

    private void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

}
