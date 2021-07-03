/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.creatures.personalities;

import javax.swing.JFrame;

import com.puttysoftware.retrorpgcs.creatures.party.PartyManager;

public class PersonalityManager {
    private static boolean CACHE_CREATED = false;
    private static Personality[] CACHE;
    private static String[] DESC_CACHE;

    private static void createCache() {
        if (!PersonalityManager.CACHE_CREATED) {
            if (!PersonalityConstants.personalitiesReady()) {
                PersonalityConstants.initPersonalities();
            }
            // Create cache
            if (!PersonalityConstants.personalitiesReady()) {
                PersonalityConstants.initPersonalities();
            }
            final var pc = PersonalityConstants.getPersonalitiesCount();
            PersonalityManager.CACHE = new Personality[pc];
            PersonalityManager.DESC_CACHE = new String[pc];
            for (var x = 0; x < pc; x++) {
                PersonalityManager.CACHE[x] = new Personality(x);
                PersonalityManager.DESC_CACHE[x] = PersonalityManager.CACHE[x]
                        .getDescription();
            }
            PersonalityManager.CACHE_CREATED = true;
        }
    }

    public static Personality getPersonality(final int personalityID) {
        PersonalityManager.createCache();
        return PersonalityManager.CACHE[personalityID];
    }

    public static Personality selectPersonality(final JFrame owner) {
        PersonalityManager.createCache();
        final var names = PersonalityConstants.getPersonalityNames();
        String dialogResult;
        dialogResult = PartyManager.showCreationDialog(owner,
                "Select a Personality", "Create Character", names,
                PersonalityManager.DESC_CACHE);
        if (dialogResult != null) {
            int index;
            for (index = 0; index < names.length; index++) {
                if (dialogResult.equals(names[index])) {
                    break;
                }
            }
            return PersonalityManager.getPersonality(index);
        } else {
            return null;
        }
    }
}
