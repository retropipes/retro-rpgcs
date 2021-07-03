/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.items.combat.predefined;

import com.puttysoftware.retrorpgcs.battle.BattleTarget;
import com.puttysoftware.retrorpgcs.creatures.StatConstants;
import com.puttysoftware.retrorpgcs.effects.Effect;
import com.puttysoftware.retrorpgcs.items.combat.CombatItem;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;

public class Fireball extends CombatItem {
    public Fireball() {
	super("Fireball", 500, BattleTarget.ENEMY);
    }

    @Override
    protected void defineFields() {
	this.sound = SoundConstants.SOUND_BOLT;
	this.e = new Effect("Fireball", 1);
	this.e.setEffect(Effect.EFFECT_ADD, StatConstants.STAT_CURRENT_HP, -3);
	this.e.setScaleStat(StatConstants.STAT_LEVEL);
	this.e.setScaleFactor(1.5);
	this.e.setMessage(Effect.MESSAGE_INITIAL, "You throw a fireball at the enemy!");
	this.e.setMessage(Effect.MESSAGE_SUBSEQUENT, "The fireball sears the enemy badly, dealing LOTS of damage!");
    }
}
