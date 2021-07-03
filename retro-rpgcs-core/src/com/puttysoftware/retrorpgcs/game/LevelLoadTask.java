/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.game;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import com.puttysoftware.retrorpgcs.Application;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.creatures.party.PartyManager;
import com.puttysoftware.retrorpgcs.maze.Maze;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractMazeObject;
import com.puttysoftware.retrorpgcs.maze.utilities.ImageColorConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.LogoManager;

public class LevelLoadTask extends Thread {
    // Fields
    private final JFrame loadFrame;
    private final int level;

    // Constructors
    public LevelLoadTask(final int offset) {
	this.level = offset;
	this.setName("Level Loader");
	this.loadFrame = new JFrame("Loading...");
	this.loadFrame.setIconImage(LogoManager.getIconLogo());
	final JProgressBar loadBar = new JProgressBar();
	loadBar.setIndeterminate(true);
	this.loadFrame.getContentPane().add(loadBar);
	this.loadFrame.setResizable(false);
	this.loadFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	this.loadFrame.pack();
    }

    // Methods
    @Override
    public void run() {
	try {
	    this.loadFrame.setVisible(true);
	    final Application app = RetroRPGCS.getApplication();
	    final Maze gameMaze = app.getMazeManager().getMaze();
	    app.getGameManager().disableEvents();
	    gameMaze.switchLevelOffset(this.level);
	    gameMaze.offsetPlayerLocationW(this.level);
	    PartyManager.getParty().offsetTowerLevel(this.level);
	    AbstractMazeObject
		    .setTemplateColor(ImageColorConstants.getColorForLevel(PartyManager.getParty().getTowerLevel()));
	    app.getGameManager().resetViewingWindow();
	    app.getGameManager().enableEvents();
	    app.getGameManager().redrawMaze();
	} catch (final Exception ex) {
	    RetroRPGCS.getErrorLogger().logError(ex);
	} finally {
	    this.loadFrame.setVisible(false);
	}
    }
}
