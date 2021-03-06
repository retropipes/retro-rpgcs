/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.creatures.faiths;

import javax.swing.JFrame;

import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.retrorpgcs.creatures.party.PartyManager;

public class FaithManager {
    private static boolean CACHE_CREATED = false;
    private static Faith[] CACHE;
    private static String[] DESC_CACHE;

    private static void createCache() {
        if (!FaithManager.CACHE_CREATED) {
            // Create cache
            if (!FaithConstants.faithsReady()) {
                FaithConstants.initFaiths();
            }
            final var fc = FaithConstants.getFaithsCount();
            FaithManager.CACHE = new Faith[fc];
            FaithManager.DESC_CACHE = new String[fc];
            for (var x = 0; x < fc; x++) {
                FaithManager.CACHE[x] = new Faith(x);
                FaithManager.DESC_CACHE[x] = FaithManager.CACHE[x]
                        .getDescription();
            }
            FaithManager.CACHE_CREATED = true;
        }
    }

    public static Faith getFaith(final int faithID) {
        FaithManager.createCache();
        return FaithManager.CACHE[faithID];
    }

    public static Faith getRandomFaith() {
        FaithManager.createCache();
        final var faithID = new RandomRange(0, FaithManager.CACHE.length - 1)
                .generate();
        return FaithManager.CACHE[faithID];
    }

    public static Faith selectFaith(final JFrame owner) {
        FaithManager.createCache();
        final var names = FaithConstants.getFaithNames();
        String dialogResult;
        dialogResult = PartyManager.showCreationDialog(owner, "Select a Faith",
                "Create Character", names, FaithManager.DESC_CACHE);
        if (dialogResult != null) {
            int index;
            for (index = 0; index < names.length; index++) {
                if (dialogResult.equals(names[index])) {
                    break;
                }
            }
            return FaithManager.getFaith(index);
        } else {
            return null;
        }
    }
}
