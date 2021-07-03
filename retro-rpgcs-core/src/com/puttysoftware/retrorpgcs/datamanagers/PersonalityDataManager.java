/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.datamanagers;

import java.io.IOException;

import com.puttysoftware.fileutils.ResourceStreamReader;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.creatures.personalities.PersonalityConstants;
import com.puttysoftware.retrorpgcs.maze.Extension;

public class PersonalityDataManager {
    public static double[] getPersonalityData(final int p) {
        final var name = PersonalityConstants.getPersonalityName(p)
                .toLowerCase();
        try (final var rsr = new ResourceStreamReader(
                PersonalityDataManager.class.getResourceAsStream(
                        "/com/puttysoftware/retrorpgcs/resources/data/personality/"
                                + name + Extension
                                        .getInternalDataExtensionWithPeriod()))) {
            // Fetch data
            final var rawData = new int[PersonalityConstants.PERSONALITY_ATTRIBUTES_COUNT];
            for (var x = 0; x < rawData.length; x++) {
                rawData[x] = rsr.readInt();
            }
            // Parse raw data
            final var finalData = new double[rawData.length];
            for (var x = 0; x < rawData.length; x++) {
                if (x == PersonalityConstants.PERSONALITY_ATTRIBUTE_LEVEL_UP_SPEED) {
                    finalData[x] = PersonalityConstants
                            .getLookupTableEntry(-rawData[x]);
                } else {
                    finalData[x] = PersonalityConstants
                            .getLookupTableEntry(rawData[x]);
                }
            }
            return finalData;
        } catch (final IOException e) {
            RetroRPGCS.getInstance().handleError(e);
            return null;
        }
    }
}
