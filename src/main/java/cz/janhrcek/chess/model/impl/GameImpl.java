package cz.janhrcek.chess.model.impl;

import cz.janhrcek.chess.FEN.Fen;
import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.gui.MoveListener;
import cz.janhrcek.chess.model.api.Game;
import cz.janhrcek.chess.model.api.GameBrowser;
import cz.janhrcek.chess.model.api.GameBrowserChangedEvent;
import cz.janhrcek.chess.model.api.GameListener;
import cz.janhrcek.chess.model.api.Position;
import cz.janhrcek.chess.model.api.PositionFactory;
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

    public GameImpl(String initialPositionfen) throws InvalidFenException {
        Position initialPosition = new Fen(initialPositionfen).toPosition();
        log.info("Creating new instance of Game using initial position \"{}\"", initialPositionfen);
        rootNode = new Node(null, null, initialPosition, NODE_ID_GENERATOR.getAndIncrement());
        browser = new GameBrowserImpl(new PositionFactoryImpl(new FIDERuleChecker()));
        id2Node = new HashMap<>();
        id2Node.put(rootNode.getId(), rootNode);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("<html><body>");
        nodeToString(rootNode, result);
        result.append("</body></html>");
        return result.toString();
    }

    @Override
    public GameBrowser getBrowser() {
        return browser;
    }

//--------------------------- PRIVATE IMPLEMENTATION ---------------------------
    private void nodeToString(Node node, StringBuilder sb) { //TODO - fix order of displaying variations
        String startTag = node.equals(browser.getFocusedNode()) ? "&nbsp;<a class=\"focus\" href=\"" : "&nbsp;<a href=\""; //We want to highlight the focused move
        sb.append(startTag).append(node.getId());

        if (node.getMove() != null) {
            sb.append("\">")
                    .append(node.toLan())
                    .append("</a>")
                    .append(node.getPosition().isWhiteToMove() ? "<br/>" : ""); //line break after black's move
        } else { //Case for root node (= initial position)- has no parent
            sb.append("\">Start</a><br/>");
        }

        if (node.getChildren().size() >= 1) {
            nodeToString(node.getChildren().get(0), sb);       //1) Main line
        }
        if (node.getChildren().size() > 1) {
            sb.append("(");                                        //2) Variations
            for (int i = 1; i < node.getChildren().size(); i++) {
                nodeToString(node.getChildren().get(i), sb);
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

        public GameBrowserImpl(PositionFactory positionFactory) {
            this.positionFactory = positionFactory;
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
                Node previous = focusedNode;
                makeMove(move);
                notifyListenersOfPositionChange(previous);
            } catch (IllegalMoveException ex) {
                log.info("The move selected is illegal.", ex);
            } catch (PieceNotPresentException ex) {
                log.info("The move selected has problem.", ex);
            }
        }

        @Override
        public void makeMove(Move newMove) throws PieceNotPresentException, IllegalMoveException {
            log.info("Trying to add {} to the game", newMove);

            //if currently focused node already has a child preceded by move in the node, just move the gocus     
            for (Node childNode : focusedNode.getChildren()) {
                if (childNode.getMove().equals(newMove)) {
                    log.debug("There is already a child with move {} -> just switching focus to that child, not adding the move", newMove);
                    focusedNode = childNode;
                    return;
                }
            }
            log.debug("Adding new Position using {}", newMove);
            Position positionAfterMove = positionFactory.create(getFocusedPosition(), newMove);
            Node newFocusedNode = new Node(focusedNode, newMove, positionAfterMove, NODE_ID_GENERATOR.getAndIncrement());
            id2Node.put(newFocusedNode.getId(), newFocusedNode);
            focusedNode.addChild(newFocusedNode);
            focusedNode = newFocusedNode;
        }

        @Override
        public Position getFocusedPosition() {
            return focusedNode.getPosition();
        }

        @Override
        public void focusInitialPosition() {
            log.info("Browsing game: Focusing initial position");
            Node previous = focusedNode;
            focusedNode = rootNode;
            notifyListenersOfPositionChange(previous);
        }

        @Override
        public void focusNextPosition() {
            if (focusedNode.children.size() > 0) {
                log.info("Browsing game: focusing focusing next position on current main line");
                Node previous = focusedNode;
                focusedNode = focusedNode.getChildren().get(0);
                notifyListenersOfPositionChange(previous);
            } else {
                log.info("Browsing game: can't focus child, we are already at the end of current line!");
            }
        }

        @Override
        public void focusPreviousPosition() {
            if (focusedNode.parent != null) { //we're currently not at root
                log.info("Browsing game: focusing previous position");
                Node previous = focusedNode;
                focusedNode = focusedNode.parent;
                notifyListenersOfPositionChange(previous);
            } else {
                log.info("Browsing game: can't focus previous position, we are already at the initial position!");
            }
        }

        @Override
        public void focusLastPosition() {
            Node previous = focusedNode;
            log.info("Browsing tree: moving to the end of the current line");
            while (focusedNode.children.size() > 0) {
                focusedNode = focusedNode.getChildren().get(0);
            }
            notifyListenersOfPositionChange(previous);
        }

        @Override
        public void focusPositionWithId(int id) {
            log.info("Browsing tree: focusing node with id = {}", id);
            Node previous = focusedNode;
            focusedNode = id2Node.get(id);
            notifyListenersOfPositionChange(previous);
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
        private void notifyListenersOfPositionChange(Node previouslyFocusedNode) {
            //TODO -focused node is null here, when new game is initialized with 
            if (!focusedNode.equals(previouslyFocusedNode)) {
                log.info("Focused Position has changed - notifying {} GameListener(s)", gameListeners.size());
                GameBrowserChangedEvent change = new GameBrowserChangedEvent(previouslyFocusedNode.getPosition(), getFocusedPosition());

                for (GameListener gameListener : gameListeners) {
                    gameListener.gameChanged(change);
                }
            } else {
                log.info("Focused position has not changed.");
            }
        }
        //
        private final List<GameListener> gameListeners;
        private final PositionFactory positionFactory;
        private final Logger log = LoggerFactory.getLogger(getClass());
    }

    /**
     * Constituent part of a game tree - holds Position and information about
     * parent / children positions.
     */
    private static class Node {

        private static final Logger log = LoggerFactory.getLogger(Node.class);
        //link to parent (to enable taking back of moves - moving to previous position)
        private final Node parent;
        //list of children
        private final List<Node> children;
        //Data
        private final Move move;
        private final Position position;
        private final int ID; //Unique ID withing a game

        public Node(Node parent, Move move, Position position, int id) {
            this.parent = parent;
            this.move = move;
            this.position = position;
            this.ID = id;
            this.children = new ArrayList<>();

        }

        public Position getPosition() {
            return position;
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

            String fullMoveNumber = getPosition().isWhiteToMove() ? "" : getPosition().getFullmoveNumber() + ".";
            String pieceLetter = move.getPiece().getSanName();
            String fromSquare = move.getFrom().toString().toLowerCase();
            boolean wasCapture = getParent().getPosition().getChessboard().getPiece(move.getTo()) != null;
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
