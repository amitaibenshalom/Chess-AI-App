package com.example.chess2;

import java.util.ArrayList;

public class Player {

    private int color;
    private ArrayList<Piece> pieces;
    private King ownKing;

    public Player(int color, GameManager board) {
        this.color = color;
        this.pieces = new ArrayList<Piece>();
        if (color != board.getColorOwned()) {
            for (int i = 0; i <= 1; i++)
                for (int j = 0; j < 8; j++) {
                    this.pieces.add(board.getPiece(i,j));
                    if (board.getPiece(i,j) instanceof King)
                        ownKing = (King)board.getPiece(i,j);
                }
        }
        else {
            for (int i = 6; i <= 7; i++)
                for (int j = 0; j < 8; j++) {
                    this.pieces.add(board.getPiece(i,j));
                    if (board.getPiece(i,j) instanceof King)
                        ownKing = (King)board.getPiece(i,j);
                }
        }
    }

    public int getColor() {
        return color;
    }

    public King getKing() {
        return ownKing;
    }

    public ArrayList<Piece> getPieces() {
        return pieces;
    }

}