package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.model.MoveInfo;
import java.util.EventObject;

/**
 * This event represents that some move was selected by ChessboardComponent.
 *
 * @author xhrcek
 */
public class MoveSelectedEvent extends EventObject {

    private MoveInfo selectedMove;

    /**
     * Creates new instance of MoveSelectedEvent.
     *
     * @param source the source of the event
     * @param selectedMove the move whose selection this event represents
     */
    public MoveSelectedEvent(Object source, MoveInfo selectedMove) {
        super(source);
        this.selectedMove = selectedMove;
    }

    /**
     * The move that was selected.
     *
     * @return The move that was selected
     */
    public MoveInfo getSelectedMove() {
        return selectedMove;
    }

    /**
     * String representation of this MoveSelectedEvent
     *
     * @return the String representation of this MoveSelectedEvent containing
     * source and selected move
     */
    @Override
    public String toString() {
        return "Source of event = " + source + "selected move = " + selectedMove;
    }
}
