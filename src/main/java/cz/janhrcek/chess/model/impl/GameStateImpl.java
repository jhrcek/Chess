/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model.impl;

import cz.janhrcek.chess.model.api.CastlingAvailability;
import cz.janhrcek.chess.model.api.Square;
import cz.janhrcek.chess.model.api.GameState;
import java.util.EnumSet;

/**
 * Represents state of the game. Provide all the necessary information to
 * restart a game from a particular position. The game state provides
 * information equivalent of FEN (Forsythe Edwards Notation) string used for
 * representing particular state of chess game.
 *
 * @author jhrcek
 */
public class GameStateImpl implements GameState {

    private Position position;
    private boolean whiteToMove;
    private EnumSet<CastlingAvailability> castlingAvailabilities;
    private Square enPassantTargetSquare;
    private int halfmoveClock;
    private int fullmoveNumber;

    /**
     * Creates new instance of GameState which corresponds to initial state of
     * the chess game, as described by FIDE rules.
     */
    public GameStateImpl() {
        position = new Position();
        whiteToMove = true;
        castlingAvailabilities = EnumSet.allOf(CastlingAvailability.class);
        enPassantTargetSquare = null;
        halfmoveClock = 0;
        fullmoveNumber = 1;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public boolean isWhiteToMove() {
        return whiteToMove;
    }

    @Override
    public void setWhiteToMove(boolean whiteToMove) {
        this.whiteToMove = whiteToMove;
    }

    @Override
    public EnumSet<CastlingAvailability> getCastlingAvailabilities() {
        return castlingAvailabilities;
    }

    @Override
    public void setCastlingAvailabilities(EnumSet<CastlingAvailability> castlingAvailabilities) {
        this.castlingAvailabilities = castlingAvailabilities;
    }

    @Override
    public Square getEnPassantTargetSquare() {
        return enPassantTargetSquare;
    }

    @Override
    public void setEnPassantTargetSquare(Square enPassantTargetSquare) {
        this.enPassantTargetSquare = enPassantTargetSquare;
    }

    @Override
    public int getHalfmoveClock() {
        return halfmoveClock;
    }

    @Override
    public void setHalfmoveClock(int halfmoveClock) {
        this.halfmoveClock = halfmoveClock;
    }

    @Override
    public int getFullmoveNumber() {
        return fullmoveNumber;
    }

    @Override
    public void setFullmoveNumber(int fullmoveNumber) {
        this.fullmoveNumber = fullmoveNumber;
    }
}
