/* RetroRPGCS: An RPG */
package com.puttysoftware.retrorpgcs;

import java.awt.Image;
import java.net.MalformedURLException;

import javax.swing.JFrame;

import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.gameshell.ExceptionMessageConfiguration;
import com.puttysoftware.gameshell.GameShell;
import com.puttysoftware.images.BufferedImageIcon;
import com.puttysoftware.retrorpgcs.battle.Battle;
import com.puttysoftware.retrorpgcs.battle.map.time.MapTimeBattleLogic;
import com.puttysoftware.retrorpgcs.battle.map.turn.MapTurnBattleLogic;
import com.puttysoftware.retrorpgcs.battle.window.time.WindowTimeBattleLogic;
import com.puttysoftware.retrorpgcs.battle.window.turn.WindowTurnBattleLogic;
import com.puttysoftware.retrorpgcs.creatures.Creature;
import com.puttysoftware.retrorpgcs.game.GameLogicManager;
import com.puttysoftware.retrorpgcs.maze.MazeManager;
import com.puttysoftware.retrorpgcs.maze.utilities.MazeObjectList;
import com.puttysoftware.retrorpgcs.prefs.PreferencesManager;
import com.puttysoftware.retrorpgcs.resourcemanagers.LogoManager;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundConstants;
import com.puttysoftware.retrorpgcs.resourcemanagers.SoundManager;
import com.puttysoftware.retrorpgcs.security.SandboxManager;
import com.puttysoftware.retrorpgcs.shops.Shop;
import com.puttysoftware.retrorpgcs.shops.ShopTypes;
import com.puttysoftware.updater.ProductData;

public final class RetroRPGCS extends GameShell {
    // Constants
    private static final int BATTLE_MAZE_SIZE = 16;
    private static final ExceptionMessageConfiguration errorConfig = new ExceptionMessageConfiguration(
            RetroRPGCS.ERROR_MESSAGE, RetroRPGCS.ERROR_TITLE, true);
    private static final ExceptionMessageConfiguration warningConfig = new ExceptionMessageConfiguration(
            RetroRPGCS.WARNING_MESSAGE, RetroRPGCS.WARNING_TITLE, true);
    private static final String PROGRAM_NAME = "RetroRPGCS";
    private static final String ERROR_MESSAGE = "Perhaps a bug is to blame for this error message.\n"
            + "Include the error log with your bug report.\n"
            + "Email bug reports to: products@puttysoftware.com\n"
            + "Subject: RetroRPGCS Bug Report";
    private static final String ERROR_TITLE = "RetroRPGCS Error";
    private static final String WARNING_MESSAGE = "WARNING: Something has gone wrong. The details have been logged.";
    private static final String WARNING_TITLE = "RetroRPGCS Warning";
    private static final String UPDATE_SITE = "https://update.puttysoftware.com/retrorpgcs/";
    private static final String NEW_VERSION_SITE = "https://www.puttysoftware.com/retrorpgcs/";
    private static ProductData pd;
    private static final int VERSION_MAJOR = 5;
    private static final int VERSION_MINOR = 1;
    private static final int VERSION_BUGFIX = 0;
    private static final int VERSION_CODE = ProductData.CODE_BETA;
    private static final int VERSION_PRERELEASE = 2;
    public static final int STATUS_GUI = 0;
    public static final int STATUS_GAME = 1;
    public static final int STATUS_BATTLE = 2;
    public static final int STATUS_PREFS = 3;
    public static final int STATUS_NULL = 4;
    // Instance
    private static RetroRPGCS instance;

    public static int getBattleMazeSize() {
        return RetroRPGCS.BATTLE_MAZE_SIZE;
    }

    public static String getCachesDirectory() {
        return SandboxManager.getSandboxManager().getCachesDirectory();
    }

    public static String getDocumentsDirectory() {
        return SandboxManager.getSandboxManager().getDocumentsDirectory();
    }

    public static Image getIconLogo() {
        return LogoManager.getIconLogo();
    }

    public static RetroRPGCS getInstance() {
        return RetroRPGCS.instance;
    }

    public static BufferedImageIcon getMicroLogo() {
        return LogoManager.getMicroLogo();
    }

    public static String getSupportDirectory() {
        return SandboxManager.getSandboxManager().getSupportDirectory();
    }

