package cz.janhrcek.chess.model.impl;

import cz.janhrcek.chess.FEN.Fen;
import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.model.api.Game;
import cz.janhrcek.chess.model.api.GameBrowser;
import cz.janhrcek.chess.model.api.Position;
import cz.janhrcek.chess.rules.IllegalMoveException;
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
    private Game game;
    private GameBrowser gameBrowser;

    @BeforeMethod
    public void initializeGame() {
        try {
            game = new GameImpl(Fen.INITIAL_POSITION);
        } catch (InvalidFenException ife) {
            fail("Initial position not parsed correctly");
        }
        gameBrowser = game.getBrowser();
    }

    @Test
    public void testGetInitialPosition() {
        Position initialPosition = gameBrowser.getInitialPosition();
        assertEquals(initialPosition.getChessboard().getPiece(A1), WHITE_ROOK);
        assertEquals(initialPosition.getHalfmoveClock(), 0);
        assertEquals(initialPosition.getFullmoveNumber(), 1);
        assertEquals(initialPosition.getChessboard().getPiece(E2), WHITE_PAWN);
        assertEquals(initialPosition.getChessboard().getPiece(E4), null);
        assertEquals(initialPosition.getEnPassantTarget(), null);
    }

    @Test
    public void testMakeMoveE4() {
        try {
            gameBrowser.makeMove(MOVE_E4);
            Position position = gameBrowser.getFocusedPosition();
            assertEquals(position.getHalfmoveClock(), 0);
            assertEquals(position.getFullmoveNumber(), 1);
            assertEquals(position.getChessboard().getPiece(E4), WHITE_PAWN);
            assertEquals(position.getChessboard().getPiece(E2), null);
            assertEquals(position.getEnPassantTarget(), E3);
        } catch (PieceNotPresentException | IllegalMoveException ex) {
            fail("Unexpected exception!", ex);
        }
    }

    @Test
    public void testMakeMoveE4Nf6() {
        try {
            gameBrowser.makeMove(MOVE_E4);
            gameBrowser.makeMove(MOVE_NF6);
            Position position = gameBrowser.getFocusedPosition();

            assertEquals(position.getChessboard().getPiece(E4), WHITE_PAWN);
            assertEquals(position.getChessboard().getPiece(E2), null);
            assertEquals(position.getChessboard().getPiece(G8), null);
            assertEquals(position.getChessboard().getPiece(F6), BLACK_KNIGHT);
            assertEquals(position.getEnPassantTarget(), null);
            assertEquals(position.getHalfmoveClock(), 1);
            assertEquals(position.getFullmoveNumber(), 2);
        } catch (PieceNotPresentException | IllegalMoveException ex) {
            fail("Unexpected exception!", ex);
        }
    }

    @Test
    public void testFocusPreviousPosition() {
        try {
            gameBrowser.makeMove(MOVE_E4);
            gameBrowser.makeMove(MOVE_NF6);
            gameBrowser.focusPreviousPosition();
            Position position = gameBrowser.getFocusedPosition();

            assertEquals(position.getChessboard().getPiece(E4), WHITE_PAWN);
            assertEquals(position.getChessboard().getPiece(E2), null);
            assertEquals(position.getChessboard().getPiece(G8), BLACK_KNIGHT); //knight is back!
            assertEquals(position.getChessboard().getPiece(F6), null); //F6 is empty again!
            assertEquals(position.getEnPassantTarget(), E3);
            assertEquals(position.getHalfmoveClock(), 0);
            assertEquals(position.getFullmoveNumber(), 1);
        } catch (IllegalMoveException | PieceNotPresentException ex) {
            fail("Unexpected exception!", ex);
        }
    }

    @Test
    public void testSomeMoves() { //TODO add some asserts
        try {
            gameBrowser.makeMove(MOVE_E4);
            gameBrowser.makeMove(MOVE_NF6);
            gameBrowser.makeMove(MOVE_NF3);
            Position position = gameBrowser.getFocusedPosition();
            System.out.println(gameBrowser.toString());
        } catch (IllegalMoveException | PieceNotPresentException ex) {
            fail("Unexpected exception!", ex);
        }
    }
}
