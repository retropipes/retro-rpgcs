/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.ai.window;

import com.puttysoftware.retrorpgcs.creatures.Creature;
import com.puttysoftware.retrorpgcs.spells.Spell;

public abstract class WindowAI {
    public static final int ACTION_ATTACK = 0;
    public static final int ACTION_FLEE = 1;
    public static final int ACTION_CAST_SPELL = 2;
    public static final int ACTION_STEAL = 3;
    public static final int ACTION_DRAIN = 4;
    public static final int ACTION_USE_ITEM = 5;
    // Fields
    protected Spell spell;

    // Methods
    public abstract int getNextAction(Creature c);

    public final Spell getSpellToCast() {
        return this.spell;
    }

    public void newRoundHook() {
        // Do nothing
    }
}
