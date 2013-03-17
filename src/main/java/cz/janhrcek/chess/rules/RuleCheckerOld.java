package cz.janhrcek.chess.rules;

import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.impl.OldGameStateMutable;

/**
 * Provides methods, which allows user to control legality of moves in the given
 * some state of game.
 *
 * @author Jan Hrcek
 */
public interface RuleCheckerOld {

    /**
     * Checks legality of the given move in the given state of the game.
     *
     * @param state the state of game in which we would like to move a piece
     * from square to square and we want to know whether this move is legal.
     * @param move information about the move (which pice, from where, to where)
     * we want to check legality of
     * @return MoveTypeOld object carrying information about the legality of the
     * move.
     */
    MoveTypeOld checkMove(OldGameStateMutable state, Move move);
}
