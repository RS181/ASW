package rea.components;

/**
 * An avatar is a set of features from character.
 * It is a maker interface for an enumeration of avatars for a particular game.
 */
public interface Avatar {

    /**
     * Name of avatar
     */
    String avatarName = null;
    /**
     * Visual for avatar
     */
    Visual avatarVisual = null;

    /**
     * The name of the avatar.
     * @return the name of the avatar
     */
    String getAvatarName();

    /**
     * The visual representation of the avatar.
     * @return the visual representation of the avatar
     */
    Visual getAvatarVisual();
}
