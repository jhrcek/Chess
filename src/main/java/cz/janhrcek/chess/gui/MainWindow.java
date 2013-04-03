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
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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

    public MainWindow() throws HeadlessException, InvalidFenException {
        frame = new JFrame("Chess");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createAndShowGui();
    }

    private void createAndShowGui() throws InvalidFenException {
        //Model
        currentGame = createNewGameModel();

        gameBrowser = currentGame.getBrowser();
//TODO - untangle the way model objects and components are intertwined - to enable set new game, when new instance of game (browser) will be injected into the components
        //GUI components
        chessboardComponent = new ChessboardComponent(gameBrowser);
        gameTreeDisplayer = new GameTreeDisplayer(gameBrowser);
        positionInfoDisplayer = new PositionInfoDisplayer(gameBrowser);

        JScrollPane scrollableGameTreeDisplay = new JScrollPane(gameTreeDisplayer);

        JPanel gameBrowseControls = createPanelWithButtons();
        JSplitPane controlsPlusPositionInfoDisplayer = new JSplitPane(JSplitPane.VERTICAL_SPLIT, gameBrowseControls, positionInfoDisplayer);
        controlsPlusPositionInfoDisplayer.setDividerLocation(30);
        controlsPlusPositionInfoDisplayer.setDividerSize(0);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 2, 5, 5));
        mainPanel.add(chessboardComponent);
        mainPanel.add(scrollableGameTreeDisplay);
        mainPanel.add(controlsPlusPositionInfoDisplayer);

        JMenuBar menuBar = createMenu();

        frame.setJMenuBar(menuBar);
        frame.add(mainPanel);
        frame.setSize(700, 700);
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

    private JMenuBar createMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGameItem = new JMenuItem(new NewGameAction("New Game"));
        JMenuItem newGameItem2 = new JMenuItem(new NewGameWithCustomStart("New Game (Custom Initial Position)"));
        gameMenu.add(newGameItem);
        gameMenu.add(newGameItem2);
        menuBar.add(gameMenu);
        return menuBar;
    }
    //Main window
    private JFrame frame;
    //Model (Browser providing view on chess game)
    private Game currentGame;
    private GameBrowser gameBrowser;
    //Gui providing view into model
    private ChessboardComponent chessboardComponent;
    private GameTreeDisplayer gameTreeDisplayer;
    private PositionInfoDisplayer positionInfoDisplayer;
    //
    private static final Logger log = LoggerFactory.getLogger(MainWindow.class);

    private Game createNewGameModel() throws InvalidFenException {
        return createNewGameModel(Fen.INITIAL_POSITION);
    }

    private Game createNewGameModel(String fen) throws InvalidFenException {
        return new GameImpl(fen);
    }

    private void injectNewGameIntoGuiComponents(Game game) {
        chessboardComponent.removeMoveSelectedEventListener((MoveListener) gameBrowser); //first remove the association ChessboardComponent->gameBrowser
        //get new GB and inject it into components
        gameBrowser = game.getBrowser();
        chessboardComponent.setGameBrowser(gameBrowser);
        gameTreeDisplayer.setGameBrowser(gameBrowser);
        positionInfoDisplayer.setGameBrowser(gameBrowser);
    }

    //TODO - move to outer level
    public class NewGameAction extends AbstractAction {

        public NewGameAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                currentGame = createNewGameModel();
            } catch (InvalidFenException ex) {
                throw new RuntimeException(ex);
            }

            injectNewGameIntoGuiComponents(currentGame);
        }
    }

    public class NewGameWithCustomStart extends AbstractAction {

        public NewGameWithCustomStart(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String fenString = null;
            boolean fenInvalid = true;
            while (fenInvalid) {
                try {
                    fenString = JOptionPane.showInputDialog(frame, "Please enter starting position in FEN", "Choose initial position", JOptionPane.PLAIN_MESSAGE);
                    currentGame = createNewGameModel(fenString);
                    injectNewGameIntoGuiComponents(currentGame);
                    fenInvalid = false;
                } catch (InvalidFenException ex) {
                    JOptionPane.showMessageDialog(frame, "The string you entered is not valid FEN string:\n" + fenString + "\n" + ex.getMessage(), "Invalid FEN String", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
