/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze;

import java.io.IOException;

import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class MazeNote {
    static MazeNote readNote(final XDataReader reader) throws IOException {
        final var mn = new MazeNote();
        mn.contents = reader.readString();
        return mn;
    }

    // Fields
    private String contents;

    // Constructor
    public MazeNote() {
        this.contents = "Empty Note";
    }

    public MazeNote(final MazeNote copyFrom) {
        this.contents = copyFrom.contents;
    }

    // Methods
    public String getContents() {
        return this.contents;
    }

    public void setContents(final String newContents) {
        this.contents = newContents;
    }

    void writeNote(final XDataWriter writer) throws IOException {
        writer.writeString(this.contents);
    }
}
