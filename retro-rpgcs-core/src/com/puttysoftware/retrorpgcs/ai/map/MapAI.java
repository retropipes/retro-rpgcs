/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.ai.map;

import java.util.Objects;

import com.puttysoftware.retrorpgcs.items.combat.CombatItem;
import com.puttysoftware.retrorpgcs.spells.Spell;

public abstract class MapAI {
    public static final int ACTION_MOVE = 0;
    public static final int ACTION_CAST_SPELL = 1;
    public static final int ACTION_STEAL = 2;
    public static final int ACTION_DRAIN = 3;
    public static final int ACTION_USE_ITEM = 4;
    static final int ACTION_END_TURN = 5;
    // Fields
    protected Spell spell;
    private final CombatItem item;
    protected int moveX;
    protected int moveY;
    protected boolean lastResult;

    // Constructor
    protected MapAI() {
        this.spell = null;
        this.item = null;
        this.moveX = 0;
        this.moveY = 0;
        this.lastResult = true;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || !(obj instanceof MapAI)) {
            return false;
        }
        final var other = (MapAI) obj;
        if (!Objects.equals(this.item, other.item)) {
            return false;
        }
        if (this.lastResult != other.lastResult) {
            return false;
        }
        if (this.moveX != other.moveX) {
            return false;
        }
        if (this.moveY != other.moveY) {
            return false;
        }
        if (!Objects.equals(this.spell, other.spell)) {
            return false;
        }
        return true;
    }

    public final CombatItem getItemToUse() {
        return this.item;
    }

    public final int getMoveX() {
        return this.moveX;
    }

    public final int getMoveY() {
        return this.moveY;
    }

    // Methods
    public abstract int getNextAction(MapAIContext ac);

    public final Spell getSpellToCast() {
        return this.spell;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.item, this.lastResult, this.moveX, this.moveY, this.spell);
    }

    public void newRoundHook() {
        // Do nothing
    }

    public final void setLastResult(final boolean res) {
        this.lastResult = res;
    }
}
