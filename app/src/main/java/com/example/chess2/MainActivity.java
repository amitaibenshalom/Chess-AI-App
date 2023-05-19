package com.example.chess2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.text.InputType;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseDatabase database;
    DatabaseReference usersRef;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private Button startBtn, closeBtn, rulesBtn, logOutBtn, leaderboardBtn, musicBtn;
    private TextView userText, ratingText;
    private TextToSpeech textToSpeech;
    private FirebaseAuth mAuth;
    private boolean loggedIn;
    private Dialog dialog, rulesDialog;
    private Handler handler = new Handler();
    public User currentUserData;
    public User[] bestUsers;
    private Switch switchMusic;
    public static MusicService musicService;
    private Intent playIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstraintLayout constraintLayout = findViewById(R.id.ly);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        bestUsers = new User[5];
        currentUserData = new User("",0);

        usersRef = FirebaseDatabase.getInstance().getReference("Users/nu");
        addUsersEventListener();
        usersRef.setValue("a");
        usersRef.removeValue();

        mAuth = FirebaseAuth.getInstance();
        userText = (TextView) findViewById(R.id.userText);
        ratingText = (TextView) findViewById(R.id.ratingText);
        leaderboardBtn = findViewById(R.id.leaderBoardBtn);
        leaderboardBtn.setOnClickListener(this);


        logOutBtn = (Button) findViewById(R.id.logOutBtn);
        logOutBtn.setOnClickListener(this);
        startBtn = (Button) findViewById(R.id.startBtn);
        startBtn.setOnClickListener(this);
        closeBtn = (Button) findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(this);
        rulesBtn = (Button) findViewById(R.id.rulesBtn);
        rulesBtn.setOnClickListener(this);


        // music
        switchMusic = (Switch) findViewById(R.id.swMusic);
        switchMusic.setOnClickListener(this);

        musicBtn = (Button) findViewById(R.id.musicBtn);
        musicBtn.setOnClickListener(this);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

        if (!isNetworkAvailable(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("Error!")
                    .setMessage("No network available")
//                      .setIcon(R.drawable.error)
                    .show(); //error icon
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            logOutBtn.setText("log out");
            loggedIn = true;
            if (isNetworkAvailable(this)) {
                Toast.makeText(MainActivity.this, "User is connected", Toast.LENGTH_LONG).show();
                setUserNameText();
                setRatingTextListener();
                databaseReference.child("Users/a").setValue("a");
                databaseReference.child("Users/a").removeValue();
            }
            else{
                userText.setText("Cannot load user");
                ratingText.setText("No network available");
            }
        } else {
            logOutBtn.setText("log in");
            loggedIn = false;
            Toast.makeText(MainActivity.this, "No user is logged in", Toast.LENGTH_LONG).show();
            userText.setText("No user connected");
            ratingText.setText("");
        }

        // music service
        musicService = new MusicService();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

//    conect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicService = binder.getService();
            // pass list
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.btnMusic:
                Intent intent = new Intent(this, SongsActivity.class);
                startActivity(intent);
                break;
            case R.id.exitBtn:
                stopService(playIntent);
                musicService = null;
                finishAffinity();
//                musicService.stopPlayMusic();
                finishAndRemoveTask();
                finish();
                break;
            case R.id.btnMute:
                if (switchMusic.isChecked()) {
                    musicService.pause();
                    item.setTitle("Unmute Sound");
                    switchMusic.setChecked(false);
                } else {
                    musicService.resume();
                    item.setTitle("Mute Sound");
                    switchMusic.setChecked(true);
                }
                break;
            case R.id.btnCallAFriend:
                dialogCall();
                break;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        //if the user presses back, do not go back.
        //super.onBackPressed();
    }

    public void onClick(View view) {

        if (view == logOutBtn) {
            if (loggedIn) {
                mAuth.signOut();
                logOutBtn.setText("log in");
                loggedIn = false;
                Toast.makeText(MainActivity.this, "user not logged in", Toast.LENGTH_LONG).show();
                userText.setText("No user connected");
                ratingText.setText("");
            } else {
                Intent intent = new Intent(this, LoginSignupActivity.class);
                startActivity(intent);
            }
        }
        else if (view == startBtn) {
            if (!isNetworkAvailable(this)) {
                new AlertDialog.Builder(this)
                        .setTitle("Error!")
                        .setMessage("No network available")
//                      .setIcon(R.drawable.error)
                        .show(); //error icon

            }
            else if (!loggedIn) {
                textToSpeech.speak("Sign Up or Log in",TextToSpeech.QUEUE_FLUSH,null);
                Intent intent = new Intent(this, LoginSignupActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, GamesActivity.class);
                intent.putExtra("USERNAME", currentUserData.getUsername());
                startActivity(intent);
            }
        }
        else if (view == musicBtn) {
            Intent intent = new Intent(this, SongsActivity.class);
            intent.putExtra("USERNAME", currentUserData.getUsername());
            startActivity(intent);
        }
        else if (view == closeBtn) {
            stopService(playIntent);
            musicService = null;
            finishAffinity();
//            musicService.stopPlayMusic();
            finishAndRemoveTask();
            finish();
        }
        else if (view == rulesBtn) {
            createRulesDialog();
        }
        else if (view == leaderboardBtn) {
            if (!isNetworkAvailable(this)) {
                new AlertDialog.Builder(this)
                        .setTitle("Error!")
                        .setMessage("No network available")
//                      .setIcon(R.drawable.error)
                        .show(); //error icon
            }
            else
                leaderboard();
        }
        else if (view == switchMusic) {
            if (switchMusic.isChecked()) {
                musicService.resume();
                switchMusic.setChecked(true);
            } else {
                musicService.pause();
                switchMusic.setChecked(false);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void leaderboard() {
        database = FirebaseDatabase.getInstance();
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.leaderboard_layout);
        dialog.setTitle("Leaderboard");
        dialog.setCancelable(true);

        Button closeBtn2;
        TextView leaderboardText1 = dialog.findViewById(R.id.textLeader0);
        TextView leaderboardText2 = dialog.findViewById(R.id.textLeader1);
        TextView leaderboardText3 = dialog.findViewById(R.id.textLeader2);
        TextView leaderboardText4 = dialog.findViewById(R.id.textLeader3);
        TextView leaderboardText5 = dialog.findViewById(R.id.textLeader4);

        TextView ratingText1 = dialog.findViewById(R.id.ratingText0);
        TextView ratingText2 = dialog.findViewById(R.id.ratingText1);
        TextView ratingText3 = dialog.findViewById(R.id.ratingText2);
        TextView ratingText4 = dialog.findViewById(R.id.ratingText3);
        TextView ratingText5 = dialog.findViewById(R.id.ratingText4);

        usersRef = database.getReference("Users/nu");
        addUsersEventListener();
        usersRef.setValue("a");
        usersRef.removeValue();

        leaderboardText1.setText(bestUsers[0].getUsername());
        leaderboardText2.setText(bestUsers[1].getUsername());
        leaderboardText3.setText(bestUsers[2].getUsername());
        leaderboardText4.setText(bestUsers[3].getUsername());
        leaderboardText5.setText(bestUsers[4].getUsername());

        ratingText1.setText("" + bestUsers[0].getRating());
        ratingText2.setText("" + bestUsers[1].getRating());
        ratingText3.setText("" + bestUsers[2].getRating());
        ratingText4.setText("" + bestUsers[3].getRating());
        ratingText5.setText("" + bestUsers[4].getRating());

        closeBtn2 = dialog.findViewById(R.id.close2Btn);
        closeBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    public void addUsersEventListener() {
        databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get the childern of "Users" in the realtime database
                Iterable<DataSnapshot> users = snapshot.getChildren();
                // create an empty list that will hold the users in type User object
                ArrayList<User> leaderboardList2 = new ArrayList<User>();
                // iterate through the users in the db
                for (DataSnapshot snop : users) {
                    if (snop.hasChild("rating")) {
                        // add the user in a type of User object to the users list
                        leaderboardList2.add(new User(snop.getKey(), snop.child("rating").getValue(long.class)));
                    }
                }
                // for 5 times, find the best player in terms of rating and remove it from the list
                for (int k = 0; k < 5; k++) {
                    long highestRating = -999;
                    User temp = null;
                    for (int i = 0; i < leaderboardList2.size(); i++) {
                        if (leaderboardList2.get(i).getRating() > highestRating) {
                            temp = leaderboardList2.get(i);
                            // update the array in place k (1-5) to be the current leader user
                            bestUsers[k] = leaderboardList2.get(i);
                            // update the current highest rating
                            highestRating = leaderboardList2.get(i).getRating();
                        }
                    }
                    // after this loop, temp is holding the k best user in the database
                    // remove the current best user and find the next best user
                    leaderboardList2.remove(temp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setRatingTextListener() {
        databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> users = dataSnapshot.getChildren();

                for (DataSnapshot snapshot : users) {
                    //if (snapshot.child("email") != null && snapshot.child("email").getValue(String.class).equals(mAuth.getCurrentUser().getEmail()))
                    //if (snapshot.child("email").getValue(String.class).equals(mAuth.getCurrentUser().getEmail()))

                    if (mAuth.getCurrentUser() != null && Objects.equals(snapshot.child("email").getValue(String.class), mAuth.getCurrentUser().getEmail()))
                        currentUserData.setRating(snapshot.child("rating").getValue(long.class));
                }
                ratingText.setText("Rating: " + currentUserData.getRating());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setUserNameText() {
        databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                if (mAuth.getCurrentUser() != null) {
                    for (DataSnapshot snapshot : users) {
                        if (Objects.equals(snapshot.child("email").getValue(String.class), mAuth.getCurrentUser().getEmail()))
                            currentUserData.setUsername(snapshot.getKey());
                    }
                    userText.setText("User: " + currentUserData.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    // הפעלת הפופ אפ
    public void createRulesDialog()
    {
        rulesDialog = new Dialog(this);
        rulesDialog.setContentView(R.layout.rules_layout);
        rulesDialog.setCancelable(true);

        Button btnBack = rulesDialog.findViewById(R.id.btnBackRules);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                rulesDialog.cancel();
            }
        });

        rulesDialog.show();
    }

    public void dialogCall (){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("    Invite a Friend!    ");
        alertDialog.setMessage("Enter a phone number ");

        //setting a cancle button
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        //edit text to the phone number
        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        alertDialog.setView(input);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"Call",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String number = input.getText().toString();
                //check if the number contains only numbers
                if (!number.isEmpty()&&number.matches("[0-9]+")) {
                    dialContactPhone(input.getText().toString());
                    dialog.dismiss();
                }
                else {
                    Toast t = Toast.makeText(getApplicationContext(),"There is a mistake in the number you entered, please check again",Toast.LENGTH_LONG);
                    t.show();
                }
            }});

        alertDialog.show();
    }

    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }


    public static boolean isNetworkAvailable(Context con) {
        try {
            ConnectivityManager cm = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}