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
public interface Game {

    public GameState getInitialState();

    public void makeMove(Move m) throws ChessboardException, IllegalMoveException;

    public GameState getFocusedState();

    public void focusPreviousState();
    
    /**
     * User's of this class that want to be notified of changes should add
     * GameListener objects using this method.
     */
    public void addGameListener(GameListener gl);
    
    public void removeGameListener(GameListener gl);

    public void focusNextState();
}
