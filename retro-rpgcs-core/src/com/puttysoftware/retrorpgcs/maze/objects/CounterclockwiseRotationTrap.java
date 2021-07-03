/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractTrap;
import com.puttysoftware.retrorpgcs.maze.effects.MazeEffectConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;

public class CounterclockwiseRotationTrap extends AbstractTrap {
    // Constructors
    public CounterclockwiseRotationTrap() {
	super(ObjectImageConstants.OBJECT_IMAGE_CCW_ROTATION_TRAP);
    }

    @Override
    public String getName() {
	return "Counterclockwise Rotation Trap";
    }

    @Override
    public String getPluralName() {
	return "Counterclockwise Rotation Traps";
    }

    @Override
    public void postMoveAction(final boolean ie, final int dirX, final int dirY) {
	SoundManager.playSound(SoundConstants.SOUND_CHANGE);
	RetroRPGCS.getInstance().showMessage("Your controls are rotated!");
	RetroRPGCS.getInstance().getGameManager().activateEffect(MazeEffectConstants.EFFECT_ROTATED_COUNTERCLOCKWISE);
    }

    @Override
    public String getDescription() {
	return "Counterclockwise Rotation Traps rotate your controls counterclockwise for 6 steps when stepped on.";
    }
}