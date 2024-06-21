package rea.gameplay.games;

import rea.components.Avatar;
import rea.components.Visual;

import java.io.Serializable;
import java.lang.constant.Constable;

/**
 * Enumeration of cartoon avatars for the game examples.
 * Each avatar has a name and a visual representation.
 */
public enum CartoonAvatar
        implements Avatar,
        Serializable, Comparable<CartoonAvatar>, Constable {

    /**
     * a cartoon bunny avatar.
     */
    BUNNY("Bunny","images/bunny.png",100,150),
    /**
     * a cartoon chick avatar.
     */
    CHICK("Chick","images/chick.png",100,150),
    /**
     * a cartoon lamb avatar.
     */
    LAMB("Lamb","images/lamb.png",100,150);

    /**
     * The name of the avatar.
     */
    final String avatarName;

    /**
     * The visual representation of the avatar.
     */
    final Visual avatarVisual;


    CartoonAvatar(String name, String pathname, int width, int height) {
        this.avatarName = name;
        this.avatarVisual = new Visual(pathname, width, height);
    }

    @Override
    public String getAvatarName() {
        return this.avatarName;
    }

    @Override
    public Visual getAvatarVisual() {
        return this.avatarVisual;
    }

}
