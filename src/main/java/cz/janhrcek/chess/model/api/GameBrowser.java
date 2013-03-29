package cz.janhrcek.chess.model.api;

import cz.janhrcek.chess.rules.IllegalMoveException;
import cz.janhrcek.chess.model.impl.PieceNotPresentException;

/**
 * Enables browsing of chess game. The key concept of this browser is, that it
 * focuses on a single Position of a game (retrievable by
 * {@link getFocusedPosition()}). It also provides methods to change focus by
 * using 4 focus methods starting (this is read-only, without changing the
 * underlying game) and to add new Positions to the underlying Game using
 * {@link makeMove()} method.
 *
 * @author jhrcek
 */
public interface GameBrowser {

    public Position getFocusedPosition();

    //The only way to change the underlying tree of positions
    public void makeMove(Move m) throws PieceNotPresentException, IllegalMoveException;

    /**
     * User's of this class that want to be notified of changes should add
     * GameListener objects using this method.
     */
    public void addGameListener(GameListener gl);

    public void removeGameListener(GameListener gl);

    //Browsing positions without changing underlying game
    public void focusInitialPosition();

    public void focusNextPosition();

    public void focusPreviousPosition();

    public void focusLastPosition();

    public void focusPositionWithId(int id); //TODO -leaking implementation detail into API -remove somehow
}
