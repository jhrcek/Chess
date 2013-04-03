package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.model.api.Chessboard;
import cz.janhrcek.chess.model.api.GameBrowser;
import cz.janhrcek.chess.model.api.GameBrowserChangedEvent;
import cz.janhrcek.chess.model.api.GameListener;
import cz.janhrcek.chess.model.api.Position;
import cz.janhrcek.chess.model.api.enums.Castling;
import cz.janhrcek.chess.model.api.enums.Piece;
import cz.janhrcek.chess.model.api.enums.Square;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

/**
 *
 * @author jhrcek
 */
public final class PositionInfoDisplayer extends JTextArea implements GameListener {

    private GameBrowser gameBrowser;

    public PositionInfoDisplayer(GameBrowser gameBrowser) {
        setLineWrap(true);
        setBorder(new LineBorder(Color.BLACK, 1));
        setGameBrowser(gameBrowser);
    }

    //TODO - remove dependency on GameBrowser, because we can get all the info we need from GameBrowserChangedEvent!
    //question remains - how to register this component as a gameListener - here, or outside?
    void setGameBrowser(GameBrowser gameBrowser) {
        this.gameBrowser = Objects.requireNonNull(gameBrowser, "gameBrowser must not be null!");
        this.gameBrowser.addGameListener(this);
        displayPosition(this.gameBrowser.getFocusedPosition());
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
                .append("\n Fullmove: ").append(position.getFullmoveNumber())
                .append("\n ----------")
                .append("\n Material imbalance: ").append(getMaterialImbalance(position));
        setText(sb.toString());
    }

    private List<Piece> getMaterialImbalance(Position position) {
        List<Piece> imbalance = new ArrayList<>();
        List<Piece> whitePieces = new ArrayList<>();
        List<Piece> blackPieces = new ArrayList<>();
        Chessboard board = position.getChessboard();
        Piece p;
        for (Square sq : Square.values()) {//TODO extract method, the list of white/black pieces should probably be obtainable by chessboard
            if ((p = board.getPiece(sq)) != null) {
                if (p.isWhite()) {
                    whitePieces.add(p);
                } else {
                    blackPieces.add(p);
                }
            }
        }
        Collections.sort(whitePieces, Piece.COMPARATOR);
        Collections.sort(blackPieces, Piece.COMPARATOR);

        System.out.println("White: " + whitePieces);
        System.out.println("Black: " + blackPieces);
        int i = 0, j = 0;
        while (i < whitePieces.size() && j < blackPieces.size()) {
            if (whitePieces.get(i).getOrdinal() > blackPieces.get(j).getOrdinal()) {
                imbalance.add(blackPieces.get(j));
                j++;
            } else if (whitePieces.get(i).getOrdinal() < blackPieces.get(j).getOrdinal()) {
                imbalance.add(whitePieces.get(i));
                i++;
            } else {
                i++;
                j++;
            }
        }
        return imbalance;
    }
}
