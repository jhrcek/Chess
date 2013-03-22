package cz.janhrcek.chess.model.api.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Represents the right to castle.
 * @author jhrcek
 */
public enum Castling {

    WHITE_KINGSIDE('K'),
    WHITE_QUEENSIDE('Q'),
    BLACK_KINGSIDE('k'),
    BLACK_QUEENSIDE('q');

    public char getFenName() {
        return fenName;
    }

    public static Castling getCastling(char fenName) {
        if ("kqKQ".indexOf(fenName) == -1) {
            throw new IllegalArgumentException("fenName must be one of characters kqKQ; Your input: " + fenName);
        }
        return fen2ca.get(fenName);
    }

    /**
     * Parses the substring of FEN string containing castling availabilities
     */
    public static EnumSet<Castling> parseFenCastlingSubstring(String fenCASubstring) {
        if (!CASTLING_AVAILABILITY_PATTERN.matcher(fenCASubstring).matches()) {
            throw new IllegalArgumentException(fenCASubstring + " is not valid FEN castling availability substring!");
        }
        EnumSet<Castling> cas = EnumSet.noneOf(Castling.class);
        if (!"-".equals(fenCASubstring)) {
            for (char c : fenCASubstring.toCharArray()) {
                cas.add(Castling.getCastling(c));
            }
        }
        return cas;
    }

    public static String toFenCastlingSubstring(EnumSet<Castling> cas) {
        StringBuilder sb = new StringBuilder();
        if (cas.isEmpty()) {
            return "-";
        } else {
            for (Castling ca : cas) {
                sb.append(ca.getFenName());
            }
        }
        return sb.toString();

    }

    private Castling(char fenName) {
        this.fenName = fenName;
    }
    private final char fenName;
    private static final Map<Character, Castling> fen2ca = new HashMap<>();
    private static final Pattern CASTLING_AVAILABILITY_PATTERN = Pattern.compile("^K?Q?k?q?$|^-$");

    static {
        for (Castling ca : Castling.values()) {
            fen2ca.put(ca.getFenName(), ca);
        }
    }
}
