package rea.components;

/**
 * The <b>Element</b> interface.
 * This type ust be added to the <b>Component</b> of the <b>Composite</b>
 * to ensure that all types of the structure implement it.
 * implNote abstract component of the <b>Visitor</b> design pattern with the same name.
 */
public interface Element {

    /**
     * Accept a visitor to operate on a node of the composite structure
     * @param visitor to the node
     */
    void accept (Visitor visitor);
}
