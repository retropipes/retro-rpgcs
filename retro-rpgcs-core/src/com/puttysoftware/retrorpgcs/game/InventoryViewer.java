/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.game;

import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.retrorpgcs.creatures.party.PartyManager;

public final class InventoryViewer {
    public static void showEquipmentDialog() {
        final var title = "Equipment";
        final var member = PartyManager.getParty().getLeader();
        if (member != null) {
            final var equipString = member.getItems()
                    .generateEquipmentStringArray();
            CommonDialogs.showInputDialog("Equipment", title, equipString,
                    equipString[0]);
        }
    }

    public static void showItemInventoryDialog() {
        final var title = "Items";
        final var member = PartyManager.getParty().getLeader();
        if (member != null) {
            final var invString = member.getItems()
                    .generateInventoryStringArray();
            CommonDialogs.showInputDialog("Items", title, invString,
                    invString[0]);
        }
    }

    private InventoryViewer() {
        // Do nothing
    }
}
