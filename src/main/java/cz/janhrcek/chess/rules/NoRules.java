package cz.janhrcek.chess.rules;

import zzz.to.delete.OldGameStateMutable;
import cz.janhrcek.chess.model.api.Move;

/**
 * Implementation of the Rule checker, that does not control the legality of the
 * moves in any way, that is it considers any move legal in any state of game.
 *
 * @author Jan Hrcek
 */
public class NoRules implements OldRuleChecker {

    /**
     * Since no control of legality is made, this implementation always returns
     * OldMoveType.LEGAL object.
     *
     * @param state the state of game in which we would like to move a piece
     * from square to square and we want to know whether this move is legal.
     * @param move information about the move (which pice, from where, to where)
     * of which we want to check legality
     * @return OldMoveType.LEGAL
     *
     */
    @Override
    public final OldMoveType checkMove(final OldGameStateMutable state,
            final Move move) {
        if (state == null) {
            throw new IllegalArgumentException("state can't be null!");
        }
        if (move == null) {
            throw new IllegalArgumentException("move can't be null!");
        }
        return OldMoveType.LEGAL;
    }
}
