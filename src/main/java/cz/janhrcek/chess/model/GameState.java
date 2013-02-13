/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model;

import java.util.EnumSet;

/**
 *
 * @author jhrcek
 */
public interface GameState {

    EnumSet<CastlingAvailability> getCastlingAvailabilities();

    Square getEnPassantTargetSquare();

    int getFullmoveNumber();

    int getHalfmoveClock();

    Position getPosition();

    boolean isWhiteToMove();

    void setCastlingAvailabilities(EnumSet<CastlingAvailability> castlingAvailabilities);

    void setEnPassantTargetSquare(Square enPassantTargetSquare);

    void setFullmoveNumber(int fullmoveNumber);

    void setHalfmoveClock(int halfmoveClock);

    void setPosition(Position position);

    void setWhiteToMove(boolean whiteToMove);
    
}
