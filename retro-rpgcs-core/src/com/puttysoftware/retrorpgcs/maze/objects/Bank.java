/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.retrorpgcs.maze.abc.AbstractShop;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;
import com.puttysoftware.retrorpgcs.shops.ShopTypes;

public class Bank extends AbstractShop {
    // Constructors
    public Bank() {
	super(ShopTypes.SHOP_TYPE_BANK);
    }

    @Override
    public int getBaseID() {
	return ObjectImageConstants.OBJECT_IMAGE_BANK;
    }

    @Override
    public String getName() {
	return "Bank";
    }

    @Override
    public String getPluralName() {
	return "Banks";
    }

    @Override
    public String getDescription() {
	return "Banks store money for safe keeping.";
    }
}
