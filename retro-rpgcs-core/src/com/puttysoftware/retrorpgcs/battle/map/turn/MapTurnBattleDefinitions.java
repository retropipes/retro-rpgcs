/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.battle.map.turn;

import com.puttysoftware.retrorpgcs.ai.map.MapAIContext;
import com.puttysoftware.retrorpgcs.maze.Maze;
import com.puttysoftware.retrorpgcs.maze.objects.BattleCharacter;

public class MapTurnBattleDefinitions {
    private static final int MAX_BATTLERS = 100;
    // Fields
    private BattleCharacter activeCharacter;
    private final BattleCharacter[] battlers;
    private final MapAIContext[] aiContexts;
    private Maze battleMaze;
    private int battlerCount;

    // Constructors
    public MapTurnBattleDefinitions() {
        this.battlers = new BattleCharacter[MapTurnBattleDefinitions.MAX_BATTLERS];
        this.aiContexts = new MapAIContext[MapTurnBattleDefinitions.MAX_BATTLERS];
        this.battlerCount = 0;
    }

    public boolean addBattler(final BattleCharacter battler) {
        if (this.battlerCount < MapTurnBattleDefinitions.MAX_BATTLERS) {
            this.battlers[this.battlerCount] = battler;
            this.battlerCount++;
            return true;
        } else {
            return false;
        }
    }

    public int findBattler(final String name) {
        return this.findBattler(name, 0, this.battlers.length);
    }

    private int findBattler(final String name, final int start,
            final int limit) {
        for (var x = start; x < limit; x++) {
            if ((this.battlers[x] != null)
                    && this.battlers[x].getName().equals(name)) {
                return x;
            }
        }
        return -1;
    }

    public int findFirstBattlerOnTeam(final int teamID) {
        return this.findFirstBattlerOnTeam(teamID, 0, this.battlers.length);
    }

    private int findFirstBattlerOnTeam(final int teamID, final int start,
            final int limit) {
        for (var x = start; x < limit; x++) {
            if ((this.battlers[x] != null)
                    && (this.battlers[x].getTeamID() == teamID)) {
                return x;
            }
        }
        return -1;
    }

    public BattleCharacter getActiveCharacter() {
        return this.activeCharacter;
    }

    public Maze getBattleMaze() {
        return this.battleMaze;
    }

    public MapAIContext[] getBattlerAIContexts() {
        return this.aiContexts;
    }

    // Methods
    public BattleCharacter[] getBattlers() {
        return this.battlers;
    }

    public void resetBattlers() {
        for (final BattleCharacter battler : this.battlers) {
            if ((battler != null) && battler.getTemplate().isAlive()) {
                battler.activate();
                battler.resetAP();
                battler.resetAttacks();
                battler.resetSpells();
                battler.resetLocation();
            }
        }
    }

    public void roundResetBattlers() {
        for (final BattleCharacter battler : this.battlers) {
            if ((battler != null) && battler.getTemplate().isAlive()) {
                battler.resetAP();
                battler.resetAttacks();
                battler.resetSpells();
            }
        }
    }

    public void setActiveCharacter(final BattleCharacter bc) {
        this.activeCharacter = bc;
    }

    public void setBattleMaze(final Maze bMaze) {
        this.battleMaze = bMaze;
    }
}
