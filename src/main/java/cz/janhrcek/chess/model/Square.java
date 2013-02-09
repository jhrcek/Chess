package cz.janhrcek.chess.model;

/**
 * Represents NAMES of the squares on the standard 8*8 chessboard. Names are
 * given in the standard algebraic notation: <ul> <li>The first character of a
 * square identifier is the file of the square; a file is a column of eight
 * squares designated by a single lower case letter from "a" (leftmost on
 * queenside) up to and including "h" (rightmost on kingside).</li> <li>The
 * second character of a square identifier is the rank of the square; a rank is
 * a row of eight squares designated by a single digit from "1" (bottom side
 * [White's first rank]) up to and including "8" (top side [Black's first
 * rank]).</li></ul>
 * <pre>
 *             BLACK PLAYER
 *        A  B  C  D  E  F  G  H
 *       +--+--+--+--+--+--+--+--+
 *     8 |A8|B8|C8|D8|E8|F8|G8|H8| 8      ------>
 *       +--+--+--+--+--+--+--+--+        ranks = horizontal rows of squares
 *     7 |A7|B7|C7|D7|E7|F7|G7|H7| 7             (ranks 1-8)
 *       +--+--+--+--+--+--+--+--+
 *     6 |A6|B6|C6|D6|E6|F6|G6|H6| 6       |
 *       +--+--+--+--+--+--+--+--+         |
 *     5 |A5|B5|C5|D5|E5|F5|G5|H5| 5       |
 *       +--+--+--+--+--+--+--+--+         \/
 *     4 |A4|B4|C4|D4|E4|F4|G4|H4| 4      files = vertical columns of squares
 *       +--+--+--+--+--+--+--+--+               (files A-H)
 *     3 |A3|B3|C3|D3|E3|F3|G3|H3| 3
 *       +--+--+--+--+--+--+--+--+
 *     2 |A2|B2|C2|D2|E2|F2|G2|H2| 2
 *       +--+--+--+--+--+--+--+--+
 *     1 |A1|B1|C1|D1|E1|F1|G1|H1| 1
 *       +--+--+--+--+--+--+--+--+
 *        A  B  C  D  E  F  G  H
 *              WHITE PLAYER
 * </pre> For convenient manipulation each square is given two methods int
 * getFile() and int getRank(). These methods return indices of file and rank
 * respectively, on which the square lies.
 *
 * @author Jan Hr�ek
 * @version 3.3.2006
 */
public enum Square {

    /**
     * Names of the squares on the chessboard. Names are given in the standard
     * algebraic notation. They are declared in order as they appear on the
     * chessboard, from the poin of wiew of white player left to right - line
     * after line from top to bottom (to provide intuitive way of iterating ove
     * the values with for cycle).
     */
    A8(0, 7), B8(1, 7), C8(2, 7), D8(3, 7), E8(4, 7), F8(5, 7), G8(6, 7), H8(7, 7),
    A7(0, 6), B7(1, 6), C7(2, 6), D7(3, 6), E7(4, 6), F7(5, 6), G7(6, 6), H7(7, 6),
    A6(0, 5), B6(1, 5), C6(2, 5), D6(3, 5), E6(4, 5), F6(5, 5), G6(6, 5), H6(7, 5),
    A5(0, 4), B5(1, 4), C5(2, 4), D5(3, 4), E5(4, 4), F5(5, 4), G5(6, 4), H5(7, 4),
    A4(0, 3), B4(1, 3), C4(2, 3), D4(3, 3), E4(4, 3), F4(5, 3), G4(6, 3), H4(7, 3),
    A3(0, 2), B3(1, 2), C3(2, 2), D3(3, 2), E3(4, 2), F3(5, 2), G3(6, 2), H3(7, 2),
    A2(0, 1), B2(1, 1), C2(2, 1), D2(3, 1), E2(4, 1), F2(5, 1), G2(6, 1), H2(7, 1),
    A1(0, 0), B1(1, 0), C1(2, 0), D1(3, 0), E1(4, 0), F1(5, 0), G1(6, 0), H1(7, 0);
    /**
     * Number representing index of the file, on which this square lies. Index
     * is number between 0-7 (file A has index 0 ... H has 7).
     */
    private final int file;
    /**
     * Number representing index of the rank, on which this square lies. Index
     * is number between 0-7 (rank 1 has index 0 ... 8 has 7).
     */
    private final int rank;
    /**
     * Auxiliary 2d array for easy indexing of squares in a way, that
     * corresponds their position on the board. That is s = SQUARES[i][j] <=>
     * s.getFile() == i && s.getRank() == j.
     */
    private static final Square[][] SQUARES = {
        {A1, A2, A3, A4, A5, A6, A7, A8},
        {B1, B2, B3, B4, B5, B6, B7, B8},
        {C1, C2, C3, C4, C5, C6, C7, C8},
        {D1, D2, D3, D4, D5, D6, D7, D8},
        {E1, E2, E3, E4, E5, E6, E7, E8},
        {F1, F2, F3, F4, F5, F6, F7, F8},
        {G1, G2, G3, G4, G5, G6, G7, G8},
        {H1, H2, H3, H4, H5, H6, H7, H8},};

