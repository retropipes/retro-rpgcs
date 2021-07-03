/* Import2: An RPG */
package com.puttysoftware.retrorpgcs.battle.map.time;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import com.puttysoftware.diane.gui.CommonDialogs;
import com.puttysoftware.images.BufferedImageIcon;
import com.puttysoftware.retrorpgcs.RetroRPGCS;
import com.puttysoftware.retrorpgcs.DrawGrid;
import com.puttysoftware.retrorpgcs.ai.map.MapAI;
import com.puttysoftware.retrorpgcs.battle.Battle;
import com.puttysoftware.retrorpgcs.battle.map.MapBattleDraw;
import com.puttysoftware.retrorpgcs.battle.map.MapBattleEffects;
import com.puttysoftware.retrorpgcs.battle.map.MapBattleViewingWindowManager;
import com.puttysoftware.retrorpgcs.maze.Maze;
import com.puttysoftware.retrorpgcs.maze.MazeConstants;
import com.puttysoftware.retrorpgcs.maze.abc.AbstractMazeObject;
import com.puttysoftware.retrorpgcs.maze.objects.BattleCharacter;
import com.puttysoftware.retrorpgcs.maze.objects.Darkness;
import com.puttysoftware.retrorpgcs.maze.objects.EmptyVoid;
import com.puttysoftware.retrorpgcs.prefs.PreferencesManager;
import com.puttysoftware.retrorpgcs.resourcemanagers.BattleImageManager;
import com.puttysoftware.retrorpgcs.resourcemanagers.ImageTransformer;

class MapTimeBattleGUI {
    // Fields
    private JFrame battleFrame;
    private MapBattleDraw battlePane;
    private JLabel messageLabel;
    private JProgressBar myActionBar, enemyActionBar;
    private final MapBattleViewingWindowManager vwMgr;
    private final MapTimeBattleStats bs;
    private final MapBattleEffects be;
    private DrawGrid drawGrid;
    boolean eventHandlersOn;
    private JButton spell, steal, drain, item;
    private static final int MAX_TEXT = 1000;

    // Constructors
    MapTimeBattleGUI() {
	this.vwMgr = new MapBattleViewingWindowManager();
	this.bs = new MapTimeBattleStats();
	this.be = new MapBattleEffects();
	this.setUpGUI();
	this.eventHandlersOn = true;
    }

    // Methods
    JFrame getOutputFrame() {
	return this.battleFrame;
    }

    boolean isPlayerActionBarFull() {
	return this.myActionBar.getValue() == this.myActionBar.getMaximum();
    }

    boolean isEnemyActionBarFull() {
	return this.enemyActionBar.getValue() == this.enemyActionBar.getMaximum();
    }

    void setMaxPlayerActionBarValue(final int max) {
	this.myActionBar.setValue(0);
	this.myActionBar.setMaximum(max);
    }

    void setMaxEnemyActionBarValue(final int max) {
	this.enemyActionBar.setValue(0);
	this.enemyActionBar.setMaximum(max);
    }

    void updatePlayerActionBarValue() {
	this.myActionBar.setValue(this.myActionBar.getValue() + 1);
    }

    void updateEnemyActionBarValue() {
	this.enemyActionBar.setValue(this.enemyActionBar.getValue() + 1);
    }

    void resetPlayerActionBar() {
	this.myActionBar.setValue(0);
    }

    void resetEnemyActionBar() {
	this.enemyActionBar.setValue(0);
    }

    MapBattleViewingWindowManager getViewManager() {
	return this.vwMgr;
    }

    void clearStatusMessage() {
	this.messageLabel.setText(" ");
    }

    void setStatusMessage(final String msg) {
	if (this.messageLabel.getText().length() > MapTimeBattleGUI.MAX_TEXT) {
	    this.clearStatusMessage();
	}
	if (!msg.isEmpty() && !msg.matches("\\s+")) {
	    this.messageLabel.setText(msg);
	}
    }

    void showBattle() {
	this.battleFrame.setVisible(true);
	this.battleFrame.setJMenuBar(RetroRPGCS.getInstance().getMenus().getMainMenuBar());
    }

    void hideBattle() {
	if (this.battleFrame != null) {
	    this.battleFrame.setVisible(false);
	}
    }

