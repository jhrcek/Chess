/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model.impl;

import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.gui.MoveSelectedListener;
import cz.janhrcek.chess.model.api.Game;
import cz.janhrcek.chess.model.api.GameChangedEvent;
import cz.janhrcek.chess.model.api.GameListener;
import cz.janhrcek.chess.model.api.GameState;
import cz.janhrcek.chess.model.api.GameStateFactory;
import cz.janhrcek.chess.model.api.IllegalMoveException;
import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.enums.Square;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initial state of the game is set upon construction.
 *
 * @author jhrcek
 */
public class GameImpl implements Game, MoveSelectedListener {

    private final GameStateFactory stateFactory;
    private final GameTree gameTree;
    private final List<GameListener> gameListeners;
    private static final Logger LOG = LoggerFactory.getLogger(GameImpl.class);

    public GameImpl(String initialStateFen, GameStateFactory factory) throws InvalidFenException {
        this.stateFactory = factory;
        GameState initialState = stateFactory.create(initialStateFen);
        GameTree.HistoryNode initialHistoryNode = new GameTree.HistoryNode(null, null, initialState);
        this.gameTree = new GameTree(initialHistoryNode);
        gameListeners = new ArrayList<>();
    }

    @Override
    public GameState getInitialState() {
        return gameTree.getRoot().getGameState();
    }

    @Override
    public void makeMove(Move move) throws ChessboardException, IllegalMoveException {
        GameState stateBeforeMove = getFocusedState();
        GameState stateAfterMove = stateFactory.create(stateBeforeMove, move);
        GameTree.HistoryNode nodeAfterMove = new GameTree.HistoryNode(gameTree.getFocusedNode(), move, stateAfterMove);
        gameTree.addNode(nodeAfterMove);
    }

    @Override
    public GameState getFocusedState() {
        return gameTree.getFocusedNode().getGameState();
    }

    @Override
    public void focusPreviousState() {
        gameTree.focusParent();
    }

    @Override
    public String toString() {
        return gameTree.toString();
    }

    //----------------------- PRIVATE IMPLEMENTATION ---------------------------
    @Override
    public void addGameListener(GameListener gl) {
        gameListeners.add(gl);
    }

    @Override
    public void removeGameListener(GameListener gl) {
        gameListeners.remove(gl);
    }

    @Override
    public void moveSelected(Move move) {
        try {
            makeMove(move);
            LOG.info("Move selected: {}", move);
            List<Square> squaresThatChanged =
                    getSquaresThatChanged();
            GameChangedEvent gcEvent = new GameChangedEvent(squaresThatChanged);
            for (GameListener listener : gameListeners) {
                listener.gameChanged(gcEvent);
            }
        } catch (IllegalMoveException ex) {
            //TODO something in case of illegal move
        } catch (ChessboardException ex) {
            //TODO something in case of chessboardException
        }
    }

    /**
     * 
     * @return list of squares that changed from previously focused game state to currently focused state.
     */
    private List<Square> getSquaresThatChanged() {
        List<Square> changedSquares = new ArrayList<>();
        Position current = gameTree.getFocusedNode().getGameState().getPosition();
        Position previous = gameTree.getFocusedNode().getParent().getGameState().getPosition();
        for (Square sq : Square.values()) {
            if (current.getPiece(sq) != previous.getPiece(sq)) {
                changedSquares.add(sq);
            }
        }
        LOG.debug("Squares changed by the move: {}", changedSquares);
        return changedSquares;
    }

    private static class GameTree {

        private HistoryNode root;
        private HistoryNode focusedNode;

        public GameTree(HistoryNode root) {
            this.root = root;
            this.focusedNode = root;
        }

        public HistoryNode getRoot() {
            return root;
        }

        public HistoryNode getFocusedNode() {
            return focusedNode;
        }

        public void focusParent() {
            focusedNode = focusedNode.parent;
        }

        public void addNode(HistoryNode newNode) {
            //if currently focused node already has a child preceded by move in the node, just move the gocus
            for (HistoryNode h : focusedNode.getChildren()) {
                if (h.getMove().equals(newNode.getMove())) {
                    focusedNode = h;
                    return;
                }
            }
            focusedNode.addChild(newNode);
            focusedNode = newNode;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            histNodeToString(root, result);
            return result.toString();
        }

        private void histNodeToString(HistoryNode node, StringBuilder sb) {
            if (node.getMove() != null) {
                sb.append(node.getMove().toString()).append("\n");
            } else {
                sb.append("null move").append("\n");
            }
            switch (node.getChildren().size()) {
                case 0:
                    break;
                case 1:
                    histNodeToString(node.getChildren().get(0), sb);
                    break;
                default:
                    sb.append("{side line}");
                    histNodeToString(node.getChildren().get(0), sb);
                    break;
            }
        }

        public static class HistoryNode {

            //link to parent (to enable taking back of moves - moving to previous states)
            private final HistoryNode parent;
            //list of children
            private final List<HistoryNode> children;
            //Data
            private final Move move;
            private final GameState state;

            public HistoryNode(HistoryNode parent, Move move, GameState s) {
                this.parent = parent;
                this.move = move;
                this.state = s;
                children = new ArrayList<>();
            }

            public GameState getGameState() {
                return state;
            }

            public Move getMove() {
                return move;
            }

            public void addChild(HistoryNode child) {
                if (!children.contains(child)) {
                    children.add(child);
                }
            }

            public List<HistoryNode> getChildren() {
                return children;
            }

            public HistoryNode getParent() {
                return parent;
            }
        }
    }
}
