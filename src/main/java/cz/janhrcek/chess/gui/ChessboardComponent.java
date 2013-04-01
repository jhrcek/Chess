package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.model.api.GameBrowser;
import cz.janhrcek.chess.model.api.GameBrowserChangedEvent;
import cz.janhrcek.chess.model.api.GameListener;
import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.Promotion;
import static cz.janhrcek.chess.model.api.enums.Piece.*;
import cz.janhrcek.chess.model.api.enums.Piece;
import cz.janhrcek.chess.model.api.enums.Square;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GUI component that displays visible part of currently focused position of
 * underlying GameBrowser - as a chessboard with pieces. This component is
 * resizeable (resizing it will cause change of the size of the chessboard).
 * This component enables user to select moves by clicking on it.
 *
 * @author Jan Hrcek
 */
public final class ChessboardComponent extends JComponent implements GameListener {

    /**
     * Creates new instance of ChessboardComponent. The Component will use
     * provided GameBrowser as its model, from which it gets currently focused
     * Position and displays it.
     *
     * @param gameBrowser Browser that provides a window into underlying game
     */
    public ChessboardComponent(GameBrowser gameBrowser) {
        enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.COMPONENT_EVENT_MASK);
        setBorder(new LineBorder(Color.BLACK, 1));
        setGameBrowser(gameBrowser);
    }

    public void setGameBrowser(GameBrowser gameBrowser) {
        this.gameBrowser = Objects.requireNonNull(gameBrowser, "gameBrowser must not be null!");
        gameBrowser.addGameListener(this); //To make ChessboardComponent listen to GameBrowserChanged events
        addMoveSelectedListener((MoveListener) gameBrowser); //To make GameBrowser listen to MoveSelected events
    }

    /**
     * This method registers any object which implements MoveListener interface
     * as a listener to the instance of this class. When some MoveSeletesEvent
     * occurs, than given object will be notified of the occurrence.
     *
     * @param listener the object to be registered as a MoveListener
     */
    public void addMoveSelectedListener(MoveListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener can't be null!");
        }
        listeners.clear(); //remove all previous listeners
        listeners.add(listener);
    }

    /**
     * Unregisters given listener, so that it will no longer notified of
     * MoveSelectedEvents.
     *
     * @param listener the listener we want to unregister
     */
    public void removeMoveSelectedEventListener(MoveListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener can't be null!");
        }
        listeners.remove(listener);
    }

    /**
     * Paints component in the GUI.
     *
     * @param g the graphics object using which the component will be painted
     */
    @Override
    protected void paintComponent(Graphics g) {
        log.debug("Calling paintComponent(Graphics g)");
        if (isOpaque()) { //paint background
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        //BORDER = g.getFontMetrics().getHeight() + 4;

        if (sizeOfSquare >= 8) {
            paintCaptions(g, sizeOfSquare, deltaX, deltaY);
            paintSquares(g, sizeOfSquare, deltaX, deltaY);
        }
    }

    /**
     * This method handles user interaction with OldChessboardComponent via
     * mouse clicking.
     *
     * @param mouseEvent eventObject representing some mouse event
     */
    @Override
    protected void processMouseEvent(MouseEvent mouseEvent) {
        //In this method we are only interested in MOUSE_CLICKED events
        if (mouseEvent.getID() != MouseEvent.MOUSE_CLICKED) {
            return;
        }

        //Find out which square user clicked on
        Square clickedSquare = getClickedSquare(mouseEvent);
        log.debug("Processing mouse click: User clicked {}", (clickedSquare == null ? "outside of Chessboard" : ("on square " + clickedSquare)));

        //Finite state machine implementation that tries to constructs 
        //a move instance based on user clicks on the chessboard component
        if (selectedFromSquare == null) { //"from" is not yet selected
            if (clickedSquare != null // AND user clicked on some square
                    && gameBrowser.getFocusedPosition().getChessboard().getPiece(clickedSquare) != null) { // AND there is a some piece on the clicked square
                log.info("  User chooses {} as \"from\" square", clickedSquare);
                setAndHighlightFromSquare(clickedSquare);
            }
        } else { //"from" has been chosen previously
            if (clickedSquare != null && clickedSquare != selectedFromSquare) {
                log.info("  User chooses {} as \"to\" square", clickedSquare);
                //Both "from" and "to" are selected -> Create Move instance and notify all MoveListeners
                Piece movingPiece =
                        gameBrowser.getFocusedPosition().getChessboard().getPiece(selectedFromSquare);
                Move selectedMove =
                        createSelectedMove(movingPiece,
                        selectedFromSquare, //from
                        clickedSquare); //to
                log.info("  User constructed {}, notifying {} listener(s)", selectedMove, listeners.size());
                for (MoveListener listener : listeners) {
                    listener.moveSelected(selectedMove);
                }
            } else {
                log.info("  User cancels the selection of \"from\" square.");
                unhighlightFromSquare();
            }
        }
    }

    private void setAndHighlightFromSquare(Square s) {
        selectedFromSquare = s;
        paintRedBorderAroundSquare(s);
    }

    private void unhighlightFromSquare() {
        //Cancel the red square around selected square
        //(because user either selected a "to" square and constructed the move,
        // or he canceled a selection of "from" by clicking the "from" square
        // again or clicking elsewhere on the chessboard component)
        Square tmpSq = selectedFromSquare;
        selectedFromSquare = null;
        repaintSquare(tmpSq);
    }

    /**
     * Loads images of pieces of appropriate size, whenever this component is
     * resized.
     *
     * @param ce component event which will be processed by this method (we only
     * process ComponentEvent.COMPONENT_RESIZED events)
     */
    @Override
    protected void processComponentEvent(ComponentEvent ce) {
        //we only care about events representing that component has been resized
        if (ce.getID() != ComponentEvent.COMPONENT_RESIZED) {
            return;
        }

        Component source = ce.getComponent();
        log.info("Component resized - new size: {} x {}", source.getWidth(), source.getHeight());
        //from the new size of component determine size of one icon
        sizeOfSquare = (Math.min(source.getWidth(), source.getHeight()) - 2 * BORDER) / 8;
        deltaX = (source.getWidth() - (sizeOfSquare * 8)) / 2;
        deltaY = (source.getHeight() - (sizeOfSquare * 8)) / 2;
        if (sizeOfSquare > 8) {
            squareImages.setImageSize(sizeOfSquare);
            repaint(); //repaints component with new pieces icons
        }
    }

    /**
     * If set to true user can select moves by clicking on the component. If set
     * to false user will be unable to change what is displayed on the
     * chessboard by clicking on it.
     *
     * @param clickable set to true, if you want to enable user to select moves
     * by clicking on the component set to false if you want to ignore all
     * clicks on the component
     */
    public void setClickable(Boolean clickable) {
        if (clickable == true) {
            enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        } else {
            disableEvents(AWTEvent.MOUSE_EVENT_MASK);
        }
    }

    @Override
    public void gameChanged(GameBrowserChangedEvent event) {
        log.info("ChessboardComponent caught {}, updating state...", event.getClass());

        for (Square changedSquare : event.getChangedSquares()) {
            repaintSquare(changedSquare);
        }
        if (selectedFromSquare != null) { //if position is changed by other means (e.g. by going to the initial position using some button, we need to cancel selected from Square
            unhighlightFromSquare();
        }
    }
//------------------------------------------------------------------------
//------------------------PRIVATE IMPLEMENTATION--------------------------
//------------------------------------------------------------------------
    private static final Logger log = LoggerFactory.getLogger(ChessboardComponent.class);
    /**
     * Number of pixels from the edge of chessboard to the edge of the component
     * in which there are captions displayed.
     */
    private final int BORDER = 19;
    /**
     * Size (in pixels) of one one square on the graphics representation of the
     * chessboard.
     */
    private int sizeOfSquare;
    /**
     * Number of pixels from the left edge of the component to the left edge of
     * the chessBoard.
     */
    private int deltaX;
    /**
     * Number of pixels from the top edge of the component to the top left edge
     * of the chessBoard.
     */
    private int deltaY;
    /**
     * Represents move-selection state information. If this square is not null
     * then we know user has selected some square (with some piece), from which
     * he may want to move that piece.
     */
    private Square selectedFromSquare = null;
    /**
     * Collection of objects, which will be notified of the selection of move
     * when some move is selected.
     */
    private Collection<MoveListener> listeners =
            new HashSet<>();
    private SquareImageFactory squareImages = new SquareImageFactory(10);
    /**
     * Instance of GameBrowser, from which we can get all information
     * (displayable on chessboard) about the state of the game.
     */
    private GameBrowser gameBrowser;

    /**
     * Paint the squares of the chessboard, which this component represents.
     *
     * @param g Graphics object with which the squares will be painted
     * @param cellSize The size in pixels of the square on the resulting
     * chessboard
     * @param deltaX Number of pixels from left edge of the component to the
     * left edge of the chessboard
     * @param deltaY Number of pixels from top edge of the component to the top
     * edge of the chessboard
     */
    private void paintSquares(Graphics g, int cellSize, int deltaX, int deltaY) {
        log.debug("  Calling paintSquares()");
        for (Square sq : Square.values()) {
            //Piece pieceOnSqare = model.getChessboard().getPiece(sq);
            Piece pieceOnSqare = gameBrowser.getFocusedPosition().getChessboard().getPiece(sq);
            squareImages.getSquareImage(pieceOnSqare, sq.isLight()).paintIcon(this, g,
                    deltaX + sq.getFile() * cellSize,
                    deltaY + (7 - sq.getRank()) * cellSize);
        }

        //nakresli cerneny obdelnik kolem selectedFromSquare
        //(pokud nejaky vybrany je)
        if (selectedFromSquare != null) {
            g.setColor(Color.RED);
            g.drawRect(deltaX + selectedFromSquare.getFile() * cellSize,
                    deltaY + (7 - selectedFromSquare.getRank()) * cellSize,
                    cellSize - 1,
                    cellSize - 1);
            g.drawRect(deltaX + selectedFromSquare.getFile() * cellSize + 1,
                    deltaY + (7 - selectedFromSquare.getRank()) * cellSize + 1,
                    cellSize - 3,
                    cellSize - 3);
        }

        //nakresli cerny obdelnik kolem sacovnice
        g.setColor(Color.BLACK);
        g.drawRect(deltaX, deltaY, cellSize * 8, cellSize * 8);
    }

    /**
     * Paints captions (names of files and ranks) around the chessboard.
     *
     * @param g Graphics object with which the squares will be painted
     * @param cellSize The size in pixels of the square on the resulting
     * chessboard
     * @param deltaX Number of pixels from left edge of the component to the
     * left edge of the chessboard
     * @param deltaY Number of pixels from top edge of the component to the top
     * edge of the chessboard
     */
    private void paintCaptions(Graphics g, int cellSize, int deltaX, int deltaY) {
        log.debug("  Calling paintCaptions()");
        for (int i = 0; i < 8; i++) {
            // column
            String letter = String.valueOf((char) ('A' + i));
            double letterWidth = g.getFontMetrics().getStringBounds(
                    letter, g).getWidth();
            int offsetX = (int) (i * cellSize + deltaX
                    + (cellSize - letterWidth) / 2);
            g.drawString(letter, offsetX, -2 + deltaY);
            g.drawString(letter, offsetX, cellSize * 8 + deltaY
                    + g.getFontMetrics().getHeight());
            // row
            letter = String.valueOf(i + 1);
            Rectangle2D letterBounds = g.getFontMetrics().getStringBounds(
                    letter, g);
            int offsetY = (int) ((8 - i) * cellSize + deltaY
                    - (cellSize - letterBounds.getHeight()) / 2);
            g.drawString(letter,
                    deltaX - (int) letterBounds.getWidth() - 4,
                    offsetY);
            g.drawString(letter,
                    deltaX + 4 + cellSize * 8,
                    offsetY);
        }
    }

    /**
     * Repaints rectangle that corresponds to given square on the chessboard.
     *
     * @param s the square which we want to repaint
     */
    private void repaintSquare(Square s) {
        log.debug("Repaint square {}", s);
        Graphics g = getGraphics();
        Piece pieceOnSq = gameBrowser.getFocusedPosition().getChessboard().getPiece(s);

        squareImages.getSquareImage(pieceOnSq, s.isLight()).paintIcon(this, g,
                deltaX + s.getFile() * sizeOfSquare,
                deltaY + (7 - s.getRank()) * sizeOfSquare);

        if (s.equals(selectedFromSquare)) {
            paintRedBorderAroundSquare(s);
        }

        //prekresli cerny obdelnik kolem sachovnice
        g.setColor(Color.BLACK);
        g.drawRect(deltaX, deltaY, sizeOfSquare * 8, sizeOfSquare * 8);
    }

    private void paintRedBorderAroundSquare(Square s) {
        Graphics g = getGraphics();
        Color previousColor = g.getColor(); //set to restore the color after painting
        g.setColor(Color.RED);
        g.drawRect(deltaX + s.getFile() * sizeOfSquare,
                deltaY + (7 - s.getRank()) * sizeOfSquare,
                sizeOfSquare - 1,
                sizeOfSquare - 1);
        g.drawRect(deltaX + s.getFile() * sizeOfSquare + 1,
                deltaY + (7 - s.getRank()) * sizeOfSquare + 1,
                sizeOfSquare - 3,
                sizeOfSquare - 3);
        g.setColor(previousColor);
    }

    /**
     * Returns the square that corresponds to a mouse click on the component.
     * For example if user clicks on the square e3, then Square.E3 object is
     * returned. if user clicks on some place on this component, that does not
     * correspond to any square, than null is returned.
     *
     * @param event mouse event that holds the information about the mouse
     * click, for which we want to find corresponding square
     * @return the Square object corresponding to clicked square of null if no
     * square was clicked on.
     */
    private Square getClickedSquare(MouseEvent event) {
        if (event == null) {
            throw new NullPointerException("event can't be null!");
        }

        int x = event.getX();
        int y = event.getY();
        int columnIndex;
        int rankIndex;

        //Determine column index of clicked square on the chessboard
        if (x > deltaX && x < (deltaX + 8 * sizeOfSquare)) {
            columnIndex = (x - deltaX) / sizeOfSquare;
        } else {
            columnIndex = -1;
        }

        //Determine rank index of clicked square on the chessboard
        if (y > deltaY && y < (deltaY + 8 * sizeOfSquare)) {
            rankIndex = 7 - ((y - deltaY) / sizeOfSquare);
        } else {
            rankIndex = -1;
        }

        if (columnIndex == -1 || rankIndex == -1) {
            return null;
        } else {
            return Square.getSquare(columnIndex, rankIndex);
        }
    }

    /**
     * For given piece and two squares creates Move object which represents the
     * move of the piece from one square to the other square. This method
     * handles the promotion-piece selection in the case that the three
     * parameters represent pawn promotion.
     *
     * @param piece the piece that moves
     * @param from the square from which it moves
     * @param to the square to which hit moves
     * @return MoveInfo object representing move of given piece from given
     * square to given square
     */
    private Move createSelectedMove(Piece piece, Square from, Square to) {
        //If it is pawn promotion we must must force user to choose to what piece the pawn should be promoted
        if ((piece.equals(WHITE_PAWN) && from.getRank() == 6 && to.getRank() == 7)
                || (piece.equals(BLACK_PAWN) && from.getRank() == 1 && to.getRank() == 0)) {

            Piece[] potentialPromoPieces = piece.isWhite()
                    ? new Piece[]{WHITE_QUEEN, WHITE_ROOK, WHITE_BISHOP, WHITE_KNIGHT}
                    : new Piece[]{BLACK_QUEEN, BLACK_ROOK, BLACK_BISHOP, BLACK_KNIGHT};

            //Display options for user as icons in OptionDialog
            ImageIcon[] optionImages = new ImageIcon[4];
            boolean isBackgroundLight = true; //Determines colog or background on image icon
            int i = 0;
            for (Piece p : potentialPromoPieces) {
                optionImages[i++] = squareImages.getSquareImage(p, isBackgroundLight);
                isBackgroundLight = !isBackgroundLight;
            }

            int chosenPiece = //Will be used as index to potentialPromoPieces
                    JOptionPane.showOptionDialog(getTopLevelAncestor(),
                    "What do you want to promote this pawn to?",
                    "Pawn promotion",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    optionImages,
                    optionImages[0]);

            Piece promoPiece = potentialPromoPieces[chosenPiece];
            return new Promotion(piece, from, to, promoPiece);
        } else { //It is not pawn promotion
            return new Move(piece, from, to);
        }
    }
}
