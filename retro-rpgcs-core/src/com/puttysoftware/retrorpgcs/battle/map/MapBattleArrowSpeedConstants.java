/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.battle.map;

import com.puttysoftware.retrorpgcs.prefs.PreferencesManager;

public class MapBattleArrowSpeedConstants {
    // Constants
    private static int ARROW_SPEED_FACTOR = 8;

    // Constructor
    private MapBattleArrowSpeedConstants() {
        // Do nothing
    }

    // Method
    public static int getArrowSpeed() {
        return PreferencesManager.getBattleSpeed()
                / MapBattleArrowSpeedConstants.ARROW_SPEED_FACTOR;
    }
}