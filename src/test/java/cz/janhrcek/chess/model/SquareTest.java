package cz.janhrcek.chess.model;

import cz.janhrcek.chess.model.api.enums.Square;
import cz.janhrcek.chess.rules.BitboardManager;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test;

/**
 *
 * @author jhrcek
 */
public class SquareTest {

    @Test
    public void testIsLight() {
        String light = "The square should be light: ";
        long lightSquares =
                BitboardManager.parseString("1010101001010101101010100101010110101010010101011010101001010101");

        for (Square s : Square.getSquares(lightSquares)) {
            assertTrue(s.isLight(), light + s);
        }

        String dark = "The square should be dark: ";
        long darkSquares =
                BitboardManager.parseString("0101010110101010010101011010101001010101101010100101010110101010");

        for (Square s : Square.getSquares(darkSquares)) {
            assertFalse(s.isLight(), dark + s);
        }

    }
}
