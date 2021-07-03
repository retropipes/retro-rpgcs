/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.items.combat.predefined;

import com.puttysoftware.retrorpgcs.battle.BattleTarget;
import com.puttysoftware.retrorpgcs.creatures.StatConstants;
import com.puttysoftware.retrorpgcs.effects.Effect;
import com.puttysoftware.retrorpgcs.items.combat.CombatItem;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;

public class Bolt extends CombatItem {
    public Bolt() {
	super("Bolt", 100, BattleTarget.ENEMY);
    }

    @Override
    protected void defineFields() {
	this.sound = SoundConstants.SOUND_BOLT;
	this.e = new Effect("Bolt", 1);
	this.e.setEffect(Effect.EFFECT_ADD, StatConstants.STAT_CURRENT_HP, -4);
	this.e.setScaleStat(StatConstants.STAT_LEVEL);
	this.e.setScaleFactor(1.0);
	this.e.setMessage(Effect.MESSAGE_INITIAL, "You throw a bolt at the enemy!");
	this.e.setMessage(Effect.MESSAGE_SUBSEQUENT, "The bolt ZAPS the enemy, dealing damage!");
    }
}
