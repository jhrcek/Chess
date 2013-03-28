package zzz.to.delete;

import zzz.to.delete.OldMutablePosition;
import cz.janhrcek.chess.model.api.enums.Piece;
import cz.janhrcek.chess.model.api.Promotion;
import cz.janhrcek.chess.model.api.enums.Square;
import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.impl.PieceNotPresentException;
import cz.janhrcek.chess.rules.BitboardManager;
import cz.janhrcek.chess.rules.OldFIDERules;
import cz.janhrcek.chess.rules.OldMoveType;
import cz.janhrcek.chess.rules.NoRules;
import cz.janhrcek.chess.rules.OldRuleChecker;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * This class serves for representation of the state of the chess game. Instance
 * of this class keeps track of the following information about the state of the
 * game: <ul> <li>the position of pieces on the board</li> <li>information about
 * which player is on the move (white or black)</li> <li>information about the
 * moves played since the beginning</li> <li>the pieces, that were captured
 * since the beginning</li> <li>information about castling availabilities</li>
 * <li>full-move number (number of full moves played since the beginning)</li>
 * <li>half-move clock (for the purpose of "50 move draw" rule)</li> </ul>
 *
 * This class also contains several methods to manipulate (change) the game
 * (move, setNewGame) and also methods for traversing through the moves of given
 * game (setFirst/Previous/Next/LastPosition).
 *
 *
 * @author Jan Hrcek
 */
public class OldGameStateMutable {

    /**
     * Creates new instance of OldGameStateMutable, whose initial GameState is 
     * standard Initial OldMutablePosition of the chess game described by Fide Rules.
     */
    public OldGameStateMutable() {
        currentlyFocusedPosition = new OldMutablePosition();
        currentlyFocusedPosition.setInitialPosition();
        currentlyViewedHalfmove = 0;
        movesPlayed = new LinkedList<>();
        capturedPieces = new LinkedList<>();
        captures = new LinkedList<>();
        checks = new LinkedList<>();
        castlingAvailabilities = new LinkedList<>();
        isWhiteToMove = true;
        ruleChecker = new OldFIDERules();
        castlingAvailabilityTracker = BLACK_KING_MASK + WHITE_KING_MASK
                + BLACK_Q_ROOK_MASK + BLACK_K_ROOK_MASK + WHITE_Q_ROOK_MASK
                + WHITE_K_ROOK_MASK;
        castlingAvailabilities.add(castlingAvailabilityTracker);
        whichMovesWereEnPassant.add(new Integer(0)); //dummy to avoid underflow
        gameHeader = new GameHeader();
    }

    /**
     * Resets the state of the game to the state in which the new game can be
     * played from the beginning.
     */
    public void setNewGame() {
        currentlyFocusedPosition.setInitialPosition();
        currentlyViewedHalfmove = 0;
        isWhiteToMove = true;
        castlingAvailabilities.clear();
        capturedPieces.clear();
        movesPlayed.clear();
        captures.clear();
        checks.clear();
        //both players can castle on both sides
        castlingAvailabilityTracker = BLACK_KING_MASK + WHITE_KING_MASK
                + BLACK_Q_ROOK_MASK + BLACK_K_ROOK_MASK + WHITE_Q_ROOK_MASK
                + WHITE_K_ROOK_MASK;
        castlingAvailabilities.add(castlingAvailabilityTracker);
        whichMovesWereEnPassant.add(new Integer(0));
    }

    /**
     * Moves the piece from given square to given square, the move is made, side
     * to move is changed (Black -> White and vice versa) and if some piece is
     * captured it is stored as captured piece in the list of captured pieces.
     * This method does not check the legality of the move. If parameter
     * represents illegal move it is made nonetheless.
     *
     * @param move object of class MoveInfo, that holds the information about
     * move to be made.
     */
    public void move(final Move move) {
        if (move == null) {
            throw new NullPointerException("moveInfo can't be null!");
        }

        setLastPosition(); //make the chessboard contain state after last move!

        //if one side has allready mated, then we will allow no more moves
        if (isMate) {
            System.out.println("You cannot make further moves! "
                    + (isWhiteToMove() ? "Black" : "White")
                    + " player mated with his last move and won the game!");
            return;
        }

        OldMoveType mt = ruleChecker.checkMove(this, move);
        try {///prozatimni reseni
            switchOnMoveType(move, mt);
        } catch (IllegalStateException ise) {
            ise.printStackTrace();
        }

        try {
            if (wasEnPassant(move)) {
                setEnPassantInfo();
                removeEnPassanPawn(move.getTo());
            }
            Piece capturedPiece = currentlyFocusedPosition.makeMove(move);
            if (capturedPiece != null) {
                capturedPieces.add(capturedPiece);
                captures.add(Boolean.valueOf("TRUE"));
            } else {
                captures.add(Boolean.valueOf("FALSE"));
            }
            movesPlayed.add(move);
            changeCastlingAvailability(move);
            castlingAvailabilities.add(castlingAvailabilityTracker);
            changeSideToMove();
            if (wasCastling(move)) {
                setCastlingInfo(move);
                moveCastlingRook(move.getTo()); //kings target square
            }
            currentlyViewedHalfmove++;
        } catch (PieceNotPresentException cbe) { //throwed if the moving piece was
            //not on the "from" square
            //we should get the "right" MoveInfo objects from gui
            cbe.printStackTrace();
        }
    }

