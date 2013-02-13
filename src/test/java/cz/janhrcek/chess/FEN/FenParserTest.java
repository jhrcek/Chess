/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.FEN;

import cz.janhrcek.chess.model.impl.GameStateImpl;
import static org.testng.Assert.fail;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author jhrcek
 */
public class FenParserTest {

    private static FenParser parser;

    @BeforeClass
    public void setUp() {
        parser = new FenParser();
    }

    @Test
    public void testParsingCorrectFens() { //TODO implement testParsingCorrectFens
        String[] fens = new String[]{
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
            "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
            "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2",
            "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2",
            "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b Kkq - 1 2",
            "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w kq - 10 20",
            "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b - - 1 2",
            "8/8/7p/3KNN1k/2p4p/8/3P2p1/8 w - - 0 1",
            "6R1/P2k4/r7/5N1P/r7/p7/7K/8 w - - 0 1",
            "5B2/6P1/1p6/8/1N6/kP6/2K5/8 w - - 0 1",
            "7K/8/k1P5/7p/8/8/8/8 w - - 0 1",};
        try {
            for (String s : fens) {
                GameStateImpl gs = parser.parse(s);
                System.out.println(parser.stateToFen(gs));
            }
        } catch (InvalidFenException ife) {
            fail("Fen parser should not have thrown an exception, but did!", ife);
        }
    }

    @Test(dataProvider = "invalid-fens", expectedExceptions = InvalidFenException.class)
    public void testParsingInvalidFens(String fen) throws InvalidFenException {
        parser.parse(fen);
    }

    @DataProvider(name = "invalid-fens")
    public Object[][] invalidFenProvider() {
        return new Object[][]{
                    {"3 rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"}, //with more than 6 fields
                    {"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq 0 1"}, //with less than 6 fields
                    {"rnbqkbnr/pppppppp/8/8/8/8PPPPPPPP/RNBQKBNR w KQkq 0 1"}, //with less than 8 ranks in piece placement substring
                    {"rnbqkbnr/pppppppp/8/8/8/8PPPPP//PPP/RNBQKBNR w KQkq 0 1"}, //with more than 8 ranks in piece placement substring
                    {"anbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq 0 1"}, //with incorrect chars in piece placement substring
                    {"nbqkbnr/pppppppp/8/8/8/8PPPPP//PPP/RNBQKBNR w KQkq 0 1"}, //with wrong row-sum in piece placement substring
                    {"nbqkbnr/pppppppp/8/8/8/8PPPPP//PPP/RN1QK2NR w KQkq 0 1"}, //with wrong row-sum in piece placement substring
                    {"nbqkbnr/pppppppp/8/8/8/8PPPPP//PPP/RN1QK2NR w KQkq 0 1"}, //with wrong row-sum in piece placement substring
                    {"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR j KQkq - 0 1"},//with incorrect player to move 
                    {"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR white KQkq 0 1"},//with incorrect player to move field
                    {"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR white KQkq 0 1"},//with incorrect player to move field
                    {"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b QKkq 0 1"},//with incorrect castling availability field
                    {"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b kqKQ 0 1"},//with incorrect castling availability field
                    {"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b sQKkq 0 1"},//with incorrect castling availability field
                    {"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b king 0 1"},//with incorrect castling availability field
                    {"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b 0 0 1"},//with incorrect castling availability field
                    {"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR KQk ten 1"},//with incorrect castling halfmove clock
                    {"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR KQk 5 3.5"}//with incorrect castling fullmove number                            
                };
    }
}
