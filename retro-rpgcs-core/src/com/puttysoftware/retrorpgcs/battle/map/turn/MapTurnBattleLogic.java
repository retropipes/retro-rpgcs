/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.battle.map.turn;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.ai.map.AutoMapAI;
import com.puttysoftware.retrorpgcs.ai.map.MapAI;
import com.puttysoftware.retrorpgcs.ai.map.MapAIContext;
import com.puttysoftware.retrorpgcs.battle.Battle;
import com.puttysoftware.retrorpgcs.battle.BattleResults;
import com.puttysoftware.retrorpgcs.battle.BossRewards;
import com.puttysoftware.retrorpgcs.battle.damageengines.AbstractDamageEngine;
import com.puttysoftware.retrorpgcs.battle.map.MapBattle;
import com.puttysoftware.retrorpgcs.battle.map.MapBattleArrowTask;
import com.puttysoftware.retrorpgcs.creatures.Creature;
import com.puttysoftware.retrorpgcs.creatures.StatConstants;
import com.puttysoftware.retrorpgcs.creatures.monsters.BossMonster;
import com.puttysoftware.retrorpgcs.creatures.monsters.MonsterFactory;
import com.puttysoftware.retrorpgcs.creatures.party.PartyManager;
import com.puttysoftware.retrorpgcs.effects.Effect;
import com.puttysoftware.retrorpgcs.items.combat.CombatItemChucker;
import com.puttysoftware.retrorpgcs.maze.Maze;
import com.puttysoftware.retrorpgcs.maze.MazeConstants;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractMazeObject;
import com.puttysoftware.retrorpgcs.maze.objects.BattleCharacter;
import com.puttysoftware.retrorpgcs.maze.objects.Empty;
import com.puttysoftware.retrorpgcs.prefs.PreferencesManager;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;
import com.puttysoftware.retrorpgcs.spells.SpellCaster;

public class MapTurnBattleLogic extends Battle {
    private static final int ITEM_ACTION_POINTS = 6;
    private static final int STEAL_ACTION_POINTS = 3;
    private static final int DRAIN_ACTION_POINTS = 3;
    // Fields
    private MapTurnBattleDefinitions bd;
    private AbstractDamageEngine pde;
    private AbstractDamageEngine ede;
    private final AutoMapAI auto;
    private int damage;
    private int result;
    private int activeIndex;
    private long battleExp;
    private boolean newRound;
    private int[] speedArray;
    private int lastSpeed;
    private boolean[] speedMarkArray;
    private boolean resultDoneAlready;
    private boolean lastAIActionResult;
    private final MapTurnBattleAITask ait;
    private MapTurnBattleGUI battleGUI;
    private BattleCharacter enemy;

    // Constructors
    public MapTurnBattleLogic() {
        this.battleGUI = new MapTurnBattleGUI();
        this.auto = new AutoMapAI();
        this.ait = new MapTurnBattleAITask(this);
        this.ait.start();
    }

