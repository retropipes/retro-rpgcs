/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.items;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import com.puttysoftware.retrorpgcs.creatures.Creature;
import com.puttysoftware.retrorpgcs.creatures.StatConstants;
import com.puttysoftware.retrorpgcs.items.combat.CombatItemList;
import com.puttysoftware.retrorpgcs.maze.FormatConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;
import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class ItemInventory {
    private static final int COMBAT_ITEM_COUNT_V2 = 2;

    public static ItemInventory readItemInventory(final XDataReader dr,
            final int formatVersion) throws IOException {
        final var ii = new ItemInventory(true);
        var counter = 0;
        for (final ItemUseQuantity iqu : ii.entries) {
            iqu.setQuantity(dr.readInt());
            iqu.setUses(dr.readInt());
            counter++;
            if (formatVersion == FormatConstants.CHARACTER_FORMAT_2
                    && counter >= ItemInventory.COMBAT_ITEM_COUNT_V2) {
                break;
            }
        }
        for (var x = 0; x < ii.equipment.length; x++) {
            final var ei = Equipment.readEquipment(dr);
            if (ei != null) {
                ii.equipment[x] = ei;
            }
        }
        return ii;
    }

    // Properties
    private ItemUseQuantity[] entries;
    private Equipment[] equipment;
    private Socks socks;

    // Constructors
    public ItemInventory(final boolean hasCombatItems) {
        this.resetInventory(hasCombatItems);
    }

    public void addItem(final Item i) {
        for (final ItemUseQuantity iqu : this.entries) {
            final var item = iqu.getItem();
            if (i.getName().equals(item.getName())) {
                iqu.incrementQuantity();
                iqu.setUses(item.getInitialUses());
                return;
            }
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || !(obj instanceof ItemInventory)) {
            return false;
        }
        final var other = (ItemInventory) obj;
        if (!Arrays.equals(this.equipment, other.equipment)) {
            return false;
        }
        if (!Objects.equals(this.socks, other.socks)) {
            return false;
        }
        if (this.entries == null) {
            if (other.entries != null) {
                return false;
            }
        } else if (!Arrays.deepEquals(this.entries, other.entries)) {
            return false;
        }
        return true;
    }

    public void equipArmor(final Creature pc, final Equipment ei,
            final boolean playSound) {
        // Check for socks
        if (ei instanceof Socks) {
            this.socks = (Socks) ei;
        } else {
            // Fix character load, changing armor
            // Check for two-handed weapon
            if ((ei.getFirstSlotUsed() == EquipmentSlotConstants.SLOT_OFFHAND)
                    && (this.equipment[EquipmentSlotConstants.SLOT_MAINHAND] != null)) {
                if (this.equipment[EquipmentSlotConstants.SLOT_MAINHAND]
                        .getEquipCategory() == EquipmentCategoryConstants.EQUIPMENT_CATEGORY_TWO_HANDED_WEAPON) {
                    pc.offsetLoad(
                            -this.equipment[EquipmentSlotConstants.SLOT_MAINHAND]
                                    .getEffectiveWeight());
                }
            }
            if (this.equipment[ei.getFirstSlotUsed()] != null) {
                pc.offsetLoad(-this.equipment[ei.getFirstSlotUsed()]
                        .getEffectiveWeight());
            }
            pc.offsetLoad(ei.getEffectiveWeight());
            // Check for shield
            // Check for two-handed weapon
            if ((ei.getFirstSlotUsed() == EquipmentSlotConstants.SLOT_OFFHAND)
                    && (this.equipment[EquipmentSlotConstants.SLOT_MAINHAND] != null)) {
                if (this.equipment[EquipmentSlotConstants.SLOT_MAINHAND]
                        .getEquipCategory() == EquipmentCategoryConstants.EQUIPMENT_CATEGORY_TWO_HANDED_WEAPON) {
                    // Two-handed weapon currently equipped, unequip it
                    this.equipment[EquipmentSlotConstants.SLOT_MAINHAND] = null;
                    this.equipment[EquipmentSlotConstants.SLOT_OFFHAND] = null;
                }
            }
            // Equip it in first slot
            this.equipment[ei.getFirstSlotUsed()] = ei;
        }
        if (playSound) {
            SoundManager.playSound(SoundConstants.SOUND_EQUIP);
        }
    }

    public void equipOneHandedWeapon(final Creature pc, final Equipment ei,
            final boolean useFirst, final boolean playSound) {
        // Fix character load, changing weapons
        if (this.equipment[EquipmentSlotConstants.SLOT_MAINHAND] != null
                && useFirst) {
            pc.offsetLoad(-this.equipment[EquipmentSlotConstants.SLOT_MAINHAND]
                    .getEffectiveWeight());
        } else if (this.equipment[EquipmentSlotConstants.SLOT_OFFHAND] != null
                && !useFirst) {
            pc.offsetLoad(-this.equipment[EquipmentSlotConstants.SLOT_OFFHAND]
                    .getEffectiveWeight());
        }
        pc.offsetLoad(ei.getEffectiveWeight());
        // Check for two-handed weapon
        if ((this.equipment[EquipmentSlotConstants.SLOT_MAINHAND] != null)
                && (this.equipment[EquipmentSlotConstants.SLOT_MAINHAND]
                        .getEquipCategory() == EquipmentCategoryConstants.EQUIPMENT_CATEGORY_TWO_HANDED_WEAPON)) {
            // Two-handed weapon currently equipped, unequip it
            this.equipment[EquipmentSlotConstants.SLOT_MAINHAND] = null;
            this.equipment[EquipmentSlotConstants.SLOT_OFFHAND] = null;
        }
        if (useFirst) {
            // Equip it in first slot
            this.equipment[ei.getFirstSlotUsed()] = ei;
        } else {
            // Equip it in second slot
            this.equipment[ei.getSecondSlotUsed()] = ei;
        }
        if (playSound) {
            SoundManager.playSound(SoundConstants.SOUND_EQUIP);
        }
    }

    public void equipTwoHandedWeapon(final Creature pc, final Equipment ei,
            final boolean playSound) {
        // Fix character load, changing weapons
        if (this.equipment[EquipmentSlotConstants.SLOT_MAINHAND] != null) {
            pc.offsetLoad(-this.equipment[EquipmentSlotConstants.SLOT_MAINHAND]
                    .getEffectiveWeight());
        }
        pc.offsetLoad(ei.getEffectiveWeight());
        // Equip it in first slot
        this.equipment[ei.getFirstSlotUsed()] = ei;
        // Equip it in second slot
        this.equipment[ei.getSecondSlotUsed()] = ei;
        if (playSound) {
            SoundManager.playSound(SoundConstants.SOUND_EQUIP);
        }
    }

    public void fireStepActions(final Creature wearer) {
        if (this.socks != null) {
            this.socks.stepAction(wearer);
        }
    }

    public String[] generateCombatUsableDisplayStringArray() {
        final var result = new ArrayList<String>();
        StringBuilder sb;
        var counter = 0;
        for (final ItemUseQuantity iqu : this.entries) {
            if (iqu.getItem().isCombatUsable()) {
                sb = new StringBuilder();
                sb.append("Slot ");
                sb.append(counter + 1);
                sb.append(": ");
                sb.append(iqu.getItem().getName());
                sb.append(" (Qty: ");
                sb.append(iqu.getQuantity());
                sb.append(", Uses: ");
                sb.append(iqu.getUses());
                sb.append(")");
                result.add(sb.toString());
                counter++;
            }
        }
        return result.toArray(new String[result.size()]);
    }

    public String[] generateCombatUsableStringArray() {
        final var result = new ArrayList<String>();
        StringBuilder sb;
        for (final ItemUseQuantity iqu : this.entries) {
            if (iqu.getItem().isCombatUsable()) {
                sb = new StringBuilder();
                sb.append(iqu.getItem().getName());
                result.add(sb.toString());
            }
        }
        return result.toArray(new String[result.size()]);
    }

    public String[] generateEquipmentEnhancementStringArray() {
        final var result = new String[this.equipment.length];
        StringBuilder sb;
        for (var x = 0; x < result.length; x++) {
            sb = new StringBuilder();
            if (this.equipment[x] == null) {
                sb.append("Nothing (0)");
            } else {
                sb.append(this.equipment[x].getName());
                sb.append(" (");
                sb.append(this.equipment[x].getPotency());
                sb.append(")");
            }
            result[x] = sb.toString();
        }
        return result;
    }

    public String[] generateEquipmentStringArray() {
        final var result = new String[this.equipment.length + 1];
        StringBuilder sb;
        for (var x = 0; x < result.length - 1; x++) {
            sb = new StringBuilder();
            sb.append(EquipmentSlotConstants.getSlotNames()[x]);
            sb.append(": ");
            if (this.equipment[x] == null) {
                sb.append("Nothing (0)");
            } else {
                sb.append(this.equipment[x].getName());
                sb.append(" (");
                sb.append(this.equipment[x].getPotency());
                sb.append(")");
            }
            result[x] = sb.toString();
        }
        // Append Socks
        sb = new StringBuilder();
        sb.append("Socks: ");
        if (this.socks == null) {
            sb.append("None");
        } else {
            sb.append(this.socks.getName());
        }
        result[result.length - 1] = sb.toString();
        return result;
    }

    public String[] generateInventoryStringArray() {
        final var result = new ArrayList<String>();
        StringBuilder sb;
        var counter = 0;
        for (final ItemUseQuantity iqu : this.entries) {
            sb = new StringBuilder();
            sb.append("Slot ");
            sb.append(counter + 1);
            sb.append(": ");
            sb.append(iqu.getItem().getName());
            sb.append(" (Qty: ");
            sb.append(iqu.getQuantity());
            sb.append(", Uses: ");
            sb.append(iqu.getUses());
            sb.append(")");
            result.add(sb.toString());
            counter++;
        }
        return result.toArray(new String[result.size()]);
    }

    public Equipment getEquipmentInSlot(final int slot) {
        return this.equipment[slot];
    }

    public int getTotalAbsorb() {
        var total = 0;
        if ((this.equipment[EquipmentSlotConstants.SLOT_OFFHAND] != null)
                && (this.equipment[EquipmentSlotConstants.SLOT_OFFHAND]
                        .getEquipCategory() == EquipmentCategoryConstants.EQUIPMENT_CATEGORY_ARMOR)) {
            total += this.equipment[EquipmentSlotConstants.SLOT_OFFHAND]
                    .getPotency();
        }
        if (this.equipment[EquipmentSlotConstants.SLOT_BODY] != null) {
            total += this.equipment[EquipmentSlotConstants.SLOT_BODY]
                    .getPotency();
        }
        return total;
    }

    public int getTotalEquipmentWeight() {
        var total = 0;
        for (var x = 0; x < EquipmentSlotConstants.MAX_SLOTS; x++) {
            if (this.equipment[x] != null) {
                total += this.equipment[x].getEffectiveWeight();
            }
        }
        return total;
    }

    public int getTotalInventoryWeight() {
        var total = 0;
        for (final ItemUseQuantity iqu : this.entries) {
            total += iqu.getItem().getEffectiveWeight();
        }
        return total;
    }

    public int getTotalPower() {
        var total = 0;
        if (this.equipment[EquipmentSlotConstants.SLOT_MAINHAND] != null) {
            total += this.equipment[EquipmentSlotConstants.SLOT_MAINHAND]
                    .getPotency();
        }
        if (this.equipment[EquipmentSlotConstants.SLOT_OFFHAND] != null) {
            if (this.equipment[EquipmentSlotConstants.SLOT_OFFHAND]
                    .getEquipCategory() == EquipmentCategoryConstants.EQUIPMENT_CATEGORY_ONE_HANDED_WEAPON) {
                total += this.equipment[EquipmentSlotConstants.SLOT_OFFHAND]
                        .getPotency();
            } else if (this.equipment[EquipmentSlotConstants.SLOT_OFFHAND]
                    .getEquipCategory() == EquipmentCategoryConstants.EQUIPMENT_CATEGORY_TWO_HANDED_WEAPON) {
                total += this.equipment[EquipmentSlotConstants.SLOT_OFFHAND]
                        .getPotency();
                total *= StatConstants.FACTOR_TWO_HANDED_BONUS;
            }
        }
        return total;
    }

    public int getUses(final Item i) {
        for (final ItemUseQuantity iqu : this.entries) {
            final var item = iqu.getItem();
            if (i.getName().equals(item.getName())) {
                return iqu.getUses();
            }
        }
        return 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(this.equipment), Arrays.hashCode(this.entries), this.socks);
    }

    // Methods
    public void resetInventory() {
        this.resetInventory(true);
    }

    private void resetInventory(final boolean hasCombatItems) {
        if (hasCombatItems) {
            final var cil = new CombatItemList();
            final Item[] items = cil.getAllItems();
            this.entries = new ItemUseQuantity[items.length];
            for (var x = 0; x < items.length; x++) {
                this.entries[x] = new ItemUseQuantity(items[x], 0, 0);
            }
        } else {
            this.entries = null;
        }
        this.equipment = new Equipment[EquipmentSlotConstants.MAX_SLOTS];
        this.socks = null;
    }

    public void setEquipmentInSlot(final int slot, final Equipment e) {
        this.equipment[slot] = e;
    }

    public void writeItemInventory(final XDataWriter dw) throws IOException {
        for (final ItemUseQuantity iqu : this.entries) {
            dw.writeInt(iqu.getQuantity());
            dw.writeInt(iqu.getUses());
        }
        for (final Equipment ei : this.equipment) {
            if (ei != null) {
                ei.writeEquipment(dw);
            } else {
                dw.writeString("null");
            }
        }
    }
}
