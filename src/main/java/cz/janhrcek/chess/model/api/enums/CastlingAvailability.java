/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model.api.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author jhrcek
 */
public enum CastlingAvailability {

    WHITE_KINGSIDE('K'),
    WHITE_QUEENSIDE('Q'),
    BLACK_KINGSIDE('k'),
    BLACK_QUEENSIDE('q');

    public char getFenName() {
        return fenName;
    }

    public static CastlingAvailability getCA(char fenName) {
        if ("kqKQ".indexOf(fenName) == -1) {
            throw new IllegalArgumentException("fenName must be one of characters kqKQ; Your input: " + fenName);
        }
        return fen2ca.get(fenName);
    }

    /**
     * Parses the substring of FEN string containing castling availabilities
     */
    public static EnumSet<CastlingAvailability> parseFenCaSubstring(String fenCASubstring) {
        if (!CASTLING_AVAILABILITY_PATTERN.matcher(fenCASubstring).matches()) {
            throw new IllegalArgumentException(fenCASubstring + " is not valid FEN castling availability substring!");
        }
        EnumSet<CastlingAvailability> cas = EnumSet.noneOf(CastlingAvailability.class);
        if (!"-".equals(fenCASubstring)) {
            for (char c : fenCASubstring.toCharArray()) {
                cas.add(CastlingAvailability.getCA(c));
            }
        }
        return cas;
    }

    public static String toFenCaSubstring(EnumSet<CastlingAvailability> cas) {
        StringBuilder sb = new StringBuilder();
        if (cas.isEmpty()) {
            return "-";
        } else {
            for (CastlingAvailability ca : cas) {
                sb.append(ca.getFenName());
            }
        }
        return sb.toString();

    }

    private CastlingAvailability(char fenName) {
        this.fenName = fenName;
    }
    private final char fenName;
    private static final Map<Character, CastlingAvailability> fen2ca = new HashMap<>();
    private static final Pattern CASTLING_AVAILABILITY_PATTERN = Pattern.compile("^K?Q?k?q?$|^-$");

    static {
        for (CastlingAvailability ca : CastlingAvailability.values()) {
            fen2ca.put(ca.getFenName(), ca);
        }
    }
}
