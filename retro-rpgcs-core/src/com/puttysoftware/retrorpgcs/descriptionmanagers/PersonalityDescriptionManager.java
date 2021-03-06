/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.descriptionmanagers;

import java.io.IOException;

import com.puttysoftware.fileutils.ResourceStreamReader;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.creatures.personalities.PersonalityConstants;
import com.puttysoftware.retrorpgcs.maze.Extension;

public class PersonalityDescriptionManager {
    public static String getPersonalityDescription(final int p) {
        final var name = PersonalityConstants.getPersonalityName(p)
                .toLowerCase();
        try (final var rsr = new ResourceStreamReader(
                PersonalityDescriptionManager.class.getResourceAsStream(
                        "/com/puttysoftware/retrorpgcs/resources/descriptions/personality/"
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
