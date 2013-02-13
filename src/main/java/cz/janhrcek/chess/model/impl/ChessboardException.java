package cz.janhrcek.chess.model.impl;

/**
 * Signals, that error has occurred during the manipulation with chessboard. For
 * example when someone is trying to move a piece from a square, on which the
 * piece is not present.
 *
 * @author Jan Hrcek
 * @version 6.3.2006
 */
public class ChessboardException extends Exception {

    /**
     * Constructs a new ChessboardException with the specified detail message.
     *
     * @param msg detailed message.
     */
    public ChessboardException(final String msg) {
        super(msg);
    }
}
