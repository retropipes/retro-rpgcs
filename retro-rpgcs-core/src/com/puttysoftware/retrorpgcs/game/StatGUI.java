/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.game;

import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.puttysoftware.retrorpgcs.creatures.party.PartyManager;
import com.puttysoftware.retrorpgcs.resourcemanagers.StatImageConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.StatImageManager;

class StatGUI {
    // Fields
    private Container statsPane;
    private JLabel hpLabel;
    private JLabel mpLabel;
    private JLabel goldLabel;
    private JLabel attackLabel;
    private JLabel defenseLabel;
    private JLabel xpLabel;
    private JLabel levelLabel;

    // Constructors
    StatGUI() {
        this.setUpGUI();
    }

    // Methods
    Container getStatsPane() {
        return this.statsPane;
    }

    private void setUpGUI() {
        this.statsPane = new Container();
        this.statsPane.setLayout(new GridLayout(7, 1));
        this.hpLabel = new JLabel("", null, SwingConstants.LEFT);
        this.mpLabel = new JLabel("", null, SwingConstants.LEFT);
        this.goldLabel = new JLabel("", null, SwingConstants.LEFT);
        this.attackLabel = new JLabel("", null, SwingConstants.LEFT);
        this.defenseLabel = new JLabel("", null, SwingConstants.LEFT);
        this.xpLabel = new JLabel("", null, SwingConstants.LEFT);
        this.levelLabel = new JLabel("", null, SwingConstants.LEFT);
        this.statsPane.add(this.hpLabel);
        this.statsPane.add(this.mpLabel);
        this.statsPane.add(this.goldLabel);
        this.statsPane.add(this.attackLabel);
        this.statsPane.add(this.defenseLabel);
        this.statsPane.add(this.xpLabel);
        this.statsPane.add(this.levelLabel);
    }

    void updateImages() {
        final var hpImage = StatImageManager
                .getImage(StatImageConstants.STAT_IMAGE_HEALTH);
        this.hpLabel.setIcon(hpImage);
        final var mpImage = StatImageManager
                .getImage(StatImageConstants.STAT_IMAGE_MAGIC);
        this.mpLabel.setIcon(mpImage);
        final var goldImage = StatImageManager
                .getImage(StatImageConstants.STAT_IMAGE_GOLD);
        this.goldLabel.setIcon(goldImage);
        final var attackImage = StatImageManager
                .getImage(StatImageConstants.STAT_IMAGE_ATTACK);
        this.attackLabel.setIcon(attackImage);
        final var defenseImage = StatImageManager
                .getImage(StatImageConstants.STAT_IMAGE_DEFENSE);
        this.defenseLabel.setIcon(defenseImage);
        final var xpImage = StatImageManager
                .getImage(StatImageConstants.STAT_IMAGE_XP);
        this.xpLabel.setIcon(xpImage);
        final var levelImage = StatImageManager
                .getImage(StatImageConstants.STAT_IMAGE_LEVEL);
        this.levelLabel.setIcon(levelImage);
    }

    void updateStats() {
        final var party = PartyManager.getParty();
        if (party != null) {
            final var pc = party.getLeader();
            if (pc != null) {
                this.hpLabel.setText(pc.getHPString());
                this.mpLabel.setText(pc.getMPString());
                this.goldLabel.setText(Integer.toString(pc.getGold()));
                this.attackLabel.setText(Integer.toString(pc.getAttack()));
                this.defenseLabel.setText(Integer.toString(pc.getDefense()));
                this.xpLabel.setText(pc.getXPString());
                this.levelLabel.setText(party.getTowerLevelString());
            }
        }
    }
}
