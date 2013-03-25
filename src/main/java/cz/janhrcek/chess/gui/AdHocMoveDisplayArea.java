package cz.janhrcek.chess.gui;

import com.google.inject.Inject;
import cz.janhrcek.chess.model.api.GameBrowser;
import cz.janhrcek.chess.model.api.GameChangedEvent;
import cz.janhrcek.chess.model.api.GameListener;
import javax.swing.JTextArea;

/**
 *
 * @author jhrcek
 */
public class AdHocMoveDisplayArea extends JTextArea implements GameListener {

    private GameBrowser game;

    @Inject
    public AdHocMoveDisplayArea(GameBrowser game) {
        this.game = game;
        game.addGameListener(this);//listen to GameChangeEvents
        setText(game.toString());
    }

    @Override
    public void gameChanged(GameChangedEvent event) {
        setText(game.toString());
    }
}
