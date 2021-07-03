/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.maze.games;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.fileutils.ZipUtilities;
import com.puttysoftware.retrorpgcs.Application;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.maze.Extension;
import com.puttysoftware.retrorpgcs.maze.Maze;
import com.puttysoftware.retrorpgcs.maze.PrefixHandler;
import com.puttysoftware.retrorpgcs.maze.SuffixHandler;

public class GameSaveTask extends Thread {
    // Fields
    private String filename;

    // Constructors
    public GameSaveTask(final String file) {
	this.filename = file;
	this.setName("Game Writer");
    }

    @Override
    public void run() {
	boolean success = true;
	final String sg = "Game";
	try {
	    final Application app = RetroRPGCS.getApplication();
	    // filename check
	    final boolean hasExtension = GameSaveTask.hasExtension(this.filename);
	    if (!hasExtension) {
		this.filename += Extension.getGameExtensionWithPeriod();
	    }
	    final File mazeFile = new File(this.filename);
	    final File tempLock = new File(Maze.getMazeTempFolder() + "lock.tmp");
	    // Set prefix handler
	    app.getMazeManager().getMaze().setPrefixHandler(new PrefixHandler());
	    // Set suffix handler
	    app.getMazeManager().getMaze().setSuffixHandler(new SuffixHandler());
	    app.getMazeManager().getMaze().writeMaze();
	    ZipUtilities.zipDirectory(new File(app.getMazeManager().getMaze().getBasePath()), tempLock);
	    // Lock the file
	    GameFileManager.save(tempLock, mazeFile);
	    final boolean delSuccess = tempLock.delete();
	    if (!delSuccess) {
		throw new IOException("Failed to delete temporary file!");
	    }
	    app.showMessage(sg + " saved.");
	} catch (final FileNotFoundException fnfe) {
	    CommonDialogs.showDialog("Writing the " + sg.toLowerCase()
		    + " failed, probably due to illegal characters in the file name.");
	    success = false;
	} catch (final Exception ex) {
	    RetroRPGCS.getErrorLogger().logError(ex);
	}
	RetroRPGCS.getApplication().getMazeManager().handleDeferredSuccess(success, false, null);
    }

    private static boolean hasExtension(final String s) {
	String ext = null;
	final int i = s.lastIndexOf('.');
	if (i > 0 && i < s.length() - 1) {
	    ext = s.substring(i + 1).toLowerCase();
	}
	if (ext == null) {
	    return false;
	} else {
	    return true;
	}
    }
}
