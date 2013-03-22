package cz.janhrcek.chess.model.api;

/**
 *
 * @author jhrcek
 */
public class IllegalMoveException extends Exception {

    public IllegalMoveException(String msg) {
        super(msg);
    }
}
