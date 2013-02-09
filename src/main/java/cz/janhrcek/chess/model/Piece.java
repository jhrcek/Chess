package cz.janhrcek.chess.model;

/**
 * Represents standard chess pieces, which can be found in the usual chess set.
 * That is: white and black pawns, knights, bishops, rooks, queens and kings.
 *
 * @author Jan Hrcek
 * @version 3.3.2006
 */
public enum Piece {

    /**
     * Represents White pawn.
     */
    WHITE_PAWN(true),
    /**
     * Represents Black pawn.
     */
    BLACK_PAWN(false),
    /**
     * Represents white knight.
     */
    WHITE_KNIGHT(true),
    /**
     * Represents black knight.
     */
    BLACK_KNIGHT(false),
    /**
     * Represents white bishop.
     */
    WHITE_BISHOP(true),
    /**
     * Represents black bishop.
     */
    BLACK_BISHOP(false),
    /**
     * Represents white rook.
     */
    WHITE_ROOK(true),
    /**
     * Represents black rook.
     */
    BLACK_ROOK(false),
    /**
     * Represents white queen.
     */
    WHITE_QUEEN(true),
    /**
     * Represents blakc queen.
     */
    BLACK_QUEEN(false),
    /**
     * Represents white king.
     */
    WHITE_KING(true),
    /**
     * Represents black king.
     */
    BLACK_KING(false);
    /**
     * Flag determining, whether the piece is white or black.
     */
    private final boolean isWhite;

    /**
     * Sets isWhite flag of the chess piece.
     *
     * @param isWhite determines whether the piese is white.
     */
    private Piece(final boolean isWhite) {
        this.isWhite = isWhite;
    }

    /**
     * Indicates, whether the piece is white or black.
     *
     * @return true if the piece is white, false otherwise.
     */
    public boolean isWhite() {
        return isWhite;
    }
}
