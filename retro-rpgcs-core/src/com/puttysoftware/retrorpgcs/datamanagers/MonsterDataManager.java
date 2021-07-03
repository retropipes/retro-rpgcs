/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.datamanagers;

import java.io.IOException;
import java.util.ArrayList;

import com.puttysoftware.fileutils.ResourceStreamReader;
import com.puttysoftware.retrorpgcs.RetroRPGCS;

public class MonsterDataManager {
    public static String[] getMonsterData() {
	try (final ResourceStreamReader rsr = new ResourceStreamReader(MonsterDataManager.class
		.getResourceAsStream("/com/puttysoftware/tallertower/resources/data/monsters/monsters.txt"))) {
	    // Fetch data
	    final ArrayList<String> rawData = new ArrayList<>();
	    String line = "";
	    while (line != null) {
		line = rsr.readString();
		if (line != null) {
		    rawData.add(line);
		}
	    }
	    return rawData.toArray(new String[rawData.size()]);
	} catch (final IOException e) {
	    RetroRPGCS.getInstance().handleError(e);
	    return null;
	}
    }
}
