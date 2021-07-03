/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.abc;

import com.puttysoftware.retrorpgcs.maze.MazeConstants;
import com.puttysoftware.retrorpgcs.maze.utilities.TypeConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;

public abstract class AbstractPassThroughObject extends AbstractMazeObject {
    // Constructors
    protected AbstractPassThroughObject() {
	super(false, true, false);
    }

    @Override
    public void postMoveAction(final boolean ie, final int dirX, final int dirY) {
	SoundManager.playSound(SoundConstants.SOUND_WALK);
    }

    @Override
    public abstract String getName();

    @Override
    public int getLayer() {
	return MazeConstants.LAYER_OBJECT;
    }

    @Override
    protected void setTypes() {
	this.type.set(TypeConstants.TYPE_PASS_THROUGH);
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