package cz.janhrcek.chess.model.impl;

import cz.janhrcek.chess.FEN.FenParser;
import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.gui.MoveListener;
import cz.janhrcek.chess.model.api.Game;
import cz.janhrcek.chess.model.api.GameBrowser;
import cz.janhrcek.chess.model.api.GameChangedEvent;
import cz.janhrcek.chess.model.api.GameListener;
import cz.janhrcek.chess.model.api.GameState;
import cz.janhrcek.chess.model.api.GameStateFactory;
import cz.janhrcek.chess.rules.IllegalMoveException;
import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.Promotion;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jhrcek
 */
public class GameImpl implements Game {

    public GameImpl(String initialStateFen) throws InvalidFenException {
        GameState initialState = new FenParser().fenToGameState(initialStateFen);
        this.rootNode = new Node(null, null, initialState, NODE_ID_GENERATOR.getAndIncrement());
        this.browser = new GameBrowserImpl(new GameStateFactoryImpl(new FIDERuleChecker()));
        this.id2Node = new HashMap<>();
        id2Node.put(rootNode.getId(), rootNode);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("<html><body>");
        histNodeToString(rootNode, result);
        result.append("</body></html>");
        return result.toString();
    }

    @Override
    public GameBrowser getBrowser() {
        return browser;
    }

//--------------------------- PRIVATE IMPLEMENTATION ---------------------------
    private void histNodeToString(Node node, StringBuilder sb) { //TODO - fix order of displaying variations
        String startTag = node.equals(browser.getFocusedNode()) ? "&nbsp;<a class=\"focus\" href=\"" : "&nbsp;<a href=\""; //We want to highlight the focused move
        sb.append(startTag).append(node.getId());

        if (node.getMove() != null) {
            sb.append("\">")
                    .append(node.toLan())
                    .append("</a>")
                    .append(node.getGameState().isWhiteToMove() ? "<br/>" : ""); //line break after black's move
        } else { //Case for root node (= initial state of the game)- has no parent
            sb.append("\">Start</a><br/>");
        }

        if (node.getChildren().size() >= 1) {
            histNodeToString(node.getChildren().get(0), sb);       //1) Main line
        }
        if (node.getChildren().size() > 1) {
            sb.append("(");                                        //2) Variations
            for (int i = 1; i < node.getChildren().size(); i++) {
                histNodeToString(node.getChildren().get(i), sb);
                sb.append(";");
            }
            sb.append(")");
        }
    }
    private static final Logger log = LoggerFactory.getLogger(GameImpl.class);
    private final Node rootNode;
    private final GameBrowserImpl browser;
    private final Map<Integer, Node> id2Node;
    private final AtomicInteger NODE_ID_GENERATOR = new AtomicInteger(0);

    private class GameBrowserImpl implements GameBrowser, MoveListener {

        private Node focusedNode;

        public GameBrowserImpl(GameStateFactory stateFactory) {
            this.stateFactory = stateFactory;
            gameListeners = new ArrayList<>();
            focusedNode = GameImpl.this.rootNode;
        }

        public Node getFocusedNode() {
            return focusedNode;
        }

        @Override
        public void moveSelected(Move move) {
            log.info("Got notification from GUI - move selected: {}", move);
            try {
                GameState previous = getFocusedState();
                makeMove(move);
                GameState current = getFocusedState();
                notifyListenersOfStateChange(previous, current);
            } catch (IllegalMoveException ex) {
                log.info("The move selected is illegal.", ex);
            } catch (PieceNotPresentException ex) {
                log.info("The move selected has problem.", ex);
            }
        }

        @Override
        public GameState getInitialState() {
            return rootNode.getGameState();
        }

        @Override
        public void makeMove(Move newMove) throws PieceNotPresentException, IllegalMoveException {
            log.info("Trying to make move {}", newMove);

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
            Node newFocusedNode = new Node(focusedNode, newMove, stateAfterMove, NODE_ID_GENERATOR.getAndIncrement());
            id2Node.put(newFocusedNode.getId(), newFocusedNode);
            focusedNode.addChild(newFocusedNode);
            focusedNode = newFocusedNode;
        }

        @Override
        public GameState getFocusedState() {
            return focusedNode.getGameState();
        }

