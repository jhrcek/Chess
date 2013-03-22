package cz.janhrcek.chess.model.api;

import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.model.impl.ChessboardException;

/**
 *
 * @author jhrcek
 */
public interface GameStateFactory {

    public GameState create(String fen) throws InvalidFenException;

    public GameState create(GameState from, Move move) throws ChessboardException, IllegalMoveException;

    public void setRuleChecker(RuleChecker rc);
}
