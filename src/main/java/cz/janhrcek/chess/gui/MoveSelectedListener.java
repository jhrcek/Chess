package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.model.api.Move;

/**
 * This interface enables class to be notified when the MoveSelected event is
 * fired.
 *
 * @author xhrcek
 */
public interface MoveSelectedListener {

    /**
     * In this method should be the code, which reacts to fired
     * MoveSelectedEvent.
     *
     * @param event the event which represents event, that the move was selected
     */
    void moveSelected(Move move);
}
