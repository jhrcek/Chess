package cz.janhrcek.chess.model;

import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.Position;
import cz.janhrcek.chess.model.api.Promotion;
import cz.janhrcek.chess.model.api.enums.Piece;
import cz.janhrcek.chess.model.api.enums.Square;
import static cz.janhrcek.chess.model.api.enums.Piece.*;
import static cz.janhrcek.chess.model.api.enums.Square.*;
import cz.janhrcek.chess.model.impl.PositionImpl;
import cz.janhrcek.chess.rules.BitboardManager;
import java.util.EnumMap;
import java.util.Map;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author jhrcek
 */
public class PositionTest {

    private Position initPosition;

    @BeforeMethod
    public void setupMethod() {
        initPosition = new PositionImpl();
    }

    @Test
    public void testInitialPosition() {
        String msg = "There isn't a correct piece on the square in the initial position!";
        assertEquals(initPosition.getPiece(A1), WHITE_ROOK, msg);
        assertEquals(initPosition.getPiece(B1), WHITE_KNIGHT, msg);
        assertEquals(initPosition.getPiece(C1), WHITE_BISHOP, msg);
        assertEquals(initPosition.getPiece(D1), WHITE_QUEEN, msg);
        assertEquals(initPosition.getPiece(E1), WHITE_KING, msg);
        assertEquals(initPosition.getPiece(F1), WHITE_BISHOP, msg);
        assertEquals(initPosition.getPiece(G1), WHITE_KNIGHT, msg);
        assertEquals(initPosition.getPiece(H1), WHITE_ROOK, msg);

        assertEquals(initPosition.getPiece(A2), WHITE_PAWN, msg);
        assertEquals(initPosition.getPiece(B2), WHITE_PAWN, msg);
        assertEquals(initPosition.getPiece(C2), WHITE_PAWN, msg);
        assertEquals(initPosition.getPiece(D2), WHITE_PAWN, msg);
        assertEquals(initPosition.getPiece(E2), WHITE_PAWN, msg);
        assertEquals(initPosition.getPiece(F2), WHITE_PAWN, msg);
        assertEquals(initPosition.getPiece(G2), WHITE_PAWN, msg);
        assertEquals(initPosition.getPiece(H2), WHITE_PAWN, msg);

        assertEquals(initPosition.getPiece(A8), BLACK_ROOK, msg);
        assertEquals(initPosition.getPiece(B8), BLACK_KNIGHT, msg);
        assertEquals(initPosition.getPiece(C8), BLACK_BISHOP, msg);
        assertEquals(initPosition.getPiece(D8), BLACK_QUEEN, msg);
        assertEquals(initPosition.getPiece(E8), BLACK_KING, msg);
        assertEquals(initPosition.getPiece(F8), BLACK_BISHOP, msg);
        assertEquals(initPosition.getPiece(G8), BLACK_KNIGHT, msg);
        assertEquals(initPosition.getPiece(H8), BLACK_ROOK, msg);

        assertEquals(initPosition.getPiece(A7), BLACK_PAWN, msg);
        assertEquals(initPosition.getPiece(B7), BLACK_PAWN, msg);
        assertEquals(initPosition.getPiece(C7), BLACK_PAWN, msg);
        assertEquals(initPosition.getPiece(D7), BLACK_PAWN, msg);
        assertEquals(initPosition.getPiece(E7), BLACK_PAWN, msg);
        assertEquals(initPosition.getPiece(F7), BLACK_PAWN, msg);
        assertEquals(initPosition.getPiece(G7), BLACK_PAWN, msg);
        assertEquals(initPosition.getPiece(H7), BLACK_PAWN, msg);

        long fourRanksInMiddle =
                BitboardManager.parseString("0000000000000000111111111111111111111111111111110000000000000000");

        for (Square sq : Square.getSquares(fourRanksInMiddle)) {
            assertNull(initPosition.getPiece(sq), "The square in the middle must be empty in the initial positions");
        }
    }

    @Test
    public void testCreateNewPosition1() {
        Position newPos = initPosition.createNewPositionUsing(new Move(WHITE_PAWN, E2, E4));
        assertNull(newPos.getPiece(E2));
        assertNull(newPos.getPiece(E3));
        assertEquals(newPos.getPiece(E4), WHITE_PAWN);
        assertEquals(newPos.getPiece(A8), BLACK_ROOK);
    }

    @Test
    public void testCreateNewPosition2() {
        Position newPos = initPosition
                .createNewPositionUsing(new Move(WHITE_PAWN, E2, E4))
                .createNewPositionUsing(new Move(BLACK_PAWN, E7, E5))
                .createNewPositionUsing(new Move(WHITE_QUEEN, D1, H5))
                .createNewPositionUsing(new Move(BLACK_KNIGHT, B8, C6))
                .createNewPositionUsing(new Move(WHITE_BISHOP, F1, C4))
                .createNewPositionUsing(new Move(BLACK_KNIGHT, G8, F6))
                .createNewPositionUsing(new Move(WHITE_QUEEN, H5, F7));
        assertNull(newPos.getPiece(D1));
        assertNull(newPos.getPiece(E2));
        assertNull(newPos.getPiece(F1));
        assertNull(newPos.getPiece(B8));
        assertNull(newPos.getPiece(E7));
        assertNull(newPos.getPiece(G8));
        assertNull(newPos.getPiece(B6));
        assertNull(newPos.getPiece(F5));
        assertNull(newPos.getPiece(A4));
        assertNull(newPos.getPiece(H3));
        assertEquals(newPos.getPiece(C4), WHITE_BISHOP);
        assertEquals(newPos.getPiece(E4), WHITE_PAWN);
        assertEquals(newPos.getPiece(E5), BLACK_PAWN);
        assertEquals(newPos.getPiece(C6), BLACK_KNIGHT);
        assertEquals(newPos.getPiece(F6), BLACK_KNIGHT);
        assertEquals(newPos.getPiece(F7), WHITE_QUEEN);
        assertEquals(newPos.getPiece(A8), BLACK_ROOK);
        assertEquals(newPos.getPiece(B7), BLACK_PAWN);
        assertEquals(newPos.getPiece(F2), WHITE_PAWN);
        assertEquals(newPos.getPiece(G1), WHITE_KNIGHT);
        //Make sure the original position didn't change!
        assertNull(initPosition.getPiece(E4));
        assertNull(initPosition.getPiece(E5));
        assertNull(initPosition.getPiece(C4));
        assertEquals(initPosition.getPiece(E2), WHITE_PAWN);
        assertEquals(initPosition.getPiece(D1), WHITE_QUEEN);
        assertEquals(initPosition.getPiece(E7), BLACK_PAWN);
    }

    @Test
    public void testCreateNewPositionWithPromotion() {
        Map<Square, Piece> myPiecePlacement = new EnumMap<>(Square.class);
        myPiecePlacement.put(A7, WHITE_PAWN);
        Position beforePromotion = new PositionImpl(myPiecePlacement);
        Position afterPromotion = beforePromotion.createNewPositionUsing(new Promotion(WHITE_PAWN, A7, A8, WHITE_QUEEN));

        assertEquals(beforePromotion.getPiece(A7), WHITE_PAWN);
        assertNull(beforePromotion.getPiece(A8));
        assertEquals(afterPromotion.getPiece(A8), WHITE_QUEEN);
        assertNull(afterPromotion.getPiece(A7));
    }
}
