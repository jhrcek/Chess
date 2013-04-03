package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.FEN.InvalidFenException;

/**
 *
 * @author jhrcek
 */
public class Main {

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    MainWindow window = new MainWindow();
                } catch (InvalidFenException ex) {
                    throw new AssertionError("The exception should never be propagated here!", ex);
                }
            }
        });
    }
}
