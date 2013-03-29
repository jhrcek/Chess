package cz.janhrcek.chess.model.api;

import cz.janhrcek.chess.model.api.enums.Square;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This event represents change of the browser state (either focus change or
 * addition of Position (via making of move) to the game).
 *
 * @author jhrcek
 */
public class GameBrowserChangedEvent {

    /**
     * The list of squares in the position, whose contents changed (either
     * pieces were put on them or removed from them).
     */
    private final List<Square> changedSquares;
    private final Position previous;
    private final Position current;
    private static final Logger log = LoggerFactory.getLogger(GameBrowserChangedEvent.class);

    public GameBrowserChangedEvent(Position previous, Position current) {
        this.previous = previous;
        this.current = current;

        Chessboard oreviousBoard = previous.getChessboard();
        Chessboard currentBoard = current.getChessboard();
        changedSquares = new ArrayList<>(64);
        for (Square square : Square.values()) {
            if (oreviousBoard.getPiece(square) != currentBoard.getPiece(square)) {
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

    public Position getPreviousPosition() {
        return previous;
    }

    public Position getCurrentPosition() {
        return current;
    }
}
