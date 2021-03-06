/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze;

import java.io.IOException;

import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public interface PrefixIO {
    int readPrefix(XDataReader reader) throws IOException;

    void writePrefix(XDataWriter writer) throws IOException;
}
