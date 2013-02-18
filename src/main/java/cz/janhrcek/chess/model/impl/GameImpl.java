/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.janhrcek.chess.model.impl;

import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.model.api.Game;
import cz.janhrcek.chess.model.api.GameState;
import cz.janhrcek.chess.model.api.GameStateFactory;
import cz.janhrcek.chess.model.api.IllegalMoveException;
import cz.janhrcek.chess.model.api.Move;
import java.util.ArrayList;
import java.util.List;

/**
 * Initial state of the game is set upon construction.
 *
 * @author jhrcek
 */
public class GameImpl implements Game {

    private GameStateFactory stateFactory;
    private GameTree gameTree;

    public GameImpl(String initialStateFen, GameStateFactory factory) throws InvalidFenException {
        this.stateFactory = factory;
        GameState initialState = stateFactory.create(initialStateFen);
        GameTree.HistoryNode initialNode = new GameTree.HistoryNode(null, null, initialState);
        this.gameTree = new GameTree(initialNode);
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
    //----------------------- PRIVATE IMPLEMENTATION ---------------------------

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
        }
    }
}
