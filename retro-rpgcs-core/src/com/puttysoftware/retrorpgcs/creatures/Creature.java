/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.creatures;

import java.util.Arrays;
import java.util.Objects;

import com.puttysoftware.images.BufferedImageIcon;
import com.puttysoftware.polytable.PolyTable;
import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.retrorpgcs.ai.map.MapAI;
import com.puttysoftware.retrorpgcs.ai.window.WindowAI;
import com.puttysoftware.retrorpgcs.creatures.faiths.Faith;
import com.puttysoftware.retrorpgcs.creatures.party.PartyManager;
import com.puttysoftware.retrorpgcs.effects.Effect;
import com.puttysoftware.retrorpgcs.items.ItemInventory;
import com.puttysoftware.retrorpgcs.spells.SpellBook;

public abstract class Creature {
    private static int ACTION_CAP = 1;
    private static final int MAX_EFFECTS = 100;
    private static final int BAR_SPEED_MIN = 100;
    private static final int BAR_SPEED_MAX = 1;
    public static final int FULL_HEAL_PERCENTAGE = 100;
    public static final int TEAM_PARTY = 0;
    public static final double SPEED_ADJUST_SLOWEST = 0.5;
    public static final double SPEED_ADJUST_SLOW = 0.75;
    public static final double SPEED_ADJUST_NORMAL = 1.0;
    public static final double SPEED_ADJUST_FAST = 1.5;
    public static final double SPEED_ADJUST_FASTEST = 2.0;

    public static void computeActionCap(final int rows, final int cols) {
        final var avg = (rows + cols) / 2;
        final var mult = (int) Math.sqrt(avg);
        final double temp = avg * mult;
        Creature.ACTION_CAP = (int) (Math.round(temp / 10.0) * 10.0);
    }

    static int getMaximumLevel() {
        return StatConstants.LEVEL_MAX;
    }

    // Fields
    protected BufferedImageIcon image;
    private final Statistic[] stats;
    private long experience;
    private final Effect[] effectList;
    private SpellBook spellsKnown;
    private MapAI mapAI;
    private WindowAI windowAI;
    private ItemInventory items;
    private PolyTable toNextLevel;
    private final int teamID;
    private final int perfectBonusGold;
    private int xLoc, yLoc;
    private int saveX, saveY;

    // Constructor
    protected Creature(final boolean hasCombatItems, final int tid) {
        this.teamID = tid;
        this.stats = new Statistic[StatConstants.MAX_STORED_STATS];
        for (var x = 0; x < StatConstants.MAX_STORED_STATS; x++) {
            this.stats[x] = new Statistic();
        }
        this.stats[StatConstants.STAT_CURRENT_HP].setHasMax(true);
        this.stats[StatConstants.STAT_CURRENT_MP].setHasMax(true);
        this.stats[StatConstants.STAT_LEVEL].setHasMax(true);
        this.stats[StatConstants.STAT_LOAD].setHasMax(true);
        this.stats[StatConstants.STAT_CURRENT_HP]
                .setMaxID(StatConstants.STAT_MAXIMUM_HP);
        this.stats[StatConstants.STAT_CURRENT_MP]
                .setMaxID(StatConstants.STAT_MAXIMUM_MP);
        this.stats[StatConstants.STAT_LEVEL]
                .setMaxID(StatConstants.STAT_MAX_LEVEL);
        this.stats[StatConstants.STAT_LOAD]
                .setMaxID(StatConstants.STAT_CAPACITY);
        this.stats[StatConstants.STAT_VITALITY].setMinVal(1);
        this.stats[StatConstants.STAT_AGILITY].setMinVal(1);
        this.stats[StatConstants.STAT_VITALITY].setValue(1);
        this.stats[StatConstants.STAT_AGILITY].setValue(1);
        this.effectList = new Effect[Creature.MAX_EFFECTS];
        this.spellsKnown = null;
        this.windowAI = null;
        this.items = new ItemInventory(hasCombatItems);
        this.toNextLevel = null;
        this.perfectBonusGold = this.getInitialPerfectBonusGold();
        this.xLoc = -1;
        this.yLoc = -1;
        this.saveX = -1;
        this.saveY = -1;
    }

