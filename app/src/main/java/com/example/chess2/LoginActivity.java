package com.example.chess2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private EditText userName, password;
    private Button login, back;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        ConstraintLayout constraintLayout = findViewById(R.id.ly);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        userName = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = (Button)findViewById(R.id.loginBtn);
        login.setOnClickListener(this);

    }

    public void login() {
        String uname = userName.getText().toString();
        String pword = password.getText().toString();

        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild(uname)) {
                    String getPassword = snapshot.child(uname).child("password").getValue(String.class);
                    if (getPassword.equals(pword)) {

                        // login the user with his email and password for the authentication database
                        loginAuth(snapshot.child(uname).child("email").getValue(String.class), pword);
                    }
                    else {
                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("Error!")
                                .setMessage("Wrong Password")
//                      .setIcon(R.drawable.error)
                                .show(); //error icon
                    }
                }
                else {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Error!")
                            .setMessage("User name not existing")
//                      .setIcon(R.drawable.error)
                            .show(); //error icon
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public boolean checkIfValid() {
        if (((String)userName.getText().toString()).length() == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Error!")
                    .setMessage("Fill user name")
//                      .setIcon(R.drawable.error)
                    .show(); //error icon
            return false;
        }
        if (((String)password.getText().toString()).length() == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Error!")
                    .setMessage("Fill password")
//                      .setIcon(R.drawable.error)
                    .show(); //error icon
            return false;
        }
        return true;
    }

    public void loginAuth(String eml, String pword) {

        // login the user with his email and password for the authentication database
        mAuth.signInWithEmailAndPassword(eml, pword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,"Logged in successfully!",
                                    Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

        @Override
    public void onClick(View v) {
            if (v == login) {
                if (checkIfValid()) {
                    if (MainActivity.isNetworkAvailable(this))
                        login();
                    else {
                        new AlertDialog.Builder(this)
                                .setTitle("Error!")
                                .setMessage("No network available")
//                      .setIcon(R.drawable.error)
                                .show(); //error icon
                    }
                }
            } else if (v == back){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
    }
}