    void redrawBattle(final Maze battleMaze) {
	// Draw the battle, if it is visible
	if (this.battleFrame.isVisible()) {
	    int x, y;
	    int xFix, yFix;
	    final int xView = this.vwMgr.getViewingWindowLocationX();
	    final int yView = this.vwMgr.getViewingWindowLocationY();
	    final int xlView = this.vwMgr.getLowerRightViewingWindowLocationX();
	    final int ylView = this.vwMgr.getLowerRightViewingWindowLocationY();
	    for (x = xView; x <= xlView; x++) {
		for (y = yView; y <= ylView; y++) {
		    xFix = x - xView;
		    yFix = y - yView;
		    try {
			final BufferedImageIcon icon1 = battleMaze.getCell(y, x, 0, MazeConstants.LAYER_GROUND)
				.battleRenderHook();
			final BufferedImageIcon icon2 = battleMaze.getCell(y, x, 0, MazeConstants.LAYER_OBJECT)
				.battleRenderHook();
			this.drawGrid.setImageCell(
				ImageTransformer.getCompositeImage(icon1, icon2, BattleImageManager.getGraphicSize()),
				xFix, yFix);
		    } catch (final ArrayIndexOutOfBoundsException ae) {
			final EmptyVoid ev = new EmptyVoid();
			this.drawGrid.setImageCell(ev.battleRenderHook(), xFix, yFix);
		    } catch (final NullPointerException np) {
			final EmptyVoid ev = new EmptyVoid();
			this.drawGrid.setImageCell(ev.battleRenderHook(), xFix, yFix);
		    }
		}
	    }
	    this.battlePane.repaint();
	    this.battleFrame.pack();
	}
    }

    void redrawOneBattleSquare(final Maze battleMaze, final int x, final int y, final AbstractMazeObject obj3) {
	// Draw the battle, if it is visible
	if (this.battleFrame.isVisible()) {
	    try {
		int xFix, yFix;
		final int xView = this.vwMgr.getViewingWindowLocationX();
		final int yView = this.vwMgr.getViewingWindowLocationY();
		xFix = y - xView;
		yFix = x - yView;
		final BufferedImageIcon icon1 = battleMaze.getCell(x, y, 0, MazeConstants.LAYER_GROUND)
			.battleRenderHook();
		final BufferedImageIcon icon2 = battleMaze.getCell(x, y, 0, MazeConstants.LAYER_OBJECT)
			.battleRenderHook();
		final BufferedImageIcon icon3 = obj3.battleRenderHook();
		this.drawGrid.setImageCell(ImageTransformer.getVirtualCompositeImage(icon1, icon2, icon3,
			BattleImageManager.getGraphicSize()), xFix, yFix);
		this.battlePane.repaint();
	    } catch (final ArrayIndexOutOfBoundsException ae) {
		// Do nothing
	    } catch (final NullPointerException np) {
		// Do nothing
	    }
	    this.battleFrame.pack();
	}
    }

    void updateStatsAndEffects(final BattleCharacter active) {
	this.bs.updateStats(active);
	this.be.updateEffects(active);
    }

