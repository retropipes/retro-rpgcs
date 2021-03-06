/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.creatures.characterfiles;

import java.io.File;
import java.io.FilenameFilter;

import com.puttysoftware.retrorpgcs.maze.Extension;

class CharacterFilter implements FilenameFilter {
    private static String getExtension(final String s) {
        String ext = null;
        final var i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    @Override
    public boolean accept(final File dir, final String name) {
        final var ext = CharacterFilter.getExtension(name);
        if ((ext != null) && ext.equals(Extension.getCharacterExtension())) {
            return true;
        } else {
            return false;
        }
    }
}
