/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.creatures.monsters;

import com.puttysoftware.retrorpgcs.creatures.Creature;
import com.puttysoftware.retrorpgcs.creatures.party.PartyManager;
import com.puttysoftware.retrorpgcs.maze.Maze;

public class MonsterFactory {
    private MonsterFactory() {
	// Do nothing
    }

    public static Creature getNewMonsterInstance() {
	if (PartyManager.getParty().getTowerLevel() == Maze.getMaxLevels() - 1) {
	    return new BossMonster();
	} else {
	    return new Monster();
	}
    }
}
