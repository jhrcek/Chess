package cz.janhrcek.chess.model.impl;

import com.google.inject.Inject;
import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.guice.FenString;
import cz.janhrcek.chess.model.api.GameState;
import cz.janhrcek.chess.model.api.GameStateFactory;
import cz.janhrcek.chess.model.api.IllegalMoveException;
import cz.janhrcek.chess.model.api.Move;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jhrcek
 */
public class Game {

    @Inject
    public Game(GameStateFactory gsf, @FenString String initialStateFen) throws InvalidFenException {
        this.stateFactory = gsf;
        GameState initialState = stateFactory.create(initialStateFen);
        this.rootNode = new Node(null, null, initialState);
        this.focusedNode = rootNode;
    }

    public GameState getRootState() {
        return rootNode.getGameState();
    }

    public GameState getFocusedState() {
        return focusedNode.getGameState();
    }

    void focusRoot() {
        log.info("Browsing tree: focusing root node");
        focusedNode = rootNode;
    }

    public void focusParent() {
        if (focusedNode.parent != null) {
            //if we are not already at the root
            log.info("Browsing tree: focusing parent of the current node");
            focusedNode = focusedNode.parent;
        } else {
            log.info("Browsing tree: can't focus parent, we are already at the root of the tree!");
        }
    }

    void focusFirstChild() {
        if (focusedNode.children.size() > 0) {
            log.info("Browsing tree: focusing first child of the current node");
            focusedNode = focusedNode.getChildren().get(0);
        } else {
            log.info("Browsing tree: can't focus child, we are already at the end of current line!");
        }
    }

    void focusLeaf() {
        log.info("Browsing tree: moving to the end of the current line");
        while (focusedNode.children.size() > 0) {
            focusedNode = focusedNode.getChildren().get(0);
        }
    }

    public void addMove(Move newMove) throws ChessboardException, IllegalMoveException {
        //if currently focused node already has a child preceded by move in the node, just move the gocus
        for (Node childNode : focusedNode.getChildren()) {
            if (childNode.getMove().equals(newMove)) {
                log.debug("There is already a child with move {} -> just switching focus to that child, not adding the move", newMove);
                focusedNode = childNode;
                return;
            }
        }
        log.debug("Adding new GameState using {}", newMove);
        GameState stateAfterMove = stateFactory.create(getFocusedState(), newMove);
        Node newFocusedNode = new Node(focusedNode, newMove, stateAfterMove);
        focusedNode.addChild(newFocusedNode);
        focusedNode = newFocusedNode;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        histNodeToString(rootNode, result, "");
        return result.toString();
    }

//--------------------------- PRIVATE IMPLEMENTATION ---------------------------
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
    private static final Logger log = LoggerFactory.getLogger(Game.class);
    private final GameStateFactory stateFactory;
    private final Node rootNode;
    private Node focusedNode;

    private static class Node {

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
