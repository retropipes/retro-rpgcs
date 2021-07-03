/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.game;

import javax.swing.JFrame;

import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.creatures.party.PartyManager;
import com.puttysoftware.retrorpgcs.maze.GenerateTask;
import com.puttysoftware.retrorpgcs.maze.Maze;
import com.puttysoftware.retrorpgcs.maze.MazeConstants;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractMazeObject;
import com.puttysoftware.retrorpgcs.maze.effects.MazeEffectManager;
import com.puttysoftware.retrorpgcs.maze.objects.Empty;
import com.puttysoftware.retrorpgcs.maze.objects.EmptyVoid;
import com.puttysoftware.retrorpgcs.resourcemanagers.ImageTransformer;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;

public final class GameLogicManager {
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

    // Methods
    public boolean newGame() {
	final JFrame owner = RetroRPGCS.getInstance().getOutputFrame();
	this.em.deactivateAllEffects();
	if (this.savedGameFlag) {
	    if (PartyManager.getParty() != null) {
		return true;
	    } else {
		return PartyManager.createParty(owner);
	    }
	} else {
	    return PartyManager.createParty(owner);
	}
    }

    public void enableEvents() {
	this.mt.fireStepActions();
	this.gui.enableEvents();
    }

    public void disableEvents() {
	this.gui.disableEvents();
    }

    public void stopMovement() {
	this.mt.stopMovement();
    }

    public void deactivateAllEffects() {
	this.em.deactivateAllEffects();
    }

    public void viewingWindowSizeChanged() {
	this.gui.viewingWindowSizeChanged(this.em);
	this.resetViewingWindow();
    }

    public void stateChanged() {
	this.stateChanged = true;
    }

    public GameViewingWindowManager getViewManager() {
	return this.vwMgr;
    }

    public void setSavedGameFlag(final boolean value) {
	this.savedGameFlag = value;
    }

    public void activateEffect(final int effectID) {
	this.em.activateEffect(effectID);
    }

    public void setStatusMessage(final String msg) {
	this.gui.setStatusMessage(msg);
    }

    public void updatePositionRelative(final int dirX, final int dirY, final int dirZ) {
	this.mt.moveRelative(dirX, dirY, dirZ);
    }

    public boolean tryUpdatePositionAbsolute(final int x, final int y, final int z) {
	return this.mt.tryAbsolute(x, y, z);
    }

    public void updatePositionAbsolute(final int x, final int y, final int z) {
	this.mt.moveAbsolute(x, y, z);
    }

    public void redrawMaze() {
	this.gui.redrawMaze();
    }

    public void resetViewingWindowAndPlayerLocation() {
	GameLogicManager.resetPlayerLocation();
	this.resetViewingWindow();
    }

    public void resetViewingWindow() {
	final RetroRPGCS app = RetroRPGCS.getInstance();
	final Maze m = app.getMazeManager().getMaze();
	if (m != null && this.vwMgr != null) {
	    this.vwMgr.setViewingWindowLocationX(m.getPlayerLocationY() - GameViewingWindowManager.getOffsetFactorX());
	    this.vwMgr.setViewingWindowLocationY(m.getPlayerLocationX() - GameViewingWindowManager.getOffsetFactorY());
	}
    }

    public static void resetPlayerLocation() {
	GameLogicManager.resetPlayerLocation(0);
    }

    public static void resetPlayerLocation(final int level) {
	final RetroRPGCS app = RetroRPGCS.getInstance();
	final Maze m = app.getMazeManager().getMaze();
	if (m != null) {
	    m.switchLevel(level);
	    m.setPlayerToStart();
	}
    }

    public void goToLevelOffset(final int level) {
	final RetroRPGCS app = RetroRPGCS.getInstance();
	final Maze m = app.getMazeManager().getMaze();
	final boolean levelExists = m.doesLevelExistOffset(level);
	this.stopMovement();
	if (levelExists) {
	    new LevelLoadTask(level).start();
	} else {
	    new GenerateTask(false).start();
	}
    }

