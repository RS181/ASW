package rea.events;

import rea.components.Positionable;
import rea.components.Visual;

import java.util.List;

/**
 * An event that updates the scene, i.e. what is shown in a particular place,
 * including the background and the positionables.
 */
public class SceneUpdateEvent
        implements UpdateEvent {

    private Visual background;
    private List<Positionable> positionables;

    /**
     * Create a scene update event with a background and a set of positionables
     * @param background to show
     * @param positionables to show
     */
    public SceneUpdateEvent(Visual background,
                            List<Positionable> positionables){
        this.background = background;
        this.positionables = positionables;
    }

    /**
     * Background of this scene, representing a place
     * @return background as a visual
     */
    public Visual getBackground() {
        return this.background;
    }

    /**
     * Positionables in this scene
     * @return list of positionables
     */
    public List<Positionable> getPositionables(){
        return this.positionables;
    }
}
