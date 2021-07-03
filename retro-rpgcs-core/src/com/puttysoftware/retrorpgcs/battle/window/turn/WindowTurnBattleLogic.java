/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.battle.window.turn;

import javax.swing.JFrame;

import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.ai.window.WindowAI;
import com.puttysoftware.retrorpgcs.battle.Battle;
import com.puttysoftware.retrorpgcs.battle.BattleResults;
import com.puttysoftware.retrorpgcs.battle.damageengines.AbstractDamageEngine;
import com.puttysoftware.retrorpgcs.creatures.Creature;
import com.puttysoftware.retrorpgcs.creatures.StatConstants;
import com.puttysoftware.retrorpgcs.creatures.monsters.BossMonster;
import com.puttysoftware.retrorpgcs.creatures.monsters.Monster;
import com.puttysoftware.retrorpgcs.creatures.monsters.MonsterFactory;
import com.puttysoftware.retrorpgcs.creatures.party.PartyManager;
import com.puttysoftware.retrorpgcs.creatures.party.PartyMember;
import com.puttysoftware.retrorpgcs.effects.Effect;
import com.puttysoftware.retrorpgcs.items.combat.CombatItemChucker;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractMazeObject;
import com.puttysoftware.retrorpgcs.maze.objects.BattleCharacter;
import com.puttysoftware.retrorpgcs.prefs.PreferencesManager;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;
import com.puttysoftware.retrorpgcs.spells.SpellCaster;

public class WindowTurnBattleLogic extends Battle {
    private static final int BASE_RUN_CHANCE = 80;
    private static final int RUN_CHANCE_DIFF_FACTOR = 5;
    private static final int ENEMY_BASE_RUN_CHANCE = 60;
    private static final int ENEMY_RUN_CHANCE_DIFF_FACTOR = 10;
    // Fields
    private int stealAmount;
    private int damage;
    private boolean enemyDidDamage;
    private boolean playerDidDamage;
    private Creature enemy;
    private int result;
    private final AbstractDamageEngine pde;
    private final AbstractDamageEngine ede;
    private WindowTurnBattleGUI battleGUI;

    // Constructor
    public WindowTurnBattleLogic() {
        // Initialize Battle Parameters
        this.pde = AbstractDamageEngine.getPlayerInstance();
        this.ede = AbstractDamageEngine.getEnemyInstance();
        this.damage = 0;
        this.stealAmount = 0;
        this.enemyDidDamage = false;
        this.playerDidDamage = false;
        // Initialize GUI
        this.battleGUI = new WindowTurnBattleGUI();
    }

    @Override
    public void arrowDone(final BattleCharacter hit) {
        // Do nothing
    }

    @Override
    public final void battleDone() {
        this.battleGUI.getOutputFrame().setVisible(false);
        final var gm = RetroRPGCS.getInstance().getGameManager();
        gm.showOutput();
        gm.redrawMaze();
    }

    @Override
    public boolean castSpell() {
        final var playerCharacter = PartyManager.getParty().getLeader();
        return SpellCaster.selectAndCastSpell(playerCharacter);
    }

    final void clearMessageArea() {
        this.battleGUI.clearMessageArea();
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
        // Check damage
        if (acting instanceof PartyMember) {
            if (this.damage > 0) {
                this.playerDidDamage = true;
            } else if (this.damage < 0) {
                this.enemyDidDamage = true;
            }
        } else if (acting instanceof Monster || acting instanceof BossMonster) {
            if (this.damage > 0) {
                this.enemyDidDamage = true;
            } else if (this.damage < 0) {
                this.playerDidDamage = true;
            }
        }
    }

    final void computeEnemyDamage() {
        // Compute Enemy Damage
        this.computeDamage(PartyManager.getParty().getLeader(), this.enemy,
                this.ede);
    }

    final int computeEnemyRunChance() {
        return WindowTurnBattleLogic.ENEMY_BASE_RUN_CHANCE
                + this.enemy.getLevelDifference()
                        * WindowTurnBattleLogic.ENEMY_RUN_CHANCE_DIFF_FACTOR;
    }

