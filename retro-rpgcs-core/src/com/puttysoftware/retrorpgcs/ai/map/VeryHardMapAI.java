/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.ai.map;

import com.puttysoftware.randomrange.RandomRange;

class VeryHardMapAI extends MapAI {
    private static final int CAST_SPELL_CHANCE = 80;
    private static final int STEAL_CHANCE = 16;
    private static final int DRAIN_CHANCE = 80;
    private static final int HEAL_THRESHOLD = 60;
    private static final int MAX_VISION = 11;
    private static final int FLEE_CHANCE = 1;
    // Fields
    private final RandomRange randMove;
    private int failedMoveAttempts;
    private int[] roundsRemaining;

    // Constructor
    public VeryHardMapAI() {
        this.randMove = new RandomRange(-1, 1);
        this.failedMoveAttempts = 0;
    }

    @Override
    public int getNextAction(final MapAIContext ac) {
        if (this.roundsRemaining == null) {
            this.roundsRemaining = new int[ac.getCharacter().getTemplate()
                    .getSpellBook().getSpellCount()];
        }
        if (this.spellCheck(ac)) {
            // Cast a spell
            return MapAI.ACTION_CAST_SPELL;
        } else {
            var there = ac.isEnemyNearby();
            if (there != null) {
                if (CommonMapAIParts.check(ac, VeryHardMapAI.STEAL_CHANCE)) {
                    // Steal
                    return MapAI.ACTION_STEAL;
                } else if (CommonMapAIParts.check(ac,
                        VeryHardMapAI.DRAIN_CHANCE)) {
                    // Drain MP
                    return MapAI.ACTION_DRAIN;
                } else // Something hostile is nearby, so attack it
                if (ac.getCharacter().getCurrentAT() > 0) {
                    this.moveX = there.x;
                    this.moveY = there.y;
                    return MapAI.ACTION_MOVE;
                } else {
                    this.failedMoveAttempts = 0;
                    return MapAI.ACTION_END_TURN;
                }
            } else {
                if (CommonMapAIParts.check(ac, VeryHardMapAI.FLEE_CHANCE)) {
                    // Flee
                    final var awayDir = ac.runAway();
                    if (awayDir == null) {
                        // Wander randomly
                        this.moveX = this.randMove.generate();
                        this.moveY = this.randMove.generate();
                        // Don't attack self
                        while (this.moveX == 0 && this.moveY == 0) {
                            this.moveX = this.randMove.generate();
                            this.moveY = this.randMove.generate();
                        }
                    } else {
                        this.moveX = awayDir.x;
                        this.moveY = awayDir.y;
                    }
                    return MapAI.ACTION_MOVE;
                } else {
                    // Look further
                    for (var x = CommonMapAIParts.MIN_VISION
                            + 1; x <= VeryHardMapAI.MAX_VISION; x++) {
                        there = ac.isEnemyNearby(x, x);
                        if (there != null) {
                            // Found something hostile, move towards it
                            if (!this.lastResult) {
                                this.failedMoveAttempts++;
                                if (this.failedMoveAttempts >= CommonMapAIParts.STUCK_THRESHOLD) {
                                    // We're stuck!
                                    this.failedMoveAttempts = 0;
                                    return MapAI.ACTION_END_TURN;
                                }
                                // Last move failed, try to move around object
                                final var randTurn = new RandomRange(0,
                                        1);
                                final var rt = randTurn.generate();
                                if (rt == 0) {
                                    there = CommonMapAIParts.turnRight45(
                                            this.moveX, this.moveY);
                                } else {
                                    there = CommonMapAIParts
                                            .turnLeft45(this.moveX, this.moveY);
                                }
                                this.moveX = there.x;
                                this.moveY = there.y;
                            } else {
                                this.moveX = (int) Math.signum(there.x);
                                this.moveY = (int) Math.signum(there.y);
                            }
                            break;
                        }
                    }
                }
                if (ac.getCharacter().getCurrentAP() > 0) {
                    if (there == null) {
                        // Wander randomly
                        this.moveX = this.randMove.generate();
                        this.moveY = this.randMove.generate();
                        // Don't attack self
                        while (this.moveX == 0 && this.moveY == 0) {
                            this.moveX = this.randMove.generate();
                            this.moveY = this.randMove.generate();
                        }
                    }
                    return MapAI.ACTION_MOVE;
                } else {
                    this.failedMoveAttempts = 0;
                    return MapAI.ACTION_END_TURN;
                }
            }
        }
    }

    @Override
    public void newRoundHook() {
        // Decrement effect counters
        for (var z = 0; z < this.roundsRemaining.length; z++) {
            if (this.roundsRemaining[z] > 0) {
                this.roundsRemaining[z]--;
            }
        }
    }

    private boolean spellCheck(final MapAIContext ac) {
        final var random = new RandomRange(1, 100);
        final var chance = random.generate();
        if (chance <= VeryHardMapAI.CAST_SPELL_CHANCE) {
            final var maxIndex = CommonMapAIParts.getMaxCastIndex(ac);
            if ((maxIndex > -1) && (ac.getCharacter().getCurrentSP() > 0)) {
                // Select a random spell to cast
                final var randomSpell = new RandomRange(0,
                        maxIndex);
                final var randomSpellID = randomSpell.generate();
                // Healing spell was selected - is healing needed?
                if ((randomSpellID == CommonMapAIParts.SPELL_INDEX_HEAL)
                        && (ac.getCharacter().getTemplate()
                                .getCurrentHP() > ac.getCharacter()
                                        .getTemplate().getMaximumHP()
                                        * VeryHardMapAI.HEAL_THRESHOLD
                                        / 100)) {
                    // Do not need healing
                    return false;
                }
                if (this.roundsRemaining[randomSpellID] == 0) {
                    this.spell = ac.getCharacter().getTemplate()
                            .getSpellBook().getSpellByID(randomSpellID);
                    this.roundsRemaining[randomSpellID] = this.spell
                            .getEffect().getInitialRounds();
                    return true;
                } else {
                    // Spell selected already active
                    return false;
                }
            } else {
                // Can't cast any more spells
                return false;
            }
        } else {
            // Not casting a spell
            return false;
        }
    }
}
