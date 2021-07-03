/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.descriptionmanagers;

import java.io.IOException;

import com.puttysoftware.fileutils.ResourceStreamReader;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.creatures.castes.CasteConstants;
import com.puttysoftware.retrorpgcs.maze.Extension;

public class CasteDescriptionManager {
    public static String getCasteDescription(final int c) {
	final String name = CasteConstants.CASTE_NAMES[c].toLowerCase();
	try (final ResourceStreamReader rsr = new ResourceStreamReader(CasteDescriptionManager.class
		.getResourceAsStream("/com/puttysoftware/tallertower/resources/descriptions/caste/" + name
			+ Extension.getInternalDataExtensionWithPeriod()))) {
	    // Fetch description
	    final String desc = rsr.readString();
	    return desc;
	} catch (final IOException e) {
	    RetroRPGCS.getInstance().handleError(e);
	    return null;
	}
    }
}
