package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.PGN.PGNReader;
import cz.janhrcek.chess.model.impl.BrowsableGameOld;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * This class is the main entry point of the application. You can run the whole
 * application by invoking this class's main method.
 *
 * @author xhrcek
 */
public class Main extends JFrame implements ActionListener {

    /**
     * Creates new instance of Main class.
     */
    public Main() {
        super("MyChess"); //set frame title
        initComponents();
    }

    /**
     * This method reacts to events fired by menu items.
     *
     * @param e The event fired by some menu item.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loadFileItem) {
            int returnVal = myFileChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                selectedFile = myFileChooser.getSelectedFile();
                try {
                    handleOpenedFile(selectedFile);
                } catch (FileNotFoundException fnfe) {
                    fnfe.printStackTrace();
                }
            }
            //Handle save button action.
        } else if (e.getSource() == saveFileItem) {
            int returnVal = myFileChooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                selectedFile = myFileChooser.getSelectedFile();
                if (!selectedFile.getName().endsWith(".pgn")) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".pgn");
                }
                myGameSaver = new GameSaver(this, currentlyWiewedGame, selectedFile);
                myGameSaver.setVisible(true);
            }
        } else if (e.getSource() == exitItem) {
            System.exit(0);
        }
    }

    /**
     * This action sets gui so that it is ready for playing new game.
     */
    public class NewGameAction extends AbstractAction {

