/*
 * GameModelListener.java
 *
 * Created on 19. ��jen 2006, 15:32
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package cz.janhrcek.chess.gui;

/**
 * Enables other classes to be informed about the changes of the game state
 * which this class represents.
 *
 * @author xhrcek
 */
public interface GameModelListener {

    /**
     * This method should contain code which reacts to the GameModelEvent.
     *
     * @param event the event fired by some game model representing the change
     * of the model.
     */
    void gameStateChanged(GameModelEvent event);
}
