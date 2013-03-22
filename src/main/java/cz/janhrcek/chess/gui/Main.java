package cz.janhrcek.chess.gui;

/**
 *
 * @author jhrcek
 */
public class Main {

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainWindow window = new MainWindow();
            }
        });
    }
}
