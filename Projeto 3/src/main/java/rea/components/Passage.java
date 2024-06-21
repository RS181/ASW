package rea.components;

/**
 * A way to a place. For instance, a door to a room, or a path to a forest.
 * implNote - corresponds to a <b>Leaf</b> in the <b>Composite</b> design pattern.
 */
public class Passage
        extends Positionable
        implements Element {

    Place passagePlace;

    /**
     * Create a passage to a place.
     * @param visual showing the passage
     * @param description of the passage
     * @param place to which the passage leads
     */
    public Passage(Visual visual,
                   String description,
                   Place place){
        super(visual,description);
        this.passagePlace = place;
    }

    /**
     * The place to which this passage leads.
     * @return the place to which this passage leads
     */
    public Place getPlace(){
        return this.passagePlace;
    }

    /**
     * Accept a visitor.
     * @param visitor the visitor to accept
     */
    public void accept(Visitor visitor){
        visitor.visit(this);
    }
}
