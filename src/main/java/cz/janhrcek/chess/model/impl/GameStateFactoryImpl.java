/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model.impl;

import cz.janhrcek.chess.FEN.FenParser;
import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.model.api.GameState;
import cz.janhrcek.chess.model.api.GameStateFactory;
import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.RuleChecker;
import cz.janhrcek.chess.model.api.enums.CastlingAvailability;
import static cz.janhrcek.chess.model.api.enums.CastlingAvailability.*;
import cz.janhrcek.chess.model.api.enums.Piece;
import static cz.janhrcek.chess.model.api.enums.Piece.*;
import cz.janhrcek.chess.model.api.enums.Square;
import static cz.janhrcek.chess.model.api.enums.Square.*;
import java.util.EnumSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jhrcek
 */
public class GameStateFactoryImpl implements GameStateFactory {

    private static final Logger LOG = LoggerFactory.getLogger(GameStateFactoryImpl.class);
    private RuleChecker ruleChecker;
    private FenParser parser;

    public GameStateFactoryImpl(RuleChecker rc) {
        ruleChecker = rc;
        parser = new FenParser();
    }

    @Override
    public GameState create(String fen) throws InvalidFenException {
        return parser.fenToGameState(fen);
    }

    @Override
    public GameState create(GameState originState, Move move) throws ChessboardException {
        LOG.info("Creating new GameState using move {}", move);
        ruleChecker.isLegal(move, originState);
        Position p = Position.createFrom(originState.getPosition(), move); //create immutable position
        boolean wtm = !originState.isWhiteToMove(); //flip side to move
        EnumSet<CastlingAvailability> ca = determineCastlingAvailabilities(originState, move); //TODO determine clastling availability
        Square ep = determineEnPassantTargetSquare(move);
        int halfmove = shouldResetHalfmoveClock(originState, move)
                ? 0 : originState.getHalfmoveClock() + 1;
        int fullmove = originState.getFullmoveNumber() + (wtm ? 1 : 0); //increment by one after whites move
        return new GameStateImpl(p, wtm, ca, ep, halfmove, fullmove);
    }

    @Override
    public void setRuleChecker(RuleChecker rc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * En passant target square is a square behind a pawn which moves from it
     * starting position by 2 squares forward. For all other moves it is null.
     *
     * @param move
     * @return
     */
    private Square determineEnPassantTargetSquare(Move move) {
        Piece piece = move.getPiece();
        Square from = move.getFrom();
        Square to = move.getTo();

        if (piece.equals(WHITE_PAWN)
                && from.getFile() == to.getFile()
                && from.getRank() == 1
                && to.getRank() == 3) {
            return Square.getSquare(from.getFile(), 2);
        } else if (piece.equals(BLACK_PAWN)
                && from.getFile() == move.getTo().getFile()
                && from.getRank() == 6
                && to.getRank() == 4) {
            return Square.getSquare(from.getFile(), 5);
        } else {
            return null;
        }
    }

    /**
     * Half-move clock must be reset to 0 after pawn move or after capture
     *
     * @param state
     * @param move
     * @return
     */
    private boolean shouldResetHalfmoveClock(GameState state, Move move) {
        Square to = move.getTo();
        Piece movingPiece = move.getPiece();
        Piece capturedPiece = state.getPosition().getPiece(to);
        if (capturedPiece != null || movingPiece.equals(WHITE_PAWN) || movingPiece.equals(BLACK_PAWN)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determine changed castling availability;
     *
     * @param originState
     * @param move
     * @return
     */
    private EnumSet<CastlingAvailability> determineCastlingAvailabilities(GameState originState, Move move) {
        if (move == null) {
            throw new IllegalArgumentException("move can't be null!");
        }

        EnumSet<CastlingAvailability> oldCa = originState.getCastlingAvailabilities();
        EnumSet<CastlingAvailability> newCa = EnumSet.copyOf(oldCa);

        if (oldCa.isEmpty()) {
            return newCa;
        }

        Piece piece = move.getPiece();
        Square from = move.getFrom();

        switch (piece) {
            case WHITE_KING:
                newCa.remove(WHITE_KINGSIDE);
                newCa.remove(WHITE_QUEENSIDE);
                break;
            case BLACK_KING:
                newCa.remove(BLACK_KINGSIDE);
                newCa.remove(BLACK_QUEENSIDE);
                break;
            case WHITE_ROOK:
                if (from.equals(A1)) {
                    newCa.remove(WHITE_QUEENSIDE);
                }
                if (from.equals(H1)) {
                    newCa.remove(WHITE_KINGSIDE);
                }
                break;
            case BLACK_ROOK:
                if (from.equals(A8)) {
                    newCa.remove(BLACK_QUEENSIDE);
                }
                if (from.equals(H8)) {
                    newCa.remove(BLACK_KINGSIDE);
                }
                break;
            default:
                break;
        }
        if (oldCa.equals(newCa)) {
            LOG.info("    CA: Unchanged");
        } else {
            LOG.info("    CA: modifying from {} to {}", oldCa, newCa);
        }
        return newCa;
    }
}
