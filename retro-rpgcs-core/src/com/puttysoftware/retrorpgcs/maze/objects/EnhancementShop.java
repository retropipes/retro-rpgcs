/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.retrorpgcs.maze.abc.AbstractShop;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;
import com.puttysoftware.retrorpgcs.shops.ShopTypes;

public class EnhancementShop extends AbstractShop {
    // Constructors
    public EnhancementShop() {
        super(ShopTypes.SHOP_TYPE_ENHANCEMENTS);
    }

    @Override
    public int getBaseID() {
        return ObjectImageConstants.OBJECT_IMAGE_ENHANCEMENT_SHOP;
    }

    @Override
    public String getName() {
        return "Enhancement Shop";
    }

    @Override
    public String getPluralName() {
        return "Enhancement Shops";
    }

    @Override
    public String getDescription() {
        return "Enhancement Shops sell improvements to weapons and armor.";
    }
}
