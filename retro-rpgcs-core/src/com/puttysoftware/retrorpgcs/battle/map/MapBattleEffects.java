/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.battle.map;

import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JLabel;

import com.puttysoftware.retrorpgcs.maze.objects.BattleCharacter;

public class MapBattleEffects {
    // Fields
    private Container effectsPane;
    private JLabel[] effectLabels;

    // Constructors
    public MapBattleEffects() {
        // Do nothing
    }

    // Methods
    public Container getEffectsPane() {
        if (this.effectsPane == null) {
            this.effectsPane = new Container();
        }
        return this.effectsPane;
    }

    private void setUpGUI(final int count) {
        this.effectsPane = this.getEffectsPane();
        this.effectsPane.removeAll();
        this.effectsPane.setLayout(new GridLayout(count, 1));
        this.effectLabels = new JLabel[count];
        for (var x = 0; x < count; x++) {
            this.effectLabels[x] = new JLabel(" ");
        }
    }

    public void updateEffects(final BattleCharacter bc) {
        final var count = bc.getTemplate().getActiveEffectCount();
        if (count > 0) {
            this.setUpGUI(count);
            final var es = bc.getTemplate().getCompleteEffectStringArray();
            for (var x = 0; x < count; x++) {
                this.effectLabels[x].setText(es[x]);
            }
        }
    }
}
