package cz.janhrcek.chess.model.api;

import cz.janhrcek.chess.model.api.enums.Castling;
import cz.janhrcek.chess.model.api.enums.Square;
import java.util.EnumSet;

/**
 * The instances of this class represents particular state of chess game. They
 * are immutable. Their information contents correspond to that of represented
 * by standard FEN string.
 *
 * @author jhrcek
 */
public interface Position {

    public Chessboard getChessboard();

    boolean isWhiteToMove();

    EnumSet<Castling> getCastlings();

    int getHalfmoveClock();

    Square getEnPassantTarget();

    int getFullmoveNumber();
}
