package com.example.chess2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class AiLevelsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button easyBtn, medBtn, hardBtn;
    private ImageButton backBtn;
    private String pUserName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_levels);

        if (getIntent().getExtras() != null) {
            pUserName = getIntent().getExtras().getString("USERNAME").toString();
        }

        ConstraintLayout constraintLayout = findViewById(R.id.ly);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        easyBtn = findViewById(R.id.easyBtn);
        easyBtn.setOnClickListener(this);
        medBtn = findViewById(R.id.medBtn);
        medBtn.setOnClickListener(this);
        hardBtn = findViewById(R.id.hardBtn);
        hardBtn.setOnClickListener(this);
        backBtn = findViewById(R.id.btnBack);
        backBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == easyBtn) {
            Intent intent = new Intent(this, AiActivity.class);
            intent.putExtra("USERNAME",pUserName);
            intent.putExtra("LEVEL",1);
            startActivity(intent);
        }
        if (v == medBtn) {
            Intent intent = new Intent(this, AiActivity.class);
            intent.putExtra("USERNAME",pUserName);
            intent.putExtra("LEVEL",2);
            startActivity(intent);
        }
        if (v == hardBtn) {
            Intent intent = new Intent(this, AiHardActivity.class);
            intent.putExtra("USERNAME",pUserName);
            startActivity(intent);
        }
        if (v == backBtn) {
            Intent intent = new Intent(this, GamesActivity.class);
            startActivity(intent);
            finish();
        }
    }
}