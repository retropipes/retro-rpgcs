/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.retrorpgcs.maze.abc.AbstractWall;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;

public class SealingWall extends AbstractWall {
    // Constructors
    public SealingWall() {
	super();
    }

    @Override
    public int getBaseID() {
	return ObjectImageConstants.OBJECT_IMAGE_SEALING_WALL;
    }

    @Override
    public String getName() {
	return "Sealing Wall";
    }

    @Override
    public String getPluralName() {
	return "Sealing Walls";
    }

    @Override
    public String getDescription() {
	return "Sealing Walls are impassable and indestructible - you'll need to go around them.";
    }
}