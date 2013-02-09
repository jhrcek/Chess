package cz.janhrcek.chess.gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * This is implemented FileFilter used by file chooser, which enables to filter
 * files without PGN extension (not case sensitive).
 *
 * @author xhrcek
 */
public class PGNFileFilter extends FileFilter {

    /**
     * Creates a new instance of PGNFileFilter
     */
    public PGNFileFilter() {
    }

    /**
     * Returns true if this filter accepts the file, false otherwise.
     *
     * @return true if the file is directory or has extension .PGN (NOT case
     * sensitive), false otherwise.
     * @param f The file about which we want to know if it should be accepted.
     */
    public boolean accept(File f) {
        String fileName = f.getName();
        if (f.isDirectory() || fileName.toLowerCase().endsWith(".pgn")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * The description of this filter.
     *
     * @return the string "PGN files"
     */
    public String getDescription() {
        return "PGN files";
    }
}
