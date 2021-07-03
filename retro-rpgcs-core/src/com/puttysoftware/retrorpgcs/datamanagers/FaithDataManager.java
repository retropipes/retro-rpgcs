/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.datamanagers;

import java.io.IOException;

import com.puttysoftware.fileutils.ResourceStreamReader;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.creatures.faiths.FaithConstants;
import com.puttysoftware.retrorpgcs.maze.Extension;

public class FaithDataManager {
    public static double[] getFaithData(final int f) {
	final String name = FaithConstants.getFaithName(f).toLowerCase();
	try (final ResourceStreamReader rsr = new ResourceStreamReader(
		FaithDataManager.class.getResourceAsStream("/com/puttysoftware/tallertower/resources/data/faith/" + name
			+ Extension.getInternalDataExtensionWithPeriod()))) {
	    // Fetch data
	    final int[] rawData = new int[FaithConstants.getFaithsCount()];
	    for (int x = 0; x < rawData.length; x++) {
		rawData[x] = rsr.readInt();
	    }
	    // Parse raw data
	    final double[] finalData = new double[rawData.length];
	    for (int x = 0; x < rawData.length; x++) {
		finalData[x] = FaithConstants.getLookupTableEntry(rawData[x]);
	    }
	    return finalData;
	} catch (final IOException e) {
	    RetroRPGCS.getErrorLogger().logError(e);
	    return null;
	}
    }
}
