/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.creatures.faiths;

import java.awt.Color;
import java.util.Arrays;
import java.util.Objects;

import com.puttysoftware.retrorpgcs.datamanagers.FaithDataManager;
import com.puttysoftware.retrorpgcs.descriptionmanagers.FaithDescriptionManager;

public final class Faith {
    private final int faithID;
    private final double[] multipliers;
    private final String desc;

    Faith(final int fid) {
        this.multipliers = FaithDataManager.getFaithData(fid);
        this.desc = FaithDescriptionManager.getFaithDescription(fid);
        this.faithID = fid;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || !(obj instanceof Faith)) {
            return false;
        }
        final var other = (Faith) obj;
        if (this.faithID != other.faithID) {
            return false;
        }
        if (!Arrays.equals(this.multipliers, other.multipliers)) {
            return false;
        }
        return true;
    }

    public Color getColor() {
        return FaithConstants.getFaithColor(this.faithID);
    }

    public String getDescription() {
        return this.desc;
    }

    public int getFaithID() {
        return this.faithID;
    }

    public double getMultiplierForOtherFaith(final int fid) {
        return this.multipliers[fid];
    }

    public String getName() {
        return FaithConstants.getFaithName(this.faithID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.faithID, Arrays.hashCode(this.multipliers));
    }
}
