/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.descriptionmanagers;

import java.io.IOException;

import com.puttysoftware.fileutils.ResourceStreamReader;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.creatures.castes.CasteConstants;
import com.puttysoftware.retrorpgcs.maze.Extension;

public class CasteDescriptionManager {
    public static String getCasteDescription(final int c) {
        final var name = CasteConstants.CASTE_NAMES[c].toLowerCase();
        try (final var rsr = new ResourceStreamReader(
                CasteDescriptionManager.class.getResourceAsStream(
                        "/com/puttysoftware/retrorpgcs/resources/descriptions/caste/"
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
