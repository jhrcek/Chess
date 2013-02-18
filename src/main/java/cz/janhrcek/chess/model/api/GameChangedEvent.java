package cz.janhrcek.chess.model.api;

import cz.janhrcek.chess.model.api.enums.Square;
import java.util.List;

/**
 * This event represents change in the state of the game.
 *
 * @author jhrcek
 */
public class GameChangedEvent {

    /**
     * The list of squares in the position, whose contents changed (either
     * pieces were put on them or removed from them).
     */
    private List<Square> changedSquares;

    /**
     * Creates the instance of the GameChangedEvent.
     *
     * @param source the GameModel which fired this event
     * @param changedSquares the squares on the board that changed since the
     * last GameModelEvent was fired.
     */
    public GameChangedEvent(List<Square> changedSquares) {
        this.changedSquares = changedSquares;
    }

    /**
     * This method returns list of squares on the chessboard which changed
     * (pieces were put on them or removed from them).
     *
     * @return the squares that changes since the last GameModelEvent was fired
     */
    public List<Square> getChangedSquares() {
        return changedSquares;
    }
}
