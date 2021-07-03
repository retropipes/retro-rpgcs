/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.battle.map.turn;

import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.puttysoftware.retrorpgcs.maze.objects.BattleCharacter;
import com.puttysoftware.retrorpgcs.resourcemanagers.StatImageConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.StatImageManager;

public class MapTurnBattleStats {
    // Fields
    private Container statsPane;
    private JLabel nameLabel;
    private JLabel teamLabel;
    private JLabel hpLabel;
    private JLabel mpLabel;
    private JLabel attLabel;
    private JLabel defLabel;
    private JLabel apLabel;
    private JLabel attLeftLabel;
    private JLabel splLabel;

    // Constructors
    public MapTurnBattleStats() {
        this.setUpGUI();
        this.updateIcons();
    }

    // Methods
    public Container getStatsPane() {
        return this.statsPane;
    }

    private void setUpGUI() {
        this.statsPane = new Container();
        this.statsPane.setLayout(new GridLayout(9, 1));
        this.nameLabel = new JLabel("", null, SwingConstants.LEFT);
        this.teamLabel = new JLabel("", null, SwingConstants.LEFT);
        this.hpLabel = new JLabel("", null, SwingConstants.LEFT);
        this.mpLabel = new JLabel("", null, SwingConstants.LEFT);
        this.attLabel = new JLabel("", null, SwingConstants.LEFT);
        this.defLabel = new JLabel("", null, SwingConstants.LEFT);
        this.apLabel = new JLabel("", null, SwingConstants.LEFT);
        this.attLeftLabel = new JLabel("", null, SwingConstants.LEFT);
        this.splLabel = new JLabel("", null, SwingConstants.LEFT);
        this.statsPane.add(this.nameLabel);
        this.statsPane.add(this.teamLabel);
        this.statsPane.add(this.hpLabel);
        this.statsPane.add(this.mpLabel);
        this.statsPane.add(this.attLabel);
        this.statsPane.add(this.defLabel);
        this.statsPane.add(this.apLabel);
        this.statsPane.add(this.attLeftLabel);
        this.statsPane.add(this.splLabel);
    }

    private void updateIcons() {
        final var nameImage = StatImageManager
                .getImage(StatImageConstants.STAT_IMAGE_NAME);
        this.nameLabel.setIcon(nameImage);
        final var teamImage = StatImageManager
                .getImage(StatImageConstants.STAT_IMAGE_TEAM);
        this.teamLabel.setIcon(teamImage);
        final var hpImage = StatImageManager
                .getImage(StatImageConstants.STAT_IMAGE_HEALTH);
        this.hpLabel.setIcon(hpImage);
        final var mpImage = StatImageManager
                .getImage(StatImageConstants.STAT_IMAGE_MAGIC);
        this.mpLabel.setIcon(mpImage);
        final var attImage = StatImageManager
                .getImage(StatImageConstants.STAT_IMAGE_ATTACK);
        this.attLabel.setIcon(attImage);
        final var defImage = StatImageManager
                .getImage(StatImageConstants.STAT_IMAGE_DEFENSE);
        this.defLabel.setIcon(defImage);
        final var apImage = StatImageManager
                .getImage(StatImageConstants.STAT_IMAGE_ACTIONS);
        this.apLabel.setIcon(apImage);
        final var attLeftImage = StatImageManager
                .getImage(StatImageConstants.STAT_IMAGE_ATTACKS);
        this.attLeftLabel.setIcon(attLeftImage);
        final var spImage = StatImageManager
                .getImage(StatImageConstants.STAT_IMAGE_SPELLS);
        this.splLabel.setIcon(spImage);
    }

    public void updateStats(final BattleCharacter bc) {
        this.nameLabel.setText(bc.getName());
        this.teamLabel.setText(bc.getTeamString());
        this.hpLabel.setText(bc.getTemplate().getHPString());
        this.mpLabel.setText(bc.getTemplate().getMPString());
        this.attLabel.setText(Integer.toString(bc.getTemplate().getAttack()));
        this.defLabel.setText(Integer.toString(bc.getTemplate().getDefense()));
        this.apLabel.setText(bc.getAPString());
        this.attLeftLabel.setText(bc.getAttackString());
        this.splLabel.setText(bc.getSpellString());
    }
}
