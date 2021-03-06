/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.randomrange.RandomRange;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractMovingObject;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;

public class Monster extends AbstractMovingObject {
    // Constructors
    public Monster() {
        super(false);
        this.setSavedObject(new Empty());
        this.activateTimer(1);
    }

    @Override
    public int getBaseID() {
        return ObjectImageConstants.OBJECT_IMAGE_MONSTER;
    }

    @Override
    public String getDescription() {
        return "Monsters are dangerous. Encountering one starts a battle.";
    }

    @Override
    public String getName() {
        return "Monster";
    }

    @Override
    public String getPluralName() {
        return "Monsters";
    }

    @Override
    public void postMoveAction(final boolean ie, final int dirX,
            final int dirY) {
        if (RetroRPGCS.getInstance().getMode() != RetroRPGCS.STATUS_BATTLE) {
            RetroRPGCS.getInstance().getBattle().doBattle();
            RetroRPGCS.getInstance().getMazeManager().getMaze().postBattle(this,
                    dirX, dirY, true);
        }
    }

    @Override
    public void timerExpiredAction(final int dirX, final int dirY) {
        // Move the monster
        final var r = new RandomRange(0, 7);
        final var move = r.generate();
        RetroRPGCS.getInstance().getMazeManager().getMaze()
                .updateMonsterPosition(move, dirX, dirY, this);
        this.activateTimer(1);
    }
}
