package zzz.to.delete;

import zzz.to.delete.OldMoveType;
import zzz.to.delete.OldRuleChecker;
import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.enums.Piece;
import static cz.janhrcek.chess.model.api.enums.Piece.*;
import cz.janhrcek.chess.model.api.enums.Square;
import cz.janhrcek.chess.rules.BitboardManager;
import zzz.to.delete.OldGameStateMutable;
import zzz.to.delete.OldMutablePosition;

/**
 * This implementation of Rule checker encapsulates the international standard
 * rules of chess, that is the rules of the FIDE (Federation Internationale des
 * echecs - World Chess Federation).
 *
 * @author Jan Hrcek
 * @version 0.9
 */
public class OldFIDERules implements OldRuleChecker {

    /**
     * Checks legality of the given move in the given state of the game.
     *
     * @param state the state of game in which we would like to move a piece
     * from square to square and we want to know whether this move is legal.
     * @param move information about the move (which pice, from where, to where)
     * of which we want to check legality
     * @return OldMoveType object representing information about the legality of
     * the move.
     */
    @Override
    public final OldMoveType checkMove(final OldGameStateMutable state, final Move move) {
        if (state == null) {
            throw new NullPointerException("state can't be null!");
        }
        if (move == null) {
            throw new NullPointerException("move can't be null!");
        }

        OldMutablePosition position = state.getChessboard();
        Piece piece = move.getPiece();
        Square from = move.getFrom();
        Square to = move.getTo();

        //1. Kontrola spravnosti barvy tahajici figury
        if (!isRightColor(state, piece)) {
            return OldMoveType.ILLEGAL_WRONG_COLOR;
        }

        //2. Kontrole "Geometrie" tahu, neboli:
        // (2.a) Taha figura podle svych pravidel (strelec diagonalne, atd..)
        // (2.b) Nestoji jakakoli figura v ceste tahu (nesmi se preskakovat)
        // (2.c) Nestoji PRATELSKA figura na cilovem poli
        // (2.d) +dalsi featurky pro pesaka
        OldMoveType geomTestResultType =
                canGeometricallyMove(position, piece, from, to);
        //pokud je jakakoli ilegalita
        if (!geomTestResultType.isLegal()) {
            return geomTestResultType;
        }

        //3. Byl by po provedeni tahu kral tahajiciho v sachu (coz je ilegalni)?
        OldMoveType leavesKingInCheckResultType =
                leavesKingInCheck(move, state);
        if (!leavesKingInCheckResultType.isLegal()) {
            return leavesKingInCheckResultType;
        }

        //4. Osetreni specialniho tahu: en passant
        //zdetekujeme-li pokus o tah pesakem jako by slo o en passant
        if ((position.getPiece(to) == null)
                && ((piece.equals(WHITE_PAWN) && from.getRank() == 4 && (to.getFile() != from.getFile()))
                || (piece.equals(BLACK_PAWN) && from.getRank() == 3 && (to.getFile() != from.getFile())))) {
            //zjistime jeslti je legalni provest to e.p. capture
            OldMoveType enPassantLegalityTestResult =
                    checkEnPassantCapture(move, state);
            if (!enPassantLegalityTestResult.isLegal()) {
                return enPassantLegalityTestResult;
            }
            removeEnPassantPawn(state);
        }

        //5. Osetreni specialniho tahu: Rosada (castling)
        if (((piece.equals(WHITE_KING) && from.equals(Square.E1)) && (to.equals(Square.G1) || to.equals(Square.C1)))
                || ((piece.equals(BLACK_KING) && from.equals(Square.E8)) && (to.equals(Square.G8) || to.equals(Square.C8)))) {
            OldMoveType castlingAvailabilityTestResult =
                    checkCastlingLegality(state, to);
            if (!castlingAvailabilityTestResult.isLegal()) {
                return castlingAvailabilityTestResult;
            }
        }

        //6. Dava tento tah sach?
        OldMoveType givesCheckResultType = givesCheck(move, state);

        if (givesCheckResultType.equals(OldMoveType.LEGAL_CHECK)) {
            //7. Vime, ze dava sach. Dava tento tah i mat?
            if (givesMate(move, state)) {
                return OldMoveType.LEGAL_MATE;
            } else {
                return OldMoveType.LEGAL_CHECK;
            }
        } else {
            //vsechny test OK, neni to sach ani mat
            //-> je to "obycejny" legalni tah
            return OldMoveType.LEGAL;
        }
    }


