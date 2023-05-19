package com.example.chess2;

import java.util.ArrayList;

public class Knight extends Piece{

    Knight(Point place, int color){
        super(place,color,300);
    }

    public ArrayList<Point> availableMoves(GameManager gm)
    {
        // returns the general available moves (list of points) for a knight
        Piece[][] board = gm.getBoard();
        ArrayList<Point> moves = new ArrayList<Point>();
        int x = this.getPlace().getRow();
        int y = this.getPlace().getCol();
        int color = this.getColor();
        int SIZE = board.length;
        if (color == 0) {
            if (x+1 < SIZE) {
                if (y-2 >= 0 && (board[x+1][y-2] == null || (board[x+1][y-2] != null
                        && board[x+1][y-2].getColor()==1)))
                    moves.add(new Point(x+1,y-2));
                if (y+2 < SIZE && (board[x+1][y+2] == null || (board[x+1][y+2] != null
                        && board[x+1][y+2].getColor()==1)))
                    moves.add(new Point(x+1,y+2));
                if (x+2 < SIZE) {
                    if (y-1 >= 0 && (board[x+2][y-1] == null || (board[x+2][y-1] != null
                            && board[x+2][y-1].getColor()==1)))
                        moves.add(new Point(x+2,y-1));
                    if (y+1 < SIZE && (board[x+2][y+1] == null || (board[x+2][y+1] != null
                            && board[x+2][y+1].getColor()==1)))
                        moves.add(new Point(x+2,y+1));
                }
            }
            if (x-1 >= 0) {
                if (y-2 >= 0 && (board[x-1][y-2] == null || (board[x-1][y-2] != null
                        && board[x-1][y-2].getColor()==1)))
                    moves.add(new Point(x-1,y-2));
                if (y+2 < SIZE && (board[x-1][y+2] == null || (board[x-1][y+2] != null
                        && board[x-1][y+2].getColor()==1)))
                    moves.add(new Point(x-1,y+2));
                if (x-2 >= 0) {
                    if (y-1 >= 0 && (board[x-2][y-1] == null || (board[x-2][y-1] != null
                            && board[x-2][y-1].getColor()==1)))
                        moves.add(new Point(x-2,y-1));
                    if (y+1 < SIZE && (board[x-2][y+1] == null || (board[x-2][y+1] != null
                            && board[x-2][y+1].getColor()==1)))
                        moves.add(new Point(x-2,y+1));
                }
            }
        }
        else {
            if (x+1 < SIZE) {
                if (y-2 >= 0 && (board[x+1][y-2] == null || (board[x+1][y-2] != null
                        && board[x+1][y-2].getColor()==0)))
                    moves.add(new Point(x+1,y-2));
                if (y+2 < SIZE && (board[x+1][y+2] == null || (board[x+1][y+2] != null
                        && board[x+1][y+2].getColor()==0)))
                    moves.add(new Point(x+1,y+2));
                if (x+2 < SIZE) {
                    if (y-1 >= 0 && (board[x+2][y-1] == null || (board[x+2][y-1] != null
                            && board[x+2][y-1].getColor()==0)))
                        moves.add(new Point(x+2,y-1));
                    if (y+1 < SIZE && (board[x+2][y+1] == null || (board[x+2][y+1] != null
                            && board[x+2][y+1].getColor()==0)))
                        moves.add(new Point(x+2,y+1));
                }
            }
            if (x-1 >= 0) {
                if (y-2 >= 0 && (board[x-1][y-2] == null || (board[x-1][y-2] != null
                        && board[x-1][y-2].getColor()==0)))
                    moves.add(new Point(x-1,y-2));
                if (y+2 < SIZE && (board[x-1][y+2] == null || (board[x-1][y+2] != null
                        && board[x-1][y+2].getColor()==0)))
                    moves.add(new Point(x-1,y+2));
                if (x-2 >= 0) {
                    if (y-1 >= 0 && (board[x-2][y-1] == null || (board[x-2][y-1] != null
                            && board[x-2][y-1].getColor()==0)))
                        moves.add(new Point(x-2,y-1));
                    if (y+1 < SIZE && (board[x-2][y+1] == null || (board[x-2][y+1] != null
                            && board[x-2][y+1].getColor()==0)))
                        moves.add(new Point(x-2,y+1));
                }
            }
        }
        return moves;
    }

}
