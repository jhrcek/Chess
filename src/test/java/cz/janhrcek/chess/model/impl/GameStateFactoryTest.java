package cz.janhrcek.chess.model.impl;

import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.model.api.GameState;
import cz.janhrcek.chess.model.api.GameStateFactory;
import cz.janhrcek.chess.model.api.IllegalMoveException;
import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.RuleChecker;
import cz.janhrcek.chess.model.api.enums.CastlingAvailability;
import static cz.janhrcek.chess.model.api.enums.Piece.*;
import static cz.janhrcek.chess.model.api.enums.Square.*;
import java.util.EnumSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import org.testng.annotations.Test;

/**
 *
 * @author jhrcek
 */
public class GameStateFactoryTest {

    @Test
    public void testCreatingInitialGameState() {
        RuleChecker rc = new FIDERuleChecker();
        GameStateFactory gsf = new GameStateFactoryImpl(rc);
        try {
            GameState state = gsf.create("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

            //Asserts
            String tail = " in the initial state of game";
            //Check position
            Position expected = new Position();
            expected.setInitialPosition();
            assertEquals(state.getPosition(), expected, "The position should be equal to initial position");
            assertEquals(state.getPosition().getPiece(A1), WHITE_ROOK);
            assertEquals(state.getPosition().getPiece(B2), WHITE_PAWN);
            assertEquals(state.getPosition().getPiece(D1), WHITE_QUEEN);
            assertEquals(state.getPosition().getPiece(E1), WHITE_KING);
            assertEquals(state.getPosition().getPiece(H8), BLACK_ROOK);
            assertEquals(state.getPosition().getPiece(G7), BLACK_PAWN);
            assertEquals(state.getPosition().getPiece(D8), BLACK_QUEEN);
            assertEquals(state.getPosition().getPiece(E8), BLACK_KING);

            //Check the other components of the game state
            assertTrue(state.isWhiteToMove(), "It should be white to move" + tail);
            assertEquals(state.getCastlingAvailabilities(), EnumSet.allOf(CastlingAvailability.class), "All castling availabilities should be present" + tail);
            assertNull(state.getEnPassantTargetSquare(), "En-passant target square should be null" + tail);
            assertEquals(state.getHalfmoveClock(), 0, "Half-move clock should be 0" + tail);
            assertEquals(state.getFullmoveNumber(), 1, "Full-move number should be 1" + tail);
        } catch (InvalidFenException ife) {
            fail("Exception thrown, even though FEN was ok!", ife);
        }
    }

    @Test(dependsOnMethods = "testCreatingInitialGameState")
    public void testCreateGameStateFromInitialByE4() {
        RuleChecker rc = new FIDERuleChecker();
        GameStateFactory gsf = new GameStateFactoryImpl(rc);
        try {
            GameState initialGameState = gsf.create("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
            GameState stateAfterE4 = gsf.create(initialGameState, new Move(WHITE_PAWN, E2, E4));

            //Asserts
            String tail = " after e4 played from the initial state of game";
            //Check position
            Position expected = new Position();
            expected.setInitialPosition();
            expected.makeMove(new Move(WHITE_PAWN, E2, E4));

            assertEquals(stateAfterE4.getPosition(), expected);
            assertEquals(stateAfterE4.getPosition().getPiece(E2), null);
            assertEquals(stateAfterE4.getPosition().getPiece(E4), WHITE_PAWN);

            //Check the other components of the game state
            assertFalse(stateAfterE4.isWhiteToMove(), "It should be black to move" + tail);
            assertEquals(stateAfterE4.getCastlingAvailabilities(), EnumSet.allOf(CastlingAvailability.class), "All castling availabilities should be present" + tail);
            assertEquals(stateAfterE4.getEnPassantTargetSquare(), E3, "En-passant target square should be E3" + tail);
            assertEquals(stateAfterE4.getHalfmoveClock(), 0, "Half-move clock should be 0" + tail);
            assertEquals(stateAfterE4.getFullmoveNumber(), 1, "Full-move number should be 1" + tail);
        } catch (InvalidFenException | ChessboardException | IllegalMoveException ex) {
            fail("Unexpected exception was thrown!", ex);
        }
    }
}
