/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.utilities;

import com.puttysoftware.retrorpgcs.maze.Maze;

public interface RandomGenerationRule {
    int NO_LIMIT = 0;

    int getMaximumRequiredQuantity(Maze maze);

    int getMinimumRequiredQuantity(Maze maze);

    boolean isRequired();

    boolean shouldGenerateObject(Maze maze, int row, int col, int floor,
            int level, int layer);
}
