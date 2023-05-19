package com.example.chess2;

import java.util.ArrayList;

public class King extends Piece{

    private boolean isMoved;

    King(Point place, int color){
        super(place,color,290);
        this.isMoved = false;
    }

    public boolean isMoved() {
        return isMoved;
    }

    public void setMoved(boolean moved) {
        isMoved = moved;
    }

    public boolean isChecked(GameManager gm)
    {
        return (this.isThreatenedNoCastling(gm));
    }

    public ArrayList<Point> availableMovesNoCastling(GameManager gm)
    {
        Piece[][] board = gm.getBoard();
        ArrayList<Point> moves = new ArrayList<Point>();
        int x = this.getPlace().getRow();
        int y = this.getPlace().getCol();
        int color = this.getColor();
        int SIZE = board.length;

        if (y+1 < SIZE) {
            if (board[x][y+1] == null) {
                moves.add(new Point(x,y+1));
            }
            else {
                if (board[x][y+1].getColor() != color) {
                    moves.add(new Point(x,y+1));
                }
            }
        }
        if (y-1 >= 0) {
            if (board[x][y-1] == null) {
                moves.add(new Point(x,y-1));
            }
            else {
                if (board[x][y-1].getColor() != color) {
                    moves.add(new Point(x,y-1));
                }
            }
        }

        if (x+1 < SIZE) {
            if (board[x+1][y] == null) {
                moves.add(new Point(x+1,y));
            }
            else {
                if (board[x+1][y].getColor() != color) {
                    moves.add(new Point(x+1,y));
                }
            }
        }
        if (x-1 >=0) {
            if (board[x-1][y] == null) {
                moves.add(new Point(x-1,y));
            }
            else {
                if (board[x-1][y].getColor() != color) {
                    moves.add(new Point(x-1,y));
                }
            }
        }

        if (x+1 < SIZE && y+1 < SIZE) {
            if (board[x+1][y+1] == null) {
                moves.add(new Point(x+1,y+1));
            }
            else {
                if (board[x+1][y+1].getColor() != color) {
                    moves.add(new Point(x+1,y+1));
                }
            }
        }
        if (x-1 >= 0 && y-1 >= 0) {
            if (board[x-1][y-1] == null) {
                moves.add(new Point(x-1,y-1));
            }
            else {
                if (board[x-1][y-1].getColor() != color) {
                    moves.add(new Point(x-1,y-1));
                }
            }
        }

        if (x+1 < SIZE && y-1>= 0) {
            if (board[x+1][y-1] == null) {
                moves.add(new Point(x+1,y-1));
            }
            else {
                if (board[x+1][y-1].getColor() != color) {
                    moves.add(new Point(x+1,y-1));
                }
            }
        }
        if (x-1 >= 0 && y+1 < SIZE) {
            if (board[x-1][y+1] == null) {
                moves.add(new Point(x-1,y+1));
            }
            else {
                if (board[x-1][y+1].getColor() != color) {
                    moves.add(new Point(x-1,y+1));
                }
            }
        }

        return moves;
    }


    public ArrayList<Point> availableMoves(GameManager gm) {
        //return the available moves for a king
        Piece[][] board = gm.getBoard();
        ArrayList<Point> moves = new ArrayList<Point>();
        int x = this.getPlace().getRow();
        int y = this.getPlace().getCol();
        int color = this.getColor();
        int SIZE = board.length;

        if (y+1 < SIZE) {
            if (board[x][y+1] == null) {
                moves.add(new Point(x,y+1));
            }
            else {
                if (board[x][y+1].getColor() != color) {
                    moves.add(new Point(x,y+1));
                }
            }
        }
        if (y-1 >= 0) {
            if (board[x][y-1] == null) {
                moves.add(new Point(x,y-1));
            }
            else {
                if (board[x][y-1].getColor() != color) {
                    moves.add(new Point(x,y-1));
                }
            }
        }

        if (x+1 < SIZE) {
            if (board[x+1][y] == null) {
                moves.add(new Point(x+1,y));
            }
            else {
                if (board[x+1][y].getColor() != color) {
                    moves.add(new Point(x+1,y));
                }
            }
        }
        if (x-1 >=0) {
            if (board[x-1][y] == null) {
                moves.add(new Point(x-1,y));
            }
            else {
                if (board[x-1][y].getColor() != color) {
                    moves.add(new Point(x-1,y));
                }
            }
        }

        if (x+1 < SIZE && y+1 < SIZE) {
            if (board[x+1][y+1] == null) {
                moves.add(new Point(x+1,y+1));
            }
            else {
                if (board[x+1][y+1].getColor() != color) {
                    moves.add(new Point(x+1,y+1));
                }
            }
        }
        if (x-1 >= 0 && y-1 >= 0) {
            if (board[x-1][y-1] == null) {
                moves.add(new Point(x-1,y-1));
            }
            else {
                if (board[x-1][y-1].getColor() != color) {
                    moves.add(new Point(x-1,y-1));
                }
            }
        }

        if (x+1 < SIZE && y-1>= 0) {
            if (board[x+1][y-1] == null) {
                moves.add(new Point(x+1,y-1));
            }
            else {
                if (board[x+1][y-1].getColor() != color) {
                    moves.add(new Point(x+1,y-1));
                }
            }
        }
        if (x-1 >= 0 && y+1 < SIZE) {
            if (board[x-1][y+1] == null) {
                moves.add(new Point(x-1,y+1));
            }
            else {
                if (board[x-1][y+1].getColor() != color) {
                    moves.add(new Point(x-1,y+1));
                }
            }
        }
        if (!this.isMoved && board[x][y+1] == null && board[x][y+2] == null && board[x][y+3] instanceof Rook
        &&  !((Rook) board[x][y+3]).isMoved() && ((!gm.getPlayers()[gm.getSwitchedTurn(color)].getKing().isMoved && !board[x][y].isThreatenedNoKing(gm))
                || (gm.getPlayers()[gm.getSwitchedTurn(color)].getKing().isMoved && !board[x][y].isThreatened(gm)))) {
            board[x][y+1] = new Piece(new Point(x,y+1),color,0);
            if (!board[x][y+1].isThreatened(gm)) {
                board[x][y+2] = new Piece(new Point(x,y+2),color,0);
                if (!board[x][y+2].isThreatened(gm)) {
                    moves.add(new Point(x,y+2));
                }
                board[x][y+2] = null;
            }
            board[x][y+1] = null;
        }
        if (!this.isMoved && board[x][y-1] == null && board[x][y-2] == null && board[x][y-3] == null
                && board[x][y-4] instanceof Rook &&  !((Rook) board[x][y-4]).isMoved() &&
                ((!gm.getPlayers()[gm.getSwitchedTurn(color)].getKing().isMoved && !board[x][y].isThreatenedNoKing(gm))
                || (gm.getPlayers()[gm.getSwitchedTurn(color)].getKing().isMoved && !board[x][y].isThreatened(gm)))) {
            board[x][y-1] = new Piece(new Point(x,y-1),color,0);
            if (!board[x][y-1].isThreatened(gm)) {
                board[x][y-2] = new Piece(new Point(x,y-2),color,0);
                if (!board[x][y-2].isThreatened(gm)) {
                    moves.add(new Point(x,y-2));
                }
                board[x][y-2] = null;
            }
            board[x][y-1] = null;
        }

        return moves;
    }
}
