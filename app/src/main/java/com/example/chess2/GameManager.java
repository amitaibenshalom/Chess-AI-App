package com.example.chess2;

import java.util.ArrayList;

public class GameManager {

    private Piece[][] board;
    private int turn;
    private final int SIZE = 8;
    private Point clickedPoint;
    private Player[] players;
    private int colorOwned; // the color of the board's owner (player in bottom, mostly white)

    // the weights of the white pawn
    private final double[][] weightWP = {{1,1,1,1,1,1,1,1}
            ,{1,1,1,1,1,1,1,1}
            ,{0.98,0.98,0.98,0.98,0.98,0.98,0.98,0.98}
            ,{0.97,0.97,0.97,0.97,0.97,0.97,0.97,0.97}
            ,{0.96,0.96,0.96,0.96,0.96,0.96,0.96,0.96}
            ,{0.95,0.95,0.95,0.95,0.95,0.95,0.95,0.95}
            ,{0.94,0.94,0.94,0.94,0.94,0.94,0.94,0.94}
            ,{0.93,0.93,0.93,0.93,0.93,0.93,0.93,0.93}};

    // the weights of the black pawn
    private final double[][] weightBP = {{0.93,0.93,0.93,0.93,0.93,0.93,0.93,0.93}
            ,{0.94,0.94,0.94,0.94,0.94,0.94,0.94,0.94}
            ,{0.95,0.95,0.95,0.95,0.95,0.95,0.95,0.95}
            ,{0.96,0.96,0.96,0.96,0.96,0.96,0.96,0.96}
            ,{0.97,0.97,0.97,0.97,0.97,0.97,0.97,0.97}
            ,{0.98,0.98,0.98,0.98,0.98,0.98,0.98,0.98}
            ,{1,1,1,1,1,1,1,1}
            ,{1,1,1,1,1,1,1,1}};

    // the weights of the queen and bishop (for black and white)
    private final double[][] weightQB = {{0.94,0.95,0.96,0.97,0.97,0.96,0.95,0.94}
            ,{0.95,0.96,0.97,0.98,0.98,0.97,0.96,0.95}
            ,{0.96,0.97,0.98,0.99,0.99,0.98,0.97,0.96}
            ,{0.97,0.98,0.99,1,1,0.99,0.98,0.97}
            ,{0.97,0.98,0.99,1,1,0.99,0.98,0.97}
            ,{0.96,0.97,0.98,0.99,0.99,0.98,0.97,0.96}
            ,{0.95,0.96,0.97,0.98,0.98,0.97,0.96,0.95}
            ,{0.94,0.95,0.96,0.97,0.97,0.96,0.95,0.94}};

    // the weights of the white king
    private final double[][] weightWK = {{0.93,0.93,0.92,0.92,0.92,0.92,0.93,0.93}
            ,{0.94,0.94,0.93,0.93,0.93,0.93,0.94,0.94}
            ,{0.95,0.95,0.94,0.94,0.94,0.94,0.95,0.95}
            ,{0.96,0.96,0.95,0.95,0.95,0.95,0.96,0.96}
            ,{0.97,0.97,0.96,0.96,0.96,0.96,0.97,0.97}
            ,{0.98,0.98,0.97,0.97,0.97,0.97,0.98,0.98}
            ,{0.99,0.99,0.99,0.98,0.98,0.98,0.99,0.99}
            ,{1,1,1,0.99,0.99,0.99,1,1}};

    // the weights of the black king
    private final double[][] weightBK = {{1,1,1,0.99,0.99,0.99,1,1}
            ,{0.99,0.99,0.99,0.98,0.98,0.98,0.99,0.99}
            ,{0.98,0.98,0.97,0.97,0.97,0.97,0.98,0.98}
            ,{0.97,0.97,0.96,0.96,0.96,0.96,0.97,0.97}
            ,{0.96,0.96,0.95,0.95,0.95,0.95,0.96,0.96}
            ,{0.95,0.95,0.94,0.94,0.94,0.94,0.95,0.95}
            ,{0.94,0.94,0.93,0.93,0.93,0.93,0.94,0.94}
            ,{0.93,0.93,0.92,0.92,0.92,0.92,0.93,0.93}};

