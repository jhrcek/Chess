package zzz.to.delete;

import cz.janhrcek.chess.model.api.enums.Square;
import java.util.EventObject;
import java.util.List;

/**
 * This event indicates, that the state of game represented by the class
 * OldGameModel has changed. This event carries information which squares on
 * chessboard changed during the last change of the state of the game.
 *
 * @author Jan Hrcek
 */
public class OldGameModelEvent extends EventObject {

    /**
     * The list of squares on the chessboard, whose contents (pieces) changed
     * since the last OldGameModelEvent was fired.
     */
    private List<Square> changedSquares;

    /**
     * Creates the instance of the OldGameModelEvent.
     *
     * @param source the OldGameModel which fired this event
     * @param changedSquares the squares on the board that changed since the
     * last OldGameModelEvent was fired.
     */
    public OldGameModelEvent(OldGameModel source, List<Square> changedSquares) {
        super(source);
        this.changedSquares = changedSquares;
    }

    /**
     * This method returns list of squares on the chessboard which changed
     * during the last change of the state.
     *
     * @return the squares that changes since the last OldGameModelEvent was fired
     */
    public List<Square> getChangedSquares() {
        return changedSquares;
    }
}
