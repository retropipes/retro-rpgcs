/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.battle.map;

import com.puttysoftware.retrorpgcs.creatures.monsters.MonsterFactory;
import com.puttysoftware.retrorpgcs.maze.objects.BattleCharacter;

public class MapBattle {
    // Fields
    private final BattleCharacter monster;

    // Constructors
    public MapBattle() {
        super();
        this.monster = new BattleCharacter(
                MonsterFactory.getNewMonsterInstance());
    }

    // Methods
    public BattleCharacter getBattlers() {
        return this.monster;
    }
}
