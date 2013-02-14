package cz.janhrcek.chess.model;

import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.enums.Square;
import cz.janhrcek.chess.model.api.enums.Piece;
import static cz.janhrcek.chess.model.api.enums.Piece.*;
import static cz.janhrcek.chess.model.api.enums.Square.*;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author jhrcek
 */
public class MoveTest {

    @Test
    public void testToString() {
        assertEquals(new Move(WHITE_PAWN, A2, A4).toString(), "Move[WHITE_PAWN from A2 to A4]");
        assertEquals(new Move(WHITE_BISHOP, F8, A3).toString(), "Move[WHITE_BISHOP from F8 to A3]");
        assertEquals(new Move(WHITE_KNIGHT, H1, C1).toString(), "Move[WHITE_KNIGHT from H1 to C1]");
        assertEquals(new Move(WHITE_ROOK, E5, E6).toString(), "Move[WHITE_ROOK from E5 to E6]");
        assertEquals(new Move(WHITE_QUEEN, F8, A2).toString(), "Move[WHITE_QUEEN from F8 to A2]");
        assertEquals(new Move(WHITE_KING, C3, D7).toString(), "Move[WHITE_KING from C3 to D7]");

        assertEquals(new Move(BLACK_PAWN, A2, A4).toString(), "Move[BLACK_PAWN from A2 to A4]");
        assertEquals(new Move(BLACK_KNIGHT, F3, F2).toString(), "Move[BLACK_KNIGHT from F3 to F2]");
        assertEquals(new Move(BLACK_BISHOP, C6, H7).toString(), "Move[BLACK_BISHOP from C6 to H7]");
        assertEquals(new Move(BLACK_ROOK, F3, F2).toString(), "Move[BLACK_ROOK from F3 to F2]");
        assertEquals(new Move(BLACK_QUEEN, B5, H4).toString(), "Move[BLACK_QUEEN from B5 to H4]");
        assertEquals(new Move(BLACK_KING, D1, C8).toString(), "Move[BLACK_KING from D1 to C8]");
    }

    @Test(dataProvider = "constructor-parameters", expectedExceptions = NullPointerException.class)
    public void testConstructorWithNull(Piece piece, Square from, Square to) {
        Move m = new Move(piece, from, to);
    }

    @DataProvider(name = "constructor-parameters")
    private Object[][] moveConstructorParameterProvider() {
        return new Object[][]{
                    //each of these lines will result in 1 call of testConstructorWithNull()
                    {null, A1, B2},
                    {null, null, B2},
                    {null, A1, null},
                    {null, null, null},
                    {WHITE_PAWN, null, A1},
                    {WHITE_PAWN, A2, null},
                    {WHITE_PAWN, null, null}
                };
    }
}
