/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model;

import static cz.janhrcek.chess.model.Piece.*;
import static cz.janhrcek.chess.model.Square.*;
import cz.janhrcek.chess.rules.Bitboard;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import org.testng.annotations.BeforeClass;
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
                Bitboard.parseString("0000000000000000111111111111111111111111111111110000000000000000");
        
        for (Square sq : Square.getSquares(fourRanksInMiddle)) {
            assertNull(board.getPiece(sq), "The square in the middle must be empty in the initial positions");
        }
    }
}
