package rea.events;

import rea.components.Character;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>Event multicast to groups of registered listeners.
 * Each listener is associated to player and can be added and removed using the
 * {@link #addListener(Character, UpdateListener)} and
 * {@link #removeListener(Character)} methods.
 * Listeners are automatically removed if they throw an exception.
 * Events are multicast to listeners using the {@link #multicast(Set, UpdateEvent)} method,
 * specifying a set of players to multicast to.
 * Alternatively, events can be unicast to a single player using the {@link #unicast(Character, T)} method.</p>
 * @param <T> the type of event to be notified about.
 */
public class EventMulticast<T extends UpdateEvent> {

    /**
     * The listeners registered to receive events.
     * To support multicast, listeners are associated to a character.
     */
    Map<Character,UpdateListener<T>> listeners = new HashMap<>();

    /**
     * Create an instance of this class.
     * No particular initializations are performed.
     */
    public EventMulticast(){
    }

    /**
     * Get all the currently registered listeners.
     * @return map of listeners keyed by characters.
     */
    public Map<Character,UpdateListener<T>> getListeners(){
        return this.listeners;
    }

    /**
     * Add a listener for a given character.
     * @param character to add listener for
     * @param listener the listener to add
     */
    public void addListener(Character character,
                            UpdateListener<T> listener) {
        if (listener == null){
            throw new RuntimeException("EventMulticast.addListener: listener is null");
        }

        listeners.put(character, listener);
    }

    /**
     * Remove a listener for a given and character
     * @param character to remove listener for
     */
    protected void removeListener(Character character) {
        listeners.remove(character);
    }

    /**
     * Unicast an event to a listener assigned by a given character.
     * If an event is not handled by a listener, the listener is removed.
     * @param character the character to unicast to.
     * @param event the event to unicast.
     */
    public void unicast(Character character, T event) {
        if (!listeners.isEmpty() && listeners.containsKey(character)) {
            UpdateListener<T> uListener = listeners.get(character);
            try {
                uListener.onUpdate(event);
            }
            catch (Exception e )
            {
                listeners.remove(character);
            }
        }
    }

    /**
     * Multicast an event to listeners assigned by given characters.
     * If an event is not handled by a listener, the listener is removed.
     * @param characters the characters to multicast to.
     * @param event the event to multicast.
     */
    public void multicast(Set<Character> characters, T event) {
        for (Character c: characters){
            if (this.getListeners().containsKey(c)){
                try{
                    this.unicast(c, event);
                }
                catch(Exception e) {
                    this.removeListener(c);
                }
            }
        }
    }

}
