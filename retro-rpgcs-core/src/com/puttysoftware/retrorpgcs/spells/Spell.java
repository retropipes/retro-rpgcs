/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.spells;

import java.util.Objects;

import com.puttysoftware.retrorpgcs.battle.BattleTarget;
import com.puttysoftware.retrorpgcs.effects.Effect;

public class Spell {
    // Fields
    private final Effect effect;
    private final int cost;
    private final BattleTarget target;
    private final int soundEffect;

    // Constructors
    public Spell() {
        this.effect = null;
        this.cost = 0;
        this.target = null;
        this.soundEffect = -1;
    }

    public Spell(final Effect newEffect, final int newCost,
            final BattleTarget newTarget, final int sfx) {
        this.effect = newEffect;
        this.cost = newCost;
        this.target = newTarget;
        this.soundEffect = sfx;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || !(obj instanceof Spell)) {
            return false;
        }
        final var other = (Spell) obj;
        if (this.cost != other.cost) {
            return false;
        }
        if (!Objects.equals(this.effect, other.effect)) {
            return false;
        }
        if (this.soundEffect != other.soundEffect) {
            return false;
        }
        if (this.target != other.target) {
            return false;
        }
        return true;
    }

    public int getCost() {
        return this.cost;
    }

    public Effect getEffect() {
        return this.effect;
    }

    int getSound() {
        return this.soundEffect;
    }

    BattleTarget getTarget() {
        return this.target;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.cost, this.effect, this.soundEffect, this.target);
    }
}
