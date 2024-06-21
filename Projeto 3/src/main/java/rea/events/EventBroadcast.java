package rea.events;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>Event broadcast to registered listeners.
 * Listeners can be added and removed using the {@link #addListener(UpdateListener)} and
 * {@link #removeListener(UpdateListener)} methods.
 * Listeners are automatically removed if they throw an exception.
 * Events are broadcast to all listeners using the {@link #broadcast(T)} method.</p>
 * @param <T> the type of event that can be listened to.
 */
public class EventBroadcast<T extends UpdateEvent> {

    /**
     * The listeners registered to receive events.
     */
    Set<UpdateListener<UpdateEvent>> listeners = new HashSet<>();

    /**
     * Create an instance of this class. No particular initializations are performed.
     */
    public EventBroadcast(){
    }

    /**
     * Add a listener to the list of listeners.
     * @param listener the listener to add.
     */
    public void addListener(UpdateListener<T> listener) {
        if (! listeners.contains(listener))
            listeners.add((UpdateListener<UpdateEvent>) listener);
    }

    /**
     * Remove a listener from the list of listeners.
     * @param listener the listener to remove.
     */
    public void removeListener(UpdateListener<T> listener) {
        listeners.remove(listener);
    }

    /**
     * Get all the currently registered listeners.
     * Listeners are automatically removed if they throw an exception.
     *
     * @return set of listeners.
     */
    public Set<UpdateListener<UpdateEvent>> getListeners() {
        return this.listeners;
    }

    /**
     * Broadcast an event to all listeners.
     * If the events is not handled by a listener, the listener is removed.
     * @param event the event to broadcast.
     */
    public void broadcast(T event) {
        for(UpdateListener updateListener : listeners){
            try {
                updateListener.onUpdate(event);
            }
            catch (Exception e)   // remove listener because it threw an exception
            {
                removeListener(updateListener);
            }
        }
    }

}
