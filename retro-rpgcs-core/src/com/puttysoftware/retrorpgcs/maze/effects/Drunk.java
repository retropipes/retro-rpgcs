/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.maze.effects;

import com.puttysoftware.randomrange.RandomRange;

public class Drunk extends MazeEffect {
    // Constructor
    public Drunk(final int newRounds) {
	super("Drunk", newRounds);
    }

    @Override
    public int[] modifyMove2(final int... arg) {
	final RandomRange rx = new RandomRange(0, 1);
	final RandomRange ry = new RandomRange(0, 1);
	arg[0] += rx.generate();
	arg[1] += ry.generate();
	return arg;
    }
}