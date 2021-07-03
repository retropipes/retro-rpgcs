/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.battle.damageengines;

import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.retrorpgcs.creatures.Creature;
import com.puttysoftware.retrorpgcs.creatures.StatConstants;
import com.puttysoftware.retrorpgcs.creatures.faiths.FaithConstants;
import com.puttysoftware.retrorpgcs.items.EquipmentCategoryConstants;
import com.puttysoftware.retrorpgcs.items.EquipmentSlotConstants;

class VeryHardDamageEngine extends AbstractDamageEngine {
    private static final int MULTIPLIER_MIN = 6000;
    private static final int MULTIPLIER_MAX = 10000;
    private static final int MULTIPLIER_MIN_CRIT = 12000;
    private static final int MULTIPLIER_MAX_CRIT = 15000;
    private static final int FUMBLE_CHANCE = 1000;
    private static final int PIERCE_CHANCE = 500;
    private static final int CRIT_CHANCE = 500;
    private static final double FAITH_INCREMENT = 0.05;
    private static final double FAITH_INCREMENT_2H = 0.08;
    private static final double FAITH_DR_INCREMENT = 0.03;
    private boolean dodged = false;
    private boolean missed = false;
    private boolean crit = false;
    private boolean pierce = false;
    private boolean fumble = false;

    @Override
    public int computeDamage(final Creature enemy, final Creature acting) {
        // Compute Damage
        final var attack = acting.getEffectedAttack();
        final var defense = enemy
                .getEffectedStat(StatConstants.STAT_DEFENSE);
        final var power = acting.getItems().getTotalPower();
        this.didFumble();
        if (this.fumble) {
            // Fumble!
            return CommonDamageEngineParts.fumbleDamage(power);
        } else {
            this.didPierce();
            this.didCrit();
            double rawDamage;
            if (this.pierce) {
                rawDamage = attack;
            } else {
                rawDamage = attack - defense;
            }
            final var rHit = CommonDamageEngineParts.chance();
            var aHit = acting.getHit();
            if (this.crit || this.pierce) {
                // Critical hits and piercing hits
                // always connect
                aHit = CommonDamageEngineParts.ALWAYS;
            }
            if (rHit > aHit) {
                // Weapon missed
                this.missed = true;
                this.dodged = false;
                this.crit = false;
                return 0;
            } else {
                final var rEvade = CommonDamageEngineParts.chance();
                final var aEvade = enemy.getEvade();
                if (rEvade < aEvade) {
                    // Enemy dodged
                    this.missed = false;
                    this.dodged = true;
                    this.crit = false;
                    return 0;
                } else {
                    // Hit
                    this.missed = false;
                    this.dodged = false;
                    RandomRange rDamage;
                    if (this.crit) {
                        rDamage = new RandomRange(
                                VeryHardDamageEngine.MULTIPLIER_MIN_CRIT,
                                VeryHardDamageEngine.MULTIPLIER_MAX_CRIT);
                    } else {
                        rDamage = new RandomRange(
                                VeryHardDamageEngine.MULTIPLIER_MIN,
                                VeryHardDamageEngine.MULTIPLIER_MAX);
                    }
                    final var multiplier = rDamage.generate();
                    // Weapon Faith Power Boost
                    var faithMultiplier = CommonDamageEngineParts.FAITH_MULT_START;
                    final var fc = FaithConstants.getFaithsCount();
                    final var mainHand = acting.getItems()
                            .getEquipmentInSlot(
                                    EquipmentSlotConstants.SLOT_MAINHAND);
                    final var offHand = acting.getItems()
                            .getEquipmentInSlot(
                                    EquipmentSlotConstants.SLOT_OFFHAND);
                    if (mainHand != null && mainHand.equals(offHand)) {
                        for (var z = 0; z < fc; z++) {
                            final var fpl = mainHand.getFaithPowerLevel(z);
                            faithMultiplier += VeryHardDamageEngine.FAITH_INCREMENT_2H
                                    * fpl;
                        }
                    } else {
                        if (mainHand != null) {
                            for (var z = 0; z < fc; z++) {
                                final var fpl = mainHand.getFaithPowerLevel(z);
                                faithMultiplier += VeryHardDamageEngine.FAITH_INCREMENT
                                        * fpl;
                            }
                        }
                        if (offHand != null && offHand
                                .getEquipCategory() == EquipmentCategoryConstants.EQUIPMENT_CATEGORY_ONE_HANDED_WEAPON) {
                            for (var z = 0; z < fc; z++) {
                                final var fpl = offHand.getFaithPowerLevel(z);
                                faithMultiplier += VeryHardDamageEngine.FAITH_INCREMENT
                                        * fpl;
                            }
                        }
                    }
                    // Armor Faith Power Boost
                    var faithDR = CommonDamageEngineParts.FAITH_MULT_START;
                    final var armor = acting.getItems()
                            .getEquipmentInSlot(
                                    EquipmentSlotConstants.SLOT_BODY);
                    if (armor != null) {
                        for (var z = 0; z < fc; z++) {
                            final var fpl = armor.getFaithPowerLevel(z);
                            faithDR -= fpl
                                    * VeryHardDamageEngine.FAITH_DR_INCREMENT;
                        }
                    }
                    if (offHand != null && offHand
                            .getEquipCategory() == EquipmentCategoryConstants.EQUIPMENT_CATEGORY_ARMOR) {
                        for (var z = 0; z < fc; z++) {
                            final var fpl = offHand.getFaithPowerLevel(z);
                            faithDR -= fpl
                                    * VeryHardDamageEngine.FAITH_DR_INCREMENT;
                        }
                    }
                    final var unadjustedDamage = (int) (rawDamage * multiplier
                            * faithMultiplier
                            / CommonDamageEngineParts.MULTIPLIER_DIVIDE);
                    return (int) (unadjustedDamage * faithDR);
                }
            }
        }
    }

    private void didCrit() {
        this.crit = CommonDamageEngineParts
                .didSpecial(VeryHardDamageEngine.CRIT_CHANCE);
    }

    private void didFumble() {
        this.fumble = CommonDamageEngineParts
                .didSpecial(VeryHardDamageEngine.FUMBLE_CHANCE);
    }

    private void didPierce() {
        this.pierce = CommonDamageEngineParts
                .didSpecial(VeryHardDamageEngine.PIERCE_CHANCE);
    }

    @Override
    public boolean enemyDodged() {
        return this.dodged;
    }

    @Override
    public boolean weaponCrit() {
        return this.crit;
    }

    @Override
    public boolean weaponFumble() {
        return this.fumble;
    }

    @Override
    public boolean weaponMissed() {
        return this.missed;
    }

    @Override
    public boolean weaponPierce() {
        return this.pierce;
    }
}