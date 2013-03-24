package cz.janhrcek.chess.model.impl;

import cz.janhrcek.chess.model.api.GameState;
import cz.janhrcek.chess.model.api.enums.Castling;
import cz.janhrcek.chess.model.api.enums.Square;
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

    private MutablePosition position;
    private boolean whiteToMove;
    private EnumSet<Castling> castlingAvailabilities;
    private Square enPassantTargetSquare;
    private int halfmoveClock;
    private int fullmoveNumber;

    /**
     * Creates new instance of GameState which corresponds to initial state of
     * the chess game, as described by FIDE rules. This constructor is package
     * friendly to disable instantiation by classes outside the package.
     *
     * The instances of this class are intended to be created by
     * GameStateFactory.
     */
    public GameStateImpl() {
        position = new MutablePosition();
        whiteToMove = true;
        castlingAvailabilities = EnumSet.allOf(Castling.class);
        enPassantTargetSquare = null;
        halfmoveClock = 0;
        fullmoveNumber = 1;
    }

    /**
     * Creates new instance of GameState which corresponds to state given by
     * constructor parameters. This constructor is package friendly to disable
     * instantiation by classes outside the package.
     *
     * The instances of this class are intended to be created by
     * GameStateFactory.
     */
    public GameStateImpl(MutablePosition p, boolean wToMove, EnumSet<Castling> ca, Square ep, int halfmove, int fullmove) {
        position = p;
        whiteToMove = wToMove;
        castlingAvailabilities = ca;
        enPassantTargetSquare = ep;
        halfmoveClock = halfmove;
        fullmoveNumber = fullmove;
    }

    @Override
    public MutablePosition getPosition() {
        return position;
    }

    @Override
    public boolean isWhiteToMove() {
        return whiteToMove;
    }

    @Override
    public EnumSet<Castling> getCastlings() {
        return castlingAvailabilities;
    }

    @Override
    public Square getEnPassantTarget() {
        return enPassantTargetSquare;
    }

    @Override
    public int getHalfmoveClock() {
        return halfmoveClock;
    }

    @Override
    public int getFullmoveNumber() {
        return fullmoveNumber;
    }
}
