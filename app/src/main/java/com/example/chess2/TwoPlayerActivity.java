package com.example.chess2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TwoPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private Button back;
    private Button restart;
    private ImageButton[][] boardImg;
    private final int SIZE = 8;
    private GameManager board;
    private TextView turnText;
    private Handler handler = new Handler();
    private Dialog dialog, dialogPromote;
    public int promotedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_player);

        this.turnText = findViewById(R.id.turnText);
        this.back = (Button)findViewById(R.id.btnBack);
        this.back.setOnClickListener(this);
        this.restart = (Button)findViewById(R.id.btnRestart);
        this.restart.setOnClickListener(this);
        this.boardImg = new ImageButton[SIZE][SIZE];
        this.board = new GameManager();

        turnText.setText("Turn: White");

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

                this.boardImg[i][j].setOnDragListener(new View.OnDragListener() {
                    @Override
                    public boolean onDrag(View v, DragEvent event) {
                        final int action = event.getAction();
                        switch (action) {
                            case DragEvent.ACTION_DRAG_STARTED:
                                break;
                            case DragEvent.ACTION_DRAG_EXITED:
                                break;
                            case DragEvent.ACTION_DRAG_ENTERED:
                                break;
                            case DragEvent.ACTION_DROP:
                                return true;
                            case DragEvent.ACTION_DRAG_ENDED:
                                return true;
                        }
                        return true;
                    }
                });
                final ImageButton[] temp = {this.boardImg[i][j]};
                this.boardImg[i][j].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ClipData data = ClipData.newPlainText("", "");
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(temp[0]);
                        v.startDrag(data, shadowBuilder, v, 0);
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP)
            resetImgBoard();
        return true;
    }

    public void restart() {
        this.boardImg = new ImageButton[SIZE][SIZE];
        this.board = new GameManager();
        promotedIndex = -1;
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
        turnText.setText("Turn: White");
        resetImgBoard();
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(TwoPlayerActivity.this);
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


    @SuppressLint("SetTextI18n")
    public void gameEnded(int color) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.endgame_layout);
        dialog.setTitle("Game ended");
        dialog.setCancelable(true);

        TextView endgameText = dialog.findViewById(R.id.endText);
        Button backBtn2 = dialog.findViewById(R.id.backBtn2);

        endgameText.setText((color == 0 ? "לבן" : "שחור") + " ניצח!");

        backBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Intent intent = new Intent(TwoPlayerActivity.this, GamesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    public void onClick(View view){
        boolean found = false;

        if (view == back){
            found = true;

            AlertDialog.Builder builder = new AlertDialog.Builder(TwoPlayerActivity.this);
            builder.setTitle("Wait!");
            builder.setMessage("Are you sure you want to exit?");
            builder.setCancelable(false);
            builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.cancel();
            });
            builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                Intent intent = new Intent(this, GamesActivity.class);
                finish();
                super.onBackPressed();
                dialog.cancel();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        if (view == restart) {
            found = true;
            restart();
        }
        for (int i = 0; i < SIZE && !found; i++) {
            for (int j = 0; j < SIZE && !found; j++) {
                if (view == this.boardImg[i][j]) {
                    found = true;
                    if (this.board.getClickedPoint() == null) {
                        if (this.board.getPiece(i,j) != null) {
                            if (this.board.getPiece(i,j).getColor() == this.board.getTurn()) {
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
                    }
                    else {
                        if (this.board.getPiece(i,j) != null && this.board.getPiece(i,j).getColor() == this.board.getTurn()) {
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
                                        this.board.getPlayers()[this.board.getSwitchedTurn(this.board.getTurn())].getPieces().remove(this.board.getPiece(i,j));
                                    }
                                    this.board.getPiece(this.board.getClickedPoint()).getPlace().setRow(i);
                                    this.board.getPiece(this.board.getClickedPoint()).getPlace().setCol(j);
                                    this.board.setPiece(i,j,this.board.getPiece(this.board.getClickedPoint()));
                                    this.board.setPiece(this.board.getClickedPoint(),null);
//                                    this.board.setClickedPoint(null);  wrote later
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
                                    if (this.board.getPiece(i,j) instanceof Pawn) {
                                        if (this.board.getPiece(i,j).getColor() == 0 && i == 0) {
                                            promotePawn((Pawn)this.board.getPiece(i,j));
                                        }
                                        else {
                                            if (this.board.getPiece(i,j).getColor() == 1 && i == 7) {
                                                promotePawn((Pawn)this.board.getPiece(i,j));
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
                                    this.board.setClickedPoint(null);
                                    resetImgBoard();
                                    if (this.board.isCheckmated(this.board.getSwitchedTurn(this.board.getTurn()))) {
                                        if (this.board.getTurn() == 0)
                                            handler.postDelayed(new Runnable()
                                            {
                                                @Override
                                                public void run() {
                                                    gameEnded(0);
                                                }
                                            }, 1000);
                                        else
                                            handler.postDelayed(new Runnable()
                                            {
                                                @Override
                                                public void run() {
                                                    gameEnded(1);
                                                }
                                            }, 1000);
                                    }
                                    this.board.switchTurn();
                                    turnText.setText("Turn: " + (this.board.getTurn() == 0 ? "White" : "Black"));
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
        this.board.getPlayers()[pawn.getColor()].getPieces().remove(pawn);
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
        this.board.getPlayers()[pawn.getColor()].getPieces().add(this.board.getPiece(pawn.getPlace()));
        promotedIndex = -1;
        resetImgBoard();
        if (this.board.isCheckmated(this.board.getTurn())) {
            if (this.board.getTurn() == 0)
                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run() {
                        gameEnded(1);
                    }
                }, 1000);
            else
                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run() {
                        gameEnded(0);
                    }
                }, 1000);
        }
    }
}