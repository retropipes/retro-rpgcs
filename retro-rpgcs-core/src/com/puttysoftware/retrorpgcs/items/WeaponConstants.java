/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.items;

import com.puttysoftware.retrorpgcs.creatures.castes.CasteConstants;
import com.puttysoftware.retrorpgcs.names.NamesConstants;
import com.puttysoftware.retrorpgcs.names.NamesManager;

public class WeaponConstants {
    // Constants
    private static String[] WEAPON_1H = null;
    private static String[] WEAPON_2H = null;
    private static final String[] WEAPON_CHOICES = { "One-Handed Weapons",
            "Two-Handed Weapons" };
    private static String[] HAND_CHOICES = null;

    public static synchronized String[] get1HWeapons() {
        if (WeaponConstants.WEAPON_1H == null) {
            final var temp = new String[CasteConstants.CASTES_COUNT];
            for (var x = 0; x < temp.length; x++) {
                temp[x] = NamesManager.getName(
                        NamesConstants.SECTION_EQUIP_WEAPONS_1H,
                        NamesConstants.SECTION_ARRAY_WEAPONS_1H[x]);
            }
            WeaponConstants.WEAPON_1H = temp;
        }
        return WeaponConstants.WEAPON_1H;
    }

    public static synchronized String[] get2HWeapons() {
        if (WeaponConstants.WEAPON_2H == null) {
            final var temp = new String[CasteConstants.CASTES_COUNT];
            for (var x = 0; x < temp.length; x++) {
                temp[x] = NamesManager.getName(
                        NamesConstants.SECTION_EQUIP_WEAPONS_2H,
                        NamesConstants.SECTION_ARRAY_WEAPONS_2H[x]);
            }
            WeaponConstants.WEAPON_2H = temp;
        }
        return WeaponConstants.WEAPON_2H;
    }

    public static synchronized String[] getHandChoices() {
        if (WeaponConstants.HAND_CHOICES == null) {
            final var temp = EquipmentSlotConstants.getSlotNames();
            final var temp2 = new String[2];
            temp2[0] = temp[EquipmentSlotConstants.SLOT_MAINHAND];
            temp2[1] = temp[EquipmentSlotConstants.SLOT_OFFHAND];
            WeaponConstants.HAND_CHOICES = temp2;
        }
        return WeaponConstants.HAND_CHOICES;
    }

    // Methods
    public static String[] getWeaponChoices() {
        return WeaponConstants.WEAPON_CHOICES;
    }

    // Private Constructor
    private WeaponConstants() {
        // Do nothing
    }
}
