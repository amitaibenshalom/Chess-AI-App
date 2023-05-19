package com.example.chess2;

import java.util.ArrayList;

public class Bishop extends Piece{

    Bishop(Point place, int color){
        super(place,color,300);
    }

    public ArrayList<Point> availableMoves(GameManager gm) {
        //return the available moves for a bishop
        Piece[][] board = gm.getBoard();
        ArrayList<Point> moves = new ArrayList<Point>();
        int x = this.getPlace().getRow();
        int y = this.getPlace().getCol();
        int color = this.getColor();
        int SIZE = board.length;
        boolean found5 = false,found6 = false,found7 = false,found8 = false;
        int i=1;

        while (i < SIZE && (!found5 || !found6 || !found7 || !found8)) {
            if (!found5 && x+i < SIZE && y+i < SIZE) {
                if (board[x+i][y+i] == null) {
                    moves.add(new Point(x+i,y+i));
                }
                else {
                    found5 = true;
                    if (board[x+i][y+i].getColor() != color) {
                        moves.add(new Point(x+i,y+i));
                    }
                }
            }
            if (!found6 && x-i >= 0 && y-i >= 0) {
                if (board[x-i][y-i] == null) {
                    moves.add(new Point(x-i,y-i));
                }
                else {
                    found6 = true;
                    if (board[x-i][y-i].getColor() != color) {
                        moves.add(new Point(x-i,y-i));
                    }
                }
            }

            if (!found7 && x+i < SIZE && y-i>= 0) {
                if (board[x+i][y-i] == null) {
                    moves.add(new Point(x+i,y-i));
                }
                else {
                    found7 = true;
                    if (board[x+i][y-i].getColor() != color) {
                        moves.add(new Point(x+i,y-i));
                    }
                }
            }
            if (!found8 && x-i >= 0 && y+i < SIZE) {
                if (board[x-i][y+i] == null) {
                    moves.add(new Point(x-i,y+i));
                }
                else {
                    found8 = true;
                    if (board[x-i][y+i].getColor() != color) {
                        moves.add(new Point(x-i,y+i));
                    }
                }
            }
            i++;
        }
        return moves;
    }
}

