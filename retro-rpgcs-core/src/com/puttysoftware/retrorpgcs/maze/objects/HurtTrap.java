/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.retrorpgcs.creatures.party.PartyManager;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractTrap;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;

public class HurtTrap extends AbstractTrap {
    // Constructors
    public HurtTrap() {
	super(ObjectImageConstants.OBJECT_IMAGE_HURT_TRAP);
    }

    @Override
    public String getName() {
	return "Hurt Trap";
    }

    @Override
    public String getPluralName() {
	return "Hurt Traps";
    }

    @Override
    public void postMoveAction(final boolean ie, final int dirX, final int dirY) {
	int damage = PartyManager.getParty().getLeader().getMaximumHP() / 10;
	if (damage < 1) {
	    damage = 1;
	}
	PartyManager.getParty().getLeader().doDamage(damage);
	SoundManager.playSound(SoundConstants.SOUND_BARRIER);
    }

    @Override
    public String getDescription() {
	return "Hurt Traps hurt you when stepped on.";
    }
}