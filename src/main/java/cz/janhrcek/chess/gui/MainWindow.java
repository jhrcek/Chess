package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.FEN.FenParser;
import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.model.api.Game;
import cz.janhrcek.chess.model.impl.FIDERuleChecker;
import cz.janhrcek.chess.model.impl.GameImpl;
import cz.janhrcek.chess.model.impl.GameStateFactoryImpl;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
            game = new GameImpl(FenParser.INITIAL_STATE_FEN, new GameStateFactoryImpl(new FIDERuleChecker()));
        } catch (InvalidFenException ex) {
            throw new IllegalStateException("Unexpected exception thrown", ex);
        }

        chessboardComponent = new ChessboardComponent(game);
        chessboardComponent.addMoveSelectedListener((MoveSelectedListener) game);

        AdHocMoveDisplayArea moveDisplay = new AdHocMoveDisplayArea(game);

        JScrollPane scrollableMoveDisplay = new JScrollPane(moveDisplay);

        JPanel gameBrowseControls = new JPanel();
        gameBrowseControls.setLayout(new FlowLayout());// GridLayout(3, 3));
        gameBrowseControls.add(firstButton);
        gameBrowseControls.add(previousButton);
        gameBrowseControls.add(nextButton);
        gameBrowseControls.add(lastButton);
        previousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.focusPreviousState();
            }
        });

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 2, 10, 10));
        mainPanel.add(chessboardComponent);
        mainPanel.add(scrollableMoveDisplay);
        mainPanel.add(gameBrowseControls);

        this.add(mainPanel);
        setSize(650, 650);
    }
    //controls
    private final JButton firstButton = new JButton("First");
    private final JButton previousButton = new JButton("Previous");
    private final JButton nextButton = new JButton("Next");
    private final JButton lastButton = new JButton("Last");
    //model
    private Game game;
    //gui providing view into model
    private ChessboardComponent chessboardComponent;
}
