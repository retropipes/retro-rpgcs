/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.ai.window;

import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.retrorpgcs.creatures.Creature;

public class HardWindowAI extends WindowAI {
    // Fields
    private int[] roundsRemaining;
    private static final int CAST_SPELL_CHANCE = 40;
    private static final int STEAL_CHANCE = 8;
    private static final int DRAIN_CHANCE = 40;
    private static final int HEAL_THRESHOLD = 40;
    private static final int FLEE_CHANCE = 5;

    // Constructors
    public HardWindowAI() {
	// Do nothing
    }

    @Override
    public int getNextAction(final Creature c) {
	if (this.roundsRemaining == null) {
	    this.roundsRemaining = new int[c.getSpellBook().getSpellCount()];
	}
	if (this.spellCheck(c)) {
	    // Cast a spell
	    return WindowAI.ACTION_CAST_SPELL;
	} else if (CommonWindowAIParts.check(HardWindowAI.STEAL_CHANCE)) {
	    // Steal
	    return WindowAI.ACTION_STEAL;
	} else if (CommonWindowAIParts.check(HardWindowAI.DRAIN_CHANCE)) {
	    // Drain MP
	    return WindowAI.ACTION_DRAIN;
	} else if (CommonWindowAIParts.check(HardWindowAI.FLEE_CHANCE)) {
	    // Flee
	    return WindowAI.ACTION_FLEE;
	} else {
	    // Something hostile is nearby, so attack it
	    return WindowAI.ACTION_ATTACK;
	}
    }

    private boolean spellCheck(final Creature c) {
	final RandomRange random = new RandomRange(1, 100);
	final int chance = random.generate();
	if (chance <= HardWindowAI.CAST_SPELL_CHANCE) {
	    final int maxIndex = CommonWindowAIParts.getMaxCastIndex(c);
	    if (maxIndex > -1) {
		// Select a random spell to cast
		final RandomRange randomSpell = new RandomRange(0, maxIndex);
		final int randomSpellID = randomSpell.generate();
		if (randomSpellID == CommonWindowAIParts.SPELL_INDEX_HEAL) {
		    // Healing spell was selected - is healing needed?
		    if (c.getCurrentHP() > c.getMaximumHP() * HardWindowAI.HEAL_THRESHOLD / 100) {
			// Do not need healing
			return false;
		    }
		}
		if (this.roundsRemaining[randomSpellID] == 0) {
		    this.spell = c.getSpellBook().getSpellByID(randomSpellID);
		    this.roundsRemaining[randomSpellID] = this.spell.getEffect().getInitialRounds();
		    return true;
		} else {
		    // Spell selected already active
		    return false;
		}
	    } else {
		// Not enough MP to cast anything
		return false;
	    }
	} else {
	    // Not casting a spell
	    return false;
	}
    }

    @Override
    public void newRoundHook() {
	// Decrement effect counters
	for (int z = 0; z < this.roundsRemaining.length; z++) {
	    if (this.roundsRemaining[z] > 0) {
		this.roundsRemaining[z]--;
	    }
	}
    }
}
