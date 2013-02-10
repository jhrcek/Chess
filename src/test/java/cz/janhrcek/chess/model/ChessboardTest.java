/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model;

import static cz.janhrcek.chess.model.Piece.*;
import static cz.janhrcek.chess.model.Square.*;
import cz.janhrcek.chess.rules.BitboardManager;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ExpectedExceptions;
import org.testng.annotations.Test;

/**
 *
 * @author jhrcek
 */
public class ChessboardTest {

    private Chessboard board;

    @BeforeClass
    public void init() {
        board = new Chessboard();
    }

    @Test
    public void testInitialPosition() {
        board.setInitialPosition();
        String msg = "There isn't a correct piece on the square in the initial position!";
        assertEquals(board.getPiece(A1), WHITE_ROOK, msg);
        assertEquals(board.getPiece(B1), WHITE_KNIGHT, msg);
        assertEquals(board.getPiece(C1), WHITE_BISHOP, msg);
        assertEquals(board.getPiece(D1), WHITE_QUEEN, msg);
        assertEquals(board.getPiece(E1), WHITE_KING, msg);
        assertEquals(board.getPiece(F1), WHITE_BISHOP, msg);
        assertEquals(board.getPiece(G1), WHITE_KNIGHT, msg);
        assertEquals(board.getPiece(H1), WHITE_ROOK, msg);

        assertEquals(board.getPiece(A2), WHITE_PAWN, msg);
        assertEquals(board.getPiece(B2), WHITE_PAWN, msg);
        assertEquals(board.getPiece(C2), WHITE_PAWN, msg);
        assertEquals(board.getPiece(D2), WHITE_PAWN, msg);
        assertEquals(board.getPiece(E2), WHITE_PAWN, msg);
        assertEquals(board.getPiece(F2), WHITE_PAWN, msg);
        assertEquals(board.getPiece(G2), WHITE_PAWN, msg);
        assertEquals(board.getPiece(H2), WHITE_PAWN, msg);

        assertEquals(board.getPiece(A8), BLACK_ROOK, msg);
        assertEquals(board.getPiece(B8), BLACK_KNIGHT, msg);
        assertEquals(board.getPiece(C8), BLACK_BISHOP, msg);
        assertEquals(board.getPiece(D8), BLACK_QUEEN, msg);
        assertEquals(board.getPiece(E8), BLACK_KING, msg);
        assertEquals(board.getPiece(F8), BLACK_BISHOP, msg);
        assertEquals(board.getPiece(G8), BLACK_KNIGHT, msg);
        assertEquals(board.getPiece(H8), BLACK_ROOK, msg);

        assertEquals(board.getPiece(A7), BLACK_PAWN, msg);
        assertEquals(board.getPiece(B7), BLACK_PAWN, msg);
        assertEquals(board.getPiece(C7), BLACK_PAWN, msg);
        assertEquals(board.getPiece(D7), BLACK_PAWN, msg);
        assertEquals(board.getPiece(E7), BLACK_PAWN, msg);
        assertEquals(board.getPiece(F7), BLACK_PAWN, msg);
        assertEquals(board.getPiece(G7), BLACK_PAWN, msg);
        assertEquals(board.getPiece(H7), BLACK_PAWN, msg);

        long fourRanksInMiddle =
                BitboardManager.parseString("0000000000000000111111111111111111111111111111110000000000000000");

        for (Square sq : Square.getSquares(fourRanksInMiddle)) {
            assertNull(board.getPiece(sq), "The square in the middle must be empty in the initial positions");
        }
    }

    @Test
    public void testPutAndGetPiece() {
        board.setInitialPosition();
        
        String msg = "The piece we put on the square is not the same we got from that square!";
        board.putPiece(WHITE_PAWN, A8);
        assertEquals(board.getPiece(A8), WHITE_PAWN, msg);

        board.putPiece(WHITE_KING, F6);
        assertEquals(board.getPiece(F6), WHITE_KING, msg);

        board.putPiece(WHITE_KNIGHT, D1);
        assertEquals(board.getPiece(D1), WHITE_KNIGHT, msg);

        board.putPiece(BLACK_QUEEN, G8);
        assertEquals(board.getPiece(G8), BLACK_QUEEN, msg);

        board.putPiece(BLACK_BISHOP, B3);
        assertEquals(board.getPiece(B3), BLACK_BISHOP, msg);

        board.putPiece(BLACK_ROOK, C5);
        assertEquals(board.getPiece(C5), BLACK_ROOK, msg);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testPutPieceWithNull() {
        board.putPiece(null, A8);
        board.putPiece(WHITE_PAWN, null);
        board.putPiece(null, null);
    }
    
    @Test
    public void testRemovePiece() {
        board.setInitialPosition();
        board.removePiece(A1);
        board.removePiece(B2);
        board.removePiece(C3);
        board.removePiece(D4);
        board.putPiece(WHITE_KING, H8);
        board.removePiece(H8);
        
        String empty = "There must be null on squares from which we remove piece!";
        assertNull(board.getPiece(A1),empty);
        assertNull(board.getPiece(B2),empty);
        assertNull(board.getPiece(C3),empty);
        assertNull(board.getPiece(D4),empty);
        assertNull(board.getPiece(H8),empty);        
    }
}
