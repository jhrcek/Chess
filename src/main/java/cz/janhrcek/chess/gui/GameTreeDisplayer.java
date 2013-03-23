package cz.janhrcek.chess.gui;

import com.google.inject.Inject;
import cz.janhrcek.chess.model.api.GameBrowser;
import cz.janhrcek.chess.model.api.GameChangedEvent;
import cz.janhrcek.chess.model.api.GameListener;
import java.io.IOException;
import java.util.Enumeration;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jhrcek
 */
public class GameTreeDisplayer extends JEditorPane implements GameListener {

    private static final Logger log = LoggerFactory.getLogger(GameTreeDisplayer.class);
    HTMLDocument htmlDoc;
    private GameBrowser game;

    @Inject
    public GameTreeDisplayer(GameBrowser game) {
        setContentType("text/html");
        htmlDoc = (HTMLDocument) this.getDocument();
        setEditable(false);
        setText("<p><a id=\"0\" href=\"0\">Initial</a></p>"); //TODO obtain this HTML by parsing game tree

        //remove all style rules
        StyleSheet sheet = ((HTMLEditorKit) getEditorKit()).getStyleSheet();
        Enumeration rules = sheet.getStyleNames();
        while (rules.hasMoreElements()) {
            String name = (String) rules.nextElement();
            sheet.removeStyle(name);
        }
        sheet.addRule("a {color:blue;text-decoration:underline;}");

        addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    log.info("User clicked mover: description = {}", e.getDescription());
                }
            }
        });
        this.game = game;
    }

    @Override
    public void gameChanged(GameChangedEvent event) {
        try {
            Element elementRepresentingLastMove = htmlDoc.getElement(String.valueOf(moveId));
            htmlDoc.insertAfterEnd(elementRepresentingLastMove, String.format("&nbsp;<a id='%d' href=\"%d\">move%d</a>", ++moveId, moveId, moveId));
            //System.out.println(getText());
        } catch (BadLocationException | IOException ex) {
            log.error("Unexpected exception when reacting to game changed event", ex);
        }
    }
    private int moveId = 0;
}