    final void computePlayerDamage() {
        // Compute Player Damage
        this.computeDamage(this.enemy, PartyManager.getParty().getLeader(),
                this.pde);
    }

    final int computeRunChance() {
        return WindowTurnBattleLogic.BASE_RUN_CHANCE
                - this.enemy.getLevelDifference()
                        * WindowTurnBattleLogic.RUN_CHANCE_DIFF_FACTOR;
    }

    @Override
    public final void displayActiveEffects() {
        boolean flag1 = false, flag2 = false, flag3 = false;
        final var playerCharacter = PartyManager.getParty().getLeader();
        final var effectString = playerCharacter.getCompleteEffectString();
        final var effectMessages = playerCharacter
                .getAllCurrentEffectMessages();
        final var enemyEffectMessages = this.enemy
                .getAllCurrentEffectMessages();
        final var nMsg = Effect.getNullMessage();
        if (!effectString.equals(nMsg)) {
            flag1 = true;
        }
        if (!effectMessages.equals(nMsg)) {
            flag2 = true;
        }
        if (!enemyEffectMessages.equals(nMsg)) {
            flag3 = true;
        }
        if (flag1) {
            this.setStatusMessage(effectString);
        }
        if (flag2) {
            this.setStatusMessage(effectMessages);
        }
        if (flag3) {
            this.setStatusMessage(enemyEffectMessages);
        }
    }

    @Override
    public final void displayBattleStats() {
        final var playerCharacter = PartyManager.getParty().getLeader();
        final var enemyName = this.enemy.getName();
        final var fightingWhat = this.enemy.getFightingWhatString();
        final var monsterLevelString = enemyName + "'s Level: "
                + Integer.toString(this.enemy.getLevel());
        final var monsterHPString = this.enemy.getHPString();
        final var monsterMPString = this.enemy.getMPString();
        final var playerHPString = playerCharacter.getHPString();
        final var playerMPString = playerCharacter.getMPString();
        final var displayMonsterHPString = enemyName + "'s HP: "
                + monsterHPString;
        final var displayMonsterMPString = enemyName + "'s MP: "
                + monsterMPString;
        final var displayPlayerHPString = "Your HP: " + playerHPString;
        final var displayPlayerMPString = "Your MP: " + playerMPString;
        final var displayString = fightingWhat + "\n" + monsterLevelString
                + "\n" + displayMonsterHPString + "\n" + displayMonsterMPString
                + "\n" + displayPlayerHPString + "\n" + displayPlayerMPString;
        this.setStatusMessage(displayString);
    }

    final void displayEnemyRoundResults() {
        // Display enemy round results
        if (this.result != BattleResults.ENEMY_FLED) {
            final var enemyName = this.enemy.getName();
            final var enemyDamageString = Integer.toString(this.damage);
            final var enemyFumbleDamageString = Integer
                    .toString(this.damage);
            String displayEnemyDamageString = null;
            var enemyWhackString = "";
            if (this.ede.weaponFumble()) {
                displayEnemyDamageString = "FUMBLE! The " + enemyName
                        + " drops its weapon, doing " + enemyFumbleDamageString
                        + " damage to itself!";
                SoundManager.playSound(SoundConstants.SOUND_FUMBLE);
                enemyWhackString = "";
            } else {
                if (this.damage == 0) {
                    displayEnemyDamageString = "The " + enemyName
                            + " tries to hit you, but MISSES!";
                    SoundManager.playSound(SoundConstants.SOUND_MISSED);
                } else if (this.damage < 0) {
                    displayEnemyDamageString = "The " + enemyName
                            + " tries to hit you, but you RIPOSTE for "
                            + -this.damage + " damage!";
                    SoundManager.playSound(SoundConstants.SOUND_COUNTER);
                } else {
                    displayEnemyDamageString = "The " + enemyName
                            + " hits you for " + enemyDamageString + " damage!";
                    SoundManager.playSound(SoundConstants.SOUND_HIT);
                }
                if (this.ede.weaponCrit()) {
                    enemyWhackString += "CRITICAL HIT!\n";
                    SoundManager.playSound(SoundConstants.SOUND_CRITICAL);
                }
                if (this.ede.weaponPierce()) {
                    enemyWhackString += "The " + enemyName
                            + "'s attack pierces YOUR armor!\n";
                }
            }
            final var displayString = enemyWhackString
                    + displayEnemyDamageString;
            this.setStatusMessage(displayString);
        }
    }

