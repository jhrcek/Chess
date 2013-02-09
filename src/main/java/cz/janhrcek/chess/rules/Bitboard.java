package cz.janhrcek.chess.rules;

/**
 *
 * @author jhrcek
 */
public class Bitboard {

    /**
     * Utility method for parsing string representation of bitboards into long.
     *
     * @param bitboardAsString 64-character string consisting of only 0s and 1s
     * @return long representation of the bitboard
     */
    public static long parseString(String bitboardAsString) {
        if (bitboardAsString == null || bitboardAsString.length() != 64) {
            throw new IllegalArgumentException("The input must be 64 character"
                    + " long string consisting of only 1s and 0s, but was "
                    + bitboardAsString + "(length = " + bitboardAsString.length() + ")");
        }
        return Long.parseLong(bitboardAsString, 2);
    }
}