    private boolean areTeamEnemiesAlive(final int teamID) {
        for (var x = 0; x < this.bd.getBattlers().length; x++) {
            if ((this.bd.getBattlers()[x] != null)
                    && (this.bd.getBattlers()[x].getTeamID() != teamID)) {
                final var res = this.bd.getBattlers()[x].getTemplate()
                        .isAlive();
                if (res) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean areTeamEnemiesDeadOrGone(final int teamID) {
        var deadCount = 0;
        for (var x = 0; x < this.bd.getBattlers().length; x++) {
            if ((this.bd.getBattlers()[x] != null)
                    && (this.bd.getBattlers()[x].getTeamID() != teamID)) {
                final var res = this.bd.getBattlers()[x].getTemplate()
                        .isAlive() && this.bd.getBattlers()[x].isActive();
                if (res) {
                    return false;
                }
                if (!this.bd.getBattlers()[x].getTemplate().isAlive()) {
                    deadCount++;
                }
            }
        }
        return deadCount > 0;
    }

    private boolean areTeamEnemiesGone(final int teamID) {
        var res = true;
        for (var x = 0; x < this.bd.getBattlers().length; x++) {
            if ((this.bd.getBattlers()[x] != null)
                    && (this.bd.getBattlers()[x].getTeamID() != teamID)) {
                if (this.bd.getBattlers()[x].getTemplate().isAlive()) {
                    res = res && !this.bd.getBattlers()[x].isActive();
                    if (!res) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void arrowDone(final BattleCharacter hit) {
        this.battleGUI.turnEventHandlersOn();
        // Handle death
        if (hit != null && !hit.getTemplate().isAlive()) {
            if (hit.getTeamID() != Creature.TEAM_PARTY) {
                // Update victory spoils
                this.battleExp = hit.getTemplate().getExperience();
            }
            // Remove effects from dead character
            hit.getTemplate().stripAllEffects();
            // Set dead character to inactive
            hit.deactivate();
            // Remove character from battle
            this.bd.getBattleMaze().setCell(new Empty(), hit.getX(), hit.getY(),
                    0, MazeConstants.LAYER_OBJECT);
        }
        // Check result
        final var currResult = this.getResult();
        if (currResult != BattleResults.IN_PROGRESS) {
            // Battle Done
            this.result = currResult;
            this.doResult();
        }
    }

    @Override
    public void battleDone() {
        // Leave Battle
        this.hideBattle();
        RetroRPGCS.getInstance().setMode(RetroRPGCS.STATUS_GAME);
        // Return to whence we came
        RetroRPGCS.getInstance().getGameManager().showOutput();
        RetroRPGCS.getInstance().getGameManager().redrawMaze();
    }

    @Override
    public boolean castSpell() {
        // Check Spell Counter
        if (this.getActiveSpellCounter() > 0) {
            if (!this.bd.getActiveCharacter().getTemplate().hasMapAI()) {
                // Active character has no AI, or AI is turned off
                final var success = SpellCaster.selectAndCastSpell(
                        this.bd.getActiveCharacter().getTemplate());
                if (success) {
                    this.decrementActiveSpellCounter();
                }
                final var currResult = this.getResult();
                if (currResult != BattleResults.IN_PROGRESS) {
                    // Battle Done
                    this.result = currResult;
                    this.doResult();
                }
                return success;
            } else {
                // Active character has AI, and AI is turned on
                final var sp = this.bd.getActiveCharacter().getTemplate()
                        .getMapAI().getSpellToCast();
                final var success = SpellCaster.castSpell(sp,
                        this.bd.getActiveCharacter().getTemplate());
                if (success) {
                    this.decrementActiveSpellCounter();
                }
                final var currResult = this.getResult();
                if (currResult != BattleResults.IN_PROGRESS) {
                    // Battle Done
                    this.result = currResult;
                    this.doResult();
                }
                return success;
            }
        } else {
            // Deny cast - out of actions
            if (!this.bd.getActiveCharacter().getTemplate().hasMapAI()) {
                this.setStatusMessage("Out of actions!");
            }
            return false;
        }
    }

    private void clearStatusMessage() {
        this.battleGUI.clearStatusMessage();
    }

    private void computeDamage(final Creature theEnemy, final Creature acting,
            final AbstractDamageEngine activeDE) {
        // Compute Damage
        this.damage = 0;
        final var actual = activeDE.computeDamage(theEnemy, acting);
        // Hit or Missed
        this.damage = actual;
        if (activeDE.weaponFumble()) {
            acting.doDamage(this.damage);
        } else if (this.damage < 0) {
            acting.doDamage(-this.damage);
        } else {
            theEnemy.doDamage(this.damage);
        }
        this.displayRoundResults(theEnemy, acting, activeDE);
    }

    private void decrementActiveActionCounterBy(final int amount) {
        this.bd.getActiveCharacter().modifyAP(amount);
    }

    private void decrementActiveAttackCounter() {
        this.bd.getActiveCharacter().modifyAttacks(1);
    }

    private void decrementActiveSpellCounter() {
        this.bd.getActiveCharacter().modifySpells(1);
    }

    @Override
    public void displayActiveEffects() {
        // Do nothing
    }

    @Override
    public void displayBattleStats() {
        // Do nothing
    }

    private void displayRoundResults(final Creature theEnemy,
            final Creature active, final AbstractDamageEngine activeDE) {
        // Display round results
        final var activeName = active.getName();
        final var enemyName = theEnemy.getName();
        var damageString = Integer.toString(this.damage);
        var displayDamageString = " ";
        if (this.damage == 0) {
            if (activeDE.weaponMissed()) {
                displayDamageString = activeName + " tries to hit " + enemyName
                        + ", but MISSES!";
                SoundManager.playSound(SoundConstants.SOUND_MISSED);
            } else if (activeDE.enemyDodged()) {
                displayDamageString = activeName + " tries to hit " + enemyName
                        + ", but " + enemyName + " AVOIDS the attack!";
                SoundManager.playSound(SoundConstants.SOUND_MISSED);
            } else {
                displayDamageString = activeName + " tries to hit " + enemyName
                        + ", but the attack is BLOCKED!";
                SoundManager.playSound(SoundConstants.SOUND_MISSED);
            }
        } else if (this.damage < 0) {
            damageString = Integer.toString(-this.damage);
            var displayDamagePrefix = "";
            if (activeDE.weaponCrit() && activeDE.weaponPierce()) {
                displayDamagePrefix = "PIERCING CRITICAL HIT! ";
                SoundManager.playSound(SoundConstants.SOUND_COUNTER);
                SoundManager.playSound(SoundConstants.SOUND_CRITICAL);
            } else if (activeDE.weaponCrit()) {
                displayDamagePrefix = "CRITICAL HIT! ";
                SoundManager.playSound(SoundConstants.SOUND_CRITICAL);
            } else if (activeDE.weaponPierce()) {
                displayDamagePrefix = "PIERCING HIT! ";
                SoundManager.playSound(SoundConstants.SOUND_COUNTER);
            }
            displayDamageString = displayDamagePrefix + activeName
                    + " tries to hit " + enemyName + ", but " + enemyName
                    + " RIPOSTES for " + damageString + " damage!";
            SoundManager.playSound(SoundConstants.SOUND_COUNTER);
        } else {
            var displayDamagePrefix = "";
            if (activeDE.weaponFumble()) {
                SoundManager.playSound(SoundConstants.SOUND_FUMBLE);
                displayDamageString = "FUMBLE! " + activeName
                        + " drops their weapon on themselves, doing "
                        + damageString + " damage!";
            } else {
                if (activeDE.weaponCrit() && activeDE.weaponPierce()) {
                    displayDamagePrefix = "PIERCING CRITICAL HIT! ";
                    SoundManager.playSound(SoundConstants.SOUND_COUNTER);
                    SoundManager.playSound(SoundConstants.SOUND_CRITICAL);
                } else if (activeDE.weaponCrit()) {
                    displayDamagePrefix = "CRITICAL HIT! ";
                    SoundManager.playSound(SoundConstants.SOUND_CRITICAL);
                } else if (activeDE.weaponPierce()) {
                    displayDamagePrefix = "PIERCING HIT! ";
                    SoundManager.playSound(SoundConstants.SOUND_COUNTER);
                }
                displayDamageString = displayDamagePrefix + activeName
                        + " hits " + enemyName + " for " + damageString
                        + " damage!";
                SoundManager.playSound(SoundConstants.SOUND_HIT);
            }
        }
        this.setStatusMessage(displayDamageString);
    }

    @Override
    public void doBattle() {
        final var m = Maze.getTemporaryBattleCopy();
        final var b = new MapBattle();
        this.doBattleInternal(m, b);
    }

    @Override
    public void doBattleByProxy() {
        final var m = MonsterFactory.getNewMonsterInstance();
        final var playerCharacter = PartyManager.getParty().getLeader();
        playerCharacter.offsetExperience(m.getExperience());
        playerCharacter.offsetGold(m.getGold());
        // Level Up Check
        if (playerCharacter.checkLevelUp()) {
            playerCharacter.levelUp();
            RetroRPGCS.getInstance().getGameManager().keepNextMessage();
            RetroRPGCS.getInstance().showMessage(
                    "You reached level " + playerCharacter.getLevel() + ".");
        }
    }

    private void doBattleInternal(final Maze bMaze, final MapBattle b) {
        // Initialize Battle
        RetroRPGCS.getInstance().getGameManager().hideOutput();
        RetroRPGCS.getInstance().setMode(RetroRPGCS.STATUS_BATTLE);
        this.bd = new MapTurnBattleDefinitions();
        this.bd.setBattleMaze(bMaze);
        this.pde = AbstractDamageEngine.getPlayerInstance();
        this.ede = AbstractDamageEngine.getEnemyInstance();
        this.resultDoneAlready = false;
        this.result = BattleResults.IN_PROGRESS;
        // Generate Friends
        final var friends = PartyManager.getParty()
                .getBattleCharacters();
        // Generate Enemies
        this.enemy = b.getBattlers();
        this.enemy.getTemplate().healAndRegenerateFully();
        this.enemy.getTemplate().loadCreature();
        // Merge and Create AI Contexts
        for (var x = 0; x < 2; x++) {
            if (x == 0) {
                this.bd.addBattler(friends);
            } else {
                this.bd.addBattler(this.enemy);
            }
            if (this.bd.getBattlers()[x] != null) {
                // Create an AI Context
                this.bd.getBattlerAIContexts()[x] = new MapAIContext(
                        this.bd.getBattlers()[x], this.bd.getBattleMaze());
            }
        }
        // Reset Inactive Indicators and Action Counters
        this.bd.resetBattlers();
        // Generate Speed Array
        this.generateSpeedArray();
        // Set Character Locations
        this.setCharacterLocations();
        // Set First Active
        this.newRound = this.setNextActive(true);
        // Clear status message
        this.clearStatusMessage();
        // Start Battle
        this.battleGUI.getViewManager()
                .setViewingWindowCenterX(this.bd.getActiveCharacter().getY());
        this.battleGUI.getViewManager()
                .setViewingWindowCenterY(this.bd.getActiveCharacter().getX());
        SoundManager.playSound(SoundConstants.SOUND_BATTLE);
        this.showBattle();
        this.updateStatsAndEffects();
        this.redrawBattle();
    }

    @Override
    public boolean doPlayerActions(final int action) {
        switch (action) {
        case MapAI.ACTION_CAST_SPELL:
            this.castSpell();
            break;
        case MapAI.ACTION_DRAIN:
            this.drain();
            break;
        case MapAI.ACTION_STEAL:
            this.steal();
            break;
        case MapAI.ACTION_USE_ITEM:
            this.useItem();
            break;
        default:
            this.endTurn();
            break;
        }
        return true;
    }

    @Override
    public void doResult() {
        this.stopWaitingForAI();
        if (!this.resultDoneAlready) {
            // Handle Results
            this.resultDoneAlready = true;
            var rewardsFlag = false;
            if (this.getEnemy() instanceof BossMonster) {
                switch (this.result) {
                case BattleResults.WON:
                case BattleResults.PERFECT:
                    this.setStatusMessage("You defeated the Boss!");
                    SoundManager.playSound(SoundConstants.SOUND_VICTORY);
                    rewardsFlag = true;
                    break;
                case BattleResults.LOST:
                    this.setStatusMessage("The Boss defeated you...");
                    SoundManager.playSound(SoundConstants.SOUND_GAME_OVER);
                    PartyManager.getParty().getLeader().onDeath(-10);
                    break;
                case BattleResults.ANNIHILATED:
                    this.setStatusMessage(
                            "The Boss defeated you without suffering damage... you were annihilated!");
                    SoundManager.playSound(SoundConstants.SOUND_GAME_OVER);
                    PartyManager.getParty().getLeader().onDeath(-20);
                    break;
                case BattleResults.DRAW:
                    this.setStatusMessage(
                            "The Boss battle was a draw. You are fully healed!");
                    PartyManager.getParty().getLeader()
                            .healPercentage(Creature.FULL_HEAL_PERCENTAGE);
                    PartyManager.getParty().getLeader().regeneratePercentage(
                            Creature.FULL_HEAL_PERCENTAGE);
                    break;
                case BattleResults.FLED:
                    this.setStatusMessage("You ran away successfully!");
                    break;
                case BattleResults.ENEMY_FLED:
                    this.setStatusMessage("The Boss ran away!");
                    break;
                default:
                    break;
                }
            } else {
                switch (this.result) {
                case BattleResults.WON:
                    SoundManager.playSound(SoundConstants.SOUND_VICTORY);
                    CommonDialogs.showTitledDialog("The party is victorious!",
                            "Victory!");
                    PartyManager.getParty().getLeader()
                            .offsetGold(this.getGold());
                    PartyManager.getParty().getLeader()
                            .offsetExperience(this.battleExp);
                    break;
                case BattleResults.LOST:
                    CommonDialogs.showTitledDialog(
                            "The party has been defeated!", "Defeat...");
                    break;
                case BattleResults.DRAW:
                    CommonDialogs.showTitledDialog("The battle was a draw.",
                            "Draw");
                    break;
                case BattleResults.FLED:
                    CommonDialogs.showTitledDialog("The party fled!",
                            "Party Fled");
                    break;
                case BattleResults.ENEMY_FLED:
                    CommonDialogs.showTitledDialog("The enemies fled!",
                            "Enemies Fled");
                    break;
                case BattleResults.IN_PROGRESS:
                    CommonDialogs.showTitledDialog(
                            "The battle isn't over, but somehow the game thinks it is.",
                            "Uh-Oh!");
                    break;
                default:
                    CommonDialogs.showTitledDialog(
                            "The result of the battle is unknown!", "Uh-Oh!");
                    break;
                }
            }
            // Strip effects
            PartyManager.getParty().getLeader().stripAllEffects();
            // Level Up Check
            PartyManager.getParty().checkPartyLevelUp();
            // Battle Done
            this.battleDone();
            if (rewardsFlag) {
                BossRewards.doRewards();
            }
        }
    }

    @Override
    public boolean drain() {
        // Check Action Counter
        if (this.getActiveActionCounter() > 0) {
            Creature activeEnemy = null;
            try {
                activeEnemy = this.getEnemyBC().getTemplate();
            } catch (final NullPointerException npe) {
                // Ignore
            }
            int drainChance;
            var drainAmount = 0;
            this.bd.getActiveCharacter()
                    .modifyAP(MapTurnBattleLogic.DRAIN_ACTION_POINTS);
            drainChance = StatConstants.CHANCE_DRAIN;
            if (activeEnemy == null) {
                // Failed - nobody to drain from
                this.setStatusMessage(this.bd.getActiveCharacter().getName()
                        + " tries to drain, but nobody is there to drain from!");
                return false;
            }
            if (drainChance <= 0) {
                // Failed
                this.setStatusMessage(this.bd.getActiveCharacter().getName()
                        + " tries to drain, but fails!");
                return false;
            } else if (drainChance >= 100) {
                // Succeeded, unless target has 0 MP
                final var drained = new RandomRange(0,
                        activeEnemy.getCurrentMP());
                drainAmount = drained.generate();
                if (drainAmount == 0) {
                    this.setStatusMessage(this.bd.getActiveCharacter().getName()
                            + " tries to drain, but no MP is left to drain!");
                    return false;
                } else {
                    activeEnemy.offsetCurrentMP(-drainAmount);
                    this.bd.getActiveCharacter().getTemplate()
                            .offsetCurrentMP(drainAmount);
                    this.setStatusMessage(this.bd.getActiveCharacter().getName()
                            + " tries to drain, and successfully drains "
                            + drainAmount + " MP!");
                    return true;
                }
            } else {
                final var chance = new RandomRange(0, 100);
                final var randomChance = chance.generate();
                if (randomChance <= drainChance) {
                    // Succeeded
                    final var drained = new RandomRange(0,
                            activeEnemy.getCurrentMP());
                    drainAmount = drained.generate();
                    if (drainAmount == 0) {
                        this.setStatusMessage(this.bd.getActiveCharacter()
                                .getName()
                                + " tries to drain, but no MP is left to drain!");
                        return false;
                    } else {
                        activeEnemy.offsetCurrentMP(-drainAmount);
                        this.bd.getActiveCharacter().getTemplate()
                                .offsetCurrentMP(drainAmount);
                        this.setStatusMessage(this.bd.getActiveCharacter()
                                .getName()
                                + " tries to drain, and successfully drains "
                                + drainAmount + " MP!");
                        return true;
                    }
                } else {
                    // Failed
                    this.setStatusMessage(this.bd.getActiveCharacter().getName()
                            + " tries to drain, but fails!");
                    return false;
                }
            }
        } else {
            // Deny drain - out of actions
            if (!this.bd.getActiveCharacter().getTemplate().hasMapAI()) {
                this.setStatusMessage("Out of actions!");
            }
            return false;
        }
    }

    @Override
    public void endTurn() {
        this.newRound = this.setNextActive(this.newRound);
        if (this.newRound) {
            this.setStatusMessage("New Round");
            this.newRound = this.setNextActive(this.newRound);
            // Check result
            this.result = this.getResult();
            if (this.result != BattleResults.IN_PROGRESS) {
                this.doResult();
                return;
            }
        }
        this.updateStatsAndEffects();
        this.battleGUI.getViewManager()
                .setViewingWindowCenterX(this.bd.getActiveCharacter().getY());
        this.battleGUI.getViewManager()
                .setViewingWindowCenterY(this.bd.getActiveCharacter().getX());
        this.redrawBattle();
    }

    private void executeAutoAI(final BattleCharacter acting) {
        final var index = this.bd.findBattler(acting.getName());
        final var action = this.auto
                .getNextAction(this.bd.getBattlerAIContexts()[index]);
        switch (action) {
        case MapAI.ACTION_MOVE:
            final var x = this.auto.getMoveX();
            final var y = this.auto.getMoveY();
            final var activeTID = this.bd.getActiveCharacter().getTeamID();
            final var theEnemy = activeTID == Creature.TEAM_PARTY
                    ? this.enemy
                    : this.bd.getBattlers()[this.bd
                            .findFirstBattlerOnTeam(Creature.TEAM_PARTY)];
            final var activeDE = activeTID == Creature.TEAM_PARTY
                    ? this.ede
                    : this.pde;
            this.updatePositionInternal(x, y, false, acting, theEnemy,
                    activeDE);
            break;
        default:
            break;
        }
    }

    @Override
    public void executeNextAIAction() {
        if (this.bd != null && this.bd.getActiveCharacter() != null
                && this.bd.getActiveCharacter().getTemplate() != null
                && this.bd.getActiveCharacter().getTemplate()
                        .getMapAI() != null) {
            final var active = this.bd.getActiveCharacter();
            if (active.getTemplate().isAlive()) {
                final var action = active.getTemplate().getMapAI()
                        .getNextAction(this.bd
                                .getBattlerAIContexts()[this.activeIndex]);
                switch (action) {
                case MapAI.ACTION_MOVE:
                    final var x = active.getTemplate().getMapAI().getMoveX();
                    final var y = active.getTemplate().getMapAI().getMoveY();
                    this.lastAIActionResult = this.updatePosition(x, y);
                    active.getTemplate().getMapAI()
                            .setLastResult(this.lastAIActionResult);
                    break;
                case MapAI.ACTION_CAST_SPELL:
                    this.lastAIActionResult = this.castSpell();
                    active.getTemplate().getMapAI()
                            .setLastResult(this.lastAIActionResult);
                    break;
                case MapAI.ACTION_DRAIN:
                    this.lastAIActionResult = this.drain();
                    active.getTemplate().getMapAI()
                            .setLastResult(this.lastAIActionResult);
                    break;
                case MapAI.ACTION_STEAL:
                    this.lastAIActionResult = this.steal();
                    active.getTemplate().getMapAI()
                            .setLastResult(this.lastAIActionResult);
                    break;
                default:
                    this.lastAIActionResult = true;
                    this.endTurn();
                    this.stopWaitingForAI();
                    this.ait.aiWait();
                    break;
                }
            }
        }
    }

    private int findNextSmallestSpeed(final int max) {
        var res = -1;
        var found = 0;
        for (var x = 0; x < this.speedArray.length; x++) {
            if (!this.speedMarkArray[x] && (this.speedArray[x] <= max
                    && this.speedArray[x] > found)) {
                res = x;
                found = this.speedArray[x];
            }
        }
        if (res != -1) {
            this.speedMarkArray[res] = true;
        }
        return res;
    }

    @Override
    public void fireArrow(final int x, final int y) {
        if (this.bd.getActiveCharacter().getCurrentAP() > 0) {
            // Has actions left
            this.bd.getActiveCharacter().modifyAP(1);
            this.updateStatsAndEffects();
            this.battleGUI.turnEventHandlersOff();
            final var at = new MapBattleArrowTask(x, y,
                    this.bd.getBattleMaze(), this.bd.getActiveCharacter());
            at.start();
        } else // Deny arrow - out of actions
        if (!this.bd.getActiveCharacter().getTemplate().hasMapAI()) {
            this.setStatusMessage("Out of actions!");
        }
    }

    private void generateSpeedArray() {
        this.speedArray = new int[this.bd.getBattlers().length];
        this.speedMarkArray = new boolean[this.speedArray.length];
        this.resetSpeedArray();
    }

    private int getActiveActionCounter() {
        return this.bd.getActiveCharacter().getCurrentAP();
    }

    private int getActiveAttackCounter() {
        return this.bd.getActiveCharacter().getCurrentAT();
    }

    private int getActiveSpellCounter() {
        return this.bd.getActiveCharacter().getCurrentSP();
    }

    @Override
    public Creature getEnemy() {
        return this.enemy.getTemplate();
    }

    private BattleCharacter getEnemyBC() {
        final var px = this.bd.getActiveCharacter().getX();
        final var py = this.bd.getActiveCharacter().getY();
        final var m = this.bd.getBattleMaze();
        AbstractMazeObject next = null;
        for (var x = -1; x <= 1; x++) {
            for (var y = -1; y <= 1; y++) {
                if (x == 0 && y == 0) {
                    continue;
                }
                try {
                    next = m.getCell(px + x, py + y, 0,
                            MazeConstants.LAYER_OBJECT);
                } catch (final ArrayIndexOutOfBoundsException aioob) {
                    // Ignore
                }
                if ((next != null) && next.isSolidInBattle()) {
                    if (next instanceof BattleCharacter) {
                        return (BattleCharacter) next;
                    }
                }
            }
        }
        return null;
    }

    private int getGold() {
        var res = 0;
        for (var x = 0; x < this.bd.getBattlers().length; x++) {
            if ((this.bd.getBattlers()[x] != null)
                    && (this.bd.getBattlers()[x].getTeamID() != 0)) {
                res += this.bd.getBattlers()[x].getTemplate().getGold();
            }
        }
        return res;
    }

    @Override
    public boolean getLastAIActionResult() {
        return this.lastAIActionResult;
    }

    // Methods
    @Override
    public JFrame getOutputFrame() {
        return this.battleGUI.getOutputFrame();
    }

    @Override
    public int getResult() {
        int currResult;
        if (this.result != BattleResults.IN_PROGRESS) {
            return this.result;
        }
        if (this.areTeamEnemiesAlive(Creature.TEAM_PARTY)
                && !this.isTeamAlive(Creature.TEAM_PARTY)) {
            currResult = BattleResults.LOST;
        } else if (!this.areTeamEnemiesAlive(Creature.TEAM_PARTY)
                && this.isTeamAlive(Creature.TEAM_PARTY)) {
            currResult = BattleResults.WON;
        } else if (!this.areTeamEnemiesAlive(Creature.TEAM_PARTY)
                && !this.isTeamAlive(Creature.TEAM_PARTY)) {
            currResult = BattleResults.DRAW;
        } else if (this.isTeamAlive(Creature.TEAM_PARTY)
                && !this.isTeamGone(Creature.TEAM_PARTY)
                && this.areTeamEnemiesDeadOrGone(Creature.TEAM_PARTY)) {
            currResult = BattleResults.WON;
        } else if (!this.isTeamAlive(Creature.TEAM_PARTY)
                && !this.isTeamGone(Creature.TEAM_PARTY)
                && !this.areTeamEnemiesDeadOrGone(Creature.TEAM_PARTY)) {
            currResult = BattleResults.LOST;
        } else if (this.areTeamEnemiesGone(Creature.TEAM_PARTY)) {
            currResult = BattleResults.ENEMY_FLED;
        } else if (this.isTeamGone(Creature.TEAM_PARTY)) {
            currResult = BattleResults.FLED;
        } else {
            currResult = BattleResults.IN_PROGRESS;
        }
        return currResult;
    }

    private void hideBattle() {
        this.battleGUI.hideBattle();
    }

    private boolean isTeamAlive(final int teamID) {
        for (var x = 0; x < this.bd.getBattlers().length; x++) {
            if ((this.bd.getBattlers()[x] != null)
                    && (this.bd.getBattlers()[x].getTeamID() == teamID)) {
                final var res = this.bd.getBattlers()[x].getTemplate()
                        .isAlive();
                if (res) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isTeamGone(final int teamID) {
        var res = true;
        for (var x = 0; x < this.bd.getBattlers().length; x++) {
            if ((this.bd.getBattlers()[x] != null)
                    && (this.bd.getBattlers()[x].getTeamID() == teamID)) {
                if (this.bd.getBattlers()[x].getTemplate().isAlive()) {
                    res = res && !this.bd.getBattlers()[x].isActive();
                    if (!res) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean isWaitingForAI() {
        return !this.battleGUI.areEventHandlersOn();
    }

    @Override
    public void maintainEffects(final boolean player) {
        for (var x = 0; x < this.bd.getBattlers().length; x++) {
            // Maintain Effects
            if (this.bd.getBattlers()[x] != null
                    && this.bd.getBattlers()[x].isActive()) {
                final var active = this.bd.getBattlers()[x].getTemplate();
                // Use Effects
                active.useEffects();
                // Display all effect messages
                final var effectMessages = this.bd.getBattlers()[x]
                        .getTemplate().getAllCurrentEffectMessages();
                final var individualEffectMessages = effectMessages
                        .split("\n");
                for (final String message : individualEffectMessages) {
                    if (!message.equals(Effect.getNullMessage())) {
                        this.setStatusMessage(message);
                        try {
                            Thread.sleep(PreferencesManager.getBattleSpeed());
                        } catch (final InterruptedException ie) {
                            // Ignore
                        }
                    }
                }
                // Handle low health for party members
                if (active.isAlive()
                        && active.getTeamID() == Creature.TEAM_PARTY
                        && active.getCurrentHP() <= active.getMaximumHP() * 3
                                / 10) {
                    SoundManager.playSound(SoundConstants.SOUND_LOW_HEALTH);
                }
                // Cull Inactive Effects
                active.cullInactiveEffects();
                // Handle death caused by effects
                if (!active.isAlive()) {
                    if (this.bd.getBattlers()[x]
                            .getTeamID() != Creature.TEAM_PARTY) {
                        // Update victory spoils
                        this.battleExp = this.bd.getBattlers()[x].getTemplate()
                                .getExperience();
                    }
                    // Set dead character to inactive
                    this.bd.getBattlers()[x].deactivate();
                    // Remove effects from dead character
                    active.stripAllEffects();
                    // Remove character from battle
                    this.bd.getBattleMaze().setCell(new Empty(),
                            this.bd.getBattlers()[x].getX(),
                            this.bd.getBattlers()[x].getY(), 0,
                            MazeConstants.LAYER_OBJECT);
                    if (this.bd.getActiveCharacter().getName()
                            .equals(this.bd.getBattlers()[x].getName())) {
                        // Active character died, end turn
                        this.endTurn();
                    }
                }
            }
        }
    }

    private void performNewRoundActions() {
        for (var x = 0; x < this.bd.getBattlers().length; x++) {
            if (this.bd.getBattlers()[x] != null) {
                // Perform New Round Actions
                if (this.bd.getBattlerAIContexts()[x] != null
                        && this.bd.getBattlerAIContexts()[x].getCharacter()
                                .getTemplate().hasMapAI()
                        && this.bd.getBattlers()[x].isActive()
                        && this.bd.getBattlers()[x].getTemplate().isAlive()) {
                    this.bd.getBattlerAIContexts()[x].getCharacter()
                            .getTemplate().getMapAI().newRoundHook();
                }
            }
        }
    }

    private void redrawBattle() {
        this.battleGUI.redrawBattle(this.bd);
    }

    @Override
    public void redrawOneBattleSquare(final int x, final int y,
            final AbstractMazeObject obj3) {
        this.battleGUI.redrawOneBattleSquare(this.bd, x, y, obj3);
    }

    @Override
    public void resetGUI() {
        // Destroy old GUI
        this.battleGUI.getOutputFrame().dispose();
        // Create new GUI
        this.battleGUI = new MapTurnBattleGUI();
    }

    private void resetSpeedArray() {
        for (var x = 0; x < this.speedArray.length; x++) {
            if (this.bd.getBattlers()[x] != null
                    && this.bd.getBattlers()[x].getTemplate().isAlive()) {
                this.speedArray[x] = (int) this.bd.getBattlers()[x]
                        .getTemplate()
                        .getEffectedStat(StatConstants.STAT_AGILITY);
            } else {
                this.speedArray[x] = Integer.MIN_VALUE;
            }
        }
        for (var x = 0; x < this.speedMarkArray.length; x++) {
            if (this.speedArray[x] != Integer.MIN_VALUE) {
                this.speedMarkArray[x] = false;
            } else {
                this.speedMarkArray[x] = true;
            }
        }
    }

    private void setCharacterLocations() {
        final var randX = new RandomRange(0,
                this.bd.getBattleMaze().getRows() - 1);
        final var randY = new RandomRange(0,
                this.bd.getBattleMaze().getColumns() - 1);
        int rx, ry;
        // Set Character Locations
        for (var x = 0; x < this.bd.getBattlers().length; x++) {
            if ((this.bd.getBattlers()[x] != null) && (this.bd.getBattlers()[x]
                    .isActive()
                    && this.bd.getBattlers()[x].getTemplate().getX() == -1
                    && this.bd.getBattlers()[x].getTemplate().getY() == -1)) {
                rx = randX.generate();
                ry = randY.generate();
                var obj = this.bd.getBattleMaze().getCell(rx, ry,
                        0, MazeConstants.LAYER_OBJECT);
                while (obj.isSolidInBattle()) {
                    rx = randX.generate();
                    ry = randY.generate();
                    obj = this.bd.getBattleMaze().getCell(rx, ry, 0,
                            MazeConstants.LAYER_OBJECT);
                }
                this.bd.getBattlers()[x].setX(rx);
                this.bd.getBattlers()[x].setY(ry);
                this.bd.getBattleMaze().setCell(this.bd.getBattlers()[x], rx,
                        ry, 0, MazeConstants.LAYER_OBJECT);
            }
        }
    }

    private boolean setNextActive(final boolean isNewRound) {
        var res = 0;
        if (isNewRound) {
            res = this.findNextSmallestSpeed(Integer.MAX_VALUE);
        } else {
            res = this.findNextSmallestSpeed(this.lastSpeed);
        }
        if (res != -1) {
            this.lastSpeed = this.speedArray[res];
            this.activeIndex = res;
            this.bd.setActiveCharacter(this.bd.getBattlers()[this.activeIndex]);
            // Check
            if (!this.bd.getActiveCharacter().isActive()) {
                // Inactive, pick new active character
                return this.setNextActive(isNewRound);
            }
            // AI Check
            if (this.bd.getActiveCharacter().getTemplate().hasMapAI()) {
                // Run AI
                this.waitForAI();
                this.ait.aiRun();
            } else {
                // No AI
                SoundManager.playSound(SoundConstants.SOUND_PLAYER_UP);
            }
            return false;
        } else {
            // Reset Speed Array
            this.resetSpeedArray();
            // Reset Action Counters
            this.bd.roundResetBattlers();
            // Maintain effects
            this.maintainEffects(true);
            this.updateStatsAndEffects();
            // Perform new round actions
            this.performNewRoundActions();
            // Play new round sound
            SoundManager.playSound(SoundConstants.SOUND_NEXT_ROUND);
            // Nobody to act next, set new round flag
            return true;
        }
    }

    @Override
    public void setResult(final int resultCode) {
        // Do nothing
    }

    @Override
    public void setStatusMessage(final String msg) {
        this.battleGUI.setStatusMessage(msg);
    }

    private void showBattle() {
        this.battleGUI.showBattle();
    }

    @Override
    public boolean steal() {
        // Check Action Counter
        if (this.getActiveActionCounter() > 0) {
            Creature activeEnemy = null;
            try {
                activeEnemy = this.getEnemyBC().getTemplate();
            } catch (final NullPointerException npe) {
                // Ignore
            }
            int stealChance;
            var stealAmount = 0;
            this.bd.getActiveCharacter()
                    .modifyAP(MapTurnBattleLogic.STEAL_ACTION_POINTS);
            stealChance = StatConstants.CHANCE_STEAL;
            if (activeEnemy == null) {
                // Failed - nobody to steal from
                this.setStatusMessage(this.bd.getActiveCharacter().getName()
                        + " tries to steal, but nobody is there to steal from!");
                return false;
            }
            if (stealChance <= 0) {
                // Failed
                this.setStatusMessage(this.bd.getActiveCharacter().getName()
                        + " tries to steal, but fails!");
                return false;
            } else if (stealChance >= 100) {
                // Succeeded, unless target has 0 Gold
                final var stole = new RandomRange(0,
                        activeEnemy.getGold());
                stealAmount = stole.generate();
                if (stealAmount == 0) {
                    this.setStatusMessage(this.bd.getActiveCharacter().getName()
                            + " tries to steal, but no Gold is left to steal!");
                    return false;
                } else {
                    this.bd.getActiveCharacter().getTemplate()
                            .offsetGold(stealAmount);
                    this.setStatusMessage(this.bd.getActiveCharacter().getName()
                            + " tries to steal, and successfully steals "
                            + stealAmount + " gold!");
                    return true;
                }
            } else {
                final var chance = new RandomRange(0, 100);
                final var randomChance = chance.generate();
                if (randomChance <= stealChance) {
                    // Succeeded, unless target has 0 Gold
                    final var stole = new RandomRange(0,
                            activeEnemy.getGold());
                    stealAmount = stole.generate();
                    if (stealAmount == 0) {
                        this.setStatusMessage(this.bd.getActiveCharacter()
                                .getName()
                                + " tries to steal, but no Gold is left to steal!");
                        return false;
                    } else {
                        this.bd.getActiveCharacter().getTemplate()
                                .offsetGold(stealAmount);
                        this.setStatusMessage(this.bd.getActiveCharacter()
                                .getName()
                                + " tries to steal, and successfully steals "
                                + stealAmount + " gold!");
                        return true;
                    }
                } else {
                    // Failed
                    this.setStatusMessage(this.bd.getActiveCharacter().getName()
                            + " tries to steal, but fails!");
                    return false;
                }
            }
        } else {
            // Deny steal - out of actions
            if (!this.bd.getActiveCharacter().getTemplate().hasMapAI()) {
                this.setStatusMessage("Out of actions!");
            }
            return false;
        }
    }

    private void stopWaitingForAI() {
        this.battleGUI.turnEventHandlersOn();
    }

    private void updateAllAIContexts() {
        for (var x = 0; x < this.bd.getBattlers().length; x++) {
            // Update all AI Contexts
            if ((this.bd.getBattlers()[x] != null)
                    && (this.bd.getBattlerAIContexts()[x] != null)) {
                this.bd.getBattlerAIContexts()[x]
                        .updateContext(this.bd.getBattleMaze());
            }
        }
    }

    @Override
    public boolean updatePosition(final int x, final int y) {
        final var activeTID = this.bd.getActiveCharacter().getTeamID();
        var theEnemy = activeTID == Creature.TEAM_PARTY ? this.enemy
                : this.bd.getBattlers()[this.bd
                        .findFirstBattlerOnTeam(Creature.TEAM_PARTY)];
        final var activeDE = activeTID == Creature.TEAM_PARTY
                ? this.ede
                : this.pde;
        if (x == 0 && y == 0) {
            theEnemy = this.bd.getActiveCharacter();
        }
        return this.updatePositionInternal(x, y, true,
                this.bd.getActiveCharacter(), theEnemy, activeDE);
    }

    private boolean updatePositionInternal(final int x, final int y,
            final boolean useAP, final BattleCharacter active,
            final BattleCharacter theEnemy,
            final AbstractDamageEngine activeDE) {
        this.updateAllAIContexts();
        var px = active.getX();
        var py = active.getY();
        final var m = this.bd.getBattleMaze();
        AbstractMazeObject next = null;
        AbstractMazeObject nextGround = null;
        AbstractMazeObject currGround = null;
        active.saveLocation();
        this.battleGUI.getViewManager().saveViewingWindow();
        try {
            next = m.getCell(px + x, py + y, 0, MazeConstants.LAYER_OBJECT);
            nextGround = m.getCell(px + x, py + y, 0,
                    MazeConstants.LAYER_GROUND);
            currGround = m.getCell(px, py, 0, MazeConstants.LAYER_GROUND);
        } catch (final ArrayIndexOutOfBoundsException aioob) {
            // Ignore
        }
        if (next != null && nextGround != null && currGround != null) {
            if (!next.isSolidInBattle()) {
                if (useAP && this.getActiveActionCounter() >= MapAIContext
                        .getAPCost() || !useAP) {
                    // Move
                    AbstractMazeObject obj1 = null;
                    AbstractMazeObject obj2 = null;
                    AbstractMazeObject obj3 = null;
                    AbstractMazeObject obj4 = null;
                    AbstractMazeObject obj6 = null;
                    AbstractMazeObject obj7 = null;
                    AbstractMazeObject obj8 = null;
                    AbstractMazeObject obj9 = null;
                    try {
                        obj1 = m.getCell(px - 1, py - 1, 0,
                                MazeConstants.LAYER_OBJECT);
                    } catch (final ArrayIndexOutOfBoundsException aioob) {
                        // Ignore
                    }
                    try {
                        obj2 = m.getCell(px, py - 1, 0,
                                MazeConstants.LAYER_OBJECT);
                    } catch (final ArrayIndexOutOfBoundsException aioob) {
                        // Ignore
                    }
                    try {
                        obj3 = m.getCell(px + 1, py - 1, 0,
                                MazeConstants.LAYER_OBJECT);
                    } catch (final ArrayIndexOutOfBoundsException aioob) {
                        // Ignore
                    }
                    try {
                        obj4 = m.getCell(px - 1, py, 0,
                                MazeConstants.LAYER_OBJECT);
                    } catch (final ArrayIndexOutOfBoundsException aioob) {
                        // Ignore
                    }
                    try {
                        obj6 = m.getCell(px + 1, py - 1, 0,
                                MazeConstants.LAYER_OBJECT);
                    } catch (final ArrayIndexOutOfBoundsException aioob) {
                        // Ignore
                    }
                    try {
                        obj7 = m.getCell(px - 1, py + 1, 0,
                                MazeConstants.LAYER_OBJECT);
                    } catch (final ArrayIndexOutOfBoundsException aioob) {
                        // Ignore
                    }
                    try {
                        obj8 = m.getCell(px, py + 1, 0,
                                MazeConstants.LAYER_OBJECT);
                    } catch (final ArrayIndexOutOfBoundsException aioob) {
                        // Ignore
                    }
                    try {
                        obj9 = m.getCell(px + 1, py + 1, 0,
                                MazeConstants.LAYER_OBJECT);
                    } catch (final ArrayIndexOutOfBoundsException aioob) {
                        // Ignore
                    }
                    // Auto-attack check
                    if ((obj1 != null) && (obj1 instanceof BattleCharacter)) {
                        if ((((x != -1) || (y != 0))
                                && ((x != -1)
                                        || (y != -1))
                                && ((x != 0) || (y != -1)))) {
                            final var bc1 = (BattleCharacter) obj1;
                            if (bc1.getTeamID() != active.getTeamID()) {
                                this.executeAutoAI(bc1);
                            }
                        }
                    }
                    if ((obj2 != null) && (obj2 instanceof BattleCharacter)) {
                        if (y == 1) {
                            final var bc2 = (BattleCharacter) obj2;
                            if (bc2.getTeamID() != active.getTeamID()) {
                                this.executeAutoAI(bc2);
                            }
                        }
                    }
                    if ((obj3 != null) && (obj3 instanceof BattleCharacter)) {
                        if ((((x != 0) || (y != -1))
                                && ((x != 1) || (y != -1))
                                && ((x != 1) || (y != 0)))) {
                            final var bc3 = (BattleCharacter) obj3;
                            if (bc3.getTeamID() != active.getTeamID()) {
                                this.executeAutoAI(bc3);
                            }
                        }
                    }
                    if ((obj4 != null) && (obj4 instanceof BattleCharacter)) {
                        if (x == 1) {
                            final var bc4 = (BattleCharacter) obj4;
                            if (bc4.getTeamID() != active.getTeamID()) {
                                this.executeAutoAI(bc4);
                            }
                        }
                    }
                    if ((obj6 != null) && (obj6 instanceof BattleCharacter)) {
                        if (x == -1) {
                            final var bc6 = (BattleCharacter) obj6;
                            if (bc6.getTeamID() != active.getTeamID()) {
                                this.executeAutoAI(bc6);
                            }
                        }
                    }
                    if ((obj7 != null) && (obj7 instanceof BattleCharacter)) {
                        if ((((x != -1) || (y != 0))
                                && ((x != -1) || (y != 1))
                                && ((x != 0) || (y != 1)))) {
                            final var bc7 = (BattleCharacter) obj7;
                            if (bc7.getTeamID() != active.getTeamID()) {
                                this.executeAutoAI(bc7);
                            }
                        }
                    }
                    if ((obj8 != null) && (obj8 instanceof BattleCharacter)) {
                        if (y == -1) {
                            final var bc8 = (BattleCharacter) obj8;
                            if (bc8.getTeamID() != active.getTeamID()) {
                                this.executeAutoAI(bc8);
                            }
                        }
                    }
                    if ((obj9 != null) && (obj9 instanceof BattleCharacter)) {
                        if ((((x != 0) || (y != 1))
                                && ((x != 1) || (y != 1))
                                && ((x != 1) || (y != 0)))) {
                            final var bc9 = (BattleCharacter) obj9;
                            if (bc9.getTeamID() != active.getTeamID()) {
                                this.executeAutoAI(bc9);
                            }
                        }
                    }
                    m.setCell(active.getSavedObject(), px, py, 0,
                            MazeConstants.LAYER_OBJECT);
                    active.offsetX(x);
                    active.offsetY(y);
                    px += x;
                    py += y;
                    this.battleGUI.getViewManager()
                            .offsetViewingWindowLocationX(y);
                    this.battleGUI.getViewManager()
                            .offsetViewingWindowLocationY(x);
                    active.setSavedObject(
                            m.getCell(px, py, 0, MazeConstants.LAYER_OBJECT));
                    m.setCell(active, px, py, 0, MazeConstants.LAYER_OBJECT);
                    this.decrementActiveActionCounterBy(
                            MapAIContext.getAPCost());
                    SoundManager.playSound(SoundConstants.SOUND_WALK);
                } else {
                    // Deny move - out of actions
                    if (!this.bd.getActiveCharacter().getTemplate()
                            .hasMapAI()) {
                        this.setStatusMessage("Out of moves!");
                    }
                    return false;
                }
            } else if (next instanceof BattleCharacter) {
                if (useAP && this.getActiveAttackCounter() > 0 || !useAP) {
                    // Attack
                    final var bc = (BattleCharacter) next;
                    if (bc.getTeamID() == active.getTeamID()) {
                        // Attack Friend?
                        if (!active.getTemplate().hasMapAI()) {
                            final var confirm = CommonDialogs.showConfirmDialog(
                                    "Attack Friend?", "Battle");
                            if (confirm != JOptionPane.YES_OPTION) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                    if (useAP) {
                        this.decrementActiveAttackCounter();
                    }
                    // Do damage
                    this.computeDamage(theEnemy.getTemplate(),
                            active.getTemplate(), activeDE);
                    // Handle low health for party members
                    if (theEnemy.getTemplate().isAlive()
                            && theEnemy.getTeamID() == Creature.TEAM_PARTY
                            && theEnemy.getTemplate().getCurrentHP() <= theEnemy
                                    .getTemplate().getMaximumHP() * 3 / 10) {
                        SoundManager.playSound(SoundConstants.SOUND_LOW_HEALTH);
                    }
                    // Handle enemy death
                    if (!theEnemy.getTemplate().isAlive()) {
                        if (theEnemy.getTeamID() != Creature.TEAM_PARTY) {
                            // Update victory spoils
                            this.battleExp = theEnemy.getTemplate()
                                    .getExperience();
                        }
                        // Remove effects from dead character
                        bc.getTemplate().stripAllEffects();
                        // Set dead character to inactive
                        bc.deactivate();
                        // Remove character from battle
                        this.bd.getBattleMaze().setCell(new Empty(), bc.getX(),
                                bc.getY(), 0, MazeConstants.LAYER_OBJECT);
                    }
                    // Handle self death
                    if (!active.getTemplate().isAlive()) {
                        // Remove effects from dead character
                        active.getTemplate().stripAllEffects();
                        // Set dead character to inactive
                        active.deactivate();
                        // Remove character from battle
                        this.bd.getBattleMaze().setCell(new Empty(),
                                active.getX(), active.getY(), 0,
                                MazeConstants.LAYER_OBJECT);
                        // End turn
                        this.endTurn();
                    }
                } else {
                    // Deny attack - out of actions
                    if (!this.bd.getActiveCharacter().getTemplate()
                            .hasMapAI()) {
                        this.setStatusMessage("Out of attacks!");
                    }
                    return false;
                }
            } else {
                // Move Failed
                if (!active.getTemplate().hasMapAI()) {
                    this.setStatusMessage("Can't go that way");
                }
                return false;
            }
        } else {
            // Confirm Flee
            if (!active.getTemplate().hasMapAI()) {
                SoundManager.playSound(SoundConstants.SOUND_SPECIAL);
                final var confirm = CommonDialogs
                        .showConfirmDialog("Embrace Cowardice?", "Battle");
                if (confirm != JOptionPane.YES_OPTION) {
                    this.battleGUI.getViewManager().restoreViewingWindow();
                    active.restoreLocation();
                    return false;
                }
            }
            // Flee
            this.battleGUI.getViewManager().restoreViewingWindow();
            active.restoreLocation();
            // Set fled character to inactive
            active.deactivate();
            // Remove character from battle
            m.setCell(new Empty(), active.getX(), active.getY(), 0,
                    MazeConstants.LAYER_OBJECT);
            // End Turn
            this.endTurn();
            this.updateStatsAndEffects();
            final var currResult = this.getResult();
            if (currResult != BattleResults.IN_PROGRESS) {
                // Battle Done
                this.result = currResult;
                this.doResult();
            }
            this.battleGUI.getViewManager().setViewingWindowCenterX(py);
            this.battleGUI.getViewManager().setViewingWindowCenterY(px);
            this.redrawBattle();
            return true;
        }
        this.updateStatsAndEffects();
        final var currResult = this.getResult();
        if (currResult != BattleResults.IN_PROGRESS) {
            // Battle Done
            this.result = currResult;
            this.doResult();
        }
        this.battleGUI.getViewManager().setViewingWindowCenterX(py);
        this.battleGUI.getViewManager().setViewingWindowCenterY(px);
        this.redrawBattle();
        return true;
    }

    private void updateStatsAndEffects() {
        this.battleGUI.updateStatsAndEffects(this.bd);
    }

    @Override
    public boolean useItem() {
        // Check Action Counter
        if (this.getActiveActionCounter() > 0) {
            if (!this.bd.getActiveCharacter().getTemplate().hasMapAI()) {
                // Active character has no AI, or AI is turned off
                final var success = CombatItemChucker.selectAndUseItem(
                        this.bd.getActiveCharacter().getTemplate());
                if (success) {
                    this.bd.getActiveCharacter()
                            .modifyAP(MapTurnBattleLogic.ITEM_ACTION_POINTS);
                }
                final var currResult = this.getResult();
                if (currResult != BattleResults.IN_PROGRESS) {
                    // Battle Done
                    this.result = currResult;
                    this.doResult();
                }
                return success;
            } else {
                // Active character has AI, and AI is turned on
                final var cui = this.bd.getActiveCharacter()
                        .getTemplate().getMapAI().getItemToUse();
                final var success = CombatItemChucker.useItem(cui,
                        this.bd.getActiveCharacter().getTemplate());
                if (success) {
                    this.bd.getActiveCharacter()
                            .modifyAP(MapTurnBattleLogic.ITEM_ACTION_POINTS);
                }
                final var currResult = this.getResult();
                if (currResult != BattleResults.IN_PROGRESS) {
                    // Battle Done
                    this.result = currResult;
                    this.doResult();
                }
                return success;
            }
        } else {
            // Deny use - out of actions
            if (!this.bd.getActiveCharacter().getTemplate().hasMapAI()) {
                this.setStatusMessage("Out of actions!");
            }
            return false;
        }
    }

    private void waitForAI() {
        this.battleGUI.turnEventHandlersOff();
    }
}
