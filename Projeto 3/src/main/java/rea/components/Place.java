package rea.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>A place in the game. A place can contain connections to other places and
 *  game components such as characters, items, etc.</p>
 * implNote - Corresponds to a <b>Container</b> in the <b>Composite</b> design pattern.
 */
public class Place
        extends Component
        implements Element {

    /**
     * Position where the character enters this place.
     */
    final Position placeEntrance;

    /**
     * The positionable components in this place.
     */
    List<Positionable> placePositionables;

    /**
     * Create a place.
     * @param background showing this place.
     * @param description of this place.
     * @param entrance position where the characters enter this place.
     */
    public Place(Visual background, String description, Position entrance) {
        super(background, description);
        this.placeEntrance = entrance;
        this.placePositionables = new ArrayList<>();
    }

    /**
     * Accept a visitor.
     * @param visitor the visitor to accept
     */
    @Override
    public void accept(Visitor visitor){

        visitor.visit(this);

        for(Positionable positionable : this.getPositionables()){
            positionable.accept(visitor);

            // Recursion to visit the destination Place in a Passage
            if ((positionable instanceof Passage passage) && (passage.getPlace()!= null)){
                passage.getPlace().accept(visitor);
            }
        }
    }

    /**
     * Add a positionable component, such as {@link Item} or {@link Character} to this place.
     * It returns the place itself to allow chaining.
     * @param positionable component to add.
     * @param position where to add the component.
     * @return the place itself.
     */
    public Place addGameComponent(Positionable positionable, Position position) {
        positionable.moveTo(position);

        if (!(this.placePositionables.contains(positionable)))
            this.placePositionables.add(positionable);

        return this;
    }

    /**
     * Get the characters in this place. Convenience methods to get the positionables that are players.
     * @return the set of players in this place.
     */
    public Set<Character> getCharacters() {
        Set<Character> characterSet = new HashSet<>();

        for(Positionable positionable : placePositionables)
            if (positionable instanceof Character characterToAdd)
                characterSet.add(characterToAdd);

        return characterSet;
    }

    /**
     * The entrance to this place.
     * @return the position where the character enters this place.
     */
    public Position getEntrance() {
        return this.placeEntrance;
    }

    /**
     * Get the items in this place. Convenience methods to get the positionables that are item.
     * @return the set of items in this place.
     */
    public Set<Item> getItems() {
        Set<Item> itemSet = new HashSet<>();

        for(Positionable item : this.placePositionables)
            if (item instanceof Item itemToAdd)
                itemSet.add(itemToAdd);

        return itemSet;
    }

    /**
     * Get the list of game positionable components in this place.
     * @return the list of positionable components.
     */
    public List<Positionable> getPositionables() {
        return this.placePositionables;
    }

    /**
     * Remove a positionable component, such as {@link Item} or {@link Character} from this place.
     * It returns <code>true</code> if the component was removed, <code>false</code> otherwise.
     * @param positionable component to remove.
     * @return <code>true</code> if the component was removed, <code>false</code> otherwise.
     */
    public boolean removeGameComponent(Positionable positionable) {
        return placePositionables.remove(positionable);
    }

}
