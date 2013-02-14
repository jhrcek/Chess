/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model.impl;

import cz.janhrcek.chess.model.api.GameState;
import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.RuleChecker;
import java.util.Collections;
import java.util.List;



/**
 *
 * @author jhrcek
 */
public class FIDERuleChecker implements RuleChecker {

    public FIDERuleChecker() {
    }
    
    @Override
    public boolean isLegal(Move m, GameState state) {
        return true;
    }

    @Override
    public List<Move> generateLegal(GameState state) {
        return Collections.EMPTY_LIST;
    }
}
