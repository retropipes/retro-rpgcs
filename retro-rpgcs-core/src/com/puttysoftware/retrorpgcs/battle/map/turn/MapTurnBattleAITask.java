/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.battle.map.turn;

import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.battle.Battle;
import com.puttysoftware.retrorpgcs.prefs.PreferencesManager;

public class MapTurnBattleAITask extends Thread {
    // Fields
    private final Battle b;

    // Constructors
    public MapTurnBattleAITask(final Battle battle) {
        this.setName("Map AI Runner");
        this.b = battle;
    }

    @Override
    public void run() {
        try {
            this.aiWait();
            while (true) {
                this.b.executeNextAIAction();
                if (this.b.getLastAIActionResult()) {
                    // Delay, for animation purposes
                    try {
                        final int battleSpeed = PreferencesManager
                                .getBattleSpeed();
                        Thread.sleep(battleSpeed);
                    } catch (final InterruptedException i) {
                        // Ignore
                    }
                }
            }
        } catch (final Throwable t) {
            RetroRPGCS.getInstance().handleError(t);
        }
    }

    public synchronized void aiWait() {
        try {
            this.wait();
        } catch (final InterruptedException e) {
            // Ignore
        }
    }

    public synchronized void aiRun() {
        this.notify();
    }
}
