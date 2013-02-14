package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.enums.Piece;
import cz.janhrcek.chess.model.api.Promotion;
import cz.janhrcek.chess.model.api.enums.Square;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GUI component ChessboardComponent represents chessboard with pieces. This
 * component is resizable (resizing it will cause change of the size of the
 * chessboard). This component enables user to select moves by clicking on it.
 *
 * @author xhrcek
 */
public class ChessboardComponent extends JComponent {

    /**
     * Creates new instance of ChessboardComponent.
     *
     * @param model the model which represents state of the game, which this
     * component displays
     */
    public ChessboardComponent(GameModel model) {
        if (model == null) {
            throw new NullPointerException("model can't be null!");
        }
        setModel(model);
        enableEvents(java.awt.AWTEvent.MOUSE_EVENT_MASK);
        enableEvents(java.awt.AWTEvent.COMPONENT_EVENT_MASK);
    }

    /**
     * Sets model representing state of game displayed by this component.
     *
     * @param model model representing state of the game which we want to
     * display using this component
     */
    public void setModel(GameModel model) {
        if (this.model != null) {
            this.model.removeGameModelListener(myGameStateModelListener);
        }
        this.model = model;
        model.addGameModelListener(myGameStateModelListener);
    }

    /**
     * Returns model representing state of game diplayed by the component.
     *
     * @return the model representing state of the game which is displayed by
     * this component
     */
    public GameModel getModel() {
        return model;
    }