    /**
     * Makes the move on the chessboard without checking all rule-related
     * problems.
     *
     * @param move the move to be made
     */
    public void makeUncheckedMove(final Move move) {
        if (move == null) {
            throw new NullPointerException("moveInfo can't be null!");
        }
        setLastPosition(); //make the chessboard contain state after last move!

        try {
            if (wasEnPassant(move)) {
                setEnPassantInfo();
                removeEnPassanPawn(move.getTo());
            }
            castlingAvailabilities.add(castlingAvailabilityTracker);
            Piece capturedPiece = currentlyFocusedPosition.makeMove(move);
            if (capturedPiece != null) {
                capturedPieces.add(capturedPiece);
                captures.add(Boolean.valueOf("TRUE"));
            } else {
                captures.add(Boolean.valueOf("FALSE"));
            }
            checks.add(Boolean.valueOf("FALSE"));
            movesPlayed.add(move);
            changeSideToMove();
        } catch (PieceNotPresentException cbe) {
            cbe.printStackTrace();
        }
    }

    /**
     * Return the game to the state in which it was before the last move was
     * played.
     */
    public void cancelLastMove() {
        int numberOfLastHalfmove = movesPlayed.size();

        if (numberOfLastHalfmove == 0) { //don't do anythig if
            return;                   //no move was played
        }

        Move lastMove = movesPlayed.getLast();

        //pokud byl posledni tah rosada musime vratit nejen KINGA ale i ROOK
        if (numberOfLastHalfmove == whenWhiteCastled) {
            returnCastledRook(lastMove.getTo());
            whenWhiteCastled = 0;
        } else if (numberOfLastHalfmove == whenBlackCastled) {
            returnCastledRook(lastMove.getTo());
            whenBlackCastled = 0;
            //pokud byl posledni tah enpassant capture musime vratit en passant pawna
        } else if (whichMovesWereEnPassant.getLast().equals(numberOfLastHalfmove)) {
            returnEnPassantPawn(lastMove);
            whichMovesWereEnPassant.removeLast();
            //
        }

        movesPlayed.removeLast();
        Piece capturedPiece = null;
        //jestli neco bylo vzato tak zjisti co
        if (captures.getLast().equals(Boolean.TRUE)) {
            capturedPiece = capturedPieces.getLast();
            capturedPieces.removeLast();
        }
        captures.removeLast();
        checks.removeLast();
        castlingAvailabilities.removeLast();
        castlingAvailabilityTracker = castlingAvailabilities.getLast();
        setLastPosition();
        currentlyFocusedPosition.putPiece(lastMove.getPiece(),
                lastMove.getFrom());
        currentlyFocusedPosition.removePiece(lastMove.getTo());
        if (capturedPiece != null) {
            currentlyFocusedPosition.putPiece(capturedPiece, lastMove.getTo());
        }
        changeSideToMove();
        currentlyViewedHalfmove = movesPlayed.size();
        if (isMate) { //if last move was mate, then setPreviousPosition was not mate.
            isMate = false;
        }
    }

    /**
     * Gives information about which player is to move.
     *
     * @return true if it is white player to move <br> false if is it's black to
     * move.
     */
    public boolean isWhiteToMove() {
        return isWhiteToMove;
    }

    /**
     * Returns information about whether the last move of the game was mate.
     *
     * @return true if the last move played in this game was mate, false
     * otherwise
     */
    public boolean isMate() {
        return isMate;
    }

    /**
     * Returns information about position of pieces on the board.
     *
     * @return instance of chessboard holding information about the positioning
     * of pieces
     */
    public OldMutablePosition getChessboard() {
        return currentlyFocusedPosition;
    }

    /**
     * Returns the list of pieces that were captured during the game.
     *
     * @return list of pieces captured during the game
     */
    public LinkedList<Piece> getCapturedPieces() {
        return capturedPieces;
    }

    /**
     * Returns list of MoveInfo objects, which represents the history of moves
     * played since the beginning of the game.
     *
     * @return list of moves played since the beginning of the game
     */
    public LinkedList<Move> getMovesPlayed() {
        return movesPlayed;
    }

    /**
     * This method returns list of Boolean values which for each half-move
     * represent whether it was/was not check (that is
     * getChecks().get(i).equals(Boolean.TRUE) <=> i-th half move was check)
     *
     * @return list holding information about all played moves, whether they
     * were checks or not.
     */
    public LinkedList<Boolean> getChecks() {
        return checks;
    }

    /**
     * Returns string representing castling availability.
     *
     * @return string representing castling availability in this state of the
     * game
     */
    public String getCastlingAvailability() {
        StringBuilder result = new StringBuilder();
        if ((castlingAvailabilityTracker & WHITE_KING_MASK) != 0
                && (castlingAvailabilityTracker & WHITE_K_ROOK_MASK) != 0) {
            result.append("K");
        }
        if ((castlingAvailabilityTracker & WHITE_KING_MASK) != 0
                && (castlingAvailabilityTracker & WHITE_Q_ROOK_MASK) != 0) {
            result.append("Q");
        }
        if ((castlingAvailabilityTracker & BLACK_KING_MASK) != 0
                && (castlingAvailabilityTracker & BLACK_K_ROOK_MASK) != 0) {
            result.append("k");
        }
        if ((castlingAvailabilityTracker & BLACK_KING_MASK) != 0
                && (castlingAvailabilityTracker & BLACK_Q_ROOK_MASK) != 0) {
            result.append("q");
        }
        if (result.length() == 0) {
            result.append("-");
        }
        return result.toString();
    }

