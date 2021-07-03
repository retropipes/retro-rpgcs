/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.battle.map;

import com.puttysoftware.retrorpgcs.prefs.PreferencesManager;

public class MapBattleViewingWindowManager {
    private static final int VIEWING_WINDOW_SIZE_MULTIPLIER = 1;
    private static final int VIEWING_WINDOW_SIZE_ADDITION = 0;

    public static int getViewingWindowSize() {
        return PreferencesManager.getViewingWindowSize()
                * MapBattleViewingWindowManager.VIEWING_WINDOW_SIZE_MULTIPLIER
                + MapBattleViewingWindowManager.VIEWING_WINDOW_SIZE_ADDITION;
    }

    // Fields
    private int oldLocX, oldLocY, locX, locY;

    // Constructors
    public MapBattleViewingWindowManager() {
        this.locX = 0;
        this.locY = 0;
        this.oldLocX = 0;
        this.oldLocY = 0;
    }

    public int getLowerRightViewingWindowLocationX() {
        return this.locX + MapBattleViewingWindowManager.getViewingWindowSize()
                - 1;
    }

    public int getLowerRightViewingWindowLocationY() {
        return this.locY + MapBattleViewingWindowManager.getViewingWindowSize()
                - 1;
    }

    // Methods
    public int getViewingWindowLocationX() {
        return this.locX;
    }

    public int getViewingWindowLocationY() {
        return this.locY;
    }

    public void offsetViewingWindowLocationX(final int val) {
        this.locX += val;
    }

    public void offsetViewingWindowLocationY(final int val) {
        this.locY += val;
    }

    public void restoreViewingWindow() {
        this.locX = this.oldLocX;
        this.locY = this.oldLocY;
    }

    public void saveViewingWindow() {
        this.oldLocX = this.locX;
        this.oldLocY = this.locY;
    }

    public void setViewingWindowCenterX(final int val) {
        this.locX = val
                - MapBattleViewingWindowManager.getViewingWindowSize() / 2;
    }

    public void setViewingWindowCenterY(final int val) {
        this.locY = val
                - MapBattleViewingWindowManager.getViewingWindowSize() / 2;
    }
}
