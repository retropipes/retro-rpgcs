/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.maze.MazeConstants;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractMazeObject;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractWall;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;

public class EmptyVoid extends AbstractWall {
    // Properties
    private String currAppearance;

    // Constructors
    public EmptyVoid() {
        this.currAppearance = "Void";
    }

    @Override
    public void determineCurrentAppearance(final int x, final int y,
            final int z) {
        final var app = RetroRPGCS.getInstance();
        String mo1Name, mo2Name, mo3Name, mo4Name, mo6Name, mo7Name, mo8Name,
                mo9Name, thisName;
        thisName = this.getName();
        final var mo1 = app.getMazeManager().getMazeObject(x - 1,
                y - 1, z, MazeConstants.LAYER_OBJECT);
        try {
            mo1Name = mo1.getName();
        } catch (final NullPointerException np) {
            mo1Name = thisName;
        }
        final var mo2 = app.getMazeManager().getMazeObject(x - 1,
                y, z, MazeConstants.LAYER_OBJECT);
        try {
            mo2Name = mo2.getName();
        } catch (final NullPointerException np) {
            mo2Name = thisName;
        }
        final var mo3 = app.getMazeManager().getMazeObject(x - 1,
                y + 1, z, MazeConstants.LAYER_OBJECT);
        try {
            mo3Name = mo3.getName();
        } catch (final NullPointerException np) {
            mo3Name = thisName;
        }
        final var mo4 = app.getMazeManager().getMazeObject(x,
                y - 1, z, MazeConstants.LAYER_OBJECT);
        try {
            mo4Name = mo4.getName();
        } catch (final NullPointerException np) {
            mo4Name = thisName;
        }
        final var mo6 = app.getMazeManager().getMazeObject(x,
                y + 1, z, MazeConstants.LAYER_OBJECT);
        try {
            mo6Name = mo6.getName();
        } catch (final NullPointerException np) {
            mo6Name = thisName;
        }
        final var mo7 = app.getMazeManager().getMazeObject(x + 1,
                y - 1, z, MazeConstants.LAYER_OBJECT);
        try {
            mo7Name = mo7.getName();
        } catch (final NullPointerException np) {
            mo7Name = thisName;
        }
        final var mo8 = app.getMazeManager().getMazeObject(x + 1,
                y, z, MazeConstants.LAYER_OBJECT);
        try {
            mo8Name = mo8.getName();
        } catch (final NullPointerException np) {
            mo8Name = thisName;
        }
        final var mo9 = app.getMazeManager().getMazeObject(x + 1,
                y + 1, z, MazeConstants.LAYER_OBJECT);
        try {
            mo9Name = mo9.getName();
        } catch (final NullPointerException np) {
            mo9Name = thisName;
        }
        if (!thisName.equals(mo1Name) || !thisName.equals(mo2Name)
                || !thisName.equals(mo3Name) || !thisName.equals(mo4Name)
                || !thisName.equals(mo6Name) || !thisName.equals(mo7Name)
                || !thisName.equals(mo8Name) || !thisName.equals(mo9Name)) {
            this.currAppearance = "Sealing Wall";
        } else {
            this.currAppearance = "Void";
        }
    }

    @Override
    public AbstractMazeObject gameRenderHook(final int x, final int y,
            final int z) {
        this.determineCurrentAppearance(x, y, z);
        if (this.currAppearance.equals(this.getName())) {
            return this;
        } else {
            return new SealingWall();
        }
    }

    @Override
    public int getBaseID() {
        return ObjectImageConstants.OBJECT_IMAGE_VOID;
    }

    @Override
    public String getDescription() {
        return "The Void surrounds the maze, and cannot be altered in any way.";
    }

    @Override
    public String getGameName() {
        return this.currAppearance;
    }

    @Override
    public String getName() {
        return "Void";
    }

    @Override
    public String getPluralName() {
        return "Voids";
    }
}
