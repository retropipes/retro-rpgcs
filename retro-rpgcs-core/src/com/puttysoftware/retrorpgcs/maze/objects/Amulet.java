/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.game.GameLogicManager;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractTrap;
import com.puttysoftware.retrorpgcs.maze.effects.MazeEffectConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;

public class Amulet extends AbstractTrap {
    // Constructors
    public Amulet() {
        super(ObjectImageConstants.OBJECT_IMAGE_AMULET);
    }

    @Override
    public String getDescription() {
        return "Amulets make you not slide on ice for 15 steps when stepped on.";
    }

    @Override
    public String getName() {
        return "Amulet";
    }

    @Override
    public String getPluralName() {
        return "Amulets";
    }

    @Override
    public void postMoveAction(final boolean ie, final int dirX,
            final int dirY) {
        RetroRPGCS.getInstance().showMessage("You no longer slide on ice!");
        final var glm = RetroRPGCS.getInstance().getGameManager();
        glm.activateEffect(MazeEffectConstants.EFFECT_STICKY);
        SoundManager.playSound(SoundConstants.SOUND_GRAB);
        GameLogicManager.decay();
    }
}