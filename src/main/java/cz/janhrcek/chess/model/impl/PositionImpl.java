package cz.janhrcek.chess.model.impl;

import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.Position;
import cz.janhrcek.chess.model.api.enums.Piece;
import cz.janhrcek.chess.model.api.enums.Square;
import static cz.janhrcek.chess.model.api.enums.Piece.*;
import static cz.janhrcek.chess.model.api.enums.Square.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jhrcek
 */
public class PositionImpl implements Position {

    public PositionImpl() {
        piecePlacement = new HashMap<>(INIT_PIECE_PLACEMENT);
    }

    public PositionImpl(Map<Square, Piece> piecePlacement) {
        this.piecePlacement = new HashMap<>(piecePlacement);
    }

    @Override
    public Piece getPiece(Square square) {
        return piecePlacement.get(square);
    }

    @Override
    public Position createNewPositionUsing(Move move) {
        Map<Square, Piece> newPiecePlacement = new HashMap<>(piecePlacement); //TODO - verify, that this HashMap constructor copies the position
        Square from = move.getFrom();
        if (newPiecePlacement.containsKey(from)) {
            newPiecePlacement.put(move.getTo(), newPiecePlacement.get(from));
            newPiecePlacement.remove(from);
        } else {
            throw new IllegalStateException(String.format("Request to create new"
                    + " position from \n %s \n using %s cannot be satisfied, "
                    + "because there is no piece on the from square %s!",
                    this.toString(), move, from));
        }
        return new PositionImpl(newPiecePlacement);
    }
    private final Map<Square, Piece> piecePlacement;
    private static final Map<Square, Piece> INIT_PIECE_PLACEMENT;

    static {
        INIT_PIECE_PLACEMENT = new HashMap<>();
        INIT_PIECE_PLACEMENT.put(A8, BLACK_ROOK);
        INIT_PIECE_PLACEMENT.put(B8, BLACK_KNIGHT);
        INIT_PIECE_PLACEMENT.put(C8, BLACK_BISHOP);
        INIT_PIECE_PLACEMENT.put(D8, BLACK_QUEEN);
        INIT_PIECE_PLACEMENT.put(E8, BLACK_KING);
        INIT_PIECE_PLACEMENT.put(F8, BLACK_BISHOP);
        INIT_PIECE_PLACEMENT.put(G8, BLACK_KNIGHT);
        INIT_PIECE_PLACEMENT.put(H8, BLACK_ROOK);
        INIT_PIECE_PLACEMENT.put(A1, WHITE_ROOK);
        INIT_PIECE_PLACEMENT.put(B1, WHITE_KNIGHT);
        INIT_PIECE_PLACEMENT.put(C1, WHITE_BISHOP);
        INIT_PIECE_PLACEMENT.put(D1, WHITE_QUEEN);
        INIT_PIECE_PLACEMENT.put(E1, WHITE_KING);
        INIT_PIECE_PLACEMENT.put(F1, WHITE_BISHOP);
        INIT_PIECE_PLACEMENT.put(G1, WHITE_KNIGHT);
        INIT_PIECE_PLACEMENT.put(H1, WHITE_ROOK);
        for (Square s : Square.values()) {
            if (s.getRank() == 6) {
                INIT_PIECE_PLACEMENT.put(s, BLACK_PAWN);
            } else if (s.getRank() == 1) {
                INIT_PIECE_PLACEMENT.put(s, WHITE_PAWN);
            }
        }
    }
}
