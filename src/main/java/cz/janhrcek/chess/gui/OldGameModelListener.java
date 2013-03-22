package cz.janhrcek.chess.gui;

/**
 * Enables other classes to be informed about the changes of the game state
 * which this class represents.
 *
 * @author xhrcek
 */
public interface OldGameModelListener {

    /**
     * This method should contain code which reacts to the OldGameModelEvent.
     *
     * @param event the event fired by some game model representing the change
     * of the model.
     */
    void gameStateChanged(OldGameModelEvent event);
}
