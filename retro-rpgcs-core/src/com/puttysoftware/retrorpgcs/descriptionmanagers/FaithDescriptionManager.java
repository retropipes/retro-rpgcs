/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.descriptionmanagers;

import java.io.IOException;

import com.puttysoftware.fileutils.ResourceStreamReader;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.creatures.faiths.FaithConstants;
import com.puttysoftware.retrorpgcs.maze.Extension;

public class FaithDescriptionManager {
    public static String getFaithDescription(final int f) {
        final var name = FaithConstants.getFaithName(f).toLowerCase();
        try (final var rsr = new ResourceStreamReader(
                FaithDescriptionManager.class.getResourceAsStream(
                        "/com/puttysoftware/retrorpgcs/resources/descriptions/faith/"
                                + name + Extension
                                        .getInternalDataExtensionWithPeriod()))) {
            // Fetch description
            final var desc = rsr.readString();
            return desc;
        } catch (final IOException e) {
            RetroRPGCS.getInstance().handleError(e);
            return null;
        }
    }
}
