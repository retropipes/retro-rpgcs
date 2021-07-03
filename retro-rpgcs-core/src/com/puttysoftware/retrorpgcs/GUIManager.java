/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.puttysoftware.fileutils.DirectoryUtilities;
import com.puttysoftware.images.BufferedImageIcon;
import com.puttysoftware.retrorpgcs.maze.Maze;
import com.puttysoftware.retrorpgcs.maze.MazeManager;
import com.puttysoftware.retrorpgcs.prefs.PreferencesManager;
import com.puttysoftware.retrorpgcs.resourcemanagers.LogoManager;

public final class GUIManager {
    // Fields
    private final JFrame guiFrame;
    private final JLabel logoLabel;

    // Constructors
    public GUIManager() {
	final CloseHandler cHandler = new CloseHandler();
	this.guiFrame = new JFrame("RetroRPGCS");
	final Image iconlogo = LogoManager.getIconLogo();
	this.guiFrame.setIconImage(iconlogo);
	final Container guiPane = this.guiFrame.getContentPane();
	this.guiFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	this.guiFrame.setLayout(new GridLayout(1, 1));
	this.logoLabel = new JLabel("", null, SwingConstants.CENTER);
	this.logoLabel.setBorder(new EmptyBorder(0, 0, 0, 0));
	guiPane.add(this.logoLabel);
	this.guiFrame.setResizable(false);
	this.guiFrame.addWindowListener(cHandler);
    }

    // Methods
    public JFrame getGUIFrame() {
	if (this.guiFrame.isVisible()) {
	    return this.guiFrame;
	} else {
	    return null;
	}
    }

    public void showGUI() {
	final RetroRPGCS app = RetroRPGCS.getInstance();
	app.setMode(RetroRPGCS.STATUS_GUI);
	this.guiFrame.setJMenuBar(app.getMenus().getMainMenuBar());
	this.guiFrame.setVisible(true);
	app.getMenus().setMainMenus();
	app.getMenus().checkFlags();
    }

    public void hideGUI() {
	this.guiFrame.setVisible(false);
    }

    public void hideGUITemporarily() {
	this.guiFrame.setVisible(false);
    }

    public void updateLogo() {
	final BufferedImageIcon logo = LogoManager.getLogo();
	this.logoLabel.setIcon(logo);
	final Image iconlogo = RetroRPGCS.getIconLogo();
	this.guiFrame.setIconImage(iconlogo);
	this.guiFrame.pack();
    }

    // Used by reflection, so cannot be static!
    @SuppressWarnings("static-method")
    public boolean quitHandler() {
	final MazeManager mm = RetroRPGCS.getInstance().getMazeManager();
	boolean saved = true;
	int status = JOptionPane.DEFAULT_OPTION;
	if (mm.getDirty()) {
	    status = MazeManager.showSaveDialog();
	    if (status == JOptionPane.YES_OPTION) {
		saved = MazeManager.saveGame();
	    } else if (status == JOptionPane.CANCEL_OPTION) {
		saved = false;
	    } else {
		mm.setDirty(false);
	    }
	}
	if (saved) {
	    PreferencesManager.writePrefs();
	    // Run cleanup task
	    try {
		final File dirToDelete = new File(Maze.getMazeTempFolder());
		DirectoryUtilities.removeDirectory(dirToDelete);
	    } catch (final Throwable t) {
		// Ignore
	    }
	}
	return saved;
    }

    private class CloseHandler implements WindowListener {
	CloseHandler() {
	    // Do nothing
	}

	@Override
	public void windowActivated(final WindowEvent arg0) {
	    // Do nothing
	}

	@Override
	public void windowClosed(final WindowEvent arg0) {
	    // Do nothing
	}

	@Override
	public void windowClosing(final WindowEvent arg0) {
	    if (GUIManager.this.quitHandler()) {
		System.exit(0);
	    }
	}

	@Override
	public void windowDeactivated(final WindowEvent arg0) {
	    // Do nothing
	}

	@Override
	public void windowDeiconified(final WindowEvent arg0) {
	    // Do nothing
	}

	@Override
	public void windowIconified(final WindowEvent arg0) {
	    // Do nothing
	}

	@Override
	public void windowOpened(final WindowEvent arg0) {
	    // Do nothing
	}
    }
}
