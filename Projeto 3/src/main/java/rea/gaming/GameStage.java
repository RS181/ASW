package rea.gaming;

import java.io.Serializable;
import java.lang.constant.Constable;

/**
 * Game stage enum
 */
public enum GameStage
        implements Serializable, Comparable<GameStage>, Constable {

    /**
     * GameStage for created status
     */
    CREATED,
    /**
     * GameStage for playing game status
     */
    PLAYING,
    /**
     * GameStage for ended game status
     */
    ENDED
}