    private void setUpGUI() {
	final EventHandler handler = new EventHandler();
	final Container borderPane = new Container();
	final Container buttonPane = new Container();
	final Container effectBarPane = new Container();
	final Container barPane = new Container();
	borderPane.setLayout(new BorderLayout());
	barPane.setLayout(new FlowLayout());
	effectBarPane.setLayout(new BorderLayout());
	this.myActionBar = new JProgressBar(0);
	this.enemyActionBar = new JProgressBar(0);
	barPane.add(this.myActionBar);
	barPane.add(this.enemyActionBar);
	effectBarPane.add(barPane, BorderLayout.NORTH);
	effectBarPane.add(this.be.getEffectsPane(), BorderLayout.CENTER);
	this.messageLabel = new JLabel(" ");
	this.messageLabel.setOpaque(true);
	this.battleFrame = new JFrame("Battle");
	this.battleFrame.setContentPane(borderPane);
	this.spell = new JButton("Cast Spell");
	this.steal = new JButton("Steal");
	this.drain = new JButton("Drain");
	this.item = new JButton("Use Item");
	buttonPane.setLayout(new GridLayout(4, 1));
	buttonPane.add(this.spell);
	buttonPane.add(this.steal);
	buttonPane.add(this.drain);
	buttonPane.add(this.item);
	this.spell.setFocusable(false);
	this.steal.setFocusable(false);
	this.drain.setFocusable(false);
	this.item.setFocusable(false);
	this.spell.addActionListener(handler);
	this.steal.addActionListener(handler);
	this.drain.addActionListener(handler);
	this.item.addActionListener(handler);
	int modKey;
	if (System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
	    modKey = InputEvent.META_DOWN_MASK;
	} else {
	    modKey = InputEvent.CTRL_DOWN_MASK;
	}
	this.spell.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, modKey),
		"Cast Spell");
	this.spell.getActionMap().put("Cast Spell", handler);
	this.steal.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_T, modKey),
		"Steal");
	this.steal.getActionMap().put("Steal", handler);
	this.drain.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, modKey),
		"Drain");
	this.drain.getActionMap().put("Drain", handler);
	this.item.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_I, modKey),
		"Use Item");
	this.item.getActionMap().put("Use Item", handler);
	// Platform.hookFrameIcon(this.battleFrame, LogoManager.getIconLogo());
	this.battleFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	this.battleFrame.setResizable(false);
	this.drawGrid = new DrawGrid(MapBattleViewingWindowManager.getViewingWindowSize());
	for (int x = 0; x < MapBattleViewingWindowManager.getViewingWindowSize(); x++) {
	    for (int y = 0; y < MapBattleViewingWindowManager.getViewingWindowSize(); y++) {
		final AbstractMazeObject dark = new Darkness().gameRenderHook(y, x, 0);
		this.drawGrid.setImageCell(BattleImageManager.getImage(dark.getName(), dark.getGameBaseID(),
			AbstractMazeObject.getTemplateColor()), x, y);
	    }
	}
	this.battlePane = new MapBattleDraw(this.drawGrid);
	borderPane.add(this.battlePane, BorderLayout.CENTER);
	borderPane.add(buttonPane, BorderLayout.WEST);
	borderPane.add(this.messageLabel, BorderLayout.NORTH);
	borderPane.add(this.bs.getStatsPane(), BorderLayout.EAST);
	borderPane.add(effectBarPane, BorderLayout.SOUTH);
	this.battleFrame.addKeyListener(handler);
    }

    void turnEventHandlersOff() {
	this.eventHandlersOn = false;
	this.spell.setEnabled(false);
	this.steal.setEnabled(false);
	this.drain.setEnabled(false);
	this.item.setEnabled(false);
    }

    void turnEventHandlersOn() {
	this.eventHandlersOn = true;
	this.spell.setEnabled(true);
	this.steal.setEnabled(true);
	this.drain.setEnabled(true);
	this.item.setEnabled(true);
    }

    boolean areEventHandlersOn() {
	return this.eventHandlersOn;
    }

    private class EventHandler extends AbstractAction implements KeyListener {
	private static final long serialVersionUID = 20239525230523524L;

	public EventHandler() {
	    // Do nothing
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
	    try {
		final String cmd = e.getActionCommand();
		final Battle b = RetroRPGCS.getInstance().getBattle();
		// Do Player Actions
		if (cmd.equals("Cast Spell") || cmd.equals("c")) {
		    // Cast Spell
		    b.doPlayerActions(MapAI.ACTION_CAST_SPELL);
		} else if (cmd.equals("Steal") || cmd.equals("t")) {
		    // Steal Money
		    b.doPlayerActions(MapAI.ACTION_STEAL);
		} else if (cmd.equals("Drain") || cmd.equals("d")) {
		    // Drain Enemy
		    b.doPlayerActions(MapAI.ACTION_DRAIN);
		} else if (cmd.equals("Use Item") || cmd.equals("i")) {
		    // Use Item
		    b.doPlayerActions(MapAI.ACTION_USE_ITEM);
		}
	    } catch (final Throwable t) {
		RetroRPGCS.getInstance().handleError(t);
	    }
	}

	@Override
	public void keyPressed(final KeyEvent e) {
	    if (!PreferencesManager.oneMove()) {
		if (e.isShiftDown()) {
		    this.handleArrows(e);
		} else {
		    this.handleMovement(e);
		}
	    }
	}

	@Override
	public void keyReleased(final KeyEvent e) {
	    if (PreferencesManager.oneMove()) {
		if (e.isShiftDown()) {
		    this.handleArrows(e);
		} else {
		    this.handleMovement(e);
		}
	    }
	}

	@Override
	public void keyTyped(final KeyEvent e) {
	    // Do nothing
	}

	private void handleMovement(final KeyEvent e) {
	    try {
		if (System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
		    if (e.isMetaDown()) {
			return;
		    }
		} else {
		    if (e.isControlDown()) {
			return;
		    }
		}
		final Battle bl = RetroRPGCS.getInstance().getBattle();
		final MapTimeBattleGUI bg = MapTimeBattleGUI.this;
		if (bg.eventHandlersOn) {
		    final int keyCode = e.getKeyCode();
		    switch (keyCode) {
		    case KeyEvent.VK_NUMPAD4:
		    case KeyEvent.VK_LEFT:
		    case KeyEvent.VK_A:
			bl.updatePosition(-1, 0);
			break;
		    case KeyEvent.VK_NUMPAD2:
		    case KeyEvent.VK_DOWN:
		    case KeyEvent.VK_X:
			bl.updatePosition(0, 1);
			break;
		    case KeyEvent.VK_NUMPAD6:
		    case KeyEvent.VK_RIGHT:
		    case KeyEvent.VK_D:
			bl.updatePosition(1, 0);
			break;
		    case KeyEvent.VK_NUMPAD8:
		    case KeyEvent.VK_UP:
		    case KeyEvent.VK_W:
			bl.updatePosition(0, -1);
			break;
		    case KeyEvent.VK_NUMPAD7:
		    case KeyEvent.VK_Q:
			bl.updatePosition(-1, -1);
			break;
		    case KeyEvent.VK_NUMPAD9:
		    case KeyEvent.VK_E:
			bl.updatePosition(1, -1);
			break;
		    case KeyEvent.VK_NUMPAD3:
		    case KeyEvent.VK_C:
			bl.updatePosition(1, 1);
			break;
		    case KeyEvent.VK_NUMPAD1:
		    case KeyEvent.VK_Z:
			bl.updatePosition(-1, 1);
			break;
		    case KeyEvent.VK_NUMPAD5:
		    case KeyEvent.VK_S:
			// Confirm before attacking self
			final int res = CommonDialogs.showConfirmDialog("Are you sure you want to attack yourself?",
				"Battle");
			if (res == JOptionPane.YES_OPTION) {
			    bl.updatePosition(0, 0);
			}
			break;
		    default:
			break;
		    }
		    bg.resetPlayerActionBar();
		}
	    } catch (final Exception ex) {
		RetroRPGCS.getInstance().handleError(ex);
	    }
	}

	private void handleArrows(final KeyEvent e) {
	    try {
		if (System.getProperty("os.name").equalsIgnoreCase("Mac OS X")) {
		    if (e.isMetaDown()) {
			return;
		    }
		} else {
		    if (e.isControlDown()) {
			return;
		    }
		}
		final Battle bl = RetroRPGCS.getInstance().getBattle();
		final MapTimeBattleGUI bg = MapTimeBattleGUI.this;
		if (bg.eventHandlersOn) {
		    final int keyCode = e.getKeyCode();
		    switch (keyCode) {
		    case KeyEvent.VK_NUMPAD4:
		    case KeyEvent.VK_LEFT:
		    case KeyEvent.VK_A:
			bl.fireArrow(-1, 0);
			break;
		    case KeyEvent.VK_NUMPAD2:
		    case KeyEvent.VK_DOWN:
		    case KeyEvent.VK_X:
			bl.fireArrow(0, 1);
			break;
		    case KeyEvent.VK_NUMPAD6:
		    case KeyEvent.VK_RIGHT:
		    case KeyEvent.VK_D:
			bl.fireArrow(1, 0);
			break;
		    case KeyEvent.VK_NUMPAD8:
		    case KeyEvent.VK_UP:
		    case KeyEvent.VK_W:
			bl.fireArrow(0, -1);
			break;
		    case KeyEvent.VK_NUMPAD7:
		    case KeyEvent.VK_Q:
			bl.fireArrow(-1, -1);
			break;
		    case KeyEvent.VK_NUMPAD9:
		    case KeyEvent.VK_E:
			bl.fireArrow(1, -1);
			break;
		    case KeyEvent.VK_NUMPAD3:
		    case KeyEvent.VK_C:
			bl.fireArrow(1, 1);
			break;
		    case KeyEvent.VK_NUMPAD1:
		    case KeyEvent.VK_Z:
			bl.fireArrow(-1, 1);
			break;
		    case KeyEvent.VK_NUMPAD5:
		    case KeyEvent.VK_S:
			// Confirm before attacking self
			final int res = CommonDialogs.showConfirmDialog("Are you sure you want to attack yourself?",
				"Battle");
			if (res == JOptionPane.YES_OPTION) {
			    bl.fireArrow(0, 0);
			}
			break;
		    default:
			break;
		    }
		    bg.resetPlayerActionBar();
		}
	    } catch (final Exception ex) {
		RetroRPGCS.getInstance().handleError(ex);
	    }
	}
    }
}
