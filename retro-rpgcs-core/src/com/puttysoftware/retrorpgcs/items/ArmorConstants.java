/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.items;

import com.puttysoftware.retrorpgcs.names.NamesConstants;
import com.puttysoftware.retrorpgcs.names.NamesManager;

public class ArmorConstants {
    private static String[] ARMOR = null;
    private static String[] ARMOR_CHOICES = null;

    public static synchronized String[] getArmor() {
        if (ArmorConstants.ARMOR == null) {
            final var temp1 = ArmorConstants.getArmorChoices();
            final var temp2 = new String[temp1.length + 1];
            temp2[EquipmentSlotConstants.SLOT_MAINHAND] = "";
            temp2[EquipmentSlotConstants.SLOT_OFFHAND] = temp1[0];
            temp2[EquipmentSlotConstants.SLOT_BODY] = temp1[1];
            ArmorConstants.ARMOR = temp2;
        }
        return ArmorConstants.ARMOR;
    }

    public static synchronized String[] getArmorChoices() {
        if (ArmorConstants.ARMOR_CHOICES == null) {
            final var temp1 = EquipmentSlotConstants.getArmorSlotNames();
            final var temp2 = new String[temp1.length];
            System.arraycopy(temp1, 0, temp2, 0, temp1.length);
            temp2[EquipmentSlotConstants.SLOT_OFFHAND - 1] = NamesManager
                    .getName(NamesConstants.SECTION_EQUIP_ARMOR,
                            NamesConstants.ARMOR_SHIELD);
            ArmorConstants.ARMOR_CHOICES = temp2;
        }
        return ArmorConstants.ARMOR_CHOICES;
    }
}
