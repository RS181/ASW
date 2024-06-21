package rea.gameplay.games;

import rea.components.*;
import rea.gameplay.Gameplay;

import java.util.*;

/**
 * A simple game where the player has to collect all the Easter eggs in the lawn.
 */
public class EasterEggRace
        implements Gameplay {

    /**
     * string with path for lawn image
     */
    protected static final String LAWN_IMAGE = "images/background.png";
    /**
     * string with paths for eggs
     */
    protected static final String EGG_PATHNAMES[] = {"images/easter-egg-1.png", "images/easter-egg-2.png", "images/easter-egg-3.png"};
    /**
     * The background width
     */
    static final int BACKGROUND_WIDTH = 800;

    /**
     * The background height
     */
    static final int BACKGROUND_HEIGHT = 600;

    /**
     * The visual representation of the lawn used as a background
     */
    static final Visual LAWN_VISUAL = new Visual(LAWN_IMAGE, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);

    /**
     * The width of an Easter egg
     */
    static final int EGG_WIDTH = 50;

    /**
     * The height of an Easter egg
     */
    static final int EGG_HEIGHT = 70;

    /**
     * The visual representation of some Easter eggs
     */
    static final Visual[] EGG_VISUAL = {
            new Visual(EGG_PATHNAMES[0], EGG_WIDTH, EGG_HEIGHT),
            new Visual(EGG_PATHNAMES[1], EGG_WIDTH, EGG_HEIGHT),
            new Visual(EGG_PATHNAMES[2], EGG_WIDTH, EGG_HEIGHT)
    }; // PENSAR EM UMA FORMA MAIS ELEGANTE DE FAZER ESSE CODIGO!!


    /**
     * Create a new instance of the game
     */
    public EasterEggRace() {
    }


    public String getName() {
        return "Easter Egg Race";
    }


    public String getDescription() {
        return "Collect all the eggs in the lawn";
    }


    public GameMap makeGameMap() {

        Position position = new Position(0,0);
        Place startPlace = new Place(LAWN_VISUAL, "lawn", position); // entrada em Null na posicao.

        Stack<Item> easterEggs = new Stack<>();
        int i=0;
        for (Visual easterEggVisual : EGG_VISUAL){
            easterEggs.add(new Item(easterEggVisual, "egg " + (++i)));
            Random random = new Random();

            Position eggPosition = new Position(0,0);
            eggPosition.setX(random.nextInt(EGG_WIDTH));
            eggPosition.setY(random.nextInt(EGG_HEIGHT));

            startPlace.addGameComponent(easterEggs.peek(), eggPosition);
        }

        GameMap gameMap = new GameMap(startPlace);

        gameMap.visitMap(new SimpleVisitor());

        return gameMap;
    }

    /**
     * The avatars available for this game: cartoon avatars.
     * @return set of cartoon avatars
     * implNote - The avatars are the enum constants of the CartoonAvatar class
     */
    public Set<Avatar> getAvatars(){
        return new HashSet<>(EnumSet.allOf(CartoonAvatar.class));
    }

    /**
     * Only a single player
     * @return 1
     */public int getMaxPlayers() {
        return 1;
    }

    /**
     * At least 1 player is required
     * @return 1
     */
    public int getMinPlayers() {
        return 1;
    }

    /**
     * Game ends when there are no more eggs to collect
     * @param gameMap for this game instance
     * @return <code>true</code> if the game has ended, <code>false</code> otherwise
     */
    public boolean gamedEnded(GameMap gameMap) {
        SimpleVisitor visitor = new SimpleVisitor();
        gameMap.visitMap(visitor);

        return (visitor.getItemWith("egg") == null);

    }
}
