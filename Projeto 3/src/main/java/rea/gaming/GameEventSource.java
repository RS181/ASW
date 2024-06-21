package rea.gaming;

import rea.ReaException;
import rea.components.Character;
import rea.components.Place;
import rea.events.*;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>Source of game related events.
 * This class will be extended by {@link GameInstance},
 * to which it provides methods for adding listeners and methods for broadcasting,
 * multicasting or unicasting events.
 * This class delegates on methods of {@link EventBroadcast} and {@link EventMulticast} instances.</p>
 */
public class GameEventSource {

    /**
     * The listeners registered to receive game changed events.
     */
    final EventBroadcast<GameChangedEvent> gameChanged = new EventBroadcast<>();

    /**
     * The listeners registered to receive inventory update events.
     */
    final EventMulticast<InventoryUpdateEvent> inventoryUpdate = new EventMulticast<>();

    /**
     * The listeners registered to receive message update events.
     */
    final EventMulticast<MessageUpdateEvent> messageUpdate = new EventMulticast<>();

    /**
     * The listeners registered to receive scene update events.
     */
    final EventMulticast<SceneUpdateEvent> sceneUpdate = new EventMulticast<>();

    /**
     * The set of players in this game.
     */
    final Set<Player> players = new HashSet<>();

    /**
     * Create an instance of this class.
     * No particular initializations are performed.
     */
    protected GameEventSource() {
    }

    /**
     * Add a listener for {@link GameChangedEvent}.
     * These events will be broadcast to all registered listeners.
     * @param listener of game changed events.
     */
    public void addGameChangedListener(UpdateListener<GameChangedEvent> listener){
        try{
            this.gameChanged.addListener(listener);
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Add a listener for {@link InventoryUpdateEvent}.
     * Only identified players can add these listeners.
     * The player will be used as key to this listener in multicasting.
     * @param player adding the listener
     * @param listener of inventory update events
     * @throws ReaException if player not in this game
     */
    public void addInventoryUpdateListener(Player player,
                                           UpdateListener<InventoryUpdateEvent> listener)
            throws ReaException {

        if (player == null)
            throw new ReaException("GameEventSource.addInventoryUpdateListener: Player is null");

        if (player.playerCharacter == null)
            throw new ReaException("GameEventSource.addInventoryUpdateListener: Character is null for Player.");

        if (!players.contains(player))
            throw new ReaException("GameEventSource.addInventoryUpdateListener: Player is not in this game.");

        try{
            this.inventoryUpdate.addListener(player.playerCharacter, listener);
        }
        catch (Exception e){
            throw new ReaException(e.getMessage());
        }
    }

    /**
     * Add a listener for {@link MessageUpdateEvent}.
     * Only identified players can add these listeners.
     * The player will be used as key to this listener in multicasting.
     * @param player adding the listener
     * @param listener of message update events
     * @throws ReaException if player not in this game
     */
    public void addMessageUpdateListener(Player player,
                                         UpdateListener<MessageUpdateEvent> listener)
            throws ReaException {
        if (player == null)
            throw new ReaException("GameEventSource.addMessageUpdateListener: Player is null.");

        if (player.playerCharacter == null)
            throw new ReaException("GameEventSource.addMessageUpdateListener: Character is null for Player.");

        if (!players.contains(player))
            throw new ReaException("GameEventSource.addMessageUpdateListener: Player is not in this game.");

        this.messageUpdate.addListener(player.playerCharacter, listener);

    }

    /**
     * Add a listener for {@link SceneUpdateEvent}.
     * Only identified players can add these listeners.
     * The player will be used as key to this listener in multicasting.
     * @param player adding the listener
     * @param listener of scene update events
     * @throws ReaException if player not in this game
     */
    public void addSceneUpdateListener(Player player,
                                       UpdateListener<SceneUpdateEvent> listener)
            throws ReaException {
        if (player == null)
            throw new ReaException("GameEventSource.addSceneUpdateListener: Player is null");

        if (player.playerCharacter == null)
            throw new ReaException("GameEventSource.addSceneUpdateListener: Character is null for Player.");

        if (!players.contains(player))
            throw new ReaException("GameEventSource.addSceneUpdateListener: Player is not in this game.");

        try{
            this.sceneUpdate.addListener(player.playerCharacter, listener);
        }
        catch (Exception e){
            throw new ReaException(e.getMessage());
        }
    }

    /**
     * Broadcast major game changes to all registered listeners.
     * It is typically called when players are added to a game, or when the game starts or ends.
     * The argument is the changed game instance itself, passed as argument by the calling method
     * from a class extension (e.g. <code>broadcastGameChanged(this)</code>).
     * @param gameInstance changed
     */
    void broadcastGameChanged(GameInstance gameInstance) {
        this.gameChanged.broadcast(new GameChangedEvent(gameInstance));
    }

    /**
     * Multicast players in the given scene with the current state of that scene.
     * @param place to notify
     */
    void multicastSceneUpdate(Place place) {

        if (place == null)
            throw new RuntimeException("GameEventSource.multicastSceneUpdate: place is null.");

        this.sceneUpdate.multicast(
                place.getCharacters(),
                new SceneUpdateEvent( place.getVisual(), place.getPositionables() )
        );
    }

    /**
     * Multicasts players in the given place with a message.
     * @param place with players to multicast
     * @param character that sends the message
     * @param message to multicast
     */
    void multicastMessageUpdate(Place place, Character character, String message) {

        if (place == null || place.getCharacters().isEmpty())
            throw new RuntimeException("GameEventSource.multicastMessageUpdate: Place is null or no Characters in Place to receive message.");

        if (message.isEmpty() || character == null)
            throw new RuntimeException("GameEventSource.multicastMessageUpdate: Message is empty or no Character to speak message.");

        this.messageUpdate.multicast(
                place.getCharacters(),
                new MessageUpdateEvent(character, message)
        );

    }

    /**
     * Sends a {@link MessageUpdateEvent} to the character
     * @param character that receive the message
     * @param message to be sent
     */
    void unicastMessageUpdate(Character character, String message) {
        if (message.isEmpty())
            throw new RuntimeException("GameEventSource.unicastMessageUpdate: Message is empty.");

        if (character == null)
            throw new RuntimeException("GameEventSource.unicastMessageUpdate: no Character to speak message (character is null).");

        this.messageUpdate.unicast(
                character
                , new MessageUpdateEvent(message));
    }

    /**
     * Sends a InventoryUpdateEvent to the character given as argument.
     * @param character to be updated
     */
    void unicastInventoryUpdate(Character character) {
        if (character == null)
            throw new RuntimeException("GameEventSource.unicastInventoryUpdate: Character is null.");

        this.inventoryUpdate.unicast(
                character,
                new InventoryUpdateEvent(character.getInventory(), character.getHolding())
        );
    }

}
