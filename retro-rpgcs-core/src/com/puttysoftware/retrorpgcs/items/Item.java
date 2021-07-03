/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.items;

import java.io.IOException;
import java.util.Objects;

import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class Item {
    protected static Item readItem(final XDataReader dr) throws IOException {
        final var itemName = dr.readString();
        if (itemName.equals("null")) {
            // Abort
            return null;
        }
        final var itemInitialUses = dr.readInt();
        final var itemWeightPerUse = dr.readInt();
        final var i = new Item(itemName, itemInitialUses, itemWeightPerUse);
        i.uses = dr.readInt();
        i.buyPrice = dr.readInt();
        i.sellPrice = dr.readInt();
        i.weight = dr.readInt();
        i.potency = dr.readInt();
        i.combatUsable = dr.readBoolean();
        return i;
    }

    // Properties
    private String name;
    private final int initialUses;
    private final int weightPerUse;
    private int uses;
    private int buyPrice;
    private int sellPrice;
    private int weight;
    private int potency;
    private boolean combatUsable;

    // Constructors
    public Item() {
        this.name = "Un-named Item";
        this.initialUses = 0;
        this.uses = 0;
        this.weightPerUse = 0;
        this.buyPrice = 0;
        this.sellPrice = 0;
        this.weight = 0;
        this.potency = 0;
        this.combatUsable = false;
    }

    public Item(final String itemName, final int itemInitialUses,
            final int itemWeightPerUse) {
        this.name = itemName;
        this.initialUses = itemInitialUses;
        this.uses = itemInitialUses;
        this.weightPerUse = itemWeightPerUse;
        this.buyPrice = 0;
        this.sellPrice = 0;
        this.weight = 0;
        this.potency = 0;
        this.combatUsable = false;
    }

    protected Item(final String iName, final Item i) {
        this.name = iName;
        this.initialUses = i.initialUses;
        this.uses = i.uses;
        this.weightPerUse = i.weightPerUse;
        this.buyPrice = i.buyPrice;
        this.sellPrice = i.sellPrice;
        this.weight = i.weight;
        this.potency = i.potency;
        this.combatUsable = false;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (this.getClass() != obj.getClass())) {
            return false;
        }
        final var other = (Item) obj;
        if (this.buyPrice != other.buyPrice) {
            return false;
        }
        if (this.combatUsable != other.combatUsable) {
            return false;
        }
        if (this.initialUses != other.initialUses) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.potency != other.potency) {
            return false;
        }
        if (this.sellPrice != other.sellPrice) {
            return false;
        }
        if (this.weight != other.weight) {
            return false;
        }
        if (this.weightPerUse != other.weightPerUse) {
            return false;
        }
        return true;
    }

    private final int getBaseWeight() {
        return this.weight;
    }

    public final int getBuyPrice() {
        return this.buyPrice;
    }

    public final int getEffectiveWeight() {
        return this.getBaseWeight() + this.getUses() * this.getWeightPerUse();
    }

    public final int getInitialUses() {
        return this.initialUses;
    }

    public String getName() {
        return this.name;
    }

    public final int getPotency() {
        return this.potency;
    }

    private final int getUses() {
        return this.uses;
    }

    public final int getWeightPerUse() {
        return this.weightPerUse;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.buyPrice, this.combatUsable, this.initialUses, this.name, this.potency,
                this.sellPrice, this.weight, this.weightPerUse);
    }

    public final boolean isCombatUsable() {
        return this.isUsable() && this.combatUsable;
    }

    private final boolean isUsable() {
        return this.uses > 0;
    }

    public final void setBuyPrice(final int newBuyPrice) {
        this.buyPrice = newBuyPrice;
    }

    public final void setCombatUsable(final boolean isCombatUsable) {
        this.combatUsable = isCombatUsable;
    }

    public final void setName(final String newName) {
        this.name = newName;
    }

    public final void setPotency(final int newPotency) {
        this.potency = newPotency;
    }

    public final void setSellPrice(final int newSellPrice) {
        this.sellPrice = newSellPrice;
    }

    // Methods
    @Override
    public String toString() {
        return this.name;
    }

    public final boolean use() {
        if (this.uses > 0) {
            this.uses--;
            return true;
        } else {
            return false;
        }
    }

    final void writeItem(final XDataWriter dw) throws IOException {
        dw.writeString(this.name);
        dw.writeInt(this.initialUses);
        dw.writeInt(this.weightPerUse);
        dw.writeInt(this.uses);
        dw.writeInt(this.buyPrice);
        dw.writeInt(this.sellPrice);
        dw.writeInt(this.weight);
        dw.writeInt(this.potency);
        dw.writeBoolean(this.combatUsable);
    }
}
