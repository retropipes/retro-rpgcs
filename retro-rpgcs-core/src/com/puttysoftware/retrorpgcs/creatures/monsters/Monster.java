/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.creatures.monsters;

import com.puttysoftware.images.BufferedImageIcon;
import com.puttysoftware.randomrange.RandomLongRange;
import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.retrorpgcs.ai.map.MapAI;
import com.puttysoftware.retrorpgcs.ai.map.MapAIPicker;
import com.puttysoftware.retrorpgcs.ai.window.WindowAI;
import com.puttysoftware.retrorpgcs.ai.window.WindowAIPicker;
import com.puttysoftware.retrorpgcs.creatures.Creature;
import com.puttysoftware.retrorpgcs.creatures.faiths.Faith;
import com.puttysoftware.retrorpgcs.creatures.faiths.FaithManager;
import com.puttysoftware.retrorpgcs.creatures.party.PartyManager;
import com.puttysoftware.retrorpgcs.creatures.party.PartyMember;
import com.puttysoftware.retrorpgcs.prefs.PreferencesManager;
import com.puttysoftware.retrorpgcs.resourcemanagers.MonsterImageManager;
import com.puttysoftware.retrorpgcs.resourcemanagers.MonsterNames;
import com.puttysoftware.retrorpgcs.shops.Shop;
import com.puttysoftware.retrorpgcs.spells.SpellBook;

public final class Monster extends Creature {
    // Constants
    private static final int STAT_MULT_VERY_EASY = 2;
    private static final int STAT_MULT_EASY = 3;
    private static final int STAT_MULT_NORMAL = 5;
    private static final int STAT_MULT_HARD = 7;
    private static final int STAT_MULT_VERY_HARD = 9;
    private static final double GOLD_MULT_VERY_EASY = 2.0;
    private static final double GOLD_MULT_EASY = 1.5;
    private static final double GOLD_MULT_NORMAL = 1.0;
    private static final double GOLD_MULT_HARD = 0.75;
    private static final double GOLD_MULT_VERY_HARD = 0.5;
    private static final double EXP_MULT_VERY_EASY = 1.2;
    private static final double EXP_MULT_EASY = 1.1;
    private static final double EXP_MULT_NORMAL = 1.0;
    private static final double EXP_MULT_HARD = 0.9;
    private static final double EXP_MULT_VERY_HARD = 0.8;
    private static final double MINIMUM_EXPERIENCE_RANDOM_VARIANCE = -5.0 / 2.0;
    private static final double MAXIMUM_EXPERIENCE_RANDOM_VARIANCE = 5.0 / 2.0;
    private static final int PERFECT_GOLD_MIN = 1;
    private static final int PERFECT_GOLD_MAX = 3;
    private static final int BATTLES_SCALE_FACTOR = 2;
    private static final int BATTLES_START = 2;
    // Fields
    private String type;
    protected Element element;

    public Monster() {
        super(true, 1);
        this.setWindowAI(Monster.getInitialWindowAI());
        this.setMapAI(Monster.getInitialMapAI());
        this.element = new Element(FaithManager.getFaith(0));
        final SpellBook spells = new SystemMonsterSpellBook();
        spells.learnAllSpells();
        this.setSpellBook(spells);
        this.element = Monster.getInitialElement();
        this.image = this.getInitialImage();
    }

    @Override
    public void loadCreature() {
        final int newLevel = PartyManager.getParty().getTowerLevel() + 1;
        this.setLevel(newLevel);
        this.setVitality(this.getInitialVitality());
        this.setCurrentHP(this.getMaximumHP());
        this.setIntelligence(this.getInitialIntelligence());
        this.setCurrentMP(this.getMaximumMP());
        this.setStrength(this.getInitialStrength());
        this.setBlock(this.getInitialBlock());
        this.setAgility(this.getInitialAgility());
        this.setLuck(this.getInitialLuck());
        this.setGold(this.getInitialGold());
        this.setExperience((long) (this.getInitialExperience()
                * this.adjustForLevelDifference()));
        this.setAttacksPerRound(1);
        this.setSpellsPerRound(1);
        this.image = this.getInitialImage();
    }

    private int getInitialStrength() {
        final RandomRange r = new RandomRange(1, Math.max(
                this.getLevel() * Monster.getStatMultiplierForDifficulty(), 1));
        return r.generate();
    }

