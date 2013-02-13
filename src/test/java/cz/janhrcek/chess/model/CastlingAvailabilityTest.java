/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model;

import static cz.janhrcek.chess.model.CastlingAvailability.*;
import java.util.EnumSet;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

/**
 *
 * @author jhrcek
 */
public class CastlingAvailabilityTest {

    @Test
    public void testParsingCorrectFenSubstring() {

        //Test parsing all valid CA substrings:
        //"KQkq", "KQk", "KQq", "KQ"
        //"Kkq", "Kk", "Kq", "K",
        //"Qkq", "Qk", "Qq", "Q",
        //"kq", "k", "q", "-"

        String msg = "Castling availability fen substring was not parsed correctly!";
        assertEquals(CastlingAvailability.parseFenCaSubstring("KQkq"), EnumSet.of(WHITE_KINGSIDE, WHITE_QUEENSIDE, BLACK_KINGSIDE, BLACK_QUEENSIDE), msg);
        assertEquals(CastlingAvailability.parseFenCaSubstring("KQk"), EnumSet.of(WHITE_KINGSIDE, WHITE_QUEENSIDE, BLACK_KINGSIDE), msg);
        assertEquals(CastlingAvailability.parseFenCaSubstring("KQq"), EnumSet.of(WHITE_KINGSIDE, WHITE_QUEENSIDE, BLACK_QUEENSIDE), msg);
        assertEquals(CastlingAvailability.parseFenCaSubstring("KQ"), EnumSet.of(WHITE_KINGSIDE, WHITE_QUEENSIDE), msg);
        assertEquals(CastlingAvailability.parseFenCaSubstring("Kkq"), EnumSet.of(WHITE_KINGSIDE, BLACK_KINGSIDE, BLACK_QUEENSIDE), msg);
        assertEquals(CastlingAvailability.parseFenCaSubstring("Kk"), EnumSet.of(WHITE_KINGSIDE, BLACK_KINGSIDE), msg);
        assertEquals(CastlingAvailability.parseFenCaSubstring("Kq"), EnumSet.of(WHITE_KINGSIDE, BLACK_QUEENSIDE), msg);
        assertEquals(CastlingAvailability.parseFenCaSubstring("K"), EnumSet.of(WHITE_KINGSIDE), msg);
        assertEquals(CastlingAvailability.parseFenCaSubstring("Qkq"), EnumSet.of(WHITE_QUEENSIDE, BLACK_KINGSIDE, BLACK_QUEENSIDE), msg);
        assertEquals(CastlingAvailability.parseFenCaSubstring("Qk"), EnumSet.of(WHITE_QUEENSIDE, BLACK_KINGSIDE), msg);
        assertEquals(CastlingAvailability.parseFenCaSubstring("Qq"), EnumSet.of(WHITE_QUEENSIDE, BLACK_QUEENSIDE), msg);
        assertEquals(CastlingAvailability.parseFenCaSubstring("Q"), EnumSet.of(WHITE_QUEENSIDE), msg);
        assertEquals(CastlingAvailability.parseFenCaSubstring("kq"), EnumSet.of(BLACK_KINGSIDE, BLACK_QUEENSIDE), msg);
        assertEquals(CastlingAvailability.parseFenCaSubstring("k"), EnumSet.of(BLACK_KINGSIDE), msg);
        assertEquals(CastlingAvailability.parseFenCaSubstring("q"), EnumSet.of(BLACK_QUEENSIDE), msg);
        assertEquals(CastlingAvailability.parseFenCaSubstring("-"), EnumSet.noneOf(CastlingAvailability.class), msg);
    }
    
    //@Test
    // public static void //TODO implement testParsingInvalidFenSubstring() {}
}
