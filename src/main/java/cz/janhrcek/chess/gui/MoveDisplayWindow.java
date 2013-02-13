package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.model.api.Move;
import java.util.LinkedList;
import javax.swing.JTextArea;

/**
 * This component serves for displaying the short-algebraic representation of
 * moves of the game currently displayed in the GUI.
 *
 * @author xhrcek
 */
public class MoveDisplayWindow extends JTextArea {

    private GameModel model;
    private String moveDelimiter;

    /**
     * Creates a new instance of MoveDisplayWindow
     *
     * @param model The model of the game from which this method can extract all
     * necessary information, so that it can display the text representation of
     * the moves played in the game
     */
    public MoveDisplayWindow(GameModel model) {
        if (model == null) {
            throw new NullPointerException("model can't be null!");
        }
        setModel(model);
        setEditable(false);
        setLineWrap(true); //make it wap lines
        setWrapStyleWord(true); //but not in the middle of words!
        writeOutMoves();
    }

    /**
     * Sets the model for the representation of the gamestate.
     *
     * @param model The model for representation of the state of the game
     */
    public void setModel(GameModel model) {
        if (this.model != null) {
            this.model.removeGameModelListener(myGameStateModelListener);
        }
        this.model = model;
        model.addGameModelListener(myGameStateModelListener);
    }

    /**
     * Makes this component display all the moves played in the game whose state
     * is represented by the GameMovel.
     */
    public void writeOutMoves() {
        LinkedList<Move> movesPlayed = model.getMovesPlayed();
        LinkedList<Boolean> checks = model.getChecks();
        int currentMove = model.getCurrentlyViewedHalfMove();
        setText("");
        String movetext = model.getMovetextString(true);
        setText(movetext.substring(0, movetext.lastIndexOf(" ")));
    }
    private GameModelListener myGameStateModelListener =
            new GameModelListener() {
                @Override
                public void gameStateChanged(GameModelEvent event) {
                    //Kdyz se zmeni stav hry prepiseme text
                    writeOutMoves();
                }
            };
}