    /**
     * Returns EnPassantTargetSquare if there is such square, null otherwise. En
     * Passant target square is not null right after some player moves his pawn
     * two squares forward from its starting position on the second rank. In
     * that case his opponent can capture that pawn if his pawn is on the fifth
     * rank on the file directly adjacent to advanced pawn's file by moving to
     * en passant target square.
     *
     * @return EnPassant Target Square if there is such a square<br> null
     * otherwise
     */
    public Square getEnPassantTargetSquare() {
        if (movesPlayed.isEmpty()) { //zadny tah nebyl odehran
            return null;
        } else {
            Move lastMove = movesPlayed.getLast();

            if (lastMove.getPiece().equals(Piece.WHITE_PAWN)
                    && lastMove.getFrom().getFile() == lastMove.getTo().getFile()
                    && lastMove.getFrom().getRank() == 1
                    && lastMove.getTo().getRank() == 3) {
                return Square.getSquare(lastMove.getFrom().getFile(), 2);
            } else if (lastMove.getPiece().equals(Piece.BLACK_PAWN)
                    && lastMove.getFrom().getFile() == lastMove.getTo().getFile()
                    && lastMove.getFrom().getRank() == 6
                    && lastMove.getTo().getRank() == 4) {
                return Square.getSquare(lastMove.getFrom().getFile(), 5);
            } else {
                return null;
            }
        }
    }

    /**
     * returns the full-move number. At the beginning of the game it is 1, and
     * it is incremented after black's move.
     *
     * @return full-move number;
     */
    public int getFullmoveNumber() {
        return 1 + movesPlayed.size() / 2;
    }

    /**
     * Returns half-move clock: This is the number of half-moves since the last
     * pawn advance or capture. This is used to determine if a draw can be
     * claimed under the fifty move rule.
     *
     * @return number of half-moves since the last pawn advance or capture
     */
    public int getHalfmoveClock() {
        int numberOfHalfMovesSinceLastCapture = 0;
        int numberOfHalfMovesSinceLastPawnMove = 0;

        //spoctem kolik pultahu uplynulo od posledni capture
        ListIterator<Boolean> capturesIt = captures.listIterator(captures.size());
        while (capturesIt.hasPrevious()) {
            if (capturesIt.previous().equals(Boolean.TRUE)) {
                break;
            } else {
                numberOfHalfMovesSinceLastCapture++;
            }
        }

        //spoctem kolik pultahu uplynulo od posledni pawn advance
        ListIterator<Move> moveIt = movesPlayed.listIterator(movesPlayed.size());
        while (moveIt.hasPrevious()) {
            Piece tmpPiece = moveIt.previous().getPiece();
            if (tmpPiece.equals(Piece.WHITE_PAWN)
                    || tmpPiece.equals(Piece.BLACK_PAWN)) {
                break;
            } else {
                numberOfHalfMovesSinceLastPawnMove++;
            }
        }
        return Math.min(numberOfHalfMovesSinceLastCapture,
                numberOfHalfMovesSinceLastPawnMove);
    }

    /**
     * This method enables to set any position of pieces on the board.
     *
     * @param c the chessboard
     */
    public void setChessboard(OldMutablePosition c) {
        if (c == null) {
            throw new NullPointerException("c can't be null!");
        }

        if (!isLegalPosition(c)) {
            throw new IllegalArgumentException("You can't set illegal position!");
        }

        currentlyFocusedPosition = c;
    }

    /**
     * Turns rule checking on/of. If turned on legality of each move is checked
     * against international FIDE chess rules. If turned on any move made is
     * considered legal.
     *
     * @param flag true if you want to set rule checking ON false if you want to
     * set rule checking OFF
     */
    public void setRuleChecking(boolean flag) {
        if (flag == true) {
            ruleChecker = new OldFIDERules();
        } else {
            ruleChecker = new NoRules();
        }
    }

    private void switchOnMoveType(Move move, OldMoveType mt) {
        switch (mt) {
            case LEGAL_MATE:
                checks.add(Boolean.valueOf("TRUE"));
                isMate = true;
                break;
            case LEGAL_CHECK:
                checks.add(Boolean.valueOf("TRUE"));
                break;
            case LEGAL:
                checks.add(Boolean.valueOf("FALSE"));
                break;
            default:
                throw new IllegalStateException("I got MoveType represen"
                        + "ting illegal move here!");
        }
    }

