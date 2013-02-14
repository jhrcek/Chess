/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model.api;

import cz.janhrcek.chess.FEN.InvalidFenException;

/**
 *
 * @author jhrcek
 */
public interface GameStateFactory {

    public GameState create(String fen) throws InvalidFenException;

    public GameState create(GameState from, Move move);

    public void setRuleChecker(RuleChecker rc);
}
