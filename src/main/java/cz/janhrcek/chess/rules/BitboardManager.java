package cz.janhrcek.chess.rules;

import cz.janhrcek.chess.model.api.Piece;
import cz.janhrcek.chess.model.api.Square;
import java.math.BigInteger;

/**
 * This class enables retrieval of various bitboards, represented as values of
 * primitive long. Bitboard is useful data structure used for representing
 * chessboard-related information. It is 64-bit long vector, enabling to store
 * yes/no information for each square on the chessboard.
 *
 * @author Jan Hrcek
 */
public final class BitboardManager {
    /**
     * Utility method for parsing string representation of bitboards into long.
     *
     * @param bitboardAsString 64-character string consisting of only 0s and 1s
     * @return long representation of the bitboard
     */
    public static long parseString(String bitboardAsString) {
        if (bitboardAsString == null) {
            throw new NullPointerException("bitboardAsString must not be null!");
        }
        if (bitboardAsString.length() != 64) {
            throw new IllegalArgumentException("The input must be 64 character"
                    + " long string consisting of only 1s and 0s, but was "
                    + bitboardAsString + "(length = " + bitboardAsString.length() + ")");
        }
        return new BigInteger(bitboardAsString, 2).longValue();
    }

    /**
     * This method tells whether given piece can go from given square to another
     * given square on empty board.
     *
     * @param piece piece whose movement we want to check
     * @param from square on which the piece starts
     * @param to square about which we want to know, whether given piece can
     * move to it from given "from" square
     * @return true if given piece can move from given square to given square
     * false otherwise
     */
    public static boolean canGo(final Piece piece,
            final Square from, final Square to) {
        if (((getReachableSquaresBB(piece, from)) & (getSquareBB(to))) != 0L) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns bitboard representing squares to which given piece can move from
     * the given square on the empty board.
     *
     * @param piece piece about which we want to know where can it move to on
     * the empty board in one move
     * @param from square that piece starts on
     * @return bitboard representing squares to which given piece can move from
     * given square
     */
    public static long getReachableSquaresBB(final Piece piece,
            final Square from) {
        if (piece == null) {
            throw new NullPointerException("piece can't be null!");
        }
        if (from == null) {
            throw new NullPointerException("from can't be null!");
        }
        long result = 0;

        switch (piece) {
            case WHITE_KING:
                result =
                        WHITE_KINGS_REACHABLE_SQUARES[from.getFile()][from.getRank()];
                break;
            case BLACK_KING:
                result =
                        BLACK_KINGS_REACHABLE_SQUARES[from.getFile()][from.getRank()];
                break;
            case WHITE_QUEEN:
            case BLACK_QUEEN:
                result =
                        QUEENS_REACHABLE_SQUARES[from.getFile()][from.getRank()];
                break;
            case WHITE_ROOK:
            case BLACK_ROOK:
                result =
                        ROOKS_REACHABLE_SQUARES[from.getFile()][from.getRank()];
                break;
            case WHITE_BISHOP:
            case BLACK_BISHOP:
                result =
                        BISHOPS_REACHABLE_SQUARES[from.getFile()][from.getRank()];
                break;
            case WHITE_KNIGHT:
            case BLACK_KNIGHT:
                result =
                        KNIGHTS_REACHABLE_SQUARES[from.getFile()][from.getRank()];
                break;
            case WHITE_PAWN:
                result =
                        W_PAWNS_REACHABLE_SQUARES[from.getFile()][from.getRank()];
                break;
            case BLACK_PAWN:
                result =
                        B_PAWNS_REACHABLE_SQUARES[from.getFile()][from.getRank()];
                break;
            default:
                break;
        }
        return result;
    }

////////////PRIVATE IMPLEMENTATION
    /**
     * This is utility class, and should not be instantiated.
     */
    private BitboardManager() {
    }

    /**
     * Returns bitboard representing given square.
     *
     * @param sq square which we want represented by the bitboard
     * @return bitboard representing given square
     */
    private static long getSquareBB(final Square sq) {
        if (sq == null) {
            throw new NullPointerException("sq can't be null!");
        }
        return SQUARES[sq.getFile()][sq.getRank()];
    }
    /**
     * 64 bitboards representing squares, to which KNIGHT can move from given
     * square on empty board. Bitboards are aranged into 2-dimensional array and
     * can be easily indexed by following way:
     * KNIGHTS_REACHABLE_SQUARES[from.getFile()][from.getRank()]
     */
    private static final long[][] KNIGHTS_REACHABLE_SQUARES = {
        {
            4202496L, //A1
            1075839008L, //A2
            275414786112L, //A3
            70506185244672L, //A4
            18049583422636032L, //A5
            4620693356194824192L, //A6
            2305878468463689728L, //A7
            9077567998918656L, //A8
        },
        {
            10489856L, //B1
            2685403152L, //B2
            687463207072L, //B3
            175990581010432L, //B4
            45053588738670592L, //B5
            -6913025356609880064L, //B6
            1152939783987658752L, //B7
            4679521487814656L, //B8
        },
        {
            5277696L, //C1
            1351090312L, //C2
            345879119952L, //C3
            88545054707712L, //C4
            22667534005174272L, //C5
            5802888705324613632L, //C6
            -8646761407372591104L, //C7
            38368557762871296L, //C8
        },
        {
            2638848L, //D1
            675545156L, //D2
            172939559976L, //D3
            44272527353856L, //D4
            11333767002587136L, //D5
            2901444352662306816L, //D6
            4899991333168480256L, //D7
            19184278881435648L, //D8
        },
        {
            1319424L, //E1
            337772578L, //E2
            86469779988L, //E3
            22136263676928L, //E4
            5666883501293568L, //E5
            1450722176331153408L, //E6
            2449995666584240128L, //E7
            9592139440717824L, //E8
        },
        {
            659712L, //F1
            168886289L, //F2
            43234889994L, //F3
            11068131838464L, //F4
            2833441750646784L, //F5
            725361088165576704L, //F6
            1224997833292120064L, //F7
            4796069720358912L, //F8
        },
        {
            329728L, //G1
            84410376L, //G2
            21609056261L, //G3
            5531918402816L, //G4
            1416171111120896L, //G5
            362539804446949376L, //G6
            576469569871282176L, //G7
            2257297371824128L, //G8
        },
        {
            132096L, //H1
            33816580L, //H2
            8657044482L, //H3
            2216203387392L, //H4
            567348067172352L, //H5
            145241105196122112L, //H6
            288234782788157440L, //H7
            1128098930098176L, //H8
        },};
    /**
     * 64 bitboards representing squares, to which Bishop can move from given
     * square on empty board. Bitboards are aranged into 2-dimensional array and
     * can be easily indexed by following way:
     * BISHOPS_REACHABLE_SQUARES[from.getFile()][from.getRank()]
     */
    private static final long[][] BISHOPS_REACHABLE_SQUARES = {
        {
            72624976668147712L, //A1
            145249953336262720L, //A2
            290499906664153120L, //A3
            580999811184992272L, //A4
            1161999073681608712L, //A5
            2323857683139004420L, //A6
            4611756524879479810L, //A7
            18049651735527937L, //A8
        },
        {
            283691315142656L, //B1
            72624976676520096L, //B2
            145249955479592976L, //B3
            290500455356698632L, //B4
            581140276476643332L, //B5
            1197958188344280066L, //B6
            -6917353036926680575L, //B7
            45053622886727936L, //B8
        },
        {
            1108177604608L, //C1
            283693466779728L, //C2
            72625527495610504L, //C3
            145390965166737412L, //C4
            326598935265674242L, //C5
            -8624392940535152127L, //C6
            5764696068147249408L, //C7
            22667548931719168L, //C8
        },
        {
            6480472064L, //D1
            1659000848424L, //D2
            424704217196612L, //D3
            108724279602332802L, //D4
            -9060072569221905919L, //D5
            4911175566595588352L, //D6
            2882348036221108224L, //D7
            11334324221640704L, //D8
        },
        {
            550848566272L, //E1
            141017232965652L, //E2
            36100411639206946L, //E3
            -9205038694072573375L, //E4
            4693335752243822976L, //E5
            2455587783297826816L, //E6
            1441174018118909952L, //E7
            5667164249915392L, //E8
        },
        {
            141012904249856L, //F1
            36099303487963146L, //F2
            -9205322380790986223L, //F3
            4620711952330133792L, //F4
            2310639079102947392L, //F5
            1227793891648880768L, //F6
            720587009051099136L, //F7
            2833579985862656L, //F8
        },
        {
            36099303471056128L, //G1
            -9205322385119182843L, //G2
            4620710844311799048L, //G3
            2310355426409252880L, //G4
            1155178802063085600L, //G5
            577868148797087808L, //G6
            360293502378066048L, //G7
            1416240237150208L, //G8
        },
        {
            -9205322385119247872L, //H1
            4620710844295151618L, //H2
            2310355422147510788L, //H3
            1155177711057110024L, //H4
            577588851267340304L, //H5
            288793334762704928L, //H6
            144117404414255168L, //H7
            567382630219904L, //H8
        },};
    /**
     * 64 bitboards representing squares, to which Rook can move from given
     * square on empty board. Bitboards are aranged into 2-dimensional array and
     * can be easily indexed by following way:
     * ROOKS_REACHABLE_SQUARES[from.getFile()][from.getRank()]
     */
    private static final long[][] ROOKS_REACHABLE_SQUARES = {
        {
            -9187201950435737473L, //A1
            -9187201950435737728L, //A2
            -9187201950435803008L, //A3
            -9187201950452514688L, //A4
            -9187201954730704768L, //A5
            -9187203049947365248L, //A6
            -9187483425412448128L, //A7
            9187484529235886208L, //A8
        },
        {
            4629771061636907199L, //B1
            4629771061636939584L, //B2
            4629771061645230144L, //B3
            4629771063767613504L, //B4
            4629771607097753664L, //B5
            4629910699613634624L, //B6
            4665518383679160384L, //B7
            -4665658569255796672L, //B8
        },
        {
            2314885530818453727L, //C1
            2314885530818502432L, //C2
            2314885530830970912L, //C3
            2314885534022901792L, //C4
            2314886351157207072L, //C5
            2315095537539358752L, //C6
            2368647251370188832L, //C7
            -2368858081646862304L, //C8
        },
        {
            1157442765409226991L, //D1
            1157442765409283856L, //D2
            1157442765423841296L, //D3
            1157442769150545936L, //D4
            1157443723186933776L, //D5
            1157687956502220816L, //D6
            1220211685215703056L, //D7
            -1220457837842395120L, //D8
        },
        {
            578721382704613623L, //E1
            578721382704674568L, //E2
            578721382720276488L, //E3
            578721386714368008L, //E4
            578722409201797128L, //E5
            578984165983651848L, //E6
            645993902138460168L, //E7
            -646257715940161528L, //E8
        },
        {
            289360691352306939L, //F1
            289360691352369924L, //F2
            289360691368494084L, //F3
            289360695496279044L, //F4
            289361752209228804L, //F5
            289632270724367364L, //F6
            358885010599838724L, //F7
            -359157654989044732L, //F8
        },
        {
            144680345676153597L, //G1
            144680345676217602L, //G2
            144680345692602882L, //G3
            144680349887234562L, //G4
            144681423712944642L, //G5
            144956323094725122L, //G6
            215330564830528002L, //G7
            -215607624513486334L, //G8
        },
        {
            72340172838076926L, //H1
            72340172838141441L, //H2
            72340172854657281L, //H3
            72340177082712321L, //H4
            72341259464802561L, //H5
            72618349279904001L, //H6
            143553341945872641L, //H7
            -143832609275707135L, //H8
        },};
    /**
     * 64 bitboards representing squares, to which Queen can move from given
     * square on empty board. Bitboards are aranged into 2-dimensional array and
     * can be easily indexed by following way:
     * QUEENS_REACHABLE_SQUARES[from.getFile()][from.getRank()]
     */
    private static final long[][] QUEENS_REACHABLE_SQUARES = {
        {
            -9114576973767589761L, //A1
            -9041951997099475008L, //A2
            -8896702043771649888L, //A3
            -8606202139267522416L, //A4
            -8025202881049096056L, //A5
            -6863345366808360828L, //A6
            -4575726900532968318L, //A7
            9205534180971414145L, //A8
        },
        {
            4630054752952049855L, //B1
            4702396038313459680L, //B2
            4775021017124823120L, //B3
            4920271519124312136L, //B4
            5210911883574396996L, //B5
            5827868887957914690L, //B6
            -2251834653247520191L, //B7
            -4620604946369068736L, //B8
        },
        {
            2314886638996058335L, //C1
            2315169224285282160L, //C2
            2387511058326581416L, //C3
            2460276499189639204L, //C4
            2641485286422881314L, //C5
            -6309297402995793375L, //C6
            8133343319517438240L, //C7
            -2346190532715143136L, //C8
        },
        {
            1157442771889699055L, //D1
            1157444424410132280L, //D2
            1157867469641037908L, //D3
            1266167048752878738L, //D4
            -7902628846034972143L, //D5
            6068863523097809168L, //D6
            4102559721436811280L, //D7
            -1209123513620754416L, //D8
        },
        {
            578721933553179895L, //E1
            578862399937640220L, //E2
            614821794359483434L, //E3
            -8626317307358205367L, //E4
            5272058161445620104L, //E5
            3034571949281478664L, //E6
            2087167920257370120L, //E7
            -640590551690246136L, //E8
        },
        {
            289501704256556795L, //F1
            325459994840333070L, //F2
            -8915961689422492139L, //F3
            4910072647826412836L, //F4
            2600000831312176196L, //F5
            1517426162373248132L, //F6
            1079472019650937860L, //F7
            -356324075003182076L, //F8
        },
        {
            180779649147209725L, //G1
            -9060642039442965241L, //G2
            4765391190004401930L, //G3
            2455035776296487442L, //G4
            1299860225776030242L, //G5
            722824471891812930L, //G6
            575624067208594050L, //G7
            -214191384276336126L, //G8
        },
        {
            -9132982212281170946L, //H1
            4693051017133293059L, //H2
            2382695595002168069L, //H3
            1227517888139822345L, //H4
            649930110732142865L, //H5
            361411684042608929L, //H6
            287670746360127809L, //H7
            -143265226645487231L, //H8
        },};
    /**
     * 64 bitboards representing squares, to which King can move from given
     * square on empty board. Bitboards are aranged into 2-dimensional array and
     * can be easily indexed in following way:
     * WHITE_KINGS_REACHABLE_SQUARES[from.getFile()][from.getRank()]
     */
    private static final long[][] WHITE_KINGS_REACHABLE_SQUARES = {
        {
            49216L, //A1
            12599488L, //A2
            3225468928L, //A3
            825720045568L, //A4
            211384331665408L, //A5
            54114388906344448L, //A6
            -4593460513685372928L, //A7
            4665729213955833856L, //A8
        },
        {
            57504L, //B1
            14721248L, //B2
            3768639488L, //B3
            964771708928L, //B4
            246981557485568L, //B5
            63227278716305408L, //B6
            -2260560722335367168L, //B7
            -6854478632857894912L, //B8
        },
        {
            28752L, //C1
            7360624L, //C2
            1884319744L, //C3
            482385854464L, //C4
            123490778742784L, //C5
            31613639358152704L, //C6
            8093091675687092224L, //C7
            5796132720425828352L, //C8
        },
        {
            14376L, //D1
            3680312L, //D2
            942159872L, //D3
            241192927232L, //D4
            61745389371392L, //D5
            15806819679076352L, //D6
            4046545837843546112L, //D7
            2898066360212914176L, //D8
        },
        {
            7222L, //E1
            1840156L, //E2
            471079936L, //E3
            120596463616L, //E4
            30872694685696L, //E5
            7903409839538176L, //E6
            2023272918921773056L, //E7
            1449033180106457088L, //E8
        },
        {
            3594L, //F1
            920078L, //F2
            235539968L, //F3
            60298231808L, //F4
            15436347342848L, //F5
            3951704919769088L, //F6
            1011636459460886528L, //F7
            724516590053228544L, //F8
        },
        {
            1797L, //G1
            460039L, //G2
            117769984L, //G3
            30149115904L, //G4
            7718173671424L, //G5
            1975852459884544L, //G6
            505818229730443264L, //G7
            362258295026614272L, //G8
        },
        {
            770L, //H1
            197123L, //H2
            50463488L, //H3
            12918652928L, //H4
            3307175149568L, //H5
            846636838289408L, //H6
            216739030602088448L, //H7
            144959613005987840L, //H8
        },};
    /**
     * 64 bitboards representing squares, to which King can move from given
     * square on empty board. Bitboards are aranged into 2-dimensional array and
     * can be easily indexed in following way:
     * BLACK_KINGS_REACHABLE_SQUARES[from.getFile()][from.getRank()]
     */
    private static final long[][] BLACK_KINGS_REACHABLE_SQUARES = {
        {
            49216L, //A1
            12599488L, //A2
            3225468928L, //A3
            825720045568L, //A4
            211384331665408L, //A5
            54114388906344448L, //A6
            -4593460513685372928L, //A7
            4665729213955833856L, //A8
        },
        {
            57504L, //B1
            14721248L, //B2
            3768639488L, //B3
            964771708928L, //B4
            246981557485568L, //B5
            63227278716305408L, //B6
            -2260560722335367168L, //B7
            -6854478632857894912L, //B8
        },
        {
            28752L, //C1
            7360624L, //C2
            1884319744L, //C3
            482385854464L, //C4
            123490778742784L, //C5
            31613639358152704L, //C6
            8093091675687092224L, //C7
            5796132720425828352L, //C8
        },
        {
            14376L, //D1
            3680312L, //D2
            942159872L, //D3
            241192927232L, //D4
            61745389371392L, //D5
            15806819679076352L, //D6
            4046545837843546112L, //D7
            2898066360212914176L, //D8
        },
        {
            7188L, //E1
            1840156L, //E2
            471079936L, //E3
            120596463616L, //E4
            30872694685696L, //E5
            7903409839538176L, //E6
            2023272918921773056L, //E7
            3898991377396006912L, //E8
        },
        {
            3594L, //F1
            920078L, //F2
            235539968L, //F3
            60298231808L, //F4
            15436347342848L, //F5
            3951704919769088L, //F6
            1011636459460886528L, //F7
            724516590053228544L, //F8
        },
        {
            1797L, //G1
            460039L, //G2
            117769984L, //G3
            30149115904L, //G4
            7718173671424L, //G5
            1975852459884544L, //G6
            505818229730443264L, //G7
            362258295026614272L, //G8
        },
        {
            770L, //H1
            197123L, //H2
            50463488L, //H3
            12918652928L, //H4
            3307175149568L, //H5
            846636838289408L, //H6
            216739030602088448L, //H7
            144959613005987840L, //H8
        },};
    /**
     * 64 bitboards representing squares, to which WhitePawn can move from given
     * square on empty board (eventual captures, which are not posible on EMPTY
     * board, are included). Bitboards are aranged into 2-dimensional array and
     * can be easily indexed in following way:
     * WHITE_PAWNS_REACHABLE_SQUARES[from.getFile()][from.getRank()]
     */
    private static final long[][] W_PAWNS_REACHABLE_SQUARES = {
        {
            0L, //A1 -pawn may never stand on 1st rank
            2160066560L, //A2
            3221225472L, //A3
            824633720832L, //A4
            211106232532992L, //A5
            54043195528445952L, //A6
            -4611686018427387904L, //A7
            0L, //A8 -pawn must have been promoted on 8th
        },
        {
            0L, //B1
            1088421888L, //B2
            3758096384L, //B3
            962072674304L, //B4
            246290604621824L, //B5
            63050394783186944L, //B6
            -2305843009213693952L, //B7
            0L, //B8
        },
        {
            0L, //C1
            544210944L, //C2
            1879048192L, //C3
            481036337152L, //C4
            123145302310912L, //C5
            31525197391593472L, //C6
            8070450532247928832L, //C7
            0L, //C8
        },
        {
            0L, //D1
            272105472L, //D2
            939524096L, //D3
            240518168576L, //D4
            61572651155456L, //D5
            15762598695796736L, //D6
            4035225266123964416L, //D7
            0L, //D8
        },
        {
            0L, //E1
            136052736L, //E2
            469762048L, //E3
            120259084288L, //E4
            30786325577728L, //E5
            7881299347898368L, //E6
            2017612633061982208L, //E7
            0L, //E8
        },
        {
            0L, //F1
            68026368L, //F2
            234881024L, //F3
            60129542144L, //F4
            15393162788864L, //F5
            3940649673949184L, //F6
            1008806316530991104L, //F7
            0L, //F8
        },
        {
            0L, //G1
            34013184L, //G2
            117440512L, //G3
            30064771072L, //G4
            7696581394432L, //G5
            1970324836974592L, //G6
            504403158265495552L, //G7
            0L, //G8
        },
        {
            0L, //H1
            16973824L, //H2
            50331648L, //H3
            12884901888L, //H4
            3298534883328L, //H5
            844424930131968L, //H6
            216172782113783808L, //H7
            0L, //H8
        },};
    /**
     * 64 bitboards representing squares, to which Black Pawn can move from
     * given square on empty board (eventual captures, which are not posible on
     * EMPTY board, are included). Bitboards are aranged into 2-dimensional
     * array and can be easily indexed in following way:
     * WHITE_PAWNS_REACHABLE_SQUARES[from.getFile()][from.getRank()]
     */
    private static final long[][] B_PAWNS_REACHABLE_SQUARES = {
        {
            0L, //A1 -pawn must have been promoted on 1st
            192L, //A2
            49152L, //A3
            12582912L, //A4
            3221225472L, //A5
            824633720832L, //A6
            211655988346880L, //A7
            0L, //A8 -pawn may never stand on 1st rank
        },
        {
            0L, //B1
            224L, //B2
            57344L, //B3
            14680064L, //B4
            3758096384L, //B5
            962072674304L, //B6
            246565482528768L, //B7
            0L, //B8
        },
        {
            0L, //C1
            112L, //C2
            28672L, //C3
            7340032L, //C4
            1879048192L, //C5
            481036337152L, //C6
            123282741264384L, //C7
            0L, //C8
        },
        {
            0L, //D1
            56L, //D2
            14336L, //D3
            3670016L, //D4
            939524096L, //D5
            240518168576L, //D6
            61641370632192L, //D7
            0L, //D8
        },
        {
            0L, //E1
            28L, //E2
            7168L, //E3
            1835008L, //E4
            469762048L, //E5
            120259084288L, //E6
            30820685316096L, //E7
            0L, //E8
        },
        {
            0L, //F1
            14L, //F2
            3584L, //F3
            917504L, //F4
            234881024L, //F5
            60129542144L, //F6
            15410342658048L, //F7
            0L, //F8
        },
        {
            0L, //G1
            7L, //G2
            1792L, //G3
            458752L, //G4
            117440512L, //G5
            30064771072L, //G6
            7705171329024L, //G7
            0L, //G8
        },
        {
            0L, //H1
            3L, //H2
            768L, //H3
            196608L, //H4
            50331648L, //H5
            12884901888L, //H6
            3302829850624L, //H7
            0L, //H8
        },};
    /**
     * 64 bitboards representing squares.
     */
    private static final long[][] SQUARES = {
        {
            128L, //A1
            32768L, //A2
            8388608L, //A3
            2147483648L, //A4
            549755813888L, //A5
            140737488355328L, //A6
            36028797018963968L, //A7
            -9223372036854775808L, //A8
        },
        {
            64L, //B1
            16384L, //B2
            4194304L, //B3
            1073741824L, //B4
            274877906944L, //B5
            70368744177664L, //B6
            18014398509481984L, //B7
            4611686018427387904L, //B8
        },
        {
            32L, //C1
            8192L, //C2
            2097152L, //C3
            536870912L, //C4
            137438953472L, //C5
            35184372088832L, //C6
            9007199254740992L, //C7
            2305843009213693952L, //C8
        },
        {
            16L, //D1
            4096L, //D2
            1048576L, //D3
            268435456L, //D4
            68719476736L, //D5
            17592186044416L, //D6
            4503599627370496L, //D7
            1152921504606846976L, //D8
        },
        {
            8L, //E1
            2048L, //E2
            524288L, //E3
            134217728L, //E4
            34359738368L, //E5
            8796093022208L, //E6
            2251799813685248L, //E7
            576460752303423488L, //E8
        },
        {
            4L, //F1
            1024L, //F2
            262144L, //F3
            67108864L, //F4
            17179869184L, //F5
            4398046511104L, //F6
            1125899906842624L, //F7
            288230376151711744L, //F8
        },
        {
            2L, //G1
            512L, //G2
            131072L, //G3
            33554432L, //G4
            8589934592L, //G5
            2199023255552L, //G6
            562949953421312L, //G7
            144115188075855872L, //G8
        },
        {
            1L, //H1
            256L, //H2
            65536L, //H3
            16777216L, //H4
            4294967296L, //H5
            1099511627776L, //H6
            281474976710656L, //H7
            72057594037927936L, //H8
        },};
}
