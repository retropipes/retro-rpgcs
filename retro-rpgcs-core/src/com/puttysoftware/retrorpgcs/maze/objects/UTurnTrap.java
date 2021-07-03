/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractTrap;
import com.puttysoftware.retrorpgcs.maze.effects.MazeEffectConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;

public class UTurnTrap extends AbstractTrap {
    // Constructors
    public UTurnTrap() {
	super(ObjectImageConstants.OBJECT_IMAGE_U_TURN_TRAP);
    }

    @Override
    public String getName() {
	return "U Turn Trap";
    }

    @Override
    public String getPluralName() {
	return "U Turn Traps";
    }

    @Override
    public void postMoveAction(final boolean ie, final int dirX, final int dirY) {
	RetroRPGCS.getInstance().showMessage("Your controls are turned around!");
	RetroRPGCS.getInstance().getGameManager().activateEffect(MazeEffectConstants.EFFECT_U_TURNED);
	SoundManager.playSound(SoundConstants.SOUND_CHANGE);
    }

    @Override
    public String getDescription() {
	return "U Turn Traps invert your controls for 6 steps when stepped on.";
    }
}