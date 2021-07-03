/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs.prefs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.resourcemanagers.LogoManager;

class PreferencesGUIManager {
    private class EventHandler
            implements ActionListener, ItemListener, WindowListener {
        EventHandler() {
            // Do nothing
        }

        // Handle buttons
        @Override
        public void actionPerformed(final ActionEvent e) {
            try {
                final var pm = PreferencesGUIManager.this;
                final var cmd = e.getActionCommand();
                if (cmd.equals("OK")) {
                    pm.setPrefs();
                } else if (cmd.equals("Cancel")) {
                    pm.hidePrefs();
                }
            } catch (final Exception ex) {
                RetroRPGCS.getInstance().handleError(ex);
            }
        }

        @Override
        public void itemStateChanged(final ItemEvent e) {
            try {
                final var pm = PreferencesGUIManager.this;
                final var o = e.getItem();
                if (o.getClass().equals(JCheckBox.class)) {
                    final var check = (JCheckBox) o;
                    if (check.equals(pm.music[PreferencesManager.MUSIC_ALL])) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            for (var x = 1; x < PreferencesManager.MUSIC_LENGTH; x++) {
                                pm.music[x].setEnabled(true);
                            }
                        } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                            for (var x = 1; x < PreferencesManager.MUSIC_LENGTH; x++) {
                                pm.music[x].setEnabled(false);
                            }
                        }
                    }
                }
            } catch (final Exception ex) {
                RetroRPGCS.getInstance().handleError(ex);
            }
        }

        @Override
        public void windowActivated(final WindowEvent e) {
            // Do nothing
        }

        @Override
        public void windowClosed(final WindowEvent e) {
            // Do nothing
        }

        @Override
        public void windowClosing(final WindowEvent e) {
            final var pm = PreferencesGUIManager.this;
            pm.hidePrefs();
        }

        @Override
        public void windowDeactivated(final WindowEvent e) {
            // Do nothing
        }

        @Override
        public void windowDeiconified(final WindowEvent e) {
            // Do nothing
        }

        @Override
        public void windowIconified(final WindowEvent e) {
            // Do nothing
        }

        @Override
        public void windowOpened(final WindowEvent e) {
            // Do nothing
        }
    }

    static final int[] VIEWING_WINDOW_SIZES = new int[] { 7, 9, 11, 13, 15, 17,
            19, 21, 23, 25 };
    private static final int[] VIEWING_WINDOW_TRIGGERS = new int[] { 0, 0, 0,
            950, 1075, 1200, 1325, 1450, 1575, 1700 };
    static final int DEFAULT_SIZE_INDEX = 2;
    static final int DEFAULT_VIEWING_WINDOW_SIZE = PreferencesGUIManager.VIEWING_WINDOW_SIZES[PreferencesGUIManager.DEFAULT_SIZE_INDEX];
    private static final String[] VIEWING_WINDOW_SIZE_NAMES = new String[] {
            "Tiny", "Small", "Medium", "Large", "Huge", "Tiny HD", "Small HD",
            "Medium HD", "Large HD", "Huge HD" };
    private static final String[] DIFFICULTY_NAMES = new String[] { "Very Easy",
            "Easy", "Normal", "Hard", "Very Hard" };
    private static final int GRID_LENGTH = 11;
    // Fields
    private JFrame prefFrame;
    JCheckBox[] music;
    private JCheckBox sound;
    private JCheckBox checkUpdatesStartup;
    private JCheckBox moveOneAtATime;
    private JCheckBox useMapBattleEngine;
    private JCheckBox useTimeBattleEngine;
    private ButtonGroup viewingWindowGroup;
    private JRadioButton[] viewingWindowChoices;
    private JComboBox<String> difficultyPicker;

    // Constructors
    public PreferencesGUIManager() {
        this.music = new JCheckBox[PreferencesManager.MUSIC_LENGTH];
        this.setUpGUI();
        this.setDefaultPrefs();
    }

    // Methods
    public JFrame getPrefFrame() {
        if (this.prefFrame != null && this.prefFrame.isVisible()) {
            return this.prefFrame;
        } else {
            return null;
        }
    }

    public void hidePrefs() {
        final var app = RetroRPGCS.getInstance();
        this.prefFrame.setVisible(false);
        PreferencesManager.writePrefs();
        final var formerMode = app.getFormerMode();
        if (formerMode == RetroRPGCS.STATUS_GUI) {
            app.getGUIManager().showGUI();
        } else if (formerMode == RetroRPGCS.STATUS_GAME) {
            app.getGameManager().showOutput();
        }
    }

    private void loadPrefs() {
        for (var x = 0; x < PreferencesManager.MUSIC_LENGTH; x++) {
            this.music[x].setSelected(PreferencesManager.getMusicEnabled(x));
        }
        this.checkUpdatesStartup
                .setSelected(PreferencesManager.shouldCheckUpdatesAtStartup());
        this.moveOneAtATime.setSelected(PreferencesManager.oneMove());
        try {
            this.viewingWindowChoices[PreferencesManager
                    .getViewingWindowSizeIndex()].setSelected(true);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            this.viewingWindowChoices[PreferencesGUIManager.DEFAULT_SIZE_INDEX]
                    .setSelected(true);
            PreferencesManager.setViewingWindowSizeIndex(
                    PreferencesGUIManager.DEFAULT_SIZE_INDEX);
            PreferencesManager.writePrefs();
        }
        this.sound.setSelected(PreferencesManager.getSoundsEnabled());
        this.useMapBattleEngine
                .setSelected(PreferencesManager.useMapBattleEngine());
        this.useTimeBattleEngine
                .setSelected(PreferencesManager.useTimeBattleEngine());
        this.difficultyPicker
                .setSelectedIndex(PreferencesManager.getGameDifficulty());
    }

    public final void setDefaultPrefs() {
        PreferencesManager.readPrefs();
        this.loadPrefs();
    }

    public void setPrefs() {
        for (var x = 0; x < PreferencesManager.MUSIC_LENGTH; x++) {
            PreferencesManager.setMusicEnabled(x, this.music[x].isSelected());
        }
        PreferencesManager.setCheckUpdatesAtStartup(
                this.checkUpdatesStartup.isSelected());
        PreferencesManager.setOneMove(this.moveOneAtATime.isSelected());
        final var vwSize = PreferencesManager.getViewingWindowSizeIndex();
        final var newSize = this.viewingWindowGroup.getSelection()
                .getMnemonic();
        PreferencesManager.setViewingWindowSizeIndex(newSize);
        if (vwSize != newSize) {
            RetroRPGCS.getInstance().getGameManager()
                    .viewingWindowSizeChanged();
            RetroRPGCS.getInstance().resetBattleGUI();
        }
        PreferencesManager.setSoundsEnabled(this.sound.isSelected());
        PreferencesManager
                .setMapBattleEngine(this.useMapBattleEngine.isSelected());
        PreferencesManager
                .setTimeBattleEngine(this.useTimeBattleEngine.isSelected());
        PreferencesManager
                .setGameDifficulty(this.difficultyPicker.getSelectedIndex());
        this.hidePrefs();
    }

    private void setUpGUI() {
        final var handler = new EventHandler();
        this.prefFrame = new JFrame("Preferences");
        final Image iconlogo = LogoManager.getIconLogo();
        this.prefFrame.setIconImage(iconlogo);
        final var prefTabPane = new JTabbedPane();
        final var mainPrefPane = new Container();
        final var mediaPane = new Container();
        final var miscPane = new Container();
        final var viewPane = new Container();
        prefTabPane.setOpaque(true);
        final var buttonPane = new Container();
        final var prefsOK = new JButton("OK");
        prefsOK.setDefaultCapable(true);
        this.prefFrame.getRootPane().setDefaultButton(prefsOK);
        final var prefsCancel = new JButton("Cancel");
        prefsCancel.setDefaultCapable(false);
        this.viewingWindowGroup = new ButtonGroup();
        this.viewingWindowChoices = new JRadioButton[PreferencesGUIManager.VIEWING_WINDOW_TRIGGERS.length];
        final var ss = Toolkit.getDefaultToolkit().getScreenSize();
        final var minSS = Math.min(ss.width, ss.height);
        for (var z = 0; z < PreferencesGUIManager.VIEWING_WINDOW_TRIGGERS.length; z++) {
            this.viewingWindowChoices[z] = new JRadioButton(
                    PreferencesGUIManager.VIEWING_WINDOW_SIZE_NAMES[z]);
            this.viewingWindowChoices[z].setMnemonic(z);
            this.viewingWindowGroup.add(this.viewingWindowChoices[z]);
            if (minSS < PreferencesGUIManager.VIEWING_WINDOW_TRIGGERS[z]) {
                this.viewingWindowChoices[z].setEnabled(false);
            }
        }
        this.music[PreferencesManager.MUSIC_ALL] = new JCheckBox(
                "Enable ALL music", true);
        this.music[PreferencesManager.MUSIC_EXPLORING] = new JCheckBox(
                "Enable exploring music", true);
        this.music[PreferencesManager.MUSIC_BATTLE] = new JCheckBox(
                "Enable battle music", true);
        this.sound = new JCheckBox("Enable sounds", true);
        this.checkUpdatesStartup = new JCheckBox("Check for Updates at Startup",
                true);
        this.moveOneAtATime = new JCheckBox("One Move at a Time", true);
        this.useMapBattleEngine = new JCheckBox("Use Map Battle Engine", false);
        this.useTimeBattleEngine = new JCheckBox("Use Time Battle Engine",
                false);
        this.difficultyPicker = new JComboBox<>(
                PreferencesGUIManager.DIFFICULTY_NAMES);
        this.prefFrame.setContentPane(mainPrefPane);
        this.prefFrame
                .setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.prefFrame.addWindowListener(handler);
        mainPrefPane.setLayout(new BorderLayout());
        this.prefFrame.setResizable(false);
        mediaPane.setLayout(
                new GridLayout(PreferencesGUIManager.GRID_LENGTH, 1));
        for (var x = 0; x < PreferencesManager.MUSIC_LENGTH; x++) {
            mediaPane.add(this.music[x]);
        }
        mediaPane.add(this.sound);
        miscPane.setLayout(
                new GridLayout(PreferencesGUIManager.GRID_LENGTH, 1));
        miscPane.add(this.checkUpdatesStartup);
        miscPane.add(this.moveOneAtATime);
        miscPane.add(this.useMapBattleEngine);
        miscPane.add(this.useTimeBattleEngine);
        miscPane.add(new JLabel("Game Difficulty"));
        miscPane.add(this.difficultyPicker);
        viewPane.setLayout(
                new GridLayout(PreferencesGUIManager.GRID_LENGTH, 1));
        viewPane.add(new JLabel("Viewing Window Size"));
        for (var z = 0; z < PreferencesGUIManager.VIEWING_WINDOW_TRIGGERS.length; z++) {
            viewPane.add(this.viewingWindowChoices[z]);
        }
        buttonPane.setLayout(new FlowLayout());
        buttonPane.add(prefsOK);
        buttonPane.add(prefsCancel);
        prefTabPane.addTab("Media", null, mediaPane, "Media");
        prefTabPane.addTab("View", null, viewPane, "View");
        prefTabPane.addTab("Misc.", null, miscPane, "Misc.");
        mainPrefPane.add(prefTabPane, BorderLayout.CENTER);
        mainPrefPane.add(buttonPane, BorderLayout.SOUTH);
        this.music[PreferencesManager.MUSIC_ALL].addItemListener(handler);
        prefsOK.addActionListener(handler);
        prefsCancel.addActionListener(handler);
        this.prefFrame.pack();
    }

    public void showPrefs() {
        final var app = RetroRPGCS.getInstance();
        if (app.getMode() == RetroRPGCS.STATUS_BATTLE) {
            // Deny
            CommonDialogs.showTitledDialog(
                    "Preferences may NOT be changed in the middle of battle!",
                    "Battle");
        } else {
            app.setMode(RetroRPGCS.STATUS_PREFS);
            if (System.getProperty("os.name").startsWith("Mac OS X")) {
                this.prefFrame.setJMenuBar(app.getMenus().getMainMenuBar());
            }
            app.getMenus().setPrefMenus();
            this.prefFrame.setVisible(true);
            final var formerMode = app.getFormerMode();
            app.restoreFormerMode();
            if (formerMode == RetroRPGCS.STATUS_GUI) {
                app.getGUIManager().hideGUITemporarily();
            } else if (formerMode == RetroRPGCS.STATUS_GAME) {
                app.getGameManager().hideOutput();
            }
        }
    }
}
