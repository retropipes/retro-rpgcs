/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.battle.map.time;

import com.puttysoftware.retrorpgcs.prefs.PreferencesManager;

class MapTimeBattleSpeed {
    // Constants
    private static int SPEED_FACTOR = 20;

    // Method
    static int getSpeed() {
        return PreferencesManager.getBattleSpeed()
                / MapTimeBattleSpeed.SPEED_FACTOR;
    }

    // Constructor
    private MapTimeBattleSpeed() {
        // Do nothing
    }
}