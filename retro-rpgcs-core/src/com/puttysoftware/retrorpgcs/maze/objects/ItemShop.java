/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.retrorpgcs.maze.abc.AbstractShop;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;
import com.puttysoftware.retrorpgcs.shops.ShopTypes;

public class ItemShop extends AbstractShop {
    // Constructors
    public ItemShop() {
	super(ShopTypes.SHOP_TYPE_ITEMS);
    }

    @Override
    public int getBaseID() {
	return ObjectImageConstants.OBJECT_IMAGE_ITEM_SHOP;
    }

    @Override
    public String getName() {
	return "Item Shop";
    }

    @Override
    public String getPluralName() {
	return "Item Shops";
    }

    @Override
    public String getDescription() {
	return "Item Shops sell items used in battle.";
    }
}
