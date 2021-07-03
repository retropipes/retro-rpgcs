/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.creatures.monsters;

import com.puttysoftware.images.BufferedImageIcon;
import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.retrorpgcs.ai.map.MapAIPicker;
import com.puttysoftware.retrorpgcs.ai.window.VeryHardWindowAI;
import com.puttysoftware.retrorpgcs.ai.window.WindowAI;
import com.puttysoftware.retrorpgcs.creatures.Creature;
import com.puttysoftware.retrorpgcs.creatures.faiths.Faith;
import com.puttysoftware.retrorpgcs.creatures.faiths.FaithManager;
import com.puttysoftware.retrorpgcs.creatures.party.PartyManager;
import com.puttysoftware.retrorpgcs.prefs.PreferencesManager;
import com.puttysoftware.retrorpgcs.resourcemanagers.BossImageManager;
import com.puttysoftware.retrorpgcs.spells.SpellBook;

public class BossMonster extends Creature {
    // Fields
    private static final int MINIMUM_STAT_VALUE_VERY_EASY = 100;
    private static final int MINIMUM_STAT_VALUE_EASY = 200;
    private static final int MINIMUM_STAT_VALUE_NORMAL = 400;
    private static final int MINIMUM_STAT_VALUE_HARD = 600;
    private static final int MINIMUM_STAT_VALUE_VERY_HARD = 900;
    private static final int STAT_MULT_VERY_EASY = 3;
    private static final int STAT_MULT_EASY = 4;
    private static final int STAT_MULT_NORMAL = 5;
    private static final int STAT_MULT_HARD = 8;
    private static final int STAT_MULT_VERY_HARD = 12;

    // Constructors
    BossMonster() {
        super(true, 1);
        this.setWindowAI(BossMonster.getInitialWindowAI());
        this.setMapAI(MapAIPicker.getNextRoutine());
        final SpellBook spells = new SystemMonsterSpellBook();
        spells.learnAllSpells();
        this.setSpellBook(spells);
        this.loadCreature();
    }

    // Methods
    @Override
    public String getFightingWhatString() {
        return "You're fighting The Boss";
    }

    @Override
    public String getName() {
        return "The Boss";
    }

    @Override
    public Faith getFaith() {
        return FaithManager.getFaith(0);
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
    protected BufferedImageIcon getInitialImage() {
        return BossImageManager.getBossImage();
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

    // Helper Methods
    @Override
    public void loadCreature() {
        final int newLevel = PartyManager.getParty().getTowerLevel() + 6;
        this.setLevel(newLevel);
        this.setVitality(this.getInitialVitality());
        this.setCurrentHP(this.getMaximumHP());
        this.setIntelligence(this.getInitialIntelligence());
        this.setCurrentMP(this.getMaximumMP());
        this.setStrength(this.getInitialStrength());
        this.setBlock(this.getInitialBlock());
        this.setAgility(this.getInitialAgility());
        this.setLuck(this.getInitialLuck());
        this.setGold(0);
        this.setExperience(0);
        this.setAttacksPerRound(1);
        this.setSpellsPerRound(1);
        this.image = this.getInitialImage();
    }

    private int getInitialStrength() {
        final int min = BossMonster.getMinimumStatForDifficulty();
        final RandomRange r = new RandomRange(min,
                Math.max(
                        this.getLevel()
                                * BossMonster.getStatMultiplierForDifficulty(),
                        min));
        return r.generate();
    }

    private int getInitialBlock() {
        final int min = BossMonster.getMinimumStatForDifficulty();
        final RandomRange r = new RandomRange(min,
                Math.max(
                        this.getLevel()
                                * BossMonster.getStatMultiplierForDifficulty(),
                        min));
        return r.generate();
    }

    private int getInitialAgility() {
        final int min = BossMonster.getMinimumStatForDifficulty();
        final RandomRange r = new RandomRange(min,
                Math.max(
                        this.getLevel()
                                * BossMonster.getStatMultiplierForDifficulty(),
                        min));
        return r.generate();
    }

    private int getInitialVitality() {
        final int min = BossMonster.getMinimumStatForDifficulty();
        final RandomRange r = new RandomRange(min,
                Math.max(
                        this.getLevel()
                                * BossMonster.getStatMultiplierForDifficulty(),
                        min));
        return r.generate();
    }

    private int getInitialIntelligence() {
        final int min = BossMonster.getMinimumStatForDifficulty();
        final RandomRange r = new RandomRange(min,
                Math.max(
                        this.getLevel()
                                * BossMonster.getStatMultiplierForDifficulty(),
                        min));
        return r.generate();
    }

    private int getInitialLuck() {
        final int min = BossMonster.getMinimumStatForDifficulty();
        final RandomRange r = new RandomRange(min,
                Math.max(
                        this.getLevel()
                                * BossMonster.getStatMultiplierForDifficulty(),
                        min));
        return r.generate();
    }

    private static WindowAI getInitialWindowAI() {
        return new VeryHardWindowAI();
    }

    private static int getStatMultiplierForDifficulty() {
        final int difficulty = PreferencesManager.getGameDifficulty();
        if (difficulty == PreferencesManager.DIFFICULTY_VERY_EASY) {
            return BossMonster.STAT_MULT_VERY_EASY;
        } else if (difficulty == PreferencesManager.DIFFICULTY_EASY) {
            return BossMonster.STAT_MULT_EASY;
        } else if (difficulty == PreferencesManager.DIFFICULTY_NORMAL) {
            return BossMonster.STAT_MULT_NORMAL;
        } else if (difficulty == PreferencesManager.DIFFICULTY_HARD) {
            return BossMonster.STAT_MULT_HARD;
        } else if (difficulty == PreferencesManager.DIFFICULTY_VERY_HARD) {
            return BossMonster.STAT_MULT_VERY_HARD;
        } else {
            return BossMonster.STAT_MULT_NORMAL;
        }
    }

    private static int getMinimumStatForDifficulty() {
        final int difficulty = PreferencesManager.getGameDifficulty();
        if (difficulty == PreferencesManager.DIFFICULTY_VERY_EASY) {
            return BossMonster.MINIMUM_STAT_VALUE_VERY_EASY;
        } else if (difficulty == PreferencesManager.DIFFICULTY_EASY) {
            return BossMonster.MINIMUM_STAT_VALUE_EASY;
        } else if (difficulty == PreferencesManager.DIFFICULTY_NORMAL) {
            return BossMonster.MINIMUM_STAT_VALUE_NORMAL;
        } else if (difficulty == PreferencesManager.DIFFICULTY_HARD) {
            return BossMonster.MINIMUM_STAT_VALUE_HARD;
        } else if (difficulty == PreferencesManager.DIFFICULTY_VERY_HARD) {
            return BossMonster.MINIMUM_STAT_VALUE_VERY_HARD;
        } else {
            return BossMonster.MINIMUM_STAT_VALUE_NORMAL;
        }
    }
}
