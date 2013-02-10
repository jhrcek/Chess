package cz.janhrcek.chess.model;

/**
 * Encapsulates information about move in chess. Each move is determined by
 * following information: <ul> <li>which piece moved</li> <li>from which square
 * it moved</li> <li>to which square it moved</li> <li>and (ONLY) in the case of
 * pawn promotion by the piece, to which the pawn should be promoted </li> </ul>
 * Here we don't care, whether the move represented by the instance of this
 * class is legal.
 *
 * @author Jan Hrcek
 * @version 14.3.2006
 */
public class Move {

    /**
     * The piece that moved.
     */
    private final Piece piece;
    /**
     * The square from which the piece moved.
     */
    private final Square from;
    /**
     * The square to which the piece moved.
     */
    private final Square to;
    /**
     * The string representing to which piece should pawn be promoted. this is
     * not null only for pawn-promotion moves.
     */
    private Piece toWhatPromote = null;

    /**
     * Creates instance of move, setting the piece and the square from which it
     * moved and to which it moved. Null values are not permitted.
     *
     * @param p the piece that moved
     * @param f the square, from which the piece moved
     * @param t the square to which the piece moved
     */
    public Move(final Piece p, final Square f, final Square t) {
        if (p == null) {
            throw new NullPointerException("piece can't be null!");
        }
        if (f == null) {
            throw new NullPointerException("from can't be null!");
        }
        if (t == null) {
            throw new NullPointerException("to can't be null!");
        }
        this.piece = p;
        this.from = f;
        this.to = t;
    }

    /**
     * Creates instance of moveInfo representing pawn promotion, setting the
     * moving piece, the square from which it moved, to which it moved and to
     * what piece should it be promoted. Null values are not permitted.
     *
     * @param p the piece that moved
     * @param f the square, from which the piece moved
     * @param t the square to which the piece moved
     * @param toWhatPromote piece to which we want to promote that pawn (it must
     * be Queen, Rook, Bishop or Knight of the same color as the moving pawn)
     */
    public Move(Piece p, Square f, Square t, Piece toWhatPromote) {
        this(p, f, t);

        if (toWhatPromote == null) {
            throw new NullPointerException("toWhatPromote can't be null!");
        }
        //jestlize nejde o promotion pak nema smysl volani tohoto konstruktoru
        if (!((p.equals(Piece.WHITE_PAWN) && f.getRank() == 6 && t.getRank() == 7)
                || (p.equals(Piece.BLACK_PAWN) && f.getRank() == 1 && t.getRank() == 0))) {
            throw new IllegalArgumentException("This constructor should be"
                    + " used only when move promotes pawn");
        }

        if (toWhatPromote.isWhite() != p.isWhite()) {
            throw new IllegalArgumentException("toWhatPromote must be QUEEN,"
                    + " ROOK, BISHOP or KNIGHT of the same color as the moving"
                    + " pawn");
        }

        if (toWhatPromote.equals(Piece.WHITE_KING)
                || toWhatPromote.equals(Piece.WHITE_PAWN)
                || toWhatPromote.equals(Piece.BLACK_KING)
                || toWhatPromote.equals(Piece.BLACK_PAWN)) {
            throw new IllegalArgumentException("toWhatPromote must be QUEEN,"
                    + " ROOK, BISHOP or KNIGHT of the same color as the moving"
                    + " pawn");
        }
        this.toWhatPromote = toWhatPromote;
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
     * If this move represents pawn promotion, then return piece to which the
     * pawn should be promoted, otherwise it returns null.
     *
     * @return the piece to which the pawn should be promoted if this moveInfo
     * represents pawn promotion, null otherwise
     */
    public final Piece getToWhatPromote() {
        return toWhatPromote;
    }

    /**
     * Returns String representation of the move
     *
     * @return string representing SAN (Short Algebraic Notation) shortcut for
     * the move
     *
     */
    @Override
    public final String toString() {
        StringBuilder resultStr = new StringBuilder();

        switch (piece) {
            case WHITE_KING:
            case BLACK_KING:
                resultStr.append("K");
                //rosada ma specialni znaceni
                if ((from.equals(Square.E1) && to.equals(Square.G1))
                        || (from.equals(Square.E8) && to.equals(Square.G8))) {
                    resultStr = new StringBuilder("O-O");
                } else if ((from.equals(Square.E1) && to.equals(Square.C1))
                        || (from.equals(Square.E8) && to.equals(Square.C8))) {
                    resultStr = new StringBuilder("O-O-O");
                }
                break;
            case WHITE_QUEEN:
            case BLACK_QUEEN:
                resultStr.append("Q");
                break;
            case WHITE_ROOK:
            case BLACK_ROOK:
                resultStr.append("R");
                break;
            case WHITE_KNIGHT:
            case BLACK_KNIGHT:
                resultStr.append("N");
                break;
            case WHITE_BISHOP:
            case BLACK_BISHOP:
                resultStr.append("B");
                break;
            case WHITE_PAWN:
            case BLACK_PAWN:
                //brani pesakem ma predponu "<from-file-letter>x"
                if (from.getFile() != to.getFile()) {
                    resultStr.append(from.toString().toLowerCase().charAt(0));
                    resultStr.append("x");
                }
                break;
            default:
                break;
        }

        //pokud to neni rosada appendni "to" square
        if (!resultStr.toString().startsWith("O")) {
            resultStr.append(to.toString().toLowerCase());
        }

        //pokud to je povyseni pesaka pridej "=<first-letter-of-promo-piece>"
        if (toWhatPromote != null) {
            resultStr.append("=");
            resultStr.append(toWhatPromote.toString().charAt(6));
        }

        return resultStr.toString();
    }
}
