package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.model.Square;
import java.util.EventObject;
import java.util.List;

/**
 * This event indicates, that the state of game represented by the class
 * GameModel has changed. This event carries information which squares on
 * chessboard changed during the last change of the state of the game.
 *
 * @author Jan Hrcek
 */
public class GameModelEvent extends EventObject {

    /**
     * The list of squares on the chessboard, whose contents (pieces) changed
     * since the last GameModelEvent was fired.
     */
    private List<Square> changedSquares;

    /**
     * Creates the instance of the GameModelEvent.
     *
     * @param source the GameModel which fired this event
     * @param changedSquares the squares on the board that changed since the
     * last GameModelEvent was fired.
     */
    public GameModelEvent(GameModel source, List<Square> changedSquares) {
        super(source);
        this.changedSquares = changedSquares;
    }

    /**
     * This method returns list of squares on the chessboard which changed
     * during the last change of the state.
     *
     * @return the squares that changes since the last GameModelEvent was fired
     */
    public List<Square> getChangedSquares() {
        return changedSquares;
    }
}
