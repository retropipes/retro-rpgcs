/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.datamanagers;

import java.io.IOException;

import com.puttysoftware.fileutils.ResourceStreamReader;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.creatures.faiths.FaithConstants;
import com.puttysoftware.retrorpgcs.maze.Extension;

public class FaithDataManager {
    public static double[] getFaithData(final int f) {
        final var name = FaithConstants.getFaithName(f).toLowerCase();
        try (final var rsr = new ResourceStreamReader(
                FaithDataManager.class.getResourceAsStream(
                        "/com/puttysoftware/retrorpgcs/resources/data/faith/"
                                + name + Extension
                                        .getInternalDataExtensionWithPeriod()))) {
            // Fetch data
            final var rawData = new int[FaithConstants.getFaithsCount()];
            for (var x = 0; x < rawData.length; x++) {
                rawData[x] = rsr.readInt();
            }
            // Parse raw data
            final var finalData = new double[rawData.length];
            for (var x = 0; x < rawData.length; x++) {
                finalData[x] = FaithConstants.getLookupTableEntry(rawData[x]);
            }
            return finalData;
        } catch (final IOException e) {
            RetroRPGCS.getInstance().handleError(e);
            return null;
        }
    }
}
