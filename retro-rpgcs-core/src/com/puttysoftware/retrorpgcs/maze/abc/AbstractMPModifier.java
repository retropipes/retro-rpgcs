/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.abc;

import com.puttysoftware.retrorpgcs.maze.MazeConstants;
import com.puttysoftware.retrorpgcs.maze.utilities.TypeConstants;

public abstract class AbstractMPModifier extends AbstractMazeObject {
    // Constructors
    protected AbstractMPModifier() {
	super(false, false);
    }

    @Override
    protected void setTypes() {
	this.type.set(TypeConstants.TYPE_MP_MODIFIER);
    }

    @Override
    public int getLayer() {
	return MazeConstants.LAYER_OBJECT;
    }

    @Override
    public int getCustomProperty(final int propID) {
	return AbstractMazeObject.DEFAULT_CUSTOM_VALUE;
    }

    @Override
    public void setCustomProperty(final int propID, final int value) {
	// Do nothing
    }
}
