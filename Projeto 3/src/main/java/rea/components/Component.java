package rea.components;

/**
 * Abstract class common to all classes implementing the game map structure.
 * Defines methods required by those classes and provides general methods.
 * implNote Corresponds to the <b>Component</b> in the <b>Composite</b> design pattern.
 */
public abstract class Component
        implements Element {

    /**
     * The visual representation of the component.
     */
    Visual componentVisual;

    /**
     * The description of the component.
     */
    String componentDescription;

    /**
     * Create a component.
     * @param image visual representation of the component
     * @param description description of the component
     */
    public Component(Visual image,
                     String description) {
        this.componentDescription = description;
        this.componentVisual = image;
    }

    /**
     * Get the visual representation of the component.
     * @return the visual representation of the component
     */
    public Visual getVisual() {
        return this.componentVisual;
    }

    /**
     * Get the description of the component.
     * @return the description of the component
     */
    public String getDescription() {
        return this.componentDescription;
    }

    /**
     * Show component as a string using its description.
     *
     * @return the description of the component
     */
    @Override
    public String toString() {
        return this.getDescription();
    }

}
