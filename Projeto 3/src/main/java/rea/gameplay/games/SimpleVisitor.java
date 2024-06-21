package rea.gameplay.games;

import rea.components.Character;
import rea.components.*;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>A simples visitor that counts elements of each kind in the game map, namely:<ul>
 *     <li>{@link Place}</li>
 *     <li>{@link Item}</li>
 *     <li>{@link Passage}</li>
 *     <li>{@link Character}</li></ul>
 *
 * <p>This visitor can be used to extend specialized visitors,
 * to collect other information needed to check if the games is completed,
 * by going through all the elements in the map.
 * The extended visitor may reuse methods from this class that
 * propagate <code>Element.accept(Visitor)</code> to descendents.</p>
 * <p>This class is part of the <b>Visitor</b> pattern - a concrete visitor.</p>
 */
public class SimpleVisitor
        implements Visitor {

    /**
     * Set of characters
     */
    protected static Set<Character> characters = null;
    /**
     * Set of places
     */
    protected static Set<Place> places = null;
    /**
     * Set of items
     */
    protected static Set<Item> items = null;
    /**
     * Set of passages
     */
    protected static Set<Passage> passages = null;

    /**
     * Create an instance of this class. No particular initializations are performed.
     */
    public SimpleVisitor() {
        characters = new HashSet<>();
        places = new HashSet<>();
        items = new HashSet<>();
        passages = new HashSet<>();
    }

    /**
     * The set of players in the map. This value will change as players are added and removed from the map.
     * @return the set of players in the map
     */
    public Set<Character> getCharacters() {
        return characters;
    }

    /**
     * The set of items in the map. This will change as items are collected and remove from the map.
     * @return set of items in the map
     */
    public Set<Item> getItems() {
        return items;
    }

    /**
     * Get the first item containing given text in the description
     * @param text to lookup
     * @return item or null if not found
     */
    Item getItemWith(String text) {
        return this.getItems()
                .stream()
                .filter(item -> item.getDescription().toLowerCase().contains(text))
                .findFirst()
                .orElse(null);
    }

    /**
     * The set of passages in the map. This value will change as certain items are used other items.
     * For instance, if a key is used to open a door, the door will be changed in a passage to another place
     * @return set of passages in the map
     */
    public Set<Passage> getPassages() {
        return passages;
    }


    /**
     * Get the first passage containing given text in the description
     * @param text to lookup
     * @return item or null if not found
     */
    Passage getPassageWith(String text) {
        return this.getPassages()
                .stream()
                .filter(passage -> passage.getDescription().toLowerCase().contains(text))
                .findFirst()
                .orElse(null);
    }


    /**
     * The set places in the map. This value will change as places are added and removed from the map.
     * @return set of places in the map
     */
    public Set<Place> getPlaces(){
        return places;
    }

    public void visit(Character character) {
        if (!this.getCharacters().contains(character)){
            characters.add(character);
        }
    }

    public void visit(Place place) {
        if (!this.getPlaces().contains(place)) {
            places.add(place);
        }
    }

    public void visit(Item item) {
        if (!this.getItems().contains(item)) {
            items.add(item);
        }
    }

    public void visit(Passage passage) {
        if (!this.getPassages().contains(passage)) {
            passages.add(passage);
        }
    }
}
