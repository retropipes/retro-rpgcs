/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.objects;

import com.puttysoftware.retrorpgcs.creatures.Creature;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractBattleCharacter;

public class BattleCharacter extends AbstractBattleCharacter {
    // Constructors
    public BattleCharacter(final Creature newTemplate) {
        super(newTemplate);
    }
}
