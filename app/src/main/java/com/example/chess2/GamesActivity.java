package com.example.chess2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class GamesActivity extends AppCompatActivity implements View.OnClickListener {

    private Button multiplayerBtn, aiBtn, localBtn;
    private ImageButton backBtn;
    private String pUserName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        if (getIntent().getExtras() != null) {
            pUserName = getIntent().getExtras().getString("USERNAME").toString();
        }

        ConstraintLayout constraintLayout = findViewById(R.id.ly);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        multiplayerBtn = findViewById(R.id.multiplyBtn);
        multiplayerBtn.setOnClickListener(this);
        aiBtn = findViewById(R.id.aiBtn);
        aiBtn.setOnClickListener(this);
        localBtn = findViewById(R.id.localBtn);
        localBtn.setOnClickListener(this);
        backBtn = findViewById(R.id.btnBack);
        backBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == multiplayerBtn) {
            if (!MainActivity.isNetworkAvailable(this)) {
                new AlertDialog.Builder(this)
                        .setTitle("Error!")
                        .setMessage("No network available")
//                      .setIcon(R.drawable.error)
                        .show(); //error icon
            }
            else {
                Intent intent = new Intent(this, RoomsActivity.class);
                intent.putExtra("USERNAME",pUserName);
                startActivity(intent);
            }
        }
        if (v == aiBtn) {
            Intent intent = new Intent(this, AiLevelsActivity.class);
            intent.putExtra("USERNAME",pUserName);
            startActivity(intent);
        }
        if (v == localBtn) {
            Intent intent = new Intent(this, TwoPlayerActivity.class);
            startActivity(intent);
        }
        if (v == backBtn) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}