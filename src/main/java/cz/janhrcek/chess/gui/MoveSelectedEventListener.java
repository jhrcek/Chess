package cz.janhrcek.chess.gui;

import java.util.EventListener;

/**
 * This interface enables class to be informed when the MoveSelected event is
 * fired.
 *
 * @author xhrcek
 */
public interface MoveSelectedEventListener extends EventListener {

    /**
     * In this method should be the code, which reacts to fired
     * MoveSelectedEvent.
     *
     * @param event the event which represents event, that the move was selected
     */
    void moveSelected(MoveSelectedEvent event);
}
