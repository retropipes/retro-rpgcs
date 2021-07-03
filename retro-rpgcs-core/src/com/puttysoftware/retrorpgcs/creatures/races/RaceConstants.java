/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.creatures.races;

import java.io.IOException;
import java.util.ArrayList;

import com.puttysoftware.fileutils.ResourceStreamReader;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.datamanagers.RaceDataManager;

public class RaceConstants {
    public static final int RACE_ATTRIBUTE_STRENGTH_PER_LEVEL = 0;
    public static final int RACE_ATTRIBUTE_BLOCK_PER_LEVEL = 1;
    public static final int RACE_ATTRIBUTE_AGILITY_PER_LEVEL = 2;
    public static final int RACE_ATTRIBUTE_VITALITY_PER_LEVEL = 3;
    public static final int RACE_ATTRIBUTE_INTELLIGENCE_PER_LEVEL = 4;
    public static final int RACE_ATTRIBUTE_LUCK_PER_LEVEL = 5;
    public static final int RACE_ATTRIBUTE_COUNT = 6;
    private static int RACES_COUNT = -1;
    private static String[] RACE_NAMES = {};
    private static boolean INITED = false;

    public static final String getRaceName(final int index) {
        return RaceConstants.RACE_NAMES[index];
    }

    public static String[] getRaceNames() {
        return RaceConstants.RACE_NAMES;
    }

    static final int getRacesCount() {
        return RaceConstants.RACES_COUNT;
    }

    static void initRaces() {
        if (!RaceConstants.INITED) {
            try (final var rsr = new ResourceStreamReader(
                    RaceDataManager.class.getResourceAsStream(
                            "/com/puttysoftware/retrorpgcs/resources/data/race/catalog.txt"))) {
                // Fetch data
                final var tempNames = new ArrayList<String>();
                var input = "";
                while (input != null) {
                    input = rsr.readString();
                    if (input != null) {
                        tempNames.add(input);
                    }
                }
                RaceConstants.RACE_NAMES = tempNames
                        .toArray(new String[tempNames.size()]);
                RaceConstants.RACES_COUNT = RaceConstants.RACE_NAMES.length;
                RaceConstants.INITED = true;
            } catch (final IOException ioe) {
                RetroRPGCS.getInstance().handleError(ioe);
            }
        }
    }

    static boolean racesReady() {
        return RaceConstants.INITED;
    }
}
