package cz.janhrcek.chess.model.impl;

import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.model.api.GameState;
import cz.janhrcek.chess.model.api.GameStateFactory;
import cz.janhrcek.chess.model.api.RuleChecker;
import cz.janhrcek.chess.model.api.enums.CastlingAvailability;
import cz.janhrcek.chess.model.api.enums.Piece;
import cz.janhrcek.chess.model.api.enums.Square;
import java.util.EnumSet;
import static org.testng.Assert.assertEquals;
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
            assertEquals(state.getPosition().getPiece(Square.A8), Piece.BLACK_ROOK);//TODO gameState factory test -implement initial position verification
            assertTrue(state.isWhiteToMove(), "It should be white to move" + tail);
            assertEquals(state.getCastlingAvailabilities(), EnumSet.allOf(CastlingAvailability.class), "All castling availabilities should be present" + tail);
            assertNull(state.getEnPassantTargetSquare(), "En-passant target square should be null" + tail);
            assertEquals(state.getHalfmoveClock(), 0, "Half-move clock should be 0" + tail);
            assertEquals(state.getFullmoveNumber(), 1, "Full-move number should be 1" + tail);
        } catch (InvalidFenException ife) {
            fail("Exception thrown, even though FEN was ok!", ife);
        }
    }
}
