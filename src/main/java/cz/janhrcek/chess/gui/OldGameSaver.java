package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.model.impl.OldGameStateMutable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

/**
 * This class makes all steps necessary to save the game into selected file in
 * pgn format.
 *
 * @author xhrcek
 */
public class OldGameSaver extends JDialog {

    /**
     * Creates new form OldGameSaver
     *
     * @param owner the frame on which this dialog depends
     * @param gameToWrite the game, which we want to save to file
     * @param outputFile the file in which we want to save te game
     */
    public OldGameSaver(JFrame owner, OldGameStateMutable gameToWrite, File outputFile) {
        super(owner, "Saving game ...");
        if (gameToWrite == null) {
            throw new NullPointerException("gameToWrite can't be null!");
        }
        if (outputFile == null) {
            throw new NullPointerException("outputFile can't be null!");
        }
        this.gameToWrite = gameToWrite;
        this.outputFile = outputFile;
        initComponents();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        okButton.addActionListener(myActionListener);
        cancelButton.addActionListener(myActionListener);
    }
    private ActionListener myActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(okButton)) {
                //after pressing ok button extract information about the game
                boolean headerFilledOK = fillInHeader();
                if (!headerFilledOK) {
                    return;
                }

                //find out whether user wants to append or overwrite
                boolean appendFlag;
                Object[] options = {"Append", "Overwrite", "Cancel Saving"};
                int n = JOptionPane.showOptionDialog(OldGameSaver.this,
                        "Do you want to APPEND game to the end of selected"
                        + " file or\n would you like to OVERWRITE the selected file",
                        "Append or Overwrite?",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (n == 0) {
                    appendFlag = true;
                } else if (n == 1) {
                    appendFlag = false;
                } else {
                    dispose(); //canceling savin operation
                    return;
                }

                //and write the game to selected output file in PGN format
                Writer output = null;
                try {
                    System.out.println(gameToWrite.toString());
                    output =
                            new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(outputFile, appendFlag), "ISO-8859-1"));
                    output.append(gameToWrite.toString());
                    output.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                dispose();
            } else if (e.getSource().equals(cancelButton)) {
                dispose();
            }
        }
    };

    /**
     * Fills in header of current game.
     *
     * @return true if filling header went OK, false otherwise
     */
    private boolean fillInHeader() {
        OldGameStateMutable.GameHeader header = gameToWrite.getGameHeader();
        //extract values from the fields...
        header.setEvent(jTextField1.getText());
        header.setSite(jTextField2.getText());
        if (!checkDate(jTextField3.getText())) {
            JOptionPane.showMessageDialog(null, "Date must have form"
                    + " YYYY.MM.DD but you entered "
                    + jTextField3.getText());
            return false;
        }
        header.setDate(jTextField3.getText());
        header.setRound(jTextField4.getText());
        header.setWhite(jTextField5.getText());
        header.setBlack(jTextField6.getText());
        header.setResult(getResult((String) jComboBox1.getSelectedItem()));
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        jLabel1.setText("Please enter the following information about the game");

        jLabel2.setText("Event");
        jLabel2.setToolTipText("Enter the name of the event during which the game was played.");

        jLabel3.setText("Site");
        jLabel3.setToolTipText("Enter the name site, where the game was played.");

        jLabel4.setText("Date");
        jLabel4.setToolTipText("Enter date, when the game was played. (Date must have formYYYY.MM.DD)");

        jLabel5.setText("Round");
        jLabel5.setToolTipText("Enter the Round, in which the game was played.");

        jLabel6.setText("White");
        jLabel6.setToolTipText("Enter the name of white player.");

        jLabel7.setText("Black");
        jLabel7.setToolTipText("Enter the name of Black player.");

        jLabel8.setText("Result");
        jLabel8.setToolTipText("Choose the result of the game.");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"White won", "Black won", "Draw", "Unknown"}));

        okButton.setText("OK");
        okButton.setToolTipText("Saves the game and the information about into selected file.");

        cancelButton.setText("Cancel");
        cancelButton.setToolTipText("Cancels saving of the game.");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jLabel1)
                .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jLabel2)
                .addComponent(jLabel3)
                .addComponent(jLabel4)
                .addComponent(jLabel5)
                .addComponent(jLabel6)
                .addComponent(jLabel7)
                .addComponent(jLabel8))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(jComboBox1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(jTextField2, GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addComponent(jTextField3, GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addComponent(jTextField4, GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addComponent(jTextField5, GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addComponent(jTextField6, GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                .addComponent(/*GroupLayout.Alignment.TRAILING,*/jTextField1, GroupLayout.PREFERRED_SIZE, 274, GroupLayout.PREFERRED_SIZE)))
                .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(cancelButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(okButton)))
                .addContainerGap()));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel2)
                .addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel3)
                .addComponent(jTextField2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel4)
                .addComponent(jTextField3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel5)
                .addComponent(jTextField4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel6)
                .addComponent(jTextField5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel7)
                .addComponent(jTextField6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel8)
                .addComponent(jComboBox1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(okButton)
                .addComponent(cancelButton))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        pack();
    }

    private boolean checkDate(String date) {
        if (date == null || "".equals(date)) {
            return true;
        }
        String[] tokens = date.split("\\.");
        if (tokens.length != 3) {
            System.out.println("tokenu je: " + tokens.length);
            return false;
        } else if (tokens[0].length() != 4
                || tokens[1].length() != 2
                || tokens[2].length() != 2) {
            return false;
        } else {
            return true;
        }
    }

    private String getResult(String result) {
        switch (result) {
            case "White won":
                return "1-0";
            case "Black won":
                return "0-1";
            case "Draw":
                return "1/2-1/2";
            default:
                return "*";
        }
    }
    // Variables declaration - do not modify
    private JComboBox jComboBox1;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JTextField jTextField1;
    private JTextField jTextField2;
    private JTextField jTextField3;
    private JTextField jTextField4;
    private JTextField jTextField5;
    private JTextField jTextField6;
    private JButton okButton;
    private JButton cancelButton;
    // End of variables declaration
    private OldGameStateMutable gameToWrite;
    private File outputFile;
}
