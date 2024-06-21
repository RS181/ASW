package rea.gameplay;

import java.util.Set;

/**
 * implNote - an abstract participant of the <b>Factory method</b> design pattern.
 */
public interface AbstractGameplayFactory {

    /**
     * Get the available games in this factory.
     * @return the available games.
     */
    Set<String> getAvailableGameplays();

    /**
     * Get the gameplay for a game with a given name.
     * @param name of the game.
     * @return the gameplay for the given game.
     */
    Gameplay getGameplay(String name);
}