    /*--------------------------------------------*/
    /*---------- PRIVATE IMPLEMENTATION ----------*/
    /*--------------------------------------------*/
//pomocna metoda k canGeometricallyMove
    /**
     * Checks, whether the path of movement of some piece is blocked by another
     * piece in the given position. The path is blocked if there is ANY piece on
     * ANY square between starting and destination square.
     *
     * @param position the position of pieces on the board
     * @param from the starting square on the path
     * @param to destination square on the path
     * @return true, if the path of movement of some piece is blocked on the
     * given position by some piece, false otherwise
     */
    private static boolean isPathOfMoveBlocked(final OldMutablePosition position,
            final Square from,
            final Square to) {
        if (position == null) {
            throw new NullPointerException("position can't be null!");
        }
        if (from == null) {
            throw new NullPointerException("from can't be null!");
        }
        if (to == null) {
            throw new NullPointerException("to can't be null!");
        }

        java.util.List<Square> path = getSquaresOnPath(from, to);
        Piece tmpPiece;

        for (int i = 0; i < path.size(); i++) {
            tmpPiece = position.getPiece(path.get(i));
            if (tmpPiece != null) { // if there is ANY piece on path
                return true;        // between "from" and "to"
            }                       // then the path is blocked
        }
        return false;
    }

//pomocna metoda k isPathOfMoveBlocked
    /**
     * Returns ordered list of squares on the path between square "from" and
     * square "to". This method will throw the IllegalArgumentException if the
     * squares don't lie on some path (file, rank or diagonal).
     *
     * @param from starting square of the path
     * @param to ending square of the path
     * @return ordered list of squares between "from" and "to" squares
     */
    private static java.util.List<Square> getSquaresOnPath(final Square from,
            final Square to) {
        if (from == null) {
            throw new NullPointerException("from can't be null!");
        }
        if (to == null) {
            throw new NullPointerException("to can't be null!");
        }
        if (!isPath(from, to)) {
            throw new IllegalArgumentException("The squares from and to"
                    + " must be on the same file, rank or diagonal!"
                    + " But were: from = " + from + " to = " + to);
        }
        java.util.List<Square> path = new java.util.ArrayList<Square>();
        int direction = getDirectionOfPath(from, to);
        int ff = from.getFile();
        int fr = from.getRank();
        int tf = to.getFile();
        int tr = to.getRank();

        switch (direction) {
            case 1:
                for (int i = fr + 1; i < tr; i++) {
                    path.add(Square.getSquare(ff, i));
                }
                break;
            case 2:
                for (int i = ff + 1; i < tf; i++) {
                    path.add(Square.getSquare(i, i - tf + tr));
                }
                break;
            case 3:
                for (int i = ff + 1; i < tf; i++) {
                    path.add(Square.getSquare(i, fr));
                }
                break;
            case 4:
                for (int i = ff + 1; i < tf; i++) {
                    path.add(Square.getSquare(i, tf + tr - i));
                }
                break;
            case 5:
                for (int i = fr - 1; i > tr; i--) {
                    path.add(Square.getSquare(ff, i));
                }
                break;
            case 6:
                for (int i = ff - 1; i > tf; i--) {
                    path.add(Square.getSquare(i, i - tf + tr));
                }
                break;
            case 7:
                for (int i = ff - 1; i > tf; i--) {
                    path.add(Square.getSquare(i, fr));
                }
                break;
            case 8:
                for (int i = ff - 1; i > tf; i--) {
                    path.add(Square.getSquare(i, tf + tr - i));
                }
                break;
            default: //should never get here
                throw new IllegalStateException("Direction of path was"
                        + " not number from {1,2,..,8}");
        }
        return path;
    }

//pomocna metoda k isPathOfMoveBlocked
    /**
     * Returns number from {1, 2,...,8} represening the direction of the path
     * from square from to square to. 1 represents North, 2 North-East, ...,7
     * West, 8 North-West
     *
     * @param from starting square
     * @param to destination square
     * @return number from {1, 2,...,8} representing direction (1=North,
     * 2=NorthEast, 3=East, ..., 8=NorthWest)
     */
    private static int getDirectionOfPath(final Square from,
            final Square to) {
        if (!isPath(from, to)) {
            throw new IllegalArgumentException("The squares from and to"
                    + " must be on the same file, rank or diagonal!"
                    + " But were: from = " + from + " to = " + to);
        }
        int ff = from.getFile();
        int fr = from.getRank();
        int tf = to.getFile();
        int tr = to.getRank();

        if (ff == tf) { //same file
            if (fr > tr) {
                return 5;
            } else {
                return 1;
            }
        } else if (fr == tr) { //same rank
            if (ff > tf) {
                return 7;
            } else {
                return 3;
            }
        } else if (ff + fr == tf + tr) { //lefttop-rightbottom diagonal
            if (ff > tf) {
                return 8;
            } else {
                return 4;
            }
        } else if (ff - fr == tf - tr) { //leftbottom-righttop diagonal
            if (ff > tf) {
                return 6;
            } else {
                return 2;
            }
        }
        throw new IllegalStateException("Error! Should never get here!");
    }

//pomocna metoda k isPathOfMoveBlocked
    /**
     * Checks, whether two squares lie on the path (that is on same file, rank
     * or diagonal). If the squares are identical they are not considered to
     * form a path.
     *
     * @param from starting square
     * @param to destination square
     * @return true if those two squares are on the same rank, file or diagonal
     * AND are not identical at the same time<\br> false otherwise
     */
    private static boolean isPath(final Square from, final Square to) {
        int ff = from.getFile();
        int fr = from.getRank();
        int tf = to.getFile();
        int tr = to.getRank();

        if (!from.equals(to) //Must not be identical
                && (ff == tf //squares on same file
                || fr == tr //squares on same rank
                || ff + fr == tf + tr //squares on same diagonal
                || ff - fr == tf - tr)) {
            return true;
        } else {
            return false;
        }
    }

//test na "geometrickou legalnost"
    /**
     * Checks whether in the given gamestate given piece can move from given
     * square to given square. Here we don't check the "right-color-to-move"
     * rule nor whether king is left in check after that move nor any other
     * rules.
     *
     * @param position position on pieces on the board
     * @param piece the piece we want to move in this position
     * @param from the square from which we want to move the piece
     * @param to the square to which we want to move the piece
     * @return OldMoveType object representing legality type of checked move
     */
    private static OldMoveType canGeometricallyMove(final OldMutablePosition position,
            final Piece piece,
            final Square from,
            final Square to) {
        // i) muze tam ta figura jit na prazdne sachovnici?
        if (!BitboardManager.canGo(piece, from, to)) {
            return OldMoveType.ILLEGAL_PIECE_MOVE;
        }

        // ii) nestoji nejaka figura v ceste tahu?
        // (ma smysl testovat jen pro Q,R,B a P -bo 2sq 1.tah)
        if (!piece.equals(WHITE_KNIGHT)
                && !piece.equals(BLACK_KNIGHT)
                && !piece.equals(WHITE_KING)
                && !piece.equals(BLACK_KING)) {
            if (isPathOfMoveBlocked(position, from, to)) {
                return OldMoveType.ILLEGAL_PATH_BLOCKED;
            }
        }

        //iii) Je na cilovem poli pratelska figura?
        Piece pieceOnDestSq = position.getPiece(to);
        if (piece.equals(WHITE_PAWN) //pro pesaky specific pravidla
                || piece.equals(BLACK_PAWN)) {
            if (from.getFile() == to.getFile()) {  //pohyb po stejnem sloupci
                if (pieceOnDestSq != null) { //nesmi tam byt ZADNA figura
                    return OldMoveType.ILLEGAL_SQUARE_OCCUPIED;
                }
            } else { //pohyb pesaka po diagonale
                //jde o en passant pokus
                if (((piece.equals(WHITE_PAWN) && from.getRank() == 4)
                        || (piece.equals(BLACK_PAWN) && from.getRank() == 3))
                        && (pieceOnDestSq == null)) {
                    return OldMoveType.LEGAL; //tu ho povolime a checknem ho pak zvlast
                } else { //nejde o en passant pokus
                    if (pieceOnDestSq == null //neni tam nic
                            || pieceOnDestSq.isWhite() == piece.isWhite()) { //ci je friend
                        return OldMoveType.ILLEGAL_PIECE_MOVE;
                    } else { //je to pokus o en passant
                        return OldMoveType.LEGAL;
                    }
                }
            }
        } else { //pro nepesaky: if je tam friendly piece -> nesmis
            if (pieceOnDestSq != null
                    && pieceOnDestSq.isWhite() == piece.isWhite()) {
                return OldMoveType.ILLEGAL_SQUARE_OCCUPIED;
            }
        }

        return OldMoveType.LEGAL; //vsechny testy prosly, vracime,
        //ze tah je (vzhledem k dosavadnim testum!) legalni
    }

//metoda na vyhledani sachujici figury (pokud existuje)
    /**
     * This method finds out whether in the given state of game one of the kings
     * (which one is determined by the flag youMeanWhite) is in check and finds
     * a square on which there is a piece which gives check.
     *
     * @param youMeanWhite -true -if you are searching for piece, which checks
     * white king or -false -if you are searching for piece, which checks black
     * king
     * @param position the position in which we are searching for checking piece
     * @return if there is a piece, which checks the king of selected color,
     * then this method returns the square on which this piece sits. If there is
     * no such piece, this method returns null
     */
    private static Square findCheckingPiece(final boolean youMeanWhite,
            final OldMutablePosition position) {
        //nejprv si najdem krale
        Square kingsSquare = findKing(youMeanWhite, position);
        Piece tmpPiece = null;

        //hledame checking piece
        for (Square s : Square.values()) {
            tmpPiece = position.getPiece(s);
            if ((tmpPiece != null) //je tam nejaky piece
                    // && je opacne barvy nez kral
                    && (tmpPiece.isWhite() != youMeanWhite)) {
                //pak zisti, jestli ten piece muze krale "geometricky" vzit
                //(event. pin na ten piece nema vliv na to ze dava sach!)
                if (canGeometricallyMove(position, tmpPiece, s,
                        kingsSquare).isLegal()) {
                    //checking piece nalezen -> vrat pole kde sedi
                    return s;
                }
            }
        }
        //nenasli jsme zadny checking piece, vracime null
        return null;
    }

//byl by po tomto tahu MUJ kral v sachu? (coz je ilegalni)
    /**
     * This method checks, whether given move in given state of game would leave
     * the king (friendly to moving piece) in check.
     *
     * @param move the move we want to check if it leaves the kin in check
     * @param state the sate of game in which we want to check it
     * @return OldMoveType.ILLEGAL_LEAVES_KING_IN_CHECK if given move would leave
     * the king in check OldMoveType.LEGAL otherwise
     */
    private static OldMoveType leavesKingInCheck(final Move move,
            final OldGameStateMutable state) {
        OldMoveType result;
        state.makeUncheckedMove(move); //zkusebne si ten tah udelame ...
        //a zjistime, zda existuje pole, na kterem je figura davajici sach
        if (findCheckingPiece(!state.isWhiteToMove(), state.getChessboard())
                != null) {
            result = OldMoveType.ILLEGAL_LEAVES_KING_IN_CHECK;
        } else {
            result = OldMoveType.LEGAL;
        }
        state.cancelLastMove(); //a pak kazdopadne vratime ten tah zpet
        return result;
    }

//bude po tomto tahu SOUPERUV kral v sachu? (Coz je legalni)
    /**
     * Checks, whether given move gives Check to the enemy king. IMPORTANT -this
     * method does NOT check, whether given move leaves moving player's king in
     * check, only if it GIVEC check to the enemy king.
     *
     * @param move the move about which we want to know whether it gives check
     * to the enemy king
     * @param state the state of game in which we would like to make the given
     * move
     * @return OldMoveType.LEGAL_CHECK if given move gives check when made in given
     * state of game<br> OldMoveType.LEGAL otherwise.
     */
    private static OldMoveType givesCheck(final Move move,
            final OldGameStateMutable state) {
        OldMoveType result;
        state.makeUncheckedMove(move); //zkusebne si ten tah udelame
        //existuje pole, na kterem je figura davajici sach?
        if (findCheckingPiece(state.isWhiteToMove(), state.getChessboard())
                != null) {
            result = OldMoveType.LEGAL_CHECK;
        } else {
            result = OldMoveType.LEGAL;
        }
        state.cancelLastMove(); //a pak kazdopadne vratime ten tah zpet
        return result;
    }

