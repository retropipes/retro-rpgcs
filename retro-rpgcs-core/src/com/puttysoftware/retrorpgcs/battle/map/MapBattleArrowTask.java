/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.battle.map;

import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.maze.Maze;
import com.puttysoftware.retrorpgcs.maze.MazeConstants;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractMazeObject;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractTransientObject;
import com.puttysoftware.retrorpgcs.maze.objects.Arrow;
import com.puttysoftware.retrorpgcs.maze.objects.BattleCharacter;
import com.puttysoftware.retrorpgcs.maze.objects.Empty;
import com.puttysoftware.retrorpgcs.maze.objects.Wall;
import com.puttysoftware.retrorpgcs.maze.utilities.DirectionResolver;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;

public class MapBattleArrowTask extends Thread {
    private static AbstractTransientObject createArrow() {
        return new Arrow();
    }

    // Fields
    private final int x, y;
    private final Maze battleMaze;
    private final BattleCharacter active;

    // Constructors
    public MapBattleArrowTask(final int newX, final int newY, final Maze maze,
            final BattleCharacter ac) {
        this.x = newX;
        this.y = newY;
        this.battleMaze = maze;
        this.active = ac;
        this.setName("Arrow Handler");
    }

    @Override
    public void run() {
        try {
            var res = true;
            final var app = RetroRPGCS.getInstance();
            final var m = this.battleMaze;
            final var px = this.active.getX();
            final var py = this.active.getY();
            var cumX = this.x;
            var cumY = this.y;
            final var incX = this.x;
            final var incY = this.y;
            AbstractMazeObject o = null;
            try {
                o = m.getCell(px + cumX, py + cumY, 0,
                        MazeConstants.LAYER_OBJECT);
            } catch (final ArrayIndexOutOfBoundsException ae) {
                o = new Wall();
            }
            final var a = MapBattleArrowTask.createArrow();
            final var newDir = DirectionResolver.resolveRelativeDirection(incX,
                    incY);
            a.setDirection(newDir);
            SoundManager.playSound(SoundConstants.SOUND_ARROW);
            while (res) {
                res = o.arrowHitBattleCheck();
                if (!res) {
                    break;
                }
                // Draw arrow
                app.getBattle().redrawOneBattleSquare(px + cumX, py + cumY, a);
                // Delay, for animation purposes
                Thread.sleep(MapBattleArrowSpeedConstants.getArrowSpeed());
                // Clear arrow
                app.getBattle().redrawOneBattleSquare(px + cumX, py + cumY,
                        new Empty());
                cumX += incX;
                cumY += incY;
                try {
                    o = m.getCell(px + cumX, py + cumY, 0,
                            MazeConstants.LAYER_OBJECT);
                } catch (final ArrayIndexOutOfBoundsException ae) {
                    res = false;
                }
            }
            // Check to see if the arrow hit a creature
            BattleCharacter hit = null;
            if (o instanceof BattleCharacter) {
                // Arrow hit a creature, hurt it
                hit = (BattleCharacter) o;
                final var shooter = this.active.getTemplate().getFaith();
                final var target = hit.getTemplate().getFaith();
                final var mult = (int) (shooter
                        .getMultiplierForOtherFaith(target.getFaithID()) * 10);
                final var bl = app.getBattle();
                switch (mult) {
                case 0:
                    hit.getTemplate().doDamage(1);
                    bl.setStatusMessage(
                            "Ow, you got shot! It didn't hurt much.");
                    break;
                case 5:
                    hit.getTemplate().doDamage(3);
                    bl.setStatusMessage(
                            "Ow, you got shot! It hurt a little bit.");
                    break;
                case 10:
                    hit.getTemplate().doDamage(5);
                    bl.setStatusMessage("Ow, you got shot! It hurt somewhat.");
                    break;
                case 20:
                    hit.getTemplate().doDamage(8);
                    bl.setStatusMessage(
                            "Ow, you got shot! It hurt significantly!");
                    break;
                default:
                    break;
                }
            }
            // Arrow has died
            SoundManager.playSound(SoundConstants.SOUND_ARROW_DIE);
            app.getBattle().arrowDone(hit);
        } catch (final Throwable t) {
            RetroRPGCS.getInstance().handleError(t);
        }
    }
}
