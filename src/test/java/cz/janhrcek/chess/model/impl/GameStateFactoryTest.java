package cz.janhrcek.chess.model.impl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import cz.janhrcek.chess.FEN.FenParser;
import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.guice.MyModule;
import cz.janhrcek.chess.model.api.GameState;
import cz.janhrcek.chess.model.api.GameStateFactory;
import cz.janhrcek.chess.rules.IllegalMoveException;
import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.Position;
import cz.janhrcek.chess.model.api.enums.Castling;
import static cz.janhrcek.chess.model.api.enums.Piece.*;
import static cz.janhrcek.chess.model.api.enums.Square.*;
import java.util.EnumSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author jhrcek
 */
public class GameStateFactoryTest {

    private Injector injector;
    private GameStateFactory gameStateFactory;

    @BeforeClass
    public void setupClass() {
        injector = Guice.createInjector(new MyModule());
    }

    @BeforeMethod
    public void setupMethod() {
        gameStateFactory = injector.getInstance(GameStateFactory.class);
    }

    @Test
    public void testCreatingInitialGameState() {
        try {
            GameState state = gameStateFactory.create(FenParser.INITIAL_STATE_FEN);

            //Asserts
            String tail = " in the initial state of game";
            //Check position
            Position expected = Guice.createInjector(new MyModule()).getInstance(Position.class);
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
            assertEquals(state.getCastlings(), EnumSet.allOf(Castling.class), "All castling availabilities should be present" + tail);
            assertNull(state.getEnPassantTarget(), "En-passant target square should be null" + tail);
            assertEquals(state.getHalfmoveClock(), 0, "Half-move clock should be 0" + tail);
            assertEquals(state.getFullmoveNumber(), 1, "Full-move number should be 1" + tail);
        } catch (InvalidFenException ife) {
            fail("Exception thrown, even though FEN was ok!", ife);
        }
    }

    @Test(dependsOnMethods = "testCreatingInitialGameState")
    public void testCreateGameStateFromInitialByE4() {
        final Move firstMove = new Move(WHITE_PAWN, E2, E4);
        try {
            GameState initialGameState = gameStateFactory.create(FenParser.INITIAL_STATE_FEN);
            GameState stateAfterE4 = gameStateFactory.create(initialGameState, firstMove);

            //Check position
            Position initial = injector.getInstance(Position.class);
            Position expected = initial.createNewPositionUsing(firstMove);

            //Asserts
            String msgTail = " after e4 played from the initial state of game";
            assertEquals(stateAfterE4.getPosition(), expected);
            assertEquals(stateAfterE4.getPosition().getPiece(E2), null);
            assertEquals(stateAfterE4.getPosition().getPiece(E4), WHITE_PAWN);

            //Check the other components of the game state
            assertFalse(stateAfterE4.isWhiteToMove(), "It should be black to move" + msgTail);
            assertEquals(stateAfterE4.getCastlings(), EnumSet.allOf(Castling.class), "All castling availabilities should be present" + msgTail);
            assertEquals(stateAfterE4.getEnPassantTarget(), E3, "En-passant target square should be E3" + msgTail);
            assertEquals(stateAfterE4.getHalfmoveClock(), 0, "Half-move clock should be 0" + msgTail);
            assertEquals(stateAfterE4.getFullmoveNumber(), 1, "Full-move number should be 1" + msgTail);
        } catch (InvalidFenException | PieceNotPresentException | IllegalMoveException ex) {
            fail("Unexpected exception was thrown!", ex);
        }
    }
}
