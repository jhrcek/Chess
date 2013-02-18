package cz.janhrcek.chess.model.api;

/**
 *
 * @author jhrcek
 */
public interface GameListener {
        /**
     * This method should contain code which reacts to the GameModelEvent.
     *
     * @param event the event fired by some game model representing the change
     * of the model.
     */
    void gameChangedChanged(GameEvent event);
}
