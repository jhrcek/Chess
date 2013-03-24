package cz.janhrcek.chess.model.api;

import cz.janhrcek.chess.rules.IllegalMoveException;
import cz.janhrcek.chess.model.impl.PieceNotPresentException;

/**
 * Enables browsing of chess game. The key concept of this browser is, that it
 * focuses on a single GameState of a game (retrievable by
 * {@link getFocusedState()}). It also provides methods to change focus by using
 * 4 focus methods starting with "link" (this is read-only, without changing the
 * underlying game) and to add new GameStates to the underlying GameTree using
 * {@link makeMove()} method.
 *
 * @author jhrcek
 */
public interface GameBrowser {

    public GameState getInitialState();

    public GameState getFocusedState();

    //The only way to change the underlying graph of game states
    public void makeMove(Move m) throws PieceNotPresentException, IllegalMoveException;

    /**
     * User's of this class that want to be notified of changes should add
     * GameListener objects using this method.
     */
    public void addGameListener(GameListener gl);

    public void removeGameListener(GameListener gl);

    //Browsing states without changing underlying game
    public void focusInitialState();

    public void focusNextState();

    public void focusPreviousState();

    public void focusLastState();
}
