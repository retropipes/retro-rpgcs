/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.abc;

import java.util.Objects;

import com.puttysoftware.images.BufferedImageIcon;
import com.puttysoftware.retrorpgcs.creatures.Creature;
import com.puttysoftware.retrorpgcs.creatures.StatConstants;
import com.puttysoftware.retrorpgcs.maze.MazeConstants;
import com.puttysoftware.retrorpgcs.maze.objects.Empty;
import com.puttysoftware.retrorpgcs.resourcemanagers.ObjectImageConstants;

public abstract class AbstractBattleCharacter extends AbstractMazeObject {
    // Fields
    private final Creature template;
    private int actionCounter;
    private int attackCounter;
    private int spellCounter;
    private boolean isActive;

    // Constructors
    protected AbstractBattleCharacter(final Creature newTemplate) {
        super(true, false);
        this.template = newTemplate;
        this.actionCounter = newTemplate.getMapBattleActionsPerRound();
        this.attackCounter = (int) newTemplate
                .getEffectedStat(StatConstants.STAT_ATTACKS_PER_ROUND);
        this.spellCounter = (int) newTemplate
                .getEffectedStat(StatConstants.STAT_SPELLS_PER_ROUND);
        this.isActive = true;
        this.setSavedObject(new Empty());
    }

    public final void activate() {
        this.isActive = true;
    }

    @Override
    public BufferedImageIcon battleRenderHook() {
        return this.template.getImage();
    }

    public final void deactivate() {
        this.isActive = false;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj) || !(obj instanceof AbstractBattleCharacter)) {
            return false;
        }
        final var other = (AbstractBattleCharacter) obj;
        if (this.actionCounter != other.actionCounter) {
            return false;
        }
        if (this.attackCounter != other.attackCounter) {
            return false;
        }
        if (this.isActive != other.isActive) {
            return false;
        }
        if (this.spellCounter != other.spellCounter) {
            return false;
        }
        if (!Objects.equals(this.template, other.template)) {
            return false;
        }
        return true;
    }

    public final String getAPString() {
        return "Moves Left: "
                + (this.actionCounter >= 0 ? this.actionCounter : 0);
    }

    public final String getAttackString() {
        return "Attacks Left: "
                + (this.attackCounter >= 0 ? this.attackCounter : 0);
    }

    @Override
    public int getBaseID() {
        return ObjectImageConstants.OBJECT_IMAGE_NONE;
    }

    public final int getCurrentAP() {
        return this.actionCounter;
    }

    public final int getCurrentAT() {
        return this.attackCounter;
    }

    public final int getCurrentSP() {
        return this.spellCounter;
    }

    @Override
    public int getCustomFormat() {
        return 2;
    }

    @Override
    public int getCustomProperty(final int propID) {
        switch (propID) {
        case 0:
            return this.getX();
        case 1:
            return this.getY();
        default:
            return AbstractMazeObject.DEFAULT_CUSTOM_VALUE;
        }
    }

    @Override
    public String getDescription() {
        // Description isn't used for battle characters
        return "";
    }

    @Override
    public int getLayer() {
        return MazeConstants.LAYER_OBJECT;
    }

    @Override
    public String getName() {
        return this.template.getName();
    }

    @Override
    public String getPluralName() {
        // Plural name isn't used for battle characters
        return "";
    }

    public final String getSpellString() {
        return "Spells Left: "
                + (this.spellCounter >= 0 ? this.spellCounter : 0);
    }

    public final int getTeamID() {
        return this.template.getTeamID();
    }

    public final String getTeamString() {
        if (this.getTemplate().getTeamID() == 0) {
            return "Team: Party";
        } else {
            return "Team: Enemies " + this.getTemplate().getTeamID();
        }
    }

    public final Creature getTemplate() {
        return this.template;
    }

    public final int getX() {
        return this.template.getX();
    }

    public final int getY() {
        return this.template.getY();
    }

    @Override
    public int hashCode() {
        final var prime = 31;
        var result = super.hashCode();
        result = prime * result + this.actionCounter;
        result = prime * result + this.attackCounter;
        result = prime * result + (this.isActive ? 1231 : 1237);
        result = prime * result + this.spellCounter;
        return prime * result
                + (this.template == null ? 0 : this.template.hashCode());
    }

    public final boolean isActive() {
        return this.isActive;
    }

    public final void modifyAP(final int mod) {
        this.actionCounter -= mod;
    }

    public final void modifyAttacks(final int mod) {
        this.attackCounter -= mod;
    }

    public final void modifySpells(final int mod) {
        this.spellCounter -= mod;
    }

    public final void offsetX(final int newX) {
        this.template.offsetX(newX);
    }

    public final void offsetY(final int newY) {
        this.template.offsetY(newY);
    }

    @Override
    public void postMoveAction(final boolean ie, final int dirX,
            final int dirY) {
        // Do nothing
    }

    public final void resetAll() {
        this.resetAP();
        this.resetAttacks();
        this.resetSpells();
    }

    public final void resetAP() {
        this.actionCounter = this.template.getMapBattleActionsPerRound();
    }

    public final void resetAttacks() {
        this.attackCounter = (int) this.template
                .getEffectedStat(StatConstants.STAT_ATTACKS_PER_ROUND);
    }

    public final void resetLocation() {
        this.template.setX(-1);
        this.template.setY(-1);
    }

    public final void resetSpells() {
        this.spellCounter = (int) this.template
                .getEffectedStat(StatConstants.STAT_SPELLS_PER_ROUND);
    }

    public final void restoreLocation() {
        this.template.restoreLocation();
    }

    public final void saveLocation() {
        this.template.saveLocation();
    }

    @Override
    public void setCustomProperty(final int propID, final int value) {
        switch (propID) {
        case 0:
            this.setX(value);
            break;
        case 1:
            this.setY(value);
            break;
        default:
            break;
        }
    }

    @Override
    protected void setTypes() {
        // Do nothing
    }

    public final void setX(final int newX) {
        this.template.setX(newX);
    }

    public final void setY(final int newY) {
        this.template.setY(newY);
    }
}
