package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.model.Game;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * The model for the table containing the games, which were parsed from some pgn
 * file. Each row contains Sevent tags describing one game.
 *
 * @author xhrcek
 */
public class GamesTableModel extends AbstractTableModel {

    private List<Game> games;
    private int NUM_OF_TAGS = 7;
    private String[] columnNames = {"White", "Black", "Result", "Event",
        "Site", "Date", "Round"};

    /**
     * Creates a new instance of GamesTableModel
     *
     * @param games The list of games about which the information in this table
     * will be displayed.
     */
    public GamesTableModel(List<Game> games) {
        if (games == null) {
            throw new NullPointerException("Games can't be null");
        }
        this.games = games;
    }

    /**
     * Returns the name of given column.
     *
     * @param col the number of the column (from 1 to getColumnCount())
     * @return the name of the column
     */
    @Override
    public String getColumnName(int col) {
        if (col < 0 || col >= NUM_OF_TAGS) {
            throw new IllegalArgumentException("col must be between 0 and "
                    + NUM_OF_TAGS);
        }
        return columnNames[col];
    }

    /**
     * Returns the number of rows in this table.
     *
     * @return the number of the rows in this table model.
     */
    public int getRowCount() {
        return games.size();
    }

    /**
     * Returns the number of rows inthis column.
     *
     * @return the nuber of the columns in this table
     */
    public int getColumnCount() {
        return 7;
    }

    /**
     * Returns the value contained in given column and row
     *
     * @param rowIndex the index of the row
     * @param columnIndex the index of the column
     * @return The object stored in the cell of the table contained in given row
     * and column.
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= games.size()) {
            throw new IllegalArgumentException("row index must be"
                    + " between 0 and games.size()!");
        }
        if (columnIndex < 0 || columnIndex > NUM_OF_TAGS) {
            throw new IllegalArgumentException("Number of colums must be"
                    + " between 0 and " + NUM_OF_TAGS);
        }
        Game.GameHeader header = games.get(rowIndex).getGameHeader();

        switch (columnIndex) {
            case 0:
                return header.getWhite();
            case 1:
                return header.getBlack();
            case 2:
                return header.getResult();
            case 3:
                return header.getEvent();
            case 4:
                return header.getSite();
            case 5:
                return header.getDate();
            case 6:
                return header.getRound();
            default:
                throw new IllegalArgumentException("column index not"
                        + " between 0 and " + NUM_OF_TAGS);
        }
    }
}
