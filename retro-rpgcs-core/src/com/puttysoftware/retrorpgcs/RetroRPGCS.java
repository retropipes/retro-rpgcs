/* Import2: An RPG */
package com.puttysoftware.retrorpgcs;

import com.puttysoftware.diane.ErrorLogger;
import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.retrorpgcs.creatures.Creature;

public class RetroRPGCS {
    // Constants
    private static Application application;
    private static final String PROGRAM_NAME = "RetroRPGCS";
    private static final String ERROR_MESSAGE = "Perhaps a bug is to blame for this error message.\n"
	    + "Include the error log with your bug report.\n" + "Email bug reports to: products@puttysoftware.com\n"
	    + "Subject: Import2 Bug Report";
    private static final String ERROR_TITLE = "Import2 Error";
    private static final ErrorLogger elog = new ErrorLogger(RetroRPGCS.PROGRAM_NAME);
    private static final int BATTLE_MAZE_SIZE = 16;

    // Methods
    public static Application getApplication() {
	return RetroRPGCS.application;
    }

    public static int getBattleMazeSize() {
	return RetroRPGCS.BATTLE_MAZE_SIZE;
    }

    public static ErrorLogger getErrorLogger() {
	// Display error message
	CommonDialogs.showErrorDialog(RetroRPGCS.ERROR_MESSAGE, RetroRPGCS.ERROR_TITLE);
	return RetroRPGCS.elog;
    }

    public static void preInit() {
	// Compute action cap
	Creature.computeActionCap(RetroRPGCS.BATTLE_MAZE_SIZE, RetroRPGCS.BATTLE_MAZE_SIZE);
    }

    public static void main_disabled(final String[] args) {
	try {
	    // Pre-Init
	    RetroRPGCS.preInit();
	    // Integrate with host platform
	    // Platform.hookLAF(Import2.PROGRAM_NAME);
	    RetroRPGCS.application = new Application();
	    RetroRPGCS.application.postConstruct();
	    Application.playLogoSound();
	    RetroRPGCS.application.getGUIManager().showGUI();
	    // Register platform hooks
	    // Platform.hookAbout(Import2.application.getAboutDialog(),
	    // Import2.application.getAboutDialog().getClass().getDeclaredMethod("showAboutDialog"));
	    // Platform.hookPreferences(PreferencesManager.class,
	    // PreferencesManager.class.getDeclaredMethod("showPrefs"));
	    // Platform.hookQuit(Import2.application.getGUIManager(),
	    // Import2.application.getGUIManager().getClass().getDeclaredMethod("quitHandler"));
	    // Platform.hookDockIcon(LogoManager.getLogo());
	    // Set up Common Dialogs
	    CommonDialogs.setDefaultTitle(RetroRPGCS.PROGRAM_NAME);
	    CommonDialogs.setIcon(Application.getMicroLogo());
	} catch (final Throwable t) {
	    RetroRPGCS.getErrorLogger().logError(t);
	}
    }
}
