/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractTrap;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;

public class WarpTrap extends AbstractTrap {
    // Constructors
    public WarpTrap() {
	super(ObjectImageConstants.OBJECT_IMAGE_WARP_TRAP);
    }

    @Override
    public String getName() {
	return "Warp Trap";
    }

    @Override
    public String getPluralName() {
	return "Warp Traps";
    }

    @Override
    public void postMoveAction(final boolean ie, final int dirX, final int dirY) {
	RandomRange rr, rc, rf;
	final RetroRPGCS app = RetroRPGCS.getInstance();
	int maxRow, maxCol, maxFloor, rRow, rCol, rFloor;
	maxRow = app.getMazeManager().getMaze().getRows() - 1;
	rr = new RandomRange(0, maxRow);
	maxCol = app.getMazeManager().getMaze().getColumns() - 1;
	rc = new RandomRange(0, maxCol);
	maxFloor = app.getMazeManager().getMaze().getFloors() - 1;
	rf = new RandomRange(0, maxFloor);
	app.getGameManager();
	do {
	    rRow = rr.generate();
	    rCol = rc.generate();
	    rFloor = rf.generate();
	} while (app.getGameManager().tryUpdatePositionAbsolute(rRow, rCol, rFloor));
	app.getGameManager().updatePositionAbsolute(rRow, rCol, rFloor);
	SoundManager.playSound(SoundConstants.SOUND_TELEPORT);
    }

    @Override
    public String getDescription() {
	return "Warp Traps send anything that steps on one to a random location.";
    }
}
