/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.game;

import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.retrorpgcs.creatures.party.PartyManager;
import com.puttysoftware.retrorpgcs.creatures.party.PartyMember;

public final class InventoryViewer {
    private InventoryViewer() {
	// Do nothing
    }

    public static void showEquipmentDialog() {
	final String title = "Equipment";
	final PartyMember member = PartyManager.getParty().getLeader();
	if (member != null) {
	    final String[] equipString = member.getItems().generateEquipmentStringArray();
	    CommonDialogs.showInputDialog("Equipment", title, equipString, equipString[0]);
	}
    }

    public static void showItemInventoryDialog() {
	final String title = "Items";
	final PartyMember member = PartyManager.getParty().getLeader();
	if (member != null) {
	    final String[] invString = member.getItems().generateInventoryStringArray();
	    CommonDialogs.showInputDialog("Items", title, invString, invString[0]);
	}
    }
}
