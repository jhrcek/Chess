package cz.janhrcek.chess.rules;

/**
 *
 * @author jhrcek
 */
public class IllegalMoveException extends Exception {

    public IllegalMoveException(String msg) {
        super(msg);
    }
}
