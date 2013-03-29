package cz.janhrcek.chess.FEN;

import cz.janhrcek.chess.model.api.Position;
import cz.janhrcek.chess.model.api.enums.Castling;
import cz.janhrcek.chess.model.api.enums.Piece;
import cz.janhrcek.chess.model.api.enums.Square;
import cz.janhrcek.chess.model.api.Chessboard;
import cz.janhrcek.chess.model.impl.PositionImpl;
import cz.janhrcek.chess.model.impl.ChessboardImpl;
import static java.lang.String.format;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author jhrcek
 */
public class Fen {

    public Fen(String fenString) throws InvalidFenException {
        log.info("Parsing: \"{}\"", fenString);
        String[] fields = splitIntoFields(requireNonNull(fenString, "Fen string must not be null!"));

        position = parsePiecePlacement(fields[0]);
        log.debug("    1. Position \n{}", position);

        whiteToMove = parsePlayerToMove(fields[1]);
        log.debug("    2. It's {} to move", whiteToMove ? "WHITE" : "BLACK");

        //3. check castling availability string
        castlings = parseCastlings(fields[2]);
        log.debug("    3. Castling availabilities: {}", castlings);

        //4. Check en-passant target square
        enPassantTargetSquare = parseEnPassant(fields[3]);
        log.debug("    4. En-passant target square: {}", this.enPassantTargetSquare);


        //5. & 6.check halfmove clock & fullmove number
        if (!DIGIT_PATTERN.matcher(fields[4]).matches()
                || !DIGIT_PATTERN.matcher(fields[5]).matches()) {
            throw new InvalidFenException(format(COUNTERS_FILED_MSG, fields[4], fields[5]));
        } else {
            this.halfmoveClock = Integer.valueOf(fields[4]);
            log.debug("    5. Half-move clock: {}", this.halfmoveClock);
            this.fullmoveNumber = Integer.valueOf(fields[5]);
            log.debug("    6. Full-move number: {}", this.fullmoveNumber);
        }
    }

    /**
     * Converts given Position to equivalent FEN string.
     *
     * @param position the position to convert
     * @return the FEN string representation of position
     */
    public static String positionToFen(Position position) {
        log.info("Converting position to FEN");
        StringBuilder sb = new StringBuilder(50);
        Square ep = position.getEnPassantTarget();
        sb.append(piecePlacementToFen(position.getChessboard()))
                .append(position.isWhiteToMove() ? " w " : " b ")
                .append(Castling.toFenCastlingSubstring(position.getCastlings()))
                .append(" ")
                .append(ep == null ? "-" : ep.toString().toLowerCase())
                .append(" ")
                .append(position.getHalfmoveClock())
                .append(" ")
                .append(position.getFullmoveNumber());
        return sb.toString();
    }

