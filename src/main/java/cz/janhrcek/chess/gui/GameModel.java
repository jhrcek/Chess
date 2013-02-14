package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.enums.Piece;
import cz.janhrcek.chess.model.impl.Position;
import java.util.LinkedList;

/**
 * The GameModel interface specifies the methods the ChessboardComponent will
 * use to get data from some class represennting the state of the game to be
 * displayed.
 *
 * @author xhrcek
 */
public interface GameModel {

    /**
     * Adds Listener, which will of changes of the gamestate.
     *
     * @param listener the listener to register
     */
    void addGameModelListener(GameModelListener listener);

    /**
     * Removes given move listener, so that it will be no longer notified of the
     * changes of the gamestate.
     *
     * @param listener the listener to unregister
     */
    void removeGameModelListener(GameModelListener listener);

    //methody na ziskavani informaci o stavy hry
    /**
     * Returns the halfmove after which the state is displayed on the
     * chessboard, if starting position is on the board this method returns 0,
     * after each move played on the chessboard makes the value returned by the
     * method one larger.
     *
     * @return the halfmove after which the state is displayed on the chessboard
     *
     */
    int getCurrentlyViewedHalfMove();

    /**
     * Return string, which represents Moves played since the beginning of the
     * game.
     *
     * @param highlightCurrentMove true if the current move should be
     * highlighted, false otherwise.
     * @return text representation of played moves
     */
    String getMovetextString(boolean highlightCurrentMove);

    /**
     * Returns list of pieces captured since the beginning of the game.
     *
     * @return list of pieces captured since the beginning of the game.
     */
    LinkedList<Piece> getCapturedPieces();

    /**
     * Return list of moves played since the beginning of the game.
     *
     * @return list of moves played since the beginning of the game.
     */
    LinkedList<Move> getMovesPlayed();

    /**
     * Return list of Boolean values representing which moves were checks.
     *
     * @return list of Boolean values representing which moves were checks
     * list.get(i) is true <=> i-th move was check
     */
    LinkedList<Boolean> getChecks();

    /**
     * Return the chessboard representing position of pieces on the board.
     *
     * @return the chessboard representing position of pieces on the board.
     */
    Position getChessboard();

    /**
     * Returns true if it is white to move, false otherwise.
     *
     * @return true if it is white to move, false otherwise.
     */
    boolean isWhiteToMove();

    /**
     * Returns true if the last move of the game was mate, false otherwise.
     *
     * @return true if the last move of the game was mate, false otherwise
     */
    boolean isMate();

    //metody umoznujici menit stav hry
    /**
     * Makes the move in the current gamestate.
     *
     * @param move the object representing information about the move
     */
    void makeMove(final Move move);

    /**
     * Erases all information about the current gamestate, so that new game can
     * be played.
     */
    void setNewGame();

    /**
     * Returns the game state one move before the last move, as if the last move
     * was not played at all.
     */
    void cancelLastMove();

    //metody umoznujici prochazet stavem hry tam & zpet
    /**
     * Makes the chessboard display the first position of the game.
     */
    void setFirstPosition();

    /**
     * Makes the chessboard display one move before surrent move (if it allready
     * does not contain starting position).
     */
    void setPreviousPosition();

    /**
     * Makes chessboard display one move after the current move (if it allready
     * does not contain position after last move).
     */
    void setNextPosition();

    /**
     * Makes the Chessboard display the position after the last move.
     */
    void setLastPositon();
}
