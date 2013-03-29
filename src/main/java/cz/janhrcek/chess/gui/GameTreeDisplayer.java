package cz.janhrcek.chess.gui;

import cz.janhrcek.chess.model.api.GameBrowser;
import cz.janhrcek.chess.model.api.GameBrowserChangedEvent;
import cz.janhrcek.chess.model.api.GameListener;
import java.util.Enumeration;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
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
    private GameBrowser gameBrowser;

    public GameTreeDisplayer(final GameBrowser gameBrowser) {
        this.gameBrowser = gameBrowser;
        setContentType("text/html");
        setEditable(false);

        //Remove all style rules ...
        StyleSheet sheet = ((HTMLEditorKit) getEditorKit()).getStyleSheet();
        Enumeration rules = sheet.getStyleNames();
        while (rules.hasMoreElements()) {
            String name = (String) rules.nextElement();
            sheet.removeStyle(name);
        }
        //and add only rules for displaying "a" html tag
        sheet.addRule("a.focus {font-weight:bold;text-decoration:underline;}");
        sheet.addRule("a {color:black;}");

        setText(gameBrowser.toString());

        addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    log.info("User clicked link with id = {}", e.getDescription());
                    gameBrowser.focusPositionWithId(Integer.valueOf(e.getDescription()));
                }
            }
        });
    }

    @Override
    public void gameChanged(GameBrowserChangedEvent event) {
        setText(gameBrowser.toString());
    }
}
