/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model.api;

import java.util.List;

/**
 *
 * @author jhrcek
 */
public interface RuleChecker {

    public boolean isLegal(Move m, GameState state);

    public List<Move> generateLegal(GameState state);
}
