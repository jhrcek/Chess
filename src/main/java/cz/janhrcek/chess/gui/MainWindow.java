package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.FEN.FenParser;
import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.model.api.GameBrowser;
import cz.janhrcek.chess.model.impl.FIDERuleChecker;
import cz.janhrcek.chess.model.impl.GameBrowserImpl;
import cz.janhrcek.chess.model.impl.GameStateFactoryImpl;
import cz.janhrcek.chess.model.impl.GameTree;
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
        //create representation of gamestate
        try {
            GameTree gameTree = new GameTree(new GameStateFactoryImpl(new FIDERuleChecker()), FenParser.INITIAL_STATE_FEN);
            gameBrowser = new GameBrowserImpl(gameTree);
        } catch (InvalidFenException ex) {
            throw new IllegalStateException("Unexpected exception thrown", ex);
        }

        chessboardComponent = new ChessboardComponent(gameBrowser);
        chessboardComponent.addMoveSelectedListener((MoveListener) gameBrowser);

        AdHocMoveDisplayArea moveDisplay = new AdHocMoveDisplayArea(gameBrowser);
        JScrollPane scrollableMoveDisplay = new JScrollPane(moveDisplay);

        treeDisplayer = new GameTreeDisplayer(gameBrowser);
        JScrollPane scrollableGameTreeDisplay = new JScrollPane(treeDisplayer);

        stateDisplayer = new GameStateDisplayer();

        gameBrowser.addGameListener(stateDisplayer);
        gameBrowser.addGameListener(treeDisplayer);

        JPanel gameBrowseControls = new JPanel();
        gameBrowseControls.setLayout(new FlowLayout());// GridLayout(3, 3));
        gameBrowseControls.add(firstButton);
        gameBrowseControls.add(previousButton);
        gameBrowseControls.add(nextButton);
        gameBrowseControls.add(lastButton);
        previousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("User clicks \"{}\" button", previousButton.getText());
                gameBrowser.focusPreviousState();
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("User clicks \"{}\" button", nextButton.getText());
                gameBrowser.focusNextState();
            }
        });

        firstButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("User clicks \"{}\" button", firstButton.getText());
                gameBrowser.focusInitialState();
            }
        });

        lastButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.info("User clicks \"{}\" button", lastButton.getText());
                gameBrowser.focusLastState();
            }
        });
        JSplitPane controlsPlusStateDisplayer = new JSplitPane(JSplitPane.VERTICAL_SPLIT, gameBrowseControls, stateDisplayer);
        controlsPlusStateDisplayer.setDividerLocation(30);
        controlsPlusStateDisplayer.setDividerSize(0);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 2, 10, 10));
        mainPanel.add(chessboardComponent);
        mainPanel.add(scrollableMoveDisplay);
        mainPanel.add(controlsPlusStateDisplayer);
        mainPanel.add(scrollableGameTreeDisplay);
        //mainPanel.add(stateDisplayer);

        frame.add(mainPanel);
        frame.setSize(650, 650);
        frame.setVisible(true);
    }
    //controls
    private final JButton firstButton = new JButton("First");
    private final JButton previousButton = new JButton("Previous");
    private final JButton nextButton = new JButton("Next");
    private final JButton lastButton = new JButton("Last");
    //model
    private GameBrowser gameBrowser;
    //gui providing view into model
    private ChessboardComponent chessboardComponent;
    private GameTreeDisplayer treeDisplayer;
    private GameStateDisplayer stateDisplayer;
    private static final Logger log = LoggerFactory.getLogger(MainWindow.class);
}
