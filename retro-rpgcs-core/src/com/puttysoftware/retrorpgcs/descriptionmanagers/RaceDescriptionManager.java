/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.descriptionmanagers;

import java.io.IOException;

import com.puttysoftware.fileutils.ResourceStreamReader;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.creatures.races.RaceConstants;
import com.puttysoftware.retrorpgcs.maze.Extension;

public class RaceDescriptionManager {
    public static String getRaceDescription(final int r) {
        final var name = RaceConstants.getRaceName(r).toLowerCase();
        try (final var rsr = new ResourceStreamReader(
                RaceDescriptionManager.class.getResourceAsStream(
                        "/com/puttysoftware/retrorpgcs/resources/descriptions/race/"
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
