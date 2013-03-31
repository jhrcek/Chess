package cz.janhrcek.chess.model;

import cz.janhrcek.chess.FEN.Fen;
import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.model.api.Position;
import cz.janhrcek.chess.model.api.PositionFactory;
import cz.janhrcek.chess.rules.IllegalMoveException;
import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.Chessboard;
import cz.janhrcek.chess.model.api.enums.Castling;
import cz.janhrcek.chess.model.impl.ChessboardImpl;
import cz.janhrcek.chess.model.impl.FIDERuleChecker;
import cz.janhrcek.chess.model.impl.PieceNotPresentException;
import cz.janhrcek.chess.model.impl.PositionFactoryImpl;
import static cz.janhrcek.chess.model.api.enums.Piece.*;
import static cz.janhrcek.chess.model.api.enums.Square.*;
import java.util.EnumSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author jhrcek
 */
public class PositionFactoryTest {

    private PositionFactory positionFactory;

    @BeforeMethod
    public void setupMethod() {
        positionFactory = new PositionFactoryImpl(new FIDERuleChecker());
    }

    @Test
    public void testCreatingInitialPosition() {
        try {
            Position position = positionFactory.create(Fen.INITIAL_POSITION);

            //Asserts
            String tail = " in the initial position";
            //Check position
            Chessboard expected = new ChessboardImpl();
            assertEquals(position.getChessboard(), expected, "The position should be equal to initial position");
            assertEquals(position.getChessboard().getPiece(A1), WHITE_ROOK);
            assertEquals(position.getChessboard().getPiece(B2), WHITE_PAWN);
            assertEquals(position.getChessboard().getPiece(D1), WHITE_QUEEN);
            assertEquals(position.getChessboard().getPiece(E1), WHITE_KING);
            assertEquals(position.getChessboard().getPiece(H8), BLACK_ROOK);
            assertEquals(position.getChessboard().getPiece(G7), BLACK_PAWN);
            assertEquals(position.getChessboard().getPiece(D8), BLACK_QUEEN);
            assertEquals(position.getChessboard().getPiece(E8), BLACK_KING);

            //Check the other components of the position
            assertTrue(position.isWhiteToMove(), "It should be white to move" + tail);
            assertEquals(position.getCastlings(), EnumSet.allOf(Castling.class), "All castling availabilities should be present" + tail);
            assertNull(position.getEnPassantTarget(), "En-passant target square should be null" + tail);
            assertEquals(position.getHalfmoveClock(), 0, "Half-move clock should be 0" + tail);
            assertEquals(position.getFullmoveNumber(), 1, "Full-move number should be 1" + tail);
        } catch (InvalidFenException ife) {
            fail("Exception thrown, even though FEN was ok!", ife);
        }
    }

    @Test
    public void testCreatePositionFromInitialByE4() {
        final Move firstMove = new Move(WHITE_PAWN, E2, E4);
        try {
            Position initialPosition = positionFactory.create(Fen.INITIAL_POSITION);
            Position positionAfterE4 = positionFactory.create(initialPosition, firstMove);

            //Check position
            Chessboard initial = new ChessboardImpl();
            Chessboard expected = initial.createNewPositionUsing(firstMove);

            //Asserts
            String msgTail = " after e4 played from the initial position";
            assertEquals(positionAfterE4.getChessboard(), expected);
            assertEquals(positionAfterE4.getChessboard().getPiece(E2), null);
            assertEquals(positionAfterE4.getChessboard().getPiece(E4), WHITE_PAWN);

            //Check the other components of the position
            assertFalse(positionAfterE4.isWhiteToMove(), "It should be black to move" + msgTail);
            assertEquals(positionAfterE4.getCastlings(), EnumSet.allOf(Castling.class), "All castling availabilities should be present" + msgTail);
            assertEquals(positionAfterE4.getEnPassantTarget(), E3, "En-passant target square should be E3" + msgTail);
            assertEquals(positionAfterE4.getHalfmoveClock(), 0, "Half-move clock should be 0" + msgTail);
            assertEquals(positionAfterE4.getFullmoveNumber(), 1, "Full-move number should be 1" + msgTail);
        } catch (InvalidFenException | PieceNotPresentException | IllegalMoveException ex) {
            fail("Unexpected exception was thrown!", ex);
        }
    }
}
