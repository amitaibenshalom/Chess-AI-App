package com.example.chess2;

import java.util.ArrayList;

public class Pawn extends Piece {


    Pawn(Point place, int color) {
        super(place, color, 100);
    }


    public ArrayList<Point> availableMoves(GameManager gm) {
        //returns the general available moves for a pawn

        //get the board (matrix of pieces)
        Piece[][] board = gm.getBoard();
        // create an empty list of moves (dest points)
        ArrayList<Point> moves = new ArrayList<Point>();
        int x = this.getPlace().getRow();
        int y = this.getPlace().getCol();
        int color = this.getColor();
        int SIZE = board.length;
        // if the color of the pawn is the color of the player in the low area of the board (rows 7 and 8),
        // then the pawn is going up in the board (index of y level is upside down - getting reduced)
        if (color == gm.getColorOwned()) {
            // if not in the top level
            if (x > 0) {
                // if square up is empty, add move (point) to list
                if (board[x - 1][y] == null) {
                    moves.add(new Point(x - 1, y));
                    // if on the second row - then the pawn can move two squares up (if empty)
                    if (x == SIZE - 2)
                        if (board[x - 2][y] == null) {
                            moves.add(new Point(x - 2, y));
                        }
                }
                // check for available eating (a pawn can eat diagonally one square up):
                if (y > 0 && board[x - 1][y - 1] != null && board[x - 1][y - 1].getColor() != color)
                    moves.add(new Point(x - 1, y - 1));
                if (y < SIZE - 1 && board[x - 1][y + 1] != null && board[x - 1][y + 1].getColor() != color)
                    moves.add(new Point(x - 1, y + 1));
            }
        }
        // if the pawn color is on the other half of the board (high area - rows 1 and 2)
        // do the exact same thing but from the other side (from above)
        else {
            if (x < SIZE - 1) {
                if (board[x + 1][y] == null) {
                    moves.add(new Point(x + 1, y));
                    if (x == 1)
                        if (board[x + 2][y] == null) {
                            moves.add(new Point(x + 2, y));
                        }
                }
                if (y > 0 && board[x + 1][y - 1] != null && board[x + 1][y - 1].getColor() != color)
                    moves.add(new Point(x + 1, y - 1));
                if (y < SIZE - 1 && board[x + 1][y + 1] != null && board[x + 1][y + 1].getColor() != color)
                    moves.add(new Point(x + 1, y + 1));
            }
        }

        // return the moves for the pawn
        return moves;
    }
}
