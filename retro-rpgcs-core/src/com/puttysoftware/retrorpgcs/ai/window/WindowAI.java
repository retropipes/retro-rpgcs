/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.ai.window;

import com.puttysoftware.retrorpgcs.creatures.Creature;
import com.puttysoftware.retrorpgcs.spells.Spell;

public abstract class WindowAI {
    // Fields
    protected Spell spell;
    public static final int ACTION_ATTACK = 0;
    public static final int ACTION_FLEE = 1;
    public static final int ACTION_CAST_SPELL = 2;
    public static final int ACTION_STEAL = 3;
    public static final int ACTION_DRAIN = 4;
    public static final int ACTION_USE_ITEM = 5;

    // Methods
    public abstract int getNextAction(Creature c);

    public void newRoundHook() {
	// Do nothing
    }

    public final Spell getSpellToCast() {
	return this.spell;
    }
}
