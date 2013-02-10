package cz.janhrcek.chess.FEN;

import cz.janhrcek.chess.model.GameState;

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

    public static String gameStateToFen(GameState state) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Checks, whether this string is valid FEN string. Valid fen string has the
     * following properties: <ul> <li>it is not null</li> <li>must contain
     * exactly 6 fields separated by space</li> <li>first component represents
     * piece positioning</li> <ul> <li>must contain exactly 8 components (1 per
     * rank) separated by "/"</li> <li></li> <li></li> <li></li> <li></li>
     * <li></li> </ul>
     *
     * <li></li> <li></li> <li></li> <li></li> <li></li> <li></li> <li></li>
     * <li></li>
     *
     * </ul>
     */
    private static void checkFenValidity(String fenString) throws InvalidFenException {
        if (fenString == null) {
            throw new NullPointerException("fenString must not be null!");
        }

        String[] fenFields = fenString.split(" ");
        if (fenFields.length != 6) {
            throw new InvalidFenException("Fen string must have exactly 6 fields"
                    + " separated by spaces, but this one (" + fenString
                    + ") had " + fenFields.length);
        }
        checkPiecePlacementSubstring(fenFields[0]);
        //TODO check active color - only 1 letter w or b
        //TODO check castling availability string
        //TODO check en-passant target square
        //TODO check halfmove clock
        //TODO check fullmove number
        

    }

    private static void checkPiecePlacementSubstring(String piecePlacement) throws InvalidFenException {
        String[] ranks = piecePlacement.split("/");
        if (ranks.length != 8) {
            throw new InvalidFenException("piecePlacement field of FEN must have"
                    + " info about 8 ranks, each of them separated by \"\\\""
                    + " but this one (" + piecePlacement + ") had "
                    + ranks.length + ranks);
        }
        //TODO check that there are only letters kqrbnpKQRBNP and digits
        //TODO check that they sum up to correct value (8 per row)
    }
}