    public void exitGame() {
	this.stateChanged = true;
	final RetroRPGCS app = RetroRPGCS.getInstance();
	final Maze m = app.getMazeManager().getMaze();
	// Restore the maze
	m.restore();
	m.resetVisibleSquares();
	final boolean playerExists = m.doesPlayerExist();
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

    public static void decay() {
	final RetroRPGCS app = RetroRPGCS.getInstance();
	final Maze m = app.getMazeManager().getMaze();
	m.setCell(new Empty(), m.getPlayerLocationX(), m.getPlayerLocationY(), m.getPlayerLocationZ(),
		MazeConstants.LAYER_OBJECT);
    }

    public static void morph(final AbstractMazeObject morphInto) {
	final RetroRPGCS app = RetroRPGCS.getInstance();
	final Maze m = app.getMazeManager().getMaze();
	m.setCell(morphInto, m.getPlayerLocationX(), m.getPlayerLocationY(), m.getPlayerLocationZ(),
		morphInto.getLayer());
    }

    public void keepNextMessage() {
	this.gui.keepNextMessage();
    }

    public void identifyObject(final int x, final int y) {
	final RetroRPGCS app = RetroRPGCS.getInstance();
	final Maze m = app.getMazeManager().getMaze();
	final int xOffset = this.vwMgr.getViewingWindowLocationX() - GameViewingWindowManager.getOffsetFactorX();
	final int yOffset = this.vwMgr.getViewingWindowLocationY() - GameViewingWindowManager.getOffsetFactorY();
	final int destX = x / ImageTransformer.getGraphicSize() + this.vwMgr.getViewingWindowLocationX() - xOffset
		+ yOffset;
	final int destY = y / ImageTransformer.getGraphicSize() + this.vwMgr.getViewingWindowLocationY() + xOffset
		- yOffset;
	final int destZ = m.getPlayerLocationZ();
	try {
	    final AbstractMazeObject target1 = m.getCell(destX, destY, destZ, MazeConstants.LAYER_GROUND);
	    final AbstractMazeObject target2 = m.getCell(destX, destY, destZ, MazeConstants.LAYER_OBJECT);
	    target1.determineCurrentAppearance(destX, destY, destZ);
	    target2.determineCurrentAppearance(destX, destY, destZ);
	    final String gameName1 = target1.getGameName();
	    final String gameName2 = target2.getGameName();
	    RetroRPGCS.getInstance().showMessage(gameName2 + " on " + gameName1);
	    SoundManager.playSound(SoundConstants.SOUND_IDENTIFY);
	} catch (final ArrayIndexOutOfBoundsException ae) {
	    final EmptyVoid ev = new EmptyVoid();
	    ev.determineCurrentAppearance(destX, destY, destZ);
	    RetroRPGCS.getInstance().showMessage(ev.getGameName());
	    SoundManager.playSound(SoundConstants.SOUND_IDENTIFY);
	}
    }

    public void playMaze() {
	final RetroRPGCS app = RetroRPGCS.getInstance();
	final Maze m = app.getMazeManager().getMaze();
	if (app.getMazeManager().getLoaded()) {
	    this.gui.initViewManager();
	    app.getGUIManager().hideGUI();
	    if (this.stateChanged) {
		// Initialize only if the maze state has changed
		app.getMazeManager().getMaze().switchLevel(app.getMazeManager().getMaze().getStartLevel());
		this.stateChanged = false;
	    }
	    // Make sure message area is attached to the border pane
	    this.gui.updateGameGUI(this.em);
	    // Make sure initial area player is in is visible
	    final int px = m.getPlayerLocationX();
	    final int py = m.getPlayerLocationY();
	    final int pz = m.getPlayerLocationZ();
	    m.updateVisibleSquares(px, py, pz);
	    this.showOutput();
	    this.redrawMaze();
	} else {
	    CommonDialogs.showDialog("No Maze Opened");
	}
    }

    public void showOutput() {
	RetroRPGCS.getInstance().setMode(RetroRPGCS.STATUS_GAME);
	this.gui.showOutput();
    }

    public void hideOutput() {
	this.stopMovement();
	this.gui.hideOutput();
    }
}