    /**
     * This method checks, whether given move in given state of game gives mate.
     * It only makes sense to call this method when we KNOW that given move
     * gives check!
     *
     * @param state state of game in which we check given move
     * @param move move we check whether it gives mate in given state of game
     * @return true if given move gives mate in given state of game <br> false
     * otherwise
     */
    private static boolean givesMate(final Move move,
            final OldGameStateMutable state) {
        state.makeUncheckedMove(move); //A:Zkusebne si ten tah udelame
        OldMutablePosition position = state.getChessboard();

        //najdeme krale, pro ktereho zjistujeme, jestli je v matu
        Piece king = //jde o cerneho nebo bileho?
                move.getPiece().isWhite()
                ? BLACK_KING : WHITE_KING;
        Square kingsSquare = findKing(king.isWhite(), position);

        //je li to mat pak musi byt splneny 3 podminky:
        //1) Kral nesmi mit moznost uniknout na nechranene pole (flight sq.)
        //2) Zadna kralo-barevna figura nesmi byt schopna vzit sachujici fig.
        //3) Zadna kralo-pratelska figura nesmi byt schopna blokovat sach
        //  (tj. postavit se do drahy sachujici figury)

        //overeni podminky 1) (muze kral uniknout?)
        Square potentFlightSq = null; //potencialni unikove pole pro krale
        Piece tmpPiece = null;
        //projdeme vsechny potencialni unikove pole a mrknem se jestli
        //kral nebude v sachu, kdyz na ne pojede -if najdem -> neni mat
        for (int rank = Math.min(7, kingsSquare.getRank() + 1);
                rank >= Math.max(0, kingsSquare.getRank() - 1); rank--) {
            for (int file = Math.max(0, kingsSquare.getFile() - 1);
                    file <= Math.min(7, kingsSquare.getFile() + 1); file++) {
                potentFlightSq = Square.getSquare(file, rank);
                tmpPiece = position.getPiece(potentFlightSq);
                if (tmpPiece != null && tmpPiece.isWhite() == king.isWhite()) {
                    continue; //tam king nemuze bo je tam pratelska figura
                } else {
                    if (leavesKingInCheck(
                            new Move(king, kingsSquare, potentFlightSq),
                            state).isLegal()) {
//System.out.println("King can escape to " + potentFlightSq + " from check.");
                        state.cancelLastMove(); //A:A pak ho vezmem z5
                        return false; //nasli jsme unik. pole -> neni to mat
                    }
                }
            }
        }

        //vime, ze kral nemuze uhnout, takze najdeme checking piece:
        Square sqOfCheckingPiece = findCheckingPiece(king.isWhite(), position);

        //overeni podminky 2) (muzem checkingPiece vzit?)
        for (Square s : Square.values()) {
            tmpPiece = position.getPiece(s);
            if (tmpPiece != null && tmpPiece.isWhite() == king.isWhite()) {
                //mame friendly piece, zkusime jestli muze vzit sachujici fig.
                if (canGeometricallyMove(//muze li ho vzit
                        position, tmpPiece, s, sqOfCheckingPiece).isLegal()) {
                    if (leavesKingInCheck(//a nenecha pritom krale v sachu
                            new Move(tmpPiece, s, sqOfCheckingPiece),
                            state).isLegal()) {
                        //jakmile najdeme piece, ktery muze sachujici
                        // figuru vzit, je jasne, ze to neni mat.
/*System.out.println("checking piece" + position.getPiece(sqOfCheckingPiece) 
                         + " can be captured by the " + tmpPiece + " on " + s);*/
                        state.cancelLastMove(); //A:A pak ho vezmem z5
                        return false; //muzem ju vzit -> neni to mat
                    }
                }
            }
        }

        //overeni podminky 3) (muzem sach blokovat?)
        Piece checkingPiece = position.getPiece(sqOfCheckingPiece);
        //pokud je sachujici figura jezdec, pak nema smysl testovat moznost
        // blokovani sachu predstavenim jine figury do drahy sachu -je to mat
        if (checkingPiece.equals(WHITE_KNIGHT)
                || checkingPiece.equals(BLACK_KNIGHT)) {
            state.cancelLastMove(); //A:A pak ho kazdopadne vezmem zpet
            return true;
        }

        java.util.List<Square> squaresOnPath =
                getSquaresOnPath(kingsSquare, sqOfCheckingPiece);
        //pro kazde pole na ceste sachu overime, zda na nej muzeme predstavit
        //kralovi-pratelskou figuru.
        for (Square sqOnPath : squaresOnPath) {
            for (Square s : Square.values()) {
                tmpPiece = position.getPiece(s);
                if (tmpPiece != null && tmpPiece.isWhite() == king.isWhite()) {
                    //Mame friend piece. Muze postavit do drahy sachu?
                    if (canGeometricallyMove(position, tmpPiece, s, sqOnPath)
                            .isLegal()) { //muze li tam jit
                        if (leavesKingInCheck(//a nenecha pritom krale v sachu
                                new Move(tmpPiece, s, sqOnPath),
                                state).isLegal()) {
                            //jakmile najdeme tah blokujici sach,
                            //je jasne, ze to neni mat.
//System.out.println("this check can be blocked by the " + tmpPiece + " on " + s);
                            state.cancelLastMove(); //A:A tah vemem z5
                            return false; //muzem ju vzit -> neni to mat
                        }
                    }
                }
            }
        }

        state.cancelLastMove(); //A:A pak ho kazdopadne vezmem zpet
        return true; //nenasli jsme "unikovy" tah ze sachu -> je to mat
    }