    /**
     *
     * @param board the position to convert to FEN piece placement substring
     * @return the string representing piece placement (1st component of FEN
     * record)
     */
    public static String piecePlacementToFen(Chessboard board) {
        StringBuilder sb = new StringBuilder(64);
        int counter = 0;
        for (Square sq : Square.values()) {
            Piece p = board.getPiece(sq);
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
                if (counter != 0) { //empty the counter before each rank
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
     * Parses given string, populating Position object. If the parameter is
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
    public Position toPosition() {
        return new PositionImpl(position, whiteToMove, castlings, enPassantTargetSquare, halfmoveClock, fullmoveNumber);
    }

    public Chessboard toChessboard() {
        return position;
    }

    /**
     *
     * @param piecePlacementSubstring The first field (piece placement) of Fen
     * string
     * @return MutablePosition with the same piece placement as the one
     * described in the input fen piece placement field
     * @throws InvalidFenException
     */
    public static Chessboard parsePiecePlacement(String piecePlacementSubstring) throws InvalidFenException {
        String[] ranks = piecePlacementSubstring.split("/");
        //Check there are exactly 8 ranks, separated by "/"
        if (ranks.length != 8) {
            throw new InvalidFenException("piecePlacement field of FEN must have"
                    + " exactly 8 ranks, each of them separated by \"/\". But "
                    + "this one (" + piecePlacementSubstring + ") had "
                    + ranks.length + "ranks");
        }

        //Check, that there are only correct characters (piece FEN names + digits 0-8 and slashes "/"
        if (!piecePlacementSubstring.matches("^[pnbrqkPNBRQK1-8/]+$")) {
            throw new InvalidFenException("piecePlacement must only contain the"
                    + " following charasters: pnbrqkPNBRQK12345678 - but yours"
                    + " contained something else: " + piecePlacementSubstring);
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

        //Everything seems OK, initialize the position using the info from piece-placement substring
        Map<Square, Piece> piecePlacement = new HashMap<>();
        for (int rankIdx = 7; rankIdx >= 0; rankIdx--) {
            int colIdx = 0;
            for (char c : ranks[7 - rankIdx].toCharArray()) {
                if (Character.isLetter(c)) { //it is letter -> put corresponding piece on
                    //LOG.debug("Putting {} on {}", Piece.getPiece(c), Square.getSquare(colIdx, rankIdx));
                    piecePlacement.put(Square.getSquare(colIdx, rankIdx), Piece.getPiece(c));
                    colIdx++;
                } else { //it is number --> move 'c' columns to the right
                    colIdx += Character.getNumericValue(c);
                }
            }
        }
        Chessboard newPos = new ChessboardImpl(piecePlacement);
        return newPos;
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
    //fields for storing initialization values of currently parsed fen string
    private final Chessboard position;
    private final boolean whiteToMove;
    private final EnumSet<Castling> castlings;
    private final Square enPassantTargetSquare;
    private final int halfmoveClock;
    private final int fullmoveNumber;
    //loggind and exception messages
    private static final Logger log = LoggerFactory.getLogger(Fen.class);
    private static final String SIX_FIELD_MSG = "Fen string must have exactly 6 fields separated by spaces, but this one (%s) had %d";
    private static final String PLAYER_FIELD_MSG = "The 2nd field of FEN must be either letter w or b, but yours was: %s";
    private static final String CA_FIELD_MSG = "The 3rd field of FEN must consist of (some of the) letters KQkq in that order or be -, but yours was: %s";
    private static final String EP_FIELD_MSG = "The 4th fen field must be either \"-\" or valid square for en-passant capture - (on 3rd or 6th rank). But yours was: %s";
    private static final String COUNTERS_FILED_MSG = "The 5th and 6th fen fields must be valid decimal digits, but yours were: %d and %d";
    //Patterns for checking valid values for FEN string fields
    private static final Pattern DIGIT_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern CASTLING_AVAILABILITY_PATTERN = Pattern.compile("^KQ?k?q?$|^K?Qk?q?$|^K?Q?kq?$|^K?Q?k?q$|^-$"); //Means either one or more of the mentioned, or "-", NOT the empty string
    private static final Pattern EN_PASSANT_PATTERN = Pattern.compile("^[abcdefgh][36]$|^-$");
    //
    public static final String INITIAL_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private boolean parsePlayerToMove(String playerFenField) throws InvalidFenException {
        if (!"w".equals(playerFenField) && !"b".equals(playerFenField)) {
            throw new InvalidFenException(format(PLAYER_FIELD_MSG, playerFenField));
        }
        return "w".equals(playerFenField);
    }

    private EnumSet<Castling> parseCastlings(String castlingFenField) throws InvalidFenException {
        if (!CASTLING_AVAILABILITY_PATTERN.matcher(castlingFenField).matches()) {
            throw new InvalidFenException(format(CA_FIELD_MSG, castlingFenField));
        }
        return Castling.parseFenCastlingSubstring(castlingFenField);
    }

    private String[] splitIntoFields(String fenString) throws InvalidFenException {
        String[] fields = fenString.split(" ");
        if (fields.length != 6) {
            throw new InvalidFenException(format(SIX_FIELD_MSG, fenString, fields.length));
        }
        return fields;
    }

    private Square parseEnPassant(String enPassantFenFiled) throws InvalidFenException {
        if (!EN_PASSANT_PATTERN.matcher(enPassantFenFiled).matches()) {
            throw new InvalidFenException(format(EP_FIELD_MSG, enPassantFenFiled));
        }
        return "-".equals(enPassantFenFiled) ? null : Square.valueOf(enPassantFenFiled.toUpperCase());
    }
}