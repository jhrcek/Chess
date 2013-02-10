/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model;

import java.util.EnumSet;

/**
 * Represents state of the game. Provide all the necessary information to
 * restart a game from a particular position. The game state provides
 * information equivalent of FEN (Forsythe Edwards Notation) string used for
 * representing particular state of chess game.
 *
 * @author jhrcek
 */
public class GameState {

    public Position getPosition() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public boolean isWhiteToMove() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public EnumSet<CastlingAvailability> getCastlingAvailability() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Square getEnPassantTargetSquare() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void getHalfmoveClock() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void getFullmoveNumber() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
