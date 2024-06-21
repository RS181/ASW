package rea.gameplay.games;

import rea.components.*;
import rea.gameplay.Gameplay;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>A simple game where the player has to look for a treasure hidden inside a house with a closed door.
 * The player has to find a key to open the door and get the treasure.</p>
 */
public class TreasureHunt
        implements Gameplay {

    /**
     * LAWN_IMAGE file path string
     */
    protected static final String LAWN_IMAGE = "images/lawn.jpg";
    /**
     * EMPTY_ROOM_IMAGE file path string
     */
    protected static final String EMPTY_ROOM_IMAGE = "images/empty_room.jpg";
    /**
     * KEY_IMAGE file path string
     */
    protected static final String KEY_IMAGE = "images/key.png";
    /**
     * TREASURE_IMAGE file path string
     */
    protected static final String TREASURE_IMAGE = "images/treasure.png";
    /**
     * HOUSE_CLOSE_DOOR_IMAGE file path string
     */
    protected static final String HOUSE_CLOSE_DOOR_IMAGE = "images/house_close_door.png";
    /**
     * string for path of house open door image
     */
    protected static final String HOUSE_CLOSE_OPEN_IMAGE = "images/house_open_door.png";
    /**
     * The background width
     */
    static final int BACKGROUND_WIDTH = 800;
    /**
     * The background height
     */
    static final int BACKGROUND_HEIGHT = 600;
    /**
     * The visual representation of a lawn used a background
     */
    static final Visual LAWN_VISUAL = new Visual(LAWN_IMAGE, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);

    /**
     * The visual representation of an empty room used as a background
     */
    static final Visual EMPTY_ROOM_VISUAL = new Visual(EMPTY_ROOM_IMAGE, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
    /**
     * The width of the key visual
     */
    static final int KEY_WIDTH = 70;
    /**
     * The height of the key visual
     */
    static final int KEY_HEIGHT = 50;

    /**
     * The visual representation of a key
     */
    static final Visual KEY_VISUAL = new Visual(KEY_IMAGE, KEY_WIDTH, KEY_HEIGHT);
    /**
     * The width of the treasure visual
     */
    static final int TREASURE_WIDTH = 300;
    /**
     * The height of the treasure visual
     */
    static final int TREASURE_HEIGHT = 200;
    /**
     * The visual representation of the treasure
     */
    static final Visual TREASURE_VISUAL = new Visual(TREASURE_IMAGE, TREASURE_WIDTH, TREASURE_HEIGHT);
    /**
     * The initial position of the character's x coordinate
     */
    static int CHARACTER_X;
    /**
     * The initial position of the character's y coordinate
     */
    static int CHARACTER_Y;
    /**
     * The width of the house visual
     */
    static int HOUSE_WIDTH = 200;
    /**
     * The height of the house visual
     */
    static int HOUSE_HEIGHT = 200;

    /**
     * The visual representation of house with a closed door
     */
    static final Visual HOUSE_CLOSE_DOOR_VISUAL = new Visual(HOUSE_CLOSE_DOOR_IMAGE, HOUSE_WIDTH, HOUSE_HEIGHT);

    /**
     * The visual representation of a house with an open door
     */
    static final Visual HOUSE_OPEN_DOOR_VISUAL = new Visual (HOUSE_CLOSE_OPEN_IMAGE, HOUSE_WIDTH, HOUSE_HEIGHT);

    private GameMap gameMap;
    SimpleVisitor v;

    /**
     * Create a new instance of the game
     */
    public TreasureHunt() {
        gameMap = null;
        v = new SimpleVisitor();
    }

    public String getName() {
        return "Treasure Hunt";
    }

    public String getDescription() {
        return "Find the trasure hidden inside the house with a closed door.";
    }

    public GameMap makeGameMap() {

        // Place, Lawn (the start place, outside house)
        Position entrancePosition = new Position(CHARACTER_X,CHARACTER_Y);
        Place startPlace = new Place(LAWN_VISUAL, "Start place", entrancePosition);

        // Item, house closed door (on the Lawn)
        Item closedDoor = new Item(HOUSE_CLOSE_DOOR_VISUAL, "house with closed door");
        closedDoor.setPickable(false);
        startPlace.addGameComponent(closedDoor, new Position(0, 0));

        // Item, key (on the Lawn)
        Item key = new Item(KEY_VISUAL, "key");
        key.setPickable(true);
        startPlace.addGameComponent(key, new Position(0, 0));

        // Place, Empty room (inside the house)
        Place insideHouse = new Place(EMPTY_ROOM_VISUAL, "inside house", new Position(0,0));

        // Passage, open door is a passage between Lawn and Empty room.
        Passage openDoor = new Passage(HOUSE_OPEN_DOOR_VISUAL, "house with open door", insideHouse);

        // Item, Treasure (in empty room)
        Item treasure = new Item(TREASURE_VISUAL, "treasure");
        treasure.setPickable(true);
        insideHouse.addGameComponent(treasure, new Position(0, 0));

        gameMap = new GameMap(startPlace);

        gameMap.defineChange(closedDoor, key, openDoor);

        gameMap.visitMap(v);

        return gameMap;
    }

    /**
     * The avatars available for this game: cartoon avatars.
     * @return set of cartoon avatars
     * implNote - The avatars are the enum constants of the CartoonAvatar class
     */
    public Set<Avatar> getAvatars() {
        return new HashSet<>(EnumSet.allOf(CartoonAvatar.class));
    }

    /**
     * Can have up to 2 players
     * @return 2
     */
    public int getMaxPlayers() {
        return 2;
    }

    /**
     * At least 1 player is required
     * @return 1
     */
    public int getMinPlayers() {
        return 1;
    }

    /**
     * Game ends when there is a passage (house with open door)
     * and there are no items left (treasure was collected).
     * @param gameMap for this game instance
     * @return <code>true</code> if game has ended, <code>false</code> otherwise.
     * implNote - uses the {@link SimpleVisitor} to check if there,
     * and if more than the initial place where visited.
     */
    public boolean gamedEnded(GameMap gameMap) {

        // Used as end game condition
        boolean treasureCollected;
        boolean thereIsPassage;

        gameMap.visitMap(v);

        // Checks if are there any "treasure" Item
        treasureCollected = v.getItemWith("treasure") == null;

        // Checks if are there any "open door" Passage
        thereIsPassage = v.getPassageWith("open door") != null;

        System.out.println("* There is a Passage? " + thereIsPassage);
        System.out.println("* Was the treasure collected? " + treasureCollected);

        return thereIsPassage && treasureCollected;
    }
}
