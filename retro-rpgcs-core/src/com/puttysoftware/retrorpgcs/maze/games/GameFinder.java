/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.games;

import java.io.File;
import java.io.FilenameFilter;

import com.puttysoftware.retrorpgcs.maze.Extension;

public class GameFinder implements FilenameFilter {
    private static String getExtension(final String s) {
        String ext = null;
        final var i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    @Override
    public boolean accept(final File f, final String s) {
        final var extension = GameFinder.getExtension(s);
        if (extension != null) {
            if (extension.equals(Extension.getGameExtension())) {
                return true;
            } else {
            }
        }
        return false;
    }
}
