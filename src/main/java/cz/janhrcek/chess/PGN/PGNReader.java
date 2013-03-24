package cz.janhrcek.chess.PGN;

import cz.janhrcek.chess.model.impl.OldGameStateMutable;
import cz.janhrcek.chess.model.impl.OldGameStateMutable.GameHeader;
import cz.janhrcek.chess.model.api.Move;
import cz.janhrcek.chess.model.api.enums.Piece;
import static cz.janhrcek.chess.model.api.enums.Piece.*;
import cz.janhrcek.chess.model.impl.MutablePosition;
import cz.janhrcek.chess.model.api.Promotion;
import cz.janhrcek.chess.model.api.enums.Square;
import cz.janhrcek.chess.rules.BitboardManager;
import cz.janhrcek.chess.rules.FIDERulesOld;
import cz.janhrcek.chess.rules.RuleCheckerOld;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Enables parsing files in reduced PGN export format.
 *
 * @author xhrcek
 */
public class PGNReader {

    /**
     * State information of the parser representing whether it currently parses
     * tag pair section of some game (true) or movetext section (false).
     */
    private static boolean parsingTagpairs;
    /**
     * When parsing movetext of some game we need to switch between players so
     * that we can easily determine the color of moving piece.
     */
    private static boolean isWhiteToMove;
    /**
     * The game which is currently being reconstructed from parsed pgn file.
     */
    private static OldGameStateMutable currentGame;
    /**
     * We need rule checker to find ambiguous SAN moves.
     */
    private static RuleCheckerOld ruleChecker = new FIDERulesOld();

