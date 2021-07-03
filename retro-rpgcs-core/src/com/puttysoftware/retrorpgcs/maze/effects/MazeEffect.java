/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.effects;

import java.util.Objects;

public class MazeEffect {
    public static final int ROUNDS_INFINITE = -1;
    // Fields
    protected String name;
    protected int rounds;
    protected int initialRounds;

    // Constructor
    public MazeEffect(final String effectName, final int newRounds) {
        this.name = effectName;
        this.rounds = newRounds;
        this.initialRounds = newRounds;
    }

    public boolean areRoundsInfinite() {
        return this.rounds == MazeEffect.ROUNDS_INFINITE;
    }

    public void customExtendLogic() {
        // Do nothing
    }

    public void customTerminateLogic() {
        // Do nothing
    }

    public void deactivateEffect() {
        this.rounds = 0;
        this.customTerminateLogic();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || !(obj instanceof MazeEffect)) {
            return false;
        }
        final var other = (MazeEffect) obj;
        if (this.initialRounds != other.initialRounds) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (this.rounds != other.rounds) {
            return false;
        }
        return true;
    }

    public void extendEffect(final int additionalRounds) {
        this.customExtendLogic();
        this.rounds += additionalRounds;
    }

    public String getEffectString() {
        if (this.name.equals("")) {
            return "";
        } else if (this.areRoundsInfinite()) {
            return this.name;
        } else {
            return this.name + " (" + this.rounds + " Steps Left)";
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.initialRounds, this.name, this.rounds);
    }

    public boolean isActive() {
        if (this.areRoundsInfinite()) {
            return true;
        } else {
            return this.rounds > 0;
        }
    }

    public int modifyMove1(final int arg) {
        return arg;
    }

    public int[] modifyMove2(final int... arg) {
        return arg;
    }

    public void useEffect() {
        if (!this.areRoundsInfinite()) {
            this.rounds--;
            if (this.rounds < 0) {
                this.rounds = 0;
            }
            if (this.rounds == 0) {
                this.customTerminateLogic();
            }
        }
    }
}