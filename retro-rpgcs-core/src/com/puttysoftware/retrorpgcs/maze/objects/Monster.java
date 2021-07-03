/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.retrorpgcs.Application;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractMovingObject;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;

public class Monster extends AbstractMovingObject {
    // Constructors
    public Monster() {
	super(false);
	this.setSavedObject(new Empty());
	this.activateTimer(1);
    }

    @Override
    public void postMoveAction(final boolean ie, final int dirX, final int dirY) {
	if (RetroRPGCS.getApplication().getMode() != Application.STATUS_BATTLE) {
	    RetroRPGCS.getApplication().getBattle().doBattle();
	    RetroRPGCS.getApplication().getMazeManager().getMaze().postBattle(this, dirX, dirY, true);
	}
    }

    @Override
    public void timerExpiredAction(final int dirX, final int dirY) {
	// Move the monster
	final RandomRange r = new RandomRange(0, 7);
	final int move = r.generate();
	RetroRPGCS.getApplication().getMazeManager().getMaze().updateMonsterPosition(move, dirX, dirY, this);
	this.activateTimer(1);
    }

    @Override
    public int getBaseID() {
	return ObjectImageConstants.OBJECT_IMAGE_MONSTER;
    }

    @Override
    public String getName() {
	return "Monster";
    }

    @Override
    public String getPluralName() {
	return "Monsters";
    }

    @Override
    public String getDescription() {
	return "Monsters are dangerous. Encountering one starts a battle.";
    }
}
