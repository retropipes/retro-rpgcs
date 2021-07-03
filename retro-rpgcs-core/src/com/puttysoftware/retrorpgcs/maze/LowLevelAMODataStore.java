/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.maze;

import com.puttysoftware.retrorpgcs.maze.abc.AbstractMazeObject;
import com.puttysoftware.storage.ObjectStorage;

class LowLevelAMODataStore extends ObjectStorage {
    // Constructor
    public LowLevelAMODataStore(final int... shape) {
        super(shape);
    }

    public LowLevelAMODataStore(final LowLevelAMODataStore copyFrom) {
        super(copyFrom);
    }

    // Methods
    public AbstractMazeObject getMazeCell(final int... loc) {
        return (AbstractMazeObject) super.getCell(loc);
    }

    public void setMazeCell(AbstractMazeObject obj, final int... loc) {
        super.setCell(obj, loc);
    }
}
