package cz.janhrcek.chess.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents standard chess pieces, which can be found in the usual chess set.
 * That is: white and black pawns, knights, bishops, rooks, queens and kings.
 *
 * @author Jan Hrcek
 */
public enum Piece {

    /**
     * Represents White pawn.
     */
    WHITE_PAWN(true, 'P'), //pawns have empty names in SAN

    /**
     * Represents Black pawn.
     */
    BLACK_PAWN(false, 'p'),
    /**
     * Represents white knight.
     */
    WHITE_KNIGHT(true, 'N'),
    /**
     * Represents black knight.
     */
    BLACK_KNIGHT(false, 'n'),
    /**
     * Represents white bishop.
     */
    WHITE_BISHOP(true, 'B'),
    /**
     * Represents black bishop.
     */
    BLACK_BISHOP(false, 'b'),
    /**
     * Represents white rook.
     */
    WHITE_ROOK(true, 'R'),
    /**
     * Represents black rook.
     */
    BLACK_ROOK(false, 'r'),
    /**
     * Represents white queen.
     */
    WHITE_QUEEN(true, 'Q'),
    /**
     * Represents black queen.
     */
    BLACK_QUEEN(false, 'q'),
    /**
     * Represents white king.
     */
    WHITE_KING(true, 'K'),
    /**
     * Represents black king.
     */
    BLACK_KING(false, 'k');
    /**
     * Flag determining, whether the piece is white or black.
     */
    private final boolean isWhite;
    /**
     * String representing the piece letter used for denoting the piece in SAN -
     * Short Algebraic Notation
     */
    private final char fenLetter;

    /**
     * Sets isWhite flag of the chess piece.
     *
     * @param isWhite determines whether the piece is white.
     * @param fenLetter piece name in FEN - Forsythe Edwards Notation
     */
    private Piece(final boolean isWhite, char fenLetter) {
        this.isWhite = isWhite;
        this.fenLetter = fenLetter;
    }

    /**
     * Indicates, whether the piece is white or black.
     *
     * @return true if the piece is white, false otherwise.
     */
    public boolean isWhite() {
        return isWhite;
    }

    /**
     * Returns a piece whose fenName is given name.
     *
     * @param fenLetter the FEN name of the piece (one of: pbnrqkPBNRQK)
     * @return the piece whose FEN name is given by the argument
     */
    public static Piece getPiece(char fenLetter) {
        if ("pbnrqkPBNRQK".indexOf(fenLetter) == -1) {
            throw new IllegalArgumentException("fenName must be non-null string "
                    + "that has length 1 and contains exactly 1 of the following "
                    + "characters: pbnrqkPBNRQK; Your input: " + fenLetter);
        }
        return fen2Piece.get(fenLetter);
    }

    /**
     * @return string representing FEN name of the piece
     */
    public char getFenLetter() {
        return fenLetter;
    }
    private static final Map<Character, Piece> fen2Piece = new HashMap<>();

    static {
        for (Piece p : Piece.values()) {
            fen2Piece.put(p.fenLetter, p);
        }
    }
}