    /**
     * Sets up castling availability. This method takes one String parameter,
     * which must be of the following format: If neither side can castle, the
     * string is "-". Otherwise, this has one or more letters: "K" (White can
     * castle kingside), "Q" (White can castle queenside), "k" (Black can castle
     * kingside), and/or "q" (Black can castle queenside) in that order. For
     * example: <br> 1) in the starting position, when both player can castle on
     * both sides this is "KQkq" or any permutatuin on the four letters.<br> 2)
     * when White can castle queenside and black can castle kingside the string
     * is "Qk".<br> All in all there are 16 legal castling availability strings:
     * "KQkq", "KQk", "KQq", "KQ", "Kkq", "Kk", "Kq", "K", "Qkq", "Qk", "Qq",
     * "Q", "kq", "k", "q", "-"
     *
     * @param castlingAvailability string representing castling availability.
     */
    public void setCastlingAvailability(String castlingAvailability) {
        if (castlingAvailability == null) {
            throw new NullPointerException("castlingAvailability can't be null!");
        }

        java.util.List validCAStrings = java.util.Arrays.asList("KQkq",
                "KQk", "KQq", "KQ", "Kkq", "Kk", "Kq", "K", "Qkq", "Qk",
                "Qq", "Q", "kq", "k", "q", "-");

        if (!validCAStrings.contains(castlingAvailability)) {
            throw new IllegalArgumentException("castlingAvailability must be"
                    + " one of the following strings: " + validCAStrings);
        }

        castlingAvailabilityTracker = 0;

        if (castlingAvailability.contains("K")) {
            castlingAvailabilityTracker |= WHITE_KING_MASK;
            castlingAvailabilityTracker |= WHITE_K_ROOK_MASK;
        }
        if (castlingAvailability.contains("Q")) {
            castlingAvailabilityTracker |= WHITE_KING_MASK;
            castlingAvailabilityTracker |= WHITE_Q_ROOK_MASK;
        }
        if (castlingAvailability.contains("k")) {
            castlingAvailabilityTracker |= BLACK_KING_MASK;
            castlingAvailabilityTracker |= BLACK_K_ROOK_MASK;
        }
        if (castlingAvailability.contains("q")) {
            castlingAvailabilityTracker |= BLACK_KING_MASK;
            castlingAvailabilityTracker |= BLACK_Q_ROOK_MASK;
        }
    }

    /**
     * Sets which player is on the move.
     *
     * @param isWhiteToMove true - if you want to set White to move false - if
     * you want to set black to move
     */
    public void setPlayerToMove(boolean isWhiteToMove) {
        this.isWhiteToMove = isWhiteToMove;
    }

    /**
     * Makes the chessboard display the position, with which the game started.
     */
    public void setFirstPosition() {
        setPositionAfter(0);
    }

    /**
     * Makes the chessboard contain position of the pieces one move before
     * current position.
     */
    public void setPreviousPosition() {
        if (getCurrentlyViewedHalfmove() > 0) {
            setPositionAfter(getCurrentlyViewedHalfmove() - 1);
        }
    }

    /**
     * Makes the chessboard contain position after one move from the currently
     * displayed position.
     */
    public void setNextPosition() {
        if (currentlyViewedHalfmove < movesPlayed.size()) {
            setPositionAfter(getCurrentlyViewedHalfmove() + 1);
        }
    }

    /**
     * Makes the chessboard display the state after all the moves from the game
     * history have been played since the starting position.
     */
    public void setLastPosition() {
        setPositionAfter(movesPlayed.size());
    }

    /**
     * Returns the number representing which half-move is currently displayed on
     * the chessboard.
     *
     * @return number representing which half-move is currently displayed on the
     * chessboard.
     */
    public int getCurrentlyViewedHalfmove() {
        return currentlyViewedHalfmove;
    }

    /**
     * Returns the header of the game containing information identifying game.
     *
     * @return the header of the game containing information identifying game
     * (such as names of the players etc.)
     */
    public GameHeader getGameHeader() {
        return gameHeader;
    }

    /**
     * Inner class representing non-state-related information about the game
     * (who played white/black, when, where, ...)
     */
    public class GameHeader {

        /**
         * creates new instance of the GameHeader.
         */
        public GameHeader() {
        }

        /**
         * Sets event, during whic the game was played.
         *
         * @param event event, during whic the game was played.
         */
        public void setEvent(String event) {
            this.event = event;
        }

        /**
         * Sets the location of the event, during which thegame was played.
         *
         * @param site the location of the event, during which thegame was
         * played.
         */
        public void setSite(String site) {
            this.site = site;
        }

        /**
         * Sets the starting date of the game in the YYYY.MM.DD format.
         *
         * @param date the starting date of the game in the YYYY.MM.DD format.
         */
        public void setDate(String date) {
            this.date = date;
        }

        /**
         * Sets the playing round of the game.
         *
         * @param round the playing round of the game
         */
        public void setRound(String round) {
            this.round = round;
        }

        /**
         * Sets the name of the player with white pieces.
         *
         * @param white the name of the player with white pieces
         */
        public void setWhite(String white) {
            this.white = white;
        }

        /**
         * Sets the name of the player with black pieces.
         *
         * @param black the name of the player with black pieces
         */
        public void setBlack(String black) {
            this.black = black;
        }

        /**
         * Sets the result of the game. Must be one of the following: "1-0" when
         * white won, "1/2-1/2" when there was a draw, "0-1" when black won or
         * "*" when the result is unknown
         *
         * @param result one of the following strings: "1-0" when white won,
         * "1/2-1/2" when there was a draw, "0-1" when black won or "*" when the
         * result is unknown
         */
        public void setResult(String result) {
            this.result = result;
        }

        /**
         * Returns the name of the tournament or match event where the game was
         * played.
         *
         * @return the name of the tournament or match event (if it is unknown
         * "?" is returned)
         */
        public String getEvent() {
            return event;
        }

        /**
         * Returns the location of the event, during which thegame was played.
         *
         * @return the location of the event.
         */
        public String getSite() {
            return site;
        }

