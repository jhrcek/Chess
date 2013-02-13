/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model.api;

/**
 *
 * @author jhrcek
 */
public interface BrowsableGame {
 public GameState getInitialGameState();
 public GameState getFocusedGameState();
}
