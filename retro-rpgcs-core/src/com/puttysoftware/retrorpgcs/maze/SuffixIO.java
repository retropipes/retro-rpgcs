/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze;

import java.io.IOException;

import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public interface SuffixIO {
    void readSuffix(XDataReader reader, int formatVersion) throws IOException;

    void writeSuffix(XDataWriter writer) throws IOException;
}
