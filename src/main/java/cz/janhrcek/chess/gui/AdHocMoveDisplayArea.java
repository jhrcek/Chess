/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.gui;

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

    public AdHocMoveDisplayArea(GameBrowser game) {
        this.game = game;
        game.addGameListener(this);//listen to GameChangeEvents
    }
    
    @Override
    public void gameChanged(GameChangedEvent event) {
        setText(game.toString());
    }
    
}
