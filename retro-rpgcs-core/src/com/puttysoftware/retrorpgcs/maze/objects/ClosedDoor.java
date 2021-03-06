/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.game.GameLogicManager;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractTrigger;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;

public class ClosedDoor extends AbstractTrigger {
    // Constructors
    public ClosedDoor() {
    }

    @Override
    public int getBaseID() {
        return ObjectImageConstants.OBJECT_IMAGE_CLOSED_DOOR;
    }

    @Override
    public String getDescription() {
        return "Closed Doors open when stepped on.";
    }

    // Scriptability
    @Override
    public String getName() {
        return "Closed Door";
    }

    @Override
    public String getPluralName() {
        return "Closed Doors";
    }

    @Override
    public void postMoveAction(final boolean ie, final int dirX,
            final int dirY) {
        SoundManager.playSound(SoundConstants.SOUND_PICK_LOCK);
        final var glm = RetroRPGCS.getInstance().getGameManager();
        GameLogicManager.morph(new OpenDoor());
        glm.redrawMaze();
    }
}
