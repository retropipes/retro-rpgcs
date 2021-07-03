/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.retrorpgcs.maze.abc.AbstractPassThroughObject;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;

public class OpenDoor extends AbstractPassThroughObject {
    // Constructors
    public OpenDoor() {
	super();
    }

    @Override
    public int getBaseID() {
	return ObjectImageConstants.OBJECT_IMAGE_OPEN_DOOR;
    }

    // Scriptability
    @Override
    public String getName() {
	return "Open Door";
    }

    @Override
    public String getPluralName() {
	return "Open Doors";
    }

    @Override
    public String getDescription() {
	return "Open Doors are purely decorative.";
    }
}
