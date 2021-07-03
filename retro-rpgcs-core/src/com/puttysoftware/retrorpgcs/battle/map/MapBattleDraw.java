/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.battle.map;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.puttysoftware.retrorpgcs.DrawGrid;
import com.puttysoftware.retrorpgcs.resourcemanagers.BattleImageManager;

public class MapBattleDraw extends JPanel {
    private static final long serialVersionUID = 35935343464625L;
    private final DrawGrid drawGrid;

    public MapBattleDraw(final DrawGrid grid) {
        this.drawGrid = grid;
        final var vSize = MapBattleViewingWindowManager.getViewingWindowSize();
        final var gSize = BattleImageManager.getGraphicSize();
        this.setPreferredSize(new Dimension(vSize * gSize, vSize * gSize));
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        if (this.drawGrid != null) {
            final var gSize = BattleImageManager.getGraphicSize();
            final var vSize = MapBattleViewingWindowManager
                    .getViewingWindowSize();
            for (var x = 0; x < vSize; x++) {
                for (var y = 0; y < vSize; y++) {
                    g.drawImage(this.drawGrid.getImageCell(y, x), x * gSize,
                            y * gSize, gSize, gSize, null);
                }
            }
        }
    }
}
