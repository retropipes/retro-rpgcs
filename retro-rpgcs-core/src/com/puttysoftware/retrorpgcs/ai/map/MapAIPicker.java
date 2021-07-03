/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.ai.map;

import com.puttysoftware.retrorpgcs.prefs.PreferencesManager;

public final class MapAIPicker {
    // Methods
    public static MapAI getNextRoutine() {
        final var difficulty = PreferencesManager.getGameDifficulty();
        switch (difficulty) {
        case PreferencesManager.DIFFICULTY_VERY_EASY:
            return new VeryEasyMapAI();
        case PreferencesManager.DIFFICULTY_EASY:
            return new EasyMapAI();
        case PreferencesManager.DIFFICULTY_NORMAL:
            return new NormalMapAI();
        case PreferencesManager.DIFFICULTY_HARD:
            return new HardMapAI();
        case PreferencesManager.DIFFICULTY_VERY_HARD:
            return new VeryHardMapAI();
        default:
            return new NormalMapAI();
        }
    }

    // Constructors
    private MapAIPicker() {
        // Do nothing
    }
}
