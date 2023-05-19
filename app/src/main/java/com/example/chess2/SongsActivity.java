package com.example.chess2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class SongsActivity extends AppCompatActivity {

    private ListView lvSongs;
    private ArrayList<Song> songList;
    private ArrayList<String> songsNames;
    private ArrayAdapter adapter;
    public static final int mPrem =1; //for premition request

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        songsNames = new ArrayList<String>();
        lvSongs = (ListView) findViewById(R.id.lvSongs);
        songList = new ArrayList<Song>();

        if (ContextCompat.checkSelfPermission(SongsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(SongsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(SongsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, mPrem);
            } else {
                ActivityCompat.requestPermissions(SongsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, mPrem);
            }

        } else {
            //todo enter to the list
        }


        getSongs();


        adapter = new ArrayAdapter(this, R.layout.room_list_view_layout, songsNames);
        lvSongs.setAdapter(adapter);
        lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // change playing song to the chosen song
                MainActivity.musicService.setSong(i);
                MainActivity.musicService.playSong();
            }
        });
    }

    public void getSongs(){

        ContentResolver cr= getContentResolver();       //--allows access to the the phone
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;      //--songUri is the address to the music files in the phone
        Cursor songs = cr.query(songUri, null, null, null, null);
        if(songs != null && songs.moveToFirst()) {
            int songTitle = songs.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songID = songs.getColumnIndex(MediaStore.Audio.Media._ID);

            Song song;

            while(songs.moveToNext())
            {
                //long longSongID = songs.getLong(songID);
                String currentTitle = songs.getString(songTitle);
                songsNames.add(currentTitle);
                song = new Song(songID,currentTitle);
                songList.add(song);
            }

        }
    }
}