package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.model.api.GameBrowser;
import cz.janhrcek.chess.model.api.GameBrowserChangedEvent;
import cz.janhrcek.chess.model.api.GameListener;
import cz.janhrcek.chess.model.api.Position;
import cz.janhrcek.chess.model.api.enums.Castling;
import cz.janhrcek.chess.model.api.enums.Square;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

/**
 *
 * @author jhrcek
 */
public class PositionInfoDisplayer extends JTextArea implements GameListener {

    public PositionInfoDisplayer(GameBrowser gameBrowser) {
        setLineWrap(true);
        setBorder(new LineBorder(Color.BLACK, 1));
        displayPosition(gameBrowser.getFocusedPosition());
    }

    @Override
    public void gameChanged(GameBrowserChangedEvent event) {
        displayPosition(event.getCurrentPosition());
    }

    private void displayPosition(Position position) {
        StringBuilder sb = new StringBuilder();
        Square ep = position.getEnPassantTarget();
        sb.append(" Player to move: ").append(position.isWhiteToMove() ? "WHITE" : "BLACK")
                .append("\n Caslings: ").append(Castling.toFenCastlingSubstring(position.getCastlings()))
                .append("\n E-P target: ").append(ep == null ? "-" : ep)
                .append("\n Halfmove: ").append(position.getHalfmoveClock())
                .append("\n Fullmove: ").append(position.getFullmoveNumber());
        setText(sb.toString());
    }
}
