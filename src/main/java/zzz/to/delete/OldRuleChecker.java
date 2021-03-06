package zzz.to.delete;

import cz.janhrcek.chess.model.api.Move;
import zzz.to.delete.OldGameStateMutable;

/**
 * Provides methods, which allows user to control legality of moves in the given
 * some state of game.
 *
 * @author Jan Hrcek
 */
public interface OldRuleChecker {

    /**
     * Checks legality of the given move in the given state of the game.
     *
     * @param state the state of game in which we would like to move a piece
     * from square to square and we want to know whether this move is legal.
     * @param move information about the move (which pice, from where, to where)
     * we want to check legality of
     * @return OldMoveType object carrying information about the legality of the
     * move.
     */
    OldMoveType checkMove(OldGameStateMutable state, Move move);
}
