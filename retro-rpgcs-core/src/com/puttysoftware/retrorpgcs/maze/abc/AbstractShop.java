/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.maze.abc;

import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.maze.MazeConstants;
import com.puttysoftware.retrorpgcs.maze.utilities.TypeConstants;
import com.puttysoftware.retrorpgcs.shops.Shop;

public abstract class AbstractShop extends AbstractMazeObject {
    // Fields
    private final int shopType;

    // Constructors
    public AbstractShop(final int newShopType) {
	super(false, false);
	this.shopType = newShopType;
    }

    // Methods
    @Override
    protected void setTypes() {
	this.type.set(TypeConstants.TYPE_SHOP);
    }

    @Override
    public void postMoveAction(final boolean ie, final int dirX, final int dirY) {
	final Shop shop = RetroRPGCS.getInstance().getGenericShop(this.shopType);
	if (shop != null) {
	    shop.showShop();
	}
    }

    @Override
    public int getLayer() {
	return MazeConstants.LAYER_OBJECT;
    }

    @Override
    public int getCustomProperty(final int propID) {
	return AbstractMazeObject.DEFAULT_CUSTOM_VALUE;
    }

    @Override
    public void setCustomProperty(final int propID, final int value) {
	// Do nothing
    }
}