        /**
         * Returns the starting date of the game in the YYYY.MM.DD format.
         *
         * @return the starting date of the game in the YYYY.MM.DD format.
         */
        public String getDate() {
            return date;
        }

        /**
         * Returns the playing round of the game
         *
         * @return the playing round of the game
         */
        public String getRound() {
            return round;
        }

        /**
         * Returns the name of the player with white pieces.
         *
         * @return the name of the player with white pieces
         */
        public String getWhite() {
            return white;
        }

        /**
         * Returns the name of the player with black pieces
         *
         * @return the name of the player with black pieces
         */
        public String getBlack() {
            return black;
        }

        /**
         * the result of the game ("1-0" when white won, "1/2-1/2" when there
         * was a draw, "0-1" when black won or "*" when the result is unknown)
         *
         * @return the result of the game ("1-0" when white won, "1/2-1/2" when
         * there was a draw, "0-1" when black won or "*" when the result is
         * unknown)
         */
        public String getResult() {
            return result;
        }

        private String asString(String s) {
            if (s == null || s.length() == 0) {
                return "?";
            } else {
                return s;
            }
        }

        /**
         * Returns string representation of this Header.
         *
         * @return string representation of the header
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[Event \"").append(asString(event)).append(EOL);
            sb.append("[Site \"").append(asString(site)).append(EOL);
            sb.append("[Date \"").append(asString(date)).append(EOL);
            sb.append("[Round \"").append(asString(round)).append(EOL);
            sb.append("[White \"").append(asString(white)).append(EOL);
            sb.append("[Black \"").append(asString(black)).append(EOL);
            sb.append("[Result \"").append(asString(result)).append(EOL);
            return sb.toString();
        }
        /**
         * End of normal "tag-pair" line.
         */
        private static final String EOL = "\"]\n";
        private String event;
        private String site;
        private String date;
        private String round;
        private String white;
        private String black;
        private String result;
    };

    /**
     * This method converts game to string representation of the game. The
     * string is game in PGN format and can be directly saved to the pgn file.
     *
     * @return pgn representation of the game
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(gameHeader.toString()); //tagpair section
        sb.append("\n"); //empty new line between tagpair adn movetext section
        sb.append(getMovetextSectionString(false)); //movetext section
        sb.append("\n"); //empty line afte movetext
        return sb.toString();
    }

    /*------------------------------------------------------------------------*/
    /*------------------------- PRIVATE IMPLEMENTATION -----------------------*/
    /**
     * Method changes the side to move. That is, if it is white to move, the
     * method changes the state so that it is black to move and vice versa.
     */
    private void changeSideToMove() {
        if (isWhiteToMove) {
            isWhiteToMove = false;
        } else {
            isWhiteToMove = true;
        }
    }

    /**
     * If given move changes the castling availability, then this method makes
     * appropriate changes to this Game's castlingAvailability representation.
     *
     * @param move any move
     */
    private void changeCastlingAvailability(Move move) {
        if (move == null) {
            throw new IllegalArgumentException("move can't be null!");
        }

        if (getCastlingAvailability().equals("-")) { //uz neni co ubrat
            return;
        }

        Piece piece = move.getPiece();

        if (piece.equals(Piece.WHITE_KING)) { //pohl se W_KING->ztraci KQ
            castlingAvailabilityTracker &= WHITE_KING_MASK ^ -1;
        } else if (piece.equals(Piece.BLACK_KING)) { //ztraci kq
            castlingAvailabilityTracker &= BLACK_KING_MASK ^ -1;
        } else if (piece.equals(Piece.WHITE_ROOK)) {
            if (move.getFrom().equals(Square.A1)) {
                castlingAvailabilityTracker &= WHITE_Q_ROOK_MASK ^ -1;
            }
            if (move.getFrom().equals(Square.H1)) {
                castlingAvailabilityTracker &= WHITE_K_ROOK_MASK ^ -1;
            }
        } else if (piece.equals(Piece.BLACK_ROOK)) {
            if (move.getFrom().equals(Square.A8)) {
                castlingAvailabilityTracker &= BLACK_Q_ROOK_MASK ^ -1;
            }
            if (move.getFrom().equals(Square.H8)) {
                castlingAvailabilityTracker &= BLACK_K_ROOK_MASK ^ -1;
            }
        }
    }

    /**
     * Checks, whether position of pieces on the chessboard is legal (reachable
     * by following FIDE rules from starting position).
     *
     * @param c the position of piece on the board
     * @return true if the position is legal, false otherwise
     */
    private boolean isLegalPosition(OldMutablePosition c) {
        //NEUPLNA IMPLEMENTACE!!! DODELAT!!!
        return true;
    }

