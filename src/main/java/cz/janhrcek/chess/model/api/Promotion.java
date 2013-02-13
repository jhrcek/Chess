package cz.janhrcek.chess.model.api;

import cz.janhrcek.chess.model.api.Square;
import cz.janhrcek.chess.model.api.Move;
import static cz.janhrcek.chess.model.api.Piece.*;

/**
 * Is a specific case of Move, in which pawn reaches the opponent's first rank
 * and is forced to promote itself to Queen/Rook/Bishop or Knight of the same
 * color.
 * TODO: Promotion constructor checks whether it is PAWN and it's on correct rank. Should that not be moved to rules?
 * @author jhrcek
 */
public class Promotion extends Move {

    /**
     * The string representing to which piece should pawn be promoted. this is
     * not null only for pawn-promotion moves.
     */
    private Piece promoPiece;

    /**
     * Creates instance of Move representing pawn promotion, setting the moving
     * piece, the square from which it moved, to which it moved and to what
     * piece should it be promoted. Null values are not permitted.
     *
     * @param piece the piece that moved
     * @param from the square, from which the piece moved
     * @param to the square to which the piece moved
     * @param promoPiece piece to which we want to promote that pawn (it must be
     * Queen, Rook, Bishop or Knight of the same color as the moving pawn)
     */
    public Promotion(Piece piece, Square from, Square to, Piece promoPiece) {
        super(piece, from, to);

        if (promoPiece == null) {
            throw new NullPointerException("toWhatPromote can't be null!");
        }
        //jestlize nejde o promotion pak nema smysl volani tohoto konstruktoru
        if (!((piece.equals(WHITE_PAWN) && from.getRank() == 6 && to.getRank() == 7)
                || (piece.equals(BLACK_PAWN) && from.getRank() == 1 && to.getRank() == 0))) {
            throw new IllegalArgumentException("This constructor should be"
                    + " used only when move promotes pawn");
        }

        if (promoPiece.isWhite() != piece.isWhite()) {
            throw new IllegalArgumentException("toWhatPromote must be QUEEN,"
                    + " ROOK, BISHOP or KNIGHT of the same color as the moving"
                    + " pawn but was: " + piece + "->" + promoPiece);
        }

        if (promoPiece.equals(WHITE_KING)
                || promoPiece.equals(WHITE_PAWN)
                || promoPiece.equals(BLACK_KING)
                || promoPiece.equals(BLACK_PAWN)) {
            throw new IllegalArgumentException("toWhatPromote must be QUEEN,"
                    + " ROOK, BISHOP or KNIGHT of the same color as the moving"
                    + " pawn");
        }
        this.promoPiece = promoPiece;
    }

    /**
     * If this move represents pawn promotion, then return piece to which the
     * pawn should be promoted, otherwise it returns null.
     *
     * @return the piece to which the pawn should be promoted if this moveInfo
     * represents pawn promotion, null otherwise
     */
    public final Piece getPromoPiece() {
        return promoPiece;
    }

    @Override
    public String toString() {
        /*//the convention is to represent the promotion as <destination>=N/=B/=R/=Q based on the promotionPiece
         char promotionPieceLetter =
         (promoPiece.equals(WHITE_KNIGHT) //Knights with N, to distinguish from K (stands for King)
         || promoPiece.equals(BLACK_KNIGHT))
         ? 'N' : promoPiece.toString().charAt(6); //Q, B and R = 6th letter of piece name
         return super.toString() + "=" + promotionPieceLetter;*/
        return new StringBuilder("Move[").append(getPiece())
                .append(" from ").append(getFrom())
                .append(" to ").append(getTo())
                .append(" promoting to ").append(promoPiece)
                .append("]").toString();
    }
}
