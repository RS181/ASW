package rea.components;

/**
 * A positionable object in the game. The object has a {@link Position} in a {@link Place}.
 * When created, the object lacks a specific position in a place.
 * @author José Paulo Leal <code>jpleal@fc.up.pt</code>
 */
public abstract class Positionable
        extends Component
        implements Element {

    private Position positionablePosition;

    /**
     * Create a positionable object.
     * @param visual visual representation of the object
     * @param description description of the object
     */
    public Positionable(Visual visual,
                        String description){
        super(visual, description);
        this.positionablePosition = null;

    }

    /**
     * Move point to a new location.
     * @param position new position
     */
    public void moveTo(Position position){
        this.positionablePosition = position;
    }

    /**
     * The position of the object in the place.
     * @return the position of the object
     */
    public Position getPosition() {
        return this.positionablePosition;
    }

}
