/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.retrorpgcs.maze.Maze;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractGround;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;

public class Ice extends AbstractGround {
    public Ice() {
	super(false);
    }

    @Override
    public final int getBaseID() {
	return ObjectImageConstants.OBJECT_IMAGE_ICE;
    }

    @Override
    public String getName() {
	return "Ice";
    }

    @Override
    public String getPluralName() {
	return "Squares of Ice";
    }

    @Override
    public boolean overridesDefaultPostMove() {
	return true;
    }

    @Override
    public void postMoveAction(final boolean ie, final int dirX, final int dirY) {
	SoundManager.playSound(SoundConstants.SOUND_WALK_ICE);
    }

    @Override
    public String getDescription() {
	return "Ice is one of the many types of ground - it is frictionless. Anything that crosses it will slide.";
    }

    @Override
    public boolean shouldGenerateObject(final Maze maze, final int row, final int col, final int floor, final int level,
	    final int layer) {
	// Generate Ice at 40% rate
	final RandomRange reject = new RandomRange(1, 100);
	return reject.generate() < 40;
    }
}
