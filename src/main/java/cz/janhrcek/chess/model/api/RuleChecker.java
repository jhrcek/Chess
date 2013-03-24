package cz.janhrcek.chess.model.api;

import cz.janhrcek.chess.rules.IllegalMoveException;
import java.util.List;

/**
 *
 * @author jhrcek
 */
public interface RuleChecker {

    public boolean checkLegality(Move m, GameState state) throws IllegalMoveException;

    public List<Move> generateLegal(GameState state);
}
