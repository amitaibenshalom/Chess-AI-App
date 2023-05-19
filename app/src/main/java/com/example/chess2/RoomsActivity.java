package com.example.chess2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class RoomsActivity extends AppCompatActivity{

    private ListView listView;
    private Button createRoomBtn;
    private List<String> roomsList;
    private String pUserName = "";
    private String roomName = "";
    private ImageButton btnBack;
    FirebaseDatabase database;
    DatabaseReference roomRef;
    DatabaseReference roomsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        database = FirebaseDatabase.getInstance();

//        SharedPreferences preferences = getSharedPreferences("PREFS",0);
//        pUserName = preferences.getString("Users","");

        if (getIntent().getExtras() != null) {
            pUserName = getIntent().getExtras().getString("USERNAME").toString();
            roomName = pUserName;
        }

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoomsActivity.this, GamesActivity.class);
                startActivity(intent);
                finish();
            }
        });

        listView = findViewById(R.id.rooms);

        createRoomBtn = findViewById(R.id.createRoomBtn);

        roomsList = new ArrayList<>();

        roomsRef = database.getReference("Rooms/nr");
        addRoomsEventListener();
        roomsRef.setValue("a");
        roomsRef.removeValue();

        createRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRoomBtn.setText("Creating Room");
                createRoomBtn.setEnabled(false);
                // set the room's name to be the user who clicked on the button
                roomName = pUserName;
                // add the room to the database
                roomRef = FirebaseDatabase.getInstance().getReference("Rooms/" + roomName + "/player1");
                // move the user to the game screen (TwoPlayerRoomActivity)
                addRoomEventListener();
                // set the child "player1" (which is the child of the room in the DB) to be the user's name
                roomRef.setValue(pUserName);
            }
        });

        // if a user clicks on an open room in the listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get the name of the clicked room
                roomName = roomsList.get(position);
                // add the second user (the one who is joining the open room) to the room in the DB
                roomRef = database.getReference("Rooms/" + roomName + "/player2");
                // move the user to the game screen (TwoPlayerRoomActivity)
                addRoomEventListener();
                // set the child "player2" (which is the child of the room in the DB) to be the user's name
                roomRef.setValue(pUserName);
            }
        });
        // get the reference of Rooms in the database
        roomsRef = database.getReference("Rooms");
        // remove the room from the listView (the room has two players so it is closed)
        addRoomsEventListener();
    }

    private void addRoomEventListener() {
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                createRoomBtn.setText("create room");
                createRoomBtn.setEnabled(true);
                Intent intent = new Intent(getApplicationContext(), TwoPlayerRoomActivity.class);
                intent.putExtra("ROOMNAME", roomName);
                intent.putExtra("USERNAME", pUserName);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                createRoomBtn.setText("create room");
                createRoomBtn.setEnabled(true);
                Toast.makeText(RoomsActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addRoomsEventListener() {
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                roomsList.clear();
                Iterable<DataSnapshot> rooms = dataSnapshot.getChildren();

                for (DataSnapshot snapshot : rooms) {
                    if (!snapshot.hasChild("player2")) {
                        roomsList.add(snapshot.getKey());
//                        System.out.println(snapshot.getKey());
                    }
                }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(RoomsActivity.this,
                            R.layout.room_list_view_layout, roomsList);
                    listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}