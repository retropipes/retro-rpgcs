/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.game;

import javax.swing.JFrame;

import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.creatures.party.PartyManager;
import com.puttysoftware.retrorpgcs.maze.GenerateTask;
import com.puttysoftware.retrorpgcs.maze.MazeConstants;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractMazeObject;
import com.puttysoftware.retrorpgcs.maze.effects.MazeEffectManager;
import com.puttysoftware.retrorpgcs.maze.objects.Empty;
import com.puttysoftware.retrorpgcs.maze.objects.EmptyVoid;
import com.puttysoftware.retrorpgcs.resourcemanagers.ImageTransformer;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;

public final class GameLogicManager {
    public static void decay() {
        final var app = RetroRPGCS.getInstance();
        final var m = app.getMazeManager().getMaze();
        m.setCell(new Empty(), m.getPlayerLocationX(), m.getPlayerLocationY(),
                m.getPlayerLocationZ(), MazeConstants.LAYER_OBJECT);
    }

    public static void morph(final AbstractMazeObject morphInto) {
        final var app = RetroRPGCS.getInstance();
        final var m = app.getMazeManager().getMaze();
        m.setCell(morphInto, m.getPlayerLocationX(), m.getPlayerLocationY(),
                m.getPlayerLocationZ(), morphInto.getLayer());
    }

    public static void resetPlayerLocation() {
        GameLogicManager.resetPlayerLocation(0);
    }

    public static void resetPlayerLocation(final int level) {
        final var app = RetroRPGCS.getInstance();
        final var m = app.getMazeManager().getMaze();
        if (m != null) {
            m.switchLevel(level);
            m.setPlayerToStart();
        }
    }

    // Fields
    private boolean savedGameFlag;
    private final GameViewingWindowManager vwMgr;
    private boolean stateChanged;
    private final GameGUIManager gui;
    private final MazeEffectManager em;
    private final MovementTask mt;

    // Constructors
    public GameLogicManager() {
        this.vwMgr = new GameViewingWindowManager();
        this.em = new MazeEffectManager();
        this.gui = new GameGUIManager();
        this.mt = new MovementTask(this.vwMgr, this.em, this.gui);
        this.mt.start();
        this.savedGameFlag = false;
        this.stateChanged = true;
    }

    public void activateEffect(final int effectID) {
        this.em.activateEffect(effectID);
    }

    public void deactivateAllEffects() {
        this.em.deactivateAllEffects();
    }

    public void disableEvents() {
        this.gui.disableEvents();
    }

    public void enableEvents() {
        this.mt.fireStepActions();
        this.gui.enableEvents();
    }

    public void exitGame() {
        this.stateChanged = true;
        final var app = RetroRPGCS.getInstance();
        final var m = app.getMazeManager().getMaze();
        // Restore the maze
        m.restore();
        m.resetVisibleSquares();
        final var playerExists = m.doesPlayerExist();
        if (playerExists) {
            this.resetViewingWindowAndPlayerLocation();
        } else {
            app.getMazeManager().setLoaded(false);
        }
        // Reset saved game flag
        this.savedGameFlag = false;
        app.getMazeManager().setDirty(false);
        // Exit game
        this.hideOutput();
        app.getGUIManager().showGUI();
    }

    public JFrame getOutputFrame() {
        return this.gui.getOutputFrame();
    }

    public GameViewingWindowManager getViewManager() {
        return this.vwMgr;
    }

    public void goToLevelOffset(final int level) {
        final var app = RetroRPGCS.getInstance();
        final var m = app.getMazeManager().getMaze();
        final var levelExists = m.doesLevelExistOffset(level);
        this.stopMovement();
        if (levelExists) {
            new LevelLoadTask(level).start();
        } else {
            new GenerateTask(false).start();
        }
    }

    public void hideOutput() {
        this.stopMovement();
        this.gui.hideOutput();
    }

