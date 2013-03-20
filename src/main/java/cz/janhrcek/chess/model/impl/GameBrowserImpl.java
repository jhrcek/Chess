package cz.janhrcek.chess.model.impl;

import cz.janhrcek.chess.gui.MoveListener;
import cz.janhrcek.chess.model.api.GameBrowser;
import cz.janhrcek.chess.model.api.GameChangedEvent;
import cz.janhrcek.chess.model.api.GameListener;
import cz.janhrcek.chess.model.api.GameState;
import cz.janhrcek.chess.model.api.IllegalMoveException;
import cz.janhrcek.chess.model.api.Move;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initial state of the game is set upon construction.
 *
 * @author jhrcek
 */
public class GameBrowserImpl implements GameBrowser, MoveListener {

    private final GameTree gameTree;
    private final List<GameListener> gameListeners;
    private static final Logger log = LoggerFactory.getLogger(GameBrowserImpl.class);

    public GameBrowserImpl(GameTree gameTree) {
        this.gameTree = gameTree;
        gameListeners = new ArrayList<>();
    }

    @Override
    public GameState getInitialState() {
        return gameTree.getRootState();
    }

    @Override
    public void makeMove(Move move) throws ChessboardException, IllegalMoveException {
        log.info("Trying to make move{}", move);
        gameTree.addMove(move);
    }

    @Override
    public GameState getFocusedState() {
        return gameTree.getFocusedState();
    }

    @Override
    public void focusInitialState() {
        log.info("Focusing initial state");
        GameState previousState = getFocusedState();
        gameTree.focusRoot();
        GameState currentState = getFocusedState();
        notifyListeners(new GameChangedEvent(previousState, currentState));
    }

    @Override
    public void focusNextState() {
        log.info("Focusing next state");
        GameState previousState = getFocusedState();
        gameTree.focusFirstChild();
        GameState currentState = getFocusedState();
        notifyListeners(new GameChangedEvent(previousState, currentState));
    }

    @Override
    public void focusPreviousState() {
        log.info("Focusing previous state");
        GameState previousState = getFocusedState();
        gameTree.focusParent();
        GameState currentState = getFocusedState();
        notifyListeners(new GameChangedEvent(previousState, currentState));
    }

    @Override
    public void focusLastState() {
        log.info("Focusing last state");
        GameState previousState = getFocusedState();
        gameTree.focusLeaf();
        GameState currentState = getFocusedState();
        notifyListeners(new GameChangedEvent(previousState, currentState));
    }

    @Override
    public void moveSelected(Move move) {
        log.info("Got notification from GUI - move selected: {}", move);
        try {
            GameState previousState = getFocusedState();
            makeMove(move);
            GameState currentState = getFocusedState();
            notifyListeners(new GameChangedEvent(previousState, currentState));
        } catch (IllegalMoveException ex) {
            log.info("The move selected is illegal.", ex);
        } catch (ChessboardException ex) {
            log.info("The move selected has problem.", ex);
        }
    }

    @Override
    public String toString() {
        return gameTree.toString();
    }

    @Override
    public void addGameListener(GameListener gl) {
        gameListeners.add(gl);
    }

    @Override
    public void removeGameListener(GameListener gl) {
        gameListeners.remove(gl);
    }

    //----------------------- PRIVATE IMPLEMENTATION ---------------------------
    private void notifyListeners(GameChangedEvent change) {
        for (GameListener gameListener : gameListeners) {
            gameListener.gameChanged(change);
        }
    }
}
