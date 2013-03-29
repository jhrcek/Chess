package cz.janhrcek.chess.model.impl;

import cz.janhrcek.chess.model.api.Position;
import cz.janhrcek.chess.rules.IllegalMoveException;
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
    public boolean checkLegality(Move m, Position position) throws IllegalMoveException {
        return true;
    }

    @Override
    public List<Move> generateLegal(Position position) {
        return Collections.emptyList();
    }
}
