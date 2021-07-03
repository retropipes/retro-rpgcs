/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.prefs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFrame;

import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.maze.Extension;

public class PreferencesManager {
    // Fields
    private static PreferencesStoreManager storeMgr = new PreferencesStoreManager();
    private static PreferencesGUIManager guiMgr = new PreferencesGUIManager();
    static final int MUSIC_ALL = 0;
    public static final int MUSIC_EXPLORING = 1;
    public static final int MUSIC_BATTLE = 2;
    static final int MUSIC_LENGTH = 3;
    public static final int DIFFICULTY_VERY_EASY = 0;
    public static final int DIFFICULTY_EASY = 1;
    public static final int DIFFICULTY_NORMAL = 2;
    public static final int DIFFICULTY_HARD = 3;
    public static final int DIFFICULTY_VERY_HARD = 4;
    private static final int DEFAULT_DIFFICULTY = PreferencesManager.DIFFICULTY_NORMAL;
    private static final String DIR = "Preferences";
    private static final String MAC_FILE = "com.puttysoftware.retrorpgcs";
    private static final String WIN_FILE = "RetroRPGCSPreferences";
    private static final String UNIX_FILE = "RetroRPGCSPreferences";
    private static final String SOUNDS_SETTING = "SoundsEnabled";
    private static final String WINDOW_SETTING = "ViewingWindowSize";
    private static final String UPDATE_SETTING = "UpdatesStartup";
    private static final String MOVE_SETTING = "OneMove";
    private static final String BATTLE_SETTING = "UseMapBattleEngine";
    private static final String TIME_SETTING = "UseTimeBattleEngine";
    private static final String DIFFICULTY_SETTING = "GameDifficulty";
    private static final String MUSIC_SETTING = "MUSIC_";
    private static final String MUSIC_ALL_SETTING = "MUSIC_0";
    private static final int BATTLE_SPEED = 1000;

    // Methods
    public static int getBattleSpeed() {
        return PreferencesManager.BATTLE_SPEED;
    }

    public static int getGameDifficulty() {
        return PreferencesManager.storeMgr.getInteger(
                PreferencesManager.DIFFICULTY_SETTING,
                PreferencesManager.DEFAULT_DIFFICULTY);
    }

    public static boolean getMusicEnabled(final int mus) {
        if (!PreferencesManager.storeMgr
                .getBoolean(PreferencesManager.MUSIC_ALL_SETTING, false)) {
            return false;
        } else {
            return PreferencesManager.storeMgr
                    .getBoolean(PreferencesManager.MUSIC_SETTING + mus, true);
        }
    }

    public static JFrame getPrefFrame() {
        return PreferencesManager.guiMgr.getPrefFrame();
    }

    private static String getPrefsDirectory() {
        return PreferencesManager.DIR;
    }

    private static String getPrefsDirPrefix() {
        return RetroRPGCS.getSupportDirectory();
    }

    private static String getPrefsFile() {
        final var b = new StringBuilder();
        b.append(PreferencesManager.getPrefsDirPrefix());
        b.append(File.pathSeparator);
        b.append(PreferencesManager.getPrefsDirectory());
        b.append(File.pathSeparator);
        b.append(PreferencesManager.getPrefsFileName());
        b.append(File.pathSeparator);
        b.append(PreferencesManager.getPrefsFileExtension());
        return b.toString();
    }

    private static String getPrefsFileExtension() {
        return "." + Extension.getPreferencesExtension();
    }

    private static String getPrefsFileName() {
        final var osName = System.getProperty("os.name");
        if (osName.indexOf("Mac OS X") != -1) {
            // Mac OS X
            return PreferencesManager.MAC_FILE;
        } else if (osName.indexOf("Windows") != -1) {
            // Windows
            return PreferencesManager.WIN_FILE;
        } else {
            // Other - assume UNIX-like
            return PreferencesManager.UNIX_FILE;
        }
    }

    public static boolean getSoundsEnabled() {
        return PreferencesManager.storeMgr
                .getBoolean(PreferencesManager.SOUNDS_SETTING, true);
    }

    public static int getViewingWindowSize() {
        return PreferencesGUIManager.VIEWING_WINDOW_SIZES[PreferencesManager
                .getViewingWindowSizeIndex()];
    }