    private static String getVersionString() {
        final var code = RetroRPGCS.pd.getCodeVersion();
        String rt;
        if (code < ProductData.CODE_STABLE) {
            rt = "-beta" + RetroRPGCS.VERSION_PRERELEASE;
        } else {
            rt = "";
        }
        return RetroRPGCS.VERSION_MAJOR + "." + RetroRPGCS.VERSION_MINOR + "."
                + RetroRPGCS.VERSION_BUGFIX + rt;
    }

    public static void main(final String[] args) {
        try {
            // Integrate with host platform
            // Platform.hookLAF(RetroRPGCS.PROGRAM_NAME);
            RetroRPGCS.instance = new RetroRPGCS();
            RetroRPGCS.instance.postConstruct();
            RetroRPGCS.playLogoSound();
            RetroRPGCS.instance.getGUIManager().showGUI();
            // Register platform hooks
            // Platform.hookAbout(RetroRPGCS.application.getAboutDialog(),
            // RetroRPGCS.application.getAboutDialog().getClass().getDeclaredMethod("showAboutDialog"));
            // Platform.hookPreferences(PreferencesManager.class,
            // PreferencesManager.class.getDeclaredMethod("showPrefs"));
            // Platform.hookQuit(RetroRPGCS.application.getGUIManager(),
            // RetroRPGCS.application.getGUIManager().getClass().getDeclaredMethod("quitHandler"));
            // Platform.hookDockIcon(LogoManager.getLogo());
            // Set up Common Dialogs
            CommonDialogs.setDefaultTitle(RetroRPGCS.PROGRAM_NAME);
            CommonDialogs.setIcon(RetroRPGCS.getMicroLogo());
        } catch (final Throwable t) {
            if (RetroRPGCS.instance != null) {
                RetroRPGCS.instance.handleError(t);
            } else {
                t.printStackTrace();
                System.exit(2);
            }
        }
    }

    public static void playLogoSound() {
        SoundManager.playSound(SoundConstants.SOUND_LOGO);
    }

    // Fields
    private AboutDialog about;
    private GameLogicManager gameMgr;
    private MazeManager mazeMgr;
    private MenuManager menuMgr;
    private ObjectHelpManager oHelpMgr;
    private GUIManager guiMgr;
    private final MazeObjectList objects;
    private Shop weapons, armor, healer, bank, regenerator, spells, items,
            socks, enhancements, faiths;
    private WindowTurnBattleLogic windowTurnBattle;
    private WindowTimeBattleLogic windowTimeBattle;
    private MapTurnBattleLogic mapTurnBattle;
    private MapTimeBattleLogic mapTimeBattle;
    private int currentMode;
    private int formerMode;

    // Constructor
    public RetroRPGCS() {
        super(RetroRPGCS.PROGRAM_NAME, RetroRPGCS.errorConfig,
                RetroRPGCS.warningConfig);
        Creature.computeActionCap(RetroRPGCS.BATTLE_MAZE_SIZE,
                RetroRPGCS.BATTLE_MAZE_SIZE);
        this.objects = new MazeObjectList();
        this.currentMode = RetroRPGCS.STATUS_NULL;
        this.formerMode = RetroRPGCS.STATUS_NULL;
    }

    public AboutDialog getAboutDialog() {
        return this.about;
    }

    public Battle getBattle() {
        if (PreferencesManager.useMapBattleEngine()) {
            if (PreferencesManager.useTimeBattleEngine()) {
                return this.mapTimeBattle;
            } else {
                return this.mapTurnBattle;
            }
        } else if (PreferencesManager.useTimeBattleEngine()) {
            return this.windowTimeBattle;
        } else {
            return this.windowTurnBattle;
        }
    }

    public int getFormerMode() {
        return this.formerMode;
    }

    public GameLogicManager getGameManager() {
        if (this.gameMgr == null) {
            this.gameMgr = new GameLogicManager();
        }
        return this.gameMgr;
    }

    public Shop getGenericShop(final int shopType) {
        this.getGameManager().stopMovement();
        switch (shopType) {
        case ShopTypes.SHOP_TYPE_ARMOR:
            return this.armor;
        case ShopTypes.SHOP_TYPE_BANK:
            return this.bank;
        case ShopTypes.SHOP_TYPE_ENHANCEMENTS:
            return this.enhancements;
        case ShopTypes.SHOP_TYPE_FAITH_POWERS:
            return this.faiths;
        case ShopTypes.SHOP_TYPE_HEALER:
            return this.healer;
        case ShopTypes.SHOP_TYPE_ITEMS:
            return this.items;
        case ShopTypes.SHOP_TYPE_REGENERATOR:
            return this.regenerator;
        case ShopTypes.SHOP_TYPE_SOCKS:
            return this.socks;
        case ShopTypes.SHOP_TYPE_SPELLS:
            return this.spells;
        case ShopTypes.SHOP_TYPE_WEAPONS:
            return this.weapons;
        default:
            // Invalid shop type
            return null;
        }
    }