    // the weights of the knights (black and white)
    private final double[][] weightN =
            {{0.94,0.95,0.96,0.97,0.97,0.96,0.95,0.94}
            ,{0.95,0.96,0.97,0.98,0.98,0.97,0.96,0.95}
            ,{0.97,0.97,0.99,0.99,0.99,0.99,0.97,0.97}
            ,{0.98,0.99,0.99,0.99,0.99,0.99,0.99,0.98}
            ,{0.98,0.99,0.99,0.99,0.99,0.99,0.99,0.98}
            ,{0.97,0.97,0.99,0.99,0.99,0.99,0.97,0.97}
            ,{0.95,0.96,0.97,0.98,0.98,0.97,0.96,0.95}
            ,{0.94,0.95,0.96,0.97,0.97,0.96,0.95,0.94}};

    // the weights of the rook (black and white)
    private final double[][] weightR = {{1,0.99,0.97,0.97,0.97,0.97,0.99,1}
            ,{1,0.99,0.97,0.97,0.97,0.97,0.99,1}
            ,{1,0.99,0.97,0.97,0.97,0.97,0.99,1}
            ,{1,0.99,0.97,0.97,0.97,0.97,0.99,1}
            ,{1,0.99,0.97,0.97,0.97,0.97,0.99,1}
            ,{1,0.99,0.97,0.97,0.97,0.97,0.99,1}
            ,{1,0.99,0.97,0.97,0.97,0.97,0.99,1}
            ,{1,0.99,0.98,0.98,0.98,0.98,0.99,1}};


    public GameManager(int color) {

        this.turn = 0;
        this.clickedPoint = null;
        this.colorOwned = color;
        //create new board

        board = new Piece[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            board[SIZE - 2][i] = new Pawn(new Point(SIZE - 2, i), color);
            board[1][i] = new Pawn(new Point(1, i), 1-color);
        }
        board[SIZE-1][0] = new Rook(new Point(SIZE-1,0),color);
        board[SIZE-1][SIZE-1] = new Rook(new Point(SIZE-1,SIZE-1),color);
        board[0][0] = new Rook(new Point(0,0),1-color);
        board[0][SIZE-1] = new Rook(new Point(0,SIZE-1),1-color);

        board[SIZE-1][1] = new Knight(new Point(SIZE-1,1),color);
        board[SIZE-1][SIZE-2] = new Knight(new Point(SIZE-1,SIZE-2),color);
        board[0][1] = new Knight(new Point(0,1),1-color);
        board[0][SIZE-2] = new Knight(new Point(0,SIZE-2),1-color);

        board[SIZE-1][2] = new Bishop(new Point(SIZE-1,2),color);
        board[SIZE-1][SIZE-3] = new Bishop(new Point(SIZE-1,SIZE-3),color);
        board[0][2] = new Bishop(new Point(0,2),1-color);
        board[0][SIZE-3] = new Bishop(new Point(0,SIZE-3),1-color);

        if (color == 0) {
            board[SIZE-1][3] = new Queen(new Point(SIZE-1,3),0);
            board[0][3] = new Queen(new Point(0,3),1);

            board[SIZE-1][4] = new King(new Point(SIZE-1,4),0);
            board[0][4] = new King(new Point(0,4),1);
        }
        else {
            board[SIZE-1][4] = new Queen(new Point(SIZE-1,4),1);
            board[0][4] = new Queen(new Point(0,4),0);

            board[SIZE-1][3] = new King(new Point(SIZE-1,3),1);
            board[0][3] = new King(new Point(0,3),0);
        }

        this.players = new Player[2];
        this.players[0] = new Player(0, this);
        this.players[1] = new Player(1, this);

    }

