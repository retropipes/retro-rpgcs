/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.creatures.genders;

import com.puttysoftware.diane.gui.CommonDialogs;

public class GenderManager {
    private static boolean CACHE_CREATED = false;
    private static Gender[] CACHE;

    public static Gender getGender(final int genderID) {
        if (!GenderManager.CACHE_CREATED) {
            // Create cache
            GenderManager.CACHE = new Gender[GenderConstants.GENDERS_COUNT];
            for (var x = 0; x < GenderConstants.GENDERS_COUNT; x++) {
                GenderManager.CACHE[x] = new Gender(x);
            }
            GenderManager.CACHE_CREATED = true;
        }
        return GenderManager.CACHE[genderID];
    }

    public static Gender selectGender() {
        final var names = GenderConstants.GENDER_NAMES;
        String dialogResult;
        dialogResult = CommonDialogs.showInputDialog("Select a Gender",
                "Create Character", names, names[0]);
        if (dialogResult != null) {
            int index;
            for (index = 0; index < names.length; index++) {
                if (dialogResult.equals(names[index])) {
                    break;
                }
            }
            return GenderManager.getGender(index);
        } else {
            return null;
        }
    }
}