    static int getViewingWindowSizeIndex() {
        return PreferencesManager.storeMgr.getInteger(
                PreferencesManager.WINDOW_SETTING,
                PreferencesGUIManager.DEFAULT_SIZE_INDEX);
    }

    public static boolean oneMove() {
        return PreferencesManager.storeMgr
                .getBoolean(PreferencesManager.MOVE_SETTING, true);
    }

    static void readPrefs() {
        try (var bis = new BufferedInputStream(
                new FileInputStream(PreferencesManager.getPrefsFile()))) {
            // Read new preferences
            PreferencesManager.storeMgr.loadStore(bis);
        } catch (final IOException io) {
            // Populate store with defaults
            PreferencesManager.storeMgr
                    .setBoolean(PreferencesManager.UPDATE_SETTING, true);
            PreferencesManager.storeMgr
                    .setBoolean(PreferencesManager.MOVE_SETTING, true);
            for (var x = 0; x < PreferencesManager.MUSIC_LENGTH; x++) {
                PreferencesManager.storeMgr
                        .setBoolean(PreferencesManager.MUSIC_SETTING + x, true);
            }
            PreferencesManager.storeMgr.setInteger(
                    PreferencesManager.WINDOW_SETTING,
                    PreferencesGUIManager.DEFAULT_VIEWING_WINDOW_SIZE);
            PreferencesManager.storeMgr
                    .setBoolean(PreferencesManager.SOUNDS_SETTING, true);
            PreferencesManager.storeMgr
                    .setBoolean(PreferencesManager.BATTLE_SETTING, false);
            PreferencesManager.storeMgr
                    .setBoolean(PreferencesManager.TIME_SETTING, false);
            PreferencesManager.storeMgr.setInteger(
                    PreferencesManager.DIFFICULTY_SETTING,
                    PreferencesManager.DEFAULT_DIFFICULTY);
        }
    }

    static void setCheckUpdatesAtStartup(final boolean value) {
        PreferencesManager.storeMgr
                .setBoolean(PreferencesManager.UPDATE_SETTING, value);
    }

    static void setGameDifficulty(final int value) {
        PreferencesManager.storeMgr
                .setInteger(PreferencesManager.DIFFICULTY_SETTING, value);
    }

    static void setMapBattleEngine(final boolean value) {
        PreferencesManager.storeMgr
                .setBoolean(PreferencesManager.BATTLE_SETTING, value);
    }

    static void setMusicEnabled(final int mus, final boolean status) {
        PreferencesManager.storeMgr
                .setBoolean(PreferencesManager.MUSIC_SETTING + mus, status);
    }

    static void setOneMove(final boolean value) {
        PreferencesManager.storeMgr.setBoolean(PreferencesManager.MOVE_SETTING,
                value);
    }

    public static void setSoundsEnabled(final boolean value) {
        PreferencesManager.storeMgr
                .setBoolean(PreferencesManager.SOUNDS_SETTING, value);
    }

    static void setTimeBattleEngine(final boolean value) {
        PreferencesManager.storeMgr.setBoolean(PreferencesManager.TIME_SETTING,
                value);
    }

    static void setViewingWindowSizeIndex(final int value) {
        PreferencesManager.storeMgr
                .setInteger(PreferencesManager.WINDOW_SETTING, value);
    }

    public static boolean shouldCheckUpdatesAtStartup() {
        return PreferencesManager.storeMgr
                .getBoolean(PreferencesManager.UPDATE_SETTING, true);
    }

    public static void showPrefs() {
        PreferencesManager.guiMgr.showPrefs();
    }

    public static boolean useMapBattleEngine() {
        return PreferencesManager.storeMgr
                .getBoolean(PreferencesManager.BATTLE_SETTING, false);
    }

    public static boolean useTimeBattleEngine() {
        return PreferencesManager.storeMgr
                .getBoolean(PreferencesManager.TIME_SETTING, false);
    }

    public static void writePrefs() {
        try (var bos = new BufferedOutputStream(
                new FileOutputStream(PreferencesManager.getPrefsFile()))) {
            PreferencesManager.storeMgr.saveStore(bos);
        } catch (final IOException io) {
            // Ignore
        }
    }

    // Private constructor
    private PreferencesManager() {
        // Do nothing
    }
}
