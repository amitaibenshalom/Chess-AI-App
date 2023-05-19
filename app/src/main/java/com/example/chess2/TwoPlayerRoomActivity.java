package com.example.chess2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TwoPlayerRoomActivity extends AppCompatActivity implements View.OnClickListener{

    private String playerName = "", enemyName = "";
    private String roomName = "";
    private int color = 0;
    private FirebaseDatabase database;
    private DatabaseReference moveRef;
    private DatabaseReference player2, player1;
    private Button back;
    private ImageButton[][] boardImg;
    private final int SIZE = 8;
    private GameManager board;
    private boolean hasPlayed, isPromoted;
    private Dialog dialog, dialogPromote;
    public int promotedIndex = -1;
    private Button backBtn2;
    private TextView pointsText,endgameText, turnText;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player_room);

        database = FirebaseDatabase.getInstance();
//        SharedPreferences preferences = getSharedPreferences("PREFS",0);

        isPromoted = false;
        this.turnText = (TextView) findViewById(R.id.turnText);
        this.turnText.setText("Waiting for another player...");
        this.back = (Button)findViewById(R.id.btnBack);
        this.back.setOnClickListener(this);
        this.boardImg = new ImageButton[SIZE][SIZE];

        String str = "";
        int resId;

        for (int i = 0; i < SIZE; i++)
        {
            for (int j = 0; j < SIZE; j++)
            {
                str = "b" + i + j;
                resId = getResources().getIdentifier(str,"id",getPackageName());
                this.boardImg[i][j] = (ImageButton)findViewById(resId);
                this.boardImg[i][j].setOnClickListener(this);
            }
        }

        if (getIntent().getExtras() != null) {
            playerName = getIntent().getExtras().getString("USERNAME");
            roomName = getIntent().getExtras().getString("ROOMNAME");

            if (roomName.equals(playerName)) {
                color = 0;
            }
            else {
                color = 1;
            }
            this.board = new GameManager(color);
//
//            this.board.getPlayers()[0].setPlayed(true);
//            this.board.getPlayers()[1].setPlayed(true);

            this.hasPlayed = true;
            resetImgBoard();
        }
        moveRef = database.getReference("Rooms/" + roomName + "/move");
        player2 = database.getReference("Rooms/"+ roomName + "/player2");
        player1 = database.getReference("Rooms/"+ roomName + "/player1");
