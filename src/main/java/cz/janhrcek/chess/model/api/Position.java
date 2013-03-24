package cz.janhrcek.chess.model.api;

import cz.janhrcek.chess.model.api.enums.Piece;
import cz.janhrcek.chess.model.api.enums.Square;

/**
 * Represents placement of pieces on the chessboard. Instances of this class
 * must be immutable.
 *
 * @author jhrcek
 */
public interface Position {

    Piece getPiece(Square square);

    Position createNewPositionUsing(Move move);
}
