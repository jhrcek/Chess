package cz.janhrcek.chess.rules;

import org.testng.annotations.Test;
 //TODO - implement BitboardManagerTest
/**
 *
 * @author jhrcek
 */
public class BitboardManagerTest {

    @Test
    public void testParseString() {
        BitboardManager.parseString("1111111111111111111111111111111111111111111111111111111111111111");
    }
}
