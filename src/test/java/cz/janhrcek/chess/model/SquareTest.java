package cz.janhrcek.chess.model;

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
        String light = "The square should be light!";
        assertTrue(Square.A8.isLight(), light);
        assertTrue(Square.C4.isLight(), light);
        assertTrue(Square.G2.isLight(), light);
        assertTrue(Square.H1.isLight(), light);
        
        String dark = "The square should be dark!";
        assertFalse(Square.A1.isLight(), dark);
        assertFalse(Square.B6.isLight(), dark);
        assertFalse(Square.G5.isLight(), dark);
        assertFalse(Square.H8.isLight(), dark);
    }
}
