/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.creatures.personalities;

import java.util.Arrays;
import java.util.Objects;

import com.puttysoftware.retrorpgcs.datamanagers.PersonalityDataManager;
import com.puttysoftware.retrorpgcs.descriptionmanagers.PersonalityDescriptionManager;

public final class Personality {
    private final int personalityID;
    private final double[] data;
    private final String desc;

    Personality(final int pid) {
        this.data = PersonalityDataManager.getPersonalityData(pid);
        this.desc = PersonalityDescriptionManager
                .getPersonalityDescription(pid);
        this.personalityID = pid;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || !(obj instanceof Personality)) {
            return false;
        }
        final var other = (Personality) obj;
        if (this.personalityID != other.personalityID) {
            return false;
        }
        if (!Arrays.equals(this.data, other.data)) {
            return false;
        }
        return true;
    }

    public double getAttribute(final int aid) {
        return this.data[aid];
    }

    public String getDescription() {
        return this.desc;
    }

    public int getPersonalityID() {
        return this.personalityID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.personalityID, Arrays.hashCode(this.data));
    }
}
