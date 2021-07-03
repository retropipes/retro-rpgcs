/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.games;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.fileutils.ZipUtilities;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.maze.Extension;
import com.puttysoftware.retrorpgcs.maze.Maze;
import com.puttysoftware.retrorpgcs.maze.PrefixHandler;
import com.puttysoftware.retrorpgcs.maze.SuffixHandler;

public class GameSaveTask extends Thread {
    private static boolean hasExtension(final String s) {
        String ext = null;
        final var i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        if (ext == null) {
            return false;
        } else {
            return true;
        }
    }

    // Fields
    private String filename;

    // Constructors
    public GameSaveTask(final String file) {
        this.filename = file;
        this.setName("Game Writer");
    }

    @Override
    public void run() {
        var success = true;
        final var sg = "Game";
        try {
            final var app = RetroRPGCS.getInstance();
            // filename check
            final var hasExtension = GameSaveTask
                    .hasExtension(this.filename);
            if (!hasExtension) {
                this.filename += Extension.getGameExtensionWithPeriod();
            }
            final var mazeFile = new File(this.filename);
            final var tempLock = new File(
                    Maze.getMazeTempFolder() + "lock.tmp");
            // Set prefix handler
            app.getMazeManager().getMaze()
                    .setPrefixHandler(new PrefixHandler());
            // Set suffix handler
            app.getMazeManager().getMaze()
                    .setSuffixHandler(new SuffixHandler());
            app.getMazeManager().getMaze().writeMaze();
            ZipUtilities.zipDirectory(
                    new File(app.getMazeManager().getMaze().getBasePath()),
                    tempLock);
            // Lock the file
            GameFileManager.save(tempLock, mazeFile);
            final var delSuccess = tempLock.delete();
            if (!delSuccess) {
                throw new IOException("Failed to delete temporary file!");
            }
            app.showMessage(sg + " saved.");
        } catch (final FileNotFoundException fnfe) {
            CommonDialogs.showDialog("Writing the " + sg.toLowerCase()
                    + " failed, probably due to illegal characters in the file name.");
            success = false;
        } catch (final Exception ex) {
            RetroRPGCS.getInstance().handleError(ex);
        }
        RetroRPGCS.getInstance().getMazeManager().handleDeferredSuccess(success,
                false, null);
    }
}
