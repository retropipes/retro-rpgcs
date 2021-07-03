/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.creatures.party;

import java.io.IOException;

import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.retrorpgcs.maze.Maze;
import com.puttysoftware.retrorpgcs.maze.objects.BattleCharacter;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;
import com.puttysoftware.xio.XDataReader;
import com.puttysoftware.xio.XDataWriter;

public class Party {
    static Party read(final XDataReader worldFile) throws IOException {
        worldFile.readInt();
        final var lid = worldFile.readInt();
        final var apc = worldFile.readInt();
        final var lvl = worldFile.readInt();
        final var pty = new Party();
        pty.leaderID = lid;
        pty.activePCs = apc;
        pty.towerLevel = lvl;
        final var present = worldFile.readBoolean();
        if (present) {
            pty.members = PartyMember.read(worldFile);
        }
        return pty;
    }

    // Fields
    private PartyMember members;
    private BattleCharacter battlers;
    private int leaderID;
    private int activePCs;
    private int towerLevel;

    // Constructors
    public Party() {
        this.members = null;
        this.leaderID = 0;
        this.activePCs = 0;
        this.towerLevel = 0;
    }

    boolean addPartyMember(final PartyMember member) {
        this.members = member;
        return true;
    }

    public void checkPartyLevelUp() {
        // Level Up Check
        if (this.members.checkLevelUp()) {
            this.members.levelUp();
            SoundManager.playSound(SoundConstants.SOUND_LEVEL_UP);
            CommonDialogs
                    .showTitledDialog(
                            this.members.getName() + " reached level "
                                    + this.members.getLevel() + "!",
                            "Level Up");
        }
    }

    public void fireStepActions() {
        this.members.getItems().fireStepActions(this.members);
    }

    // Methods
    private void generateBattleCharacters() {
        this.battlers = new BattleCharacter(this.members);
    }

    public BattleCharacter getBattleCharacters() {
        if (this.battlers == null) {
            this.generateBattleCharacters();
        }
        return this.battlers;
    }

    public PartyMember getLeader() {
        return this.members;
    }

    public long getPartyMaxToNextLevel() {
        return this.members.getToNextLevelValue();
    }

    public int getTowerLevel() {
        return this.towerLevel;
    }

    public String getTowerLevelString() {
        return "Tower Level: " + (this.towerLevel + 1);
    }

    public boolean isAlive() {
        return this.members.isAlive();
    }

    public void offsetTowerLevel(final int offset) {
        if (this.towerLevel + offset > Maze.getMaxLevels()
                || this.towerLevel + offset < 0) {
            return;
        }
        this.towerLevel += offset;
    }

    void resetTowerLevel() {
        this.towerLevel = 0;
    }

    void write(final XDataWriter worldFile) throws IOException {
        worldFile.writeInt(1);
        worldFile.writeInt(this.leaderID);
        worldFile.writeInt(this.activePCs);
        worldFile.writeInt(this.towerLevel);
        if (this.members == null) {
            worldFile.writeBoolean(false);
        } else {
            worldFile.writeBoolean(true);
            this.members.write(worldFile);
        }
    }
}