    public void identifyObject(final int x, final int y) {
        final var app = RetroRPGCS.getInstance();
        final var m = app.getMazeManager().getMaze();
        final var xOffset = this.vwMgr.getViewingWindowLocationX()
                - GameViewingWindowManager.getOffsetFactorX();
        final var yOffset = this.vwMgr.getViewingWindowLocationY()
                - GameViewingWindowManager.getOffsetFactorY();
        final var destX = x / ImageTransformer.getGraphicSize()
                + this.vwMgr.getViewingWindowLocationX() - xOffset + yOffset;
        final var destY = y / ImageTransformer.getGraphicSize()
                + this.vwMgr.getViewingWindowLocationY() + xOffset - yOffset;
        final var destZ = m.getPlayerLocationZ();
        try {
            final var target1 = m.getCell(destX, destY, destZ,
                    MazeConstants.LAYER_GROUND);
            final var target2 = m.getCell(destX, destY, destZ,
                    MazeConstants.LAYER_OBJECT);
            target1.determineCurrentAppearance(destX, destY, destZ);
            target2.determineCurrentAppearance(destX, destY, destZ);
            final var gameName1 = target1.getGameName();
            final var gameName2 = target2.getGameName();
            RetroRPGCS.getInstance()
                    .showMessage(gameName2 + " on " + gameName1);
            SoundManager.playSound(SoundConstants.SOUND_IDENTIFY);
        } catch (final ArrayIndexOutOfBoundsException ae) {
            final var ev = new EmptyVoid();
            ev.determineCurrentAppearance(destX, destY, destZ);
            RetroRPGCS.getInstance().showMessage(ev.getGameName());
            SoundManager.playSound(SoundConstants.SOUND_IDENTIFY);
        }
    }

    public void keepNextMessage() {
        this.gui.keepNextMessage();
    }

    // Methods
    public boolean newGame() {
        final var owner = RetroRPGCS.getInstance().getOutputFrame();
        this.em.deactivateAllEffects();
        if (this.savedGameFlag && (PartyManager.getParty() != null)) {
            return true;
        } else {
            return PartyManager.createParty(owner);
        }
    }

    public void playMaze() {
        final var app = RetroRPGCS.getInstance();
        final var m = app.getMazeManager().getMaze();
        if (app.getMazeManager().getLoaded()) {
            this.gui.initViewManager();
            app.getGUIManager().hideGUI();
            if (this.stateChanged) {
                // Initialize only if the maze state has changed
                app.getMazeManager().getMaze().switchLevel(
                        app.getMazeManager().getMaze().getStartLevel());
                this.stateChanged = false;
            }
            // Make sure message area is attached to the border pane
            this.gui.updateGameGUI(this.em);
            // Make sure initial area player is in is visible
            final var px = m.getPlayerLocationX();
            final var py = m.getPlayerLocationY();
            final var pz = m.getPlayerLocationZ();
            m.updateVisibleSquares(px, py, pz);
            this.showOutput();
            this.redrawMaze();
        } else {
            CommonDialogs.showDialog("No Maze Opened");
        }
    }

    public void redrawMaze() {
        this.gui.redrawMaze();
    }

    public void resetViewingWindow() {
        final var app = RetroRPGCS.getInstance();
        final var m = app.getMazeManager().getMaze();
        if (m != null && this.vwMgr != null) {
            this.vwMgr.setViewingWindowLocationX(m.getPlayerLocationY()
                    - GameViewingWindowManager.getOffsetFactorX());
            this.vwMgr.setViewingWindowLocationY(m.getPlayerLocationX()
                    - GameViewingWindowManager.getOffsetFactorY());
        }
    }

    public void resetViewingWindowAndPlayerLocation() {
        GameLogicManager.resetPlayerLocation();
        this.resetViewingWindow();
    }

    public void setSavedGameFlag(final boolean value) {
        this.savedGameFlag = value;
    }

    public void setStatusMessage(final String msg) {
        this.gui.setStatusMessage(msg);
    }

    public void showOutput() {
        RetroRPGCS.getInstance().setMode(RetroRPGCS.STATUS_GAME);
        this.gui.showOutput();
    }

    public void stateChanged() {
        this.stateChanged = true;
    }

    public void stopMovement() {
        this.mt.stopMovement();
    }

    public boolean tryUpdatePositionAbsolute(final int x, final int y,
            final int z) {
        return this.mt.tryAbsolute(x, y, z);
    }

    public void updatePositionAbsolute(final int x, final int y, final int z) {
        this.mt.moveAbsolute(x, y, z);
    }

    public void updatePositionRelative(final int dirX, final int dirY,
            final int dirZ) {
        this.mt.moveRelative(dirX, dirY, dirZ);
    }

    public void viewingWindowSizeChanged() {
        this.gui.viewingWindowSizeChanged(this.em);
        this.resetViewingWindow();
    }
}
