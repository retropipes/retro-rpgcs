/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.retrorpgcs.maze.abc.AbstractShop;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;
import com.puttysoftware.retrorpgcs.shops.ShopTypes;

public class SpellShop extends AbstractShop {
    // Constructors
    public SpellShop() {
	super(ShopTypes.SHOP_TYPE_SPELLS);
    }

    @Override
    public int getBaseID() {
	return ObjectImageConstants.OBJECT_IMAGE_SPELL_SHOP;
    }

    @Override
    public String getName() {
	return "Spell Shop";
    }

    @Override
    public String getPluralName() {
	return "Spell Shops";
    }

    @Override
    public String getDescription() {
	return "Spell Shops teach spells, for a fee.";
    }
}
