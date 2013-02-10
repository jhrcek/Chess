/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jhrcek
 */
public enum CastlingAvailability {

    WHITE_KINGSIDE("K"),
    WHITE_QUEENSIDE("Q"),
    BLACK_KINGSIDE("k"),
    BLACK_QUEENSIDE("q");

    public String getFenName() {
        return fenName;
    }

    public CastlingAvailability getCA(String fenName) {
        if (fenName == null || fenName.length() != 1 || !"kqKQ".contains(fenName)) {
            throw new IllegalArgumentException("fenName must be non-null string "
                    + "that has length 1 and contains exactly 1 of the following "
                    + "characters: kqKQ; Your input: " + fenName);
        }
        return fen2ca.get(fenName);
    }

    private CastlingAvailability(String fenName) {
        this.fenName = fenName;
    }
    private final String fenName;
    private static final Map<String, CastlingAvailability> fen2ca = new HashMap<>();

    static {
        for (CastlingAvailability ca : CastlingAvailability.values()) {
            fen2ca.put(ca.getFenName(), ca);
        }
    }
}
