/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.retrorpgcs.maze.abc.AbstractPassThroughObject;
import com.puttysoftware.retrorpgcs.maze.utilities.TypeConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;

public class WallOff extends AbstractPassThroughObject {
    // Constructors
    public WallOff() {
        super();
    }

    @Override
    public int getBaseID() {
        return ObjectImageConstants.OBJECT_IMAGE_WALL_OFF;
    }

    @Override
    public String getName() {
        return "Wall Off";
    }

    @Override
    public String getPluralName() {
        return "Walls Off";
    }

    @Override
    public String getDescription() {
        return "Walls Off can be walked through.";
    }

    @Override
    protected void setTypes() {
        this.type.set(TypeConstants.TYPE_PASS_THROUGH);
        this.type.set(TypeConstants.TYPE_EMPTY_SPACE);
    }
}