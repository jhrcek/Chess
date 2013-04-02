package cz.janhrcek.chess.model.api;

import cz.janhrcek.chess.model.api.enums.Piece;
import cz.janhrcek.chess.model.api.enums.Square;

/**
 * Represents placement of pieces on the chessboard. Instances of this class
 * are supposed to be be immutable.
 *
 * @author jhrcek
 */
public interface Chessboard {

    Piece getPiece(Square square);

    Chessboard createNewPositionUsing(Move move);
}
