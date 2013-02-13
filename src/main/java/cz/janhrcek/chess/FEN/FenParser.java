package cz.janhrcek.chess.FEN;

import cz.janhrcek.chess.model.api.CastlingAvailability;
import cz.janhrcek.chess.model.impl.GameStateImpl;
import cz.janhrcek.chess.model.api.Piece;
import cz.janhrcek.chess.model.impl.Position;
import cz.janhrcek.chess.model.api.Square;
import static java.lang.String.format;
import java.util.EnumSet;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jhrcek
 */
public class FenParser {

    public FenParser() {
    }

    public String stateToFen(GameStateImpl state) {
        LOG.info("Converting game state to FEN");
        StringBuilder sb = new StringBuilder(50);
        Square ep = state.getEnPassantTargetSquare();
        sb.append(position2Fen(state.getPosition()))
                .append(state.isWhiteToMove() ? " w " : " b ")
                .append(CastlingAvailability.toFenCaSubstring(state.getCastlingAvailabilities()))
                .append(ep == null ? " - " : ep.toString().toLowerCase())
                .append(state.getHalfmoveClock())
                .append(" ")
                .append(state.getFullmoveNumber());
        return sb.toString();
    }

    private String position2Fen(Position position) {
        StringBuilder sb = new StringBuilder(64);
        int counter = 0;
        for (Square sq : Square.values()) {
            Piece p = position.getPiece(sq);
            if (p != null) {
                if (counter != 0) { //empty the counter before each piece
                    sb.append(counter);
                    counter = 0;
                }
                sb.append(p.getFenLetter());
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
     * Parses given string, populating GameState object. If the parameter is
     * invalid fen string, an {@link InvalidFenException} is thrown. Valid fen
     * string has the following properties: <ul> <li>It is not null</li>
     * <li>Must contain exactly 6 fields separated by space</li><ul> <li>1st
     * field: represents piece positioning and additionally conforms to the
     * following restrictions:</li> <ul> <li>contains exactly 8 substrings (1
     * per rank) separated by "/"</li><li>only contains letters:
     * pnbrqkPNBRQK12345678/</li> <li>each component (rank) "sums up" to 8 (sum
     * of all digits + 1 for each piece)</li> </ul>
     *
     * <li>2nd field: is either w or b, representing player to move</li> <li>3rd
     * field: consists of (some of) the letters KQkq (in that order)
     * representing castling availability</li> <li>4th field: is either - or
     * target square for en-passant capture</li> <li>5th field: represents
     * half-move clock</li> <li>6th field: represents full-move number</li>
     * </ul></ul>
     */
    public GameStateImpl parse(String fenString) throws InvalidFenException {
        if (fenString == null) {
            throw new NullPointerException("fenString must not be null!");
        }
        LOG.info("Parsing FEN String: \"{}\"", fenString);
        GameStateImpl state = new GameStateImpl();

        //0. It must have 6 fields separated by spaces
        String[] fields = fenString.split(" ");
        if (fields.length != 6) {
            throw new InvalidFenException(format(SIX_FIELD_MSG, fenString, fields.length));
        }

        //1. Process placement string
        state.setPosition(parsePiecePlacement(fields[0]));

        //2. Check active color - only 1 letter w or b
        if (!"w".equals(fields[1]) && !"b".equals(fields[1])) {
            throw new InvalidFenException(format(PLAYER_FIELD_MSG, fields[1]));
        } else {
            state.setWhiteToMove("w".equals(fields[1]) ? true : false);
            LOG.debug("    2. It's {} to move", state.isWhiteToMove() ? "WHITE" : "BLACK");
        }

        //3. check castling availability string
        if (!CASTLING_AVAILABILITY_PATTERN.matcher(fields[2]).matches()) {
            throw new InvalidFenException(format(CA_FIELD_MSG, fields[2]));
        } else {
            EnumSet<CastlingAvailability> cas = CastlingAvailability.parseFenCaSubstring(fields[2]);
            state.setCastlingAvailabilities(cas);
            LOG.debug("    3. Castling availabilities: {}", cas);
        }

        //4. Check en-passant target square
        if (!EN_PASSANT_PATTERN.matcher(fields[3]).matches()) {
            throw new InvalidFenException(format(EP_FIELD_MSG, fields[3]));
        } else {
            if (!"-".equals(fields[3])) {
                Square epTarget = Square.valueOf(fields[3].toUpperCase());
                state.setEnPassantTargetSquare(epTarget);
            } //else it remains null
            LOG.debug("    4. En-passant target square: {}", state.getEnPassantTargetSquare());
        }

        //check halfmove clock & fullmove number
        if (!DIGIT_PATTERN.matcher(fields[4]).matches()
                || !DIGIT_PATTERN.matcher(fields[5]).matches()) {
            throw new InvalidFenException(format(COUNTERS_FILED_MSG, fields[4], fields[5]));
        } else {
            state.setHalfmoveClock(Integer.valueOf(fields[4]));
            LOG.debug("    5. Half-move clock: {}", state.getHalfmoveClock());
            state.setFullmoveNumber(Integer.valueOf(fields[5]));
            LOG.debug("    6. Full-move number: {}", state.getFullmoveNumber());
        }

        return state;
    }

    private Position parsePiecePlacement(String piecePlacement) throws InvalidFenException {
        String[] ranks = piecePlacement.split("/");
        //Check there are exactly 8 ranks, separated by "/"
        if (ranks.length != 8) {
            throw new InvalidFenException("piecePlacement field of FEN must have"
                    + " exactly 8 ranks, each of them separated by \"/\". But "
                    + "this one (" + piecePlacement + ") had "
                    + ranks.length + "ranks");
        }

        //Check, that there are only correct characters (piece FEN names + digits 0-8 and slashes "/"
        if (!piecePlacement.matches("^[pnbrqkPNBRQK1-8/]+$")) {
            throw new InvalidFenException("piecePlacement must only contain the"
                    + " following charasters: pnbrqkPNBRQK12345678 - but yours"
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

        Position position = new Position();
        for (int rankIdx = 7; rankIdx >= 0; rankIdx--) {
            int colIdx = 0;
            for (char c : ranks[7 - rankIdx].toCharArray()) {
                if (Character.isLetter(c)) { //it is letter -> put corresponding piece on
                    //LOG.debug("Putting {} on {}", Piece.getPiece(c), Square.getSquare(colIdx, rankIdx));
                    position.putPiece(Piece.getPiece(c), Square.getSquare(colIdx, rankIdx));
                    colIdx++;
                } else { //it is number --> move 'c' columns to the right
                    colIdx += Character.getNumericValue(c);
                }

            }
        }

        LOG.debug("    1. Position \n{}", position);
        return position;
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
    private int sumRank(String fenStringOfRank) {
        int sum = 0;
        for (char c : fenStringOfRank.toCharArray()) {
            sum += Character.isDigit(c) ? Character.getNumericValue(c) : 1;
        }
        return sum;
    }
    private static final Logger LOG = LoggerFactory.getLogger(FenParser.class);
    private static final String SIX_FIELD_MSG = "Fen string must have exactly 6 fields separated by spaces, but this one (%s) had %d";
    private static final String PLAYER_FIELD_MSG = "The 2nd field of FEN must be either letter w or b, but yours was: %s";
    private static final String CA_FIELD_MSG = "The 3rd field of FEN must consist of (some of the) letters KQkq in that order or be -, but yours was: %s";
    private static final String EP_FIELD_MSG = "The 4th fen field must be either \"-\" or valid square for en-passant capture - (on 3rd or 6th rank). But yours was: %s";
    private static final String COUNTERS_FILED_MSG = "The 5th and 6th fen fields must be valid decimal digits, but yours were: %d and %d";
    //
    private static final Pattern DIGIT_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern CASTLING_AVAILABILITY_PATTERN = Pattern.compile("^K?Q?k?q?$|^-$");
    private static final Pattern EN_PASSANT_PATTERN = Pattern.compile("^[abcdefgh][36]$|^-$");
}