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

/**
 *
 * @author jhrcek
 */
public class MainWindow extends JFrame {

    public MainWindow() throws HeadlessException {
        super("Chess");
        createAndShowGui();
    }

    private void createAndShowGui() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //create representation of gamestate
        
        try {
            GameTree gameTree = new GameTree(new GameStateFactoryImpl(new FIDERuleChecker()), FenParser.INITIAL_STATE_FEN);
            gameBrowser = new GameBrowserImpl(gameTree);
        } catch (InvalidFenException ex) {
            throw new IllegalStateException("Unexpected exception thrown", ex);
        }

        chessboardComponent = new ChessboardComponent(gameBrowser);
        chessboardComponent.addMoveSelectedListener((MoveSelectedListener) gameBrowser);

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
                gameBrowser.focusPreviousState();
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameBrowser.focusNextState();
            }
        });

        firstButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameBrowser.focusInitialState();
            }
        });

        lastButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameBrowser.focusLastState();
            }
        });
        JSplitPane controlsPlusStateDisplayer = new JSplitPane(JSplitPane.VERTICAL_SPLIT, gameBrowseControls, stateDisplayer);
        controlsPlusStateDisplayer.setDividerLocation(20);
        controlsPlusStateDisplayer.setDividerSize(0);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 2, 10, 10));
        mainPanel.add(chessboardComponent);
        mainPanel.add(scrollableMoveDisplay);
        mainPanel.add(controlsPlusStateDisplayer);
        mainPanel.add(scrollableGameTreeDisplay);
        //mainPanel.add(stateDisplayer);

        this.add(mainPanel);
        setSize(650, 650);
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
}
