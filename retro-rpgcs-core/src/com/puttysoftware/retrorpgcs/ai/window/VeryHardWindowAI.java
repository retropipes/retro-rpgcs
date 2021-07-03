/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.ai.window;

import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.retrorpgcs.creatures.Creature;

public class VeryHardWindowAI extends WindowAI {
    private static final int CAST_SPELL_CHANCE = 80;
    private static final int STEAL_CHANCE = 16;
    private static final int DRAIN_CHANCE = 80;
    private static final int HEAL_THRESHOLD = 60;
    private static final int FLEE_CHANCE = 1;
    // Fields
    private int[] roundsRemaining;

    // Constructors
    public VeryHardWindowAI() {
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
        } else if (CommonWindowAIParts.check(VeryHardWindowAI.STEAL_CHANCE)) {
            // Steal
            return WindowAI.ACTION_STEAL;
        } else if (CommonWindowAIParts.check(VeryHardWindowAI.DRAIN_CHANCE)) {
            // Drain MP
            return WindowAI.ACTION_DRAIN;
        } else if (CommonWindowAIParts.check(VeryHardWindowAI.FLEE_CHANCE)) {
            // Flee
            return WindowAI.ACTION_FLEE;
        } else {
            // Something hostile is nearby, so attack it
            return WindowAI.ACTION_ATTACK;
        }
    }

    @Override
    public void newRoundHook() {
        // Decrement effect counters
        for (var z = 0; z < this.roundsRemaining.length; z++) {
            if (this.roundsRemaining[z] > 0) {
                this.roundsRemaining[z]--;
            }
        }
    }

    private boolean spellCheck(final Creature c) {
        final var random = new RandomRange(1, 100);
        final var chance = random.generate();
        if (chance <= VeryHardWindowAI.CAST_SPELL_CHANCE) {
            final var maxIndex = CommonWindowAIParts.getMaxCastIndex(c);
            if (maxIndex > -1) {
                // Select a random spell to cast
                final var randomSpell = new RandomRange(0, maxIndex);
                final var randomSpellID = randomSpell.generate();
                // Healing spell was selected - is healing needed?
                if ((randomSpellID == CommonWindowAIParts.SPELL_INDEX_HEAL)
                        && (c.getCurrentHP() > c.getMaximumHP()
                                * VeryHardWindowAI.HEAL_THRESHOLD / 100)) {
                    // Do not need healing
                    return false;
                }
                if (this.roundsRemaining[randomSpellID] == 0) {
                    this.spell = c.getSpellBook().getSpellByID(randomSpellID);
                    this.roundsRemaining[randomSpellID] = this.spell.getEffect()
                            .getInitialRounds();
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
}
