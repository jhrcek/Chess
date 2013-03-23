package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.guice.MyModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import cz.janhrcek.chess.model.api.GameBrowser;
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
        Injector injector = Guice.createInjector(new MyModule());

        //Model
        gameBrowser = injector.getInstance(GameBrowser.class);
        //GUI components
        chessboardComponent = injector.getInstance(ChessboardComponent.class);
        adHocMoveDisplayer = injector.getInstance(AdHocMoveDisplayArea.class);
        adHocGameTreeDisplayer = injector.getInstance(GameTreeDisplayer.class);
        adHocGameStateDisplayer = new GameStateDisplayer();

        JScrollPane scrollableMoveDisplay = new JScrollPane(adHocMoveDisplayer);
        JScrollPane scrollableGameTreeDisplay = new JScrollPane(adHocGameTreeDisplayer);

        gameBrowser.addGameListener(adHocGameStateDisplayer);
        gameBrowser.addGameListener(adHocGameTreeDisplayer);

        JPanel gameBrowseControls = createPanelWithButtons();
        JSplitPane controlsPlusStateDisplayer = new JSplitPane(JSplitPane.VERTICAL_SPLIT, gameBrowseControls, adHocGameStateDisplayer);
        controlsPlusStateDisplayer.setDividerLocation(30);
        controlsPlusStateDisplayer.setDividerSize(0);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 2, 10, 10));
        mainPanel.add(chessboardComponent);
        mainPanel.add(scrollableMoveDisplay);
        mainPanel.add(controlsPlusStateDisplayer);
        mainPanel.add(scrollableGameTreeDisplay);

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
        panelWithButtons.setLayout(new FlowLayout());
        panelWithButtons.add(firstButton);
        panelWithButtons.add(previousButton);
        panelWithButtons.add(nextButton);
        panelWithButtons.add(lastButton);
        return panelWithButtons;
    }
    //Model (Browser providing view on chess game)
    private GameBrowser gameBrowser;
    //Gui providing view into model
    private ChessboardComponent chessboardComponent;
    private AdHocMoveDisplayArea adHocMoveDisplayer;
    private GameTreeDisplayer adHocGameTreeDisplayer;
    private GameStateDisplayer adHocGameStateDisplayer;
    //
    private static final Logger log = LoggerFactory.getLogger(MainWindow.class);
}
