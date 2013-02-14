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

    public GameState getInitialGameState(); //TODO should be immutable!

    public void setInitialGameState(GameState initial);

    public void makeMove(Move m); //from currently focused state

    public GameState getFocusedGameState();

    public void focusPreviousGameState();
}
