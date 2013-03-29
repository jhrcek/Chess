package cz.janhrcek.chess.model.impl;

import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.Position;
import cz.janhrcek.chess.model.api.Promotion;
import cz.janhrcek.chess.model.api.enums.Piece;
import cz.janhrcek.chess.model.api.enums.Square;
import static cz.janhrcek.chess.model.api.enums.Piece.*;
import static cz.janhrcek.chess.model.api.enums.Square.*;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author jhrcek
 */
public class PositionImpl implements Position {

    public PositionImpl() {
        piecePlacement = new EnumMap<>(INIT_PIECE_PLACEMENT);
    }

    public PositionImpl(Map<Square, Piece> piecePlacement) {
        requireNonNull(piecePlacement, "PiecePlacement map must not be null!");
        this.piecePlacement = new EnumMap<>(piecePlacement);
    }

    @Override
    public Piece getPiece(Square square) {
        requireNonNull(square, "Square must not be null!");
        return piecePlacement.get(square);
    }

    @Override
    public Position createNewPositionUsing(Move move) {
        requireNonNull(move, "Move must note be null!");
        Map<Square, Piece> newPiecePlacement = new EnumMap<>(piecePlacement); //1) Copy curent piece placement
        Square from = move.getFrom();
        if (newPiecePlacement.containsKey(from)) { //There is some piece on "from" square
            if (move instanceof Promotion) { //2) Put the piece on "to" square
                newPiecePlacement.put(move.getTo(), ((Promotion) move).getPromoPiece());
            } else {
                newPiecePlacement.put(move.getTo(), newPiecePlacement.get(from));
            }
            newPiecePlacement.remove(from); //3) Remove piece from "from" square
        } else { //There was not piece on "from" square
            throw new IllegalStateException(String.format("Request to create new"
                    + " position from \n %s \n using %s cannot be satisfied, "
                    + "because there is no piece on the from square %s!",
                    this.toString(), move, from));
        }
        return new PositionImpl(newPiecePlacement);
    }

    /**
     * Returns pseudo-graphic String representation of the chessboard.
     *
     * @return pseudo-graphic String representation of the chessboard.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        final String NEW_LINE = System.getProperty("line.separator");
        final String BETWEEN_ROWS = "  +---+---+---+---+---+---+---+---+" + NEW_LINE;
        sb.append(BETWEEN_ROWS);
        for (Square s : Square.values()) {
            if (s.getFile() == 0) { //beginning of rank
                sb.append("  |");
            }
            Piece piece = piecePlacement.get(s);
            if (piece == null) {
                sb.append("   |");
            } else {
                sb.append(" ").append(piece.getFenLetter()).append(" |");
            }
            if (s.getFile() == 7) {
                sb.append(NEW_LINE).append(BETWEEN_ROWS);
            }
        }
        sb.append("    A   B   C   D   E   F   G   H").append(NEW_LINE);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.piecePlacement);
        return hash;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        PositionImpl otherPos = (PositionImpl) other;
        if (!Objects.equals(this.piecePlacement, otherPos.piecePlacement)) {
            return false;
        }
        return true;
    }
//------------------------- PRIVATE IMPLEMENTATION -----------------------------
    private final Map<Square, Piece> piecePlacement;
    private static final Map<Square, Piece> INIT_PIECE_PLACEMENT;

    static {
        INIT_PIECE_PLACEMENT = new EnumMap<>(Square.class);
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
