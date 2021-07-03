/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.creatures.castes;

import com.puttysoftware.retrorpgcs.descriptionmanagers.CasteDescriptionManager;

public class Caste {
    private final int casteID;
    private final String desc;

    public Caste(final int cid) {
	this.desc = CasteDescriptionManager.getCasteDescription(cid);
	this.casteID = cid;
    }

    public String getDescription() {
	return this.desc;
    }

    public final int getCasteID() {
	return this.casteID;
    }

    static String casteIDtoName(final int casteID) {
	return CasteConstants.CASTE_NAMES[casteID];
    }
}
