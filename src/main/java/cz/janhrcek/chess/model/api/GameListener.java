package cz.janhrcek.chess.model.api;

/**
 *
 * @author jhrcek
 */
public interface GameListener {

    /**
     * This method should contain code which reacts to the GameBrowserChangedEvent.
     *
     * @param event the event fired by some game representing the change
     * of the game.
     */
    void gameChanged(GameBrowserChangedEvent event);
}