    final void displayPlayerRoundResults() {
        // Display player round results
        if (this.result != BattleResults.ENEMY_FLED) {
            final var enemyName = this.enemy.getName();
            final var playerDamageString = Integer.toString(this.damage);
            final var playerFumbleDamageString = Integer
                    .toString(this.damage);
            String displayPlayerDamageString = null;
            var playerWhackString = new StringBuilder();
            if (this.pde.weaponFumble()) {
                displayPlayerDamageString = "FUMBLE! You drop your weapon, doing "
                        + playerFumbleDamageString + " damage to yourself!";
                SoundManager.playSound(SoundConstants.SOUND_FUMBLE);
            } else {
                if (this.damage == 0) {
                    displayPlayerDamageString = "You try to hit the "
                            + enemyName + ", but MISS!";
                    SoundManager.playSound(SoundConstants.SOUND_MISSED);
                } else if (this.damage < 0) {
                    displayPlayerDamageString = "You try to hit the "
                            + enemyName + ", but are RIPOSTED for "
                            + -this.damage + " damage!";
                    SoundManager.playSound(SoundConstants.SOUND_COUNTER);
                } else {
                    displayPlayerDamageString = "You hit the " + enemyName
                            + " for " + playerDamageString + " damage!";
                    SoundManager.playSound(SoundConstants.SOUND_HIT);
                }
                if (this.pde.weaponCrit()) {
                    playerWhackString.append("CRITICAL HIT!\n");
                    SoundManager.playSound(SoundConstants.SOUND_CRITICAL);
                }
                if (this.pde.weaponPierce()) {
                    playerWhackString.append("Your attack pierces the ")
                            .append(enemyName).append("'s armor!\n");
                }
            }
            final var displayString = playerWhackString
                    .append(displayPlayerDamageString).toString();
            this.setStatusMessage(displayString);
        }
    }

    // Methods
    @Override
    public void doBattle() {
        try {
            final var app = RetroRPGCS.getInstance();
            final var gm = app.getGameManager();
            if (app.getMode() != RetroRPGCS.STATUS_BATTLE) {
                SoundManager.playSound(SoundConstants.SOUND_BATTLE);
            }
            app.setMode(RetroRPGCS.STATUS_BATTLE);
            gm.hideOutput();
            gm.stopMovement();
            this.enemy = MonsterFactory.getNewMonsterInstance();
            this.enemy.loadCreature();
            this.enemyDidDamage = false;
            this.playerDidDamage = false;
            this.setResult(BattleResults.IN_PROGRESS);
            this.battleGUI.initBattle(this.enemy.getImage());
            this.firstUpdateMessageArea();
        } catch (final Throwable t) {
            RetroRPGCS.getInstance().handleError(t);
        }
    }