    /**
     * Given pgn file this method returns list of games contained in the file.
     *
     * @param f the pgn file to be parsed.
     * @return The list of games stored in the file.
     */
    public static List<OldGameStateMutable> parseFile(File f) {
        if (f == null) {
            throw new NullPointerException("File can't be null!");
        }
        if (!f.isFile()) {
            throw new IllegalArgumentException("f should not be a directo"
                    + "ry, but was: " + f);
        }
        if (!f.getName().toLowerCase().endsWith(".pgn")) {
            throw new IllegalArgumentException("File must end with \".pgn\"");
        }

        ArrayList<OldGameStateMutable> listOfGames = new ArrayList<OldGameStateMutable>();
        BufferedReader input = null;
        parsingTagpairs = true;

        try {
            input = new BufferedReader(new FileReader(f));
            String line = null;

            currentGame = new OldGameStateMutable();
            OldGameStateMutable.GameHeader currentGameHeader = currentGame.getGameHeader();
            StringBuilder moveText = new StringBuilder();

            //po radcich precteme cely pgn file
            while ((line = input.readLine()) != null) {
                if (parsingTagpairs) { //parsujeme tagpair section
                    if (line.startsWith("[")) {
                        String[] pole = line.split("\"");
                        setAttribute(currentGameHeader,
                                pole[0].substring(1, pole[0].length() - 1), //attribute name
                                pole[1]);                              //attribute value
                    } else {
                        parsingTagpairs = false; //zahodime current lradek (dle PGN Spec. zarucene prazdny)
                    }
                } else { //parsujeme muvtext
                    if (!line.startsWith("[")) {
                        //pridej ten radek k movetextu
                        moveText.append(line);
                        moveText.append(" "); //mezera pro oddeleni radku
                    } else { //dosli jsme jednu hru, tedy parsnem jeji muvtext a zacnem novou hru
                        //System.out.println(currentGameHeader);////////////////////////
                        //parsuj muvtext a uzavri tu jednu hru
                        parseMoveText(moveText.toString());
                        moveText = new StringBuilder();
                        listOfGames.add(currentGame);
                        //a vytvor novou hru, jez zacnes parsovat
                        if (line.startsWith("[")) {
                            parsingTagpairs = true;
                            currentGame = new OldGameStateMutable();
                            currentGameHeader = currentGame.getGameHeader();
                            String[] pole = line.split("\"");
                            setAttribute(currentGameHeader,
                                    pole[0].substring(1, pole[0].length() - 1), //attribute name
                                    pole[1]);                                //attribute value
                        }
                    }
                }
            }
            //a dokoncime parsovani posledni hry
            //System.out.println(currentGameHeader);/////////////////////////
            parseMoveText(moveText.toString());
            listOfGames.add(currentGame);
            System.out.println("Uspesne rozparsovano " + listOfGames.size() + " her!");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return listOfGames;
    }

    /**
     * Sets attribute with givenName in given GameHeader to given attibute
     * value.
     *
     * @param header header of game in which we want to set attrName to
     * attrValue
     * @param attrName name of attribute to set
     * @param attrValue value of attribute which will be set
     */
    private static void setAttribute(GameHeader header, String attrName,
            String attrValue) {
        attrName = attrName.toLowerCase();
        switch (attrName) {
            case "event":
                header.setEvent(attrValue);
                break;
            case "site":
                header.setSite(attrValue);
                break;
            case "date":
                header.setDate(attrValue);
                break;
            case "round":
                header.setRound(attrValue);
                break;
            case "white":
                header.setWhite(attrValue);
                break;
            case "black":
                header.setBlack(attrValue);
                break;
            case "result":
                header.setResult(attrValue);
                break;
        }
    }

    /**
     * Parse given movetext and set state-changing information of currentGame.
     *
     * @param moveText String containing concatenated lines of movetext
     */
    private static void parseMoveText(String moveText) {
        //System.out.println(moveText);////////////////
        //remove all redundant characters (move numbers, dots, +'s, ...)
        String[] sanMoves = removeBalastChars(moveText);
        //System.out.println(java.util.Arrays.asList(sanMoves));///////////////

        isWhiteToMove = true;
        for (String sanMove : sanMoves) {
            if (isSanMove(sanMove)) { //v movetextu muze byt i vysledek hry.
                //System.out.println("Parsuju tah " + sanMove + " ...");
                Move nextMove = san2Move(sanMove);
                //System.out.print("vysledny tah je -> " + nextMove + "\n");
                currentGame.move(nextMove);
                changeSideToMove();
            }
        }
        System.out.println("Koncim parsovani movetextu hry: "
                + currentGame.getGameHeader().getWhite() + " vs. "
                + currentGame.getGameHeader().getBlack());
    }

    /**
     * Removes redundant characters and symbols from given movetext. For example
     * for input movetext "1.f3 e5 2.g4 Qh4# 0-1" the output is array [f3, e5,
     * g4, Qh4, 0-1].
     */
    private static String[] removeBalastChars(String moveText) {
        String[] pole = moveText.split(" ");

        for (int i = 0; i < pole.length; i++) {
            //odstran cislovani tahu s teckami
            if (pole[i].contains(".")) {
                pole[i] = pole[i].substring(pole[i].indexOf('.') + 1);
            }
            //odstran znaky '+'
            if (pole[i].endsWith("+") || pole[i].endsWith("#")) {
                pole[i] = pole[i].substring(0, pole[i].length() - 1);
            }
            //odstran 'x' symbolizujici capture
            if (pole[i].contains("x")) {
                pole[i] = pole[i].substring(0, pole[i].indexOf('x'))
                        + pole[i].substring(pole[i].indexOf('x') + 1, pole[i].length());
            }
        }
        return pole;
    }

    /**
     * This method checks, whether given moveTextToken can be token representing
     * move or is just some other information token contained in movetext (
     * result information, empty string etc).
     */
    private static boolean isSanMove(String moveTextToken) {
        //neni prazdny retezec ani to nenivysledek
        if (moveTextToken.length() != 0
                && !moveTextToken.startsWith("0")
                && !moveTextToken.startsWith("1")
                && !moveTextToken.startsWith("*")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method converts SAN (Short Algebraic Notation) String representation
     * of the move into MoveInfo Object.
     */
    private static Move san2Move(String sanMove) {
        if (sanMove == null) {
            throw new NullPointerException("sanMove can't be null!");
        }
        if (sanMove.length() < 2) {
            throw new IllegalArgumentException("no legal sanMove can have"
                    + " less than 2 characters!");
        }

        Piece piece = determinePiece(sanMove);
        Square to = determineTo(sanMove, piece);
        Square from = determineFrom(sanMove, piece, to);
        Piece toWhatPromote = determinePromoPiece(sanMove, piece);

        if (toWhatPromote == null) {
            return new Move(piece, from, to);
        } else {
            return new Promotion(piece, from, to, toWhatPromote);
        }
    }

    private static void changeSideToMove() {
        if (isWhiteToMove) {
            isWhiteToMove = false;
        } else {
            isWhiteToMove = true;
        }
    }

    private static Piece determinePiece(String sanMove) {
        char firstLetter = sanMove.charAt(0);
        Piece piece = null;

        //determine piece from the first letter
        switch (firstLetter) {
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
                piece = (isWhiteToMove ? WHITE_PAWN : BLACK_PAWN);
                break;
            case 'B':
                piece = (isWhiteToMove ? WHITE_BISHOP : BLACK_BISHOP);
                break;
            case 'K':
            case 'O': //rosada zacina Ockem (NE NULOU!)
                piece = (isWhiteToMove ? WHITE_KING : BLACK_KING);
                break;
            case 'N':
                piece = (isWhiteToMove ? WHITE_KNIGHT : BLACK_KNIGHT);
                break;
            case 'Q':
                piece = (isWhiteToMove ? WHITE_QUEEN : BLACK_QUEEN);
                break;
            case 'R':
                piece = (isWhiteToMove ? WHITE_ROOK : BLACK_ROOK);
                break;
            default:
                throw new IllegalStateException("I have sanMove, which does NOT"
                        + " start with [abcdefghBKNOQR]!");
        }
        return piece;
    }

    private static Piece determinePromoPiece(String sanMove, Piece piece) {
        Piece toWhatPromote;
        if (sanMove.contains("=")) {
            char lastChar = sanMove.charAt(sanMove.length() - 1);
            toWhatPromote = letterToPromoPiece(lastChar);
        } else {
            toWhatPromote = null;
        }
        return toWhatPromote;
    }

    private static Piece letterToPromoPiece(char letter) {
        switch (letter) {
            case 'Q':
                return (isWhiteToMove
                        ? WHITE_QUEEN : BLACK_QUEEN);
            case 'R':
                return (isWhiteToMove
                        ? WHITE_ROOK : BLACK_ROOK);
            case 'B':
                return (isWhiteToMove
                        ? WHITE_BISHOP : BLACK_BISHOP);
            case 'N':
                return (isWhiteToMove
                        ? WHITE_KNIGHT : BLACK_KNIGHT);
            default:
                throw new IllegalStateException("I got letter t"
                        + "hat is not in [QRBN]!");
        }
    }

    private static Square determineTo(String sanMove, Piece piece) {
        if (piece == null) {
            throw new NullPointerException("piece can't be null!");
        }
        Square to;

        switch (piece) {
            case WHITE_PAWN: //pro pesaky rozlisujem zda to je/neni promotion
            case BLACK_PAWN:
                if (sanMove.contains("=")) {
                    //je to promo => 4. - 3. znak od konce je "to"
                    to = Square.valueOf(
                            sanMove.substring(sanMove.length() - 4,
                            sanMove.length() - 2).toUpperCase());
                } else {
                    //kdyz to neni promotion posledni 2 znaky jsou "to"
                    to = Square.valueOf(
                            sanMove.substring(sanMove.length() - 2).toUpperCase());
                }
                break;
            case WHITE_KING: //pro ostatni figury je "to" vzdy urcen poslednimi
            case BLACK_KING: //dvemi znaky sanMoveu (s vyjimkou rosady)
            case WHITE_QUEEN:
            case BLACK_QUEEN:
            case WHITE_ROOK:
            case BLACK_ROOK:
            case WHITE_BISHOP:
            case BLACK_BISHOP:
            case WHITE_KNIGHT:
            case BLACK_KNIGHT:
                switch (sanMove) {
                    case "O-O":
                        to = (piece.isWhite() ? Square.G1 : Square.G8);
                        break;
                    case "O-O-O":
                        to = (piece.isWhite() ? Square.C1 : Square.C8);
                        break;
                    default:
                        to = Square.valueOf(sanMove.substring(sanMove.length() - 2).toUpperCase());
                        break;
                }
                break;
            default:
                throw new IllegalStateException("piece was not one of those"
                        + " defined in Chessmen enumeration!");
        }
        return to;
    }

    private static Square determineFrom(String sanMove, Piece piece, Square to) {
        if (to == null) {
            throw new NullPointerException("to can't be null!");
        }

        //pred vsim ostatnim vyresime rosadu
        if (sanMove.equals("O-O") || sanMove.equals("O-O-O")) {
            return (piece.isWhite() ? Square.E1 : Square.E8);
        }

        Square from = null;
        ArrayList<Square> squaresWithPiece = new ArrayList<Square>();
        Square[] potentFromSquares = Square.getSquares(
                BitboardManager.getReachableSquaresBB(piece, to));

        MutablePosition currBoard = currentGame.getChessboard();

        if (piece.equals(WHITE_PAWN) || piece.equals(BLACK_PAWN)) {
            int fileIdx = (int) sanMove.charAt(0) - 97;
            for (Square s : Square.values()) {
                if (s.getFile() == fileIdx) {
                    if (piece.equals(currBoard.getPiece(s))
                            && ruleChecker.checkMove(currentGame, new Move(piece, s, to)).isLegal()) {
                        from = s;
                        break;
                    }
                }
            }
        } else {
            for (Square s : potentFromSquares) { //najdem vsechny vyskyty piecu
                if (piece.equals(currBoard.getPiece(s)) //je-li na nem dany piece
                        //a pritom ten piece muze v tom stavy hry legalne tahnou na "to"
                        && ruleChecker.checkMove(currentGame, new Move(piece, s, to)).isLegal()) {
                    //System.out.println("Piece: " + piece + " potent from " + s + " to " + to);
                    squaresWithPiece.add(s);
                }
            }
            if (squaresWithPiece.size() == 1) {
                from = squaresWithPiece.get(0);
            } else {
                //ziskame desambiguacni info
                char desambigChar = sanMove.charAt(1);
                //System.out.println("Desambig Char = " + desambigChar);
                if (Character.isDigit(desambigChar)) {
                    int rankIdx = Character.digit(desambigChar, 10) - 1;
                    //System.out.println("desambiguace urcila rank s idx " + rankIdx);
                    for (Square s : squaresWithPiece) {
                        if (s.getRank() == rankIdx) {
                            from = s;
                            break;
                        }
                    }
                } else {
                    int fileIdx = desambigChar - 97;
                    //System.out.println("desambiguace urcila file s idx " + fileIdx);
                    for (Square s : squaresWithPiece) {
                        if (s.getFile() == (fileIdx)) {
                            from = s;
                            break;
                        }
                    }
                }
            }
        }

        return from;
    }
}
