/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.maze;

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.fileutils.FilenameChecker;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractMazeObject;
import com.puttysoftware.retrorpgcs.maze.games.GameFinder;
import com.puttysoftware.retrorpgcs.maze.games.GameLoadTask;
import com.puttysoftware.retrorpgcs.maze.games.GameSaveTask;

public final class MazeManager {
    private static final String DIR = "Games";

    private static String getFileNameOnly(final String s) {
        String fno = null;
        final var i = s.lastIndexOf(File.pathSeparatorChar);
        if (i > 0 && i < s.length() - 1) {
            fno = s.substring(i + 1);
        } else {
            fno = s;
        }
        return fno;
    }

    private static String getGameDirectory() {
        final var b = new StringBuilder();
        b.append(MazeManager.getGameDirPrefix());
        b.append(File.pathSeparator);
        b.append(MazeManager.getGameDirectoryName());
        b.append(File.pathSeparator);
        return b.toString();
    }

    private static String getGameDirectoryName() {
        return MazeManager.DIR;
    }

    private static String getGameDirPrefix() {
        return RetroRPGCS.getSupportDirectory();
    }

    private static String getNameWithoutExtension(final String s) {
        String ext = null;
        final var i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(0, i);
        } else {
            ext = s;
        }
        return ext;
    }

    private static void loadFile(final String filename) {
        if (!FilenameChecker.isFilenameOK(MazeManager.getNameWithoutExtension(
                MazeManager.getFileNameOnly(filename)))) {
            CommonDialogs.showErrorDialog(
                    "The file you selected contains illegal characters in its\n"
                            + "name. These characters are not allowed: /?<>\\:|\"\n"
                            + "Files named con, nul, or prn are illegal, as are files\n"
                            + "named com1 through com9 and lpt1 through lpt9.",
                    "Load");
        } else {
            final var llt = new GameLoadTask(filename);
            llt.start();
        }
    }

    private static void saveFile(final String filename) {
        final var sg = "Saved Game";
        RetroRPGCS.getInstance().showMessage("Saving " + sg + " file...");
        final var lst = new GameSaveTask(filename);
        lst.start();
    }

    public static boolean saveGame() {
        var filename = "";
        String extension;
        var returnVal = "\\";
        while (!FilenameChecker.isFilenameOK(returnVal)) {
            returnVal = CommonDialogs.showTextInputDialog("Name?", "Save Game");
            if (returnVal != null) {
                extension = Extension.getGameExtensionWithPeriod();
                final var file = new File(
                        MazeManager.getGameDirectory() + returnVal + extension);
                filename = file.getAbsolutePath();
                if (!FilenameChecker.isFilenameOK(returnVal)) {
                    CommonDialogs.showErrorDialog(
                            "The file name you entered contains illegal characters.\n"
                                    + "These characters are not allowed: /?<>\\:|\"\n"
                                    + "Files named con, nul, or prn are illegal, as are files\n"
                                    + "named com1 through com9 and lpt1 through lpt9.",
                            "Save Game");
                } else {
                    // Make sure folder exists
                    if (!file.getParentFile().exists()) {
                        final var okay = file.getParentFile().mkdirs();
                        if (!okay) {
                            RetroRPGCS.getInstance()
                                    .handleError(new IOException(
                                            "Cannot create game folder!"));
                        }
                    }
                    MazeManager.saveFile(filename);
                }
            } else {
                break;
            }
        }
        return false;
    }

    public static int showSaveDialog() {
        String type, source;
        final var app = RetroRPGCS.getInstance();
        final var mode = app.getMode();
        if (mode == RetroRPGCS.STATUS_GAME) {
            type = "game";
            source = "RetroRPGCS";
        } else {
            // Not in the game or editor, so abort
            return JOptionPane.NO_OPTION;
        }
        return CommonDialogs.showYNCConfirmDialog(
                "Do you want to save your " + type + "?", source);
    }

    // Fields
    private Maze gameMaze;
    private boolean loaded, isDirty;

    // Constructors
    public MazeManager() {
        this.loaded = false;
        this.isDirty = false;
        this.gameMaze = null;
    }

    public boolean getDirty() {
        return this.isDirty;
    }

    public boolean getLoaded() {
        return this.loaded;
    }

    // Methods
    public Maze getMaze() {
        return this.gameMaze;
    }

    public AbstractMazeObject getMazeObject(final int x, final int y,
            final int z, final int e) {
        try {
            return this.gameMaze.getCell(x, y, z, e);
        } catch (final ArrayIndexOutOfBoundsException ae) {
            return null;
        }
    }

    public void handleDeferredSuccess(final boolean value,
            final boolean versionError, final File triedToLoad) {
        if (value) {
            this.setLoaded(true);
        }
        if (versionError) {
            triedToLoad.delete();
        }
        this.setDirty(false);
        RetroRPGCS.getInstance().getGameManager().stateChanged();
        RetroRPGCS.getInstance().getMenus().checkFlags();
    }

    public boolean loadGame() {
        var status = 0;
        var saved = true;
        String filename;
        final var gf = new GameFinder();
        if (this.getDirty()) {
            status = MazeManager.showSaveDialog();
            if (status == JOptionPane.YES_OPTION) {
                saved = MazeManager.saveGame();
            } else if (status == JOptionPane.CANCEL_OPTION) {
                saved = false;
            } else {
                this.setDirty(false);
            }
        }
        if (saved) {
            final var gameDir = MazeManager.getGameDirectory();
            final var rawChoices = new File(gameDir).list(gf);
            if (rawChoices != null) {
                final var choices = new String[rawChoices.length];
                // Strip extension
                for (var x = 0; x < choices.length; x++) {
                    choices[x] = MazeManager
                            .getNameWithoutExtension(rawChoices[x]);
                }
                final var returnVal = CommonDialogs.showInputDialog(
                        "Select a Game", "Load Game", choices, choices[0]);
                if (returnVal != null) {
                    var index = -1;
                    for (var x = 0; x < choices.length; x++) {
                        if (returnVal.equals(choices[x])) {
                            index = x;
                            break;
                        }
                    }
                    if (index != -1) {
                        final var file = new File(gameDir + rawChoices[index]);
                        filename = file.getAbsolutePath();
                        MazeManager.loadFile(filename);
                    } else {
                        // Result not found
                        if (this.loaded) {
                            return true;
                        }
                    }
                } else // User cancelled
                if (this.loaded) {
                    return true;
                }
            } else {
                CommonDialogs.showErrorDialog("No Games Found!", "Load Game");
                if (this.loaded) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setDirty(final boolean newDirty) {
        final var app = RetroRPGCS.getInstance();
        this.isDirty = newDirty;
        final var frame = app.getOutputFrame();
        if (frame != null) {
            frame.getRootPane().putClientProperty("Window.documentModified",
                    Boolean.valueOf(newDirty));
        }
        app.getMenus().checkFlags();
    }

    public void setLoaded(final boolean status) {
        final var app = RetroRPGCS.getInstance();
        this.loaded = status;
        app.getMenus().checkFlags();
    }

    public void setMaze(final Maze newMaze) {
        this.gameMaze = newMaze;
    }
}