    @Override
    public void doBattleByProxy() {
        this.enemy = MonsterFactory.getNewMonsterInstance();
        this.enemy.loadCreature();
        final var playerCharacter = PartyManager.getParty().getLeader();
        final var m = this.enemy;
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

    @Override
    public final boolean doPlayerActions(final int actionToPerform) {
        var success = true;
        final var playerCharacter = PartyManager.getParty().getLeader();
        switch (actionToPerform) {
        case WindowAI.ACTION_ATTACK:
            final var actions = playerCharacter
                    .getWindowBattleActionsPerRound();
            for (var x = 0; x < actions; x++) {
                this.computePlayerDamage();
                this.displayPlayerRoundResults();
            }
            break;
        case WindowAI.ACTION_CAST_SPELL:
            success = this.castSpell();
            break;
        case WindowAI.ACTION_FLEE:
            final var rf = new RandomRange(0, 100);
            final var runChance = rf.generate();
            if (runChance <= this.computeRunChance()) {
                // Success
                this.setResult(BattleResults.FLED);
            } else {
                // Failure
                success = false;
                this.updateMessageAreaFleeFailed();
            }
            break;
        case WindowAI.ACTION_STEAL:
            success = this.steal();
            if (success) {
                SoundManager.playSound(SoundConstants.SOUND_DRAIN);
                this.updateMessageAreaPostSteal();
            } else {
                SoundManager.playSound(SoundConstants.SOUND_ACTION_FAILED);
                this.updateMessageAreaStealFailed();
            }
            break;
        case WindowAI.ACTION_DRAIN:
            success = this.drain();
            if (success) {
                SoundManager.playSound(SoundConstants.SOUND_DRAIN);
                this.updateMessageAreaPostDrain();
            } else {
                SoundManager.playSound(SoundConstants.SOUND_ACTION_FAILED);
                this.updateMessageAreaDrainFailed();
            }
            break;
        case WindowAI.ACTION_USE_ITEM:
            success = this.useItem();
            break;
        default:
            break;
        }
        return success;
    }

    @Override
    public void doResult() {
        final var playerCharacter = PartyManager.getParty().getLeader();
        final var m = this.enemy;
        var rewardsFlag = false;
        if (m instanceof BossMonster) {
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
                playerCharacter.healPercentage(Creature.FULL_HEAL_PERCENTAGE);
                playerCharacter
                        .regeneratePercentage(Creature.FULL_HEAL_PERCENTAGE);
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
                this.setStatusMessage("You gain " + m.getExperience()
                        + " experience and " + m.getGold() + " Gold.");
                playerCharacter.offsetExperience(m.getExperience());
                playerCharacter.offsetGold(m.getGold());
                SoundManager.playSound(SoundConstants.SOUND_VICTORY);
                break;
            case BattleResults.PERFECT:
                this.setStatusMessage("You gain " + m.getExperience()
                        + " experience and " + m.getGold() + " Gold,\nplus "
                        + m.getPerfectBonusGold()
                        + " extra gold for a perfect fight!");
                playerCharacter.offsetExperience(m.getExperience());
                playerCharacter
                        .offsetGold(m.getGold() + m.getPerfectBonusGold());
                SoundManager.playSound(SoundConstants.SOUND_VICTORY);
                break;
            case BattleResults.LOST:
                this.setStatusMessage("You lost...");
                SoundManager.playSound(SoundConstants.SOUND_GAME_OVER);
                PartyManager.getParty().getLeader().onDeath(-10);
                break;
            case BattleResults.ANNIHILATED:
                this.setStatusMessage(
                        "You lost without hurting your foe... you were annihilated!");
                SoundManager.playSound(SoundConstants.SOUND_GAME_OVER);
                PartyManager.getParty().getLeader().onDeath(-20);
                break;
            case BattleResults.DRAW:
                this.setStatusMessage(
                        "The battle was a draw. You are fully healed!");
                playerCharacter.healPercentage(Creature.FULL_HEAL_PERCENTAGE);
                playerCharacter
                        .regeneratePercentage(Creature.FULL_HEAL_PERCENTAGE);
                break;
            case BattleResults.FLED:
                this.setStatusMessage("You ran away successfully!");
                break;
            case BattleResults.ENEMY_FLED:
                this.setStatusMessage("The enemy runs away!");
                this.setStatusMessage(
                        "Since the enemy ran away, you gain nothing for this battle.");
                break;
            default:
                break;
            }
        }
        // Cleanup
        this.battleGUI.doResultCleanup();
        playerCharacter.stripAllEffects();
        this.enemy.stripAllEffects();
        // Level Up Check
        if (playerCharacter.checkLevelUp()) {
            playerCharacter.levelUp();
            if (PreferencesManager.getSoundsEnabled()) {
                SoundManager.playSound(SoundConstants.SOUND_LEVEL_UP);
            }
            this.setStatusMessage(
                    "You reached level " + playerCharacter.getLevel() + ".");
        }
        // Final Cleanup
        this.battleGUI.doResultFinalCleanup(rewardsFlag);
    }

