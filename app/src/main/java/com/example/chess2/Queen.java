package com.example.chess2;
import java.util.ArrayList;

public class Queen extends Piece {

    Queen(Point place, int color){
        super(place,color,900);
    }

    public ArrayList<Point> availableMoves(GameManager gm) {
        //return the available moves for a queen
        Piece[][] board = gm.getBoard();
        ArrayList<Point> moves = new ArrayList<Point>();
        int x = this.getPlace().getRow();
        int y = this.getPlace().getCol();
        int color = this.getColor();
        int SIZE = board.length;
        boolean found1 = false,found2 = false,found3 = false,found4 = false,
                found5 = false,found6 = false,found7 = false,found8 = false;
        int i=1;

        while (i < SIZE && (!found1 || !found2 || !found3 || !found4 ||
                !found5 || !found6 || !found7 || !found8)) {
            if (!found1 && y+i < SIZE) {
                if (board[x][y+i] == null) {
                    moves.add(new Point(x,y+i));
                }
                else {
                    found1 = true;
                    if (board[x][y+i].getColor() != color) {
                        moves.add(new Point(x,y+i));
                    }
                }
            }
            if (!found2 && y-i >= 0) {
                if (board[x][y-i] == null) {
                    moves.add(new Point(x,y-i));
                }
                else {
                    found2 = true;
                    if (board[x][y-i].getColor() != color) {
                        moves.add(new Point(x,y-i));
                    }
                }
            }

            if (!found3 && x+i < SIZE) {
                if (board[x+i][y] == null) {
                    moves.add(new Point(x+i,y));
                }
                else {
                    found3 = true;
                    if (board[x+i][y].getColor() != color) {
                        moves.add(new Point(x+i,y));
                    }
                }
            }
            if (!found4 && x-i >=0) {
                if (board[x-i][y] == null) {
                    moves.add(new Point(x-i,y));
                }
                else {
                    found4 = true;
                    if (board[x-i][y].getColor() != color) {
                        moves.add(new Point(x-i,y));
                    }
                }
            }

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
