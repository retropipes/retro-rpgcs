/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.battle;

import javax.swing.JOptionPane;

import com.puttysoftware.retrorpgcs.creatures.party.PartyManager;

public class BossRewards {
    // Fields
    static final String[] rewardOptions = { "Attack", "Defense", "HP", "MP" };

    // Methods
    public static void doRewards() {
        final var player = PartyManager.getParty().getLeader();
        String dialogResult = null;
        while (dialogResult == null) {
            dialogResult = (String) JOptionPane.showInputDialog(null,
                    "You get to increase a stat permanently.\nWhich Stat?",
                    "Boss Rewards", JOptionPane.QUESTION_MESSAGE, null,
                    BossRewards.rewardOptions, BossRewards.rewardOptions[0]);
        }
        if (dialogResult.equals(BossRewards.rewardOptions[0])) {
            // Attack
            player.spendPointOnAttack();
        } else if (dialogResult.equals(BossRewards.rewardOptions[1])) {
            // Defense
            player.spendPointOnDefense();
        } else if (dialogResult.equals(BossRewards.rewardOptions[2])) {
            // HP
            player.spendPointOnHP();
        } else if (dialogResult.equals(BossRewards.rewardOptions[3])) {
            // MP
            player.spendPointOnMP();
        }
        PartyManager.updatePostKill();
    }

    // Constructor
    private BossRewards() {
        // Do nothing
    }
}
