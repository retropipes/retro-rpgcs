/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.battle.window.time;

import com.puttysoftware.retrorpgcs.prefs.PreferencesManager;

class WindowTimeBattleSpeed {
    // Constants
    private static int SPEED_FACTOR = 10;

    // Method
    static int getSpeed() {
        return PreferencesManager.getBattleSpeed()
                / WindowTimeBattleSpeed.SPEED_FACTOR;
    }

    // Constructor
    private WindowTimeBattleSpeed() {
        // Do nothing
    }
}