        @Override
        public void focusInitialState() {
            log.info("Browsing game: Focusing initial state");
            GameState previous = focusedNode.getGameState();

            focusedNode = rootNode;

            GameState current = focusedNode.getGameState();
            notifyListenersOfStateChange(previous, current);
        }

        @Override
        public void focusNextState() {
            log.info("Focusing next state");
            GameState previous = focusedNode.getGameState();

            if (focusedNode.children.size() > 0) {
                log.info("Browsing game: focusing first child of the current node");
                focusedNode = focusedNode.getChildren().get(0);
            } else {
                log.info("Browsing game: can't focus child, we are already at the end of current line!");
            }


            GameState current = focusedNode.getGameState();
            notifyListenersOfStateChange(previous, current);
        }

        @Override
        public void focusPreviousState() {
            GameState previous = getFocusedState();

            if (focusedNode.parent != null) {
                //if we are not already at the root
                log.info("Browsing game: focusing parent of the current node");
                focusedNode = focusedNode.parent;
            } else {
                log.info("Browsing game: can't focus parent, we are already at the root of the tree!");
            }

            GameState current = getFocusedState();
            notifyListenersOfStateChange(previous, current);
        }

        @Override
        public void focusLastState() {
            log.info("Focusing last state");
            GameState previous = getFocusedState();

            log.info("Browsing tree: moving to the end of the current line");
            while (focusedNode.children.size() > 0) {
                focusedNode = focusedNode.getChildren().get(0);
            }

            GameState current = getFocusedState();
            notifyListenersOfStateChange(previous, current);
        }

        @Override
        public void focusStateWithId(int id) {
            log.info("Focusing state with id = {}", id);
            GameState previous = getFocusedState();

            log.info("Browsing tree: focusing node with id = {}", id);
            focusedNode = id2Node.get(id);



            GameState current = getFocusedState();
            notifyListenersOfStateChange(previous, current);
        }

        @Override
        public String toString() {
            return GameImpl.this.toString();
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
        private void notifyListenersOfStateChange(GameState previousState, GameState currentState) {
            log.info("Focused GameState was changed - notifying {} GameListener(s)", gameListeners.size());
            GameChangedEvent change = new GameChangedEvent(previousState, currentState);
            for (GameListener gameListener : gameListeners) {
                gameListener.gameChanged(change);
            }
        }
        //
        private final List<GameListener> gameListeners;
        private final GameStateFactory stateFactory;
        private final Logger log = LoggerFactory.getLogger(getClass());
    }

    /**
     * Constituent part of a game tree - holds GameImpl state and information
     * about parent / children states.
     */
    private static class Node {

        private static final Logger log = LoggerFactory.getLogger(Node.class);
        //link to parent (to enable taking back of moves - moving to previous states)
        private final Node parent;
        //list of children
        private final List<Node> children;
        //Data
        private final Move move;
        private final GameState state;
        private final int ID; //Unique ID withing a game

        public Node(Node parent, Move move, GameState s, int id) {
            this.parent = parent;
            this.move = move;
            this.state = s;
            this.ID = id;
            this.children = new ArrayList<>();

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

        public int getId() {
            return ID;
        }

        /**
         * Takes the node and converts the move, that led to this node to Long
         * Algebraic Notation (LAN).
         *
         * @return Long Algebraic Notation representation of the move
         */
        private String toLan() {
            if (getParent() == null) {
                throw new IllegalArgumentException("Cannot convert root node to LAN!");
            }

            StringBuilder result = new StringBuilder();

            String fullMoveNumber = getGameState().isWhiteToMove() ? "" : getGameState().getFullmoveNumber() + ".";
            Move move = getMove();

            String pieceLetter = move.getPiece().getSanName();
            String fromSquare = move.getFrom().toString().toLowerCase();
            boolean wasCapture = getParent().getGameState().getPosition().getPiece(move.getTo()) != null;
            String toSquare = move.getTo().toString().toLowerCase();
            String promotion = (move instanceof Promotion) ? "=" + ((Promotion) move).getPromoPiece().getSanName() : "";

            result.append(fullMoveNumber)
                    .append(pieceLetter)
                    .append(fromSquare)
                    .append(wasCapture ? "x" : "-")
                    .append(toSquare)
                    .append(promotion);

            return result.toString();
        }
    }
}
