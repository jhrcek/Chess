package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.FEN.FenParser;
import cz.janhrcek.chess.FEN.InvalidFenException;
import cz.janhrcek.chess.model.api.Game;
import cz.janhrcek.chess.model.impl.FIDERuleChecker;
import cz.janhrcek.chess.model.impl.GameImpl;
import cz.janhrcek.chess.model.impl.GameStateFactoryImpl;
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author jhrcek
 */
public class MainWindow extends JFrame {

    public MainWindow() throws HeadlessException {
        super("Chess");
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

//        initActions();
//        initMenus();
//        initButtons();

        //create representation of gamestate
        try {
            game = new GameImpl(FenParser.INITIAL_STATE_FEN, new GameStateFactoryImpl(new FIDERuleChecker()));
        } catch (InvalidFenException ex) {
            throw new IllegalStateException("Unexpected exception thrown", ex);
        }
        
        
        
        chessboardComponent = new ChessboardComponent(game);
        chessboardComponent.addMoveSelectedListener((MoveSelectedListener) game);
        //this.add(chessboardComponent);
        
        AdHocMoveDisplayArea moveDisplay = new AdHocMoveDisplayArea(game);
        //this.add(moveDisplay);
        
        JScrollPane moveScroll = new JScrollPane(moveDisplay);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                chessboardComponent, moveScroll);
        splitPane.setDividerLocation(300);
        splitPane.setDividerSize(1);
        
        this.add(splitPane);
//       
//
//        //create okno na zobrazovani tahu
//        moveDisplayWindow = new MoveDisplayWindow(stateModel);
//        //vytvorime tabulku na zobrazovani parslych her
//        gamesTable = new JTable(gamesTableModel);
//        gamesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        myLSListener = new Main.MyListSelectionListener();
//        rowSelectionModel = gamesTable.getSelectionModel();
//        rowSelectionModel.addListSelectionListener(myLSListener);
//
//        JPanel navigButtonPanel = new JPanel();
//        navigButtonPanel.add(firstButton);
//        navigButtonPanel.add(previousButton);
//        navigButtonPanel.add(cancelLastButton);
//        navigButtonPanel.add(nextButton);
//        navigButtonPanel.add(lastButton);
//
//
//        JScrollPane moveDisplPane = new JScrollPane(moveDisplayWindow);
//        moveDisplPane.setHorizontalScrollBarPolicy(
//                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//
//        JSplitPane rightPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
//                moveDisplPane, new JScrollPane(gamesTable));
//        rightPane.setDividerLocation(230);
//        rightPane.setDividerSize(4);
//
//        JSplitPane upperPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
//                chessboardComponent, rightPane);
//        upperPane.setDividerLocation(425);
//        upperPane.setDividerSize(4);
//
//        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
//                upperPane, navigButtonPanel);
//        mainSplitPane.setDividerSize(4);
//        mainSplitPane.setDividerLocation(463);
//
//        add(mainSplitPane);
        setSize(300, 500);
    }
    private Game game;
    private ChessboardComponent chessboardComponent;
}
