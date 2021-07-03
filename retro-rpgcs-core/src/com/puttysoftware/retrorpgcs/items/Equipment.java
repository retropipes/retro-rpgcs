/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.items;

import java.io.IOException;
import java.util.Arrays;

import com.puttysoftware.retrorpgcs.creatures.faiths.FaithConstants;
import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class Equipment extends Item {
    static Equipment readEquipment(final XDataReader dr) throws IOException {
        final var i = Item.readItem(dr);
        if (i == null) {
            // Abort
            return null;
        }
        final var matID = dr.readInt();
        final var eCat = dr.readInt();
        final var ei = new Equipment(i, eCat, matID);
        ei.firstSlotUsed = dr.readInt();
        ei.secondSlotUsed = dr.readInt();
        ei.conditionalSlot = dr.readBoolean();
        final var fc = FaithConstants.getFaithsCount();
        for (var z = 0; z < fc; z++) {
            ei.faithPowerName[z] = dr.readString();
            ei.faithPowersApplied[z] = dr.readInt();
        }
        return ei;
    }

    // Properties
    private final int equipCat;
    private final int materialID;
    private int firstSlotUsed;
    private int secondSlotUsed;
    private boolean conditionalSlot;
    private int[] faithPowersApplied;
    private String[] faithPowerName;

    Equipment(final Equipment e) {
        super(e.getItemName(), e);
        this.equipCat = e.equipCat;
        this.materialID = e.materialID;
        this.firstSlotUsed = e.firstSlotUsed;
        this.secondSlotUsed = e.secondSlotUsed;
        this.conditionalSlot = e.conditionalSlot;
        this.initFaithPowers();
        System.arraycopy(e.faithPowersApplied, 0, this.faithPowersApplied, 0,
                e.faithPowersApplied.length);
        System.arraycopy(e.faithPowerName, 0, this.faithPowerName, 0,
                e.faithPowerName.length);
    }

    // Constructors
    private Equipment(final Item i, final int equipCategory,
            final int newMaterialID) {
        super(i.getName(), i.getInitialUses(), i.getWeightPerUse());
        this.equipCat = equipCategory;
        this.materialID = newMaterialID;
        this.firstSlotUsed = EquipmentSlotConstants.SLOT_NONE;
        this.secondSlotUsed = EquipmentSlotConstants.SLOT_NONE;
        this.conditionalSlot = false;
        this.initFaithPowers();
    }

    protected Equipment(final String itemName, final int cost) {
        super(itemName, 0, 0);
        this.equipCat = EquipmentCategoryConstants.EQUIPMENT_CATEGORY_ARMOR;
        this.materialID = ArmorMaterialConstants.MATERIAL_NONE;
        this.firstSlotUsed = EquipmentSlotConstants.SLOT_SOCKS;
        this.secondSlotUsed = EquipmentSlotConstants.SLOT_NONE;
        this.conditionalSlot = false;
        this.setBuyPrice(cost);
        this.initFaithPowers();
    }

    Equipment(final String itemName, final int itemInitialUses,
            final int itemWeightPerUse, final int equipCategory,
            final int newMaterialID) {
        super(itemName, itemInitialUses, itemWeightPerUse);
        this.equipCat = equipCategory;
        this.materialID = newMaterialID;
        this.firstSlotUsed = EquipmentSlotConstants.SLOT_NONE;
        this.secondSlotUsed = EquipmentSlotConstants.SLOT_NONE;
        this.conditionalSlot = false;
        this.initFaithPowers();
    }

    public final void applyFaithPower(final int fid, final String fpName) {
        this.faithPowerName[fid] = fpName + " ";
        this.faithPowersApplied[fid]++;
    }

    public final void enchantName(final int bonus) {
        var oldName = this.getName();
        // Check - is name enchanted already?
        if (oldName.charAt(oldName.length() - 2) == '+') {
            // Yes - remove old enchantment
            oldName = oldName.substring(0, oldName.length() - 3);
        }
        final var newName = oldName + " +" + bonus;
        this.setName(newName);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || !(obj instanceof Equipment)) {
            return false;
        }
        final var other = (Equipment) obj;
        if (this.conditionalSlot != other.conditionalSlot) {
            return false;
        }
        if (this.equipCat != other.equipCat) {
            return false;
        }
        if (!Arrays.equals(this.faithPowerName, other.faithPowerName)) {
            return false;
        }
        if (!Arrays.equals(this.faithPowersApplied, other.faithPowersApplied)) {
            return false;
        }
        if (this.firstSlotUsed != other.firstSlotUsed) {
            return false;
        }
        if (this.materialID != other.materialID) {
            return false;
        }
        if (this.secondSlotUsed != other.secondSlotUsed) {
            return false;
        }
        return true;
    }

    public final int getEquipCategory() {
        return this.equipCat;
    }

    public final int getFaithPowerLevel(final int fid) {
        return this.faithPowersApplied[fid];
    }

    public final int getFirstSlotUsed() {
        return this.firstSlotUsed;
    }

    private String getItemName() {
        return super.getName();
    }

    public final int getMaterial() {
        return this.materialID;
    }

    @Override
    public String getName() {
        final var faithBuilder = new StringBuilder();
        final var fc = FaithConstants.getFaithsCount();
        for (var z = 0; z < fc; z++) {
            if (!this.faithPowerName[z].isEmpty()) {
                faithBuilder.append(this.faithPowerName[z]);
            }
        }
        return faithBuilder.toString() + super.getName();
    }

    public final int getSecondSlotUsed() {
        return this.secondSlotUsed;
    }

    @Override
    public int hashCode() {
        final var prime = 31;
        var result = super.hashCode();
        result = prime * result + (this.conditionalSlot ? 1231 : 1237);
        result = prime * result + this.equipCat;
        result = prime * result + Arrays.hashCode(this.faithPowerName);
        result = prime * result + Arrays.hashCode(this.faithPowersApplied);
        result = prime * result + this.firstSlotUsed;
        result = prime * result + this.materialID;
        return prime * result + this.secondSlotUsed;
    }

    // Methods
    private final void initFaithPowers() {
        this.faithPowersApplied = new int[FaithConstants.getFaithsCount()];
        this.faithPowerName = new String[FaithConstants.getFaithsCount()];
        Arrays.fill(this.faithPowerName, "");
    }

    public final boolean isTwoHanded() {
        return this.firstSlotUsed == EquipmentSlotConstants.SLOT_MAINHAND
                && this.secondSlotUsed == EquipmentSlotConstants.SLOT_OFFHAND
                && !this.conditionalSlot;
    }

    public final void setConditionalSlot(final boolean newConditionalSlot) {
        this.conditionalSlot = newConditionalSlot;
    }

    public final void setFirstSlotUsed(final int newFirstSlotUsed) {
        this.firstSlotUsed = newFirstSlotUsed;
    }

    public final void setSecondSlotUsed(final int newSecondSlotUsed) {
        this.secondSlotUsed = newSecondSlotUsed;
    }

    final void writeEquipment(final XDataWriter dw) throws IOException {
        super.writeItem(dw);
        dw.writeInt(this.materialID);
        dw.writeInt(this.equipCat);
        dw.writeInt(this.firstSlotUsed);
        dw.writeInt(this.secondSlotUsed);
        dw.writeBoolean(this.conditionalSlot);
        final var fc = FaithConstants.getFaithsCount();
        for (var z = 0; z < fc; z++) {
            dw.writeString(this.faithPowerName[z]);
            dw.writeInt(this.faithPowersApplied[z]);
        }
    }
}
