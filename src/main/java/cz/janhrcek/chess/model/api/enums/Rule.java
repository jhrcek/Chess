package cz.janhrcek.chess.model.api.enums;

/**
 *
 * @author jhrcek
 */
public enum Rule {

    NO_LEAVE_KING_IN_CHECK,
    PIECE_MOVE_PATTERN_OK,
    PLAYER_CHANGE_SIDES,
    NO_JUMP_OVER_OTHER_PIECES,
    NO_CAPTURE_OWN;
}
