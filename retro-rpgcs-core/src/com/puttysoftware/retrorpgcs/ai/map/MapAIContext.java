/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.ai.map;

import java.awt.Point;

import com.puttysoftware.retrorpgcs.maze.Maze;
import com.puttysoftware.retrorpgcs.maze.MazeConstants;
import com.puttysoftware.retrorpgcs.maze.objects.BattleCharacter;

public class MapAIContext {
    private static final int MINIMUM_RADIUS = 1;
    private static final int MAXIMUM_RADIUS = 16;
    private static final int NOTHING_THERE = -1;
    private static final int CANNOT_MOVE_THERE = -1;
    private static final int AP_COST = 1;

    // Static method
    public static int getAPCost() {
        return MapAIContext.AP_COST;
    }

    private final BattleCharacter aiContext;
    private final int myTeam;
    private final int[][] apCosts;
    private final int[][] creatureLocations;

    // Constructor
    public MapAIContext(final BattleCharacter context, final Maze arena) {
        this.aiContext = context;
        this.myTeam = context.getTeamID();
        this.apCosts = new int[arena.getRows()][arena.getColumns()];
        this.creatureLocations = new int[arena.getRows()][arena.getColumns()];
    }

    public BattleCharacter getCharacter() {
        return this.aiContext;
    }

    Point isEnemyNearby() {
        return this.isEnemyNearby(1, 1);
    }

    Point isEnemyNearby(final int minRadius, final int maxRadius) {
        var fMinR = minRadius;
        var fMaxR = maxRadius;
        if (fMaxR > MapAIContext.MAXIMUM_RADIUS) {
            fMaxR = MapAIContext.MAXIMUM_RADIUS;
        }
        if (fMaxR < MapAIContext.MINIMUM_RADIUS) {
            fMaxR = MapAIContext.MINIMUM_RADIUS;
        }
        if (fMinR > MapAIContext.MAXIMUM_RADIUS) {
            fMinR = MapAIContext.MAXIMUM_RADIUS;
        }
        if (fMinR < MapAIContext.MINIMUM_RADIUS) {
            fMinR = MapAIContext.MINIMUM_RADIUS;
        }
        final var x = this.aiContext.getX();
        final var y = this.aiContext.getY();
        int u, v;
        for (u = x - fMaxR; u <= x + fMaxR; u++) {
            for (v = y - fMaxR; v <= y + fMaxR; v++) {
                if (Math.abs(u - x) < fMinR && Math.abs(v - y) < fMinR) {
                    continue;
                }
                try {
                    if (this.creatureLocations[u][v] != -1
                            && this.creatureLocations[u][v] != this.myTeam) {
                        return new Point(u - x, v - y);
                    }
                } catch (final ArrayIndexOutOfBoundsException aioob) {
                    // Ignore
                }
            }
        }
        return null;
    }

    Point runAway() {
        final var fMinR = MapAIContext.MAXIMUM_RADIUS;
        final var fMaxR = MapAIContext.MAXIMUM_RADIUS;
        final var x = this.aiContext.getX();
        final var y = this.aiContext.getY();
        int u, v;
        for (u = x - fMaxR; u <= x + fMaxR; u++) {
            for (v = y - fMaxR; v <= y + fMaxR; v++) {
                if (Math.abs(u - x) < fMinR && Math.abs(v - y) < fMinR) {
                    continue;
                }
                try {
                    if (this.creatureLocations[u][v] != -1
                            && this.creatureLocations[u][v] != this.myTeam) {
                        return new Point(u + x, v + y);
                    }
                } catch (final ArrayIndexOutOfBoundsException aioob) {
                    // Ignore
                }
            }
        }
        return null;
    }

    // Methods
    public void updateContext(final Maze arena) {
        for (var x = 0; x < this.apCosts.length; x++) {
            for (var y = 0; y < this.apCosts[x].length; y++) {
                final var obj = arena.getCell(x, y, 0,
                        MazeConstants.LAYER_OBJECT);
                if (obj.isSolid()) {
                    this.apCosts[x][y] = MapAIContext.CANNOT_MOVE_THERE;
                } else {
                    this.apCosts[x][y] = MapAIContext.AP_COST;
                }
            }
        }
        for (var x = 0; x < this.creatureLocations.length; x++) {
            for (var y = 0; y < this.creatureLocations[x].length; y++) {
                final var obj = arena.getCell(x, y, 0,
                        MazeConstants.LAYER_OBJECT);
                if (obj instanceof BattleCharacter) {
                    final var bc = (BattleCharacter) obj;
                    this.creatureLocations[x][y] = bc.getTeamID();
                } else {
                    this.creatureLocations[x][y] = MapAIContext.NOTHING_THERE;
                }
            }
        }
    }
}
