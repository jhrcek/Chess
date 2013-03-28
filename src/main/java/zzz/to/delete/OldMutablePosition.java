package zzz.to.delete;

import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.Promotion;
import cz.janhrcek.chess.model.api.enums.Piece;
import static cz.janhrcek.chess.model.api.enums.Piece.*;
import cz.janhrcek.chess.model.api.enums.Square;
import cz.janhrcek.chess.model.impl.PieceNotPresentException;
import static cz.janhrcek.chess.model.api.enums.Square.*;
import java.util.Arrays;

/**
 * TODO: make position immutable (only FEN string based constructor + static
 * factory method from given position and move) Represents piece placement on
 * the standard 8x8 chess board. This is basically the visible component of
 * given game state. This class offers methods to manipulate with the pieces on
 * the board arbitrarily (that is the manipulation with the pieces does not have
 * to be according to chess rules).
 *
 * @author Jan Hrcek
 */
public class OldMutablePosition {

    /**
     * Number of files on the chessboard.
     */
    private static final int NUMBER_OF_FILES = 8;
    /**
     * Number of ranks on the chessboard.
     */
    private static final int NUMBER_OF_RANKS = 8;
    /**
     * Two dimensional array containing chess pieces (instances of enum
     * Chessmen). If no piece is on particular square, then there is null stored
     * in the corresponding array slot.
     */
    private final Piece[][] board;

    /**
     * Creates new position by making given move from given position.
     *
     * @param from the position, from which we want to create new one by making
     * given move
     * @param move the move to make in the from position
     * @return the position which arises by making given move in given position
     * @throws PieceNotPresentException if the piece described in the move is not on
     * given "from" square in given position
     */
    public static OldMutablePosition createFrom(OldMutablePosition from, Move move) throws PieceNotPresentException {
        OldMutablePosition newPosition = from.copy();
        newPosition.makeMove(move);
        return newPosition;
    }

    /**
     * Creates new Instance of the chessboard.
     */
    public OldMutablePosition() {
        board = new Piece[NUMBER_OF_FILES][NUMBER_OF_RANKS];
    }

    /**
     * Sets up the starting position on the chessboard. That is, according to
     * standard FIDE rules, the following position:
     * <pre>
     *             BLACK PLAYER
     *        A  B  C  D  E  F  G  H
     *       +--+--+--+--+--+--+--+--+
     *     8 |r |n |b |q |k |b |n |r |      K = king
     *       +--+--+--+--+--+--+--+--+      Q = queen
     *     7 |p |p |p |p |p |p |p |p |      R = rook
     *       +--+--+--+--+--+--+--+--+      B = bishop
     *     6 |  |  |  |  |  |  |  |  |      N = knight
     *       +--+--+--+--+--+--+--+--+      P = pawn
     *     5 |  |  |  |  |  |  |  |  |
     *       +--+--+--+--+--+--+--+--+     uppercase letters denote
     *     4 |  |  |  |  |  |  |  |  |     white pieces, lowercase
     *       +--+--+--+--+--+--+--+--+     denote black pieces
     *     3 |  |  |  |  |  |  |  |  |
     *       +--+--+--+--+--+--+--+--+
     *     2 |P |P |P |P |P |P |P |P |
     *       +--+--+--+--+--+--+--+--+
     *     1 |R |N |B |Q |K |B |N |R |
     *       +--+--+--+--+--+--+--+--+
     *        A  B  C  D  E  F  G  H
     *             WHITE PLAYER
     * </pre>
     */
    public void setInitialPosition() {
        //put white pieces on the board
        putPiece(WHITE_ROOK, A1);  //rooks
        putPiece(WHITE_ROOK, H1);
        putPiece(WHITE_KNIGHT, B1);  //knights
        putPiece(WHITE_KNIGHT, G1);
        putPiece(WHITE_BISHOP, C1);  //bishops
        putPiece(WHITE_BISHOP, F1);
        putPiece(WHITE_QUEEN, D1);  //queen
        putPiece(WHITE_KING, E1);  //king
        putPiece(WHITE_PAWN, A2);  //pawns
        putPiece(WHITE_PAWN, B2);
        putPiece(WHITE_PAWN, C2);
        putPiece(WHITE_PAWN, D2);
        putPiece(WHITE_PAWN, E2);
        putPiece(WHITE_PAWN, F2);
        putPiece(WHITE_PAWN, G2);
        putPiece(WHITE_PAWN, H2);
        //put black pieces on the board
        putPiece(BLACK_ROOK, A8);  //rooks
        putPiece(BLACK_ROOK, H8);
        putPiece(BLACK_KNIGHT, B8);  //knights
        putPiece(BLACK_KNIGHT, G8);
        putPiece(BLACK_BISHOP, C8);  //bishops
        putPiece(BLACK_BISHOP, F8);
        putPiece(BLACK_QUEEN, D8);  //queen
        putPiece(BLACK_KING, E8);  //king
        putPiece(BLACK_PAWN, A7);  //pawns
        putPiece(BLACK_PAWN, B7);
        putPiece(BLACK_PAWN, C7);
        putPiece(BLACK_PAWN, D7);
        putPiece(BLACK_PAWN, E7);
        putPiece(BLACK_PAWN, F7);
        putPiece(BLACK_PAWN, G7);
        putPiece(BLACK_PAWN, H7);
        //remove everything from the 3rd, 4th, 5th and 6th rank
        for (int file = 0; file < NUMBER_OF_FILES; file++) {
            for (int rank = 2; rank < NUMBER_OF_RANKS - 2; rank++) { //ranks 3-6
                board[file][rank] = null;
            }
        }
    }