    /**
     * Finds given king in given position.
     *
     * @param youMeanWhite true if you want to find white king<br> false if you
     * want to find blakc king
     * @param position the position in which we shoul find given king
     * @return the square on which given king sits
     */
    private static Square findKing(final boolean youMeanWhite,
            final OldMutablePosition position) {
        Piece king = //jde o cerneho nebo bileho?
                youMeanWhite ? WHITE_KING : BLACK_KING;
        Piece tmpPiece = null;

        for (Square s : Square.values()) {
            tmpPiece = position.getPiece(s);
            if (tmpPiece != null && tmpPiece.equals(king)) {
                return s;
            }
        }
        //nenasli jsme krale -> vyjimka
        throw new IllegalStateException("There was not " + king
                + " on the board!");
    }

    private static OldMoveType checkCastlingLegality(OldGameStateMutable state,
            Square kingsTargetSquare) {
        String caString = state.getCastlingAvailability();
        switch (kingsTargetSquare) {//testneme dostupnost rosady v danem stavu hry
            case C8:
                if (!caString.contains("q")) {
                    return OldMoveType.ILLEGAL_CASTLING_NO_LONGER_AVAILABLE;
                }
                break;
            case G8:
                if (!caString.contains("k")) {
                    return OldMoveType.ILLEGAL_CASTLING_NO_LONGER_AVAILABLE;
                }
                break;
            case C1:
                if (!caString.contains("Q")) {
                    return OldMoveType.ILLEGAL_CASTLING_NO_LONGER_AVAILABLE;
                }
                break;
            case G1:
                if (!caString.contains("K")) {
                    return OldMoveType.ILLEGAL_CASTLING_NO_LONGER_AVAILABLE;
                }
                break;
            default:
                throw new IllegalArgumentException("when castling"
                        + " kingsTargetSquare can only be C8, G8, C1 or G1!");
        }
        //je-li rosada dostupna pak jeste testnem jestli je legalni (2 podminky)
        //1.pokud je kral v sachu rosada neni legalni
        if (state.getChecks().getLast().equals(Boolean.TRUE)) {
            return OldMoveType.ILLEGAL_CASTLING_KING_IN_CHECK;
        }
        //2. kral nesmi pri rosade prejit pres ohrozen pole
        //(nejdeme piece nepratelsy pis, ktery ohrozuje inkriminovane pole
        OldMutablePosition position = state.getChessboard();
        Piece tmpPiece;
        Square squareOnKingsCastlingPath =
                getSquareOnKingsCastlingPath(kingsTargetSquare);

        for (Square s : Square.values()) {
            tmpPiece = position.getPiece(s);
            if ((tmpPiece != null) //je tam nejaky piece
                    // && je opacne barvy nez kral
                    && (tmpPiece.isWhite() != state.isWhiteToMove())) {
                //pak zisti, jestli ten piece muze "geometricky" jit na to pole
                //(event. pin na ten piece nema vliv na to pole ohrozuje!)
                if (canGeometricallyMove(position, tmpPiece, s,
                        squareOnKingsCastlingPath).isLegal()) {
                    System.out.println("testuju, jestli v pozici " + position + "muze jit" + tmpPiece + " z " + s + " na " + squareOnKingsCastlingPath);
                    //ohrozujici nalezen -> nesmi se rosadovat
                    return OldMoveType.ILLEGAL_CASTLING_THROUGH_CHECK;
                }
            }
        }
        return OldMoveType.LEGAL;
    }