//        status = database.getReference("Rooms/" + roomName + "/status");

        addRoomEventListener();
    }

    private void addRoomEventListener() {
        moveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String changeMove = dataSnapshot.getValue(String.class);
                if (changeMove != null && changeMove.charAt(4) != (48+color)) {

                    Point from = new Point(SIZE-1-(changeMove.charAt(0)-48),SIZE-1-(changeMove.charAt(1)-48));
                    Point to = new Point(SIZE-1-(changeMove.charAt(2)-48),SIZE-1-(changeMove.charAt(3)-48));

                    if (board.getPiece(to) != null) {
                        board.getPlayers()[color].getPieces().remove(board.getPiece(to));
                    }
//                    board.getPiece(from).getPlace().setRow(to.getRow());
//                    board.getPiece(from).getPlace().setCol(to.getCol());
                    board.getPiece(from).setPlace(to);
                    board.setPiece(to,board.getPiece(from));
                    board.setPiece(from,null);

                    if (board.getPiece(to) instanceof Rook)
                        ((Rook) board.getPiece(to)).setMoved(true);
                    if (board.getPiece(to) instanceof King && !((King)board.getPiece(to)).isMoved()) {
                        ((King) board.getPiece(to)).setMoved(true);
                        if (color == 0) {
                            if (to.getCol() == 6) {
                                board.getPiece(to.getRow(),7).getPlace().setCol(5);
                                board.setPiece(to.getRow(),5,board.getPiece(to.getRow(),7));
                                board.setPiece(to.getRow(),7,null);
                            }
                            else if (to.getCol() == 2) {
                                board.getPiece(to.getRow(),0).getPlace().setCol(3);
                                board.setPiece(to.getRow(),3,board.getPiece(to.getRow(),0));
                                board.setPiece(to.getRow(),0,null);
                            }
                        }
                        else {
                            if (to.getCol() == 5) {
                                board.getPiece(to.getRow(),7).getPlace().setCol(4);
                                board.setPiece(to.getRow(),4,board.getPiece(to.getRow(),7));
                                board.setPiece(to.getRow(),7,null);
                            }
                            else if (to.getCol() == 1) {
                                board.getPiece(to.getRow(),0).getPlace().setCol(2);
                                board.setPiece(to.getRow(),2,board.getPiece(to.getRow(),0));
                                board.setPiece(to.getRow(),0,null);
                            }
                        }
                    }
                    if (board.getPiece(to) instanceof Pawn) {
                        if (changeMove.length() > 5) {
                            board.getPlayers()[1-color].getPieces().remove(board.getPiece(to));
                            switch (changeMove.charAt(5)-48) {
                                case 3:
                                    board.setPiece(to, new Queen(to,1-color));
                                    break;
                                case 1:
                                    board.setPiece(to, new Knight(to,1-color));
                                    break;
                                case 2:
                                    board.setPiece(to, new Rook(to,1-color));
                                    ((Rook)board.getPiece(to)).setMoved(true);
                                    break;
                                case 0:
                                    board.setPiece(to, new Bishop(to,1-color));
                                    break;
                            }
                            board.getPlayers()[1-color].getPieces().add(board.getPiece(to));
                        }
                    }
                    resetImgBoard();
//                    board.getPlayers()[color].setPlayed(false);
                    if (board.isCheckmated(color)) {
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run() {
                                gameEnded(1-color);
                            }
                        }, 1000);
                    }
                    turnText.setText("Turn: " + playerName);
                    hasPlayed = false;
                    board.switchTurn();
                }
                else {
                    if (changeMove != null && changeMove.charAt(4) == (48+color)) {
                        turnText.setText("Turn: " + enemyName);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        player2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String changePlayer2 = dataSnapshot.getValue(String.class);
                if (color == 0 && changePlayer2 != null) {
                    hasPlayed = false;
                    turnText.setText("Turn: " + playerName);
                    enemyName = changePlayer2;
                }
                else {
                    hasPlayed = true;
                    if (changePlayer2 != null) {
                        enemyName = roomName;
                        turnText.setText("Turn: " + enemyName);
                    }
                }
                if (changePlayer2 == null) {
                    if (!playerName.equals(roomName)) {
                        hasPlayed = true;
                        Intent intent = new Intent(getApplicationContext(), RoomsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("USERNAME",playerName);
                        startActivity(intent);
                        finish();
                    }
                    else
                        hasPlayed = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        player1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String changePlayer1 = dataSnapshot.getValue(String.class);
                if (playerName.equals(roomName) && changePlayer1 == null) {
                    hasPlayed = true;
                    Intent intent = new Intent(getApplicationContext(), RoomsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("USERNAME",playerName);
                    startActivity(intent);
                    finish();
                }
                else {
                    if ((!playerName.equals(roomName)) && changePlayer1 == null) {
                        hasPlayed = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
//        if (playerName.equals(roomName)) {
//            gameDestroyed(0);
//        }
//        else {
//            gameDestroyed(1);
//        }
//        database.getReference("Rooms/" + roomName).child((playerName.equals(roomName) ? (String)"player1" : (String)"player2")).removeValue();
        database.getReference("Rooms/" + roomName).removeValue();
//        database.getReference("Rooms/" + roomName).child("move").removeValue();
//        if (playerName != roomName)
//            status.setValue(3);

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(TwoPlayerRoomActivity.this);
        builder.setTitle("Wait!");
        builder.setMessage("Are you sure you want to exit?");
        builder.setCancelable(false);
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            database.getReference("Rooms/" + roomName).child((playerName.equals(roomName) ? (String)"player1" : (String)"player2")).removeValue();
//            Intent intent = new Intent(getApplicationContext(), RoomsActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();

            super.onBackPressed();
            dialog.cancel();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View view) {
        boolean found = false;

        if (view == back){
            found = true;

            AlertDialog.Builder builder = new AlertDialog.Builder(TwoPlayerRoomActivity.this);
            builder.setTitle("Wait!");
            builder.setMessage("Are you sure you want to exit?");
            builder.setCancelable(false);
            builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.cancel();
            });
            builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                database.getReference("Rooms/" + roomName).child((playerName.equals(roomName) ? (String)"player1" : (String)"player2")).removeValue();
                Intent intent = new Intent(this, RoomsActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                dialog.cancel();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        if (!hasPlayed) {
            isPromoted = false;
            for (int i = 0; i < SIZE && !found; i++) {
                for (int j = 0; j < SIZE && !found; j++) {
                    if (view == this.boardImg[i][j]) {
                        found = true;
                        if (this.board.getClickedPoint() == null) {
                            if (this.board.getPiece(i,j) != null) {
                                if (this.board.getPiece(i,j).getColor() == this.color) {
                                    resetImgBoard();
                                    this.board.setClickedPoint(new Point(i,j));
                                    ArrayList<Point> avlMoves = board.getPiece(i,j).availableMoves2(board);
                                    if (avlMoves.size() > 0) {
                                        Resources r0 = getResources();
                                        Drawable[] layers0 = new Drawable[2];
                                        layers0[0] = r0.getDrawable(R.drawable.red_bg);
                                        if (this.board.getPiece(this.board.getClickedPoint()) instanceof Pawn)
                                            if (this.board.getPiece(this.board.getClickedPoint()).getColor() == 0)
                                                layers0[1] = r0.getDrawable(R.drawable.chess_wp);
                                            else
                                                layers0[1] = r0.getDrawable(R.drawable.chess_bp);

                                        if (this.board.getPiece(this.board.getClickedPoint()) instanceof Bishop)
                                            if (this.board.getPiece(this.board.getClickedPoint()).getColor() == 0)
                                                layers0[1] = r0.getDrawable(R.drawable.chess_wb);
                                            else
                                                layers0[1] = r0.getDrawable(R.drawable.chess_bb);

                                        if (this.board.getPiece(this.board.getClickedPoint()) instanceof Knight)
                                            if (this.board.getPiece(this.board.getClickedPoint()).getColor() == 0)
                                                layers0[1] = r0.getDrawable(R.drawable.chess_wn);
                                            else
                                                layers0[1] = r0.getDrawable(R.drawable.chess_bn);

                                        if (this.board.getPiece(this.board.getClickedPoint()) instanceof Queen)
                                            if (this.board.getPiece(this.board.getClickedPoint()).getColor() == 0)
                                                layers0[1] = r0.getDrawable(R.drawable.chess_wq);
                                            else
                                                layers0[1] = r0.getDrawable(R.drawable.chess_bq);

                                        if (this.board.getPiece(this.board.getClickedPoint()) instanceof Rook)
                                            if (this.board.getPiece(this.board.getClickedPoint()).getColor() == 0)
                                                layers0[1] = r0.getDrawable(R.drawable.chess_wr);
                                            else
                                                layers0[1] = r0.getDrawable(R.drawable.chess_br);

                                        if (this.board.getPiece(this.board.getClickedPoint()) instanceof King)
                                            if (this.board.getPiece(this.board.getClickedPoint()).getColor() == 0)
                                                layers0[1] = r0.getDrawable(R.drawable.chess_wk);
                                            else
                                                layers0[1] = r0.getDrawable(R.drawable.chess_bk);
                                        LayerDrawable layerDrawable0 = new LayerDrawable(layers0);
                                        boardImg[this.board.getClickedPoint().getRow()][this.board.getClickedPoint().getCol()]
                                                .setImageDrawable(layerDrawable0);
                                    }
                                    for (int k = 0; k < avlMoves.size(); k++) {
                                        int x = avlMoves.get(k).getRow();
                                        int y = avlMoves.get(k).getCol();
                                        Resources r = getResources();
                                        Drawable[] layers = new Drawable[2];
                                        if (this.board.getPiece(x,y) == null)
                                            boardImg[x][y].setImageResource(R.drawable.red_dot4);
                                        else {
                                            if (this.board.getPiece(x,y) instanceof Pawn)
                                                if (this.board.getPiece(x,y).getColor() == 0)
                                                    layers[0] = r.getDrawable(R.drawable.chess_wp);
                                                else
                                                    layers[0] = r.getDrawable(R.drawable.chess_bp);

                                            if (this.board.getPiece(x,y) instanceof Bishop)
                                                if (this.board.getPiece(x,y).getColor() == 0)
                                                    layers[0] = r.getDrawable(R.drawable.chess_wb);
                                                else
                                                    layers[0] = r.getDrawable(R.drawable.chess_bb);
                                            if (this.board.getPiece(x,y) instanceof Knight)
                                                if (this.board.getPiece(x,y).getColor() == 0)
                                                    layers[0] = r.getDrawable(R.drawable.chess_wn);
                                                else
                                                    layers[0] = r.getDrawable(R.drawable.chess_bn);

                                            if (this.board.getPiece(x,y) instanceof Queen)
                                                if (this.board.getPiece(x,y).getColor() == 0)
                                                    layers[0] = r.getDrawable(R.drawable.chess_wq);
                                                else
                                                    layers[0] = r.getDrawable(R.drawable.chess_bq);

                                            if (this.board.getPiece(x,y) instanceof Rook)
                                                if (this.board.getPiece(x,y).getColor() == 0)
                                                    layers[0] = r.getDrawable(R.drawable.chess_wr);
                                                else
                                                    layers[0] = r.getDrawable(R.drawable.chess_br);

                                            if (this.board.getPiece(x,y) instanceof King)
                                                if (this.board.getPiece(x,y).getColor() == 0)
                                                    layers[0] = r.getDrawable(R.drawable.chess_wk);
                                                else
                                                    layers[0] = r.getDrawable(R.drawable.chess_bk);

                                            layers[1] = r.getDrawable(R.drawable.red_dot4);
                                            LayerDrawable layerDrawable = new LayerDrawable(layers);
                                            boardImg[x][y].setImageDrawable(layerDrawable);
                                        }
                                        boardImg[x][y].setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                        else {
                            if (this.board.getPiece(i,j) != null && this.board.getPiece(i,j).getColor() == color) {
                                if (this.board.getClickedPoint().getRow() == i && this.board.getClickedPoint().getCol() == j) {
                                    resetImgBoard();
                                    this.board.setClickedPoint(null);
                                }
                                else {
                                    resetImgBoard();
                                    this.board.setClickedPoint(new Point(i,j));
                                    ArrayList<Point> avlMoves = board.getPiece(i,j).availableMoves2(board);
                                    if (avlMoves.size() > 0) {
                                        Resources r0 = getResources();
                                        Drawable[] layers0 = new Drawable[2];
                                        layers0[0] = r0.getDrawable(R.drawable.red_bg);
                                        if (this.board.getPiece(this.board.getClickedPoint()) instanceof Pawn)
                                            if (this.board.getPiece(this.board.getClickedPoint()).getColor() == 0)
                                                layers0[1] = r0.getDrawable(R.drawable.chess_wp);
                                            else
                                                layers0[1] = r0.getDrawable(R.drawable.chess_bp);

                                        if (this.board.getPiece(this.board.getClickedPoint()) instanceof Bishop)
                                            if (this.board.getPiece(this.board.getClickedPoint()).getColor() == 0)
                                                layers0[1] = r0.getDrawable(R.drawable.chess_wb);
                                            else
                                                layers0[1] = r0.getDrawable(R.drawable.chess_bb);

                                        if (this.board.getPiece(this.board.getClickedPoint()) instanceof Knight)
                                            if (this.board.getPiece(this.board.getClickedPoint()).getColor() == 0)
                                                layers0[1] = r0.getDrawable(R.drawable.chess_wn);
                                            else
                                                layers0[1] = r0.getDrawable(R.drawable.chess_bn);

                                        if (this.board.getPiece(this.board.getClickedPoint()) instanceof Queen)
                                            if (this.board.getPiece(this.board.getClickedPoint()).getColor() == 0)
                                                layers0[1] = r0.getDrawable(R.drawable.chess_wq);
                                            else
                                                layers0[1] = r0.getDrawable(R.drawable.chess_bq);

                                        if (this.board.getPiece(this.board.getClickedPoint()) instanceof Rook)
                                            if (this.board.getPiece(this.board.getClickedPoint()).getColor() == 0)
                                                layers0[1] = r0.getDrawable(R.drawable.chess_wr);
                                            else
                                                layers0[1] = r0.getDrawable(R.drawable.chess_br);

                                        if (this.board.getPiece(this.board.getClickedPoint()) instanceof King)
                                            if (this.board.getPiece(this.board.getClickedPoint()).getColor() == 0)
                                                layers0[1] = r0.getDrawable(R.drawable.chess_wk);
                                            else
                                                layers0[1] = r0.getDrawable(R.drawable.chess_bk);
                                        LayerDrawable layerDrawable0 = new LayerDrawable(layers0);
                                        boardImg[this.board.getClickedPoint().getRow()][this.board.getClickedPoint().getCol()].setImageDrawable(layerDrawable0);
                                    }
                                    for (int k = 0; k < avlMoves.size(); k++) {
                                        int x = avlMoves.get(k).getRow();
                                        int y = avlMoves.get(k).getCol();
                                        Resources r = getResources();
                                        Drawable[] layers = new Drawable[2];
                                        if (this.board.getPiece(x,y) == null)
                                            boardImg[x][y].setImageResource(R.drawable.red_dot4);
                                        else {
                                            if (this.board.getPiece(x,y) instanceof Pawn)
                                                if (this.board.getPiece(x,y).getColor() == 0)
                                                    layers[0] = r.getDrawable(R.drawable.chess_wp);
                                                else
                                                    layers[0] = r.getDrawable(R.drawable.chess_bp);

                                            if (this.board.getPiece(x,y) instanceof Bishop)
                                                if (this.board.getPiece(x,y).getColor() == 0)
                                                    layers[0] = r.getDrawable(R.drawable.chess_wb);
                                                else
                                                    layers[0] = r.getDrawable(R.drawable.chess_bb);

                                            if (this.board.getPiece(x,y) instanceof Knight)
                                                if (this.board.getPiece(x,y).getColor() == 0)
                                                    layers[0] = r.getDrawable(R.drawable.chess_wn);
                                                else
                                                    layers[0] = r.getDrawable(R.drawable.chess_bn);

                                            if (this.board.getPiece(x,y) instanceof Queen)
                                                if (this.board.getPiece(x,y).getColor() == 0)
                                                    layers[0] = r.getDrawable(R.drawable.chess_wq);
                                                else
                                                    layers[0] = r.getDrawable(R.drawable.chess_bq);

                                            if (this.board.getPiece(x,y) instanceof Rook)
                                                if (this.board.getPiece(x,y).getColor() == 0)
                                                    layers[0] = r.getDrawable(R.drawable.chess_wr);
                                                else
                                                    layers[0] = r.getDrawable(R.drawable.chess_br);

                                            if (this.board.getPiece(x,y) instanceof King)
                                                if (this.board.getPiece(x,y).getColor() == 0)
                                                    layers[0] = r.getDrawable(R.drawable.chess_wk);
                                                else
                                                    layers[0] = r.getDrawable(R.drawable.chess_bk);

                                            layers[1] = r.getDrawable(R.drawable.red_dot4);
                                            LayerDrawable layerDrawable = new LayerDrawable(layers);
                                            boardImg[x][y].setImageDrawable(layerDrawable);
                                        }
                                        boardImg[x][y].setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                            else {
                                boolean found2 = false;
                                ArrayList<Point> avlMoves = board.getPiece(board.getClickedPoint()).availableMoves2(board);
                                for (int k = 0; k < avlMoves.size() && !found2; k++) {
                                    if (avlMoves.get(k).getRow() == i && avlMoves.get(k).getCol() == j) {
                                        found2 = true;
                                        if (this.board.getPiece(i,j) != null) {
                                            this.board.getPlayers()[this.board.getSwitchedTurn(color)].getPieces().remove(this.board.getPiece(i,j));
                                        }
                                        this.board.getPiece(this.board.getClickedPoint()).getPlace().setRow(i);
                                        this.board.getPiece(this.board.getClickedPoint()).getPlace().setCol(j);
                                        this.board.setPiece(i,j,this.board.getPiece(this.board.getClickedPoint()));
                                        this.board.setPiece(this.board.getClickedPoint(),null);
                                        if (this.board.getPiece(i,j) instanceof Rook)
                                            ((Rook) this.board.getPiece(i,j)).setMoved(true);
                                        if (this.board.getPiece(i,j) instanceof King && !((King)this.board.getPiece(i,j)).isMoved()) {
                                            ((King) this.board.getPiece(i,j)).setMoved(true);
                                            if (color == 0) {
                                                if (j == 6) {
                                                    this.board.getPiece(i,7).getPlace().setCol(5);
                                                    this.board.setPiece(i,5,this.board.getPiece(i,7));
                                                    this.board.setPiece(i,7,null);
                                                }
                                                else if (j == 2) {
                                                    this.board.getPiece(i,0).getPlace().setCol(3);
                                                    this.board.setPiece(i,3,this.board.getPiece(i,0));
                                                    this.board.setPiece(i,0,null);
                                                }
                                            }
                                            else {
                                                if (j == 5) {
                                                    this.board.getPiece(i,7).getPlace().setCol(4);
                                                    this.board.setPiece(i,4,this.board.getPiece(i,7));
                                                    this.board.setPiece(i,7,null);
                                                }
                                                else if (j == 1) {
                                                    this.board.getPiece(i,0).getPlace().setCol(2);
                                                    this.board.setPiece(i,2,this.board.getPiece(i,0));
                                                    this.board.setPiece(i,0,null);
                                                }
                                            }

                                        }
                                        if (this.board.getPiece(i,j) instanceof Pawn) {
                                            if (this.board.getPiece(i,j).getColor() == 0 && i == 0) {
                                                promotePawn((Pawn)this.board.getPiece(i,j));
                                                isPromoted = true;
                                            }
                                            else {
                                                if (this.board.getPiece(i,j).getColor() == 1 && i == 7) {
                                                    promotePawn((Pawn)this.board.getPiece(i,j));
                                                    isPromoted = true;
                                                }
                                            }
                                        }
    //                                    if (this.board.getPiece(i,j) instanceof Pawn) {
    //                                        if (this.board.getPiece(i,j).getColor() == 0) {
    //                                            if (this.board.getClickedPoint().getRow() == 6 && i == 4) {
    //                                                ((Pawn) this.board.getPiece(i,j)).setEnPassant(true);
    //                                                this.board.
    //                                            }
    //                                            else
    //                                        }
    //                                    }

//                                        List<Move> moveList = new ArrayList<Move>();
//                                        moveList.add(new Move(this.board.getClickedPoint(),new Point(i,j), color, board));
                                        if (!isPromoted) {
                                            String move = "";
                                            move += this.board.getClickedPoint().getRow();
                                            move += this.board.getClickedPoint().getCol();
                                            move += i;
                                            move += j;
                                            move += this.color;

                                            moveRef.setValue(move);
                                            if (this.board.isCheckmated(this.board.getSwitchedTurn(color))) {
                                                handler.postDelayed(new Runnable()
                                                {
                                                    @Override
                                                    public void run() {
                                                        gameEnded(color);
                                                    }
                                                }, 1000);
                                            }
//                                        this.board.getPlayers()[color].setPlayed(true);
                                            hasPlayed = true;
                                            this.board.switchTurn();
                                            this.board.setClickedPoint(null);
                                        }
                                        resetImgBoard();
                                    }
                                }
                                if (!found2) {
                                    resetImgBoard();
                                    this.board.setClickedPoint(null);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP)
            resetImgBoard();
        return true;
    }


    public void resetImgBoard()
    {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
//                boardImg[i][j].setAlpha(255);
                if (this.board.getPiece(i,j) != null) {
                    boardImg[i][j].setVisibility(View.VISIBLE);
                    if (this.board.getPiece(i,j) instanceof Pawn)
                        if (this.board.getPiece(i,j).getColor() == 0)
                            boardImg[i][j].setImageResource(R.drawable.chess_wp);
                        else
                            boardImg[i][j].setImageResource(R.drawable.chess_bp);

                    if (this.board.getPiece(i,j) instanceof Bishop)
                        if (this.board.getPiece(i,j).getColor() == 0)
                            boardImg[i][j].setImageResource(R.drawable.chess_wb);
                        else
                            boardImg[i][j].setImageResource(R.drawable.chess_bb);

                    if (this.board.getPiece(i,j) instanceof Knight)
                        if (this.board.getPiece(i,j).getColor() == 0)
                            boardImg[i][j].setImageResource(R.drawable.chess_wn);
                        else
                            boardImg[i][j].setImageResource(R.drawable.chess_bn);

                    if (this.board.getPiece(i,j) instanceof Queen)
                        if (this.board.getPiece(i,j).getColor() == 0)
                            boardImg[i][j].setImageResource(R.drawable.chess_wq);
                        else
                            boardImg[i][j].setImageResource(R.drawable.chess_bq);

                    if (this.board.getPiece(i,j) instanceof Rook)
                        if (this.board.getPiece(i,j).getColor() == 0)
                            boardImg[i][j].setImageResource(R.drawable.chess_wr);
                        else
                            boardImg[i][j].setImageResource(R.drawable.chess_br);

                    if (this.board.getPiece(i,j) instanceof King)
                        if (this.board.getPiece(i,j).getColor() == 0)
                            boardImg[i][j].setImageResource(R.drawable.chess_wk);
                        else
                            boardImg[i][j].setImageResource(R.drawable.chess_bk);
                }
                else {
                    boardImg[i][j].setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public void gameDestroyed(int color) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.endgame_layout);
        dialog.setTitle("Game ended");
        dialog.setCancelable(true);

        pointsText = dialog.findViewById(R.id.pointsText);
        endgameText = dialog.findViewById(R.id.endText);
        backBtn2 = dialog.findViewById(R.id.backBtn2);

        if (this.color != color) {
            pointsText.setText("+ 10 נקודות");
            database.getReference("Users").child(playerName).child("rating").setValue(ServerValue.increment(10));
            endgameText.setText("השחקן השני יצא מהמשחק");
        }
        else {
            pointsText.setText("+ 0 נקודות");
//            database.getReference("Users").child(playerName).child("rating").setValue(ServerValue.increment());
            endgameText.setText("חבל למה יצאת...");
        }
        backBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                database.getReference("Rooms/" + roomName).child((playerName.equals(roomName) ? (String)"player1" : (String)"player2")).removeValue();
                Intent intent = new Intent(TwoPlayerRoomActivity.this, RoomsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }

    public void gameEnded(int color) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.endgame_layout);
        dialog.setTitle("Game ended");
        dialog.setCancelable(true);

        pointsText = dialog.findViewById(R.id.pointsText);
        endgameText = dialog.findViewById(R.id.endText);
        backBtn2 = dialog.findViewById(R.id.backBtn2);

        if (this.color == color) {
            pointsText.setText("+ 10 נקודות");
            database.getReference("Users").child(playerName).child("rating").setValue(ServerValue.increment(10));
            endgameText.setText(playerName + "ניצח!");
        }
        else {
            pointsText.setText("+ 0 נקודות");
//            database.getReference("Users").child(playerName).child("rating").setValue(ServerValue.increment());
            endgameText.setText("הפסדת יא גרוע...");
        }
        backBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                database.getReference("Rooms/" + roomName).child((playerName.equals(roomName) ? (String)"player1" : (String)"player2")).removeValue();
                Intent intent = new Intent(TwoPlayerRoomActivity.this, RoomsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        dialog.show();
    }

    public void promotePawn(Pawn pawn) {
        ImageButton b, k, r, q;
        dialogPromote = new Dialog(this);
        dialogPromote.setContentView(R.layout.promote);
        dialogPromote.setTitle("promote");
        dialogPromote.setCancelable(false);

        b = dialogPromote.findViewById(R.id.bishopBtn);
        k = dialogPromote.findViewById(R.id.knightBtn);
        r = dialogPromote.findViewById(R.id.rookBtn);
        q = dialogPromote.findViewById(R.id.queenBtn);

        if (pawn.getColor() == 1) {
            b.setImageResource(R.drawable.chess_bb);
            k.setImageResource(R.drawable.chess_bn);
            r.setImageResource(R.drawable.chess_br);
            q.setImageResource(R.drawable.chess_bq);
        }

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promotedIndex = 0;
                promotePawnInBoard(pawn);
                dialogPromote.cancel();
            }
        });
        k.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promotedIndex = 1;
                promotePawnInBoard(pawn);
                dialogPromote.cancel();
            }
        });
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promotedIndex = 2;
                promotePawnInBoard(pawn);
                dialogPromote.cancel();
            }
        });
        q.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promotedIndex = 3;
                promotePawnInBoard(pawn);
                dialogPromote.cancel();
            }
        });
        dialogPromote.show();
    }

    private void promotePawnInBoard(Pawn pawn) {
        String move = "";
        move += this.board.getClickedPoint().getRow();
        move += this.board.getClickedPoint().getCol();
        move += pawn.getPlace().getRow();
        move += pawn.getPlace().getCol();
        move += this.color;

        this.board.getPlayers()[pawn.getColor()].getPieces().remove(pawn);
        switch (promotedIndex) {
            case 3:
                this.board.setPiece(pawn.getPlace(), new Queen(pawn.getPlace(),pawn.getColor()));
                move += 3;
                break;
            case 1:
                this.board.setPiece(pawn.getPlace(), new Knight(pawn.getPlace(),pawn.getColor()));
                move += 1;
                break;
            case 2:
                this.board.setPiece(pawn.getPlace(), new Rook(pawn.getPlace(),pawn.getColor()));
                ((Rook)this.board.getPiece(pawn.getPlace())).setMoved(true);
                move += 2;
                break;
            case 0:
                this.board.setPiece(pawn.getPlace(), new Bishop(pawn.getPlace(),pawn.getColor()));
                move += 0;
                break;
        }
        this.board.getPlayers()[pawn.getColor()].getPieces().add(this.board.getPiece(pawn.getPlace()));
        promotedIndex = -1;
        resetImgBoard();

        moveRef.setValue(move);
        if (this.board.isCheckmated(this.board.getSwitchedTurn(color))) {
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run() {
                    gameEnded(color);
                }
            }, 1000);
        }
        hasPlayed = true;
        this.board.switchTurn();
        this.board.setClickedPoint(null);
        resetImgBoard();
    }
}