    /**
     * Returns chess piece (instance of enum Chessmen), which sits on particular
     * Square.
     *
     * @param square square, about which we want to know what piece sits on it.
     * @return piece sitting on the given square, or null if there is no piece
     * on given square
     */
    public Piece getPiece(final Square square) {
        if (square == null) {
            throw new NullPointerException("square can't be null!");
        }
        return board[square.getFile()][square.getRank()];
    }

    /**
     * This method puts a given piece on a given square on the chessboard.
     *
     * @param piece the piece we want to put on the chessboard
     * @param square square on the chessboard, on which we want to put the piece
     */
    public void putPiece(final Piece piece, final Square square) {
        if (piece == null || square == null) {
            throw new NullPointerException("Piece or square can not be null! piece = " + piece + ", square = " + square);
        }
        board[square.getFile()][square.getRank()] = piece;
    }

    /**
     * This method removes piece from the given square and puts it away from the
     * chessboard no matter what the piece.
     *
     * @param from the square, form which we want to remove the piece
     */
    public void removePiece(final Square from) {
        if (from == null) {
            throw new NullPointerException("from can't be null!");
        }
        board[from.getFile()][from.getRank()] = null;
    }

    /**
     * Moves the piece from one square on the chessboard to another square on
     * the same board. If there is already a piece on the destination square,
     * the new piece takes its place (captures it) and the old piece is returned
     * as a return value of the method (and removed away from the chessboard).
     * If there is no piece on the destination square, the method returns null.
     *
     * @param move object that holds information about the move we want to make
     * on th board (which pice, form where, to where)
     * @return the piece, that were formerly on the destination square, or null,
     * if there was nothing on that square
     * @throws PieceNotOnFromSquareException if the piece to move is not on the
     * square you want to move it from
     */
    public Piece makeMove(Move move) throws PieceNotPresentException {
        if (move == null) {
            throw new NullPointerException("move can't be null!");
        }

        Piece piece = move.getPiece();
        Square from = move.getFrom();
        Square to = move.getTo();

        //is piece from MoveInfo on the square from which I want to move it?
        if (!piece.equals(getPiece(from))) {
            throw new PieceNotPresentException("Trying to move the "
                    + piece + " from " + from
                    + " but the piece wasn't on that square.");
        }

        Piece capturedPiece = getPiece(to);
        removePiece(from);
        //In case it's promotion put PromoPiece on "to" square
        if (move instanceof Promotion) {
            Piece promoPiece = ((Promotion) move).getPromoPiece();
            putPiece(promoPiece, to);
        } else { //else just put there the moving piece
            putPiece(piece, to);
        }
        return capturedPiece;
    }

    /**
     * Returns pseudo-graphic String representation of the chessboard.
     *
     * @return pseudo-graphic String representation of the chessboard.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        final String BETWEEN_ROWS = "  +---+---+---+---+---+---+---+---+\n";
        sb.append(BETWEEN_ROWS);
        for (int i = 7; i >= 0; i--) {
            sb.append(getRowString(i));
            sb.append(BETWEEN_ROWS);
        }
        sb.append("    A   B   C   D   E   F   G   H\n");
        return sb.toString();
    }

    /**
     * Returns the string which pseudo-graphically represents the row of the
     * chessboard.
     *
     * @param rowIndex the index of the row
     * @return pseudo-graphical representation of the row
     */
    private String getRowString(int rowIndex) {
        StringBuilder result = new StringBuilder();
        result.append(rowIndex + 1).append(" |");
        for (int colIndex = 0; colIndex < 8; colIndex++) {
            Piece piece = board[colIndex][rowIndex];
            if (piece == null) {
                result.append("  ");
            } else {
                result.append(" ").append(piece.getFenLetter());
            }
            result.append(" |");
        }
        result.append("\n");
        return result.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof OldMutablePosition)) {
            return false;
        }

        OldMutablePosition that = (OldMutablePosition) other;
        return Arrays.deepEquals(this.board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(this.board);
    }

    private OldMutablePosition copy() {
        OldMutablePosition copy = new OldMutablePosition();
        for (Square s : Square.values()) {
            Piece p = getPiece(s);
            if (p != null) {
                copy.putPiece(p, s);
            } else {
                copy.removePiece(s);
            }
        }
        return copy;
    }
}
