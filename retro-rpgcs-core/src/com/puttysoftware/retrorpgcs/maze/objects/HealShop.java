/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.retrorpgcs.maze.abc.AbstractShop;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;
import com.puttysoftware.retrorpgcs.shops.ShopTypes;

public class HealShop extends AbstractShop {
    // Constructors
    public HealShop() {
	super(ShopTypes.SHOP_TYPE_HEALER);
    }

    @Override
    public int getBaseID() {
	return ObjectImageConstants.OBJECT_IMAGE_HEAL_SHOP;
    }

    @Override
    public String getName() {
	return "Heal Shop";
    }

    @Override
    public String getPluralName() {
	return "Heal Shops";
    }

    @Override
    public String getDescription() {
	return "Heal Shops restore health, for a fee.";
    }
}