    public GameManager()
    {
        this.turn = 0;
//        this.playsFromSet = 0;
        this.clickedPoint = null;

        //create new board

        board = new Piece[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            board[SIZE - 2][i] = new Pawn(new Point(SIZE - 2, i), 0);
            board[1][i] = new Pawn(new Point(1, i), 1);
        }
        board[SIZE-1][0] = new Rook(new Point(SIZE-1,0),0);
        board[SIZE-1][SIZE-1] = new Rook(new Point(SIZE-1,SIZE-1),0);
        board[0][0] = new Rook(new Point(0,0),1);
        board[0][SIZE-1] = new Rook(new Point(0,SIZE-1),1);

        board[SIZE-1][1] = new Knight(new Point(SIZE-1,1),0);
        board[SIZE-1][SIZE-2] = new Knight(new Point(SIZE-1,SIZE-2),0);
        board[0][1] = new Knight(new Point(0,1),1);
        board[0][SIZE-2] = new Knight(new Point(0,SIZE-2),1);

        board[SIZE-1][2] = new Bishop(new Point(SIZE-1,2),0);
        board[SIZE-1][SIZE-3] = new Bishop(new Point(SIZE-1,SIZE-3),0);
        board[0][2] = new Bishop(new Point(0,2),1);
        board[0][SIZE-3] = new Bishop(new Point(0,SIZE-3),1);

        board[SIZE-1][3] = new Queen(new Point(SIZE-1,3),0);
        board[0][3] = new Queen(new Point(0,3),1);

        board[SIZE-1][4] = new King(new Point(SIZE-1,4),0);
        board[0][4] = new King(new Point(0,4),1);

        this.players = new Player[2];
        this.players[0] = new Player(0, this);
        this.players[1] = new Player(1, this);

    }

    public int getColorOwned() {
        return colorOwned;
    }

    public Piece getPiece(Point p) {
        return this.board[p.getRow()][p.getCol()];
    }

    public Piece getPiece(int x, int y) {
        return this.board[x][y];
    }

    public void setPiece(Point point, Piece piece)
    {
        this.board[point.getRow()][point.getCol()] = piece;
    }
    public void setPiece(int x, int y,  Piece piece)
    {
        this.board[x][y] = piece;
    }

    public boolean isCheckmated(int color) {
        // if the king is not in check then the player can not be checkmated
        if (!this.players[color].getKing().isChecked(this))
            return false;
        // check if the player can not make any move at all
        ArrayList<Piece> pieces = this.players[color].getPieces();
        for (int i = 0; i < pieces.size(); i++) {
            if (pieces.get(i) == null)
                continue;
            if (pieces.get(i).availableMoves2(this).size() != 0)
                return false;
        }
        // color is checkmated
        return true;
    }

    public int getTurn() {
        return turn;
    }

//    public void setTurn(int turn) {
//        this.turn = turn;
//    }

