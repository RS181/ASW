package rea.events;

/**
 * A listener for updates. It is used to listen for updates of a specific type.
 * @param <T> the type of the update event.
 */
public interface UpdateListener<T extends UpdateEvent>{
    /**
     * Method to be called when an update event is received.
     * @param updateEvent the update event.
     */
    void onUpdate(T updateEvent);
}
