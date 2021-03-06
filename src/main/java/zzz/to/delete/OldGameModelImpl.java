package zzz.to.delete;

import cz.janhrcek.chess.gui.MoveListener;
import zzz.to.delete.OldGameStateMutable;
import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.enums.Piece;
import zzz.to.delete.OldMutablePosition;
import cz.janhrcek.chess.model.api.enums.Square;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import javax.swing.JOptionPane;

/**
 * This implementation of the OldGameModel is just encapsulation of
 * chess.stateofgame.Game. This class also implements MoveListener
 * so that it can change the gamestate when user selects a move in GUI.
 *
 * @author xhrcek
 */
public class OldGameModelImpl implements OldGameModel, MoveListener {

    /**
     * The Game object which this class encapsulates for the representation of
     * the gamestate.
     */
    private OldGameStateMutable gameState;
    /**
     * Enables us to control the legality of the moves.
     */
    private OldRuleChecker ruleChecker;
    /**
     * Listeners, which will be notified of the changes of the gamestate.
     */
    private Collection<OldGameModelListener> listeners =
            new HashSet<OldGameModelListener>();

    /**
     * Creates a new instance of OldGameModelImpl
     *
     * @param state The game object which will be encapsulated for the
     * representation of the games tate
     */
    public OldGameModelImpl(OldGameStateMutable state) {
        this.gameState = state;
        ruleChecker = new OldFIDERules();
    }

    /**
     * Sets the game which this implementation of OldGameModel encapsulates for
     * represetation of the state of the game.
     *
     * @param g the game to represent the state of the game
     */
    public void setGame(OldGameStateMutable g) {
        if (g == null) {
            throw new NullPointerException("g can't be null!");
        }
        this.gameState = g;
    }

    /**
     * Return the game which this implementation of OldGameModel encapsulates for
     * representation of the game state.
     *
     * @return the game which this implementation of OldGameModel encapsulates for
     * representation of the game state.
     */
    public OldGameStateMutable getGame() {
        return gameState;
    }

