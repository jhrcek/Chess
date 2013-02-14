/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model.api;

/**
 *
 * @author jhrcek
 */
public interface Game {

    public GameState getInitialState();

    public void setInitialState(GameState initial);

    public void makeMove(Move m);

    public GameState getFocusedState();

    public void focusPreviousState();
}
