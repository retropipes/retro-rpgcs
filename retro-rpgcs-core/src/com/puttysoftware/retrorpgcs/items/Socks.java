/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.items;

import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.retrorpgcs.creatures.Creature;

public final class Socks extends Equipment {
    private static final int STEP_ACTION_HEAL = 1;
    private static final int STEP_ACTION_REGENERATE = 2;
    private static final int STEP_ACTION_XP = 3;
    private static final int STEP_ACTION_MONEY = 4;
    // Fields
    private final int actionType;
    private final int actionAmount;

    // Constructors
    Socks(final String itemName, final int price, final int action,
            final int amount) {
        super(itemName, price);
        this.actionType = action;
        this.actionAmount = amount;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || !(obj instanceof Socks)) {
            return false;
        }
        final var other = (Socks) obj;
        if (this.actionAmount != other.actionAmount) {
            return false;
        }
        if (this.actionType != other.actionType) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final var prime = 31;
        var result = super.hashCode();
        result = prime * result + this.actionAmount;
        return prime * result + this.actionType;
    }

    // Methods
    void stepAction(final Creature wearer) {
        switch (this.actionType) {
        case Socks.STEP_ACTION_HEAL:
            wearer.heal(this.actionAmount);
            break;
        case Socks.STEP_ACTION_REGENERATE:
            wearer.regenerate(this.actionAmount);
            break;
        case Socks.STEP_ACTION_XP:
            wearer.offsetExperience(this.actionAmount);
            if (wearer.checkLevelUp()) {
                wearer.levelUp();
                CommonDialogs.showDialog(wearer.getName() + " reached level "
                        + wearer.getLevel() + ".");
            }
            break;
        case Socks.STEP_ACTION_MONEY:
            wearer.offsetGold(this.actionAmount);
            break;
        default:
            break;
        }
    }
}
