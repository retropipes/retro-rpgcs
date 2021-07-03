/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.ai.window;

import com.puttysoftware.retrorpgcs.prefs.PreferencesManager;

public final class WindowAIPicker {
    // Methods
    public static WindowAI getNextRoutine() {
        final var difficulty = PreferencesManager.getGameDifficulty();
        switch (difficulty) {
        case PreferencesManager.DIFFICULTY_VERY_EASY:
            return new VeryEasyWindowAI();
        case PreferencesManager.DIFFICULTY_EASY:
            return new EasyWindowAI();
        case PreferencesManager.DIFFICULTY_NORMAL:
            return new NormalWindowAI();
        case PreferencesManager.DIFFICULTY_HARD:
            return new HardWindowAI();
        case PreferencesManager.DIFFICULTY_VERY_HARD:
            return new VeryHardWindowAI();
        default:
            return new NormalWindowAI();
        }
    }
}