    private int getInitialBlock() {
        final RandomRange r = new RandomRange(0,
                this.getLevel() * Monster.getStatMultiplierForDifficulty());
        return r.generate();
    }

    private long getInitialExperience() {
        int minvar, maxvar;
        minvar = (int) (this.getLevel()
                * Monster.MINIMUM_EXPERIENCE_RANDOM_VARIANCE);
        maxvar = (int) (this.getLevel()
                * Monster.MAXIMUM_EXPERIENCE_RANDOM_VARIANCE);
        final RandomLongRange r = new RandomLongRange(minvar, maxvar);
        final long expbase = PartyManager.getParty().getPartyMaxToNextLevel();
        final long factor = this.getBattlesToNextLevel();
        return (int) (expbase / factor
                + r.generate() * this.adjustForLevelDifference()
                        * Monster.getExpMultiplierForDifficulty());
    }

    private int getInitialGold() {
        final PartyMember playerCharacter = PartyManager.getParty().getLeader();
        final int needed = Shop.getEquipmentCost(playerCharacter.getLevel() + 1)
                * 4;
        final int factor = this.getBattlesToNextLevel();
        final int min = 0;
        final int max = needed / factor * 2;
        final RandomRange r = new RandomRange(min, max);
        return (int) (r.generate() * this.adjustForLevelDifference()
                * Monster.getGoldMultiplierForDifficulty());
    }

    private int getInitialAgility() {
        final RandomRange r = new RandomRange(1, Math.max(
                this.getLevel() * Monster.getStatMultiplierForDifficulty(), 1));
        return r.generate();
    }

    private int getInitialVitality() {
        final RandomRange r = new RandomRange(1, Math.max(
                this.getLevel() * Monster.getStatMultiplierForDifficulty(), 1));
        return r.generate();
    }

    private int getInitialIntelligence() {
        final RandomRange r = new RandomRange(0,
                this.getLevel() * Monster.getStatMultiplierForDifficulty());
        return r.generate();
    }

    private int getInitialLuck() {
        final RandomRange r = new RandomRange(0,
                this.getLevel() * Monster.getStatMultiplierForDifficulty());
        return r.generate();
    }

    private static int getStatMultiplierForDifficulty() {
        final int difficulty = PreferencesManager.getGameDifficulty();
        if (difficulty == PreferencesManager.DIFFICULTY_VERY_EASY) {
            return Monster.STAT_MULT_VERY_EASY;
        } else if (difficulty == PreferencesManager.DIFFICULTY_EASY) {
            return Monster.STAT_MULT_EASY;
        } else if (difficulty == PreferencesManager.DIFFICULTY_NORMAL) {
            return Monster.STAT_MULT_NORMAL;
        } else if (difficulty == PreferencesManager.DIFFICULTY_HARD) {
            return Monster.STAT_MULT_HARD;
        } else if (difficulty == PreferencesManager.DIFFICULTY_VERY_HARD) {
            return Monster.STAT_MULT_VERY_HARD;
        } else {
            return Monster.STAT_MULT_NORMAL;
        }
    }

    private static double getGoldMultiplierForDifficulty() {
        final int difficulty = PreferencesManager.getGameDifficulty();
        if (difficulty == PreferencesManager.DIFFICULTY_VERY_EASY) {
            return Monster.GOLD_MULT_VERY_EASY;
        } else if (difficulty == PreferencesManager.DIFFICULTY_EASY) {
            return Monster.GOLD_MULT_EASY;
        } else if (difficulty == PreferencesManager.DIFFICULTY_NORMAL) {
            return Monster.GOLD_MULT_NORMAL;
        } else if (difficulty == PreferencesManager.DIFFICULTY_HARD) {
            return Monster.GOLD_MULT_HARD;
        } else if (difficulty == PreferencesManager.DIFFICULTY_VERY_HARD) {
            return Monster.GOLD_MULT_VERY_HARD;
        } else {
            return Monster.GOLD_MULT_NORMAL;
        }
    }

