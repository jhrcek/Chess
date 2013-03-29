package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.FEN.Fen;
import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.model.api.Game;
import cz.janhrcek.chess.model.api.GameBrowser;
import cz.janhrcek.chess.model.impl.GameImpl;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jhrcek
 */
public class MainWindow {

    private JFrame frame;

    public MainWindow() throws HeadlessException {
        frame = new JFrame("Chess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createAndShowGui();
    }

    private void createAndShowGui() {


        //Model
        try {
            currentGame = new GameImpl(Fen.INITIAL_POSITION);
        } catch (InvalidFenException ife) {
            throw new AssertionError("Initial position fen not parsed correctly");
        }
        gameBrowser = currentGame.getBrowser();
        //GUI components
        chessboardComponent = new ChessboardComponent(gameBrowser);

        adHocGameTreeDisplayer = new GameTreeDisplayer(gameBrowser);
        adHocPositionInfoDisplayer = new PositionInfoDisplayer(gameBrowser);

        JScrollPane scrollableGameTreeDisplay = new JScrollPane(adHocGameTreeDisplayer);

        gameBrowser.addGameListener(adHocPositionInfoDisplayer);
        gameBrowser.addGameListener(adHocGameTreeDisplayer);

        JPanel gameBrowseControls = createPanelWithButtons();
        JSplitPane controlsPlusPositionInfoDisplayer = new JSplitPane(JSplitPane.VERTICAL_SPLIT, gameBrowseControls, adHocPositionInfoDisplayer);
        controlsPlusPositionInfoDisplayer.setDividerLocation(30);
        controlsPlusPositionInfoDisplayer.setDividerSize(0);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 2, 5, 5));
        mainPanel.add(chessboardComponent);
        mainPanel.add(scrollableGameTreeDisplay);
        mainPanel.add(controlsPlusPositionInfoDisplayer);

        frame.add(mainPanel);
        frame.setSize(650, 650);
        frame.setVisible(true);
    }

    /**
     * @return panel with 4 buttons for browsing game via GUI: First, Previous,
     * Next and Last
     */
    private JPanel createPanelWithButtons() {
        JPanel panelWithButtons = new JPanel();
        final JButton firstButton = new JButton("First");
        final JButton previousButton = new JButton("Previous");
        final JButton nextButton = new JButton("Next");
        final JButton lastButton = new JButton("Last");

        previousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("User clicks \"{}\" button", previousButton.getText());
                gameBrowser.focusPreviousPosition();
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("User clicks \"{}\" button", nextButton.getText());
                gameBrowser.focusNextPosition();
            }
        });

        firstButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("User clicks \"{}\" button", firstButton.getText());
                gameBrowser.focusInitialPosition();
            }
        });

        lastButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("User clicks \"{}\" button", lastButton.getText());
                gameBrowser.focusLastPosition();
            }
        });
        panelWithButtons.setLayout(new FlowLayout());
        panelWithButtons.add(firstButton);
        panelWithButtons.add(previousButton);
        panelWithButtons.add(nextButton);
        panelWithButtons.add(lastButton);
        return panelWithButtons;
    }
    //Model (Browser providing view on chess game)
    private Game currentGame;
    private GameBrowser gameBrowser;
    //Gui providing view into model
    private ChessboardComponent chessboardComponent;
    private GameTreeDisplayer adHocGameTreeDisplayer;
    private PositionInfoDisplayer adHocPositionInfoDisplayer;
    //
    private static final Logger log = LoggerFactory.getLogger(MainWindow.class);
}
