/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.game.GameLogicManager;
import com.puttysoftware.retrorpgcs.maze.Maze;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractMPModifier;
import com.puttysoftware.retrorpgcs.maze.effects.MazeEffectConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;

public class LightGem extends AbstractMPModifier {
    // Constructors
    public LightGem() {
    }

    @Override
    public int getBaseID() {
        return ObjectImageConstants.OBJECT_IMAGE_LIGHT_GEM;
    }

    @Override
    public String getDescription() {
        return "Light Gems regenerate MP.";
    }

    @Override
    public String getName() {
        return "Light Gem";
    }

    @Override
    public String getPluralName() {
        return "Light Gems";
    }

    @Override
    public void postMoveAction(final boolean ie, final int dirX,
            final int dirY) {
        RetroRPGCS.getInstance().showMessage("Your power gathers!");
        RetroRPGCS.getInstance().getGameManager()
                .activateEffect(MazeEffectConstants.EFFECT_POWER_GATHER);
        SoundManager.playSound(SoundConstants.SOUND_FOCUS);
        GameLogicManager.decay();
    }

    @Override
    public boolean shouldGenerateObject(final Maze maze, final int row,
            final int col, final int floor, final int level, final int layer) {
        // Generate Light Gems at 30% rate
        final var reject = new RandomRange(1, 100);
        return reject.generate() < 30;
    }
}
