package com.example.chess2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
    

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AiActivity extends AppCompatActivity implements View.OnClickListener{

    private String playerName = "";
    private final int color = 0;
    private Button back;
    private ImageButton[][] boardImg;
    private final int SIZE = 8;
    private GameManager board;
    private boolean hasPlayed;
    private Dialog dialog, dialogPromote;
    public int promotedIndex = -1;
    private boolean markAiMoves;
    private Switch markAiMovesSwitch;
    private TextView turnText;
    private Handler handler = new Handler();
    private int depth = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);
        this.turnText = (TextView) findViewById(R.id.turnText);
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
            int level = getIntent().getExtras().getInt("LEVEL");
            switch (level) {
                case 1:
                    depth = 1;
                    break;
                case 2:
                    depth = 2;
                    break;
                case 3:
                    depth = 3;
                    break;
            }
            this.turnText.setText("Turn: " + playerName);
            this.board = new GameManager(0);
            this.hasPlayed = false;
            resetImgBoard();
        }
        this.markAiMovesSwitch = findViewById(R.id.markAiMoves);
        this.markAiMovesSwitch.setChecked(false);
        this.markAiMovesSwitch.setOnClickListener(this);
        this.markAiMoves = false;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AiActivity.this);
        builder.setTitle("Wait!");
        builder.setMessage("Are you sure you want to exit?");
        builder.setCancelable(false);
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
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

            AlertDialog.Builder builder = new AlertDialog.Builder(AiActivity.this);
            builder.setTitle("Wait!");
            builder.setMessage("Are you sure you want to exit?");
            builder.setCancelable(false);
            builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.cancel();
            });
            builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                Intent intent = new Intent(this, GamesActivity.class);
                intent.putExtra("USERNAME", playerName);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
