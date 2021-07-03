/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.retrorpgcs.maze.abc.AbstractPassThroughObject;
import com.puttysoftware.retrorpgcs.maze.utilities.TypeConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;

public class Darkness extends AbstractPassThroughObject {
    // Constructors
    public Darkness() {
	super();
    }

    @Override
    public int getBaseID() {
	return ObjectImageConstants.OBJECT_IMAGE_DARKNESS;
    }

    @Override
    public String getName() {
	return "Darkness";
    }

    @Override
    public String getPluralName() {
	return "Squares of Darkness";
    }

    @Override
    public String getDescription() {
	return "Squares of Darkness are what fills areas that cannot be seen.";
    }

    @Override
    protected void setTypes() {
	this.type.set(TypeConstants.TYPE_PASS_THROUGH);
	this.type.set(TypeConstants.TYPE_EMPTY_SPACE);
    }
}