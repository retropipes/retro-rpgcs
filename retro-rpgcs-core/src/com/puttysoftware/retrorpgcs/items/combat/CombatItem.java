/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.items.combat;

import java.util.Objects;

import com.puttysoftware.retrorpgcs.battle.BattleTarget;
import com.puttysoftware.retrorpgcs.effects.Effect;
import com.puttysoftware.retrorpgcs.items.Item;

public class CombatItem extends Item {
    // Fields
    private final BattleTarget target;
    protected Effect e;
    protected int sound;

    // Constructors
    public CombatItem() {
        this.target = null;
    }

    public CombatItem(final String itemName, final int itemBuyPrice,
            final BattleTarget itemTarget) {
        super(itemName, 1, 0);
        this.setCombatUsable(true);
        this.setBuyPrice(itemBuyPrice);
        this.target = itemTarget;
        this.defineFields();
    }

    protected void defineFields() {
        // Do nothing
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || !(obj instanceof CombatItem)) {
            return false;
        }
        final var other = (CombatItem) obj;
        if (!Objects.equals(this.e, other.e)) {
            return false;
        }
        if (this.sound != other.sound) {
            return false;
        }
        if (this.target != other.target) {
            return false;
        }
        return true;
    }

    final Effect getEffect() {
        return this.e;
    }

    final int getSound() {
        return this.sound;
    }

    // Methods
    final BattleTarget getTarget() {
        return this.target;
    }

    @Override
    public int hashCode() {
        final var prime = 31;
        var result = super.hashCode();
        result = prime * result + (this.e == null ? 0 : this.e.hashCode());
        result = prime * result + this.sound;
        return prime * result
                + (this.target == null ? 0 : this.target.hashCode());
    }
}