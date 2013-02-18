/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model.api;

import cz.janhrcek.chess.model.api.enums.Square;
import java.util.List;

/**
 *
 * @author jhrcek
 */
public class GameEvent {
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
    public GameEvent(List<Square> changedSquares) {
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