    public final void applyEffect(final Effect e) {
        int x;
        for (x = 0; x < this.effectList.length; x++) {
            if (this.get(x) == null) {
                this.set(x, e);
                e.scaleEffect(Effect.EFFECT_ADD, this);
                return;
            }
        }
    }

    public boolean checkLevelUp() {
        if (this.toNextLevel != null) {
            return this.experience >= this.getToNextLevelValue();
        } else {
            return false;
        }
    }

    public final void cullInactiveEffects() {
        int x;
        for (x = 0; x < this.effectList.length; x++) {
            try {
                final var e = this.get(x);
                if (!e.isActive()) {
                    this.set(x, null);
                }
            } catch (final NullPointerException np) {
                // Do nothing
            } catch (final ArrayIndexOutOfBoundsException aioob) {
                // Do nothing
            }
        }
    }

    public final void doDamage(final int damage) {
        this.offsetCurrentHP(-damage);
        this.fixStatValue(StatConstants.STAT_CURRENT_HP);
    }

    public final void doDamageMultiply(final double damage, final boolean max) {
        this.offsetCurrentHPMultiply(damage, max);
        this.fixStatValue(StatConstants.STAT_CURRENT_HP);
    }

    public final void drain(final int cost) {
        this.offsetCurrentMP(-cost);
        this.fixStatValue(StatConstants.STAT_CURRENT_MP);
    }

