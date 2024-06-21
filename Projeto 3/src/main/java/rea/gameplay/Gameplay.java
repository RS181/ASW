package rea.gameplay;

import rea.components.Avatar;
import rea.components.GameMap;

import java.util.Set;

/**
 * <p>Abstract definition of a gameplay in REA.
 * That is, the distinctive features of each game, such as its name,
 * its game map with places and items, the number of characters,
 * the condition in which it is completed.</p>
 * implNote - an abstract participant of the <b>Factory method</b> design pattern.
 */
public interface Gameplay {

    /**
     * The name of the game as a short string.
     * @return name of the game
     */
    String getName();

    /**
     * A long description of the game.
     * @return description of the game
     */
    String getDescription();

    /**
     * Produce a map for a game instance.
     * A map is a collection of interconnected places.
     * This method returns the place where the game starts.
     * When players enter the game they will be in this place.
     * The place can (and should) be connected to other places.
     * Each invocation of this method must return a new instance,
     * although with a similar structure.
     * @return the place where the game starts
     */
    GameMap makeGameMap();

    /**
     * The set of avatars that can be used in this game.
     * @return the set of avatars
     */
    Set<Avatar> getAvatars();

    /**
     * Check if the game has ended.
     * For instance, if a player (or all players) reached a certain place, or
     * if a certain number of items have been collected.
     * @param gameMap for this game instance
     * @return <code>true</code> if the game has ended, <code>false</code> otherwise
     */
    boolean gamedEnded(GameMap gameMap);

    /**
     * Maximum number of players accepted in this game
     * @return maximum number of players
     */
    int getMaxPlayers();

    /**
     * Minimum number of players accepted in this game
     * @return minimum number of players
     */
    int getMinPlayers();
}
