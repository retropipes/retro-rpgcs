/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.ai.window;

import com.puttysoftware.retrorpgcs.prefs.PreferencesManager;

public final class WindowAIPicker {
    // Methods
    public static WindowAI getNextRoutine() {
	final int difficulty = PreferencesManager.getGameDifficulty();
	if (difficulty == PreferencesManager.DIFFICULTY_VERY_EASY) {
	    return new VeryEasyWindowAI();
	} else if (difficulty == PreferencesManager.DIFFICULTY_EASY) {
	    return new EasyWindowAI();
	} else if (difficulty == PreferencesManager.DIFFICULTY_NORMAL) {
	    return new NormalWindowAI();
	} else if (difficulty == PreferencesManager.DIFFICULTY_HARD) {
	    return new HardWindowAI();
	} else if (difficulty == PreferencesManager.DIFFICULTY_VERY_HARD) {
	    return new VeryHardWindowAI();
	} else {
	    return new NormalWindowAI();
	}
    }
}
