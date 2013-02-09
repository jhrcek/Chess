package cz.janhrcek.chess.rules;

/**
 * Enumeration class MoveType - represents types of legality/illegality of the
 * moves, that can be made on the chessboard.
 *
 * @author Jan Hrcek
  */
public enum MoveType {

    /**
     * Legal move, that is not check, mate nor pawn promotion.
     */
    LEGAL(true),
    /**
     * Legal move, that is check.
     */
    LEGAL_CHECK(true),
    /**
     * Legal move, that gives mate.
     */
    LEGAL_MATE(true),
    /**
     * Moving a piece not according to its "empty board" movement rules.
     */
    ILLEGAL_PIECE_MOVE(false),
    /**
     * Trying to move piece of another color, than that of player to move.
     */
    ILLEGAL_WRONG_COLOR(false),
    /**
     * Moving piece cant't move to destination square because it's path is
     * blocked.
     */
    ILLEGAL_PATH_BLOCKED(false),
    /**
     * Moving piece to square occupied by friendly piece.
     */
    ILLEGAL_SQUARE_OCCUPIED(false),
    /**
     * Move after which the moving player's king is left in check.
     */
    ILLEGAL_LEAVES_KING_IN_CHECK(false),
    /**
     * Castling attempt on side on which castling is no longer available (because
     * either king or "castling-side" rook has already moved).
     */
    ILLEGAL_CASTLING_NO_LONGER_AVAILABLE(false),
    /**
     * Trying to castle king which is in check.
     */
    ILLEGAL_CASTLING_KING_IN_CHECK(false),
    /**
     * Trying to pass king through attacked square when castling.
     */
    ILLEGAL_CASTLING_THROUGH_CHECK(false),
    /**
     * Trying to make en passant capture when it is no longer available.
     */
    ILLEGAL_EN_PASSANT(false);
    /**
     * Represents information, which MoveType is legal(=true) and which is
     * illegal(=false).
     */
    private final boolean isLegal;

    /**
     * Constructor sets legality flag for MoveTypes.
     *
     * @param isLeg true if it represents legal move, false otherwise
     */
    private MoveType(final boolean isLeg) {
        this.isLegal = isLeg;
    }

    /**
     * Returns information, whether given MoveType object represents legal move
     * or illegal move.
     *
     * @return true if given constant represents legal move<br> false otherwise
     */
    public boolean isLegal() {
        return isLegal;
    }
}