        /**
         * creates new instance of NewGameAction.
         *
         * @param text Short text describing this action
         * @param desc Longer description which will be displayed in the tool
         * tip for this action.
         * @param mnemonic The keyboard shortcut by whic this action can be
         * fired.
         */
        public NewGameAction(String text, String desc, Integer mnemonic) {
            super(text);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        /**
         * This method contains code which implements the raction to the
         * occurence of this action.
         *
         * @param e Action event carrying information about the occurence of
         * this action.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            viewingMode = false;
            chessboardComponent.setClickable(true);
            cancelLastAction.setEnabled(true);

            currentlyWiewedGame = new BrowsableGameOld();
            stateModel.setGame(currentlyWiewedGame);
            chessboardComponent.addMoveSelectedEventListener(stateModel);
            chessboardComponent.setModel(stateModel);
            moveDisplayWindow.setModel(stateModel);
            moveDisplayWindow.writeOutMoves();///////////////
            chessboardComponent.repaint();/////////////
        }
    }

    /**
     * This Action cancels last move of the current game.
     */
    public class CancelLastAction extends AbstractAction {

        /**
         * creaes new instance of CancelLastAction.
         *
         * @param text Short text describing this action
         * @param desc Longer description which will be displayed in the tool
         * tip for this action.
         * @param mnemonic The keyboard shortcut by which this action can be
         * fired.
         */
        public CancelLastAction(String text, String desc, Integer mnemonic) {
            super(text);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        /**
         * This method contains code which implements the raction to the
         * occurence of this action.
         *
         * @param e Action event carrying information about the occurence of
         * this action.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            stateModel.cancelLastMove();
        }
    };

    /**
     * This Action sets gui chessboard representation so that it displays
     * starting position of current game.
     */
    public class FirstAction extends AbstractAction {

        /**
         * creates new instance of FirstAction.
         *
         * @param text Short text describing this action
         * @param desc Longer description which will be displayed in the tool
         * tip for this action.
         * @param mnemonic The keyboard shortcut by which this action can be
         * fired.
         */
        public FirstAction(String text, String desc, Integer mnemonic) {
            super(text);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        /**
         * This method contains code which implements the raction to the
         * occurence of this action.
         *
         * @param e Action event carrying information about the occurence of
         * this action.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            stateModel.setFirstPosition();
        }
    };

    /**
     * This Action sets current gui chessboard representation so that it
     * displays chessboard one move before the position it currentlu displays
     * (if there was any other move played before).
     */
    public class PreviousAction extends AbstractAction {

        /**
         * Creates instance of PreviousAction.
         *
         * @param text Short text describing this action
         * @param desc Longer description which will be displayed in the tool
         * tip for this action.
         * @param mnemonic The keyboard shortcut by which this action can be
         * fired.
         */
        public PreviousAction(String text, String desc, Integer mnemonic) {
            super(text);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        /**
         * This method contains code which implements the raction to the
         * occurence of this action.
         *
         * @param e Action event carrying information about the occurence of
         * this action.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            stateModel.setPreviousPosition();
        }
    };

    /**
     * This Action sets current gui chessboard representation so that it
     * displays chessboard one move after the position it currentlu displays (if
     * there was any other move played before).
     */
    public class NextAction extends AbstractAction {

        /**
         * creates instance of the NextAction.
         *
         * @param text Short text describing this action
         * @param desc Longer description which will be displayed in the tool
         * tip for this action.
         * @param mnemonic The keyboard shortcut by which this action can be
         * fired.
         */
        public NextAction(String text, String desc, Integer mnemonic) {
            super(text);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        /**
         * This method contains code which implements the raction to the
         * occurence of this action.
         *
         * @param e Action event carrying information about the occurence of
         * this action.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            stateModel.setNextPosition();
        }
    };

    /**
     * This Action sets gui chessboard representation so that it displays last
     * position (after all moves that were played) of current game.
     */
    public class LastAction extends AbstractAction {

        /**
         * Creates new instance of LastAction.
         *
         * @param text Short text describing this action
         * @param desc Longer description which will be displayed in the tool
         * tip for this action.
         * @param mnemonic The keyboard shortcut by which this action can be
         * fired.
         */
        public LastAction(String text, String desc, Integer mnemonic) {
            super(text);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        /**
         * This method contains code which implements the raction to the
         * occurence of this action.
         *
         * @param e Action event carrying information about the occurence of
         * this action.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            stateModel.setLastPositon();
        }
    };

    /**
     * By calling this method you run the whole application.
     *
     * @param args The optional command line arguments. This methods behaviour
     * cannot be changed by any command line arguments.
     */
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);

            }
        });
    }

/////------------PRIVATE IMPLEMENTATION--------------------////////
    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        myFileChooser = new JFileChooser();
        myFileChooser.setFileFilter(new PGNFileFilter());

        initActions();
        initMenus();
        initButtons();

        //create representation of gamestate
        currentlyWiewedGame = new BrowsableGameOld();
        stateModel = new MyGameModel(currentlyWiewedGame);
        chessboardComponent = new ChessboardComponent(stateModel);
        chessboardComponent.addMoveSelectedEventListener(stateModel);

        //create okno na zobrazovani tahu
        moveDisplayWindow = new MoveDisplayWindow(stateModel);
        //vytvorime tabulku na zobrazovani parslych her
        gamesTable = new JTable(gamesTableModel);
        gamesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myLSListener = new MyListSelectionListener();
        rowSelectionModel = gamesTable.getSelectionModel();
        rowSelectionModel.addListSelectionListener(myLSListener);

        JPanel navigButtonPanel = new JPanel();
        navigButtonPanel.add(firstButton);
        navigButtonPanel.add(previousButton);
        navigButtonPanel.add(cancelLastButton);
        navigButtonPanel.add(nextButton);
        navigButtonPanel.add(lastButton);

        /*JSplitPane leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
         chessboardComponent, navigButtonPanel);
         leftPane.setDividerLocation(460);
         leftPane.setDividerSize(4);
         
         
         
         JSplitPane rightPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
         moveDisplPane , new JScrollPane(gamesTable));
         rightPane.setDividerLocation(300);
         rightPane.setDividerSize(4);
         
         JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
         leftPane, rightPane);
         mainSplitPane.setDividerSize(4);*/

        JScrollPane moveDisplPane = new JScrollPane(moveDisplayWindow);
        moveDisplPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JSplitPane rightPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                moveDisplPane, new JScrollPane(gamesTable));
        rightPane.setDividerLocation(230);
        rightPane.setDividerSize(4);

        JSplitPane upperPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                chessboardComponent, rightPane);
        upperPane.setDividerLocation(425);
        upperPane.setDividerSize(4);

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                upperPane, navigButtonPanel);
        mainSplitPane.setDividerSize(4);
        mainSplitPane.setDividerLocation(463);

        add(mainSplitPane);
        setSize(650, 550);
    }

    /**
     * Initialize menus, menu items, menu bar.
     */
    private void initMenus() {
        mainMenuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        gameMenu = new JMenu("Game");
        helpMenu = new JMenu("Help");
        saveFileItem = new JMenuItem("Save to PGN");
        loadFileItem = new JMenuItem("Load from PGN");
        exitItem = new JMenuItem("Exit");
        newGameItem = new JMenuItem(newGameAction);
        firstItem = new JMenuItem(firstAction);
        previousItem = new JMenuItem(previousAction);
        nextItem = new JMenuItem(nextAction);
        lastItem = new JMenuItem(lastAction);

        saveFileItem.addActionListener(this);
        loadFileItem.addActionListener(this);
        exitItem.addActionListener(this);

        setJMenuBar(mainMenuBar);
        mainMenuBar.add(fileMenu);
        mainMenuBar.add(gameMenu);
        mainMenuBar.add(helpMenu);
        fileMenu.add(loadFileItem);
        fileMenu.add(saveFileItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        gameMenu.add(newGameItem);
        gameMenu.addSeparator();
        gameMenu.add(firstItem);
        gameMenu.add(previousItem);
        gameMenu.add(nextItem);
        gameMenu.add(lastItem);
    }

    /**
     * Initializes actions.
     */
    private void initActions() {
        cancelLastAction = new CancelLastAction("Cancel Last",
                "Cancels last move of the game",
                new Integer(KeyEvent.VK_C));
        firstAction = new FirstAction("First",
                "Jumps to first position of the game",
                new Integer(KeyEvent.VK_F));
        previousAction = new PreviousAction("Previous",
                "Jumps to the previous move of the game",
                new Integer(KeyEvent.VK_P));
        nextAction = new NextAction("Next",
                "Jumps to the next move of the game",
                new Integer(KeyEvent.VK_N));
        lastAction = new LastAction("Last",
                "Jumps to the last move of the game",
                new Integer(KeyEvent.VK_L));
        newGameAction = new NewGameAction("New Game",
                "Sets up new game",
                new Integer(KeyEvent.VK_G));
    }

    /**
     * Initializes buttons.
     */
    private void initButtons() {
        firstButton = new JButton(firstAction);
        previousButton = new JButton(previousAction);
        cancelLastButton = new JButton(cancelLastAction);
        nextButton = new JButton(nextAction);
        lastButton = new JButton(lastAction);
    }

    /**
     * When user selects row with the game in the games table this method makes
     * all changes neccesary to display the selected game.
     */
    private class MyListSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            //Ignore extra messages.
            if (e.getValueIsAdjusting()) {
                return;
            }

            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            if (lsm.isSelectionEmpty()) {
                currentlyWiewedGame = parsedGames.get(0);
                stateModel.setGame(currentlyWiewedGame);
                chessboardComponent.addMoveSelectedEventListener(stateModel);
                chessboardComponent.setModel(stateModel);
                moveDisplayWindow.setModel(stateModel);
                moveDisplayWindow.writeOutMoves();
                chessboardComponent.repaint();
            } else {
                int selectedRow = lsm.getMinSelectionIndex();
                currentlyWiewedGame = parsedGames.get(selectedRow);
                stateModel.setGame(currentlyWiewedGame);
                chessboardComponent.addMoveSelectedEventListener(stateModel);
                chessboardComponent.setModel(stateModel);
                moveDisplayWindow.setModel(stateModel);
                moveDisplayWindow.writeOutMoves();
                chessboardComponent.repaint();
                viewingMode = true;
                chessboardComponent.setClickable(false);
                cancelLastAction.setEnabled(false);
            }
        }
    };

    private static ImageIcon createNavigationIcon(String imageName) {
        String imgLocation = "images/" + imageName + ".png";
        java.net.URL imageURL = Main.class.getResource(imgLocation);

        if (imageURL == null) {
            System.err.println("Resource not found: "
                    + imgLocation);
            return null;
        } else {
            return new ImageIcon(imageURL);
        }
    }

    private void handleOpenedFile(File f) throws FileNotFoundException {
        parsedGames = PGNReader.parseFile(f);
        gamesTableModel = new GamesTableModel(parsedGames);
        gamesTable.setModel(gamesTableModel);

        currentlyWiewedGame = parsedGames.get(0);
        stateModel.setGame(currentlyWiewedGame);
        chessboardComponent.addMoveSelectedEventListener(stateModel);
        chessboardComponent.setModel(stateModel);
        moveDisplayWindow.setModel(stateModel);
        moveDisplayWindow.writeOutMoves();/////
        chessboardComponent.repaint();////
        viewingMode = true;
        chessboardComponent.setClickable(false);
        cancelLastAction.setEnabled(false);
    }
    /*--------------  PRIVATE FIELDS ----------------*/
    //my GUI stuff
    /**
     * The game whose state is displayed on the gui chessboard.
     */
    private BrowsableGameOld currentlyWiewedGame;
    /**
     * The model (encapsulating currently wieved game) from which the
     * chessboardcomponent gets all the information it displays.
     */
    private MyGameModel stateModel;
    /**
     * Gui representation of the chessboard.
     */
    private ChessboardComponent chessboardComponent;
    /**
     * The area where the moves of currently played/wieved game are displayed.
     */
    private MoveDisplayWindow moveDisplayWindow;
    /**
     * Table for displaying games loaded from some pgn file.
     */
    private JTable gamesTable;
    /**
     * Model for table for displaying games loaded from some pgn file.
     */
    private GamesTableModel gamesTableModel;
    /**
     * List of games loaded form some pgn file (if any is opened).
     */
    private List<BrowsableGameOld> parsedGames;
    private ListSelectionModel rowSelectionModel;
    private MyListSelectionListener myLSListener;
    /**
     * We either in viewingMode (true) or in playing mode (false).
     */
    private boolean viewingMode;
    private GameSaver myGameSaver;
    //buttons
    private JButton firstButton;
    private JButton previousButton;
    private JButton cancelLastButton;
    private JButton nextButton;
    private JButton lastButton;
    //actions
    private Action cancelLastAction;
    private Action firstAction;
    private Action previousAction;
    private Action nextAction;
    private Action lastAction;
    private Action newGameAction;
    //menus
    private JMenuBar mainMenuBar;
    private JMenu fileMenu;
    private JMenu gameMenu;
    private JMenu helpMenu;
    private JMenuItem loadFileItem;
    private JMenuItem saveFileItem;
    private JMenuItem exitItem;
    private JMenuItem newGameItem;
    private JMenuItem firstItem;
    private JMenuItem previousItem;
    private JMenuItem nextItem;
    private JMenuItem lastItem;
    private JFileChooser myFileChooser;
    private File selectedFile;
}