    @Override
    public final boolean drain() {
        final var playerCharacter = PartyManager.getParty().getLeader();
        final var drainChance = StatConstants.CHANCE_DRAIN;
        final var chance = new RandomRange(0, 100);
        final var randomChance = chance.generate();
        if (randomChance <= drainChance) {
            // Succeeded
            final var drained = new RandomRange(0,
                    this.enemy.getCurrentMP());
            final var drainAmount = drained.generate();
            this.enemy.offsetCurrentMP(-drainAmount);
            playerCharacter.offsetCurrentMP(drainAmount);
            return true;
        } else {
            // Failed
            return false;
        }
    }

    @Override
    public void endTurn() {
        // Do nothing
    }

    @Override
    public final void executeNextAIAction() {
        final var actionToPerform = this.enemy.getWindowAI()
                .getNextAction(this.enemy);
        switch (actionToPerform) {
        case WindowAI.ACTION_ATTACK:
            final var actions = this.enemy.getWindowBattleActionsPerRound();
            for (var x = 0; x < actions; x++) {
                this.computeEnemyDamage();
                this.displayEnemyRoundResults();
            }
            break;
        case WindowAI.ACTION_CAST_SPELL:
            SpellCaster.castSpell(this.enemy.getWindowAI().getSpellToCast(),
                    this.enemy);
            break;
        case WindowAI.ACTION_FLEE:
            final var rf = new RandomRange(0, 100);
            final var runChance = rf.generate();
            if (runChance <= this.computeEnemyRunChance()) {
                // Success
                this.setResult(BattleResults.ENEMY_FLED);
            } else {
                // Failure
                this.updateMessageAreaEnemyFleeFailed();
            }
            break;
        default:
            break;
        }
    }

    @Override
    public void fireArrow(final int x, final int y) {
        // Do nothing
    }

    final void firstUpdateMessageArea() {
        this.clearMessageArea();
        this.setStatusMessage("*** Beginning of Round ***");
        this.displayBattleStats();
        this.setStatusMessage("*** Beginning of Round ***\n");
        // Determine initiative
        var enemyGotJump = false;
        if (this.enemy.getSpeed() > PartyManager.getParty().getLeader()
                .getSpeed()) {
            // Enemy acts first!
            enemyGotJump = true;
        } else if (this.enemy.getSpeed() < PartyManager.getParty().getLeader()
                .getSpeed()) {
            // You act first!
            enemyGotJump = false;
        } else {
            // Equal, decide randomly
            final var jump = new RandomRange(0, 1);
            final var whoFirst = jump.generate();
            if (whoFirst == 1) {
                // Enemy acts first!
                enemyGotJump = true;
            } else {
                // You act first!
                enemyGotJump = false;
            }
        }
        if (enemyGotJump) {
            this.setStatusMessage("The enemy acts first!");
            this.executeNextAIAction();
            // Display Active Effects
            this.displayActiveEffects();
            // Maintain Effects
            this.maintainEffects(true);
            this.maintainEffects(false);
            // Check result
            this.setResult(this.getResult());
            if (this.result != BattleResults.IN_PROGRESS) {
                this.doResult();
                return;
            }
        } else {
            this.setStatusMessage("You act first!");
        }
        this.setStatusMessage("\n*** End of Round ***");
        this.displayBattleStats();
        this.setStatusMessage("*** End of Round ***");
        this.stripExtraNewLine();
        this.battleGUI.getOutputFrame().pack();
    }

