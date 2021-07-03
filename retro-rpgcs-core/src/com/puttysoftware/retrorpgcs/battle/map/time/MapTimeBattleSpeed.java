/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.battle.map.time;

import com.puttysoftware.retrorpgcs.prefs.PreferencesManager;

class MapTimeBattleSpeed {
    // Constants
    private static int SPEED_FACTOR = 20;

    // Constructor
    private MapTimeBattleSpeed() {
        // Do nothing
    }

    // Method
    static int getSpeed() {
        return PreferencesManager.getBattleSpeed()
                / MapTimeBattleSpeed.SPEED_FACTOR;
    }
}