    public final void drainMultiply(final double cost, final boolean max) {
        this.offsetCurrentMPMultiply(cost, max);
        this.fixStatValue(StatConstants.STAT_CURRENT_MP);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || !(obj instanceof Creature)) {
            return false;
        }
        final var other = (Creature) obj;
        if (!Arrays.equals(this.effectList, other.effectList)) {
            return false;
        }
        if (this.experience != other.experience) {
            return false;
        }
        if (!Objects.equals(this.items, other.items)) {
            return false;
        }
        if (!Objects.equals(this.spellsKnown, other.spellsKnown)) {
            return false;
        }
        if (!Arrays.equals(this.stats, other.stats)) {
            return false;
        }
        if (this.teamID != other.teamID) {
            return false;
        }
        if (!Objects.equals(this.toNextLevel, other.toNextLevel)) {
            return false;
        }
        return true;
    }

    public final void extendEffect(final Effect e, final int rounds) {
        final var index = this.indexOf(e);
        if (index != -1) {
            this.get(index).extendEffect(rounds);
        }
    }

    private void fixStatValue(final int stat) {
        if (this.getHasStatMin(stat)
                && (this.getStat(stat) < this.getStatMin(stat))) {
            this.setStatFixed(stat, this.getStatMin(stat));
        }
        if (this.getHasStatMax(stat)
                && (this.getStat(stat) > this.getStat(this.getStatMax(stat)))) {
            this.setStatFixed(stat, this.getStat(this.getStatMax(stat)));
        }
    }

    private final Effect get(final int x) {
        return this.effectList[x];
    }

    public final int getActionBarSpeed() {
        return Math.max(Creature.BAR_SPEED_MIN,
                Math.min(
                        (Creature.BAR_SPEED_MAX - this.getBaseSpeed())
                                / Creature.BAR_SPEED_MIN,
                        Creature.BAR_SPEED_MAX));
    }

    public final int getActiveEffectCount() {
        int x, c;
        c = 0;
        for (x = 0; x < this.effectList.length; x++) {
            try {
                final var e = this.get(x);
                if (e.isActive()) {
                    c++;
                }
            } catch (final NullPointerException np) {
                // Do nothing
            } catch (final ArrayIndexOutOfBoundsException aioob) {
                // Do nothing
            }
        }
        return c;
    }

    public final int getAgility() {
        return this.getStat(StatConstants.STAT_AGILITY);
    }

    public final String getAllCurrentEffectMessages() {
        int x;
        final var sb = new StringBuilder(Effect.getNullMessage());
        for (x = 0; x < this.effectList.length; x++) {
            try {
                sb.append(this.get(x).getCurrentMessage());
                sb.append("\n");
            } catch (final NullPointerException np) {
                // Do nothing
            } catch (final ArrayIndexOutOfBoundsException aioob) {
                // Do nothing
            }
        }
        var s = sb.toString();
        // Strip final newline character, if it exists
        if (!s.equals(Effect.getNullMessage())) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    public int getAttack() {
        return (int) (this.getStrength() * StatConstants.FACTOR_STRENGTH_ATTACK
                + this.getItems().getTotalPower()
                        * StatConstants.FACTOR_POWER_ATTACK);
    }

    public final int getAttacksPerRound() {
        return this.getStat(StatConstants.STAT_ATTACKS_PER_ROUND);
    }

    protected final int getBaseSpeed() {
        return (int) (this.getEffectedStat(StatConstants.STAT_AGILITY)
                * StatConstants.FACTOR_AGILITY_SPEED
                - (this.items.getTotalEquipmentWeight()
                        + this.items.getTotalInventoryWeight())
                        * StatConstants.FACTOR_LOAD_SPEED);
    }

    public final int getBlock() {
        return this.getStat(StatConstants.STAT_BLOCK);
    }

    public int getCapacity() {
        return Math.max(StatConstants.MIN_CAPACITY, (int) (this.getStrength()
                * StatConstants.FACTOR_STRENGTH_CAPACITY
                + this.getAgility() * StatConstants.FACTOR_AGILITY_CAPACITY));
    }

    public final String getCompleteEffectString() {
        int x;
        var s = "";
        for (x = 0; x < this.effectList.length; x++) {
            try {
                s += this.get(x).getEffectString() + "\n";
            } catch (final NullPointerException np) {
                // Do nothing
            } catch (final ArrayIndexOutOfBoundsException aioob) {
                // Do nothing
            }
        }
        // Strip final newline character, if it exists
        if (!s.equals(Effect.getNullMessage())) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    public final String[] getCompleteEffectStringArray() {
        int x, z;
        z = this.getActiveEffectCount();
        final var s = new String[z];
        var counter = 0;
        for (x = 0; x < z; x++) {
            if (this.effectList[x] != null) {
                s[counter] = this.effectList[x].getEffectString();
                counter++;
            }
        }
        return s;
    }

    public final int getCurrentHP() {
        return this.getStat(StatConstants.STAT_CURRENT_HP);
    }

    public final int getCurrentMP() {
        return this.getStat(StatConstants.STAT_CURRENT_MP);
    }

    public int getDefense() {
        return (int) (this.getBlock() * StatConstants.FACTOR_BLOCK_DEFENSE
                + this.getItems().getTotalAbsorb()
                        * StatConstants.FACTOR_ABSORB_DEFENSE);
    }

    public final double getEffectedAttack() {
        return this.getEffectedStat(StatConstants.STAT_ATTACK);
    }

    private final int getEffectedMaximumHP() {
        return (int) this.getEffectedStat(StatConstants.STAT_MAXIMUM_HP);
    }

    private final int getEffectedMaximumMP() {
        return (int) this.getEffectedStat(StatConstants.STAT_MAXIMUM_MP);
    }

    public final double getEffectedStat(final int stat) {
        int x, s, p;
        s = 0;
        p = this.getStat(stat);
        for (x = 0; x < this.effectList.length; x++) {
            try {
                final var e = this.get(x);
                p *= e.getEffect(Effect.EFFECT_MULTIPLY, stat);
            } catch (final NullPointerException np) {
                // Do nothing
            } catch (final ArrayIndexOutOfBoundsException aioob) {
                // Do nothing
            }
        }
        for (x = 0; x < this.effectList.length; x++) {
            try {
                final var e = this.get(x);
                s += e.getEffect(Effect.EFFECT_ADD, stat);
            } catch (final NullPointerException np) {
                // Do nothing
            } catch (final ArrayIndexOutOfBoundsException aioob) {
                // Do nothing
            }
        }
        return p + s;
    }

    public final int getEvade() {
        final var chance = StatConstants.EVADE_BASE;
        final var agilityContrib = Math.max(0,
                this.getEffectedStat(StatConstants.STAT_AGILITY))
                * StatConstants.FACTOR_AGILITY_EVADE;
        final var luckContrib = Math.max(0,
                this.getEffectedStat(StatConstants.STAT_LUCK))
                * StatConstants.FACTOR_LUCK_EVADE;
        final var modifier = (int) Math.round(agilityContrib + luckContrib);
        return Math.min(chance + modifier, StatConstants.EVADE_MAX);
    }

    public final long getExperience() {
        return this.experience;
    }

    public Faith getFaith() {
        return null;
    }

    public String getFightingWhatString() {
        final var enemyName = this.getName();
        final var vowel = this.isFirstLetterVowel(enemyName);
        String fightingWhat = null;
        if (vowel) {
            fightingWhat = "You're fighting an " + enemyName;
        } else {
            fightingWhat = "You're fighting a " + enemyName;
        }
        return fightingWhat;
    }

    public final int getGold() {
        return this.getStat(StatConstants.STAT_GOLD);
    }

    private boolean getHasStatMax(final int stat) {
        try {
            return this.stats[stat].hasMax();
        } catch (final ArrayIndexOutOfBoundsException aioob) {
            return false;
        }
    }

    private boolean getHasStatMin(final int stat) {
        try {
            return this.stats[stat].hasMin();
        } catch (final ArrayIndexOutOfBoundsException aioob) {
            return false;
        }
    }

    public final int getHit() {
        final var chance = StatConstants.HIT_BASE;
        final var strengthContrib = Math.max(0,
                this.getEffectedStat(StatConstants.STAT_STRENGTH))
                * StatConstants.FACTOR_STRENGTH_HIT;
        final var luckContrib = Math.max(0,
                this.getEffectedStat(StatConstants.STAT_LUCK))
                * StatConstants.FACTOR_LUCK_HIT;
        final var modifier = (int) Math.round(strengthContrib + luckContrib);
        return Math.min(chance + modifier, StatConstants.HIT_MAX);
    }

    public final String getHPString() {
        return this.getCurrentHP() + "/" + this.getEffectedMaximumHP();
    }

    public final BufferedImageIcon getImage() {
        if (this.image == null) {
            this.image = this.getInitialImage();
        }
        return this.image;
    }

    protected abstract BufferedImageIcon getInitialImage();

    protected int getInitialPerfectBonusGold() {
        return 0;
    }

    public final int getIntelligence() {
        return this.getStat(StatConstants.STAT_INTELLIGENCE);
    }

    public final ItemInventory getItems() {
        return this.items;
    }

    public final int getLevel() {
        return this.getStat(StatConstants.STAT_LEVEL);
    }

    public int getLevelDifference() {
        return this.getLevel() - PartyManager.getParty().getLeader().getLevel();
    }

    public final int getLoad() {
        return this.getStat(StatConstants.STAT_LOAD);
    }

    public final int getLuck() {
        return this.getStat(StatConstants.STAT_LUCK);
    }

    public final MapAI getMapAI() {
        return this.mapAI;
    }

    public int getMapBattleActionsPerRound() {
        final var value = (int) Math
                .sqrt(Math.ceil(this.getEffectedStat(StatConstants.STAT_SPEED)
                        * StatConstants.FACTOR_SPEED_MAP_ACTIONS_PER_ROUND));
        return Math.max(1, Math.min(Creature.ACTION_CAP, value));
    }

    private long getMaximumExperience() {
        if (this.toNextLevel != null) {
            return this.toNextLevel.evaluate(Creature.getMaximumLevel());
        } else {
            return Long.MAX_VALUE;
        }
    }

    public int getMaximumHP() {
        return (int) (this.getVitality()
                * StatConstants.FACTOR_VITALITY_HEALTH);
    }

    public int getMaximumMP() {
        return (int) (this.getIntelligence()
                * StatConstants.FACTOR_INTELLIGENCE_MAGIC);
    }

    public final String getMPString() {
        return this.getCurrentMP() + "/" + this.getEffectedMaximumMP();
    }

    public abstract String getName();

    public int getPerfectBonusGold() {
        return this.perfectBonusGold;
    }

    public abstract int getSpeed();

    public final SpellBook getSpellBook() {
        return this.spellsKnown;
    }

    public final int getSpellsPerRound() {
        return this.getStat(StatConstants.STAT_SPELLS_PER_ROUND);
    }

    public final int getStat(final int stat) {
        try {
            return this.stats[stat].getValue();
        } catch (final ArrayIndexOutOfBoundsException aioob) {
            switch (stat) {
            case StatConstants.STAT_ATTACK:
                return this.getAttack();
            case StatConstants.STAT_DEFENSE:
                return this.getDefense();
            case StatConstants.STAT_MAXIMUM_HP:
                return this.getMaximumHP();
            case StatConstants.STAT_MAXIMUM_MP:
                return this.getMaximumMP();
            case StatConstants.STAT_SPEED:
                return this.getSpeed();
            case StatConstants.STAT_HIT:
                return this.getHit();
            case StatConstants.STAT_EVADE:
                return this.getEvade();
            case StatConstants.STAT_CAPACITY:
                return this.getCapacity();
            case StatConstants.STAT_MAX_LEVEL:
                return Creature.getMaximumLevel();
            default:
                return 0;
            }
        }
    }

    private int getStatMax(final int stat) {
        try {
            return this.stats[stat].getMaxID();
        } catch (final ArrayIndexOutOfBoundsException aioob) {
            return 0;
        }
    }

    private int getStatMin(final int stat) {
        try {
            return this.stats[stat].getMinVal();
        } catch (final ArrayIndexOutOfBoundsException aioob) {
            return 0;
        }
    }

    public final int getStrength() {
        return this.getStat(StatConstants.STAT_STRENGTH);
    }

    public final int getTeamID() {
        return this.teamID;
    }

    public final long getToNextLevelValue() {
        if (this.toNextLevel != null) {
            if (this.getLevel() == Creature.getMaximumLevel()) {
                return this.getExperience();
            } else {
                return this.toNextLevel.evaluate(this.getLevel() + 1);
            }
        } else {
            return 0;
        }
    }

    public final int getVitality() {
        return this.getStat(StatConstants.STAT_VITALITY);
    }

    public WindowAI getWindowAI() {
        return this.windowAI;
    }

    public int getWindowBattleActionsPerRound() {
        final var value = (int) Math
                .sqrt(Math.ceil(this.getEffectedStat(StatConstants.STAT_SPEED)
                        * StatConstants.FACTOR_SPEED_WINDOW_ACTIONS_PER_ROUND));
        return Math.max(1, Math.min(Creature.ACTION_CAP, value));
    }

    public final int getX() {
        return this.xLoc;
    }

    public final int getY() {
        return this.yLoc;
    }

    @Override
    public int hashCode() {
        final var prime = 31;
        var result = super.hashCode();
        result = prime * result + Arrays.hashCode(this.effectList);
        result = prime * result
                + (int) (this.experience ^ this.experience >>> 32);
        result = prime * result
                + (this.items == null ? 0 : this.items.hashCode());
        result = prime * result
                + (this.spellsKnown == null ? 0 : this.spellsKnown.hashCode());
        result = prime * result + Arrays.hashCode(this.stats);
        result = prime * result + this.teamID;
        return prime * result
                + (this.toNextLevel == null ? 0 : this.toNextLevel.hashCode());
    }

    public final boolean hasMapAI() {
        return this.mapAI != null;
    }

    public final void heal(final int amount) {
        this.offsetCurrentHP(amount);
        this.fixStatValue(StatConstants.STAT_CURRENT_HP);
    }

    public final void healAndRegenerateFully() {
        this.healFully();
        this.regenerateFully();
    }

    protected final void healFully() {
        this.setCurrentHP(this.getEffectedMaximumHP());
    }

    public final void healMultiply(final double amount, final boolean max) {
        this.offsetCurrentHPMultiply(amount, max);
        this.fixStatValue(StatConstants.STAT_CURRENT_HP);
    }

    public final void healPercentage(final int percent) {
        var fP = percent;
        if (fP > Creature.FULL_HEAL_PERCENTAGE) {
            fP = Creature.FULL_HEAL_PERCENTAGE;
        }
        if (fP < 0) {
            fP = 0;
        }
        final var fPMultiplier = fP / (double) Creature.FULL_HEAL_PERCENTAGE;
        final var difference = this.getEffectedMaximumHP()
                - this.getCurrentHP();
        var modValue = (int) (difference * fPMultiplier);
        if (modValue <= 0) {
            modValue = 1;
        }
        this.offsetCurrentHP(modValue);
        this.fixStatValue(StatConstants.STAT_CURRENT_HP);
    }

    private final int indexOf(final Effect e) {
        int x;
        for (x = 0; x < this.effectList.length; x++) {
            final var le = this.get(x);
            if (le != null) {
                if (e.equals(le)) {
                    return x;
                }
            } else {
                return -1;
            }
        }
        return -1;
    }

    public final boolean isAlive() {
        return this.getCurrentHP() > 0;
    }

    public final boolean isEffectActive(final Effect e) {
        final var index = this.indexOf(e);
        if (index != -1) {
            return this.get(index).isActive();
        } else {
            return false;
        }
    }

    protected boolean isFirstLetterVowel(final String s) {
        final var firstLetter = s.substring(0, 1);
        if (firstLetter.equalsIgnoreCase("A")
                || firstLetter.equalsIgnoreCase("E")
                || firstLetter.equalsIgnoreCase("I")
                || firstLetter.equalsIgnoreCase("O")
                || firstLetter.equalsIgnoreCase("U")) {
            return true;
        } else {
            return false;
        }
    }

    public final void levelUp() {
        this.offsetLevel(1);
        this.levelUpHook();
    }

    protected abstract void levelUpHook();

    public abstract void loadCreature();

    public final void offsetAgility(final int value) {
        this.stats[StatConstants.STAT_AGILITY].offsetValue(value);
        this.fixStatValue(StatConstants.STAT_AGILITY);
    }

    public final void offsetBlock(final int value) {
        this.stats[StatConstants.STAT_BLOCK].offsetValue(value);
        this.fixStatValue(StatConstants.STAT_BLOCK);
    }

    private final void offsetCurrentHP(final int value) {
        this.stats[StatConstants.STAT_CURRENT_HP].offsetValue(value);
        this.fixStatValue(StatConstants.STAT_CURRENT_HP);
    }

    private final void offsetCurrentHPMultiply(final double value,
            final boolean max) {
        this.stats[StatConstants.STAT_CURRENT_HP].offsetValueMultiply(value,
                max, this.getStat(StatConstants.STAT_MAXIMUM_HP));
        this.fixStatValue(StatConstants.STAT_CURRENT_HP);
    }

    public final void offsetCurrentMP(final int value) {
        this.stats[StatConstants.STAT_CURRENT_MP].offsetValue(value);
        this.fixStatValue(StatConstants.STAT_CURRENT_MP);
    }

    private final void offsetCurrentMPMultiply(final double value,
            final boolean max) {
        this.stats[StatConstants.STAT_CURRENT_MP].offsetValueMultiply(value,
                max, this.getStat(StatConstants.STAT_MAXIMUM_MP));
        this.fixStatValue(StatConstants.STAT_CURRENT_MP);
    }

    public final void offsetExperience(final long value) {
        if (this.experience + value > this.getMaximumExperience()) {
            this.experience = this.getMaximumExperience();
        } else if (this.experience + value < 0) {
            this.experience = 0;
        } else {
            this.experience += value;
        }
    }

    public final void offsetExperiencePercentage(final int percentage) {
        var fixedPercentage = percentage;
        if (fixedPercentage > 100) {
            fixedPercentage = 100;
        } else if (fixedPercentage < -100) {
            fixedPercentage = -100;
        }
        if (fixedPercentage > 0) {
            final var adjustment = this.experience * fixedPercentage / 100;
            this.offsetExperience(adjustment);
        } else if (fixedPercentage < 0) {
            final var adjustment = this.experience * -fixedPercentage / 100;
            this.offsetExperience(-adjustment);
        }
    }

    public void offsetGold(final int value) {
        this.stats[StatConstants.STAT_GOLD].offsetValue(value);
        this.fixStatValue(StatConstants.STAT_GOLD);
    }

    public final void offsetIntelligence(final int value) {
        this.stats[StatConstants.STAT_INTELLIGENCE].offsetValue(value);
        this.fixStatValue(StatConstants.STAT_INTELLIGENCE);
    }

    private final void offsetLevel(final int value) {
        this.stats[StatConstants.STAT_LEVEL].offsetValue(value);
        this.fixStatValue(StatConstants.STAT_LEVEL);
    }

    public final void offsetLoad(final int value) {
        this.stats[StatConstants.STAT_LOAD].offsetValue(value);
        this.fixStatValue(StatConstants.STAT_LOAD);
    }

    public final void offsetLuck(final int value) {
        this.stats[StatConstants.STAT_LUCK].offsetValue(value);
        this.fixStatValue(StatConstants.STAT_LUCK);
    }

    public final void offsetStrength(final int value) {
        this.stats[StatConstants.STAT_STRENGTH].offsetValue(value);
        this.fixStatValue(StatConstants.STAT_STRENGTH);
    }

    public final void offsetVitality(final int value) {
        this.stats[StatConstants.STAT_VITALITY].offsetValue(value);
        this.fixStatValue(StatConstants.STAT_VITALITY);
    }

    public final void offsetX(final int newX) {
        this.xLoc += newX;
    }

    public final void offsetY(final int newY) {
        this.yLoc += newY;
    }

    public final void regenerate(final int amount) {
        this.offsetCurrentMP(amount);
        this.fixStatValue(StatConstants.STAT_CURRENT_MP);
    }

    private final void regenerateFully() {
        this.setCurrentMP(this.getMaximumMP());
    }

    public final void regenerateMultiply(final double amount,
            final boolean max) {
        this.offsetCurrentMPMultiply(amount, max);
        this.fixStatValue(StatConstants.STAT_CURRENT_MP);
    }

    public final void regeneratePercentage(final int percent) {
        var fP = percent;
        if (fP > Creature.FULL_HEAL_PERCENTAGE) {
            fP = Creature.FULL_HEAL_PERCENTAGE;
        }
        if (fP < 0) {
            fP = 0;
        }
        final var fPMultiplier = fP / (double) Creature.FULL_HEAL_PERCENTAGE;
        final var difference = this.getMaximumMP() - this.getCurrentMP();
        var modValue = (int) (difference * fPMultiplier);
        if (modValue <= 0) {
            modValue = 1;
        }
        this.offsetCurrentMP(modValue);
        this.fixStatValue(StatConstants.STAT_CURRENT_MP);
    }

    public final void restoreLocation() {
        this.xLoc = this.saveX;
        this.yLoc = this.saveY;
    }

    public final void saveLocation() {
        this.saveX = this.xLoc;
        this.saveY = this.yLoc;
    }

    private final void set(final int x, final Effect e) {
        this.effectList[x] = e;
    }

    public final void setAgility(final int value) {
        this.setStat(StatConstants.STAT_AGILITY, value);
    }

    public final void setAttacksPerRound(final int value) {
        this.setStat(StatConstants.STAT_ATTACKS_PER_ROUND, value);
    }

    public final void setBlock(final int value) {
        this.setStat(StatConstants.STAT_BLOCK, value);
    }

    public final void setCurrentHP(final int value) {
        this.setStat(StatConstants.STAT_CURRENT_HP, value);
    }

    public final void setCurrentMP(final int value) {
        this.setStat(StatConstants.STAT_CURRENT_MP, value);
    }

    public final void setExperience(final long value) {
        if (value > this.getMaximumExperience()) {
            this.experience = this.getMaximumExperience();
        } else {
            this.experience = value;
        }
    }

    public final void setGold(final int value) {
        this.setStat(StatConstants.STAT_GOLD, value);
    }

    public final void setIntelligence(final int value) {
        this.setStat(StatConstants.STAT_INTELLIGENCE, value);
    }

    public final void setItems(final ItemInventory newItems) {
        this.items = newItems;
    }

    public final void setLevel(final int value) {
        this.setStat(StatConstants.STAT_LEVEL, value);
    }

    public final void setLoad(final int value) {
        this.setStat(StatConstants.STAT_LOAD, value);
    }

    public final void setLuck(final int value) {
        this.setStat(StatConstants.STAT_LUCK, value);
    }

    public final void setMapAI(final MapAI newAI) {
        this.mapAI = newAI;
    }

    public final void setSpellBook(final SpellBook book) {
        this.spellsKnown = book;
    }

    public final void setSpellsPerRound(final int value) {
        this.setStat(StatConstants.STAT_SPELLS_PER_ROUND, value);
    }

    private final void setStat(final int stat, final int value) {
        int dynValue;
        if (this.stats[stat].getDynamism() != 0) {
            final var r = new RandomRange(
                    -this.stats[stat].getDynamism(),
                    this.stats[stat].getDynamism());
            dynValue = value + r.generate();
        } else {
            dynValue = value;
        }
        this.stats[stat].setValue(dynValue);
        this.fixStatValue(stat);
    }

    private void setStatFixed(final int stat, final int value) {
        try {
            this.stats[stat].setValue(value);
        } catch (final ArrayIndexOutOfBoundsException aioob) {
            // Do nothing
        }
    }

    public final void setStrength(final int value) {
        this.setStat(StatConstants.STAT_STRENGTH, value);
    }

    public final void setToNextLevel(final PolyTable nextLevelEquation) {
        this.toNextLevel = nextLevelEquation;
    }

    public final void setVitality(final int value) {
        this.setStat(StatConstants.STAT_VITALITY, value);
    }

    public final void setWindowAI(final WindowAI newAI) {
        this.windowAI = newAI;
    }

    public final void setX(final int newX) {
        this.xLoc = newX;
    }

    public final void setY(final int newY) {
        this.yLoc = newY;
    }

    public final void stripAllEffects() {
        int x;
        for (x = 0; x < this.effectList.length; x++) {
            this.set(x, null);
        }
    }

    public final void useEffects() {
        int x;
        for (x = 0; x < this.effectList.length; x++) {
            try {
                this.get(x).useEffect(this);
            } catch (final NullPointerException np) {
                // Do nothing
            } catch (final ArrayIndexOutOfBoundsException aioob) {
                // Do nothing
            }
        }
    }
}
