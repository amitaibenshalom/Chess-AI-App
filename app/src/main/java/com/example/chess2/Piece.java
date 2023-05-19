package com.example.chess2;

import java.util.ArrayList;

public class Piece {

    private Point place;
    private int color;
    private int value;

    public Piece(Point place, int color, int value) {
        this.place = place;
        this.color = color;
        this.value = value;
    }

    public Point getPlace() {
        return place;
    }

    public void setPlace(Point place) {
        this.place = place;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getValue() {
        return value;
    }

    public ArrayList<Point> availableMoves(GameManager gm)
    {
        /* each piece (king, queen, ...) implements its own availableMoves function,
           so this is not a recursive function
           each piece overrides this function.
         */
        return availableMoves(gm);
    }

    public ArrayList<Point> availableMoves2(GameManager gm)
    {
        int color = this.getColor();
        Point place = this.getPlace();
        Piece tempPiece;
        King ownKing = gm.getPlayers()[color].getKing();
        // get the list of the first available moves (destination points) of this piece
        ArrayList<Point> moves = availableMoves(gm);

        // try each available move and check if the king is still safe
        // if the king is not safe after the move, the move is illegal and remove it from the list
        gm.setPiece(place,null);
        for (int i = 0; i < moves.size(); i++) {
            // get the piece in the destination of the move
            tempPiece = gm.getPiece(moves.get(i));
            int removedIndex = -1;
            // if this piece eats another piece on the board then remove it from the other player's list
            // (get the index of the eaten piece and replace it with null)
            if (tempPiece != null) {
                removedIndex = gm.getPlayers()[1-color].getPieces().indexOf(tempPiece);
                gm.getPlayers()[1-color].getPieces().set(removedIndex,null);
            }
            // change the place of this piece to the move's destination
            this.setPlace(moves.get(i));
            gm.setPiece(moves.get(i),this);

            // if king is not safe - illegal move and remove it from the available moves list
            if (ownKing.isChecked(gm)) {
                // place the eaten piece back in it's place
                gm.setPiece(moves.get(i),tempPiece);
                moves.remove(i);
                i--;
                // i is reduced because when you remove an element from an ArrayList,
                // the indexes of the other elements is updated, so you need to stay on the same index
            }
            else
                // place the eaten piece back in it's place
                gm.setPiece(moves.get(i),tempPiece);
            if (tempPiece != null) {
                // if the eaten piece is an actual piece (not null), then return it to the other player's list
                gm.getPlayers()[1-color].getPieces().set(removedIndex,tempPiece);
            }
        }
        // move the piece's place back to its original place and on the board
        this.setPlace(place);
        gm.setPiece(place,this);
        // return the correct, legal list of moves (points)
        return moves;
    }

    public boolean isThreatened(GameManager gm)
    {
        boolean isThreatened = false;
        // get the pieces of the other player
        ArrayList<Piece> enemyPieces = gm.getPlayers()[gm.getSwitchedTurn(color)].getPieces();
        // a list of the available moves for each piece of the other player (enemy)
        ArrayList<Point> avlMovesOfEnemy = null;
        // for each of the enemy's pieces, check all it's available moves and check if this piece is there
        for (int i = 0; i < enemyPieces.size() && !isThreatened; i++) {
            // if the piece has been eaten (piece of the other player) - ignore and continue
            if (enemyPieces.get(i) == null)
                continue;
            // get the available moves of the other player's piece
            avlMovesOfEnemy = enemyPieces.get(i).availableMoves(gm);
            // for each move, check if this piece (this) is in the same place as the destination move for the enemy's piece
            for (int j = 0; j< avlMovesOfEnemy.size() && !isThreatened; j++) {
                if (avlMovesOfEnemy.get(j).equals(this.place))
                    // found a move that
                    isThreatened = true;
            }
        }
        return isThreatened;
    }

    public boolean isThreatenedNoCastling(GameManager gm) {
        boolean isThreatened = false;
        ArrayList<Piece> enemyPieces = gm.getPlayers()[1-color].getPieces();
        ArrayList<Point> avlMovesOfEnemy;
        for (int i = 0; i < enemyPieces.size() && !isThreatened; i++) {
            if (enemyPieces.get(i) == null)
                continue;
            if (enemyPieces.get(i) instanceof King)
                avlMovesOfEnemy = ((King)enemyPieces.get(i)).availableMovesNoCastling(gm);
            else
                avlMovesOfEnemy = enemyPieces.get(i).availableMoves(gm);
            for (int j = 0; j< avlMovesOfEnemy.size() && !isThreatened; j++) {
                if (avlMovesOfEnemy.get(j).equals(this.place))
                    isThreatened = true;
            }
        }
        return isThreatened;
    }

    public boolean isThreatenedNoKing(GameManager gm) {
        boolean isThreatened = false;

//      boolean foundKing = false;
        ArrayList<Piece> enemyPieces = gm.getPlayers()[1-color].getPieces();
        King tempKing = gm.getPlayers()[1-color].getKing();
//        for (int i = 0; i < enemyPieces.size() && !foundKing; i++)
//            if (enemyPieces.get(i) instanceof King) {
//                foundKing = true;
//                tempKing = (King)enemyPieces.get(i);
//                enemyPieces.remove(i);
//            }
        int indexKing = gm.getPlayers()[1-color].getPieces().indexOf(tempKing);
        enemyPieces.set(indexKing,null);
        ArrayList<Point> avlMovesOfEnemy;
        for (int i = 0; i < enemyPieces.size() && !isThreatened; i++) {
            if (enemyPieces.get(i) == null)
                continue;
            avlMovesOfEnemy = enemyPieces.get(i).availableMoves(gm);
            for (int j = 0; j< avlMovesOfEnemy.size() && !isThreatened; j++) {
                if (avlMovesOfEnemy.get(j).equals(this.place))
                    isThreatened = true;
            }
        }
        enemyPieces.set(indexKing,tempKing);
        return isThreatened;
    }

}

