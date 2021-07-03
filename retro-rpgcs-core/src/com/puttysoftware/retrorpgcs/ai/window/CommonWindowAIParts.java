/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.ai.window;

import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.retrorpgcs.creatures.Creature;

class CommonWindowAIParts {
    // Constants
    static final int SPELL_INDEX_HEAL = 1;

    // Constructor
    private CommonWindowAIParts() {
	// Do nothing
    }

    static int getMaxCastIndex(final Creature c) {
	final int currMP = c.getCurrentMP();
	final int[] allCosts = c.getSpellBook().getAllSpellCosts();
	int result = -1;
	if (currMP > 0) {
	    for (int x = 0; x < allCosts.length; x++) {
		if (currMP >= allCosts[x]) {
		    result = x;
		}
	    }
	}
	return result;
    }

    static boolean check(final int effChance) {
	final RandomRange random = new RandomRange(1, 100);
	final int chance = random.generate();
	if (chance <= effChance) {
	    return true;
	} else {
	    // Not acting
	    return false;
	}
    }
}
