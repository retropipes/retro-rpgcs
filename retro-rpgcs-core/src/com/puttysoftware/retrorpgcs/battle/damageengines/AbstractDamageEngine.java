/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.battle.damageengines;

import com.puttysoftware.retrorpgcs.creatures.Creature;
import com.puttysoftware.retrorpgcs.prefs.PreferencesManager;

public abstract class AbstractDamageEngine {
    public static AbstractDamageEngine getEnemyInstance() {
        final var difficulty = PreferencesManager.getGameDifficulty();
        switch (difficulty) {
        case PreferencesManager.DIFFICULTY_VERY_EASY:
            return new VeryHardDamageEngine();
        case PreferencesManager.DIFFICULTY_EASY:
            return new HardDamageEngine();
        case PreferencesManager.DIFFICULTY_NORMAL:
            return new NormalDamageEngine();
        case PreferencesManager.DIFFICULTY_HARD:
            return new EasyDamageEngine();
        case PreferencesManager.DIFFICULTY_VERY_HARD:
            return new VeryEasyDamageEngine();
        default:
            return new NormalDamageEngine();
        }
    }

    public static AbstractDamageEngine getPlayerInstance() {
        final var difficulty = PreferencesManager.getGameDifficulty();
        switch (difficulty) {
        case PreferencesManager.DIFFICULTY_VERY_EASY:
            return new VeryEasyDamageEngine();
        case PreferencesManager.DIFFICULTY_EASY:
            return new EasyDamageEngine();
        case PreferencesManager.DIFFICULTY_NORMAL:
            return new NormalDamageEngine();
        case PreferencesManager.DIFFICULTY_HARD:
            return new HardDamageEngine();
        case PreferencesManager.DIFFICULTY_VERY_HARD:
            return new VeryHardDamageEngine();
        default:
            return new NormalDamageEngine();
        }
    }

    // Methods
    public abstract int computeDamage(Creature enemy, Creature acting);

    public abstract boolean enemyDodged();

    public abstract boolean weaponCrit();

    public abstract boolean weaponFumble();

    public abstract boolean weaponMissed();

    public abstract boolean weaponPierce();
}