    public GUIManager getGUIManager() {
        return this.guiMgr;
    }

    public MazeManager getMazeManager() {
        if (this.mazeMgr == null) {
            this.mazeMgr = new MazeManager();
        }
        return this.mazeMgr;
    }

    @Override
    public MenuManager getMenus() {
        return this.menuMgr;
    }

    public int getMode() {
        return this.currentMode;
    }

    public ObjectHelpManager getObjectHelpManager() {
        return this.oHelpMgr;
    }

    public MazeObjectList getObjects() {
        return this.objects;
    }

    public JFrame getOutputFrame() {
        try {
            if (this.getMode() == RetroRPGCS.STATUS_PREFS) {
                return PreferencesManager.getPrefFrame();
            } else if (this.getMode() == RetroRPGCS.STATUS_GUI) {
                return this.getGUIManager().getGUIFrame();
            } else if (this.getMode() == RetroRPGCS.STATUS_GAME) {
                return this.getGameManager().getOutputFrame();
            } else if (this.getMode() == RetroRPGCS.STATUS_BATTLE) {
                return this.getBattle().getOutputFrame();
            } else {
                return null;
            }
        } catch (final NullPointerException npe) {
            return null;
        }
    }

    public boolean modeChanged() {
        return this.formerMode != this.currentMode;
    }

    // Methods
    void postConstruct() {
        // Create product data
        try {
            RetroRPGCS.pd = new ProductData(RetroRPGCS.UPDATE_SITE,
                    RetroRPGCS.NEW_VERSION_SITE, RetroRPGCS.VERSION_MAJOR,
                    RetroRPGCS.VERSION_MINOR, RetroRPGCS.VERSION_BUGFIX,
                    RetroRPGCS.VERSION_CODE, RetroRPGCS.VERSION_PRERELEASE);
        } catch (final MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Create Managers
        this.about = new AboutDialog(RetroRPGCS.getVersionString());
        this.guiMgr = new GUIManager();
        this.menuMgr = new MenuManager();
        this.oHelpMgr = new ObjectHelpManager();
        this.windowTurnBattle = new WindowTurnBattleLogic();
        this.windowTimeBattle = new WindowTimeBattleLogic();
        this.mapTurnBattle = new MapTurnBattleLogic();
        this.mapTimeBattle = new MapTimeBattleLogic();
        this.weapons = new Shop(ShopTypes.SHOP_TYPE_WEAPONS);
        this.armor = new Shop(ShopTypes.SHOP_TYPE_ARMOR);
        this.healer = new Shop(ShopTypes.SHOP_TYPE_HEALER);
        this.bank = new Shop(ShopTypes.SHOP_TYPE_BANK);
        this.regenerator = new Shop(ShopTypes.SHOP_TYPE_REGENERATOR);
        this.spells = new Shop(ShopTypes.SHOP_TYPE_SPELLS);
        this.items = new Shop(ShopTypes.SHOP_TYPE_ITEMS);
        this.socks = new Shop(ShopTypes.SHOP_TYPE_SOCKS);
        this.enhancements = new Shop(ShopTypes.SHOP_TYPE_ENHANCEMENTS);
        this.faiths = new Shop(ShopTypes.SHOP_TYPE_FAITH_POWERS);
        // Cache Logo
        this.guiMgr.updateLogo();
    }

    public void resetBattleGUI() {
        this.mapTimeBattle.resetGUI();
        this.windowTimeBattle.resetGUI();
        this.mapTurnBattle.resetGUI();
        this.windowTurnBattle.resetGUI();
    }

    public void restoreFormerMode() {
        this.currentMode = this.formerMode;
    }

    public void saveFormerMode() {
        this.formerMode = this.currentMode;
    }

    public void setMode(final int newMode) {
        this.formerMode = this.currentMode;
        this.currentMode = newMode;
    }

    public void showMessage(final String msg) {
        if (this.currentMode == RetroRPGCS.STATUS_GAME) {
            this.getGameManager().setStatusMessage(msg);
        } else if (this.currentMode == RetroRPGCS.STATUS_BATTLE) {
            this.getBattle().setStatusMessage(msg);
        } else {
            CommonDialogs.showDialog(msg);
        }
    }
}
