package cz.janhrcek.chess.FEN;

import cz.janhrcek.chess.model.GameState;
import cz.janhrcek.chess.model.Piece;
import cz.janhrcek.chess.model.Position;
import cz.janhrcek.chess.model.Square;
import static java.lang.String.format;
import java.util.Arrays;
import java.util.List;

/**
 * TODO: implement this
 *
 * @author jhrcek
 */
public class FenParser {

    public static GameState fenToGameState(String fen) throws InvalidFenException {
        checkFenValidity(fen);
        return new GameState();
    }

    public static Position getPiecePlacement(String fen) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static String gameStateToFen(GameState state) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static String position2Fen(Position position) {
        StringBuilder sb = new StringBuilder(64);
        int counter = 0;
        for (Square sq : Square.values()) {
            Piece p = position.getPiece(sq);
            if (p != null) {
                if (counter != 0) { //empty the counter before each piece
                    sb.append(counter);
                    counter = 0;
                }
                sb.append(p.getFenName());
            } else {
                counter++;
            }
            if (sq.getFile() == 7) {
                if (counter != 0) {
                    sb.append(counter);
                    counter = 0;
                }
                if (sq.getRank() != 0) {
                    sb.append("/");
                }
            }
        }
        return sb.toString();
    }

    /**
     * Checks, whether this string is valid FEN string. Valid fen string has the
     * following properties: <ul> <li>it is not null</li> <li>must contain
     * exactly 6 fields separated by space</li> <li>1st field: represents piece
     * positioning and additionally conforms to the following restrictions:</li>
     * <ul> <li>contains exactly 8 substrings (1 per rank) separated by
     * "/"</li><li>only contains letters: pnbrqkPNBRQK12345678/</li> <li>each
     * component (rank) "sums up" to 8 (sum of all digits + 1 for each
     * piece)</li> </ul>
     *
     * <li>2nd field: </li> <li>3rd field:</li> <li>4th field: </li> <li>5th
     * field: </li> <li>6th field:</li> </ul>
     */
    private static void checkFenValidity(String fenString) throws InvalidFenException {
        if (fenString == null) {
            throw new NullPointerException("fenString must not be null!");
        }

        String[] fenFields = fenString.split(" ");
        if (fenFields.length != 6) {
            throw new InvalidFenException(format(sixFieldsMessage, fenString, fenFields.length));
        }
        checkPiecePlacementSubstring(fenFields[0]);

        //check active color - only 1 letter w or b
        if (!"w".equals(fenFields[1]) && !"b".equals(fenFields[1])) {
            throw new InvalidFenException("The 2nd field of FEN must be either"
                    + " letter w or b, but yours was: " + fenFields[1]);
        }

        //check castling availability string
        if (!validCAStrings.contains(fenFields[2])) {
            throw new InvalidFenException("The 3rd field of FEN must be consists"
                    + " of letters KQkq in alphabetical order or be -, but yours"
                    + " was: " + fenFields[2]);
        }

        //check en-passant target square

        //TODO check halfmove clock
        //TODO check fullmove number


    }

    private static void checkPiecePlacementSubstring(String piecePlacement) throws InvalidFenException {
        String[] ranks = piecePlacement.split("/");
        //Check there are exactly 8 ranks, separated by 
        if (ranks.length != 8) {
            throw new InvalidFenException("piecePlacement field of FEN must have"
                    + " info about 8 ranks, each of them separated by \"/\""
                    + " but this one (" + piecePlacement + ") had "
                    + ranks.length + ranks);
        }

        //Check, that there are only correct characters (piece FEN names + digits 0-8 and slashes "/"
        if (!piecePlacement.matches("^[pnbrqkPNBRQK0-8/]+$")) {
            throw new InvalidFenException("piecePlacement must only contain the"
                    + " following charasters: pnbrqkPNBRQK012345678 - but yours"
                    + " contained something else: " + piecePlacement);
        }

        //Check, that each rank "sums up" to 8 (sum of all digits digits summed + 1 for each piece)
        for (int i = 0; i < 8; i++) {
            int rankSum = sumRank(ranks[i]);
            if (rankSum != 8) {
                throw new InvalidFenException("Each rank in piecePlacement must"
                        + " sum up to 8 (sum all digits + 1 for each piece), but"
                        + " your rank (" + ranks[i] + ") summed to " + rankSum);
            }
        }
    }

    /**
     * Given fen string of rank (one of the 8 things you get by splitting the
     * piece Placement substring of fen around "/" character) it computes the
     * number of squares on that rank (i.e. sums all digits + adds 1 for each
     * piece)
     *
     * @param fenStringOfRank
     * @return
     */
    private static int sumRank(String fenStringOfRank) {
        int sum = 0;
        for (char c : fenStringOfRank.toCharArray()) {
            sum += Character.isDigit(c) ? Character.getNumericValue(c) : 1;
        }
        return sum;
    }
    private static final List<String> validCAStrings =
            Arrays.asList(new String[]{"KQkq", "KQk", "KQq", "KQ", "Kkq", "Kk",
                "Kq", "K", "Qkq", "Qk", "Qq", "Q", "kq", "k", "q", "-"});
    private static final String sixFieldsMessage = "Fen string must have exactly 6 fields separated by spaces, but this one ({}) had {}";
}