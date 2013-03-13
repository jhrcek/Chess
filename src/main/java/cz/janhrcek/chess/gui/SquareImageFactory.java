package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.model.api.enums.Piece;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages creation of appropriately sized images of squares (either empty
 * squares or with an image of piece on them) to be displayed on the chessboard.
 *
 * @author jhrcek
 */
public class SquareImageFactory {

    private static Logger LOG = LoggerFactory.getLogger(SquareImageFactory.class);
    /**
     * Map for storing images (icons) of pieces on squares, which are used to
     * create image of the whole board.
     */
    private Map<String, ImageIcon> images =
            new HashMap<>(26); //6 piece kinds * 2 piece colors * 2 background colors + 2 empty 

    public SquareImageFactory(int initialSizeOfImages) {
        generateNewIcons(initialSizeOfImages);
    }

    public ImageIcon getSquareImage(Piece piece, boolean isBackgroundLight) {
        return images.get(piece + (isBackgroundLight ? "_L" : "_D"));
    }

    public void setImageSize(int sizeInPixels) {
        generateNewIcons(sizeInPixels);
    }

    /**
     * This method converts images of all pieces stored in svg files to images
     * in of given width in png format.
     *
     * @param sizeInPixels width (in pixels) of png icons to be generated from
     * svg input
     */
    private void generateNewIcons(int sizeInPixels) {
        if (sizeInPixels < 1 || sizeInPixels > 1000) {
            throw new IllegalArgumentException("width of output images must"
                    + " be between 1 and 1000 (in pixels)");
        }

        //vytvorime transcoder, ktery prevede svg soubory na pgn
        PNGTranscoder transcoder = new PNGTranscoder();
        // ... a nastavime pozadovany rozmer vystupnich pgnek
        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH,
                Float.valueOf((float) sizeInPixels));

        //vsechny svgcka postupne prevedem na png
        for (String suffix : new String[]{"_L.svg", "_D.svg"}) {
            for (Piece piece : Piece.values()) {
                String inputImageName = piece + suffix; //piece-on-square icons on the classpath
                loadIcon(inputImageName, transcoder);
            }
            String inputImageName = "null" + suffix;
            loadIcon(inputImageName, transcoder);
        }
    }

    /**
     * This method loads .svg file with given name, transforms it to PGN and
     * stores it to this class' "images" map, which holds the images of pieces
     * on squares.
     *
     * @param inputSVGName name of the input svg file
     * @param t the transcoder with which the svg file will be transcoded into
     * bitmap
     */
    private void loadIcon(String inputSVGName, PNGTranscoder t) {
        try {
            // create the transcoder input
            String svgURI = SquareImageFactory.class.getResource(inputSVGName).toString();
            TranscoderInput input = new TranscoderInput(svgURI);
            try (ByteArrayOutputStream ostream = new ByteArrayOutputStream()) {
                TranscoderOutput output = new TranscoderOutput(ostream);

                //save the image to byte array as png
                t.transcode(input, output);
                ImageIcon icon = new ImageIcon(ostream.toByteArray());
                LOG.info("loading piece image {} ", inputSVGName.substring(0, inputSVGName.length() - 4));
                images.put(inputSVGName.substring(0, inputSVGName.length() - 4), icon);
                ostream.flush();
            }
        } catch (MalformedURLException mue) {
            LOG.error("There was something wrong with the url of some svg file", mue);
        } catch (FileNotFoundException fnfe) {
            LOG.error("Exception when loading piece icons", fnfe);
        } catch (TranscoderException te) {
            LOG.error("There was an exception during transcoding of piece icon file", te);
        } catch (IOException ioe) {
            LOG.error("Exception when loading icon", ioe);
        }
    }
}
