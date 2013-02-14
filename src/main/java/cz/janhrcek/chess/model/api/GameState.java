/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model.api;

import cz.janhrcek.chess.model.api.enums.CastlingAvailability;
import cz.janhrcek.chess.model.api.enums.Square;
import cz.janhrcek.chess.model.impl.Position;
import java.util.EnumSet;

/**
 * The instances of this class represents particular state of chess game.
 * They are immutable.
 *
 * @author jhrcek
 */
public interface GameState {

    public Position getPosition();

    boolean isWhiteToMove();

    EnumSet<CastlingAvailability> getCastlingAvailabilities();

    int getHalfmoveClock();

    Square getEnPassantTargetSquare();

    int getFullmoveNumber();
}
