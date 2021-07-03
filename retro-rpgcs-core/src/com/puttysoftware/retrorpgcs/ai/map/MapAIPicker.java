/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.ai.map;

import com.puttysoftware.retrorpgcs.prefs.PreferencesManager;

public final class MapAIPicker {
    // Constructors
    private MapAIPicker() {
        // Do nothing
    }

    // Methods
    public static MapAI getNextRoutine() {
        final int difficulty = PreferencesManager.getGameDifficulty();
        if (difficulty == PreferencesManager.DIFFICULTY_VERY_EASY) {
            return new VeryEasyMapAI();
        } else if (difficulty == PreferencesManager.DIFFICULTY_EASY) {
            return new EasyMapAI();
        } else if (difficulty == PreferencesManager.DIFFICULTY_NORMAL) {
            return new NormalMapAI();
        } else if (difficulty == PreferencesManager.DIFFICULTY_HARD) {
            return new HardMapAI();
        } else if (difficulty == PreferencesManager.DIFFICULTY_VERY_HARD) {
            return new VeryHardMapAI();
        } else {
            return new NormalMapAI();
        }
    }
}
