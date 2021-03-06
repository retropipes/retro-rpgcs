/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.retrorpgcs.maze.abc.AbstractGround;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;

public class Tile extends AbstractGround {
    // Constructors
    public Tile() {
    }

    @Override
    public final int getBaseID() {
        return ObjectImageConstants.OBJECT_IMAGE_TILE;
    }

    @Override
    public String getDescription() {
        return "Tile is one of the many types of ground - unlike other types of ground, objects can be pushed and pulled over Tiles.";
    }

    @Override
    public String getName() {
        return "Tile";
    }

    @Override
    public String getPluralName() {
        return "Tiles";
    }
}