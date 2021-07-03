/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.ai.map;

import java.awt.Point;

public class AutoMapAI extends MapAI {
    // Constructor
    public AutoMapAI() {
	super();
    }

    @Override
    public int getNextAction(final MapAIContext ac) {
	final Point there = ac.isEnemyNearby();
	if (there != null) {
	    // Something hostile is nearby, so attack it
	    this.moveX = there.x;
	    this.moveY = there.y;
	    return MapAI.ACTION_MOVE;
	} else {
	    return MapAI.ACTION_END_TURN;
	}
    }
}
