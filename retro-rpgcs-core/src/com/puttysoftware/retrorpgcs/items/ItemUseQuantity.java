/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.items;

import java.util.Objects;

class ItemUseQuantity {
    // Fields
    private final Item item;
    private int uses;
    private int quantity;

    // Constructors
    public ItemUseQuantity() {
        this.item = null;
        this.quantity = 0;
        this.uses = 0;
    }

    ItemUseQuantity(final Item i, final int q, final int u) {
        this.item = i;
        this.quantity = q;
        this.uses = u;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || !(obj instanceof ItemUseQuantity)) {
            return false;
        }
        final var other = (ItemUseQuantity) obj;
        if (!Objects.equals(this.item, other.item)) {
            return false;
        }
        if (this.quantity != other.quantity) {
            return false;
        }
        if (this.uses != other.uses) {
            return false;
        }
        return true;
    }

    // Methods
    Item getItem() {
        return this.item;
    }

    int getQuantity() {
        return this.quantity;
    }

    int getUses() {
        return this.uses;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.item, this.quantity, this.uses);
    }

    void incrementQuantity() {
        if (this.quantity >= 0) {
            this.quantity++;
        }
    }

    void setQuantity(final int newQ) {
        this.quantity = newQ;
    }

    void setUses(final int newU) {
        this.uses = newU;
    }

    @Override
    public String toString() {
        if (this.quantity < 0) {
            return this.item + " - Quantity: Infinite - Uses: " + this.uses;
        } else {
            return this.item + " - Quantity: " + this.quantity + " - Uses: "
                    + this.uses;
        }
    }
}
