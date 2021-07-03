/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.items.combat;

import com.puttysoftware.retrorpgcs.items.combat.predefined.Bolt;
import com.puttysoftware.retrorpgcs.items.combat.predefined.Bomb;
import com.puttysoftware.retrorpgcs.items.combat.predefined.Fireball;
import com.puttysoftware.retrorpgcs.items.combat.predefined.Potion;
import com.puttysoftware.retrorpgcs.items.combat.predefined.Rope;

public class CombatItemList {
    // Fields
    private final CombatItem[] allItems;

    // Constructor
    public CombatItemList() {
	this.allItems = new CombatItem[] { new Bomb(), new Rope(), new Bolt(), new Potion(), new Fireball() };
    }

    // Methods
    public CombatItem[] getAllItems() {
	return this.allItems;
    }

    public String[] getAllNames() {
	final String[] allNames = new String[this.allItems.length];
	for (int x = 0; x < this.allItems.length; x++) {
	    allNames[x] = this.allItems[x].getName();
	}
	return allNames;
    }

    CombatItem getItemByName(final String name) {
	for (final CombatItem allItem : this.allItems) {
	    if (name.equals(allItem.getName())) {
		return allItem;
	    }
	}
	return null;
    }
}
