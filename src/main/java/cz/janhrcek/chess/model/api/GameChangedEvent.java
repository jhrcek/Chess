package cz.janhrcek.chess.model.api;

import cz.janhrcek.chess.model.api.enums.Square;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final List<Square> changedSquares;
    private final GameState previous;
    private final GameState current;
    private static final Logger log = LoggerFactory.getLogger(GameChangedEvent.class);

    public GameChangedEvent(GameState previous, GameState current) {
        this.previous = previous;
        this.current = current;

        Position previousPos = previous.getPosition();
        Position currentPos = current.getPosition();
        changedSquares = new ArrayList<>(64);
        for (Square square : Square.values()) {
            if (previousPos.getPiece(square) != currentPos.getPiece(square)) {
                changedSquares.add(square);
            }
        }
        log.debug("Squares changed by the event: {}", changedSquares);
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

    public GameState getPreviousState() {
        return previous;
    }

    public GameState getCurrentState() {
        return current;
    }
}