    @Override
    public Creature getEnemy() {
        return this.enemy;
    }

    @Override
    public boolean getLastAIActionResult() {
        return true;
    }

    @Override
    public JFrame getOutputFrame() {
        return this.battleGUI.getOutputFrame();
    }

    @Override
    public final int getResult() {
        final var playerCharacter = PartyManager.getParty().getLeader();
        int currResult;
        if (this.result != BattleResults.IN_PROGRESS) {
            return this.result;
        }
        if (this.enemy.isAlive() && !playerCharacter.isAlive()) {
            if (!this.playerDidDamage) {
                currResult = BattleResults.ANNIHILATED;
            } else {
                currResult = BattleResults.LOST;
            }
        } else if (!this.enemy.isAlive() && playerCharacter.isAlive()) {
            if (!this.enemyDidDamage) {
                currResult = BattleResults.PERFECT;
            } else {
                currResult = BattleResults.WON;
            }
        } else if (!this.enemy.isAlive() && !playerCharacter.isAlive()) {
            currResult = BattleResults.DRAW;
        } else {
            currResult = BattleResults.IN_PROGRESS;
        }
        return currResult;
    }

    @Override
    public boolean isWaitingForAI() {
        return false;
    }

    @Override
    public final void maintainEffects(final boolean player) {
        if (player) {
            final var playerCharacter = PartyManager.getParty()
                    .getLeader();
            playerCharacter.useEffects();
            playerCharacter.cullInactiveEffects();
        } else {
            this.enemy.useEffects();
            this.enemy.cullInactiveEffects();
        }
    }

    @Override
    public void redrawOneBattleSquare(final int x, final int y,
            final AbstractMazeObject obj3) {
        // Do nothing
    }

    @Override
    public void resetGUI() {
        // Destroy old GUI
        this.battleGUI.getOutputFrame().dispose();
        // Create new GUI
        this.battleGUI = new WindowTurnBattleGUI();
    }

    @Override
    public final void setResult(final int newResult) {
        this.result = newResult;
    }

    @Override
    public final void setStatusMessage(final String s) {
        this.battleGUI.setStatusMessage(s);
    }

    @Override
    public final boolean steal() {
        final var playerCharacter = PartyManager.getParty().getLeader();
        final var stealChance = StatConstants.CHANCE_STEAL;
        final var chance = new RandomRange(0, 100);
        final var randomChance = chance.generate();
        if (randomChance <= stealChance) {
            // Succeeded
            final var stole = new RandomRange(0, this.enemy.getGold());
            this.stealAmount = stole.generate();
            playerCharacter.offsetGold(this.stealAmount);
            return true;
        } else {
            // Failed
            this.stealAmount = 0;
            return false;
        }
    }

    final void stripExtraNewLine() {
        this.battleGUI.stripExtraNewLine();
    }

    final void updateMessageAreaDrainFailed() {
        this.setStatusMessage(
                "You try to drain the enemy's MP, but the attempt fails!");
    }

    final void updateMessageAreaEnemyFleeFailed() {
        this.setStatusMessage(
                "The enemy tries to run away, but doesn't quite make it!");
    }

    final void updateMessageAreaFleeFailed() {
        this.setStatusMessage("You try to run away, but don't quite make it!");
    }

    final void updateMessageAreaPostDrain() {
        this.setStatusMessage("You try to drain the enemy, and succeed!");
    }

    final void updateMessageAreaPostSteal() {
        this.setStatusMessage("You try to steal money, and successfully steal "
                + this.stealAmount + " Gold!");
    }

    final void updateMessageAreaStealFailed() {
        this.setStatusMessage(
                "You try to steal money from the enemy, but the attempt fails!");
    }

    @Override
    public boolean updatePosition(final int x, final int y) {
        return false;
    }

    @Override
    public boolean useItem() {
        final var playerCharacter = PartyManager.getParty().getLeader();
        return CombatItemChucker.selectAndUseItem(playerCharacter);
    }
}
