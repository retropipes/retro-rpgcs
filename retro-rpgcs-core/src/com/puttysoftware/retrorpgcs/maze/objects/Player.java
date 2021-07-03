/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.retrorpgcs.maze.Maze;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractCharacter;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;

public class Player extends AbstractCharacter {
    // Constructors
    public Player() {
    }

    @Override
    public int getBaseID() {
        return ObjectImageConstants.OBJECT_IMAGE_PLAYER;
    }

    @Override
    public String getDescription() {
        return "This is you - the Player.";
    }

    @Override
    public int getMaximumRequiredQuantity(final Maze maze) {
        return 1;
    }

    @Override
    public int getMinimumRequiredQuantity(final Maze maze) {
        return 1;
    }

    @Override
    public String getName() {
        return "Player";
    }

    @Override
    public String getPluralName() {
        return "Players";
    }

    // Random Generation Rules
    @Override
    public boolean isRequired() {
        return true;
    }
}