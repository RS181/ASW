package rea.events;

import rea.gaming.GameInstance;

/**
 * Event that indicates that a game instance has changed.
 * For instance, a new player joined the game, the game stared, or the game ended.
 */
public class GameChangedEvent
        implements UpdateEvent {

    final private GameInstance gameInstance;

    /**
     * Create a game changed event.
     * @param gameInstance the game instance that changed
     */
    public GameChangedEvent(GameInstance gameInstance){
        this.gameInstance = gameInstance;
    }

    /**
     * Get the game instance that changed.
     * @return the game instance that changed
     */
    public GameInstance getGameInstance() {
        return this.gameInstance;
    }
}