    private static double getExpMultiplierForDifficulty() {
        final int difficulty = PreferencesManager.getGameDifficulty();
        if (difficulty == PreferencesManager.DIFFICULTY_VERY_EASY) {
            return Monster.EXP_MULT_VERY_EASY;
        } else if (difficulty == PreferencesManager.DIFFICULTY_EASY) {
            return Monster.EXP_MULT_EASY;
        } else if (difficulty == PreferencesManager.DIFFICULTY_NORMAL) {
            return Monster.EXP_MULT_NORMAL;
        } else if (difficulty == PreferencesManager.DIFFICULTY_HARD) {
            return Monster.EXP_MULT_HARD;
        } else if (difficulty == PreferencesManager.DIFFICULTY_VERY_HARD) {
            return Monster.EXP_MULT_VERY_HARD;
        } else {
            return Monster.EXP_MULT_NORMAL;
        }
    }

    @Override
    protected BufferedImageIcon getInitialImage() {
        if (this.getLevel() == 0) {
            return null;
        } else {
            final String[] types = MonsterNames.getAllNames();
            final RandomRange r = new RandomRange(0, types.length - 1);
            this.setType(types[r.generate()]);
            return MonsterImageManager.getImage(this.getType(),
                    this.getElement());
        }
    }

    private static Element getInitialElement() {
        return new Element(FaithManager.getRandomFaith());
    }

    @Override
    public String getName() {
        return this.element.getName() + " " + this.type;
    }

    @Override
    public Faith getFaith() {
        return this.element.getFaith();
    }

    @Override
    public boolean checkLevelUp() {
        return false;
    }

    @Override
    protected void levelUpHook() {
        // Do nothing
    }

    @Override
    protected final int getInitialPerfectBonusGold() {
        final int tough = this.getToughness();
        final int min = tough * Monster.PERFECT_GOLD_MIN;
        final int max = tough * Monster.PERFECT_GOLD_MAX;
        final RandomRange r = new RandomRange(min, max);
        return (int) (r.generate() * this.adjustForLevelDifference());
    }

    @Override
    public int getSpeed() {
        final int difficulty = PreferencesManager.getGameDifficulty();
        final int base = this.getBaseSpeed();
        if (difficulty == PreferencesManager.DIFFICULTY_VERY_EASY) {
            return (int) (base * Creature.SPEED_ADJUST_SLOWEST);
        } else if (difficulty == PreferencesManager.DIFFICULTY_EASY) {
            return (int) (base * Creature.SPEED_ADJUST_SLOW);
        } else if (difficulty == PreferencesManager.DIFFICULTY_NORMAL) {
            return (int) (base * Creature.SPEED_ADJUST_NORMAL);
        } else if (difficulty == PreferencesManager.DIFFICULTY_HARD) {
            return (int) (base * Creature.SPEED_ADJUST_FAST);
        } else if (difficulty == PreferencesManager.DIFFICULTY_VERY_HARD) {
            return (int) (base * Creature.SPEED_ADJUST_FASTEST);
        } else {
            return (int) (base * Creature.SPEED_ADJUST_NORMAL);
        }
    }

    private int getToughness() {
        return this.getStrength() + this.getBlock() + this.getAgility()
                + this.getVitality() + this.getIntelligence() + this.getLuck();
    }

    final String getType() {
        return this.type;
    }

    final Element getElement() {
        return this.element;
    }

    final void setType(final String newType) {
        this.type = newType;
    }

    protected double adjustForLevelDifference() {
        return Math.max(0.0, this.getLevelDifference() / 4.0 + 1.0);
    }

    // Helper Methods
    private static WindowAI getInitialWindowAI() {
        return WindowAIPicker.getNextRoutine();
    }

    private static MapAI getInitialMapAI() {
        return MapAIPicker.getNextRoutine();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + (this.element == null ? 0 : this.element.hashCode());
        return prime * result + (this.type == null ? 0 : this.type.hashCode());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Monster)) {
            return false;
        }
        final Monster other = (Monster) obj;
        if (this.element == null) {
            if (other.element != null) {
                return false;
            }
        } else if (!this.element.equals(other.element)) {
            return false;
        }
        if (this.type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!this.type.equals(other.type)) {
            return false;
        }
        return true;
    }

    protected final int getBattlesToNextLevel() {
        return Monster.BATTLES_START
                + (this.getLevel() + 1) * Monster.BATTLES_SCALE_FACTOR;
    }
}
