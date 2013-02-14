/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model.api;

/**
 *
 * @author jhrcek
 */
public interface GameStateFactory {

    public GameState create(String fen);

    public GameState create(GameState from, Move move);
    
}
