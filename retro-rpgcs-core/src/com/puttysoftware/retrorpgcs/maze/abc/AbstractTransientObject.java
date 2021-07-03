/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.abc;

import com.puttysoftware.retrorpgcs.maze.MazeConstants;
import com.puttysoftware.retrorpgcs.maze.utilities.DirectionConstants;
import com.puttysoftware.retrorpgcs.maze.utilities.DirectionResolver;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;

public abstract class AbstractTransientObject extends AbstractMazeObject {
    // Fields
    private final String name;
    private int dir;

    // Constructors
    protected AbstractTransientObject(final String newName) {
        super(true, false);
        this.name = newName;
        this.dir = DirectionConstants.DIRECTION_NONE;
    }

    @Override
    public final int getBaseID() {
        switch (this.dir) {
        case DirectionConstants.DIRECTION_NORTH:
            return ObjectImageConstants.OBJECT_IMAGE_ARROW_NORTH;
        case DirectionConstants.DIRECTION_NORTHEAST:
            return ObjectImageConstants.OBJECT_IMAGE_ARROW_NORTHEAST;
        case DirectionConstants.DIRECTION_EAST:
            return ObjectImageConstants.OBJECT_IMAGE_ARROW_EAST;
        case DirectionConstants.DIRECTION_SOUTHEAST:
            return ObjectImageConstants.OBJECT_IMAGE_ARROW_SOUTHEAST;
        case DirectionConstants.DIRECTION_SOUTH:
            return ObjectImageConstants.OBJECT_IMAGE_ARROW_SOUTH;
        case DirectionConstants.DIRECTION_SOUTHWEST:
            return ObjectImageConstants.OBJECT_IMAGE_ARROW_SOUTHWEST;
        case DirectionConstants.DIRECTION_WEST:
            return ObjectImageConstants.OBJECT_IMAGE_ARROW_WEST;
        case DirectionConstants.DIRECTION_NORTHWEST:
            return ObjectImageConstants.OBJECT_IMAGE_ARROW_NORTHWEST;
        default:
            return ObjectImageConstants.OBJECT_IMAGE_EMPTY;
        }
    }

    @Override
    public int getCustomProperty(final int propID) {
        return AbstractMazeObject.DEFAULT_CUSTOM_VALUE;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public int getLayer() {
        return MazeConstants.LAYER_OBJECT;
    }

    @Override
    public final String getName() {
        return this.name + " "
                + DirectionResolver.resolveDirectionConstantToName(this.dir);
    }

    @Override
    public String getPluralName() {
        return this.name + "s "
                + DirectionResolver.resolveDirectionConstantToName(this.dir);
    }

    // Methods
    @Override
    public void postMoveAction(final boolean ie, final int dirX,
            final int dirY) {
        // Do nothing
    }

    @Override
    public void setCustomProperty(final int propID, final int value) {
        // Do nothing
    }

    public final void setDirection(final int newDir) {
        this.dir = newDir;
    }

    @Override
    protected final void setTypes() {
        // Do nothing
    }
}
