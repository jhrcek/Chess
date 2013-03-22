package cz.janhrcek.chess.FEN;

/**
 * Represents exception which arose during processing of FEN strings.
 * @author jhrcek
 */
public class InvalidFenException extends Exception {

    public InvalidFenException(String message) {
        super(message);
    }
    
}