    public Piece[][] getBoard() {
        return board;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Point getClickedPoint() {
        return clickedPoint;
    }

    public void setClickedPoint(Point clickedPoint) {
        this.clickedPoint = clickedPoint;
    }

    public int getSwitchedTurn(int color) {
        if (color == 1)
            return 0;
        return 1;
    }

    public void switchTurn() {
        if (this.turn == 0)
            this.turn = 1;
        else
            this.turn = 0;
    }

//    public boolean checkIfStillInCheck(int x, int y, Piece p) {
//        boolean isChecked = false;
//        return
//    }

    public void printBoard() {
        //prints the board
        System.out.print("   ");
        for (int i = 0; i < SIZE; i++)
            System.out.print(" " + i + " ");
        System.out.println();
        for (int i = 0; i < SIZE; i++) {
            System.out.print(" " + i + " ");
            for (int j = 0; j < SIZE; j++) {
                if (this.board[i][j] instanceof Pawn) {
                    if (this.board[i][j].getColor() == 0)
                        System.out.print("wP ");
                    else
                        System.out.print("bP ");
                } else if (this.board[i][j] instanceof Rook) {
                    if (this.board[i][j].getColor() == 0)
                        System.out.print("wR ");
                    else
                        System.out.print("bR ");
                } else if (this.board[i][j] instanceof Bishop) {
                    if (this.board[i][j].getColor() == 0)
                        System.out.print("wB ");
                    else
                        System.out.print("bB ");
                } else if (this.board[i][j] instanceof Knight) {
                    if (this.board[i][j].getColor() == 0)
                        System.out.print("wN ");
                    else
                        System.out.print("bN ");
                } else if (this.board[i][j] instanceof Queen) {
                    if (this.board[i][j].getColor() == 0)
                        System.out.print("wQ ");
                    else
                        System.out.print("bQ ");
                } else if (this.board[i][j] instanceof King) {
                    if (this.board[i][j].getColor() == 0)
                        System.out.print("wK ");
                    else
                        System.out.print("bK ");
                } else
                    System.out.print("   ");
            }
            System.out.println();
        }
    }

    public int getValueBoard0() {
        int value = 0;
        for (int i = 0; i < this.players[0].getPieces().size(); i++) {
            value += (this.players[0].getPieces().get(i).getValue());
        }
        for (int i = 0; i < this.players[1].getPieces().size(); i++) {
                value -= (this.players[1].getPieces().get(i).getValue());
        }
        return value;
    }

    public int getValueBoard() {
        int value = 0;
        for (int i = 0; i < this.players[0].getPieces().size(); i++) {
            if (this.players[0].getPieces().get(i) == null)
                continue;
            if (this.players[0].getPieces().get(i) instanceof Pawn)
                value += (this.players[0].getPieces().get(i).getValue()
                        *weightWP[this.players[0].getPieces().get(i).getPlace().getRow()]
                        [this.players[0].getPieces().get(i).getPlace().getCol()]);
            else {
                if (this.players[0].getPieces().get(i) instanceof Knight)
//                    value += (this.players[0].getPieces().get(i).getValue()
//                            *weightN[this.players[0].getPieces().get(i).getPlace().getRow()]
//                            [this.players[0].getPieces().get(i).getPlace().getCol()]);
                    value += this.players[0].getPieces().get(i).getValue();
                else {
                    if (this.players[0].getPieces().get(i) instanceof Bishop ||
                            this.players[0].getPieces().get(i) instanceof Queen)
                        value += (this.players[0].getPieces().get(i).getValue()
                                *weightQB[this.players[0].getPieces().get(i).getPlace().getRow()]
                                [this.players[0].getPieces().get(i).getPlace().getCol()]);
                    else {
                        if (this.players[0].getPieces().get(i) instanceof Rook)
                            value += (this.players[0].getPieces().get(i).getValue()
                                    *weightR[this.players[0].getPieces().get(i).getPlace().getRow()]
                                    [this.players[0].getPieces().get(i).getPlace().getCol()]);
                        else {
                            value += (this.players[0].getPieces().get(i).getValue()
                                    *weightWK[this.players[0].getPieces().get(i).getPlace().getRow()]
                                    [this.players[0].getPieces().get(i).getPlace().getCol()]);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < this.players[1].getPieces().size(); i++) {
            if (this.players[1].getPieces().get(i) == null)
                continue;
            if (this.players[1].getPieces().get(i) instanceof Pawn)
                value -= (this.players[1].getPieces().get(i).getValue()
                        *weightBP[this.players[1].getPieces().get(i).getPlace().getRow()]
                        [this.players[1].getPieces().get(i).getPlace().getCol()]);
            else {
                if (this.players[1].getPieces().get(i) instanceof Knight)
//                    value -= (this.players[1].getPieces().get(i).getValue()
//                            *weightN[this.players[1].getPieces().get(i).getPlace().getRow()]
//                            [this.players[1].getPieces().get(i).getPlace().getCol()]);
                    value -= this.players[1].getPieces().get(i).getValue();
                else {
                    if (this.players[1].getPieces().get(i) instanceof Bishop ||
                            this.players[1].getPieces().get(i) instanceof Queen)
                        value -= (this.players[1].getPieces().get(i).getValue()
                                *weightQB[this.players[1].getPieces().get(i).getPlace().getRow()]
                                [this.players[1].getPieces().get(i).getPlace().getCol()]);
                    else {
                        if (this.players[1].getPieces().get(i) instanceof Rook)
                            value -= (this.players[1].getPieces().get(i).getValue()
                                    *weightR[this.players[1].getPieces().get(i).getPlace().getRow()]
                                    [this.players[1].getPieces().get(i).getPlace().getCol()]);
                        else {
                            value -= (this.players[1].getPieces().get(i).getValue()
                                    *weightBK[this.players[1].getPieces().get(i).getPlace().getRow()]
                                    [this.players[1].getPieces().get(i).getPlace().getCol()]);
                        }
                    }
                }
            }
        }
        return value;
    }

    public int getValueBoard2() {
        int value = 0;
        for (int i = 0; i < this.players[0].getPieces().size(); i++) {
            if (this.players[0].getPieces().get(i) == null)
                continue;
            if (this.players[0].getPieces().get(i) instanceof Pawn)
                value += (this.players[0].getPieces().get(i).getValue()
                        *weightWP[this.players[0].getPieces().get(i).getPlace().getRow()]
                        [this.players[0].getPieces().get(i).getPlace().getCol()]);
            else {
                if (this.players[0].getPieces().get(i) instanceof Knight
                    || this.players[0].getPieces().get(i) instanceof Queen)
//                    value += (this.players[0].getPieces().get(i).getValue()
//                            *weightN[this.players[0].getPieces().get(i).getPlace().getRow()]
//                            [this.players[0].getPieces().get(i).getPlace().getCol()]);
                    value += this.players[0].getPieces().get(i).getValue();
                else {
                    if (this.players[0].getPieces().get(i) instanceof Bishop)
                        value += (this.players[0].getPieces().get(i).getValue()
                                *weightQB[this.players[0].getPieces().get(i).getPlace().getRow()]
                                [this.players[0].getPieces().get(i).getPlace().getCol()]);
                    else {
                        if (this.players[0].getPieces().get(i) instanceof Rook)
                            value += (this.players[0].getPieces().get(i).getValue()
                                    *weightR[this.players[0].getPieces().get(i).getPlace().getRow()]
                                    [this.players[0].getPieces().get(i).getPlace().getCol()]);
                        else {
                            value += (this.players[0].getPieces().get(i).getValue()
                                    *weightWK[this.players[0].getPieces().get(i).getPlace().getRow()]
                                    [this.players[0].getPieces().get(i).getPlace().getCol()]);
                        }
                    }
                }
            }
        }
        for (int i = 0; i < this.players[1].getPieces().size(); i++) {
            if (this.players[1].getPieces().get(i) == null)
                continue;
            if (this.players[1].getPieces().get(i) instanceof Pawn)
                value -= (this.players[1].getPieces().get(i).getValue()
                        *weightBP[this.players[1].getPieces().get(i).getPlace().getRow()]
                        [this.players[1].getPieces().get(i).getPlace().getCol()]);
            else {
                if (this.players[1].getPieces().get(i) instanceof Knight
                    || this.players[1].getPieces().get(i) instanceof Queen)
//                    value -= (this.players[1].getPieces().get(i).getValue()
//                            *weightN[this.players[1].getPieces().get(i).getPlace().getRow()]
//                            [this.players[1].getPieces().get(i).getPlace().getCol()]);
                    value -= this.players[1].getPieces().get(i).getValue();
                else {
                    if (this.players[1].getPieces().get(i) instanceof Bishop)
                        value -= (this.players[1].getPieces().get(i).getValue()
                                *weightQB[this.players[1].getPieces().get(i).getPlace().getRow()]
                                [this.players[1].getPieces().get(i).getPlace().getCol()]);
                    else {
                        if (this.players[1].getPieces().get(i) instanceof Rook)
                            value -= (this.players[1].getPieces().get(i).getValue()
                                    *weightR[this.players[1].getPieces().get(i).getPlace().getRow()]
                                    [this.players[1].getPieces().get(i).getPlace().getCol()]);
                        else {
                            value -= (this.players[1].getPieces().get(i).getValue()
                                    *weightBK[this.players[1].getPieces().get(i).getPlace().getRow()]
                                    [this.players[1].getPieces().get(i).getPlace().getCol()]);
                        }
                    }
                }
            }
        }
        return value;
    }
}

