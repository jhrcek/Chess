package cz.janhrcek.chess.model.api.enums;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents standard chess pieces, which can be found in the usual chess set.
 * That is: white and black pawns, knights, bishops, rooks, queens and kings.
 *
 * @author Jan Hrcek
 */
public enum Piece implements Comparable<Piece> {

    /**
     * Represents White pawn.
     */
    WHITE_PAWN(true, 'P', "", 1), //pawns have empty names in SAN

    /**
     * Represents Black pawn.
     */
    BLACK_PAWN(false, 'p', "", 1),
    /**
     * Represents white knight.
     */
    WHITE_KNIGHT(true, 'N', "N", 2),
    /**
     * Represents black knight.
     */
    BLACK_KNIGHT(false, 'n', "N", 2),
    /**
     * Represents white bishop.
     */
    WHITE_BISHOP(true, 'B', "B", 3),
    /**
     * Represents black bishop.
     */
    BLACK_BISHOP(false, 'b', "B", 3),
    /**
     * Represents white rook.
     */
    WHITE_ROOK(true, 'R', "R", 4),
    /**
     * Represents black rook.
     */
    BLACK_ROOK(false, 'r', "R", 4),
    /**
     * Represents white queen.
     */
    WHITE_QUEEN(true, 'Q', "Q", 5),
    /**
     * Represents black queen.
     */
    BLACK_QUEEN(false, 'q', "Q", 5),
    /**
     * Represents white king.
     */
    WHITE_KING(true, 'K', "K", 6),
    /**
     * Represents black king.
     */
    BLACK_KING(false, 'k', "K", 6);
    /**
     * Flag determining, whether the piece is white or black.
     */
    private final boolean isWhite;
    /**
     * Character representing the piece letter used for denoting the piece in
     * FEN strings.
     */
    private final char fenLetter;
    /**
     * String representing the piece letter used for denoting the piece in SAN -
     * Short Algebraic Notation.
     */
    private final String sanName;
    /**
     * The purpose of this number is to define order of pieces, regardless of
     * their color.
     */
    private final int ordinal;

    /**
     * Sets isWhite flag of the chess piece.
     *
     * @param isWhite determines whether the piece is white.
     * @param fenLetter piece name in FEN - Forsythe Edwards Notation
     */
    private Piece(final boolean isWhite, char fenLetter, String sanName, int ordinal) {
        this.isWhite = isWhite;
        this.fenLetter = fenLetter;
        this.sanName = sanName;
        this.ordinal = ordinal;
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
     * @return character representing FEN name of the piece
     */
    public char getFenLetter() {
        return fenLetter;
    }

    /**
     * @return string representing SAN name of the piece
     */
    public String getSanName() {
        return sanName;
    }

    public int getOrdinal() {
        return ordinal;
    }
    //
    private static final Map<Character, Piece> fen2Piece = new HashMap<>();

    static {
        for (Piece p : Piece.values()) {
            fen2Piece.put(p.fenLetter, p);
        }
    }
    public static final Comparator<Piece> COMPARATOR = new Comparator<Piece>() {
        @Override
        public int compare(Piece o1, Piece o2) {
            return o1.ordinal - o2.ordinal;
        }
    };
}
