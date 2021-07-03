/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.maze.abc;

import com.puttysoftware.retrorpgcs.maze.MazeConstants;
import com.puttysoftware.retrorpgcs.maze.utilities.TypeConstants;

public abstract class AbstractGround extends AbstractMazeObject {
    // Constructors
    protected AbstractGround() {
	super(false, true, false);
    }

    protected AbstractGround(final boolean hasFriction) {
	super(false, hasFriction, false);
    }

    @Override
    public abstract String getName();

    @Override
    public int getLayer() {
	return MazeConstants.LAYER_GROUND;
    }

    @Override
    protected void setTypes() {
	this.type.set(TypeConstants.TYPE_GROUND);
    }

    @Override
    public int getCustomProperty(final int propID) {
	return AbstractMazeObject.DEFAULT_CUSTOM_VALUE;
    }

    @Override
    public void setCustomProperty(final int propID, final int value) {
	// Do nothing
    }

    @Override
    public void postMoveAction(final boolean ie, final int dirX, final int dirY) {
	// Do nothing
    }
}
