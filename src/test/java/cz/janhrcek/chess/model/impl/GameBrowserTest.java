package cz.janhrcek.chess.model.impl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import cz.janhrcek.chess.guice.MyModule;
import cz.janhrcek.chess.model.api.GameBrowser;
import cz.janhrcek.chess.model.api.GameState;
import cz.janhrcek.chess.model.api.IllegalMoveException;
import cz.janhrcek.chess.model.api.Move;
import static cz.janhrcek.chess.model.api.enums.Piece.*;
import static cz.janhrcek.chess.model.api.enums.Square.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author jhrcek
 */
public class GameBrowserTest {

    private static final Move MOVE_E4 = new Move(WHITE_PAWN, E2, E4);
    private static final Move MOVE_NF6 = new Move(BLACK_KNIGHT, G8, F6);
    private static final Move MOVE_NF3 = new Move(WHITE_KNIGHT, G1, F3);
    private Injector injector;
    private GameBrowser gameBrowser;

    @BeforeMethod
    public void initializeGame() {
        injector = Guice.createInjector(new MyModule());
        gameBrowser = injector.getInstance(GameBrowser.class);
    }

    @Test
    public void testGetInitialState() {
        GameState initialState = gameBrowser.getInitialState();
        assertEquals(initialState.getPosition().getPiece(A1), WHITE_ROOK);
        assertEquals(initialState.getHalfmoveClock(), 0);
        assertEquals(initialState.getFullmoveNumber(), 1);
        assertEquals(initialState.getPosition().getPiece(E2), WHITE_PAWN);
        assertEquals(initialState.getPosition().getPiece(E4), null);
        assertEquals(initialState.getEnPassantTarget(), null);
    }

    @Test
    public void testMakeMoveE4() {
        try {
            gameBrowser.makeMove(MOVE_E4);
            GameState state = gameBrowser.getFocusedState();
            assertEquals(state.getHalfmoveClock(), 0);
            assertEquals(state.getFullmoveNumber(), 1);
            assertEquals(state.getPosition().getPiece(E4), WHITE_PAWN);
            assertEquals(state.getPosition().getPiece(E2), null);
            assertEquals(state.getEnPassantTarget(), E3);
        } catch (ChessboardException | IllegalMoveException ex) {
            fail("Unexpected exception!", ex);
        }
    }

    @Test
    public void testMakeMoveE4Nf6() {
        try {
            gameBrowser.makeMove(MOVE_E4);
            gameBrowser.makeMove(MOVE_NF6);
            GameState state = gameBrowser.getFocusedState();

            assertEquals(state.getPosition().getPiece(E4), WHITE_PAWN);
            assertEquals(state.getPosition().getPiece(E2), null);
            assertEquals(state.getPosition().getPiece(G8), null);
            assertEquals(state.getPosition().getPiece(F6), BLACK_KNIGHT);
            assertEquals(state.getEnPassantTarget(), null);
            assertEquals(state.getHalfmoveClock(), 1);
            assertEquals(state.getFullmoveNumber(), 2);
        } catch (ChessboardException | IllegalMoveException ex) {
            fail("Unexpected exception!", ex);
        }
    }

    @Test
    public void testFocusPreviousState() {
        try {
            gameBrowser.makeMove(MOVE_E4);
            gameBrowser.makeMove(MOVE_NF6);
            gameBrowser.focusPreviousState();
            GameState state = gameBrowser.getFocusedState();

            assertEquals(state.getPosition().getPiece(E4), WHITE_PAWN);
            assertEquals(state.getPosition().getPiece(E2), null);
            assertEquals(state.getPosition().getPiece(G8), BLACK_KNIGHT); //knight is back!
            assertEquals(state.getPosition().getPiece(F6), null); //F6 is empty again!
            assertEquals(state.getEnPassantTarget(), E3);
            assertEquals(state.getHalfmoveClock(), 0);
            assertEquals(state.getFullmoveNumber(), 1);
        } catch (IllegalMoveException | ChessboardException ex) {
            fail("Unexpected exception!", ex);
        }
    }

    @Test
    public void testSomeMoves() { //TODO add some asserts
        try {
            gameBrowser.makeMove(MOVE_E4);
            gameBrowser.makeMove(MOVE_NF6);
            gameBrowser.makeMove(MOVE_NF3);
            GameState state = gameBrowser.getFocusedState();
            System.out.println(gameBrowser.toString());
        } catch (IllegalMoveException | ChessboardException ex) {
            fail("Unexpected exception!", ex);
        }
    }
}
