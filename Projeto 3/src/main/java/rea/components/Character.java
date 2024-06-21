package rea.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * A character in the game. The character has a name, an avatar, an inventory of items, and a path of visited places.
 * The character can move to a place, move back to the previous place, and hold an item.
 * implNote - Corresponds to a <b>Leaf</b> in the <b>Composite</b> design pattern.
 */
public class Character
        extends Positionable
        implements Element {

    /**
     * The name of the character
     */
    String characterName;

    /**
     * The inventory of items
     */
    List<Item> inventoryItems;

    /**
     * The item being held
     */
    Item holdingItem;

    /**
     * The path of visited places.
     * Current place is the FIRST place (top) in the Stack
     */
    Stack<Place> visitedPlaces;

    /**
     * Create a character.
     * @param name the name of the character
     * @param avatar the avatar of the character
     */
    public Character(String name,
                     Avatar avatar) {
        super( avatar.getAvatarVisual(), "Character");
        this.characterName = name;
        this.holdingItem = null;
        this.inventoryItems = new ArrayList<>();
        this.visitedPlaces = new Stack<>();
        this.visitedPlaces.push(null);
    }

    /**
     * Get the name of the character.
     * @return the name of the character.
     */
    public String getName() {
        return this.characterName;
    }

    /**
     * Get the place where the character is currently in.
     * @return the place where the character is currently in, or <code>null</code> if
     *  the character is not in any place.
     */
    public Place getPlace() {
        return visitedPlaces.peek(); // peek gets Place on top of stack
    }

    /**
     * Move to the given place. In that place, the character is positioned at the entrance.
     * @param place the place to move to
     */
    public void move(Place place) {
        if (place == null)
            throw new RuntimeException("Character.move: place is null!");

        if (place.getEntrance() == null)
            throw new RuntimeException("Character.move: entrance position for Place is null.");

        if(this.getPlace() == place)
            throw new RuntimeException("Character.move: character is moving to same place.");

        this.visitedPlaces.push(place);

        //this.moveTo(place.getEntrance());
        this.moveTo(this.getPlace().getEntrance());

        if (place.placePositionables.contains(this))
            throw new RuntimeException("Character.move: character is already a positionable game component in place.");

        // add this character as positionable game component in place.
        this.getPlace().addGameComponent(this, this.getPlace().getEntrance());

    }

    /**
     * Move back to the previous place, if possible.
     * Cannot move back if the character has not moved to any place yet, apart from the starting place.
     * @return the place where the character moves to, or <code>null</code> if the character cannot go back.
     */
    public Place moveBack() {

        // visited places doesnt exist
        if (this.visitedPlaces == null)
            return null;

        if (visitedPlaces.size() <= 2 || visitedPlaces.peek() == null)
            return null;

        if (visitedPlaces.isEmpty() || visitedPlaces.get(1) == null)
            return null;

        Place oldPlace = visitedPlaces.pop(); // Removes old place from Stack

        Place newPlace = visitedPlaces.peek();

        oldPlace.removeGameComponent(this); // Removes Character for old place

        newPlace.addGameComponent(this, newPlace.getEntrance());

        this.moveTo(newPlace.getEntrance()); // Moves character to entrance of new place

        // returns the place where the character moves to
        return newPlace;
    }

    /**
     * Get the inventory, the items the character is carrying.
     * @return the inventory
     */
    public List<Item> getInventory() {
        return this.inventoryItems;
    }

    /**
     * Add an item to the inventory.
     * @param item the item to add
     */
    public void addItem(Item item) {
        this.inventoryItems.add(item);
    }

    /**
     * Drop an item from the inventory.
     * @param item the item to drop
     * @return <code>true</code> if the item was dropped, <code>false</code> otherwise
     */
    public boolean dropItem(Item item) {
        return this.inventoryItems.remove(item);
    }

    /**
     * Hold the given item. A single item can be held by a character at a time.
     * An item must be held to be used. The argument can be null to stop holding an item.
     * @param item to hold
     */
    public void holdItem(Item item) {
        this.holdingItem = item;
    }

    /**
     * Get the item being held.
     * @return the item being held, or <code>null</code> if no item is being held.
     */
    public Item getHolding() {
        return this.holdingItem;
    }

    /**
     * Accept a visitor.
     * @param visitor the visitor to accept
     */
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
