/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
