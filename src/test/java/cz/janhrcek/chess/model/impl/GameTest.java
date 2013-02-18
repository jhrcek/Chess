/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model.impl;

import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.model.api.Game;
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
import org.testng.annotations.Test;

/**
 *
 * @author jhrcek
 */
public class GameTest {

    private RuleChecker rc;
    private GameStateFactory gsf;

    @BeforeClass
    public void setup() {
        rc = new FIDERuleChecker();
        gsf = new GameStateFactoryImpl(rc);
    }

    @Test
    public void testGetInitialState() {
        try {
            Game g = new GameImpl("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", gsf);

            GameState initialState = g.getInitialState();
            assertEquals(initialState.getPosition().getPiece(A1), WHITE_ROOK);
        } catch (InvalidFenException ex) {
            fail("Unexpected exception!", ex);
        }
    }

    @Test
    public void testMakingMoves() {
        try {
            Game g = new GameImpl("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", gsf);

            g.makeMove(new Move(WHITE_PAWN, E2, E4));
            GameState state = g.getFocusedState();
            assertEquals(state.getHalfmoveClock(), 0);
            assertEquals(state.getFullmoveNumber(), 1);
            assertEquals(state.getPosition().getPiece(E4), WHITE_PAWN);
            assertEquals(state.getPosition().getPiece(E2), null);
            assertEquals(state.getEnPassantTargetSquare(), E3);
        } catch (InvalidFenException | ChessboardException | IllegalMoveException ex) {
            fail("Unexpected exception!", ex);
        }
    }
}