    /**
     * This method checks, whether given moveInfo CAN represent castling
     * attempt. This method is ONLY called in move(MoveInfo moveInfo) method.
     *
     * @return true if given move was castling attempt, false otherwise
     * @param move the move to check, whether it can represent castling
     */
    private boolean wasCastling(Move move) {
        Piece piece = move.getPiece();
        Square from = move.getFrom();
        Square to = move.getTo();

        if (((piece.equals(Piece.WHITE_KING) && from.equals(Square.E1))
                && (to.equals(Square.G1) || to.equals(Square.C1)))
                || ((piece.equals(Piece.BLACK_KING) && from.equals(Square.E8))
                && (to.equals(Square.G8) || to.equals(Square.C8)))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets info about when the castling occurred so that we can eventually take
     * it back.
     *
     * @param move the castling move
     */
    private void setCastlingInfo(Move move) {
        if (move.getPiece().equals(Piece.WHITE_KING)) {
            whenWhiteCastled = movesPlayed.size();
        } else {
            whenBlackCastled = movesPlayed.size();
        }
    }

    /**
     * Moves castling rook. This should be only called after castling!!
     *
     * @param kingsDestinationSquare the castling king's destination square
     * (C8/G8 for black or C1/G1 for white)
     */
    private void moveCastlingRook(Square kingsDestinationSquare) {
        try {
            switch (kingsDestinationSquare) {
                case C8:
                    currentlyFocusedPosition.makeMove(new Move(Piece.BLACK_ROOK,
                            Square.A8, Square.D8));
                    break;
                case G8:
                    currentlyFocusedPosition.makeMove(new Move(Piece.BLACK_ROOK,
                            Square.H8, Square.F8));
                    break;
                case C1:
                    currentlyFocusedPosition.makeMove(new Move(Piece.WHITE_ROOK,
                            Square.A1, Square.D1));
                    break;
                case G1:
                    currentlyFocusedPosition.makeMove(new Move(Piece.WHITE_ROOK,
                            Square.H1, Square.F1));
                    break;
                default:
                    throw new IllegalStateException("King's dest. Square was"
                            + " not C8, G8, C1 or G1!");
            }
        } catch (PieceNotPresentException cbe) {
            cbe.printStackTrace();
        }
    }

    /**
     * Return's castling rook when taking back moves. This should be only used
     * after taking back castling move.
     *
     * @param kingsDestinationSquare the square on which king was after castling
     * (C8/G8 for black or C1/G1 for white)
     */
    private void returnCastledRook(Square kingsDestinationSquare) {
        try {
            switch (kingsDestinationSquare) {
                case C8:
                    currentlyFocusedPosition.makeMove(new Move(Piece.BLACK_ROOK,
                            Square.D8, Square.A8));
                    break;
                case G8:
                    currentlyFocusedPosition.makeMove(new Move(Piece.BLACK_ROOK,
                            Square.F8, Square.H8));
                    break;
                case C1:
                    currentlyFocusedPosition.makeMove(new Move(Piece.WHITE_ROOK,
                            Square.D1, Square.A1));
                    break;
                case G1:
                    currentlyFocusedPosition.makeMove(new Move(Piece.WHITE_ROOK,
                            Square.F1, Square.H1));
                    break;
                default:
                    throw new IllegalStateException("King's dest. Square was"
                            + " not C8, G8, C1 or G1!");
            }
        } catch (PieceNotPresentException cbe) {
            cbe.printStackTrace();
        }
    }

    /**
     * Checks whether given move could have been en passant pawn capture try.
     *
     * @param move the move about which we want to check if it is en passant
     * capture try
     * @return true if move info represents en passant capture try (its pawn
     * move from its fifth rank to adjacent file on its sixth rank, and there is
     * nothing on move's destination square), false otherwise
     */
    private boolean wasEnPassant(Move move) {
        Piece piece = move.getPiece();
        Square from = move.getFrom();
        Square to = move.getTo();
        if ((currentlyFocusedPosition.getPiece(to) == null)
                && ((piece.equals(Piece.WHITE_PAWN) && from.getRank() == 4 && (to.getFile() != from.getFile()))
                || (piece.equals(Piece.BLACK_PAWN) && from.getRank() == 3 && (to.getFile() != from.getFile())))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets information about when (which half-move from the beginning of the
     * game) the en passant capture occured, so that we can eventually take that
     * move back. This method should only be called after making en passant pawn
     * capture!!
     */
    private void setEnPassantInfo() {
        whichMovesWereEnPassant.add(movesPlayed.size() + 1);
    }

    /**
     * When making en passant capture we can't rely on standart piece-capturing
     * mechanism (removing piece from the "to" square) so we must use this
     * method to remove the en-passant captured pawn. This method should only be
     * used when capturing the pawn en-passant!!
     *
     * @param pawnsDestinationSquare The destination square of the pawn which
     * makes the enpassant capture
     */
    private void removeEnPassanPawn(Square pawnsDestinationSquare) {
        if (pawnsDestinationSquare.getRank() == 5) {
            currentlyFocusedPosition.removePiece(Square.getSquare(pawnsDestinationSquare.getFile(), 4));
            capturedPieces.add(Piece.BLACK_PAWN);
        } else if (pawnsDestinationSquare.getRank() == 2) {
            currentlyFocusedPosition.removePiece(Square.getSquare(pawnsDestinationSquare.getFile(), 3));
            capturedPieces.add(Piece.WHITE_PAWN);
        } else {
            throw new IllegalStateException("En Passant Capturing pawn not"
                    + " going to 6th or 3rd rank!");
        }
    }

    /**
     * When we are canceling last move and the last move was en-passant capture
     * we can't use the same captured-piece-returning mechanism as with normal
     * capture (return the piece to a last move's "to" square), so we must
     * return the pawn manually with this method.
     *
     * @param lastMove the move which was en-passant pawn capture (the behavior
     * is not defined if the last move was not en passant capture!!)
     */
    private void returnEnPassantPawn(Move lastMove) {
        Square to = lastMove.getTo();
        capturedPieces.removeLast();
        if (to.getRank() == 5) {
            currentlyFocusedPosition.putPiece(Piece.BLACK_PAWN, Square.getSquare(to.getFile(), 4));
        } else if (to.getRank() == 2) {
            currentlyFocusedPosition.putPiece(Piece.WHITE_PAWN, Square.getSquare(to.getFile(), 3));
        } else {
            throw new IllegalStateException("En Passant Capturing pawn not"
                    + " going to 6th or 3rd rank!");
        }
    }

    /**
     * Returns position after given half-move from the beginning of the game
     * (for example getPositionAfter(0) returns starting position and
     * getPositionAfter(2) returns position after white and black have both made
     * one move)
     *
     * @param halfmove the number of the half-move after which we want to
     * display the position of pieces on the board. It must be 0 <= halfmove <=
     * getMovesPlayed().size()
     */
    private void setPositionAfter(int halfmove) {
        if (halfmove < 0 || halfmove > movesPlayed.size()) {
            throw new IllegalArgumentException("halfmove must be between 0"
                    + " and movesPlayed.size()");
        }
        currentlyFocusedPosition.setInitialPosition();
        for (int i = 0; i < halfmove; i++) {
            try {
                currentlyFocusedPosition.makeMove(movesPlayed.get(i));
            } catch (PieceNotPresentException ex) {
                ex.printStackTrace();
            }

            if (i != 0) {
                if (((i + 1) == whenWhiteCastled) || ((i + 1) == whenBlackCastled)) {
                    moveCastlingRook(movesPlayed.get(i).getTo());
                } else if (whichMovesWereEnPassant.contains(i + 1)) {
                    removeEnPassanPawn(movesPlayed.get(i).getTo());
                }
            }
        }
        currentlyViewedHalfmove = halfmove;
    }

    /**
     * Returns the String representation of the moves played in this game.
     *
     * @param highligtCurrentMove true if the current move should be highlighted
     * in the returned string false otherwise
     * @return the String representation of the moves played in this game.
     */
    public String getMovetextSectionString(boolean highligtCurrentMove) {
        StringBuilder sb = new StringBuilder();
        iteratingGame = new OldGameStateMutable();

        for (int i = 0; i < movesPlayed.size(); i++) {

            if (i % 2 == 0) { //pred tahy bileho davame cislovani
                sb.append((i / 2) + 1).append(". ");
            }
            if (highligtCurrentMove && (i + 1 == currentlyViewedHalfmove)) {
                sb.append("_");
            }
            sb.append(desambigMove(movesPlayed.get(i))); //zapiseme tah

            //a navic pokud to byl mat/sach/obyc. tah pridame #/+
            if ((i == movesPlayed.size() - 1) && isMate()) {
                sb.append("#");
            } else if (checks.get(i).equals(Boolean.TRUE)) {
                sb.append("+ ");
            }

            if (highligtCurrentMove && (i + 1 == currentlyViewedHalfmove)) {
                sb.append("_ ");
            } else {
                sb.append(" ");
            }
        }
        if (gameHeader.getResult() == null && !isMate) {
            sb.append(" *");
        } else if (isMate) {
            sb.append((isWhiteToMove() ? " 0-1" : " 1-0"));
        } else {
            sb.append(gameHeader.getResult());
        }
        sb.append("\n");
        return sb.toString();
    }

    private String desambigMove(Move move) {
        StringBuilder resultStr = new StringBuilder();
        Piece piece = move.getPiece();
        Square from = move.getFrom();
        Square to = move.getTo();

        switch (piece) {
            case WHITE_KING:
            case BLACK_KING:
                resultStr.append("K");
                //rosada ma specialni znaceni
                if ((from.equals(Square.E1) && to.equals(Square.G1))
                        || (from.equals(Square.E8) && to.equals(Square.G8))) {
                    resultStr = new StringBuilder("O-O");
                } else if ((from.equals(Square.E1) && to.equals(Square.C1))
                        || (from.equals(Square.E8) && to.equals(Square.C8))) {
                    resultStr = new StringBuilder("O-O-O");
                }
                break;
            case WHITE_QUEEN:
            case BLACK_QUEEN:
                resultStr.append("Q");
                break;
            case WHITE_ROOK:
            case BLACK_ROOK:
                resultStr.append("R");
                break;
            case WHITE_KNIGHT:
            case BLACK_KNIGHT:
                resultStr.append("N");
                break;
            case WHITE_BISHOP:
            case BLACK_BISHOP:
                resultStr.append("B");
                break;
            case WHITE_PAWN:
            case BLACK_PAWN:
                //brani pesakem ma predponu "<from-file-letter>x"
                if (from.getFile() != to.getFile()) {
                    resultStr.append(from.toString().toLowerCase().charAt(0));
                    resultStr.append("x");
                }
                break;
            default:
                break;
        }

        //zjisti jeslti na dane "to" square muze jit vic pisu stejneho typu
        //pokud jo pridej desabmig char
        if (!piece.equals(Piece.WHITE_PAWN)
                && !piece.equals(Piece.BLACK_PAWN)
                && !piece.equals(Piece.WHITE_KING)
                && !piece.equals(Piece.BLACK_KING)) {

            ArrayList<Square> squaresWithPiece = new ArrayList<>();
            Square[] potentFromSquares = Square.getSquares(
                    BitboardManager.getReachableSquaresBB(piece, to));

            for (Square s : potentFromSquares) { //najdem vsechny vyskyty piecu
                if (piece.equals(iteratingGame.getChessboard().getPiece(s)) //je-li na nem dany piece

                        //a pritom ten piece muze v tom stavy hry legalne tahnou na "to"
                        && ruleChecker.checkMove(iteratingGame, new Move(piece, s, to)).isLegal()) {
                    //System.out.println("..mam jednoho na " + s);
                    squaresWithPiece.add(s);
                }
            }
            if (squaresWithPiece.size() != 1 && squaresWithPiece.size() != 2) {
                throw new IllegalStateException("there is more than 2 piece"
                        + " that can go to given \"to\" square! There are "
                        + squaresWithPiece.size());
            }
            if (squaresWithPiece.size() == 2) {
                //System.out.println("Nejednoznacnost - na " + to + " muze jet " + piece + " z " + squaresWithPiece.get(0) + " i " + squaresWithPiece.get(1) + "!");
                char desambigChar;
                //urcime jake desambiguacni info je treba
                if (squaresWithPiece.get(0).getFile() != squaresWithPiece.get(1).getFile()) { //ruzny file > desamb. filem
                    desambigChar = from.toString().toLowerCase().charAt(0);
                    //System.out.println("desambiguace je mozna pouzitim file leteru" + desambigChar);
                } else {
                    desambigChar = from.toString().toLowerCase().charAt(1);
                    //System.out.println("desambiguace urcila rank s idx " + desambigChar);
                }
                resultStr.append(desambigChar);
            }

        }

        iteratingGame.move(move);

        if (!piece.equals(Piece.WHITE_PAWN)
                && !piece.equals(Piece.BLACK_PAWN)) {
            if (iteratingGame.getCaptures().getLast().equals(Boolean.valueOf(true))) {
                resultStr.append("x");
            }
        }

        //pokud to neni rosada appendni "to" square
        if (!resultStr.toString().startsWith("O")) {
            resultStr.append(to.toString().toLowerCase());
        }

        //pokud to je povyseni pesaka pridej "=<first-letter-of-promo-piece>"
        if (move instanceof Promotion) {
            Piece promoPiece = ((Promotion) move).getPromoPiece();
            resultStr.append("=");
            resultStr.append(promoPiece.toString().charAt(6)); //TODO - bug -will NOT work for KNIGHT cause their charAt(6) is K instead of N as it should be
        }

        return resultStr.toString();
    }
//////////////////////////////////////// PRIVATE FIELDS
    /**
     * Stores position which is currently being displayed in the GUI (this can
     * be any setPreviousPosition position which occurred since the beginning of
     * the game).
     */
    private OldMutablePosition currentlyFocusedPosition;
    /**
     * The number of half-move after which the position on the board is
     * displayed.
     */
    private int currentlyViewedHalfmove;
    /**
     * List of captured pieces captured pieces.
     */
    private final LinkedList<Piece> capturedPieces;
    /**
     * Stores the moves played since the beginning of the game.
     */
    private final LinkedList<Move> movesPlayed;
    /**
     * Stores information about who is to move.
     */
    private boolean isWhiteToMove;
    /**
     * Implementation of ruleChecker used to check the legality of moves.
     */
    private OldRuleChecker ruleChecker;
    /**
     * Flag determining whether there is mate in the position (and therfore no
     * further moves are allowed).
     */
    private boolean isMate;
    /**
     * Set of masks for easier manipulation with castlingAvailabilityTracker.
     */
    private static final int BLACK_KING_MASK = 1;
    private static final int WHITE_KING_MASK = 2;
    private static final int BLACK_Q_ROOK_MASK = 4;
    private static final int BLACK_K_ROOK_MASK = 8;
    private static final int WHITE_Q_ROOK_MASK = 16;
    private static final int WHITE_K_ROOK_MASK = 32;
    /**
     * This integer stores information about which of the "castling-relevant"
     * pieces (kings & rooks) has/has not moved.
     */
    private int castlingAvailabilityTracker;
 
    //Following 6 fields are used for potential "take-back-last-move"
    /**
     * To keep track which moves were/were not captures.
     */
    private final LinkedList<Boolean> captures;
    /**
     * To keep track which moves were/were not checks.
     */
    private final LinkedList<Boolean> checks;
    /**
     * To keep track of castling availabilities.
     */
    private final LinkedList<Integer> castlingAvailabilities;
    /**
     * To keep track of When players castled, so that we can take back that move
     * eventually. The info is stored as number of half-move, when they castled.
     */
    private int whenWhiteCastled = 0;
    /**
     * The number of the half-move which in which black player castled.
     */
    private int whenBlackCastled = 0;
    /**
     * To keep track which moves were enPassant captures so that we can
     * eventually take those moves back
     */
    private LinkedList<Integer> whichMovesWereEnPassant =
            new LinkedList<>();
    /**
     * Other non-state related information about the game.
     */
    private GameHeader gameHeader;
    private OldGameStateMutable iteratingGame;

    /**
     * Returns information about which moves were captures.
     *
     * @return information about which moves were captures. getCaptures().get(i)
     * is true <=> i-th move was capture.
     */
    LinkedList<Boolean> getCaptures() {
        return captures;
    }
}