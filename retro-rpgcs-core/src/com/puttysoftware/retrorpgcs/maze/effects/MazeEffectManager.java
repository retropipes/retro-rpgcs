/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.effects;

import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JLabel;

import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.maze.utilities.DirectionResolver;
import com.puttysoftware.retrorpgcs.prefs.PreferencesManager;

public class MazeEffectManager {
    private static final int NUM_EFFECTS = 9;
    private static final int MAX_ACTIVE_EFFECTS = 3;
    // Fields
    private final MazeEffect[] activeEffects;
    private final Container activeEffectMessageContainer;
    private final JLabel[] activeEffectMessages;
    private int newEffectIndex;
    private final int[] activeEffectIndices;

    // Constructors
    public MazeEffectManager() {
        // Create effects array
        this.activeEffects = new MazeEffect[MazeEffectManager.NUM_EFFECTS];
        this.activeEffects[MazeEffectConstants.EFFECT_ROTATED_CLOCKWISE] = new RotatedCW(
                0);
        this.activeEffects[MazeEffectConstants.EFFECT_ROTATED_COUNTERCLOCKWISE] = new RotatedCCW(
                0);
        this.activeEffects[MazeEffectConstants.EFFECT_U_TURNED] = new UTurned(
                0);
        this.activeEffects[MazeEffectConstants.EFFECT_CONFUSED] = new Confused(
                0);
        this.activeEffects[MazeEffectConstants.EFFECT_DIZZY] = new Dizzy(0);
        this.activeEffects[MazeEffectConstants.EFFECT_DRUNK] = new Drunk(0);
        this.activeEffects[MazeEffectConstants.EFFECT_STICKY] = new Sticky(0);
        this.activeEffects[MazeEffectConstants.EFFECT_POWER_GATHER] = new PowerGather(
                0);
        this.activeEffects[MazeEffectConstants.EFFECT_POWER_WITHER] = new PowerWither(
                0);
        // Create GUI
        this.activeEffectMessageContainer = new Container();
        this.activeEffectMessages = new JLabel[MazeEffectManager.MAX_ACTIVE_EFFECTS];
        this.activeEffectMessageContainer.setLayout(
                new GridLayout(MazeEffectManager.MAX_ACTIVE_EFFECTS, 1));
        for (var z = 0; z < MazeEffectManager.MAX_ACTIVE_EFFECTS; z++) {
            this.activeEffectMessages[z] = new JLabel("");
            this.activeEffectMessageContainer.add(this.activeEffectMessages[z]);
        }
        // Set up miscellaneous things
        this.activeEffectIndices = new int[MazeEffectManager.MAX_ACTIVE_EFFECTS];
        for (var z = 0; z < MazeEffectManager.MAX_ACTIVE_EFFECTS; z++) {
            this.activeEffectIndices[z] = -1;
        }
        this.newEffectIndex = -1;
    }

    public void activateEffect(final int effectID) {
        this.activateEffectInternal(effectID,
                MazeEffectConstants.DURATIONS[PreferencesManager
                        .getGameDifficulty()][effectID]);
    }

    private void activateEffectInternal(final int effectID,
            final int duration) {
        this.handleMutualExclusiveEffects(effectID);
        final var active = this.activeEffects[effectID].isActive();
        this.activeEffects[effectID].extendEffect(duration);
        // Update effect grid
        if (active) {
            this.updateGridEntry(effectID);
        } else {
            this.addGridEntry(effectID);
        }
        // Keep effect message
        RetroRPGCS.getInstance().getGameManager().keepNextMessage();
    }

    private void addGridEntry(final int effectID) {
        if (this.newEffectIndex < MazeEffectManager.MAX_ACTIVE_EFFECTS - 1) {
            this.newEffectIndex++;
            this.activeEffectIndices[this.newEffectIndex] = effectID;
            final var effectString = this.activeEffects[effectID]
                    .getEffectString();
            this.activeEffectMessages[this.newEffectIndex]
                    .setText(effectString);
        }
    }

    private void clearGridEntry(final int effectID) {
        final var index = this.lookupEffect(effectID);
        if (index != -1) {
            this.clearGridEntryText(index);
            // Compact grid
            for (var z = index; z < MazeEffectManager.MAX_ACTIVE_EFFECTS
                    - 1; z++) {
                this.activeEffectMessages[z]
                        .setText(this.activeEffectMessages[z + 1].getText());
                this.activeEffectIndices[z] = this.activeEffectIndices[z + 1];
            }
            // Clear last entry
            this.clearGridEntryText(MazeEffectManager.MAX_ACTIVE_EFFECTS - 1);
            this.newEffectIndex--;
        }
    }

    private void clearGridEntryText(final int index) {
        this.activeEffectIndices[index] = -1;
        this.activeEffectMessages[index].setText("");
    }

