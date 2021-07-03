/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractTrap;
import com.puttysoftware.retrorpgcs.maze.effects.MazeEffectConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;

public class DizzinessTrap extends AbstractTrap {
    // Constructors
    public DizzinessTrap() {
	super(ObjectImageConstants.OBJECT_IMAGE_DIZZINESS_TRAP);
    }

    @Override
    public String getName() {
	return "Dizziness Trap";
    }

    @Override
    public String getPluralName() {
	return "Dizziness Traps";
    }

    @Override
    public void postMoveAction(final boolean ie, final int dirX, final int dirY) {
	RetroRPGCS.getInstance().showMessage("You feel dizzy!");
	RetroRPGCS.getInstance().getGameManager().activateEffect(MazeEffectConstants.EFFECT_DIZZY);
	SoundManager.playSound(SoundConstants.SOUND_DIZZY);
    }

    @Override
    public String getDescription() {
	return "Dizziness Traps randomly alter your controls each step for 3 steps when stepped on.";
    }
}