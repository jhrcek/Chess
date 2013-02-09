package cz.janhrcek.chess.model;

/**
 * Represents standard 8*8 chess board. Each square of the board may contain
 * either one piece or no piece. This class offers methods to manipulate with
 * the pieces on the board arbitrarily (that is the manipulation with the pieces
 * does not have to be according to chess rules).
 *
 * @author Jan Hrcek
 */
public class Chessboard {

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
     * Creates new Instance of the chessboard.
     */
    public Chessboard() {
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
        putPiece(Piece.WHITE_ROOK, Square.A1);  //rooks
        putPiece(Piece.WHITE_ROOK, Square.H1);
        putPiece(Piece.WHITE_KNIGHT, Square.B1);  //knights
        putPiece(Piece.WHITE_KNIGHT, Square.G1);
        putPiece(Piece.WHITE_BISHOP, Square.C1);  //bishops
        putPiece(Piece.WHITE_BISHOP, Square.F1);
        putPiece(Piece.WHITE_QUEEN, Square.D1);  //queen
        putPiece(Piece.WHITE_KING, Square.E1);  //king
        putPiece(Piece.WHITE_PAWN, Square.A2);  //pawns
        putPiece(Piece.WHITE_PAWN, Square.B2);
        putPiece(Piece.WHITE_PAWN, Square.C2);
        putPiece(Piece.WHITE_PAWN, Square.D2);
        putPiece(Piece.WHITE_PAWN, Square.E2);
        putPiece(Piece.WHITE_PAWN, Square.F2);
        putPiece(Piece.WHITE_PAWN, Square.G2);
        putPiece(Piece.WHITE_PAWN, Square.H2);
        //put black pieces on the board
        putPiece(Piece.BLACK_ROOK, Square.A8);  //rooks
        putPiece(Piece.BLACK_ROOK, Square.H8);
        putPiece(Piece.BLACK_KNIGHT, Square.B8);  //knights
        putPiece(Piece.BLACK_KNIGHT, Square.G8);
        putPiece(Piece.BLACK_BISHOP, Square.C8);  //bishops
        putPiece(Piece.BLACK_BISHOP, Square.F8);
        putPiece(Piece.BLACK_QUEEN, Square.D8);  //queen
        putPiece(Piece.BLACK_KING, Square.E8);  //king
        putPiece(Piece.BLACK_PAWN, Square.A7);  //pawns
        putPiece(Piece.BLACK_PAWN, Square.B7);
        putPiece(Piece.BLACK_PAWN, Square.C7);
        putPiece(Piece.BLACK_PAWN, Square.D7);
        putPiece(Piece.BLACK_PAWN, Square.E7);
        putPiece(Piece.BLACK_PAWN, Square.F7);
        putPiece(Piece.BLACK_PAWN, Square.G7);
        putPiece(Piece.BLACK_PAWN, Square.H7);
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
        if (piece == null) {
            throw new NullPointerException("piece can't be null!");
        }
        if (square == null) {
            throw new NullPointerException("square can't be null!");
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
     * the same board. If there is allready a piece on the destination square,
     * the new piece takes its place (captures it) and the old piece is returned
     * as a return value of the method (and removed away from the chessboard).
     * If there is no piece on the destination square, the method returns null.
     *
     * @param moveInfo object that holds information about the move we want to
     * make on th board (which pice, form where, to where)
     * @return the piece, that were formerly on the destination square, or null,
     * if there was nothing on that square
     * @throws ChessboardException if the piece to move is not on the square you
     * want to move it from
     */
    public Piece move(MoveInfo moveInfo)
            throws ChessboardException {
        if (moveInfo == null) {
            throw new NullPointerException("moveInfo can't be null!");
        }

        Piece piece = moveInfo.getPiece();
        Square from = moveInfo.getFrom();
        Square to = moveInfo.getTo();

        //is piece from MoveInfo on the square from which I want to move it?
        if (!piece.equals(getPiece(from))) {
            throw new ChessboardException("Trying to move the "
                    + piece + " from " + from
                    + " but the piece wasn't on that square.");
        }

        Piece capturedPiece = getPiece(to);
        removePiece(from);
        // je-li to povyseni pesaka poloz tam na co se povysuje
        if (moveInfo.getToWhatPromote() != null) {
            putPiece(moveInfo.getToWhatPromote(), to);
        } else { //jinak tam poloz ten moving piece
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
        java.lang.StringBuilder sb = new java.lang.StringBuilder();
        sb.append("  +--+--+--+--+--+--+--+--+\n");
        sb.append(getRowString(7));
        sb.append("  +--+--+--+--+--+--+--+--+\n");
        sb.append(getRowString(6));
        sb.append("  +--+--+--+--+--+--+--+--+\n");
        sb.append(getRowString(5));
        sb.append("  +--+--+--+--+--+--+--+--+\n");
        sb.append(getRowString(4));
        sb.append("  +--+--+--+--+--+--+--+--+\n");
        sb.append(getRowString(3));
        sb.append("  +--+--+--+--+--+--+--+--+\n");
        sb.append(getRowString(2));
        sb.append("  +--+--+--+--+--+--+--+--+\n");
        sb.append(getRowString(1));
        sb.append("  +--+--+--+--+--+--+--+--+\n");
        sb.append(getRowString(0));
        sb.append("  +--+--+--+--+--+--+--+--+\n");
        sb.append("   A  B  C  D  E  F  G  H\n");
        return sb.toString();
    }

    /**
     * returns the string which represents pseudo-graphically the row of the
     * chessboard.
     *
     * @param index the index of the row
     * @return pseudo-graphical representation of the row
     */
    private String getRowString(int index) {
        java.lang.StringBuilder sb = new StringBuilder();
        sb.append(index + 1);
        sb.append(" |");
        for (int i = 0; i < 8; i++) {
            if (board[i][index] == null) {
                sb.append("  ");
            } else {
                switch (board[i][index]) {
                    case WHITE_PAWN:
                        sb.append("P ");
                        break;
                    case BLACK_PAWN:
                        sb.append("p ");
                        break;
                    case WHITE_KNIGHT:
                        sb.append("N ");
                        break;
                    case BLACK_KNIGHT:
                        sb.append("n ");
                        break;
                    case WHITE_BISHOP:
                        sb.append("B ");
                        break;
                    case BLACK_BISHOP:
                        sb.append("b ");
                        break;
                    case WHITE_ROOK:
                        sb.append("R ");
                        break;
                    case BLACK_ROOK:
                        sb.append("r ");
                        break;
                    case WHITE_QUEEN:
                        sb.append("Q ");
                        break;
                    case BLACK_QUEEN:
                        sb.append("q ");
                        break;
                    case WHITE_KING:
                        sb.append("K ");
                        break;
                    case BLACK_KING:
                        sb.append("k ");
                        break;
                }
            }
            sb.append("|");
        }
        sb.append("\n");
        return sb.toString();
    }
}