    /**
     * Adds Listener, which will of changes of the gamestate.
     *
     * @param listener the listener to register
     */
    @Override
    public void addGameModelListener(OldGameModelListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener can't be null!");
        }
        listeners.add(listener);
    }

    /**
     * Removes given move listener, so that it will be no longer notified of the
     * changes of the gamestate.
     *
     * @param listener the listener to unregister
     */
    @Override
    public void removeGameModelListener(OldGameModelListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener can't be null!");
        }
        listeners.remove(listener);
    }

    /**
     * Returns list of pieces captured since the beginning of the game.
     *
     * @return list of pieces captured since the beginning of the game.
     */
    @Override
    public LinkedList<Piece> getCapturedPieces() {
        return gameState.getCapturedPieces();
    }

    /**
     * Return list of moves played since the beginning of the game.
     *
     * @return list of moves played since the beginning of the game.
     */
    @Override
    public LinkedList<Move> getMovesPlayed() {
        return gameState.getMovesPlayed();
    }

    /**
     * Return list of Boolean values representing which moves were checks.
     *
     * @return list of Boolean values representing which moves were checks
     * list.get(i) is true <=> i-th move was check
     */
    @Override
    public LinkedList<Boolean> getChecks() {
        return gameState.getChecks();
    }

    /**
     * Return the chessboard representing position of pieces on the board.
     *
     * @return the chessboard representing position of pieces on the board.
     */
    @Override
    public OldMutablePosition getChessboard() {
        return gameState.getChessboard();
    }

    /**
     * Returns true if it is white to move, false otherwise.
     *
     * @return true if it is white to move, false otherwise.
     */
    @Override
    public boolean isWhiteToMove() {
        return gameState.isWhiteToMove();
    }

    /**
     * Returns true if the last move of the game was mate, false otherwise.
     *
     * @return true if the last move of the game was mate, false otherwise
     */
    @Override
    public boolean isMate() {
        return gameState.isMate();
    }

    /**
     * Makes the move in the current gamestate.
     *
     * @param move the object representing information about the move
     */
    @Override
    public void makeMove(final Move move) {
        gameState.move(move);
    }

    /**
     * Erases all information about the current gamestate, so that new game can
     * be played.
     */
    @Override
    public void setNewGame() {
        gameState.setNewGame();
        OldGameModelEvent gSMEvent =
                new OldGameModelEvent(this, Arrays.asList(Square.values()));
        for (OldGameModelListener listener : listeners) {
            listener.gameStateChanged(gSMEvent);
        }
    }

    /**
     * Returns the game state one move before the last move, as if the last move
     * was not played at all.
     */
    @Override
    public void cancelLastMove() {
        if (gameState.getMovesPlayed().size() == 0) {
            System.out.println("No more moves to take back!");
            return;
        }
        Move lastMove = gameState.getMovesPlayed().getLast();
        gameState.cancelLastMove();
        //doslo ke zmene tahu -> dame vedet posluchacum zmeny stavu
        ArrayList<Square> squaresThatChanged =
                getSquaresThatChanged(lastMove);
        OldGameModelEvent gSMEvent =
                new OldGameModelEvent(this, squaresThatChanged);
        for (OldGameModelListener listener : listeners) {
            listener.gameStateChanged(gSMEvent);
        }
    }

    /**
     * This method contains code, which reacts to MoveSelected event. That is it
     * changes the gamestate by playing the move if it was legal, or displays
     * warning message dialog containing message why the move was illegal.
     *
     * @param event the event which represents event, that the move was selected
     */
    @Override
    public void moveSelected(Move move) {
        //check the legality of selected move in given game state
        //and handle illegal moves
        if (!handleIllegalMoves(move)) {
            return; //move was illegal, so we don't continue
        }

        //only legal moves should get here!!
        makeMove(move);

        //dojdeme-li tady, pak tah je legalni a vsem posluchacum muzem
        // rict, ze udalost nastala
        ArrayList<Square> squaresThatChanged =
                getSquaresThatChanged(move);
        OldGameModelEvent gSMEvent =
                new OldGameModelEvent(this, squaresThatChanged);
        for (OldGameModelListener listener : listeners) {
            listener.gameStateChanged(gSMEvent);
        }
    }

    /**
     * Returns the squares of the chessboard which were changed since the last
     * call of this method.
     *
     * @param selectedMove The move which was carried by the MoveSelected event.
     * @return the squares of the chessboard which were changed since the last
     * call of this method.
     */
    private ArrayList<Square> getSquaresThatChanged(Move selectedMove) {
        ArrayList<Square> changedSquares = new ArrayList<>();
        Piece piece = selectedMove.getPiece();
        Square from = selectedMove.getFrom();
        Square to = selectedMove.getTo();
        changedSquares.add(from);
        changedSquares.add(to);

        if (piece.equals(Piece.WHITE_KING) && from.equals(Square.E1) && to.equals(Square.G1)) {
            changedSquares.add(Square.F1);
            changedSquares.add(Square.H1);
        } else if (piece.equals(Piece.WHITE_KING) && from.equals(Square.E1) && to.equals(Square.C1)) {
            changedSquares.add(Square.A1);
            changedSquares.add(Square.D1);
        } else if (piece.equals(Piece.BLACK_KING) && from.equals(Square.E8) && to.equals(Square.G8)) {
            changedSquares.add(Square.F8);
            changedSquares.add(Square.H8);
        } else if (piece.equals(Piece.BLACK_KING) && from.equals(Square.E8) && to.equals(Square.C8)) {
            changedSquares.add(Square.A8);
            changedSquares.add(Square.D8);
        } else if (piece.equals(Piece.WHITE_PAWN) && from.getRank() == 4 && (to.getFile() != from.getFile())) {
            changedSquares.add(Square.getSquare(to.getFile(), 4));
        } else if (piece.equals(Piece.BLACK_PAWN) && from.getRank() == 3 && (to.getFile() != from.getFile())) {
            changedSquares.add(Square.getSquare(to.getFile(), 3));
        }
        return changedSquares;
    }

    /**
     * Makes the chessboard display the first position of the game.
     */
    @Override
    public void setFirstPosition() {
        gameState.setFirstPosition();
        OldGameModelEvent gSMEvent =
                new OldGameModelEvent(this, Arrays.asList(Square.values()));
        for (OldGameModelListener listener : listeners) {
            listener.gameStateChanged(gSMEvent);
        }
    }

    /**
     * Makes the chessboard display one move before surrent move (if it allready
     * does not contain starting position).
     */
    @Override
    public void setPreviousPosition() {
        gameState.setPreviousPosition();
        OldGameModelEvent gSMEvent =
                new OldGameModelEvent(this, Arrays.asList(Square.values()));
        for (OldGameModelListener listener : listeners) {
            listener.gameStateChanged(gSMEvent);
        }
    }

    /**
     * Makes chessboard display one move after the current move (if it allready
     * does not contain position after last move).
     */
    @Override
    public void setNextPosition() {
        gameState.setNextPosition();
        OldGameModelEvent gSMEvent =
                new OldGameModelEvent(this, Arrays.asList(Square.values()));
        for (OldGameModelListener listener : listeners) {
            listener.gameStateChanged(gSMEvent);
        }
    }

    /**
     * Makes the Chessboard display the position after the last move.
     */
    @Override
    public void setLastPositon() {
        gameState.setLastPosition();
        OldGameModelEvent gSMEvent =
                new OldGameModelEvent(this, Arrays.asList(Square.values()));
        for (OldGameModelListener listener : listeners) {
            listener.gameStateChanged(gSMEvent);
        }
    }

    /**
     * Turns rule checking (of legality of moves) on/off.
     *
     * @param turnOn true if you want to check the legality of the moves agains
     * oficial chess rules (FIDE), false otherwise
     */
    public void setRuleChecking(boolean turnOn) {
        if (turnOn) {
            ruleChecker = new OldFIDERules();
        } else {
            ruleChecker = new OldNoRules();
        }
    }

    /**
     * This method handles illegal moves carried by MoveSelectedEvent. It
     * displays error message in the GUI containing the chessboard component
     * through which the illegal move was selected.
     *
     * @return true if move was legal false otherwise
     * @param event The event carrying information about the move which was
     * selected
     */
    private boolean handleIllegalMoves(Move m) {
        OldMoveType legalityTestResult = ruleChecker.checkMove(gameState, m);
        String errorMessage = "";

        switch (legalityTestResult) {
            case ILLEGAL_PIECE_MOVE:
                System.out.println("typ tahu:" + legalityTestResult);
                switch (m.getPiece()) {
                    case WHITE_KING:
                    case BLACK_KING:
                        errorMessage = "King can only move one square in any"
                                + " direction.";
                        break;
                    case WHITE_QUEEN:
                    case BLACK_QUEEN:
                        errorMessage = "Queen can only move along ranks,"
                                + " files and diagonals.";
                        break;
                    case WHITE_ROOK:
                    case BLACK_ROOK:
                        errorMessage = "Rook can only move along ranks"
                                + " and files.";
                        break;
                    case WHITE_BISHOP:
                    case BLACK_BISHOP:
                        errorMessage = "Bishop can only move along diagonals.";
                        break;
                    case WHITE_KNIGHT:
                    case BLACK_KNIGHT:
                        errorMessage = "Knight move must be L-shaped (two squ"
                                + "ares\n along rank or file and then one sq"
                                + "uare\n along file or rank respectively).";
                        break;
                    case WHITE_PAWN:
                        if (m.getFrom().getRank() == 1) {
                            errorMessage = "Pawns can only move one or two"
                                    + " squares\n forward from their starting"
                                    + " position or t";
                        } else {
                            errorMessage = "Pawns can only move one square f"
                                    + "orward\n or they can capture one square f"
                                    + "orward\n diagonally";
                        }
                        break;
                    case BLACK_PAWN:
                        if (m.getFrom().getRank() == 6) {
                            errorMessage = "Pawns can only move one or two"
                                    + " squares\n forward from their starting"
                                    + " position or t";
                        } else {
                            errorMessage = "Pawns can only move one square f"
                                    + "orward\n or they can capture one square f"
                                    + "orward\n diagonally";
                        }
                        break;
                    default://should never get here
                        throw new IllegalStateException("there was some other"
                                + " piece than those declared in Chessmen enum!");
                }
                break;
            case ILLEGAL_WRONG_COLOR:
                System.out.println("typ tahu:" + legalityTestResult);
                if (m.getPiece().isWhite()) {
                    errorMessage = "You cannot move white"
                            + " piece, when it is black to move.";
                } else {
                    errorMessage = "You cannot move black"
                            + " piece, when it is white to move.";
                }
                break;
            case ILLEGAL_PATH_BLOCKED:
                System.out.println("typ tahu: " + legalityTestResult);
                errorMessage = "No piece (except knights)"
                        + " can jump over other pieces.";
                break;
            case ILLEGAL_SQUARE_OCCUPIED:
                System.out.println("typ tahu: " + legalityTestResult);
                //test jestli nejde o pesaka blokovaneho nepratelskou figurou
                if ((m.getPiece().equals(Piece.WHITE_PAWN)
                        && !gameState.getChessboard().getPiece(m.getTo())
                        .isWhite())
                        || (m.getPiece().equals(Piece.BLACK_PAWN)
                        && gameState.getChessboard().getPiece(m.getTo())
                        .isWhite())) {
                    errorMessage = "Pawns cannot move forward when their"
                            + " path\n is blocked by anything.";
                } else {
                    errorMessage = "You may not move your piece to a square\n"
                            + " occupied by another of your pieces.";
                }
                break;
            case ILLEGAL_LEAVES_KING_IN_CHECK:
                System.out.println("typ tahu: " + legalityTestResult);
                errorMessage = "You may not leave your"
                        + " king exposed to check.";
                break;
            case ILLEGAL_CASTLING_NO_LONGER_AVAILABLE:
                System.out.println("typ tahu: " + legalityTestResult);
                errorMessage = "You can no longer castle, because either\n"
                        + " your king or rook has moved from their\n s"
                        + "tarting position";
                break;
            case ILLEGAL_CASTLING_KING_IN_CHECK:
                System.out.println("typ tahu: " + legalityTestResult);
                errorMessage = "You cannot castle when your"
                        + " king is in check.";
                break;
            case ILLEGAL_CASTLING_THROUGH_CHECK:
                System.out.println("typ tahu: " + legalityTestResult);
                errorMessage = "You cannot castle through"
                        + " attacked square";
                break;
            case ILLEGAL_EN_PASSANT:
                System.out.println("typ tahu: " + legalityTestResult);
                errorMessage = "You can only make \"en passant\" capture\n"
                        + " when your pawn stand on your fifth rank\n"
                        + " only on the very next move after your\n"
                        + " opponent has moved one of his pawns on\n"
                        + " file directly adjacent to your pawn two\n"
                        + " squares from its starting position.";
                break;
            default: //this corresponds to legal moves,
                //which we don't care about here
                break;
        }
        if (!errorMessage.equals("")) {
            JOptionPane.showMessageDialog(null,
                    errorMessage,
                    "Illegal move",
                    JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns the halfmove after which the state is displayed on the
     * chessboard, if starting position is on the board this method returns 0,
     * after each move played on the chessboard makes the value returned by the
     * method one larger.
     *
     * @return the halfmove after which the state is displayed on the chessboard
     *
     */
    @Override
    public int getCurrentlyViewedHalfMove() {
        return gameState.getCurrentlyViewedHalfmove();
    }

    /**
     * Return the string representation of the gamestate which this class
     * represents.
     *
     * @return the string representation of the gamestate which this class
     * represents.
     */
    @Override
    public String toString() {
        return gameState.toString();
    }

    /**
     * Return string, which represents Moves played since the beginning of the
     * game.
     *
     * @param highlightCurrentMove true if the current move should be
     * highlighted, false otherwise.
     * @return text representation of played moves
     */
    @Override
    public String getMovetextString(boolean highlightCurrentMove) {
        return gameState.getMovetextSectionString(highlightCurrentMove);
    }
}
