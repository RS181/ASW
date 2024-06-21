package rea.components;

/**
 * <p>The <b>Visitor</b> interface.</p>
 * implNote abstract component of the design pattern with the same name.
 * @author José Paulo Leal <code>jpleal@fc.up.pt</code>
 */
public interface Visitor {

    /**
     * Do a visit to a character in the composite structure
     * @param character to be visited
     */
    void visit (Character character);

    /**
     * Do a visit to a place in the composite structure
     * @param place to be visited
     */
    void visit(Place place);

    /**
     * Do a visit to an item in the composite structure
     * @param item to be visited
     */
    void visit(Item item);

    /**
     * Do a visit to a passage in the composite structure
     * @param passage to be visited
     */
    void visit(Passage passage);
}
