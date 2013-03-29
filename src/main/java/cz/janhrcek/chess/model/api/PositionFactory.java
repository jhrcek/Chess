package cz.janhrcek.chess.model.api;

import cz.janhrcek.chess.rules.IllegalMoveException;
import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.model.impl.PieceNotPresentException;

/**
 *
 * @author jhrcek
 */
public interface PositionFactory {

    public Position create(String fen) throws InvalidFenException;

    public Position create(Position from, Move move) throws PieceNotPresentException, IllegalMoveException;

    public void setRuleChecker(RuleChecker rc);
}
