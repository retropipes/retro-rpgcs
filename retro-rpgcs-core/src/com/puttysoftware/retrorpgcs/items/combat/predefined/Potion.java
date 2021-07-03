/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.items.combat.predefined;

import com.puttysoftware.retrorpgcs.battle.BattleTarget;
import com.puttysoftware.retrorpgcs.creatures.StatConstants;
import com.puttysoftware.retrorpgcs.effects.Effect;
import com.puttysoftware.retrorpgcs.items.combat.CombatItem;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;

public class Potion extends CombatItem {
    public Potion() {
	super("Potion", 250, BattleTarget.SELF);
    }

    @Override
    protected void defineFields() {
	this.sound = SoundConstants.SOUND_HEAL;
	this.e = new Effect("Potion", 1);
	this.e.setEffect(Effect.EFFECT_ADD, StatConstants.STAT_CURRENT_HP, 5);
	this.e.setScaleStat(StatConstants.STAT_LEVEL);
	this.e.setScaleFactor(1.25);
	this.e.setMessage(Effect.MESSAGE_INITIAL, "You drink a healing potion!");
	this.e.setMessage(Effect.MESSAGE_SUBSEQUENT, "You feel better!");
    }
}