    /**
     * In this method we suppose that castling is legal, so we move the
     * appropriate rook to its right position near the king.
     *
     * @param board the chessboard on which we want to finish castling by moving
     * the rook
     * @param kingsTargetSquare the square to which king castles
     */
    private static void moveCastlingRook(OldMutablePosition board, Square kingsTargetSquare) {
        switch (kingsTargetSquare) {
            case C8:
                board.removePiece(Square.A8);
                board.putPiece(BLACK_ROOK, Square.D8);
                break;
            case G8:
                board.removePiece(Square.H8);
                board.putPiece(BLACK_ROOK, Square.F8);
                break;
            case C1:
                board.removePiece(Square.A1);
                board.putPiece(WHITE_ROOK, Square.D1);
                break;
            case G1:
                board.removePiece(Square.H1);
                board.putPiece(WHITE_ROOK, Square.F1);
                break;
            default:
                throw new IllegalArgumentException("when castling"
                        + " kingsTargetSquare can only be C8, G8, C1 or G1!");
        }
    }

    private static Square getSquareOnKingsCastlingPath(Square kingsTargetSquare) {
        switch (kingsTargetSquare) {
            case C8:
                return Square.D8;
            case G8:
                return Square.F8;
            case C1:
                return Square.D1;
            case G1:
                return Square.F1;
            default:
                throw new IllegalArgumentException("when castling"
                        + " kingsTargetSquare can only be C8, G8, C1 or G1!");
        }
    }

