package cz.janhrcek.chess.model.impl;

import cz.janhrcek.chess.model.api.Chessboard;
import cz.janhrcek.chess.FEN.Fen;
import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.model.api.Position;
import cz.janhrcek.chess.model.api.PositionFactory;
import cz.janhrcek.chess.rules.IllegalMoveException;
import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.RuleChecker;
import cz.janhrcek.chess.model.api.enums.Castling;
import static cz.janhrcek.chess.model.api.enums.Castling.*;
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
public class PositionFactoryImpl implements PositionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PositionFactoryImpl.class);
    private RuleChecker ruleChecker;

    public PositionFactoryImpl(RuleChecker rc) {
        ruleChecker = rc;
    }

    @Override
    public Position create(String fenStr) throws InvalidFenException {
        return new Fen(fenStr).toPosition();
    }

    @Override
    public Position create(Position originPosition, Move move) throws PieceNotPresentException, IllegalMoveException {
        LOG.info("Creating new Position using {}", move);
        ruleChecker.checkLegality(move, originPosition);
        Chessboard p = originPosition.getChessboard().createNewPositionUsing(move);
        boolean wtm = !originPosition.isWhiteToMove(); //flip side to move
        EnumSet<Castling> ca = determineCastlingAvailabilities(originPosition, move);
        Square ep = determineEnPassantTargetSquare(move);
        int halfmove = shouldResetHalfmoveClock(originPosition, move)
                ? 0 : originPosition.getHalfmoveClock() + 1;
        int fullmove = originPosition.getFullmoveNumber() + (wtm ? 1 : 0); //increment by one after black's move
        return new PositionImpl(p, wtm, ca, ep, halfmove, fullmove);
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
     * @param position
     * @param move
     * @return
     */
    private boolean shouldResetHalfmoveClock(Position position, Move move) {
        Square to = move.getTo();
        Piece movingPiece = move.getPiece();
        Piece capturedPiece = position.getChessboard().getPiece(to);
        if (capturedPiece != null || movingPiece.equals(WHITE_PAWN) || movingPiece.equals(BLACK_PAWN)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determine changed castling availability;
     *
     * @param origPosition
     * @param move
     * @return
     */
    private EnumSet<Castling> determineCastlingAvailabilities(Position origPosition, Move move) {
        if (move == null) {
            throw new IllegalArgumentException("move can't be null!");
        }

        EnumSet<Castling> oldCa = origPosition.getCastlings();
        EnumSet<Castling> newCa = EnumSet.copyOf(oldCa);

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
            LOG.debug("    CA: Unchanged");
        } else {
            LOG.debug("    CA: modifying from {} to {}", oldCa, newCa);
        }
        return newCa;
    }
}
