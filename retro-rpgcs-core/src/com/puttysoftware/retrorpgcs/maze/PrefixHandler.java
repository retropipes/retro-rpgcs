/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze;

import java.io.IOException;

import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class PrefixHandler implements PrefixIO {
    private static final byte FORMAT_VERSION = (byte) FormatConstants.MAZE_FORMAT_LATEST;

    private static boolean checkFormatVersion(final byte version) {
        return version <= PrefixHandler.FORMAT_VERSION;
    }

    private static byte readFormatVersion(final XDataReader reader)
            throws IOException {
        return reader.readByte();
    }

    private static void writeFormatVersion(final XDataWriter writer)
            throws IOException {
        writer.writeByte(PrefixHandler.FORMAT_VERSION);
    }

    @Override
    public int readPrefix(final XDataReader reader) throws IOException {
        final var formatVer = PrefixHandler.readFormatVersion(reader);
        final var res = PrefixHandler.checkFormatVersion(formatVer);
        if (!res) {
            throw new IOException(
                    "Unsupported maze format version: " + formatVer);
        }
        return formatVer;
    }

    @Override
    public void writePrefix(final XDataWriter writer) throws IOException {
        PrefixHandler.writeFormatVersion(writer);
    }
}
