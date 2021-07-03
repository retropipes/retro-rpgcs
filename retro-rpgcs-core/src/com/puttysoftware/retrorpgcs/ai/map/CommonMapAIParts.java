/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.ai.map;

import java.awt.Point;

import com.puttysoftware.randomrange.RandomRange;

class CommonMapAIParts {
    // Constants
    static final int STUCK_THRESHOLD = 16;
    static final int MIN_VISION = 1;
    static final int SPELL_INDEX_HEAL = 1;

    static boolean check(final MapAIContext ac, final int effChance) {
        final var random = new RandomRange(1, 100);
        final var chance = random.generate();
        if ((chance <= effChance) && (ac.getCharacter().getCurrentAP() > 0)) {
            return true;
        } else {
            // Can't act any more times
            return false;
        }
    }

    static int getMaxCastIndex(final MapAIContext ac) {
        final var currMP = ac.getCharacter().getTemplate().getCurrentMP();
        final var allCosts = ac.getCharacter().getTemplate().getSpellBook()
                .getAllSpellCosts();
        var result = -1;
        if (currMP > 0) {
            for (var x = 0; x < allCosts.length; x++) {
                if (currMP >= allCosts[x]) {
                    result = x;
                }
            }
        }
        return result;
    }

    static Point turnLeft45(final int x, final int y) {
        if (x == -1 && y == -1) {
            return new Point(-1, 0);
        } else if (x == -1 && y == 0) {
            return new Point(-1, 1);
        } else if (x == -1 && y == 1) {
            return new Point(0, 1);
        } else if (x == 0 && y == -1) {
            return new Point(-1, -1);
        } else if (x == 0 && y == 1) {
            return new Point(1, 1);
        } else if (x == 1 && y == -1) {
            return new Point(0, -1);
        } else if (x == 1 && y == 0) {
            return new Point(1, -1);
        } else if (x == 1 && y == 1) {
            return new Point(0, -1);
        } else {
            return new Point(x, y);
        }
    }

    static Point turnRight45(final int x, final int y) {
        if (x == -1 && y == -1) {
            return new Point(-1, 0);
        } else if (x == -1 && y == 0) {
            return new Point(-1, -1);
        } else if (x == -1 && y == 1) {
            return new Point(-1, 0);
        } else if (x == 0 && y == -1) {
            return new Point(1, -1);
        } else if (x == 0 && y == 1) {
            return new Point(-1, 1);
        } else if (x == 1 && y == -1) {
            return new Point(1, 0);
        } else if (x == 1 && y == 0) {
            return new Point(1, 1);
        } else if (x == 1 && y == 1) {
            return new Point(0, 1);
        } else {
            return new Point(x, y);
        }
    }

    // Constructor
    private CommonMapAIParts() {
        // Do nothing
    }
}
