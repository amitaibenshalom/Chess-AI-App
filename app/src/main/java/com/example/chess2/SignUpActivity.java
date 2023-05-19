package com.example.chess2;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText userName, password, email, password2, name;
    private Button register, back;
    private ImageButton contactBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private static final int RESULTPICK=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        ConstraintLayout constraintLayout = findViewById(R.id.ly);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        userName = findViewById(R.id.username);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);
        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        register = findViewById(R.id.register);
        register.setOnClickListener(this);

        back = (Button)findViewById(R.id.btnBack);
        back.setOnClickListener(this);

        contactBtn = (ImageButton) findViewById(R.id.contactBtn);
        contactBtn.setOnClickListener(this);

    }

    public void register() {
        final boolean[] isOk = {true};
        // get the input from the text boxes
        String uname = userName.getText().toString();
        String pword = password.getText().toString();
        String eml = email.getText().toString();
        String sname = name.getText().toString();

        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // if the user name is already in the database
                if (snapshot.hasChild(uname)) {
//                    Toast.makeText(SignUpActivity.this, "User Name already exists", Toast.LENGTH_LONG).show();
                    new AlertDialog.Builder(SignUpActivity.this)
                            .setTitle("Error!")
                            .setMessage("User name already exists")
//                      .setIcon(R.drawable.error)
                            .show(); //error icon
                }
                else {
                    // get a list of all the children (users)
                    Iterable<DataSnapshot> snapshots = snapshot.getChildren();
                    // check if the email is not already used for another account
                    for (DataSnapshot sn : snapshots) {
                        if (sn.child("email").getValue(String.class).equals(eml)) {
//                            Toast.makeText(SignUpActivity.this, "Email is already used", Toast.LENGTH_LONG).show();
                            new AlertDialog.Builder(SignUpActivity.this)
                                    .setTitle("Error!")
                                    .setMessage("Email is already used")
//                      .setIcon(R.drawable.error)
                                    .show(); //error icon
                            isOk[0] = false;
                        }
                    }
                    // if the email is not used (and the username), create a new user account in realtime db
                    if (isOk[0]){
                        // set the information in the db
                        databaseReference.child("Users").child(uname).child("email").setValue(eml);
                        databaseReference.child("Users").child(uname).child("password").setValue(pword);
                        databaseReference.child("Users").child(uname).child("name").setValue(sname);
                        databaseReference.child("Users").child(uname).child("rating").setValue(0);
                        Toast.makeText(SignUpActivity.this, "User registered successfully in realtime database", Toast.LENGTH_LONG).show();
                        // register the user in the authentication database
                        registerAuth(eml,pword);
                        // go to the main activity
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void registerAuth(String eml, String pword) {
        mAuth.createUserWithEmailAndPassword(eml, pword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "User registered successfully in authentication database!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Email is already used",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public boolean checkIfValid() {
        if ((userName.getText().toString()).length() < 3) {
            new AlertDialog.Builder(this)
                    .setTitle("Error!")
                    .setMessage("User name must be at least 3 characters long")
//                      .setIcon(R.drawable.error)
                    .show(); //error icon
            return false;
        }
        if (name.getText().toString().length() == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Error!")
                    .setMessage("Invalid Name")
//                      .setIcon(R.drawable.error)
                    .show(); //error icon
            return false;
        }
        if (email.getText().toString().length() == 0 || !email.getText().toString().contains("@")) {
            new AlertDialog.Builder(this)
                    .setTitle("Error!")
                    .setMessage("Invalid Email")
//                      .setIcon(R.drawable.error)
                    .show(); //error icon
            return false;
        }

        if ((password.getText().toString()).length() < 6) {
            new AlertDialog.Builder(this)
                    .setTitle("Error!")
                    .setMessage("Password must be at least 6 characters long")
//                      .setIcon(R.drawable.error)
                    .show(); //error icon
            return false;
        }

        if (!(password.getText().toString()).equals(password2.getText().toString())) {
            new AlertDialog.Builder(this)
                    .setTitle("Error!")
                    .setMessage("Password fields are not equal")
//                      .setIcon(R.drawable.error)
                    .show(); //error icon
            return false;
        }
        return true;
    }

    public void onClick(View view) {
        if (view == register) {
            if (checkIfValid()) {
                if (MainActivity.isNetworkAvailable(this))
                    register();
                else {
                    new AlertDialog.Builder(this)
                            .setTitle("Error!")
                            .setMessage("No network available")
//                      .setIcon(R.drawable.error)
                            .show(); //error icon
                }
            }
        } else if (view == back){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        if (view == contactBtn) {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(intent,RESULTPICK);
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_OK && requestCode ==1)
        {
            Uri uri = data.getData();

            Cursor cursor = getContentResolver().query(uri,null,null,null,null);
            cursor.moveToFirst();

            // int phoneIndexNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int phoneIndexName = cursor.getColumnIndex
                    (ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            // String phoneNumber = cursor.getString(phoneIndexNumber);
            String phoneName = cursor.getString(phoneIndexName);
            name.setText(phoneName);
        }
    }
}