/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.creatures.monsters;

import java.util.Objects;

import com.puttysoftware.retrorpgcs.creatures.faiths.Faith;

public class Element {
    // Fields
    private final double transformRed;
    private final double transformGreen;
    private final double transformBlue;
    private final String name;
    private final Faith faith;

    // Constructor
    public Element(final Faith f) {
        this.faith = f;
        this.transformRed = f.getColor().getRed() / 256.0;
        this.transformGreen = f.getColor().getGreen() / 256.0;
        this.transformBlue = f.getColor().getBlue() / 256.0;
        this.name = f.getName();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || !(obj instanceof Element)) {
            return false;
        }
        final var other = (Element) obj;
        if (!Objects.equals(this.faith, other.faith)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (Double.doubleToLongBits(this.transformBlue) != Double
                .doubleToLongBits(other.transformBlue)) {
            return false;
        }
        if (Double.doubleToLongBits(this.transformGreen) != Double
                .doubleToLongBits(other.transformGreen)) {
            return false;
        }
        if (Double.doubleToLongBits(this.transformRed) != Double
                .doubleToLongBits(other.transformRed)) {
            return false;
        }
        return true;
    }

    public Faith getFaith() {
        return this.faith;
    }

    // Methods
    public String getName() {
        return this.name;
    }

    @Override
    public int hashCode() {
        final var prime = 31;
        var result = 1;
        result = prime * result
                + (this.faith == null ? 0 : this.faith.hashCode());
        result = prime * result
                + (this.name == null ? 0 : this.name.hashCode());
        long temp;
        temp = Double.doubleToLongBits(this.transformBlue);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.transformGreen);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(this.transformRed);
        return prime * result + (int) (temp ^ temp >>> 32);
    }
}
