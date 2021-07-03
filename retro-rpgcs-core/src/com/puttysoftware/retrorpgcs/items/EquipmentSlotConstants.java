/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.items;

import com.puttysoftware.retrorpgcs.names.NamesConstants;
import com.puttysoftware.retrorpgcs.names.NamesManager;

public class EquipmentSlotConstants {
    static final int SLOT_SOCKS = -2;
    static final int SLOT_NONE = -1;
    public static final int SLOT_MAINHAND = 0;
    public static final int SLOT_OFFHAND = 1;
    public static final int SLOT_BODY = 2;
    static final int MAX_SLOTS = 3;
    private static String[] SLOT_NAMES = null;
    private static String[] ARMOR_SLOT_NAMES = null;

    static synchronized String[] getArmorSlotNames() {
        if (EquipmentSlotConstants.ARMOR_SLOT_NAMES == null) {
            if (EquipmentSlotConstants.SLOT_NAMES == null) {
                EquipmentSlotConstants.getSlotNames();
            }
            final var temp = EquipmentSlotConstants.SLOT_NAMES;
            final var temp2 = new String[temp.length - 1];
            var offset = 0;
            for (var x = 0; x < temp.length; x++) {
                if (x == EquipmentSlotConstants.SLOT_MAINHAND) {
                    offset++;
                } else {
                    temp2[x - offset] = temp[x];
                }
            }
            EquipmentSlotConstants.ARMOR_SLOT_NAMES = temp2;
        }
        return EquipmentSlotConstants.ARMOR_SLOT_NAMES;
    }

    static synchronized String[] getSlotNames() {
        if (EquipmentSlotConstants.SLOT_NAMES == null) {
            final var temp = new String[EquipmentSlotConstants.MAX_SLOTS];
            for (var x = 0; x < temp.length; x++) {
                temp[x] = NamesManager.getName(
                        NamesConstants.SECTION_EQUIP_SLOT,
                        NamesConstants.SECTION_ARRAY_EQUIP_SLOTS[x]);
            }
            EquipmentSlotConstants.SLOT_NAMES = temp;
        }
        return EquipmentSlotConstants.SLOT_NAMES;
    }
}
