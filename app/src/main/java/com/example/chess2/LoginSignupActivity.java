package com.example.chess2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class LoginSignupActivity extends AppCompatActivity implements View.OnClickListener {

    private Button loginBtn, signUpBtn;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        signUpBtn = (Button) findViewById(R.id.signUpBtn);
        signUpBtn.setOnClickListener(this);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);

        ConstraintLayout constraintLayout = findViewById(R.id.ly);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i==TextToSpeech.SUCCESS) {
                    int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });
    }

    public void onClick(View view){

        if (view == loginBtn) {
            textToSpeech.speak("Login Page",TextToSpeech.QUEUE_FLUSH,null);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        if (view == signUpBtn) {
            textToSpeech.speak("Sign up Page",TextToSpeech.QUEUE_FLUSH,null);
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }
    }
}