    /**
     * This method registers any object which implements
     * MoveSelectedEventListener interface as a listener to the instance of this
     * class. When some MoveSeletesEvent occurs, than given object will be
     * notified of the occurrence.
     *
     * @param listener the object to be registered as a
     * MoveSelectedEventListener
     */
    public void addMoveSelectedEventListener(MoveSelectedEventListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener can't be null!");
        }
        listeners.add(listener);
    }

    /**
     * Unregisters given listener, so that it will no longer notified of
     * MoveSelectedEvents.
     *
     * @param listener the listener we want to unregister
     */
    public void removeMoveSelectedEventListener(MoveSelectedEventListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener can't be null!");
        }
        listeners.remove(listener);
    }

    /**
     * Paints component in the gui.
     *
     * @param g the graphics object using which the component will be painted
     */
    @Override
    protected void paintComponent(Graphics g) {
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
     * This method handles user ineraction with ChessboardComponent via mouse
     * clicking.
     *
     * @param e eventObject representing some mouse event
     */
    @Override
    protected void processMouseEvent(MouseEvent e) {
        //v teto metode se nestarame o ZADNE JINE mouseEventy, nez MOUSE_CLICKED
        if (e.getID() != MouseEvent.MOUSE_CLICKED) {
            return;
        }
        //zjisti na ktere pole uzivatel klikl
        Square clickedSquare = getCorrespondingSquare(e);

        //implementace konecneho automatu ktera postupne konstruuje/rusi
        //tah ktery uzivatel postupne zadava klikanim na ChessboardComponen
        if (selectedFromSquare != null) { //"from" je uz vybran
            if (clickedSquare != null && clickedSquare != selectedFromSquare) {
                //vybran "from" i "to" -> vytvorime MoveInfo reprezentujici ten tah
                Piece movingPiece =
                        model.getChessboard().getPiece(selectedFromSquare);
                Move selectedMove =
                        createSelectedMove(movingPiece,
                        selectedFromSquare, //from
                        clickedSquare); //to
                MoveSelectedEvent msevent =
                        new MoveSelectedEvent(this, selectedMove);
                //vsem posluchacum dam vedet, ze byl vybran nejaky tah
                for (MoveSelectedEventListener listener : listeners) {
                    listener.moveSelected(msevent);
                }
                //zrusime cerveny ctverce kolem selectedFromSquare
                Square tmpSq = selectedFromSquare;
                selectedFromSquare = null;
                repaintSquare(tmpSq);
            } else { //ruseni tahu (klik na stejny sq nebo mimo sachovnici)
                //zrusime cerveny ctverec kolem selectedFromSquare
                Square tmpSq = selectedFromSquare;
                selectedFromSquare = null;
                repaintSquare(tmpSq);
            }
        } else { //from jeste neni vybran
            //povolime vybrat nejaky from square POUZE TEHDY, je-li na nem
            //nejaka figura (JAKEKOLI BARVY)
            if (clickedSquare != null
                    && model.getChessboard().getPiece(clickedSquare) != null) {
                selectedFromSquare = clickedSquare;
                //nakreslime cerveny ctverec kolem selectedFromSquare
                repaintSquare(selectedFromSquare);
            }
        }
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
     * @param flag set to true, if you want to enable user to select moves by
     * clicking on the component set to false if you want to ignore all clicks
     * on the component
     */
    public void setClickable(Boolean flag) {
        if (flag == true) {
            enableEvents(java.awt.AWTEvent.MOUSE_EVENT_MASK);
        } else {
            disableEvents(java.awt.AWTEvent.MOUSE_EVENT_MASK);
        }
    }
//------------------------------------------------------------------------
//------------------------PRIVATE IMPLEMENTATION--------------------------
//------------------------------------------------------------------------
    private static final Logger LOG = LoggerFactory.getLogger(ChessboardComponent.class);
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
    private Collection<MoveSelectedEventListener> listeners =
            new HashSet<MoveSelectedEventListener>();
    SquareImageFactory squareImages = new SquareImageFactory(10);
    /**
     * Instance of GameModel, from which we can get all information (displayable
     * on chessboard) about the state of the game.
     */
    private GameModel model;
    /**
     * Whenever GameState is changed, this listener will do appropriate changes
     * to this component, so that displayed state corresponds to current
     * GameState.
     */
    private GameModelListener myGameStateModelListener =
            new GameModelListener() {
                @Override
                public void gameStateChanged(GameModelEvent event) {
                    //prekreslime vsechny pole jejichz obsah se v posledni
                    //zmene stavu zmenil
                    for (Square changedSquare : event.getChangedSquares()) {
                        repaintSquare(changedSquare);
                    }
                }
            };

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
        for (Square sq : Square.values()) {
            Piece pieceOnSqare = model.getChessboard().getPiece(sq);
            squareImages.getSquareImage(pieceOnSqare, sq.isLight()).paintIcon(this, g,
                    deltaX + sq.getFile() * cellSize,
                    deltaY + (7 - sq.getRank()) * cellSize);
        }

        //nakresli cerneny obdelnik kolem selectedFromSquare
        //(pokud nejaky vybrany je)
        if (selectedFromSquare != null) {
            g.setColor(java.awt.Color.RED);
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
        g.setColor(java.awt.Color.BLACK);
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
        LOG.debug("Repaint square {}", s);
        Graphics g = getGraphics();
        Piece pieceOnSq = model.getChessboard().getPiece(s);

        squareImages.getSquareImage(pieceOnSq, s.isLight()).paintIcon(this, g,
                deltaX + s.getFile() * sizeOfSquare,
                deltaY + (7 - s.getRank()) * sizeOfSquare);

        if (s.equals(selectedFromSquare)) {
            g.setColor(java.awt.Color.RED);
            g.drawRect(deltaX + selectedFromSquare.getFile() * sizeOfSquare,
                    deltaY + (7 - selectedFromSquare.getRank()) * sizeOfSquare,
                    sizeOfSquare - 1,
                    sizeOfSquare - 1);
            g.drawRect(deltaX + selectedFromSquare.getFile() * sizeOfSquare + 1,
                    deltaY + (7 - selectedFromSquare.getRank()) * sizeOfSquare + 1,
                    sizeOfSquare - 3,
                    sizeOfSquare - 3);
        }

        //prekresli cerny obdelnik kolem sachovnice
        g.setColor(java.awt.Color.BLACK);
        g.drawRect(deltaX, deltaY, sizeOfSquare * 8, sizeOfSquare * 8);
    }

    /**
     * Returns the square that corresponds to a mouse click on the component.
     * For example if user clicks on the square e3, then Square.E3 object is
     * returned. if user clicks on some place on this component, that does not
     * correspond to any square, than null is returned.
     *
     * @param event mouse event that holds the information about the mouse
     * click, for which we want to find correpsonding square
     * @return the Square object corrresponding to clicked square of null if no
     * square was clicked on.
     */
    private Square getCorrespondingSquare(MouseEvent event) {
        if (event == null) {
            throw new NullPointerException("event can't be null!");
        }

        int x = event.getX();
        int y = event.getY();
        int columnIndex;
        int rankIndex;

        //urcime index kliknuteho sloupce na sachovnici
        if (x > deltaX && x < (deltaX + 8 * sizeOfSquare)) {
            columnIndex = (x - deltaX) / sizeOfSquare;
        } else {
            columnIndex = -1;
        }

        //urcime index kliknuteho radku na sachovnici
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
        //je-li to pawn promotion dame vybrat na co ho chce promotnout
        if ((piece.equals(Piece.WHITE_PAWN) && from.getRank() == 6 && to.getRank() == 7)
                || (piece.equals(Piece.BLACK_PAWN) && from.getRank() == 1 && to.getRank() == 0)) {
            ImageIcon[] options;
            if (piece.isWhite()) {
                options = new ImageIcon[]{
                    squareImages.getSquareImage(Piece.WHITE_QUEEN, false),
                    squareImages.getSquareImage(Piece.WHITE_ROOK, true),
                    squareImages.getSquareImage(Piece.WHITE_BISHOP, false),
                    squareImages.getSquareImage(Piece.WHITE_KNIGHT, true)};
            } else {
                options = new ImageIcon[]{
                    squareImages.getSquareImage(Piece.BLACK_QUEEN, false),
                    squareImages.getSquareImage(Piece.BLACK_ROOK, true),
                    squareImages.getSquareImage(Piece.BLACK_BISHOP, false),
                    squareImages.getSquareImage(Piece.BLACK_KNIGHT, true)};
            }
            int toWhatPromote =
                    JOptionPane.showOptionDialog(getTopLevelAncestor(),
                    "What do you want to promote this pawn to?",
                    "Pawn promotion",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            Piece promoPiece;
            switch (toWhatPromote) {
                case 0:
                    promoPiece = piece.isWhite() ? Piece.WHITE_QUEEN
                            : Piece.BLACK_QUEEN;
                    break;
                case 1:
                    promoPiece = piece.isWhite() ? Piece.WHITE_ROOK
                            : Piece.BLACK_ROOK;
                    break;
                case 2:
                    promoPiece = piece.isWhite() ? Piece.WHITE_BISHOP
                            : Piece.BLACK_BISHOP;
                    break;
                case 3:
                    promoPiece = piece.isWhite() ? Piece.WHITE_KNIGHT
                            : Piece.BLACK_KNIGHT;
                    break;
                default:
                    throw new IllegalStateException("toWhatPromote"
                            + " wasn't 0, 1, 2 or 3");
            }
            return new Promotion(piece, from, to, promoPiece);
        } else { //neni to pawn promotion
            return new Move(piece, from, to);
        }
    }
}
