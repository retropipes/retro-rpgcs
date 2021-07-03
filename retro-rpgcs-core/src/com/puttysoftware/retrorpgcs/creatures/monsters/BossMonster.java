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

    private static WindowAI getInitialWindowAI() {
        return new VeryHardWindowAI();
    }

    private static int getMinimumStatForDifficulty() {
        final var difficulty = PreferencesManager.getGameDifficulty();
        switch (difficulty) {
        case PreferencesManager.DIFFICULTY_VERY_EASY:
            return BossMonster.MINIMUM_STAT_VALUE_VERY_EASY;
        case PreferencesManager.DIFFICULTY_EASY:
            return BossMonster.MINIMUM_STAT_VALUE_EASY;
        case PreferencesManager.DIFFICULTY_NORMAL:
            return BossMonster.MINIMUM_STAT_VALUE_NORMAL;
        case PreferencesManager.DIFFICULTY_HARD:
            return BossMonster.MINIMUM_STAT_VALUE_HARD;
        case PreferencesManager.DIFFICULTY_VERY_HARD:
            return BossMonster.MINIMUM_STAT_VALUE_VERY_HARD;
        default:
            return BossMonster.MINIMUM_STAT_VALUE_NORMAL;
        }
    }

    private static int getStatMultiplierForDifficulty() {
        final var difficulty = PreferencesManager.getGameDifficulty();
        switch (difficulty) {
        case PreferencesManager.DIFFICULTY_VERY_EASY:
            return BossMonster.STAT_MULT_VERY_EASY;
        case PreferencesManager.DIFFICULTY_EASY:
            return BossMonster.STAT_MULT_EASY;
        case PreferencesManager.DIFFICULTY_NORMAL:
            return BossMonster.STAT_MULT_NORMAL;
        case PreferencesManager.DIFFICULTY_HARD:
            return BossMonster.STAT_MULT_HARD;
        case PreferencesManager.DIFFICULTY_VERY_HARD:
            return BossMonster.STAT_MULT_VERY_HARD;
        default:
            return BossMonster.STAT_MULT_NORMAL;
        }
    }

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

    @Override
    public boolean checkLevelUp() {
        return false;
    }

    @Override
    public Faith getFaith() {
        return FaithManager.getFaith(0);
    }

    // Methods
    @Override
    public String getFightingWhatString() {
        return "You're fighting The Boss";
    }

    private int getInitialAgility() {
        final var min = BossMonster.getMinimumStatForDifficulty();
        final var r = new RandomRange(min,
                Math.max(
                        this.getLevel()
                                * BossMonster.getStatMultiplierForDifficulty(),
                        min));
        return r.generate();
    }

    private int getInitialBlock() {
        final var min = BossMonster.getMinimumStatForDifficulty();
        final var r = new RandomRange(min,
                Math.max(
                        this.getLevel()
                                * BossMonster.getStatMultiplierForDifficulty(),
                        min));
        return r.generate();
    }

    @Override
    protected BufferedImageIcon getInitialImage() {
        return BossImageManager.getBossImage();
    }

    private int getInitialIntelligence() {
        final var min = BossMonster.getMinimumStatForDifficulty();
        final var r = new RandomRange(min,
                Math.max(
                        this.getLevel()
                                * BossMonster.getStatMultiplierForDifficulty(),
                        min));
        return r.generate();
    }

    private int getInitialLuck() {
        final var min = BossMonster.getMinimumStatForDifficulty();
        final var r = new RandomRange(min,
                Math.max(
                        this.getLevel()
                                * BossMonster.getStatMultiplierForDifficulty(),
                        min));
        return r.generate();
    }

    private int getInitialStrength() {
        final var min = BossMonster.getMinimumStatForDifficulty();
        final var r = new RandomRange(min,
                Math.max(
                        this.getLevel()
                                * BossMonster.getStatMultiplierForDifficulty(),
                        min));
        return r.generate();
    }

    private int getInitialVitality() {
        final var min = BossMonster.getMinimumStatForDifficulty();
        final var r = new RandomRange(min,
                Math.max(
                        this.getLevel()
                                * BossMonster.getStatMultiplierForDifficulty(),
                        min));
        return r.generate();
    }

    @Override
    public String getName() {
        return "The Boss";
    }

    @Override
    public int getSpeed() {
        final var difficulty = PreferencesManager.getGameDifficulty();
        final var base = this.getBaseSpeed();
        switch (difficulty) {
        case PreferencesManager.DIFFICULTY_VERY_EASY:
            return (int) (base * Creature.SPEED_ADJUST_SLOWEST);
        case PreferencesManager.DIFFICULTY_EASY:
            return (int) (base * Creature.SPEED_ADJUST_SLOW);
        case PreferencesManager.DIFFICULTY_NORMAL:
            return (int) (base * Creature.SPEED_ADJUST_NORMAL);
        case PreferencesManager.DIFFICULTY_HARD:
            return (int) (base * Creature.SPEED_ADJUST_FAST);
        case PreferencesManager.DIFFICULTY_VERY_HARD:
            return (int) (base * Creature.SPEED_ADJUST_FASTEST);
        default:
            return (int) (base * Creature.SPEED_ADJUST_NORMAL);
        }
    }

    @Override
    protected void levelUpHook() {
        // Do nothing
    }

    // Helper Methods
    @Override
    public void loadCreature() {
        final var newLevel = PartyManager.getParty().getTowerLevel() + 6;
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
}
