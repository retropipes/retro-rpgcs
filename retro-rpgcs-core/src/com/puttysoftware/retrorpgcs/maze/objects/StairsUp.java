/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.maze.Maze;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractMazeObject;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractTeleport;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;

public class StairsUp extends AbstractTeleport {
    // Constructors
    public StairsUp() {
        super(0, 0, 0);
    }

    @Override
    public int getBaseID() {
        return ObjectImageConstants.OBJECT_IMAGE_STAIRS_UP;
    }

    @Override
    public int getCustomFormat() {
        return 0;
    }

    @Override
    public int getCustomProperty(final int propID) {
        return AbstractMazeObject.DEFAULT_CUSTOM_VALUE;
    }

    @Override
    public String getDescription() {
        return "Stairs Up lead to the level above.";
    }

    @Override
    public String getName() {
        return "Stairs Up";
    }

    @Override
    public String getPluralName() {
        return "Sets of Stairs Up";
    }

    @Override
    public void postMoveAction(final boolean ie, final int dirX,
            final int dirY) {
        final var app = RetroRPGCS.getInstance();
        app.getGameManager().goToLevelOffset(1);
        SoundManager.playSound(SoundConstants.SOUND_UP);
    }

    @Override
    public void setCustomProperty(final int propID, final int value) {
        // Do nothing
    }

    @Override
    public boolean shouldGenerateObject(final Maze maze, final int row,
            final int col, final int floor, final int level, final int layer) {
        if (level < Maze.getMaxLevels() - 1) {
            // Generate Stairs Up at 30% rate
            final var reject = new RandomRange(1, 100);
            return reject.generate() < 30;
        } else {
            // Do not generate Stairs Up on the top
            return false;
        }
    }
}
