package rea.gaming;

import rea.components.Character;

import java.util.Objects;
import java.util.UUID;

/**
 * Player class
 */
public class Player {

    Character playerCharacter;
    UUID playerUUID;

    /**
     * Constructor
     * @param character character for player
     */
    public Player(Character character) {
        this.playerCharacter = character;
        this.playerUUID = generateId();
    }

    /**
     * Generates ID.
     * The ID is a UUID generated with the static method UUID.nameUUIDFromBytes(byte[])
     * from the Character's name.
     */
    UUID generateId() {
        if (this.getCharacter().getName() != null)
        {
            byte[] bytes = this.getCharacter().getName().getBytes();
            return UUID.nameUUIDFromBytes(bytes);
        }
        else
            throw new RuntimeException("Cannot create a UUID for Player, Character name is null.");
    }

    /**
     * Get method for character.
     * @return character
     */
    public Character getCharacter(){
        return this.playerCharacter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(playerCharacter, player.playerCharacter)
                && Objects.equals(playerUUID, player.playerUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerCharacter, playerUUID);
    }
}
