/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.creatures.party;

import java.io.IOException;

import com.puttysoftware.images.BufferedImageIcon;
import com.puttysoftware.polytable.PolyTable;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.VersionException;
import com.puttysoftware.retrorpgcs.creatures.Creature;
import com.puttysoftware.retrorpgcs.creatures.StatConstants;
import com.puttysoftware.retrorpgcs.creatures.castes.Caste;
import com.puttysoftware.retrorpgcs.creatures.castes.CasteManager;
import com.puttysoftware.retrorpgcs.creatures.faiths.Faith;
import com.puttysoftware.retrorpgcs.creatures.genders.Gender;
import com.puttysoftware.retrorpgcs.creatures.personalities.Personality;
import com.puttysoftware.retrorpgcs.creatures.personalities.PersonalityConstants;
import com.puttysoftware.retrorpgcs.creatures.races.Race;
import com.puttysoftware.retrorpgcs.creatures.races.RaceConstants;
import com.puttysoftware.retrorpgcs.items.ItemInventory;
import com.puttysoftware.retrorpgcs.maze.FormatConstants;
import com.puttysoftware.retrorpgcs.maze.GenerateTask;
import com.puttysoftware.retrorpgcs.maze.objects.Player;
import com.puttysoftware.retrorpgcs.prefs.PreferencesManager;
import com.puttysoftware.retrorpgcs.resourcemanagers.BattleImageManager;
import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class PartyMember extends Creature {
    private static final int START_GOLD = 0;
    private static final double BASE_COEFF = 10.0;
    private static final Player ME = new Player();

    public static PartyMember read(final XDataReader worldFile)
            throws IOException {
        final int version = worldFile.readByte();
        if (version < FormatConstants.CHARACTER_FORMAT_2) {
            throw new VersionException(
                    "Invalid character version found: " + version);
        }
        final var k = worldFile.readInt();
        final var pAtk = worldFile.readInt();
        final var pDef = worldFile.readInt();
        final var pHP = worldFile.readInt();
        final var pMP = worldFile.readInt();
        final var strength = worldFile.readInt();
        final var block = worldFile.readInt();
        final var agility = worldFile.readInt();
        final var vitality = worldFile.readInt();
        final var intelligence = worldFile.readInt();
        final var luck = worldFile.readInt();
        final var lvl = worldFile.readInt();
        final var cHP = worldFile.readInt();
        final var cMP = worldFile.readInt();
        final var gld = worldFile.readInt();
        final var apr = worldFile.readInt();
        final var spr = worldFile.readInt();
        final var load = worldFile.readInt();
        final var exp = worldFile.readLong();
        final var r = worldFile.readInt();
        final var c = worldFile.readInt();
        final var f = worldFile.readInt();
        final var p = worldFile.readInt();
        final var g = worldFile.readInt();
        final var max = worldFile.readInt();
        final var known = new boolean[max];
        for (var x = 0; x < max; x++) {
            known[x] = worldFile.readBoolean();
        }
        final var n = worldFile.readString();
        final var pm = PartyManager.getNewPCInstance(r, c, f, p, g, n);
        pm.setStrength(strength);
        pm.setBlock(block);
        pm.setAgility(agility);
        pm.setVitality(vitality);
        pm.setIntelligence(intelligence);
        pm.setLuck(luck);
        pm.setAttacksPerRound(apr);
        pm.setSpellsPerRound(spr);
        pm.setItems(ItemInventory.readItemInventory(worldFile, version));
        pm.kills = k;
        pm.permanentAttack = pAtk;
        pm.permanentDefense = pDef;
        pm.permanentHP = pHP;
        pm.permanentMP = pMP;
        pm.loadPartyMember(lvl, cHP, cMP, gld, load, exp, c, known);
        return pm;
    }

    // Fields
    private Race race;
    private Caste caste;
    private Faith faith;
    private Personality personality;
    private Gender gender;
    private final String name;
    private int permanentAttack;
    private int permanentDefense;
    private int permanentHP;
    private int permanentMP;
    private int kills;

    // Constructors
    PartyMember(final Race r, final Caste c, final Faith f, final Personality p,
            final Gender g, final String n) {
        super(true, 0);
        this.name = n;
        this.race = r;
        this.caste = c;
        this.faith = f;
        this.personality = p;
        this.gender = g;
        this.permanentAttack = 0;
        this.permanentDefense = 0;
        this.permanentHP = 0;
        this.permanentMP = 0;
        this.kills = 0;
        this.setLevel(1);
        this.setStrength(StatConstants.GAIN_STRENGTH + this.race
                .getAttribute(RaceConstants.RACE_ATTRIBUTE_STRENGTH_PER_LEVEL));
        this.setBlock(StatConstants.GAIN_BLOCK + this.race
                .getAttribute(RaceConstants.RACE_ATTRIBUTE_BLOCK_PER_LEVEL));
        this.setVitality(StatConstants.GAIN_VITALITY + this.race
                .getAttribute(RaceConstants.RACE_ATTRIBUTE_VITALITY_PER_LEVEL));
        this.setIntelligence(
                StatConstants.GAIN_INTELLIGENCE + this.race.getAttribute(
                        RaceConstants.RACE_ATTRIBUTE_INTELLIGENCE_PER_LEVEL));
        this.setAgility(StatConstants.GAIN_AGILITY + this.race
                .getAttribute(RaceConstants.RACE_ATTRIBUTE_AGILITY_PER_LEVEL));
        this.setLuck(StatConstants.GAIN_LUCK + this.race
                .getAttribute(RaceConstants.RACE_ATTRIBUTE_LUCK_PER_LEVEL));
        this.setAttacksPerRound(1);
        this.setSpellsPerRound(1);
        this.healAndRegenerateFully();
        this.setGold(PartyMember.START_GOLD);
        this.setExperience(0L);
        final var nextLevelEquation = new PolyTable(3, 1, 0, true);
        final var value = PartyMember.BASE_COEFF
                * this.personality.getAttribute(
                        PersonalityConstants.PERSONALITY_ATTRIBUTE_LEVEL_UP_SPEED);
        nextLevelEquation.setCoefficient(1, value);
        nextLevelEquation.setCoefficient(2, value);
        nextLevelEquation.setCoefficient(3, value);
        this.setToNextLevel(nextLevelEquation);
        this.setSpellBook(
                CasteManager.getSpellBookByID(this.caste.getCasteID()));
    }

    @Override
    public int getAttack() {
        return super.getAttack() + this.getPermanentAttackPoints();
    }

    @Override
    public int getCapacity() {
        return Math.max(StatConstants.MIN_CAPACITY,
                (int) (super.getCapacity() * this.getPersonality().getAttribute(
                        PersonalityConstants.PERSONALITY_ATTRIBUTE_CAPACITY_MOD)));
    }

    public Caste getCaste() {
        return this.caste;
    }

    @Override
    public int getDefense() {
        return super.getDefense() + this.getPermanentDefensePoints();
    }

    @Override
    public Faith getFaith() {
        return this.faith;
    }

    protected Gender getGender() {
        return this.gender;
    }

    @Override
    protected BufferedImageIcon getInitialImage() {
        return BattleImageManager.getImage(PartyMember.ME.getName(),
                PartyMember.ME.getBaseID(), this.faith.getColor().getRGB());
    }

    @Override
    public int getMapBattleActionsPerRound() {
        return Math.max((int) (super.getMapBattleActionsPerRound()
                * this.personality.getAttribute(
                        PersonalityConstants.PERSONALITY_ATTRIBUTE_ACTION_MOD)),
                1);
    }

    @Override
    public int getMaximumHP() {
        return super.getMaximumHP() + this.getPermanentHPPoints();
    }

    @Override
    public int getMaximumMP() {
        return super.getMaximumMP() + this.getPermanentMPPoints();
    }

    @Override
    public String getName() {
        return this.name;
    }

    public int getPermanentAttackPoints() {
        return this.permanentAttack;
    }

    public int getPermanentDefensePoints() {
        return this.permanentDefense;
    }

    public int getPermanentHPPoints() {
        return this.permanentHP;
    }

    public int getPermanentMPPoints() {
        return this.permanentMP;
    }

    protected Personality getPersonality() {
        return this.personality;
    }

    public Race getRace() {
        return this.race;
    }

    @Override
    public int getSpeed() {
        final var difficulty = PreferencesManager.getGameDifficulty();
        final var base = this.getBaseSpeed();
        switch (difficulty) {
        case PreferencesManager.DIFFICULTY_VERY_EASY:
            return (int) (base * Creature.SPEED_ADJUST_FASTEST);
        case PreferencesManager.DIFFICULTY_EASY:
            return (int) (base * Creature.SPEED_ADJUST_FAST);
        case PreferencesManager.DIFFICULTY_NORMAL:
            return (int) (base * Creature.SPEED_ADJUST_NORMAL);
        case PreferencesManager.DIFFICULTY_HARD:
            return (int) (base * Creature.SPEED_ADJUST_SLOW);
        case PreferencesManager.DIFFICULTY_VERY_HARD:
            return (int) (base * Creature.SPEED_ADJUST_SLOWEST);
        default:
            return (int) (base * Creature.SPEED_ADJUST_NORMAL);
        }
    }

    @Override
    public int getWindowBattleActionsPerRound() {
        return Math.max((int) (super.getWindowBattleActionsPerRound()
                * this.personality.getAttribute(
                        PersonalityConstants.PERSONALITY_ATTRIBUTE_ACTION_MOD)),
                1);
    }

    // Methods
    public String getXPString() {
        return this.getExperience() + "/" + this.getToNextLevelValue();
    }

    public void initPostKill(final Race r, final Caste c, final Faith f,
            final Personality p, final Gender g) {
        this.race = r;
        this.caste = c;
        this.faith = f;
        this.personality = p;
        this.gender = g;
        this.setLevel(1);
        this.setStrength(StatConstants.GAIN_STRENGTH + this.race
                .getAttribute(RaceConstants.RACE_ATTRIBUTE_STRENGTH_PER_LEVEL));
        this.setBlock(StatConstants.GAIN_BLOCK + this.race
                .getAttribute(RaceConstants.RACE_ATTRIBUTE_BLOCK_PER_LEVEL));
        this.setVitality(StatConstants.GAIN_VITALITY + this.race
                .getAttribute(RaceConstants.RACE_ATTRIBUTE_VITALITY_PER_LEVEL));
        this.setIntelligence(
                StatConstants.GAIN_INTELLIGENCE + this.race.getAttribute(
                        RaceConstants.RACE_ATTRIBUTE_INTELLIGENCE_PER_LEVEL));
        this.setAgility(StatConstants.GAIN_AGILITY + this.race
                .getAttribute(RaceConstants.RACE_ATTRIBUTE_AGILITY_PER_LEVEL));
        this.setLuck(StatConstants.GAIN_LUCK + this.race
                .getAttribute(RaceConstants.RACE_ATTRIBUTE_LUCK_PER_LEVEL));
        this.setAttacksPerRound(1);
        this.setSpellsPerRound(1);
        this.healAndRegenerateFully();
        this.setGold(PartyMember.START_GOLD);
        this.setExperience(0L);
        this.getItems().resetInventory();
        RetroRPGCS.getInstance().getGameManager().deactivateAllEffects();
        final var nextLevelEquation = new PolyTable(3, 1, 0, true);
        final var value = PartyMember.BASE_COEFF
                * this.personality.getAttribute(
                        PersonalityConstants.PERSONALITY_ATTRIBUTE_LEVEL_UP_SPEED);
        nextLevelEquation.setCoefficient(1, value);
        nextLevelEquation.setCoefficient(2, value);
        nextLevelEquation.setCoefficient(3, value);
        this.setToNextLevel(nextLevelEquation);
        this.setSpellBook(
                CasteManager.getSpellBookByID(this.caste.getCasteID()));
        PartyManager.getParty().resetTowerLevel();
        new GenerateTask(true).start();
    }

    // Transformers
    @Override
    protected void levelUpHook() {
        this.offsetStrength(StatConstants.GAIN_STRENGTH + this.race
                .getAttribute(RaceConstants.RACE_ATTRIBUTE_STRENGTH_PER_LEVEL));
        this.offsetBlock(StatConstants.GAIN_BLOCK + this.race
                .getAttribute(RaceConstants.RACE_ATTRIBUTE_BLOCK_PER_LEVEL));
        this.offsetVitality(StatConstants.GAIN_VITALITY + this.race
                .getAttribute(RaceConstants.RACE_ATTRIBUTE_VITALITY_PER_LEVEL));
        this.offsetIntelligence(
                StatConstants.GAIN_INTELLIGENCE + this.race.getAttribute(
                        RaceConstants.RACE_ATTRIBUTE_INTELLIGENCE_PER_LEVEL));
        this.offsetAgility(StatConstants.GAIN_AGILITY + this.race
                .getAttribute(RaceConstants.RACE_ATTRIBUTE_AGILITY_PER_LEVEL));
        this.offsetLuck(StatConstants.GAIN_LUCK + this.race
                .getAttribute(RaceConstants.RACE_ATTRIBUTE_LUCK_PER_LEVEL));
        this.healAndRegenerateFully();
    }

    @Override
    public void loadCreature() {
        // Do nothing
    }

    private void loadPartyMember(final int newLevel, final int chp,
            final int cmp, final int newGold, final int newLoad,
            final long newExperience, final int bookID, final boolean[] known) {
        this.setLevel(newLevel);
        this.setCurrentHP(chp);
        this.setCurrentMP(cmp);
        this.setGold(newGold);
        this.setLoad(newLoad);
        this.setExperience(newExperience);
        final var book = CasteManager.getSpellBookByID(bookID);
        for (var x = 0; x < known.length; x++) {
            if (known[x]) {
                book.learnSpellByID(x);
            }
        }
        this.setSpellBook(book);
    }

    @Override
    public void offsetGold(final int value) {
        var fixedValue = value;
        if (value > 0) {
            fixedValue = (int) (fixedValue * this.getPersonality().getAttribute(
                    PersonalityConstants.PERSONALITY_ATTRIBUTE_WEALTH_MOD));
        }
        super.offsetGold(fixedValue);
    }

    public void onDeath(final int penalty) {
        this.offsetExperiencePercentage(penalty);
        this.healAndRegenerateFully();
        this.setGold(0);
    }

    public void spendPointOnAttack() {
        this.kills++;
        this.permanentAttack++;
    }

    public void spendPointOnDefense() {
        this.kills++;
        this.permanentDefense++;
    }

    public void spendPointOnHP() {
        this.kills++;
        this.permanentHP++;
    }

    public void spendPointOnMP() {
        this.kills++;
        this.permanentMP++;
    }

    public void write(final XDataWriter worldFile) throws IOException {
        worldFile.writeByte(FormatConstants.CHARACTER_FORMAT_LATEST);
        worldFile.writeInt(this.kills);
        worldFile.writeInt(this.getPermanentAttackPoints());
        worldFile.writeInt(this.getPermanentDefensePoints());
        worldFile.writeInt(this.getPermanentHPPoints());
        worldFile.writeInt(this.getPermanentMPPoints());
        worldFile.writeInt(this.getStrength());
        worldFile.writeInt(this.getBlock());
        worldFile.writeInt(this.getAgility());
        worldFile.writeInt(this.getVitality());
        worldFile.writeInt(this.getIntelligence());
        worldFile.writeInt(this.getLuck());
        worldFile.writeInt(this.getLevel());
        worldFile.writeInt(this.getCurrentHP());
        worldFile.writeInt(this.getCurrentMP());
        worldFile.writeInt(this.getGold());
        worldFile.writeInt(this.getAttacksPerRound());
        worldFile.writeInt(this.getSpellsPerRound());
        worldFile.writeInt(this.getLoad());
        worldFile.writeLong(this.getExperience());
        worldFile.writeInt(this.getRace().getRaceID());
        worldFile.writeInt(this.getCaste().getCasteID());
        worldFile.writeInt(this.getFaith().getFaithID());
        worldFile.writeInt(this.getPersonality().getPersonalityID());
        worldFile.writeInt(this.getGender().getGenderID());
        final var max = this.getSpellBook().getSpellCount();
        worldFile.writeInt(max);
        for (var x = 0; x < max; x++) {
            worldFile.writeBoolean(this.getSpellBook().isSpellKnown(x));
        }
        worldFile.writeString(this.getName());
        this.getItems().writeItemInventory(worldFile);
    }
}
