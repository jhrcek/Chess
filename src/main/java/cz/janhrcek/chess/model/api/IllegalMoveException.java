/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
