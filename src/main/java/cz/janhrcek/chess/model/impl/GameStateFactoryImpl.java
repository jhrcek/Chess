/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model.impl;

import cz.janhrcek.chess.FEN.FenParser;
import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.model.api.GameState;
import cz.janhrcek.chess.model.api.GameStateFactory;
import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.RuleChecker;

/**
 *
 * @author jhrcek
 */
public class GameStateFactoryImpl implements GameStateFactory {

    RuleChecker ruleChecker;
    FenParser parser;
    
    public GameStateFactoryImpl(RuleChecker rc) {
        ruleChecker = rc;
        parser = new FenParser();
    }

    @Override
    public GameState create(String fen) throws InvalidFenException {
        return parser.fenToGameState(fen);
    }

    @Override
    public GameState create(GameState from, Move move) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setRuleChecker(RuleChecker rc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
