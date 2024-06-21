package rea.components;

import java.util.Objects;

/**
 * <p>An item in the game, such as a key, a sword, a potion, etc.
 * Some items can be picked up and used, to open a door, cast a spell, etc.
 * By default, an item can be picked up, but it cannot be reused (can be used only once).
 * This can be changed using the methods {@link #setPickable(boolean)} and {@link #setReusable(boolean)}.
 * The visual representation of the item is a {@link Visual}.</p>
 * implNote - Corresponds to a <b>Leaf</b> in the <b>Composite</b> design pattern.
 */
public class Item
        extends Positionable
        implements Element {

    private boolean isPickable;
    private boolean isReusable;

    /**
     * Create an item.
     * @param visual visual representation of the item
     * @param description description of the item
     */
    public Item(Visual visual,
                String description){

        super(visual, description);
        this.setPickable(true);
        this.setReusable(false);
    }

    /**
     * Returns true if the item can be picked up. By default, an item can be picked up.
     * @return <code>true</code> if the item can be picked up, <code>false</code> otherwise.
     */
    public boolean isPickable(){
        return this.isPickable;
    }

    /**
     * Set whether the item can be picked up. By default, an item can be picked up.
     * @param pickable <code>true</code> if the item can be picked up, <code>false</code> otherwise.
     */
    public void setPickable(boolean pickable){
        this.isPickable = pickable;
    }

    /**
     * Returns true if the item can be reused. By default, an item cannot be reused.
     * Non-reusable items are removed from the inventory when used.
     * @return <code>true</code> if the item can be reused up, <code>false</code> otherwise.
     */
    public boolean isReusable() {
        return this.isReusable;
    }

    /**
     * Set whether the item can be reused. By default, an item cannot be reused.
     * Non-reusable items are removed from the inventory when used.
     * @param reusable <code>true</code> if the item can be reused up, <code>false</code> otherwise.
     */
    public void setReusable(boolean reusable) {
        this.isReusable = reusable;
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;

        if (o instanceof Item item){
            return Objects.equals(this.getPosition(), item.getPosition())
                    && Objects.equals(this.getDescription(), item.getDescription())
                    && Objects.equals(this.getVisual(), item.getVisual())
                    && Objects.equals(this.isPickable(), item.isPickable())
                    && Objects.equals(this.isReusable(), item.isReusable());
        }
        else
            return false;
    }

    @Override
    public int hashCode() {
        // Objects.hashCode(this);
        return Objects.hash(this.isPickable, this.isReusable,
                this.getPosition(), this.getDescription(), this.getVisual());
    }

    /**
     * Accept a visitor.
     * @param visitor the visitor to accept
     */
    public void accept(Visitor visitor){
        visitor.visit(this);
    }

}
