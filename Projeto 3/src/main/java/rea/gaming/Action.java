package rea.gaming;

import java.io.Serializable;
import java.lang.constant.Constable;

/**
 * The actions that the player can do in the game.
 */
public enum Action
        implements Serializable, Comparable<Action>, Constable {

    /**
     * Move to another position in the same place, or to a different place.
     */
    MOVE,

    /**
     * Move to the previous place.
     */
    BACK,

    /**
     * Pick an item from the scene and save it in the inventory.
     */
    PICK,

    /**
     * Drop an item from the inventory to the scene.
     */
    DROP,

    /**
     * Hold an item, either from the inventory or from the scene, to then use it.
     */
    HOLD,

    /**
     * Use the item your holding on the selected object in the scene.
     */
    USE,

    /**
     * Talk to every character in the scene.
     */
    TALK,

    /**
     * Look at the selected object in the scene to receive its description.
     */
    LOOK;

    /**
     * Get the action name as a title.
     * The first letter is capitalized and the following letters are in lower case.
     * (e.g. "Move" for {@link #MOVE}).
     * @return the title of the action.
     */
    public String getTitle() {
        // TODO
        return (
                this.name().substring(0, 1).toUpperCase()
                + this.name().substring(1).toLowerCase()
               );
    }

}
