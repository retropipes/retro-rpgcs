/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze.games;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.fileutils.ZipUtilities;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.VersionException;
import com.puttysoftware.retrorpgcs.creatures.party.PartyManager;
import com.puttysoftware.retrorpgcs.maze.Maze;
import com.puttysoftware.retrorpgcs.maze.PrefixHandler;
import com.puttysoftware.retrorpgcs.maze.SuffixHandler;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractMazeObject;
import com.puttysoftware.retrorpgcs.maze.utilities.ImageColorConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.LogoManager;

public class GameLoadTask extends Thread {
    // Fields
    private final String filename;
    private final JFrame loadFrame;

    // Constructors
    public GameLoadTask(final String file) {
        this.filename = file;
        this.setName("Game Loader");
        this.loadFrame = new JFrame("Loading...");
        this.loadFrame.setIconImage(LogoManager.getIconLogo());
        final JProgressBar loadBar = new JProgressBar();
        loadBar.setIndeterminate(true);
        this.loadFrame.getContentPane().add(loadBar);
        this.loadFrame.setResizable(false);
        this.loadFrame
                .setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.loadFrame.pack();
    }

    // Methods
    @Override
    public void run() {
        final String sg = "Game";
        final File mazeFile = new File(this.filename);
        try {
            this.loadFrame.setVisible(true);
            final RetroRPGCS app = RetroRPGCS.getInstance();
            int startW;
            app.getGameManager().setSavedGameFlag(false);
            final File tempLock = new File(
                    Maze.getMazeTempFolder() + "lock.tmp");
            Maze gameMaze = new Maze();
            // Unlock the file
            GameFileManager.load(mazeFile, tempLock);
            ZipUtilities.unzipDirectory(tempLock,
                    new File(gameMaze.getBasePath()));
            final boolean success = tempLock.delete();
            if (!success) {
                throw new IOException("Failed to delete temporary file!");
            }
            // Set prefix handler
            gameMaze.setPrefixHandler(new PrefixHandler());
            // Set suffix handler
            gameMaze.setSuffixHandler(new SuffixHandler());
            gameMaze = gameMaze.readMaze();
            if (gameMaze == null) {
                throw new IOException("Unknown object encountered.");
            }
            app.getMazeManager().setMaze(gameMaze);
            startW = gameMaze.getStartLevel();
            gameMaze.switchLevel(startW);
            final boolean playerExists = gameMaze.doesPlayerExist();
            if (playerExists) {
                app.getMazeManager().getMaze().setPlayerToStart();
                app.getGameManager().resetViewingWindow();
            }
            gameMaze.save();
            // Final cleanup
            app.getGameManager().stateChanged();
            AbstractMazeObject.setTemplateColor(ImageColorConstants
                    .getColorForLevel(PartyManager.getParty().getTowerLevel()));
            app.getMazeManager().setLoaded(true);
            CommonDialogs.showDialog(sg + " loaded.");
            app.getGameManager().playMaze();
            app.getMazeManager().handleDeferredSuccess(true, false, null);
        } catch (final VersionException ve) {
            CommonDialogs.showDialog("Loading the " + sg.toLowerCase()
                    + " failed, due to the format version being unsupported.");
            RetroRPGCS.getInstance().getMazeManager()
                    .handleDeferredSuccess(false, true, mazeFile);
        } catch (final FileNotFoundException fnfe) {
            CommonDialogs.showDialog("Loading the " + sg.toLowerCase()
                    + " failed, probably due to illegal characters in the file name.");
            RetroRPGCS.getInstance().getMazeManager()
                    .handleDeferredSuccess(false, false, null);
        } catch (final IOException ie) {
            CommonDialogs.showDialog("Loading the " + sg.toLowerCase()
                    + " failed, due to some other type of I/O error.");
            RetroRPGCS.getInstance().getMazeManager()
                    .handleDeferredSuccess(false, false, null);
        } catch (final Exception ex) {
            RetroRPGCS.getInstance().handleError(ex);
        } finally {
            this.loadFrame.setVisible(false);
        }
    }
}
