/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.creatures.races;

import javax.swing.JFrame;

import com.puttysoftware.retrorpgcs.creatures.party.PartyManager;
import com.puttysoftware.retrorpgcs.datamanagers.RaceDataManager;

public class RaceManager {
    private static boolean CACHE_CREATED = false;
    private static Race[] CACHE;
    private static String[] DESC_CACHE;

    private static void createCache() {
        if (!RaceManager.CACHE_CREATED) {
            if (!RaceConstants.racesReady()) {
                RaceConstants.initRaces();
            }
            // Create cache
            if (!RaceConstants.racesReady()) {
                RaceConstants.initRaces();
            }
            RaceManager.CACHE = new Race[RaceConstants.getRacesCount()];
            RaceManager.DESC_CACHE = new String[RaceConstants.getRacesCount()];
            for (var x = 0; x < RaceConstants.getRacesCount(); x++) {
                final var rdata = RaceDataManager.getRaceData(x);
                RaceManager.CACHE[x] = new Race(x, rdata);
                RaceManager.DESC_CACHE[x] = RaceManager.CACHE[x]
                        .getDescription();
            }
            RaceManager.CACHE_CREATED = true;
        }
    }

    public static Race getRace(final int raceID) {
        RaceManager.createCache();
        return RaceManager.CACHE[raceID];
    }

    public static Race selectRace(final JFrame owner) {
        RaceManager.createCache();
        final var names = RaceConstants.getRaceNames();
        String dialogResult;
        dialogResult = PartyManager.showCreationDialog(owner, "Select a Race",
                "Create Character", names, RaceManager.DESC_CACHE);
        if (dialogResult != null) {
            int index;
            for (index = 0; index < names.length; index++) {
                if (dialogResult.equals(names[index])) {
                    break;
                }
            }
            return RaceManager.getRace(index);
        } else {
            return null;
        }
    }
}