//                super.onBackPressed();
                dialog.cancel();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else if (view == markAiMovesSwitch) {
            this.markAiMoves = !this.markAiMoves;
        }
        else if (!hasPlayed) {
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
                                        if (this.board.getPiece(i,j) instanceof Pawn && i == 0) {
                                            promotePawn((Pawn)this.board.getPiece(0,j));
                                        }
                                        else {
                                            resetImgBoard();
//                                        turnText.setText("Turn: Computer");

                                            if (this.board.isCheckmated(1)) {
                                                handler.postDelayed(new Runnable()
                                                {
                                                    @Override
                                                    public void run() {
                                                        gameEnded(0);
                                                    }
                                                }, 1000);
                                            }
                                            else {
                                                turnText.setText("Turn: Computer");
                                                hasPlayed = true;
                                                this.board.switchTurn();
                                                this.board.setClickedPoint(null);
                                                handler.postDelayed(new Runnable()
                                                {
                                                    @Override
                                                    public void run() {
                                                        turnText.setText("Turn: " + playerName);
                                                        makeAiMove();
                                                    }
                                                }, 100);

                                                hasPlayed = false;
                                                turnText.setText("Turn: Computer");

                                                this.board.switchTurn();
//                                                resetImgBoard();
                                            }
//                                            resetImgBoard();
                                        }
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

                    else if (this.board.getPiece(i,j) instanceof Bishop)
                        if (this.board.getPiece(i,j).getColor() == 0)
                            boardImg[i][j].setImageResource(R.drawable.chess_wb);
                        else
                            boardImg[i][j].setImageResource(R.drawable.chess_bb);

                    else if (this.board.getPiece(i,j) instanceof Knight)
                        if (this.board.getPiece(i,j).getColor() == 0)
                            boardImg[i][j].setImageResource(R.drawable.chess_wn);
                        else
                            boardImg[i][j].setImageResource(R.drawable.chess_bn);

                    else if (this.board.getPiece(i,j) instanceof Queen)
                        if (this.board.getPiece(i,j).getColor() == 0)
                            boardImg[i][j].setImageResource(R.drawable.chess_wq);
                        else
                            boardImg[i][j].setImageResource(R.drawable.chess_bq);

                    else if (this.board.getPiece(i,j) instanceof Rook)
                        if (this.board.getPiece(i,j).getColor() == 0)
                            boardImg[i][j].setImageResource(R.drawable.chess_wr);
                        else
                            boardImg[i][j].setImageResource(R.drawable.chess_br);

                    else if (this.board.getPiece(i,j) instanceof King)
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


    public void gameEnded(int color) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.endgame_layout);
        dialog.setTitle("Game ended");
        dialog.setCancelable(true);

        TextView pointsText = dialog.findViewById(R.id.pointsText);
        TextView endgameText = dialog.findViewById(R.id.endText);
        Button backBtn2 = dialog.findViewById(R.id.backBtn2);

        if (this.color == color) {
            pointsText.setText("+ 0 נקודות");
            endgameText.setText(playerName + " ניצח!");
        }
        else {
            pointsText.setText("+ 0 נקודות");
            endgameText.setText("המחשב ניצח!");
        }
        backBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Intent intent = new Intent(AiActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        dialog.show();

    }

    public void makeAiMove() {
        Point[] move = new Point[2];
        int best = 99999;
        int promotion = -1;
        int piecesSize = board.getPlayers()[1].getPieces().size();
        for (int i = 0; i < piecesSize; i++) {
            Piece piece = board.getPlayers()[1].getPieces().get(i);
//            System.out.println(board.getPlayers()[1].getPieces().toString());
            ArrayList<Point> availableMovesPiece = piece.availableMoves2(board);
            int avlMovesSize = availableMovesPiece.size();
            Point prevPoint = new Point(piece.getPlace().getRow(), piece.getPlace().getCol());
            for (int j = 0; j < avlMovesSize; j++) {
                int value = 0;
                Piece eatedPiece = null;
                Point movePoint = availableMovesPiece.get(j);
                int removedIndex = -1;
                if (board.getPiece(movePoint) != null) {
                    eatedPiece = board.getPiece(movePoint);
                    removedIndex = board.getPlayers()[0].getPieces().indexOf(eatedPiece);
                    board.getPlayers()[0].getPieces().set(removedIndex,null);
                }
                board.setPiece(piece.getPlace(),null);
//                piece.getPlace().setRow(movePoint.getRow());
//                piece.getPlace().setCol(movePoint.getCol());
                piece.setPlace(movePoint);
                board.setPiece(movePoint,piece);

                if (piece instanceof Pawn && (movePoint.getRow() == 7)) {
                    Pawn tempPawn = (Pawn)piece;
//                    board.getPlayers()[1].getPieces().remove(piece);
                    int pawnIndex = board.getPlayers()[1].getPieces().indexOf(tempPawn);

                    piece = new Queen(piece.getPlace(),1);
                    board.setPiece(movePoint,piece);
                    board.getPlayers()[1].getPieces().set(pawnIndex,piece);
                    value = getMinimaxValue(depth, 0, board, true, -9999, 9999);

                    if (value <= best) {
                        move[0] = prevPoint;
                        move[1] = movePoint;
                        promotion = 3;
                        best = value;
                    }

//                    board.getPlayers()[1].getPieces().remove(piece);
                    piece = new Rook(piece.getPlace(),1);
                    ((Rook) piece).setMoved(true);
                    board.getPlayers()[1].getPieces().set(pawnIndex,piece);
                    board.setPiece(movePoint,piece);
                    value = getMinimaxValue(depth, 0, board, true, -9999, 9999);

                    if (value < best) {
                        move[0] = prevPoint;
                        move[1] = movePoint;
                        promotion = 2;
                        best = value;
                    }

//                    board.getPlayers()[1].getPieces().remove(piece);
                    piece = new Knight(piece.getPlace(),1);
                    board.getPlayers()[1].getPieces().set(pawnIndex,piece);
                    board.setPiece(movePoint,piece);
                    value = getMinimaxValue(depth, 0, board, true, -9999, 9999);

                    if (value < best) {
                        move[0] = prevPoint;
                        move[1] = movePoint;
                        promotion = 1;
                        best = value;
                    }

//                    board.getPlayers()[1].getPieces().remove(piece);
                    piece = new Bishop(piece.getPlace(),1);
                    board.getPlayers()[1].getPieces().set(pawnIndex,piece);
                    board.setPiece(movePoint,piece);
                    value = getMinimaxValue(depth, 0, board, true, -9999, 9999);
                    if (value < best) {
                        move[0] = prevPoint;
                        move[1] = movePoint;
                        promotion = 0;
                        best = value;
                    }

//                    board.getPlayers()[1].getPieces().remove(piece);
                    piece = tempPawn;
                    board.getPlayers()[1].getPieces().set(pawnIndex,piece);
                    board.setPiece(prevPoint,piece);
                    piece.setPlace(prevPoint);
                    board.setPiece(movePoint,eatedPiece);
                    if (eatedPiece != null) {
                        board.getPlayers()[0].getPieces().set(removedIndex,eatedPiece);
                    }
                }
                else {
                    if (piece instanceof Rook && !((Rook) piece).isMoved()) {
                        ((Rook) piece).setMoved(true);
                        value = getMinimaxValue(depth, 0, board, true, -9999, 9999);
                        ((Rook) piece).setMoved(false);
                        board.setPiece(prevPoint,piece);
                        piece.setPlace(prevPoint);
                        board.setPiece(movePoint,eatedPiece);
                        if (eatedPiece != null) {
                            board.getPlayers()[0].getPieces().set(removedIndex,eatedPiece);
                        }
                        if (value < best) {
                            move[0] = prevPoint;
                            move[1] = movePoint;
                            promotion = -1;
                            best = value;
                        }
                    }
                    else {
                        if (piece instanceof King && !((King)piece).isMoved()) {
                            ((King) piece).setMoved(true);
                            if (movePoint.getCol() == 6) {
                                board.getPiece(movePoint.getRow(),7).getPlace().setCol(5);
                                board.setPiece(movePoint.getRow(),5,board.getPiece(movePoint.getRow(),7));
                                board.setPiece(movePoint.getRow(),7,null);
                            }
                            else if (movePoint.getCol() == 2) {
                                board.getPiece(movePoint.getRow(),0).getPlace().setCol(3);
                                board.setPiece(movePoint.getRow(),3,board.getPiece(movePoint.getRow(),0));
                                board.setPiece(movePoint.getRow(),0,null);
                            }

                            value = getMinimaxValue(depth, 0, board, true, -9999, 9999);
                            if (value <= best) {
                                move[0] = prevPoint;
                                move[1] = movePoint;
                                promotion = -1;
                                best = value;
                            }
                            ((King) piece).setMoved(false);
                            board.setPiece(prevPoint,piece);
                            piece.setPlace(prevPoint);
                            board.setPiece(movePoint,eatedPiece);
                            if (eatedPiece != null) {
                                board.getPlayers()[0].getPieces().set(removedIndex,eatedPiece);
                            }
                            if (movePoint.getCol() == 6) {
                                board.setPiece(movePoint.getRow(),7,board.getPiece(movePoint.getRow(),5));
                                board.setPiece(movePoint.getRow(),5,null);
                                board.getPiece(movePoint.getRow(),7).getPlace().setCol(7);
                            }
                            else if (movePoint.getCol() == 2) {
                                board.setPiece(movePoint.getRow(),0,board.getPiece(movePoint.getRow(),3));
                                board.setPiece(movePoint.getRow(),3,null);
                                board.getPiece(movePoint.getRow(),0).getPlace().setCol(0);
                            }
                        }
                        else {
                            value = getMinimaxValue(depth, 0, board, true, -9999, 9999);

                            if (value < best) {
                                move[0] = prevPoint;
                                move[1] = movePoint;
                                promotion = -1;
                                best = value;
//                                System.out.println(best);
//                                System.out.println("from:" + move[0].getRow()+move[0].getCol());
//                                System.out.println("to:" + move[1].getRow()+move[1].getCol());
                            }
                            board.setPiece(prevPoint,piece);
                            piece.setPlace(prevPoint);
                            board.setPiece(movePoint,eatedPiece);
                            if (eatedPiece != null) {
                                board.getPlayers()[0].getPieces().set(removedIndex,eatedPiece);
                            }
                        }
                    }
                }
//                if (value < best) {
//                    move[0] = prevPoint;
//                    move[1] = movePoint;
//                    best = value;
//                }
            }
        }
        if (move[1] == null)
            return;
        if (this.board.getPiece(move[1]) != null) {
            this.board.getPlayers()[0].getPieces().remove(this.board.getPiece(move[1]));
        }

        this.board.getPiece(move[0]).setPlace(move[1]);
        this.board.setPiece(move[1],this.board.getPiece(move[0]));
        this.board.setPiece(move[0],null);
        if (this.board.getPiece(move[1]) instanceof Rook)
            ((Rook) this.board.getPiece(move[1])).setMoved(true);

        if (promotion != -1) {
            int pawnIndexPromotion = board.getPlayers()[1].getPieces().indexOf(this.board.getPiece(move[1]));
//            board.getPlayers()[1].getPieces().remove(this.board.getPiece(move[1]));
            Piece newPiece = null;
            switch (promotion) {
                case 0:
                    newPiece = new Bishop(move[1],1);
                    break;
                case 1:
                    newPiece = new Knight(move[1],1);
                    break;
                case 2:
                    newPiece = new Rook(move[1],1);
                    break;
                case 3:
                    newPiece = new Queen(move[1],1);
                    break;
            }
            this.board.setPiece(move[1],newPiece);
            board.getPlayers()[1].getPieces().set(pawnIndexPromotion,newPiece);
        }
        else {
            if (this.board.getPiece(move[1]) instanceof King && !((King)this.board.getPiece(move[1])).isMoved()) {
                ((King) this.board.getPiece(move[1])).setMoved(true);
                if (move[1].getCol() == 6) {
                    this.board.getPiece(move[1].getRow(),7).getPlace().setCol(5);
                    this.board.setPiece(move[1].getRow(),5,this.board.getPiece(move[1].getRow(),7));
                    this.board.setPiece(move[1].getRow(),7,null);
                }
                else if (move[1].getCol() == 2) {
                    this.board.getPiece(move[1].getRow(),0).getPlace().setCol(3);
                    this.board.setPiece(move[1].getRow(),3,this.board.getPiece(move[1].getRow(),0));
                    this.board.setPiece(move[1].getRow(),0,null);
                }
            }
        }
        resetImgBoard();
        if (this.markAiMoves) {
            Resources r0 = getResources();
            boardImg[move[0].getRow()][move[0].getCol()].setImageDrawable(r0.getDrawable(R.drawable.red_bg));
            Drawable[] layers0 = new Drawable[2];
            layers0[0] = r0.getDrawable(R.drawable.red_bg);

            if (this.board.getPiece(move[1]) instanceof Pawn)
                layers0[1] = r0.getDrawable(R.drawable.chess_bp);
            else if (this.board.getPiece(move[1]) instanceof Bishop)
                layers0[1] = r0.getDrawable(R.drawable.chess_bb);
            else if (this.board.getPiece(move[1]) instanceof Knight)
                layers0[1] = r0.getDrawable(R.drawable.chess_bn);
            else if (this.board.getPiece(move[1]) instanceof Queen)
                layers0[1] = r0.getDrawable(R.drawable.chess_bq);
            else if (this.board.getPiece(move[1]) instanceof Rook)
                layers0[1] = r0.getDrawable(R.drawable.chess_br);
            else if (this.board.getPiece(move[1]) instanceof King)
                layers0[1] = r0.getDrawable(R.drawable.chess_bk);
            boardImg[move[0].getRow()][move[0].getCol()].setVisibility(View.VISIBLE);

            LayerDrawable layerDrawable0 = new LayerDrawable(layers0);
            boardImg[move[1].getRow()][move[1].getCol()].setImageDrawable(layerDrawable0);

        }
//        System.out.println(getMinimaxValue(3,0,board,true,-9999,9999));
        if (this.board.isCheckmated(0)) {
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run() {
                    gameEnded(1);
                }
            }, 1000);
        }
    }

    public int getMinimaxValue(int depth, int color, GameManager gm,
                               boolean maximizingPlayer, int alpha, int beta) {
        if (gm.isCheckmated(1))
            return (9000+depth*100);
        if (gm.isCheckmated(0))
            return -(9000+depth*100);
        if (depth == 0) {
            return gm.getValueBoard();
        }
        if (maximizingPlayer) {
            boolean hasMove = false;
            int best = -9999;
            int piecesSize = gm.getPlayers()[color].getPieces().size();
            for (int i = 0; i < piecesSize; i++) {
                if (gm.getPlayers()[color].getPieces().get(i) == null)
                    continue;
                Piece piece = gm.getPlayers()[color].getPieces().get(i);
                ArrayList<Point> availableMovesPiece = piece.availableMoves2(gm);
                int avlMovesSize = availableMovesPiece.size();
                for (int j = 0; j < avlMovesSize; j++) {
                    int value = 0;
                    hasMove = true;
                    Piece eatedPiece = null;
                    Point prevPoint = new Point(piece.getPlace().getRow(), piece.getPlace().getCol());
                    Point movePoint = availableMovesPiece.get(j);
                    int removedIndex = -1;
                    if (gm.getPiece(movePoint) != null) {
                        eatedPiece = gm.getPiece(movePoint);
                        removedIndex = gm.getPlayers()[1-color].getPieces().indexOf(eatedPiece);
                        gm.getPlayers()[1-color].getPieces().set(removedIndex,null);
//                        gm.getPlayers()[1-color].getPieces().remove(eatedPiece);
                    }
                    gm.setPiece(piece.getPlace(),null);
//                    piece.getPlace().setRow(movePoint.getRow());
//                    piece.getPlace().setCol(movePoint.getCol());
                    piece.setPlace(movePoint);
                    gm.setPiece(movePoint,piece);

                    if (piece instanceof Pawn && ((color == 0) ? (movePoint.getRow() == 0) : (movePoint.getRow() == 7))) {
                        Pawn tempPawn = (Pawn)piece;
                        int pawnIndex = gm.getPlayers()[color].getPieces().indexOf(tempPawn);
//                        gm.getPlayers()[color].getPieces().set(pawnIndex,null);
//                        gm.getPlayers()[color].getPieces().remove(piece);
                        piece = new Queen(piece.getPlace(),color);
                        gm.setPiece(movePoint,piece);
                        gm.getPlayers()[color].getPieces().set(pawnIndex,piece);
                        value = getMinimaxValue(depth - 1, 1-color, gm, false, alpha, beta);

//                        gm.getPlayers()[color].getPieces().remove(piece);
                        piece = new Rook(piece.getPlace(),color);
                        ((Rook) piece).setMoved(true);
                        gm.getPlayers()[color].getPieces().set(pawnIndex,piece);
                        gm.setPiece(movePoint,piece);
                        value = Math.max(value,getMinimaxValue(depth - 1, 1-color, gm, false, alpha, beta));

//                        gm.getPlayers()[color].getPieces().remove(piece);
                        piece = new Knight(piece.getPlace(),color);
                        gm.getPlayers()[color].getPieces().set(pawnIndex,piece);
                        gm.setPiece(movePoint,piece);
                        value = Math.max(value,getMinimaxValue(depth - 1, 1-color, gm, false, alpha, beta));

//                        gm.getPlayers()[color].getPieces().remove(piece);
                        piece = new Bishop(piece.getPlace(),color);
                        gm.getPlayers()[color].getPieces().set(pawnIndex,piece);
                        gm.setPiece(movePoint,piece);
                        value = Math.max(value,getMinimaxValue(depth - 1, 1-color, gm, false, alpha, beta));

//                        gm.getPlayers()[color].getPieces().remove(piece);
                        piece = tempPawn;
                        gm.getPlayers()[color].getPieces().set(pawnIndex,piece);
                        gm.setPiece(prevPoint,piece);
                        piece.setPlace(prevPoint);
                        gm.setPiece(movePoint,eatedPiece);
                        if (eatedPiece != null) {
                            gm.getPlayers()[1-color].getPieces().set(removedIndex,eatedPiece);
                        }
                    }
                    else {
                        if (piece instanceof Rook && !((Rook) piece).isMoved()) {
                            ((Rook) piece).setMoved(true);
                            value = getMinimaxValue(depth - 1, 1-color, gm, false, alpha, beta);
                            ((Rook) piece).setMoved(false);
                            gm.setPiece(prevPoint,piece);
                            piece.setPlace(prevPoint);
                            gm.setPiece(movePoint,eatedPiece);
                            if (eatedPiece != null) {
                                gm.getPlayers()[1-color].getPieces().set(removedIndex,eatedPiece);
                            }
                        }
                        else {
                            if (piece instanceof King && !((King)piece).isMoved()) {
                                ((King) piece).setMoved(true);
                                if (movePoint.getCol() == 6) {
                                    gm.getPiece(movePoint.getRow(),7).getPlace().setCol(5);
                                    gm.setPiece(movePoint.getRow(),5,gm.getPiece(movePoint.getRow(),7));
                                    gm.setPiece(movePoint.getRow(),7,null);
                                }
                                else if (movePoint.getCol() == 2) {
                                    gm.getPiece(movePoint.getRow(),0).getPlace().setCol(3);
                                    gm.setPiece(movePoint.getRow(),3,gm.getPiece(movePoint.getRow(),0));
                                    gm.setPiece(movePoint.getRow(),0,null);
                                }

                                value = getMinimaxValue(depth - 1, 1-color, gm, false, alpha, beta);
                                ((King) piece).setMoved(false);
                                gm.setPiece(prevPoint,piece);
                                piece.setPlace(prevPoint);
                                gm.setPiece(movePoint,eatedPiece);
                                if (eatedPiece != null) {
                                    gm.getPlayers()[1-color].getPieces().set(removedIndex,eatedPiece);
                                }
                                if (movePoint.getCol() == 6) {
                                    gm.setPiece(movePoint.getRow(),7,gm.getPiece(movePoint.getRow(),5));
                                    gm.setPiece(movePoint.getRow(),5,null);
                                    gm.getPiece(movePoint.getRow(),7).getPlace().setCol(7);
                                }
                                else if (movePoint.getCol() == 2) {
                                    gm.setPiece(movePoint.getRow(),0,gm.getPiece(movePoint.getRow(),3));
                                    gm.setPiece(movePoint.getRow(),3,null);
                                    gm.getPiece(movePoint.getRow(),0).getPlace().setCol(0);
                                }
                            }
                            else {
                                value = getMinimaxValue(depth - 1, 1-color, gm, false, alpha, beta);
                                gm.setPiece(prevPoint,piece);
                                piece.setPlace(prevPoint);
                                gm.setPiece(movePoint,eatedPiece);
                                if (eatedPiece != null) {
                                    gm.getPlayers()[1-color].getPieces().set(removedIndex,eatedPiece);
                                }
                            }
                        }
                    }
                    best = Math.max(best, value);
                    alpha = Math.max(alpha, best);

                    // Alpha Beta Pruning
                    if (beta < alpha)
                        break;
                }
            }
            if (!hasMove)
                return 0;
            return best;
        }
        else {
            int best = 9999;
            boolean hasMove = false;
            int piecesSize = gm.getPlayers()[color].getPieces().size();
            for (int i = 0; i < piecesSize; i++) {
                if (gm.getPlayers()[color].getPieces().get(i) == null)
                    continue;
                Piece piece = gm.getPlayers()[color].getPieces().get(i);
                ArrayList<Point> availableMovesPiece = piece.availableMoves2(gm);
                int avlMovesSize = availableMovesPiece.size();
                for (int j = 0; j < avlMovesSize; j++) {
                    int value = 0;
                    hasMove = true;
                    Piece eatedPiece = null;
                    Point prevPoint = new Point(piece.getPlace().getRow(), piece.getPlace().getCol());
                    Point movePoint = availableMovesPiece.get(j);
                    int removedIndex = -1;
                    if (gm.getPiece(movePoint) != null) {
                        eatedPiece = gm.getPiece(movePoint);
                        removedIndex = gm.getPlayers()[1-color].getPieces().indexOf(eatedPiece);
                        gm.getPlayers()[1-color].getPieces().set(removedIndex,null);
//                        gm.getPlayers()[1-color].getPieces().remove(eatedPiece);
                    }
                    gm.setPiece(piece.getPlace(),null);
//                    piece.getPlace().setRow(movePoint.getRow());
//                    piece.getPlace().setCol(movePoint.getCol());
                    piece.setPlace(movePoint);
                    gm.setPiece(movePoint,piece);

                    if (piece instanceof Pawn && ((color == 0) ? (movePoint.getRow() == 0) : (movePoint.getRow() == 7))) {
                        Pawn tempPawn = (Pawn)piece;
//                        gm.getPlayers()[color].getPieces().remove(piece);
                        int pawnIndex = gm.getPlayers()[color].getPieces().indexOf(tempPawn);
                        piece = new Queen(piece.getPlace(),color);
                        gm.setPiece(movePoint,piece);
                        gm.getPlayers()[color].getPieces().set(pawnIndex,piece);
                        value = getMinimaxValue(depth - 1, 1-color, gm, true, alpha, beta);

//                        gm.getPlayers()[color].getPieces().remove(piece);
                        piece = new Rook(piece.getPlace(),color);
                        ((Rook) piece).setMoved(true);
                        gm.getPlayers()[color].getPieces().set(pawnIndex,piece);
                        gm.setPiece(movePoint,piece);
                        value = Math.min(value,getMinimaxValue(depth - 1, 1-color, gm, true, alpha, beta));

//                        gm.getPlayers()[color].getPieces().remove(piece);
                        piece = new Knight(piece.getPlace(),color);
                        gm.getPlayers()[color].getPieces().set(pawnIndex,piece);
                        gm.setPiece(movePoint,piece);
                        value = Math.min(value,getMinimaxValue(depth - 1, 1-color, gm, true, alpha, beta));

//                        gm.getPlayers()[color].getPieces().remove(piece);
                        piece = new Bishop(piece.getPlace(),color);
                        gm.getPlayers()[color].getPieces().set(pawnIndex,piece);
                        gm.setPiece(movePoint,piece);
                        value = Math.min(value,getMinimaxValue(depth - 1, 1-color, gm, true, alpha, beta));

//                        gm.getPlayers()[color].getPieces().remove(piece);
                        piece = tempPawn;
                        gm.getPlayers()[color].getPieces().set(pawnIndex,piece);
                        gm.setPiece(prevPoint,piece);
                        piece.setPlace(prevPoint);
                        gm.setPiece(movePoint,eatedPiece);
                        if (eatedPiece != null) {
                            gm.getPlayers()[1-color].getPieces().set(removedIndex,eatedPiece);
                        }
                    }
                    else {
                        if (piece instanceof Rook && !((Rook) piece).isMoved()) {
                            ((Rook) piece).setMoved(true);
                            value = getMinimaxValue(depth - 1, 1-color, gm, true, alpha, beta);
                            ((Rook) piece).setMoved(false);
                            gm.setPiece(prevPoint,piece);
                            piece.setPlace(prevPoint);
                            gm.setPiece(movePoint,eatedPiece);
                            if (eatedPiece != null) {
                                gm.getPlayers()[1-color].getPieces().set(removedIndex,eatedPiece);
                            }
                        }
                        else {
                            if (piece instanceof King && !((King)piece).isMoved()) {
                                ((King) piece).setMoved(true);
                                if (movePoint.getCol() == 6) {
                                    gm.getPiece(movePoint.getRow(),7).getPlace().setCol(5);
                                    gm.setPiece(movePoint.getRow(),5,gm.getPiece(movePoint.getRow(),7));
                                    gm.setPiece(movePoint.getRow(),7,null);
                                }
                                else if (movePoint.getCol() == 2) {
                                    gm.getPiece(movePoint.getRow(),0).getPlace().setCol(3);
                                    gm.setPiece(movePoint.getRow(),3,gm.getPiece(movePoint.getRow(),0));
                                    gm.setPiece(movePoint.getRow(),0,null);
                                }

                                value = getMinimaxValue(depth - 1, 1-color, gm, true, alpha, beta);
                                ((King) piece).setMoved(false);
                                gm.setPiece(prevPoint,piece);
                                piece.setPlace(prevPoint);
                                gm.setPiece(movePoint,eatedPiece);
                                if (eatedPiece != null) {
                                    gm.getPlayers()[1-color].getPieces().set(removedIndex,eatedPiece);
                                }
                                if (movePoint.getCol() == 6) {
                                    gm.setPiece(movePoint.getRow(),7,gm.getPiece(movePoint.getRow(),5));
                                    gm.setPiece(movePoint.getRow(),5,null);
                                    gm.getPiece(movePoint.getRow(),7).getPlace().setCol(7);
                                }
                                else if (movePoint.getCol() == 2) {
                                    gm.setPiece(movePoint.getRow(),0,gm.getPiece(movePoint.getRow(),3));
                                    gm.setPiece(movePoint.getRow(),3,null);
                                    gm.getPiece(movePoint.getRow(),0).getPlace().setCol(0);
                                }
                            }
                            else {
                                value = getMinimaxValue(depth - 1, 1-color, gm, true, alpha, beta);
                                gm.setPiece(prevPoint,piece);
                                piece.setPlace(prevPoint);
                                gm.setPiece(movePoint,eatedPiece);
                                if (eatedPiece != null) {
                                    gm.getPlayers()[1-color].getPieces().set(removedIndex,eatedPiece);
                                }
                            }
                        }
                    }
                    best = Math.min(best, value);
                    beta = Math.min(beta, best);

                    // Alpha Beta Pruning
                    if (beta < alpha)
                        break;
                }
            }
            if (!hasMove)
                return 0;
            return best;
        }
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
//        this.board.getPlayers()[pawn.getColor()].getPieces().remove(pawn);
        int pawnIndexPromotion = board.getPlayers()[pawn.getColor()].getPieces().indexOf(pawn);
        switch (promotedIndex) {
            case 3:
                this.board.setPiece(pawn.getPlace(), new Queen(pawn.getPlace(),pawn.getColor()));
                break;
            case 1:
                this.board.setPiece(pawn.getPlace(), new Knight(pawn.getPlace(),pawn.getColor()));
                break;
            case 2:
                this.board.setPiece(pawn.getPlace(), new Rook(pawn.getPlace(),pawn.getColor()));
                ((Rook)this.board.getPiece(pawn.getPlace())).setMoved(true);
                break;
            case 0:
                this.board.setPiece(pawn.getPlace(), new Bishop(pawn.getPlace(),pawn.getColor()));
                break;
        }
        this.board.getPlayers()[pawn.getColor()].getPieces().set(pawnIndexPromotion,this.board.getPiece(pawn.getPlace()));
        promotedIndex = -1;
        resetImgBoard();

        if (this.board.isCheckmated(1)) {
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run() {
                    gameEnded(0);
                }
            }, 1000);
        }
        else {
            turnText.setText("Turn: Computer");
            hasPlayed = true;
            this.board.switchTurn();
            this.board.setClickedPoint(null);
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run() {
                    turnText.setText("Turn: " + playerName);
                    makeAiMove();
                    resetImgBoard();
                }
            }, 100);
            if (this.board.isCheckmated(0)) {
                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run() {
                        gameEnded(1);
                    }
                }, 1000);
            }
            hasPlayed = false;
            turnText.setText("Turn: Computer");

            this.board.switchTurn();
            resetImgBoard();
        }
        resetImgBoard();
    }
}