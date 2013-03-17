/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model.api;

import cz.janhrcek.chess.model.impl.ChessboardException;

/**
 *
 * @author jhrcek
 */
public interface GameBrowser {

    public GameState getInitialState();

    public GameState getFocusedState();

    //The only way to change the underlying graph of game states
    public void makeMove(Move m) throws ChessboardException, IllegalMoveException;

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
