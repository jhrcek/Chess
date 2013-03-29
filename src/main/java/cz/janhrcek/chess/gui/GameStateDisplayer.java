package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.model.api.GameBrowser;
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

    public GameStateDisplayer(GameBrowser gameBrowser) {
        setLineWrap(true);
        setBorder(new LineBorder(Color.BLACK, 1));
        displayState(gameBrowser.getFocusedState());
    }

    @Override
    public void gameChanged(GameChangedEvent event) {
        displayState(event.getCurrentState());
    }

    private void displayState(GameState state) {
        StringBuilder sb = new StringBuilder();
        Square ep = state.getEnPassantTarget();
        sb.append(" Player to move: ").append(state.isWhiteToMove() ? "WHITE" : "BLACK")
                .append("\n Caslings: ").append(Castling.toFenCastlingSubstring(state.getCastlings()))
                .append("\n E-P target: ").append(ep == null ? "-" : ep)
                .append("\n Halfmove: ").append(state.getHalfmoveClock())
                .append("\n Fullmove: ").append(state.getFullmoveNumber());
        setText(sb.toString());
    }
}
