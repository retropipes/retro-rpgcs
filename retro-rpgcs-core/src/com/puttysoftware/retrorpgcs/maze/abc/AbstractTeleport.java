/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.abc;

import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.maze.MazeConstants;
import com.puttysoftware.retrorpgcs.maze.utilities.TypeConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;

public abstract class AbstractTeleport extends AbstractMazeObject {
    // Fields
    private int destRow;
    private int destCol;
    private int destFloor;

    // Constructors
    protected AbstractTeleport(final int destinationRow,
            final int destinationColumn, final int destinationFloor) {
        super(false, true, false);
        this.destRow = destinationRow;
        this.destCol = destinationColumn;
        this.destFloor = destinationFloor;
    }

    @Override
    public AbstractTeleport clone() {
        final var copy = (AbstractTeleport) super.clone();
        copy.destCol = this.destCol;
        copy.destFloor = this.destFloor;
        copy.destRow = this.destRow;
        return copy;
    }

    @Override
    public boolean defersSetProperties() {
        return true;
    }

    @Override
    public boolean equals(final Object obj) {
        if ((obj == null) || (this.getClass() != obj.getClass())) {
            return false;
        }
        final var other = (AbstractTeleport) obj;
        if (this.destRow != other.destRow) {
            return false;
        }
        if (this.destCol != other.destCol) {
            return false;
        }
        if (this.destFloor != other.destFloor) {
            return false;
        }
        return true;
    }

    @Override
    public int getCustomFormat() {
        return 3;
    }

    @Override
    public int getCustomProperty(final int propID) {
        switch (propID) {
        case 1:
            return this.destRow;
        case 2:
            return this.destCol;
        case 3:
            return this.destFloor;
        default:
            return AbstractMazeObject.DEFAULT_CUSTOM_VALUE;
        }
    }

    public int getDestinationColumn() {
        return this.destCol;
    }

    public int getDestinationFloor() {
        return this.destFloor;
    }

    // Accessor methods
    public int getDestinationRow() {
        return this.destRow;
    }

    @Override
    public int getLayer() {
        return MazeConstants.LAYER_OBJECT;
    }

    @Override
    public abstract String getName();

    @Override
    public int hashCode() {
        var hash = 7;
        hash = 67 * hash + this.destRow;
        hash = 67 * hash + this.destCol;
        return 67 * hash + this.destFloor;
    }

    // Scriptability
    @Override
    public void postMoveAction(final boolean ie, final int dirX,
            final int dirY) {
        final var app = RetroRPGCS.getInstance();
        app.getGameManager().updatePositionAbsolute(this.getDestinationRow(),
                this.getDestinationColumn(), this.getDestinationFloor());
        SoundManager.playSound(SoundConstants.SOUND_TELEPORT);
    }

    @Override
    public void setCustomProperty(final int propID, final int value) {
        switch (propID) {
        case 1:
            this.destRow = value;
            break;
        case 2:
            this.destCol = value;
            break;
        case 3:
            this.destFloor = value;
            break;
        default:
            break;
        }
    }

    @Override
    protected void setTypes() {
        this.type.set(TypeConstants.TYPE_TELEPORT);
    }
}
