package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.model.api.GameChangedEvent;
import cz.janhrcek.chess.model.api.GameListener;
import cz.janhrcek.chess.model.api.GameState;
import cz.janhrcek.chess.model.api.enums.Castling;
import cz.janhrcek.chess.model.api.enums.Square;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

/**
 *
 * @author jhrcek
 */
public class GameStateDisplayer extends JTextArea implements GameListener {

    public GameStateDisplayer() {
        setLineWrap(true);
        setBorder(new LineBorder(Color.BLACK, 1));
    }

    @Override
    public void gameChanged(GameChangedEvent event) {
        GameState state = event.getCurrentState();
        StringBuilder text = new StringBuilder();
        Square ep = state.getEnPassantTarget();
        text.append("Player to move: ").append(state.isWhiteToMove() ? "WHITE" : "BLACK")
                .append("\nCaslings: ").append(Castling.toFenCastlingSubstring(state.getCastlings()))
                .append("\nE-P target: ").append(ep == null ? "-" : ep)
                .append("\nHalfmove: ").append(state.getHalfmoveClock())
                .append("\nFullmove: ").append(state.getFullmoveNumber());
        setText(text.toString());
    }
}