    public void deactivateAllEffects() {
        for (var effectID = 0; effectID < MazeEffectManager.NUM_EFFECTS; effectID++) {
            if (this.activeEffects[effectID].isActive()) {
                this.activeEffects[effectID].deactivateEffect();
                this.clearGridEntry(effectID);
            }
        }
    }

    public void deactivateEffect(final int effectID) {
        if (this.activeEffects[effectID].isActive()) {
            this.activeEffects[effectID].deactivateEffect();
            this.clearGridEntry(effectID);
        }
    }

    public void decayEffects() {
        for (var x = 0; x < MazeEffectManager.NUM_EFFECTS; x++) {
            if (this.activeEffects[x].isActive()) {
                this.activeEffects[x].useEffect();
                // Update effect grid
                this.updateGridEntry(x);
                if (!this.activeEffects[x].isActive()) {
                    RetroRPGCS.getInstance()
                            .showMessage("You feel normal again.");
                    // Clear effect grid
                    this.clearGridEntry(x);
                    // Pack
                    RetroRPGCS.getInstance().getGameManager().getOutputFrame()
                            .pack();
                }
            }
        }
    }

    public int[] doEffects(final int x, final int y) {
        var res = new int[] { x, y };
        var dir = DirectionResolver.resolveRelativeDirection(x, y);
        for (var z = 0; z < MazeEffectManager.NUM_EFFECTS; z++) {
            if (this.activeEffects[z].isActive()) {
                dir = this.activeEffects[z].modifyMove1(dir);
                res = DirectionResolver.unresolveRelativeDirection(dir);
                res = this.activeEffects[z].modifyMove2(res);
            }
        }
        return res;
    }

    // Methods
    public Container getEffectMessageContainer() {
        return this.activeEffectMessageContainer;
    }

    private void handleMutualExclusiveEffects(final int effectID) {
        switch (effectID) {
        case MazeEffectConstants.EFFECT_ROTATED_CLOCKWISE:
            this.deactivateEffect(
                    MazeEffectConstants.EFFECT_ROTATED_COUNTERCLOCKWISE);
            this.deactivateEffect(MazeEffectConstants.EFFECT_U_TURNED);
            break;
        case MazeEffectConstants.EFFECT_ROTATED_COUNTERCLOCKWISE:
            this.deactivateEffect(MazeEffectConstants.EFFECT_ROTATED_CLOCKWISE);
            this.deactivateEffect(MazeEffectConstants.EFFECT_U_TURNED);
            break;
        case MazeEffectConstants.EFFECT_U_TURNED:
            this.deactivateEffect(MazeEffectConstants.EFFECT_ROTATED_CLOCKWISE);
            this.deactivateEffect(
                    MazeEffectConstants.EFFECT_ROTATED_COUNTERCLOCKWISE);
            break;
        case MazeEffectConstants.EFFECT_CONFUSED:
            this.deactivateEffect(MazeEffectConstants.EFFECT_DIZZY);
            this.deactivateEffect(MazeEffectConstants.EFFECT_DRUNK);
            break;
        case MazeEffectConstants.EFFECT_DIZZY:
            this.deactivateEffect(MazeEffectConstants.EFFECT_CONFUSED);
            this.deactivateEffect(MazeEffectConstants.EFFECT_DRUNK);
            break;
        case MazeEffectConstants.EFFECT_DRUNK:
            this.deactivateEffect(MazeEffectConstants.EFFECT_CONFUSED);
            this.deactivateEffect(MazeEffectConstants.EFFECT_DIZZY);
            break;
        case MazeEffectConstants.EFFECT_STICKY:
            this.deactivateEffect(MazeEffectConstants.EFFECT_POWER_GATHER);
            this.deactivateEffect(MazeEffectConstants.EFFECT_POWER_WITHER);
            break;
        case MazeEffectConstants.EFFECT_POWER_GATHER:
            this.deactivateEffect(MazeEffectConstants.EFFECT_STICKY);
            this.deactivateEffect(MazeEffectConstants.EFFECT_POWER_WITHER);
            break;
        case MazeEffectConstants.EFFECT_POWER_WITHER:
            this.deactivateEffect(MazeEffectConstants.EFFECT_STICKY);
            this.deactivateEffect(MazeEffectConstants.EFFECT_POWER_GATHER);
            break;
        default:
            break;
        }
    }

    public boolean isEffectActive(final int effectID) {
        return this.activeEffects[effectID].isActive();
    }

    private int lookupEffect(final int effectID) {
        for (var z = 0; z < MazeEffectManager.MAX_ACTIVE_EFFECTS; z++) {
            if (this.activeEffectIndices[z] == effectID) {
                return z;
            }
        }
        return -1;
    }

    private void updateGridEntry(final int effectID) {
        final var index = this.lookupEffect(effectID);
        if (index != -1) {
            final var effectString = this.activeEffects[effectID]
                    .getEffectString();
            this.activeEffectMessages[index].setText(effectString);
        }
    }
}