    /**
     * Sets file and rank indices of the square.
     *
     * @param file index of the file on which this square lies.
     * @param rank index of the rank on which this square lies.
     */
    private Square(int file, int rank) {
        this.file = file;
        this.rank = rank;
    }

    /**
     * Returns index of the file on the chessboard, on which this square lies
     * (number in the range 0-7). For example for A1 this method returns 0, for
     * C5 returns 2 and for H1 returns 7.
     *
     * @return index of the file on which this square lies.
     */
    public int getFile() {
        return file;
    }

    /**
     * Returns index of the rank on the chessboard, on which this square lies
     * (number in the range 0-7). For example for A1 this method returns 0, for
     * C5 returns 4 and for H1 returns 0.
     *
     * @return index of the rank on which this square lies.
     */
    public int getRank() {
        return rank;
    }

    /**
     * Returns square with given file and rank indices.
     *
     * @param fileIdx the index of the file (between 0-7)
     * @param rankIdx the index of the rank (between 0-7)
     * @return the square with given file and rank index
     */
    public static Square getSquare(int fileIdx, int rankIdx) {
        if (fileIdx < 0 || fileIdx > 7 || rankIdx < 0 || rankIdx > 7) {
            throw new IllegalArgumentException("file and rank indices must"
                    + " be between 0 and 7");
        }
        return SQUARES[fileIdx][rankIdx];
    }

    /**
     *
     * @return true, if color of this square on standard chessboard is light,
     * false otherwise
     */
    public boolean isLight() {
        return ((rank + file) & 1) == 1;
    }

    /**
     * Return array of squares which correspond to 1's in given bitboard. For
     * example for long with bits 1010000000....00 this method returns array
     * [A8, C8], for 111...111 return [A8, B8,... , G1, H1] and for 000...0000
     * returns [].
     *
     * @param bitboard long, whose bits represent squares that should be
     * contained in the resulting array (1 - means corresponding square should
     * be returned, 0 -means corresponding square should NOT be returned)
     * @return the array containing squares selected in bitboard
     */
    public static Square[] getSquares(final long bitboard) {
        int numOfOnes = 0; //kolik poli bude ve vyberu?
        int citac = 0;
        for (int i = 0; i < 64; i++) {
            if (((1L << i) & bitboard) != 0L) {
                numOfOnes++;
            }
        }

        Square[] result = new Square[numOfOnes];
        for (Square s : Square.values()) {
            if (((1L << (8 * s.getRank() + (7 - s.getFile()))) & bitboard) != 0L) {
                result[citac++] = s;
            }
        }
        return result;
    }
}
