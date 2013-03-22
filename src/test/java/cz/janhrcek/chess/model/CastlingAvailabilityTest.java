package cz.janhrcek.chess.model;

import cz.janhrcek.chess.model.api.enums.Castling;
import static cz.janhrcek.chess.model.api.enums.Castling.*;
import java.util.EnumSet;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.DataProvider;
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
        assertEquals(Castling.parseFenCastlingSubstring("KQkq"), EnumSet.of(WHITE_KINGSIDE, WHITE_QUEENSIDE, BLACK_KINGSIDE, BLACK_QUEENSIDE), msg);
        assertEquals(Castling.parseFenCastlingSubstring("KQk"), EnumSet.of(WHITE_KINGSIDE, WHITE_QUEENSIDE, BLACK_KINGSIDE), msg);
        assertEquals(Castling.parseFenCastlingSubstring("KQq"), EnumSet.of(WHITE_KINGSIDE, WHITE_QUEENSIDE, BLACK_QUEENSIDE), msg);
        assertEquals(Castling.parseFenCastlingSubstring("KQ"), EnumSet.of(WHITE_KINGSIDE, WHITE_QUEENSIDE), msg);
        assertEquals(Castling.parseFenCastlingSubstring("Kkq"), EnumSet.of(WHITE_KINGSIDE, BLACK_KINGSIDE, BLACK_QUEENSIDE), msg);
        assertEquals(Castling.parseFenCastlingSubstring("Kk"), EnumSet.of(WHITE_KINGSIDE, BLACK_KINGSIDE), msg);
        assertEquals(Castling.parseFenCastlingSubstring("Kq"), EnumSet.of(WHITE_KINGSIDE, BLACK_QUEENSIDE), msg);
        assertEquals(Castling.parseFenCastlingSubstring("K"), EnumSet.of(WHITE_KINGSIDE), msg);
        assertEquals(Castling.parseFenCastlingSubstring("Qkq"), EnumSet.of(WHITE_QUEENSIDE, BLACK_KINGSIDE, BLACK_QUEENSIDE), msg);
        assertEquals(Castling.parseFenCastlingSubstring("Qk"), EnumSet.of(WHITE_QUEENSIDE, BLACK_KINGSIDE), msg);
        assertEquals(Castling.parseFenCastlingSubstring("Qq"), EnumSet.of(WHITE_QUEENSIDE, BLACK_QUEENSIDE), msg);
        assertEquals(Castling.parseFenCastlingSubstring("Q"), EnumSet.of(WHITE_QUEENSIDE), msg);
        assertEquals(Castling.parseFenCastlingSubstring("kq"), EnumSet.of(BLACK_KINGSIDE, BLACK_QUEENSIDE), msg);
        assertEquals(Castling.parseFenCastlingSubstring("k"), EnumSet.of(BLACK_KINGSIDE), msg);
        assertEquals(Castling.parseFenCastlingSubstring("q"), EnumSet.of(BLACK_QUEENSIDE), msg);
        assertEquals(Castling.parseFenCastlingSubstring("-"), EnumSet.noneOf(Castling.class), msg);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, dataProvider = "invalid-ca-fen-substrings")
    public static void testParsingInvalidFenSubstring(String caFenSubstring) {
        Castling.parseFenCastlingSubstring(caFenSubstring);
    }

    @DataProvider(name = "invalid-ca-fen-substrings")
    public Object[][] invalidCAFenSubstringProducer() {
        return new Object[][]{
                    {"QKkq"}, //characters in wrong order
                    {"kqKQ"},
                    {"Kqk"},
                    {"kK"},
                    {"KKQkq"}, //duplicate characters
                    {"KQkkq"},
                    {"aKQkq"}, //invalid characters
                    {"KbQkq"},
                    {"KQkq*"},
                    {"KQ-kq"},
                    {"_"}
                };
    }
}
