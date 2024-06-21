package rea.events;
import rea.components.Item;

import java.util.List;

/**
 * An event that is sent to the client to update the player's inventory.
 * The event contains a list of items and the item the player is holding.
 */
public class InventoryUpdateEvent
        implements UpdateEvent {

    private List<Item> items;
    private Item holding;


    /**
     * Create an inventory update event with a list of items and the item the player is holding
     * @param items the player's inventory
     * @param holding the item the player is holding
     */
    public InventoryUpdateEvent(List<Item> items,
                                Item holding) {
        this.items = items;
        this.holding = holding;
    }

    /**
     * Get the player's inventory
     * @return a list of items in the player's inventory
     */
    public List<Item> getInventory() {
        return this.items;
    }

    /**
     * Get the item the player is holding
     * @return the item the player is holding
     */
    public Item getHolding(){
        return this.holding;
    }

}
