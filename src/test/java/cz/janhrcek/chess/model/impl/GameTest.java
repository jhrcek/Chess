/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model.impl;

import cz.janhrcek.chess.FEN.FenParser;
import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.model.api.GameBrowser;
import cz.janhrcek.chess.model.api.GameState;
import cz.janhrcek.chess.model.api.GameStateFactory;
import cz.janhrcek.chess.model.api.IllegalMoveException;
import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.RuleChecker;
import static cz.janhrcek.chess.model.api.enums.Piece.*;
import static cz.janhrcek.chess.model.api.enums.Square.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author jhrcek
 */
public class GameTest {

    private RuleChecker rc;
    private GameStateFactory gsf;
    private GameBrowser game;

    @BeforeClass
    public void setup() {
        rc = new FIDERuleChecker();
        gsf = new GameStateFactoryImpl(rc);
    }

    @BeforeMethod
    public void initializeGame() {
        try {
            game = new GameBrowserImpl(FenParser.INITIAL_STATE_FEN, gsf);
        } catch (InvalidFenException ex) {
            fail("Unexpected exception!", ex);
        }
    }

    @Test
    public void testGetInitialState() {
        GameState initialState = game.getInitialState();
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
            game.makeMove(new Move(WHITE_PAWN, E2, E4));
            GameState state = game.getFocusedState();
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
            game.makeMove(new Move(WHITE_PAWN, E2, E4));
            game.makeMove(new Move(BLACK_KNIGHT, G8, F6));
            GameState state = game.getFocusedState();


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
            game.makeMove(new Move(WHITE_PAWN, E2, E4));
            game.makeMove(new Move(BLACK_KNIGHT, G8, F6));
            game.focusPreviousState();
            GameState state = game.getFocusedState();

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

    @Test //TODO deleteThis test
    public void testSomeMoves() {
        try {
            game.makeMove(new Move(WHITE_PAWN, E2, E4));
            game.makeMove(new Move(BLACK_KNIGHT, G8, F6));
            game.makeMove(new Move(WHITE_KNIGHT, G1, F3));
            GameState state = game.getFocusedState();
            System.out.println(game.toString());
        } catch (IllegalMoveException | ChessboardException ex) {
            fail("Unexpected exception!", ex);
        }
    }
}
