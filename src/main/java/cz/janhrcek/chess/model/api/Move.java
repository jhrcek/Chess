package cz.janhrcek.chess.model.api;

import cz.janhrcek.chess.model.api.enums.Piece;
import cz.janhrcek.chess.model.api.enums.Square;

/**
 * Carries information about the move in chess, i.e. the information necessary
 * for transition from one state of the game to another. It consists of
 * following items: <ul> <li>which piece moved</li> <li>from which square it
 * moved</li> <li>to which square it moved</li> </ul> There is also a special
 * kind of move called {@link Promotion} that also carries information about the
 * piece, to which the pawn should be promoted. This class serves only as a
 * holder of move information and doesn't care whether the move represented by
 * the instance of this class is legal. Move objects are Immutable.
 *
 * @author Jan Hrcek
 */
public class Move {

    /**
     * The piece that moves.
     */
    private final Piece piece;
    /**
     * The square from which the piece moves.
     */
    private final Square from;
    /**
     * The square to which the piece moves.
     */
    private final Square to;

    /**
     * Creates instance of move, setting the piece and the square from which it
     * moved and to which it moved. Null values are not permitted.
     *
     * @param piece the piece that moved
     * @param from the square, from which the piece moved
     * @param to the square to which the piece moved
     */
    public Move(final Piece piece, final Square from, final Square to) {
        if (piece == null || from == null || to == null) {
            throw new NullPointerException("None of the arguments can be null!");
        }
        this.piece = piece;
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the piece that moved.
     *
     * @return the piece that moved
     */
    public final Piece getPiece() {
        return piece;
    }

    /**
     * Returns the square the piece was on prior to this move.
     *
     * @return the square, from which the piece moved
     */
    public final Square getFrom() {
        return from;
    }

    /**
     * Returns the square to which the piece moved.
     *
     * @return the square to which the piece moved
     */
    public final Square getTo() {
        return to;
    }

    /**
     * Returns String representation of the move
     *
     * @return string representing SAN (Short Algebraic Notation) shortcut for
     * the move
     *
     */
    @Override
    public String toString() {
        /*
         //Castling is special case
         if (piece.equals(WHITE_KING) || piece.equals(BLACK_KING)) {
         if ((from.equals(E1) && to.equals(G1))
         || (from.equals(E8) && to.equals(G8))) {
         return "O-O";
         } else if ((from.equals(E1) && to.equals(C1))
         || (from.equals(E8) && to.equals(C8))) {
         return "O-O-O";
         }
         }

         StringBuilder result = new StringBuilder();
         //the other cases just have piece name  ..

         result.append(piece.getSanName());
         //when capturing with the pawn use convention: "<from-file-letter>x"
         if ((piece.equals(WHITE_PAWN) || piece.equals(BLACK_PAWN)) && from.getFile() != to.getFile()) {
         result.append(from.toString().toLowerCase().charAt(0));
         result.append("x");
         }

         //..to which we append the name of the destination square
         result.append(to.toString().toLowerCase());
         return result.toString(); */
        return new StringBuilder("Move[").append(piece)
                .append(" from ").append(from)
                .append(" to ").append(to)
                .append("]").toString();
    }
}
