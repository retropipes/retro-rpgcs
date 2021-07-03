/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.items.combat.predefined;

import com.puttysoftware.retrorpgcs.battle.BattleTarget;
import com.puttysoftware.retrorpgcs.creatures.StatConstants;
import com.puttysoftware.retrorpgcs.effects.Effect;
import com.puttysoftware.retrorpgcs.items.combat.CombatItem;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;

public class Bomb extends CombatItem {
    public Bomb() {
	super("Bomb", 30, BattleTarget.ENEMY);
    }

    @Override
    protected void defineFields() {
	this.sound = SoundConstants.SOUND_EXPLODE;
	this.e = new Effect("Bomb", 1);
	this.e.setEffect(Effect.EFFECT_ADD, StatConstants.STAT_CURRENT_HP, -5);
	this.e.setScaleStat(StatConstants.STAT_LEVEL);
	this.e.setScaleFactor(0.75);
	this.e.setMessage(Effect.MESSAGE_INITIAL, "You throw a bomb at the enemy!");
	this.e.setMessage(Effect.MESSAGE_SUBSEQUENT, "The bomb goes BOOM, inflicting a little damage!");
    }
}
