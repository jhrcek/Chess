package cz.janhrcek.chess.model.api;

import cz.janhrcek.chess.model.api.enums.Piece;
import cz.janhrcek.chess.model.api.enums.Square;

/**
 * Carries information about the move in chess, i.e. the information necessary
 * for transition from one Position to another. It consists of
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
        return new StringBuilder("Move[").append(piece)
                .append(" from ").append(from)
                .append(" to ").append(to)
                .append("]").toString();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.piece != null ? this.piece.hashCode() : 0);
        hash = 29 * hash + (this.from != null ? this.from.hashCode() : 0);
        hash = 29 * hash + (this.to != null ? this.to.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Move other = (Move) obj;
        if (this.piece != other.piece) {
            return false;
        }
        if (this.from != other.from) {
            return false;
        }
        if (this.to != other.to) {
            return false;
        }
        return true;
    }
}
