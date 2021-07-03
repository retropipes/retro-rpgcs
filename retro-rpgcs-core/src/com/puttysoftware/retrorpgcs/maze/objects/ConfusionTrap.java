/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractTrap;
import com.puttysoftware.retrorpgcs.maze.effects.MazeEffectConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;

public class ConfusionTrap extends AbstractTrap {
    // Constructors
    public ConfusionTrap() {
	super(ObjectImageConstants.OBJECT_IMAGE_CONFUSION_TRAP);
    }

    @Override
    public String getName() {
	return "Confusion Trap";
    }

    @Override
    public String getPluralName() {
	return "Confusion Traps";
    }

    @Override
    public void postMoveAction(final boolean ie, final int dirX, final int dirY) {
	RetroRPGCS.getInstance().showMessage("You are confused!");
	RetroRPGCS.getInstance().getGameManager().activateEffect(MazeEffectConstants.EFFECT_CONFUSED);
	SoundManager.playSound(SoundConstants.SOUND_CONFUSED);
    }

    @Override
    public String getDescription() {
	return "Confusion Traps randomly alter your controls for 6 steps when stepped on.";
    }
}