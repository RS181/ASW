package rea.events;

import rea.gaming.GameInstance;

import java.util.List;

/**
 * An event that is sent to the client to update the list of games to play.
 */
public class GamesUpdateEvent
        implements UpdateEvent {

    final private List<GameInstance> gamesToPlay;
    /**
     * Create a games update event with a list of games to play
     * @param gamesToPlay the list of games to play
     */
    public GamesUpdateEvent(List<GameInstance> gamesToPlay) {
        this.gamesToPlay = gamesToPlay;
    }

    /**
     * Get the list of games to play
     * @return a list of games to play
     */
    public List<GameInstance> getGamesToPlay(){
        return this.gamesToPlay;
    }


}
