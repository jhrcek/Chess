/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.FEN;

import cz.janhrcek.chess.model.GameState;
import static org.testng.Assert.fail;
import org.testng.annotations.Test;

/**
 *
 * @author jhrcek
 */
public class FenParserTest {

    @Test
    public void testParsingCorrectFens() { //TODO implement testParsingCorrectFens
        try {
            GameState initState = FenParser.fenToGameState("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
            GameState afterE4 = FenParser.fenToGameState("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
            GameState afterE4E5 = FenParser.fenToGameState("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2");
            GameState afterE4E5Nf3 = FenParser.fenToGameState("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2");
        } catch (InvalidFenException ife) {
            fail("Fen parser should not have thrown an exception, but did!", ife);
        }
    }
}
