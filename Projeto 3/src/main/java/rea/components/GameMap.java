package rea.components;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>The game map. It is a composite of places, items, characters and passages.
 * The game map is a directed acyclic graph (DAG) where the nodes are places and arcs are passages.
 * Since this DAG has a single root, it can be assigned by its node, the start place.</p>
 * <p>During the game, the game map may change.
 * For example, a place may have closed door (an unpickable item) and
 * in a different place there is a key (a pickable but non-reusable item).
 * The player may pick the key, move to the place with the closed door,
 * use the key to open the door, and then become an open door (a passage).
 * The DAG representing the game map may change as a result of these actions.</p>
 * <p>The game map can be transversed using a visitor.</p>
 * @author José Paulo Leal <code>jpleal@fc.up.pt</code>
 * @see Visitor
 */
public class GameMap
        extends DAG {

    /**
     * The start place of the game map.
     */
    Place startPlace;

    /**
     * Changes that may be performed on positionable objects in the game.
     */
    Map<Positionable, Map<Item,Positionable>> changes = new HashMap<>();

    /**
     * Create a game map with a start place.
     * Since the start place is the root of the composite,
     * this will set the entire game map.
     * @param startPlace the start place of the game map
     */
    public GameMap(Place startPlace) {
        this.setStartPlace(startPlace);
        this.addPlace(this.startPlace);
        //this.dag.addPlace(this.startPlace);
    }

    /**
     * Get the start place of the game map.
     * Since the start place is the root of the composite,
     * this will return the entire game map.
     * @return the start place of the game map
     */
    public Place getStartPlace() {
        return this.startPlace;
    }

    /**
     * Set the start place of the game map.
     * Since the start place is the root of the composite,
     * this will change the entire game map.
     * @param startPlace for the game map
     */
    public void setStartPlace(Place startPlace) {
        try{
            this.startPlace = startPlace;
            //this.startPlace.setComponentDescription("Start place");
            this.addPlace(startPlace);
        }
        catch (NullPointerException e){
            System.out.print("GameMap.setStartPlace: caught the NullPointerException");
        }
    }

    /**
     * Define a change that may be performed on a positionable object in the game.
     * @param modifiable the positionable object that may be modified
     * @param tool the item that may be used to modify the positionable object
     * @param modified the modified positionable object
     */
    public void defineChange(Positionable modifiable,
                             Item tool,
                             Positionable modified) {

        //if (modified == null)
        //    throw new NoSuchElementException("defineChange: Positionable modified does not exist");

        // modifiable exists in changes Map
        if (this.changes.containsKey(modifiable)){
            this.changes.get(modifiable).put(tool, modified);
        }

        // modifiable does not exist in changes Map
        else{
            Map<Item, Positionable> valueChanges = new HashMap<>();
            valueChanges.put(tool,modified);

            this.changes.put(modifiable,valueChanges);
        }

    }

    /**
     * Checks if change exists
     *
     * @param modifiable the positionable object that may be modified
     * @return <code>true</code> if modifiable is modifiable.
     */
    public Boolean isChangeable(Positionable modifiable){
        return this.changes.get(modifiable).containsKey(modifiable);
    }

    public Set<Item> getChanges(Positionable modifiable){
        return this.changes.get(modifiable).keySet();
    }

    /**
     * Get the change that may be performed on a positionable object in the game.
     * @param modifiable the positionable object that may be modified
     * @param tool the item that may be used to modify the positionable object
     * @return the modified positionable object, or <code>null</code> if no change is defined
     */
    public Positionable getChange(Positionable modifiable,
                                  Item tool) {
        // No changes were defined at all
        if (this.changes.isEmpty() ||  this.changes == null)
            return null;

        // the object to be modified doesn't
        if (!this.changes.containsKey(modifiable))
            return null;

        // tool to modify doesn't exist
        if (tool == null)
            return null;

        Positionable newPositionable = this.changes.get(modifiable).getOrDefault(tool, null);

        if (newPositionable != null) {

            if (newPositionable instanceof Passage newPassage) {
                this.addPlace(newPassage.getPlace());

                for (Place place : this.getPlaces()) {
                    if (place.getPositionables().contains(modifiable)) {

                        place.addGameComponent(newPassage, modifiable.getPosition());

                        this.addPassage(place, newPassage);
                        place.removeGameComponent(modifiable);
                    }
                }
            } else {
                for (Place place : this.getPlaces()) {
                    if (place.getPositionables().contains(modifiable)) {
                        place.addGameComponent(newPositionable, modifiable.getPosition());
                        place.removeGameComponent(modifiable);
                    }
                }
            }
        }
        return newPositionable;
    }

    /**
     * Visit the map from the start place.
     * @param visitor the visitor to accept
     */
    public void visitMap(Visitor visitor) {
        // visits start Place in the GameMap, which then visits each  positionable in it.
        this.startPlace.accept(visitor);

    }
}