    /**
     * Checks whether in the given state of game it is right to move a piece of
     * given color.
     *
     * @param state state of game
     * @param piece the piece whose right to move (according to its color) we
     * want to check
     * @return true <=> it's white to move and the piece is white<br \> OR it's
     * black to move and the piece is black
     */
    private static boolean isRightColor(final OldGameStateMutable state,
            final Piece piece) {
        if (state.isWhiteToMove() == piece.isWhite()) {
            return true;
        } else {
            return false;
        }
    }

    private OldMoveType checkEnPassantCapture(Move move, OldGameStateMutable state) {
        Square enPassantTargetSquare = state.getEnPassantTargetSquare();
        if (enPassantTargetSquare != null
                && move.getTo().equals(enPassantTargetSquare)) {
            return OldMoveType.LEGAL;
        } else {
            return OldMoveType.ILLEGAL_EN_PASSANT;
        }
    }

    private void removeEnPassantPawn(OldGameStateMutable state) {
        Square epTargetSq = state.getEnPassantTargetSquare();
        Square pawnsSquare;

        if (state.isWhiteToMove()) {
            pawnsSquare = Square.getSquare(epTargetSq.getFile(), 4);
        } else {
            pawnsSquare = Square.getSquare(epTargetSq.getFile(), 3);
        }
        state.getChessboard().removePiece(pawnsSquare);
    }
}