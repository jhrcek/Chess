package cz.janhrcek.chess.model.impl;

import cz.janhrcek.chess.FEN.Fen;
import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.model.api.Chessboard;
import cz.janhrcek.chess.model.api.Position;
import cz.janhrcek.chess.model.api.enums.Castling;
import cz.janhrcek.chess.model.api.enums.Square;
import java.util.EnumSet;

/**
 * Represents state of the game. Provide all the necessary information to
 * restart a game from a particular position. The Position provides information
 * equivalent of FEN (Forsythe Edwards Notation) string used for representing
 * particular state of chess game.
 *
 * @author jhrcek
 */
public class PositionImpl implements Position {

    private final Chessboard position;
    private final boolean whiteToMove;
    private final EnumSet<Castling> castlingAvailabilities;
    private final Square enPassantTargetSquare;
    private final int halfmoveClock;
    private final int fullmoveNumber;

    /**
     * Creates new instance of Position which corresponds to initial position of
     * the chess game, as described by FIDE rules. This constructor is package
     * friendly to disable instantiation by classes outside the package.
     *
     * The instances of this class are intended to be created by
     * PositionFactory.
     */
    public PositionImpl() {
        try {
            position = new Fen(Fen.INITIAL_POSITION).toChessboard();
            whiteToMove = true;
            castlingAvailabilities = EnumSet.allOf(Castling.class);
            enPassantTargetSquare = null;
            halfmoveClock = 0;
            fullmoveNumber = 1;
        } catch (InvalidFenException ife) {
            throw new AssertionError("Failed to initialize Position instance", ife);
        }
    }

    /**
     * Creates new instance of Position which corresponds to Position determined
     * by constructor parameters. This constructor is package friendly to
     * disable instantiation by classes outside the package.
     *
     * The instances of this class are intended to be created by
     * PositionFactory.
     */
    public PositionImpl(Chessboard p, boolean wToMove, EnumSet<Castling> ca, Square ep, int halfmove, int fullmove) {
        position = p;
        whiteToMove = wToMove;
        castlingAvailabilities = ca;
        enPassantTargetSquare = ep;
        halfmoveClock = halfmove;
        fullmoveNumber = fullmove;
    }

    @Override
    public Chessboard getChessboard() {
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
