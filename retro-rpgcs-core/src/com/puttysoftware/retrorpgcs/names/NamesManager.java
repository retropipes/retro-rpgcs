/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.names;

import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.datamanagers.NamesDataManager;

public class NamesManager {
    private static boolean CACHE_CREATED = false;
    private static String[] RAW_CACHE;
    private static String[][] CACHE;

    private static boolean containsKey(final String key) {
        for (final String[] element : NamesManager.CACHE) {
            if (element[0].equals(key)) {
                return true;
            }
        }
        return false;
    }

    private static void createCache() {
        if (!NamesManager.CACHE_CREATED) {
            // Create raw cache
            NamesManager.RAW_CACHE = NamesDataManager.getNamesData();
            if (NamesManager.RAW_CACHE != null) {
                NamesManager.CACHE = new String[NamesManager.RAW_CACHE.length][2];
                for (var x = 0; x < NamesManager.RAW_CACHE.length; x++) {
                    if (NamesManager.RAW_CACHE[x] != null
                            && !NamesManager.RAW_CACHE[x].isEmpty()) {
                        // Entry
                        final var splitEntry = NamesManager.RAW_CACHE[x]
                                .split("=");
                        // Sanity check
                        if (splitEntry.length < 2) {
                            throw new IllegalArgumentException(
                                    "Invalid names file format: Entry format invalid!");
                        }
                        final var key = splitEntry[0];
                        final var value = splitEntry[1];
                        NamesManager.CACHE[x][0] = key;
                        NamesManager.CACHE[x][1] = value;
                    }
                }
                NamesManager.CACHE_CREATED = true;
            } else {
                throw new IllegalArgumentException("Names file not found!");
            }
        }
    }

    public static String getName(final String section, final String type) {
        try {
            NamesManager.createCache();
        } catch (final Exception e) {
            RetroRPGCS.getInstance().handleError(e);
        }
        final var key = section + ":" + type;
        if (!NamesManager.containsKey(key)) {
            RetroRPGCS.getInstance().handleError(
                    new IllegalArgumentException("No such key " + key));
        }
        return NamesManager.getValue(key);
    }

    private static String getValue(final String key) {
        for (final String[] element : NamesManager.CACHE) {
            if (element[0].equals(key)) {
                return element[1];
            }
        }
        return null;
    }
}
