/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.datamanagers;

import java.io.IOException;

import com.puttysoftware.fileutils.ResourceStreamReader;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.creatures.races.RaceConstants;
import com.puttysoftware.retrorpgcs.maze.Extension;

public class RaceDataManager {
    public static int[] getRaceData(final int r) {
	final String name = RaceConstants.getRaceName(r).toLowerCase();
	try (final ResourceStreamReader rsr = new ResourceStreamReader(
		RaceDataManager.class.getResourceAsStream("/com/puttysoftware/tallertower/resources/data/race/" + name
			+ Extension.getInternalDataExtensionWithPeriod()))) {
	    // Fetch data
	    final int[] rawData = new int[RaceConstants.RACE_ATTRIBUTE_COUNT];
	    for (int x = 0; x < rawData.length; x++) {
		rawData[x] = rsr.readInt();
	    }
	    return rawData;
	} catch (final IOException e) {
	    RetroRPGCS.getErrorLogger().logError(e);
	    return null;
	}
    }
}
