/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.datamanagers;

import java.io.IOException;

import com.puttysoftware.fileutils.ResourceStreamReader;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.creatures.races.RaceConstants;
import com.puttysoftware.retrorpgcs.maze.Extension;

public class RaceDataManager {
    public static int[] getRaceData(final int r) {
        final var name = RaceConstants.getRaceName(r).toLowerCase();
        try (final var rsr = new ResourceStreamReader(
                RaceDataManager.class.getResourceAsStream(
                        "/com/puttysoftware/retrorpgcs/resources/data/race/"
                                + name + Extension
                                        .getInternalDataExtensionWithPeriod()))) {
            // Fetch data
            final var rawData = new int[RaceConstants.RACE_ATTRIBUTE_COUNT];
            for (var x = 0; x < rawData.length; x++) {
                rawData[x] = rsr.readInt();
            }
            return rawData;
        } catch (final IOException e) {
            RetroRPGCS.getInstance().handleError(e);
            return null;
        }
    }
}
