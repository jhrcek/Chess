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
}
