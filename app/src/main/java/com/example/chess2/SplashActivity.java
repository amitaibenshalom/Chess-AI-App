package com.example.chess2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;


import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private Timer myTime; //timer for progressbar
    private ProgressBar prBar; //progressbar
    private int count; //counter for progressbar



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prBar = (ProgressBar) findViewById(R.id.progressBar);

        myTime = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                count = count+5;
                prBar.setProgress(count);
                if (count==100)
                {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    myTime.cancel();
                }
            }
        };
        myTime.schedule(timerTask,0,100);
    }
}