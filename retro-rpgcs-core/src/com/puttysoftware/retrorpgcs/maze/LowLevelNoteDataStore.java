/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze;

import com.puttysoftware.storage.ObjectStorage;

class LowLevelNoteDataStore extends ObjectStorage {
    // Constructor
    LowLevelNoteDataStore(final int... shape) {
        super(shape);
    }

    LowLevelNoteDataStore(final LowLevelNoteDataStore copyFrom) {
        super(copyFrom);
    }

    // Methods
    public MazeNote getNote(final int... loc) {
        return (MazeNote) this.getCell(loc);
    }

    public void setNote(final MazeNote obj, final int... loc) {
        this.setCell(obj, loc);
    }
}
