/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.resourcemanagers;

import com.puttysoftware.retrorpgcs.datamanagers.MonsterDataManager;

public class MonsterNames {
    // Package-Protected Constants
    private static final String[] MONSTER_NAMES = MonsterDataManager.getMonsterData();

    public static final String[] getAllNames() {
	return MonsterNames.MONSTER_NAMES;
    }

    // Private constructor
    private MonsterNames() {
	// Do nothing
    }
}