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
    private final Tree gameTree;
    private final List<GameListener> gameListeners;
    private static final Logger log = LoggerFactory.getLogger(GameImpl.class);

    public GameImpl(String initialStateFen, GameStateFactory factory) throws InvalidFenException {
        this.stateFactory = factory;
        GameState initialState = stateFactory.create(initialStateFen);
        Tree.Node initialHistoryNode = new Tree.Node(null, null, initialState);
        this.gameTree = new Tree(initialHistoryNode);
        gameListeners = new ArrayList<>();
    }

    @Override
    public GameState getInitialState() {
        return gameTree.getRoot().getGameState();
    }

    @Override
    public void makeMove(Move move) throws ChessboardException, IllegalMoveException {
        log.info("Making move {}", move);
        GameState stateBeforeMove = getFocusedState();
        GameState stateAfterMove = stateFactory.create(stateBeforeMove, move);
        Tree.Node nodeAfterMove = new Tree.Node(gameTree.getFocusedNode(), move, stateAfterMove);
        gameTree.addNode(nodeAfterMove);
    }

    @Override
    public GameState getFocusedState() {
        return gameTree.getFocusedNode().getGameState();
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

    private static class Tree {

        private static final Logger log = LoggerFactory.getLogger(Tree.class);
        private Node root;
        private Node focusedNode;

        public Tree(Node root) {
            this.root = root;
            this.focusedNode = root;
        }

        public Node getRoot() {
            return root;
        }

        public Node getFocusedNode() {
            return focusedNode;
        }

        private void focusRoot() {
            log.info("Browsing tree: focusing root node");
            focusedNode = root;
        }

        public void focusParent() {
            if (focusedNode.parent != null) { //if we are not already at the root
                log.info("Browsing tree: focusing parent of the current node");
                focusedNode = focusedNode.parent;
            } else {
                log.info("Browsing tree: can't focus parent, we are already at the root of the tree!");
            }
        }

        private void focusFirstChild() {
            if (focusedNode.children.size() > 0) {
                log.info("Browsing tree: focusing first child of the current node");
                focusedNode = focusedNode.getChildren().get(0);
            } else {
                log.info("Browsing tree: can't focus child, we are already at the end of current line!");
            }
        }

        private void focusLeaf() {
            log.info("Browsing tree: moving to the end of the current line");
            while (focusedNode.children.size() > 0) {
                focusedNode = focusedNode.getChildren().get(0);
            }
        }

        public void addNode(Node newNode) {
            //if currently focused node already has a child preceded by move in the node, just move the gocus
            for (Node childNode : focusedNode.getChildren()) {
                if (childNode.getMove().equals(newNode.getMove())) {
                    log.debug("There is already a child with move {} -> just switching focus to that child, not adding the move", newNode.getMove());
                    focusedNode = childNode;
                    return;
                }
            }
            log.debug("Adding new node for move {}", newNode.getMove());
            focusedNode.addChild(newNode);
            focusedNode = newNode;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            histNodeToString(root, result, "");
            return result.toString();
        }

        private void histNodeToString(Node node, StringBuilder sb, String level) {

            if (node.getMove() != null) {
                sb.append(level).append(node.getMove().toString()).append("\n");
            } else {
                sb.append(level).append("null move").append("\n");
            }

            switch (node.getChildren().size()) {
                case 0:
                    break;
                case 1:
                    histNodeToString(node.getChildren().get(0), sb, level);
                    break;
                default:
                    sb.append(level).append("  ").append("(\n");
                    for (int i = 1; i < node.getChildren().size(); i++) {
                        sb.append(level);
                        histNodeToString(node.getChildren().get(i), sb, level + "  ");
                        sb.append(level).append("  ").append(";\n");
                    }
                    sb.append(level).append("  ").append(")\n");

                    histNodeToString(node.getChildren().get(0), sb, level);
                    break;
            }
        }

        public static class Node {

            private static final Logger log = LoggerFactory.getLogger(Node.class);
            //link to parent (to enable taking back of moves - moving to previous states)
            private final Node parent;
            //list of children
            private final List<Node> children;
            //Data
            private final Move move;
            private final GameState state;

            public Node(Node parent, Move move, GameState s) {
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

            public void addChild(Node child) {
                log.debug("Adding new child node for move {}", child.getMove());
                children.add(child);
            }

            public List<Node> getChildren() {
                return children;
            }

            public Node getParent() {
                return parent;
            }
        }
    }
}
