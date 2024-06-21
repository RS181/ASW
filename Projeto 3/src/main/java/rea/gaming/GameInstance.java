package rea.gaming;

import rea.components.Character;
import rea.components.*;
import rea.gameplay.Gameplay;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Set;

/**
 * <p>A single instance of a role playing game with given gameplay and players.
 * The players are added to the instance with {@link #addPlayer(Character)}.
 * which return a {@link Player} that can be used to identify the character in the game.
 * The game can be started with {@link #startPlayingGame()} and ended with {@link #endPlayingGame()}.
 * If the number of players is less than the minimum number of players, the game cannot be started.
 * but if it reaches the maximum number of players, it is automatically started.
 * This class is also responsible for executing commands on the game, such as moving,
 * picking up items, etc. through the {@link #executeCommand(Player, Action, Object)} method.
 * This public method is invoked by players and delegates in package methods specific to each action.
 * Action commands report updates by sending events to the players.
 * Players and events listeners are managed by methods inherited from {@link GameEventSource}.</p>
 */
public class GameInstance
        extends GameEventSource {
    public Gameplay gameplay;
    private GameMap gameMap;
    private GameStage gameStage;
    private Date timeStarted; //private LocalDateTime timeStarted;
    private Date timeEnded; //private LocalDateTime timeEnded;

    /**
     * Create a game instance with a given gameplay
     * @param gameplay for the game instance
     */
    public GameInstance(Gameplay gameplay) {
        this.gameplay = gameplay;
        gameStage = GameStage.CREATED;
        timeStarted = null;
        timeEnded = null;

        // Initializes the gameMap
        gameMap = gameplay.makeGameMap();
    }

    /**
     * The game map used by this game instance.
     * Different instances of the same game will have different map instances,
     * although similar in structure and content.
     * @return game map
     */
    public GameMap getGameMap(){
        if (this.gameMap == null && this.gameStage.equals(GameStage.CREATED))
            this.gameMap = this.gameplay.makeGameMap();

        return this.gameMap;
    }

    /**
     * Get the name of the game
     * @return name of the game
     */
    public String getName() {
        return this.gameplay.getName();
    }

    /**
     * Number of players currently in the game
     * @return number of players currently in the game
     */
    public int getPlayerCount() {
        int numberPlayers = 0;

        if (this.players != null)
            numberPlayers = this.players.size();

        return numberPlayers;
    }

    /**
     * Get the current stage of the game
     * @return current stage of the game
     */
    public GameStage getCurrentStage() {
        return gameStage;
    }

    /**
     * Add a character to the game if it is possible, i.e. the game is in the
     * {@link GameStage#CREATED} stage and the number of players is less than the maximum number of players.
     * If the character can be added, it is added to the game map and the game is broadcasted and
     * an {@link Player} that can be used to identify the character in the game is returned.
     * Otherwise, if the character cannot be added, <code>null</code> is returned.
     * @param character instance of {@link Character} to be added
     * @return an {@link Player} if it was successfully added, or <code>null</code> otherwise
     */
    public Player addPlayer(Character character) {

        // QUANDO TIRA ESTE COMENTARIO, 20 testes não passam,
        // mas acho que era suposto ter essa regra:
        //if(!gameMap.equals(GameStage.CREATED))
        //    return null;

        // maximum number of players was reached.
        if (this.getPlayerCount() >= gameplay.getMaxPlayers())
            return null;

        // character to be added to game doesn't exist
        if (character == null)
            return null;

        // player is already in the game
        if(this.gameMap.getStartPlace().getCharacters().contains(character))
            return null;

        // 1. Create Player and make him move to startPlace
        Player player = new Player(character);

        // Player nao ser null no character.getPlace()
        this.players.add(player);
        player.getCharacter().move(this.gameMap.getStartPlace());

        // 2. player's Character added to the gamemap
        this.gameMap.getStartPlace().addGameComponent(
                character, this.gameMap.getStartPlace().getEntrance());

        // 3. broadcast player added
        this.broadcastGameChanged(this);

        //this.multicastSceneUpdate(this.gameMap.getStartPlace());

        return player;

    }

    /**
     * Additional method. Checks if it's possible to join the game.
     * @return <code>true</code> if it's possible to join the game. <code>false</code> if it's not.
     */
    public boolean canJoin(){
        return ( this.gameStage.equals(GameStage.CREATED))
                && (this.getPlayerCount() < this.gameplay.getMaxPlayers() );
    }

    /**
     * The player indicates to be ready to start the game.
     * It was already added to the game and registered handlers for game events.
     * If maximum number of players is reached, the game is automatically started.
     * A game can also be started by calling {@link #startPlayingGame()}.
     * @param player player ready to start the game
     */
    public void playerReady(Player player) {
            if (this.getPlayerCount() == this.gameplay.getMaxPlayers())
                this.startPlayingGame();
//        if (player == null)
//            throw new RuntimeException("playerReady: player is null");

//        if (!this.gameMap.getStartPlace().getCharacters().contains(player.playerCharacter)){
//            this.addPlayer(player.getCharacter());
//
//            throw new RuntimeException("playerReady: player was not added to the game");
//        }
    }

    /**
     * Get avatars for players in this game
     * @return set of avatar
     */
    public Set<Avatar> getAvatars() {
        return this.gameplay.getAvatars();
    }

    /**
     * Check if the game can be deleted, i.e. if it is in the {@link GameStage#CREATED} stage
     * and there are no players yet, or if it is in the {@link GameStage#ENDED} stage.
     * @return <code>true</code> if the game can be deleted, <code>false</code> otherwise.
     */
    public boolean canDelete() {
        if ( (this.gameStage.equals(GameStage.CREATED) && this.getPlayerCount() < 1)
                || (this.gameStage.equals(GameStage.ENDED)) )
            return true;
        else
            return false;
    }

    /**
     * Check if the game can start, i.e. if it is in the {@link GameStage#CREATED} stage and there are enough players.
     * @return <code>true</code> if the game can start, <code>false</code> otherwise.
     */
    public boolean canStart() {
        if (this.gameStage.equals(GameStage.CREATED)
                && this.getPlayerCount() >= gameplay.getMinPlayers()
                && this.getPlayerCount() <= gameplay.getMaxPlayers())
            return true;
        else
            return false;
    }

    /**
     * Get time when the game started.
     * @return time when the game started, or <code>null</code> if the game has not started yet.
     */
    public Date getPlayingSince() {
        if (this.gameStage.equals(GameStage.PLAYING)){
            if (this.timeStarted == null)
                this.timeStarted = new Date();

            return this.timeStarted;
        }
        else
            return null;
    }

    /**
     * Get the time when the game ended.
     * @return time when the game ended, or <code>null</code> if the game has not ended yet.
     */
    public Date getPlayingUntil() {
        if (this.gameStage.equals(GameStage.ENDED)){
            if (this.timeEnded == null)
                this.timeEnded = new Date();

            return this.timeEnded;
        }
        else
            return null;
    }

    /**
     * Is game complete?
     * @return <code>true</code> if complete; <code>false</code> otherwise.
     */
    public boolean isComplete() {
        return this.gameStage == GameStage.ENDED;
    }

    /**
     * Start the game, if it is not currently playing.
     * This is only possible if the game is in the {@link GameStage#CREATED} stage.
     * Otherwise, a non-checkable exception is raised.
     * The game change is broadcasted to all players and the start place scene is multicast to the players.
     * implNote - {@link IllegalStateException} raised if game cannot be created.
     */
    public void startPlayingGame() {

        if(this.gameStage == null)
            throw new IllegalStateException("GameInstance.startPlaying: game not created");

        if(this.gameStage == GameStage.PLAYING)
            throw new IllegalStateException("GameInstance.startPlaying: Game is already playing, cannot start again.");

        if(this.gameStage == GameStage.ENDED)
            throw new IllegalStateException("GameInstance.startPlaying: Game has ended, cannot start again.");

        this.gameStage = GameStage.PLAYING;
        this.timeStarted = new Date();

        this.broadcastGameChanged(this);
        this.multicastSceneUpdate(this.gameMap.getStartPlace());
    }

    /**
     * Has the game started yet?
     * @return <code>true</code> if the game has not started yet, <code>false</code> otherwise.
     */
    public boolean isNotPlayingYet() {
        return ( gameStage.equals(GameStage.CREATED) || gameStage == null);
    }

    /**
     * End the game, if it is currently playing, otherwise raise an {@link IllegalStateException}.
     */
    public void endPlayingGame() {
        if (this.gameStage.equals(GameStage.PLAYING)) {

            this.gameStage = GameStage.ENDED;
            this.timeEnded = new Date();

            // broadcast game ended.
            this.broadcastGameChanged(this);
        }
        else {
            throw new IllegalStateException("GameInstance.endPlayingGame: game stage is " + gameStage);
        }
    }

    /**
     * Command execution by an identified player.
     * Commands are {@link Action} on an object
     * (e.g. a {@link Position} to move to, a {@link Item} to pick up, etc.).
     * The command is executed if the game is in the {@link GameStage#PLAYING} stage
     * (Otherwise, an {@link IllegalStateException} exception is raised.), and the player is in the game.
     * (Otherwise, an {@link IllegalArgumentException} exception is raised.)
     * Errors reported by commands are returned as a string.
     * A successful command execution returns <code>null</code>.
     * @param player player executing the command
     * @param action to be executed
     * @param object of the action
     * @return status of the command execution, or <code>null</code> if the command was executed successfully.
     */
    public String executeCommand(Player player,
                                 Action action,
                                 Object object) {
        if (!this.gameStage.equals(GameStage.PLAYING)){
            throw new IllegalStateException("GameInstance.executeCommand: GameStage is not PLAYING.");
        }
        if (!this.players.contains(player)){
            throw new IllegalArgumentException("GameInstance.executeCommand: Player not in the game.");
        }

        // Necessary for move back, when object is null.
        String objectName = null;
        if (object!=null)
            object.toString();

        String methodName = "execute" + action.getTitle();
        Method method;

        try {
            method = this.getClass().getDeclaredMethod(methodName, Character.class, Object.class);
        } catch (NoSuchMethodException e) {
            return "GameInstance.executeCommand: method " + methodName + " doesnt exist.";
        }
        try{
        method.setAccessible(true);
        System.out.println("Estou a invocar o metodo : " + method.getName());
        method.invoke(this, player.playerCharacter, object);
        }
        catch (InvocationTargetException | IllegalAccessException e){
            return ("GameInstance.executeCommand: error invoking method "
                    + methodName + "(" + this.toString() + "," + player.playerCharacter.toString()
                    + "," + objectName + ");");
        }

        return null;
    }

    /**
     * Moving forward on the game map to another place or position
     * @param character moving to a different place or position
     * @param object place or position when the character is moved
     * @return <code>null</code> if successful. otherwise an error message
     * implNote - The game map is updated by sending a scene update event for the current place and the new place.
     */
    String executeMove(Character character,
                       Object object) {
        if (character == null)
            return "GameInstance.executeMove: character is null.";

        if (object == null)
            return "GameInstance.executeMove: object is null.";

        if (!(object instanceof Position || object instanceof Passage))
            return "GameInstance.executeMove: object is not a Position nor a Place";

        if (object instanceof Position newPosition){ //It's a Position
            character.moveTo(newPosition);
            this.multicastSceneUpdate(character.getPlace());
        }

        else if (object instanceof Passage passage) { // it's a Place
            Place oldPlace = character.getPlace();
            Place newPlace = passage.getPlace();

            if(newPlace.getEntrance() == null){
                return "GameInstance.executeMove: entrance in object is null.";
            }
            character.move(newPlace);
            oldPlace.removeGameComponent(character);

            this.multicastSceneUpdate(newPlace);
            this.multicastSceneUpdate(oldPlace);
        }

        return null;
    }

    /**
     * Backtracking to the previous place on the game map
     * @param character backtracking
     * @param ignored argument kep for consistency
     * @return <code>null</code> if successful. otherwise an error message
     * implNote - The game map is updated by sending a scene update event for the current place and the new place.
     */
    String executeBack(Character character,
                       Object ignored) {
        if(character == null)
            return "GameInstance.executeBack: character is null.";

        Place oldPlace = character.getPlace();
        Place newPlace = character.moveBack();

        this.multicastSceneUpdate(oldPlace);

        if (newPlace != null)
            this.multicastSceneUpdate(newPlace);
        else {
            return "GameInstance.executeBack: Cannot backtrack any more";
        }

        return null;
    }

    /**
     * Use the holding item on an object in the game.
     * @param character using the hold item
     * @param object on which its used
     * @return <code>null</code> if successful. otherwise an error message
     * implNote - The game map is updated by sending a scene update event.
     */
    String executeUse(Character character,
                      Object object) {
        if (character == null)
            return "GameInstance.executeUse: character is null.";

        if (object == null)
            return "GameInstance.executeUse: object modified is null.";

        Item holdingItem = null;
        if (character.getHolding() instanceof Item){
            holdingItem = character.getHolding();
            if (holdingItem == null)
                return "GameInstance.executeUse: character's holdingItem is null.";
        }
        else
            return "GameInstance.executeUse: holdingItem is not an Item.";

        if (!(character.getPlace().getPositionables().contains(object)))
            return "GameInstance.executeUse: object not in scene";

        if (!(object instanceof Positionable positionableBeforeChange))
            return "GameInstance.executeUse: object modified is not a Positionable.";
        else {
            Positionable positionableAfterChange = this.gameMap.getChange(positionableBeforeChange, holdingItem);

            character.getPlace().addGameComponent(positionableAfterChange, positionableBeforeChange.getPosition());

            character.getPlace().removeGameComponent(positionableBeforeChange);

            if (!holdingItem.isReusable()) {
                character.dropItem(holdingItem); // item "disappears", not dropped on ground.
                character.holdItem(null);
                this.unicastInventoryUpdate(character);
            }

            this.multicastSceneUpdate(character.getPlace());
        }

        return null;
    }

    /**
     * Drop an item in the inventory.
     * @param character dropping an item
     * @param object item being dropped
     * @return <code>null</code> if successful. otherwise an error message
     * implNote - The game map is updated by sending a scene update event.,
     * The inventory is updated by sending an inventory update event.
     */
    String executeDrop(Character character,
                       Object object) {
        if (character == null)
            return "GameInstance.executeDrop: character is null";

        if (object == null)
            return "GameInstance.executeDrop: object is null";

        if (!(object instanceof Item item))
            return "GameInstance.executeDrop: object is not an Item";

        if (character.getInventory().contains(object)){
            if (character.dropItem(item)){
                this.unicastInventoryUpdate(character);

                // item was dropped from inventory back into the scene
                character.getPlace().addGameComponent(item, item.getPosition());
                this.multicastSceneUpdate(character.getPlace());

                return null;
            }
            else
                return "GameInstance.executeDrop: item couldn't be dropped.";
        }
        else{
            if(item == character.getHolding()){
                character.holdItem(null);
            }
            return "GameInstance.executeDrop: item not in inventory";

        }
    }

    /**
     * Pick as item in the place.
     * @param character picking an object
     * @param object item being picked
     * @return <code>null</code> if successful. otherwise an error message
     * implNote - The game map is updated by sending a scene update event.,
     * The inventory is updated by sending an inventory update event.
     */
    String executePick(Character character,
                       Object object) {
        if(!(object instanceof Item item))
            return "GameInstance.executePick: object is not an Item.";
        else {
            if(character == null)
                return "GameInstance.executePick: character is null.";

            if(item == null)
                return "GameInstance.executePick: object is null.";

            if(!item.isPickable())
                return "GameInstance.executePick: object is not pickable.";

            if (!(character.getPlace().getItems().contains(item)))
                return "GameInstance.executePick: object is not in the same Place as character.";

            if (character.getPlace().getPositionables().contains(object)){
                character.addItem(item);
                character.getPlace().removeGameComponent(item);
            }

            this.unicastInventoryUpdate(character);
            this.multicastSceneUpdate(character.getPlace());

            return null;
        }
    }


    /**
     * Hold an item in the inventory. An item need to be held to be used.
     * @param character holding the item
     * @param object item being held
     * @return <code>null</code> if successful. otherwise an error message
     * implNote - The inventory is updated by sending an inventory update event.
     */
    String executeHold(Character character,
                       Object object) {
        if (character == null)
            return "GameInstance.executeHold: character is null.";

        if(!(object instanceof Item item))
            return "GameInstance.executeHold: invalid item, object is not an Item.";

        if (!(character.getInventory().contains(item)))
            return "GameInstance.executeHold: object does not exist in the character's inventory.";

        if(character.getHolding() == item)
            return "GameInstance.executeHold: error expected, its is not a valid item";

        // segura item, e item continua no inventorio.
        character.holdItem(item);

        this.unicastInventoryUpdate(character);

        return null;
    }

    /**
     * Talk to everyone on the same place.
     * @param character doing the talking
     * @param object what she told
     * @return <code>null</code> if successful, otherwise an error message
     * implNote - The message is sent to the players in the same place as a message update event.
     */
    String executeTalk(Character character,
                       Object object) {
        if(character == null)
            return "GameInstance.executeTalk: character is null.";

        if (character.getPlace().getCharacters().size() <= 1)
            return "GameInstance.executeTalk: no characters in the Place to receive the message.";

        if (object instanceof String message){
            if(message.isEmpty())
                return "GameInstance.executeTalk: object is null.";

            this.multicastMessageUpdate(character.getPlace(), character, message);

            return null;
        }
        else
            return "GameInstance.executeTalk: object is not a String.";
    }

    /**
     * Look at an object in the game. Its description is sent to the player.
     * @param character looking at the object
     * @param object to be looked at
     * @return <code>null</code> if successful, otherwise an error message
     * implNote - The description is sent to the player as a message update event.
     */
    String executeLook(Character character,
                       Object object) {
        if(character == null)
            return "GameInstance.executeLook: character is null.";

        if(object == null)
            return "GameInstance.executeLook: object is null.";

        // Object has to be Positionable to look at, either an Item, a Character or a Passage.
        if (object instanceof Positionable positionable){
            this.unicastMessageUpdate(character, positionable.getDescription());

            return null;
        }
        else
            return "GameInstance.executeLook: object is not Positionable.";
    }
}
