package cz.janhrcek.chess.model;

import static cz.janhrcek.chess.model.Piece.*;
import static cz.janhrcek.chess.model.Square.*;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author jhrcek
 */
public class PromotionTest {

    @Test
    public void testToString() {
        assertEquals(new Promotion(WHITE_PAWN, A7, A8, WHITE_QUEEN).toString(),
                "Move[WHITE_PAWN from A7 to A8 promoting to WHITE_QUEEN]");
        assertEquals(new Promotion(BLACK_PAWN, B2, C1, BLACK_KNIGHT).toString(),
                "Move[BLACK_PAWN from B2 to C1 promoting to BLACK_KNIGHT]");
    }

    @Test
    public void testGetPromoPiece() {
        String msg = "The promotion piece is not the same as the one the Promotion object was created with!";
        assertEquals(new Promotion(WHITE_PAWN, D7, D8, WHITE_QUEEN).getPromoPiece(), WHITE_QUEEN, msg);
        assertEquals(new Promotion(WHITE_PAWN, F7, G8, WHITE_KNIGHT).getPromoPiece(), WHITE_KNIGHT, msg);
        assertEquals(new Promotion(BLACK_PAWN, A2, A1, BLACK_ROOK).getPromoPiece(), BLACK_ROOK, msg);
        assertEquals(new Promotion(BLACK_PAWN, H2, G1, BLACK_BISHOP).getPromoPiece(), BLACK_BISHOP, msg);
    }

    @Test(dataProvider = "constructor-args", expectedExceptions = IllegalArgumentException.class)
    public void testConstructorIllegalArgs(Piece p, Square from, Square to, Piece promo) {
        Promotion pr = new Promotion(p, from, to, promo);
    }

    @DataProvider(name = "constructor-args")
    public Object[][] constructorArgsProvider() {
        return new Object[][]{
                    {WHITE_KING, A7, A8, WHITE_QUEEN},//Wrong piece to promote
                    {WHITE_QUEEN, A7, A8, WHITE_QUEEN},
                    {WHITE_BISHOP, A7, A8, WHITE_QUEEN},
                    {WHITE_ROOK, A7, A8, WHITE_QUEEN},
                    {WHITE_KNIGHT, A7, A8, WHITE_QUEEN},
                    {BLACK_KING, A7, A8, BLACK_KING},
                    {BLACK_QUEEN, A7, A8, BLACK_QUEEN},
                    {BLACK_BISHOP, A7, A8, BLACK_QUEEN},
                    {BLACK_ROOK, A7, A8, BLACK_QUEEN},
                    {BLACK_KNIGHT, A7, A8, BLACK_QUEEN},
                    {WHITE_PAWN, A7, A8, BLACK_QUEEN}, //Wrong color to promote to
                    {WHITE_PAWN, A7, A8, BLACK_ROOK},
                    {WHITE_PAWN, A7, A8, BLACK_BISHOP},
                    {WHITE_PAWN, A7, A8, BLACK_KNIGHT},
                    {WHITE_PAWN, A7, A8, BLACK_PAWN},
                    {BLACK_PAWN, A7, A8, WHITE_QUEEN},
                    {BLACK_PAWN, A7, A8, WHITE_ROOK},
                    {BLACK_PAWN, A7, A8, WHITE_BISHOP},
                    {BLACK_PAWN, A7, A8, WHITE_KNIGHT},
                    {BLACK_PAWN, A7, A8, WHITE_PAWN},
                    {BLACK_PAWN, A1, A8, BLACK_QUEEN}, //Wrong rank to move from
                    {BLACK_PAWN, A3, A8, BLACK_ROOK},
                    {BLACK_PAWN, A4, A8, BLACK_BISHOP},
                    {BLACK_PAWN, A5, A8, BLACK_KNIGHT},
                    {BLACK_PAWN, A6, A1, BLACK_QUEEN},
                    {BLACK_PAWN, A7, A8, BLACK_PAWN},
                    {WHITE_PAWN, A1, A8, WHITE_QUEEN},
                    {WHITE_PAWN, A2, A8, WHITE_ROOK},
                    {WHITE_PAWN, A3, A8, WHITE_BISHOP},
                    {WHITE_PAWN, A4, A8, WHITE_KNIGHT},
                    {WHITE_PAWN, A5, A8, WHITE_PAWN},
                    {WHITE_PAWN, A6, A8, WHITE_PAWN},
                    {WHITE_PAWN, A8, A8, WHITE_PAWN},
                };
    }
}
