//
//  Thud.java
//
//  Created by asp on Wed Nov 28 2001.
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.ui;

import net.sourceforge.btthud.data.*;
import net.sourceforge.btthud.engine.*;
import net.sourceforge.btthud.util.*;

import java.io.*;
import java.net.URL;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Keymap;

import java.util.*;
import java.lang.ref.WeakReference;

public class Thud extends JFrame implements Runnable {

    Font	mFont = new Font("Monospaced", Font.PLAIN, 10);		// default main font
    AboutBox aboutBox = null;
    
    JTextField				textField;
    JTextPane				textPane;
    BulkStyledDocument		bsd;

    boolean					connected = false;

    LineHolder				lh = null;
    MUConnection 			conn = null;
    MUParse					parse = null;
    
    MUData					data = null;
    MUContactList			conList = null;
    MUStatus				status = null;
    MUTacticalMap			tacMap = null;
    MUPrefs					prefs = null;
    MUCommands				commands = null;

    LinkedList<String>		commandHistory = new LinkedList<String>();
    int						historyLoc = 1;							// how far we are from end of history list
    
    static final int		DEBUG = 0;
    
    private String[]		args;

    boolean					firstLaunch = false;

    // ------------------

	private PrefsDialog prefsDialog;

	private ThudAction taFocusInputField;

	// Private InputMap for numeric keypad.
	private final InputMap numpadInputMap = new InputMap ();

	// Declarations for menus
	private JMenuBar mainMenuBar = new JMenuBar();

	private JMenu fileMenu;
	private ThudAction taLoadMap;
	private ThudAction taSaveMapAs;
	private ThudAction taViewReleaseNotes;
	private ThudAction taQuit;

	private JMenu editMenu;
	private ThudAction taUndo;
	private ThudAction taCut;
	private ThudAction taCopy;
	private ThudAction taPaste;
	private ThudAction taClear;
	private ThudAction taSelectAll;
	private ThudAction taRepeatPreviousCommand;
	private ThudAction taEraseCurrentCommand;
	private ThudAction taMuteMainWindowText;

	private JMenu hudMenu;
	private JMenuItem[] miConnections;
	private ThudAction taPreferences;
	private ThudAction taStartStop;
	private ThudAction taUpdateTacticalMapNow;
	private ThudAction taConnect;
	private ThudAction taAddNewHost;
	private ThudAction taRemoveHost;
	private ThudAction taDisconnect;

	private JMenu mapMenu;
	private ThudAction taZoomIn;
	private ThudAction taZoomOut;
	private ThudAction taShowWeaponsArcs;
	private ThudAction taMakeArcsWeaponRanges;
	private ThudAction taRetractArcRange;
	private ThudAction taExtendArcRange;
	private ThudAction taShowHexNumbers;
	private ThudAction taShowUnitNames;
	private ThudAction taDarkenElevations;
	private ThudAction taShowArmorDiagram;
	private ThudAction taShowLOSInfo;
	private ThudAction taMoveMapLeft;
	private ThudAction taMoveMapRight;
	private ThudAction taMoveMapUp;
	private ThudAction taMoveMapDown;
	private ThudAction taCenterMapOnUnit;
	private ThudAction taShowCliffs;
	private ThudAction taShowHeatArmoronTactical;

	private JMenu debugMenu;
	private ThudAction taDumpDocumentStructure;

	private JMenu windowMenu;
	private ThudAction taShowContactsWindow;
	private ThudAction taShowStatusWindow;
	private ThudAction taShowTacticalWindow;


	// Entry point.
	public static void main (String args[]) {
		try {
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// It was only a suggestion.
		}

		EventQueue.invokeLater(new Thud (args));
	}

	// Initialize main Thud window.
	private Thud (String[] arguments) {		
		super("Thud");
		this.args = arguments;

		final ClassLoader loader = getClass().getClassLoader();

		// Set frame icon.
		try {
			final URL appIconURL = loader.getResource("media/icon/icon.gif");
			final ImageIcon appIcon = new ImageIcon (appIconURL,
			                                         "application icon");
			setIconImage(appIcon.getImage());
		} catch (Exception e) {
			System.err.println("Couldn't load Thud icon");
		}

		// Read preferences.
		readPrefs();

		mainFontChanged();			// setup a new font

		prefsDialog = new PrefsDialog (this);

		// Create an about box
		aboutBox = new AboutBox();

		// Setup the main text areas
		setupNewTextFields();

		// Register all our actions.
		registerActions();

		// Add all of our menus
		addMenus();
		setJMenuBar(mainMenuBar);
		setupListeners();

		// Initialization strings
		String buildNumber = getClass().getPackage().getImplementationVersion();
		if (buildNumber == null)
			buildNumber = "";
		else
			buildNumber = "(r" + buildNumber + ")";

		bsd.insertPlainString(" *** Thud, (c) 2001-2007 Anthony Parker & the THUD team      ***");
		bsd.insertPlainString(" *** bt-thud.sourceforge.net                                 ***");
		bsd.insertPlainString(String.format(" *** Version: 1.4 %-42s ***", buildNumber));
		bsd.insertPlainString(" *** To get started, connect to a MUX via the HUD menu,      ***");
		bsd.insertPlainString(" *** then hit Ctrl-G when in a combat unit to activate Thud! ***\n");

		// Attempt auto-connect, if given parameters
		if(args.length == 2) {
			try {
				bsd.insertPlainString(" *** Auto-connecting to " + args[0] + " port " + args[1] + "...\n");
				startConnection(args[0],Integer.parseInt(args[1]));
			} catch (Exception e) {
				System.out.println("Error auto-connecting to " + args[0] + "  port " + args[1]);
			}
		}
	}

	// Finish setting up GUI from event dispatch thread.
	public void run () {
		// Locate the window properly
		pack();
		textField.requestFocusInWindow();

		setSize(prefs.mainSizeX, prefs.mainSizeY);
		setLocation(prefs.mainLoc);
		setAlwaysOnTop(prefs.mainAlwaysOnTop);

		setVisible(true);

		// Show version notes
		// TODO: Fix PreferenceStore to detect version upgrades.
		if (firstLaunch) {
			doReleaseNotes();
			firstLaunch = false;
		}
	}


	//
	// Menus.
	//
	// TODO: Convert all these menus to constructors or something.

	// Main menu bar.
	void addMenus() {
		// FIXME: All of the menus get re-created every time we
		// add/remove hosts, which is kinda silly.  At most, we only
		// need to update the "HUD" menu.

		addFileMenu();
		addEditMenu();
		addHUDMenu();
		addMapMenu();
		addDebugMenu();
		addWindowMenu();
	}

	// "File" menu.
	private void addFileMenu () {
		if (fileMenu != null)
			return;

		fileMenu = new JMenu ("File");

		fileMenu.add(taLoadMap);
		fileMenu.add(taSaveMapAs);

		fileMenu.addSeparator();

		fileMenu.add(taViewReleaseNotes);

		fileMenu.addSeparator();

		fileMenu.add(taQuit);

		mainMenuBar.add(fileMenu);
	}

	// "Edit" menu.
	private void addEditMenu () {
		if (editMenu != null)
			return;

		editMenu = new JMenu ("Edit");

		editMenu.add(taUndo);

		editMenu.addSeparator();

		editMenu.add(taCut);
		editMenu.add(taCopy);
		editMenu.add(taPaste);
		editMenu.add(taClear);

		editMenu.addSeparator();

		editMenu.add(taSelectAll);

		editMenu.addSeparator();

		editMenu.add(taRepeatPreviousCommand);
		editMenu.add(taEraseCurrentCommand);

		editMenu.addSeparator();

		addCheckBoxItem(editMenu, taMuteMainWindowText);

		mainMenuBar.add(editMenu);
	}

	// "HUD" menu.
	private void addHUDMenu () {
		boolean firstTime;

		if (hudMenu != null) {
			firstTime = false;
			hudMenu.removeAll();
		} else {
			firstTime = true;
			hudMenu = new JMenu ("HUD");
		}

		// Setup the connection menu items
		initConnectionMenus();

		hudMenu.add(taPreferences);

		hudMenu.addSeparator();

		hudMenu.add(taStartStop);
		hudMenu.add(taUpdateTacticalMapNow);

		hudMenu.addSeparator();

		for (int ii = 0; ii < prefs.hosts.size(); ii++) {
			MUHost nextHost = (MUHost)prefs.hosts.get(ii);
			miConnections[ii] = new JMenuItem (taConnect);
			miConnections[ii].setText(nextHost.toString());
			acceleratorForConnectionItem(miConnections[ii], ii);
			hudMenu.add(miConnections[ii]);
		}

		hudMenu.addSeparator();

		hudMenu.add(taAddNewHost);
		hudMenu.add(taRemoveHost);

		hudMenu.addSeparator();

		hudMenu.add(taDisconnect);

		if (firstTime) {
			mainMenuBar.add(hudMenu);
		}
	}

	// "Map" menu.
	private void addMapMenu () {
		if (mapMenu != null)
			return;

		mapMenu = new JMenu ("Map");

		mapMenu.add(taZoomIn);
		mapMenu.add(taZoomOut);

		mapMenu.addSeparator();

		addCheckBoxItem(mapMenu, taShowWeaponsArcs);
		addCheckBoxItem(mapMenu, taMakeArcsWeaponRanges);
		mapMenu.add(taRetractArcRange);
		mapMenu.add(taExtendArcRange);

		mapMenu.addSeparator();

		addCheckBoxItem(mapMenu, taShowHexNumbers);
		addCheckBoxItem(mapMenu, taShowUnitNames);
		addCheckBoxItem(mapMenu, taDarkenElevations);
		addCheckBoxItem(mapMenu, taShowArmorDiagram);
		addCheckBoxItem(mapMenu, taShowLOSInfo);

		mapMenu.addSeparator();

		mapMenu.add(taMoveMapLeft);
		mapMenu.add(taMoveMapRight);
		mapMenu.add(taMoveMapUp);
		mapMenu.add(taMoveMapDown);
		mapMenu.add(taCenterMapOnUnit);

		mapMenu.addSeparator();

		addCheckBoxItem(mapMenu, taShowCliffs);
		addCheckBoxItem(mapMenu, taShowHeatArmoronTactical);

		// Disable the map menu until we're actually connected
		mapMenu.setEnabled(false);
		mainMenuBar.add(mapMenu);
	}

	// "Debug" menu.
	private void addDebugMenu () {
		if (DEBUG == 0 || debugMenu != null)
			return;

		debugMenu = new JMenu("Debug");

		debugMenu.add(taDumpDocumentStructure);

		mainMenuBar.add(debugMenu);
	}

	// "Window" menu.
	private void addWindowMenu () {
		if (windowMenu != null)
			return;

		windowMenu = new JMenu ("Window");

		windowMenu.add(taShowContactsWindow);
		windowMenu.add(taShowStatusWindow);
		windowMenu.add(taShowTacticalWindow);

		// Disable the window menu until we're actually connected
		windowMenu.setEnabled(false);
		mainMenuBar.add(windowMenu);
	}


	//
	// Main Thud window actions.
	//

	private void registerActions () {
		//
		// Generic actions.
		//

		// Switch focus to this window's input text field whenever
		// someone tries to type without input text field focus.  Also,
		// relay the typed character to the text field, so we don't
		// lose the input.
		//
		// This action will usually be paired with a
		// WHEN_ANCESTOR_OF_FOCUSED_COMPONENT input mapping, so there's
		// no problem with specific components capturing input.  Only
		// unhandled input will be passed up to trigger this action.
		taFocusInputField = new ThudSimpleAction ("Focus Input Field") {
			protected void doAction () {
				//doLoadMap();
			}
		};


		//
		// Key bindings.
		//
		// FIXME: This is a bit of a hack.  We should do this in a more
		// organized way, and maybe not on the root pane.
		//
		// We'll want some sort of KeyBindingManager, that will provide
		// a GUI for the user to set/save/restore custom bindings, and
		// also supply input/action maps to inherit from.
		//
		// Probably just input maps, as action maps will be fixed for
		// any particular component, although having an Action manager
		// might be useful for enabling/disabling all related actions
		// simultaneously (for connections, for example).

		bindCommand(KeyEvent.VK_NUMPAD1, "heading 240");
		bindCommand(KeyEvent.VK_NUMPAD1, Event.SHIFT_MASK, "heading 210");

		bindCommand(KeyEvent.VK_NUMPAD2, "heading 180");

		bindCommand(KeyEvent.VK_NUMPAD3, Event.SHIFT_MASK, "heading 150");
		bindCommand(KeyEvent.VK_NUMPAD3, "heading 120");

		bindCommand(KeyEvent.VK_NUMPAD4, "heading 270");

		bindAction(KeyEvent.VK_NUMPAD5, new StayHeadingAction (this));
		bindAction(KeyEvent.VK_NUMPAD5, Event.SHIFT_MASK, new ReverseHeadingAction (this));

		bindCommand(KeyEvent.VK_NUMPAD6, "heading 90");

		bindCommand(KeyEvent.VK_NUMPAD7, "heading 300");
		bindCommand(KeyEvent.VK_NUMPAD7, Event.SHIFT_MASK, "heading 330");

		bindCommand(KeyEvent.VK_NUMPAD8, "heading 0");

		bindCommand(KeyEvent.VK_NUMPAD9, Event.SHIFT_MASK, "heading 30");
		bindCommand(KeyEvent.VK_NUMPAD9, "heading 60");

		// TIC bindings.
		bindCommand(KeyEvent.VK_F1, "firetic 0");
		bindCommand(KeyEvent.VK_F2, "firetic 1");
		bindCommand(KeyEvent.VK_F3, "firetic 2");
		bindCommand(KeyEvent.VK_F4, "firetic 3");

		bindCommand(KeyEvent.VK_F1, Event.SHIFT_MASK, "listtic 0");
		bindCommand(KeyEvent.VK_F2, Event.SHIFT_MASK, "listtic 1");
		bindCommand(KeyEvent.VK_F3, Event.SHIFT_MASK, "listtic 2");
		bindCommand(KeyEvent.VK_F4, Event.SHIFT_MASK, "listtic 3");

		// Weapon bindings.
		// FIXME: We were already using CTRL + # (or rather, menu
		// shortcut + #) for host accelerators.  Changed the host
		// accelerators to menu shortcut + SHIFT + # for now.
		bindCommand(KeyEvent.VK_1, Event.CTRL_MASK, "sight 1");
		bindCommand(KeyEvent.VK_2, Event.CTRL_MASK, "sight 2");
		bindCommand(KeyEvent.VK_3, Event.CTRL_MASK, "sight 3");
		bindCommand(KeyEvent.VK_4, Event.CTRL_MASK, "sight 4");
		bindCommand(KeyEvent.VK_5, Event.CTRL_MASK, "sight 5");
		bindCommand(KeyEvent.VK_6, Event.CTRL_MASK, "sight 6");
		bindCommand(KeyEvent.VK_7, Event.CTRL_MASK, "sight 7");
		bindCommand(KeyEvent.VK_8, Event.CTRL_MASK, "sight 8");
		bindCommand(KeyEvent.VK_9, Event.CTRL_MASK, "sight 9");
		bindCommand(KeyEvent.VK_0, Event.CTRL_MASK, "sight 0");

		bindCommand(KeyEvent.VK_1, Event.ALT_MASK, "fire 1");
		bindCommand(KeyEvent.VK_2, Event.ALT_MASK, "fire 2");
		bindCommand(KeyEvent.VK_3, Event.ALT_MASK, "fire 3");
		bindCommand(KeyEvent.VK_4, Event.ALT_MASK, "fire 4");
		bindCommand(KeyEvent.VK_5, Event.ALT_MASK, "fire 5");
		bindCommand(KeyEvent.VK_6, Event.ALT_MASK, "fire 6");
		bindCommand(KeyEvent.VK_7, Event.ALT_MASK, "fire 7");
		bindCommand(KeyEvent.VK_8, Event.ALT_MASK, "fire 8");
		bindCommand(KeyEvent.VK_9, Event.ALT_MASK, "fire 9");
		bindCommand(KeyEvent.VK_0, Event.ALT_MASK, "fire 0");

		// Targeting bindings
		// TODO: If we're locked on a tank/VTOL/whatever, adapt.
		bindCommand(KeyEvent.VK_NUMPAD1, Event.CTRL_MASK, "target ll");
		bindCommand(KeyEvent.VK_NUMPAD2, Event.CTRL_MASK, "target -");
		bindCommand(KeyEvent.VK_NUMPAD3, Event.CTRL_MASK, "target rl");
		bindCommand(KeyEvent.VK_NUMPAD4, Event.CTRL_MASK, "target la");
		bindCommand(KeyEvent.VK_NUMPAD5, Event.CTRL_MASK, "target ct");
		bindCommand(KeyEvent.VK_NUMPAD6, Event.CTRL_MASK, "target ra");
		bindCommand(KeyEvent.VK_NUMPAD7, Event.CTRL_MASK, "target lt");
		bindCommand(KeyEvent.VK_NUMPAD8, Event.CTRL_MASK, "target h");
		bindCommand(KeyEvent.VK_NUMPAD9, Event.CTRL_MASK, "target rt");

		// Misc bindings
		// TODO: If we're in a tank, rotate turret instead.
		bindCommand(KeyEvent.VK_NUMPAD7, Event.ALT_MASK, "rottorso l");
		bindCommand(KeyEvent.VK_NUMPAD8, Event.ALT_MASK, "rottorso c");
		bindCommand(KeyEvent.VK_NUMPAD9, Event.ALT_MASK, "rottorso r");


		//
		// Menu-related actions.
		//

		// Register file menu actions.
		taLoadMap = new ThudSimpleAction ("Load Map...") {
			protected void doAction () {
				doLoadMap();
			}
		};

		taSaveMapAs = new ThudSimpleAction ("Save Map As...") {
			protected void doAction () {
				doSaveMapAs();
			}
		};

		taViewReleaseNotes = new ThudSimpleAction ("View Release Notes...") {
			protected void doAction () {
				doReleaseNotes();
			}
		};

		taQuit = new ThudSimpleAction ("Quit", KeyEvent.VK_Q) {
			protected void doAction () {
				doQuit();
			}
		};

		// Register edit menu actions.
		// TODO: Use DefaultEditorKit bindings.
		taUndo = getEmptyAction("Undo", KeyEvent.VK_Z);
		taUndo.setEnabled(false);

		taCut = getEmptyAction("Cut", KeyEvent.VK_X);

		taCopy = getEmptyAction("Copy", KeyEvent.VK_C);

		taPaste = getEmptyAction("Paste", KeyEvent.VK_V);

		taClear = getEmptyAction("Clear");
		taClear.setEnabled(false);

		taSelectAll = getEmptyAction("Select All", KeyEvent.VK_A);

		taRepeatPreviousCommand = new ThudSimpleAction ("Repeat Previous Command", KeyEvent.VK_P) {
			protected void doAction () {
				doPreviousCommand();
			}
		};

		taEraseCurrentCommand = new ThudSimpleAction ("Erase Current Command", KeyEvent.VK_U) {
			protected void doAction () {
				doEraseCommand();
			}
		};

		taMuteMainWindowText = new ThudSimpleAction ("Mute Main Window Text", KeyEvent.VK_SEMICOLON, Event.SHIFT_MASK) {
			protected void doAction () {
				doMuteMainWindow();
			}
		};

		// Register HUD menu actions.
		taPreferences = new ThudSimpleAction ("Preferences...") {
			protected void doAction () {
				doPreferences();
			}
		};

		taStartStop = new ThudSimpleAction ("Start/Stop", KeyEvent.VK_G) {
			protected void doAction () {
				doStartStop();
			}
		};
		taStartStop.setEnabled(false);

		taUpdateTacticalMapNow =  new ThudSimpleAction ("Update Tactical Map Now", KeyEvent.VK_N) {
			protected void doAction () {
				commands.forceTactical();
			}
		};
		taUpdateTacticalMapNow.setEnabled(false);

		taConnect = new ThudAction ("Connect") {
			public void actionPerformed (final ActionEvent ae) {
				doNewConnection(ae.getActionCommand());
			}
		};

		taAddNewHost = new ThudSimpleAction ("Add New Host...") {
			protected void doAction () {
				doAddNewHost();
			}
		};

		taRemoveHost = new ThudSimpleAction ("Remove Host...") {
			protected void doAction () {
				doRemoveHost();
			}
		};

		taDisconnect = new ThudSimpleAction ("Disconnect", KeyEvent.VK_Q,
		                          Event.SHIFT_MASK) {
			protected void doAction () {
				stopConnection();
			}
		};
		taDisconnect.setEnabled(false);

		// Register map menu actions.
		// TODO: Maybe we should move these actions to MUTacticalMap.
		taZoomIn = new ThudSimpleAction ("Zoom In", KeyEvent.VK_CLOSE_BRACKET) {
			protected void doAction () {
				doZoom(5);
			}
		};

		taZoomOut = new ThudSimpleAction ("Zoom Out", KeyEvent.VK_OPEN_BRACKET) {
			protected void doAction () {
				doZoom(-5);
			}
		};

		taShowWeaponsArcs = new ThudSimpleAction ("Show Weapons Arcs", KeyEvent.VK_R) {
			protected void doAction () {
				doShowArcs();
			}
		};
		taShowWeaponsArcs.setSelected(prefs.tacShowArcs);

		taMakeArcsWeaponRanges = new ThudSimpleAction ("Make Arcs Weapon Ranges", KeyEvent.VK_M) {
			protected void doAction () {
				doMakeArcsWeaponRange();
			}
		};
		taMakeArcsWeaponRanges.setSelected(prefs.makeArcsWeaponRange);
		taMakeArcsWeaponRanges.setEnabled(prefs.tacShowArcs);

		taRetractArcRange = new ThudSimpleAction ("Retract Arc Range", KeyEvent.VK_SEMICOLON) {
			protected void doAction () {
				doChangeArc(-1);
			}
		};
		taRetractArcRange.setEnabled(prefs.tacShowArcs && !prefs.makeArcsWeaponRange);

		taExtendArcRange = new ThudSimpleAction ("Extend Arc Range", KeyEvent.VK_QUOTE) {
			protected void doAction () {
				doChangeArc(1);
			}
		};
		taExtendArcRange.setEnabled(prefs.tacShowArcs && !prefs.makeArcsWeaponRange);

		taShowHexNumbers = new ThudSimpleAction ("Show Hex Numbers", KeyEvent.VK_B) {
			protected void doAction () {
				doShowHexNumbers();
			}
		};
		taShowHexNumbers.setSelected(prefs.tacShowHexNumbers);

		taShowUnitNames = new ThudSimpleAction ("Show Unit Names", KeyEvent.VK_U) {
			protected void doAction () {
				doShowUnitNames();
			}
		};
		taShowUnitNames.setSelected(prefs.tacShowUnitNames);

		taDarkenElevations = new ThudSimpleAction ("Darken Elevations", KeyEvent.VK_D) {
			protected void doAction () {
				doDarkenElevations();
			}
		};
		taDarkenElevations.setSelected(prefs.tacDarkenElev);

		taShowArmorDiagram = new ThudSimpleAction ("Show Armor Diagram") {
			protected void doAction () {
				doShowArmorDiagrams();
			}
		};
		taShowArmorDiagram.setSelected(prefs.tacShowArmorDiagram);

		taShowLOSInfo = new ThudSimpleAction ("Show LOS Info", KeyEvent.VK_L) {
			protected void doAction () {
				doShowLOSInfo();
			}
		};
		taShowLOSInfo.setSelected(prefs.tacShowLOSInfo);

		taMoveMapLeft = new ThudSimpleAction ("Move Map Left", KeyEvent.VK_A, Event.SHIFT_MASK) {
			protected void doAction () {
				doChangeXOffset(-1);
			}
		};

		taMoveMapRight = new ThudSimpleAction ("Move Map Right", KeyEvent.VK_D, Event.SHIFT_MASK) {
			protected void doAction () {
				doChangeXOffset(1);
			}
		};

		taMoveMapUp = new ThudSimpleAction ("Move Map Up", KeyEvent.VK_W, Event.SHIFT_MASK) {
			protected void doAction () {
				doChangeYOffset(-1);
			}
		};

		taMoveMapDown = new ThudSimpleAction ("Move Map Down", KeyEvent.VK_S, Event.SHIFT_MASK) {
			protected void doAction () {
				doChangeYOffset(1);
			}
		};

		taCenterMapOnUnit = new ThudSimpleAction ("Center Map On Unit", KeyEvent.VK_R, Event.SHIFT_MASK) {
			protected void doAction () {
				doCenterMap();
			}
		};

		taShowCliffs = new ThudSimpleAction ("Show Cliffs", KeyEvent.VK_F) {
			protected void doAction () {
				doShowCliffs();
			}
		};
		taShowCliffs.setSelected(prefs.tacShowCliffs);

		taShowHeatArmoronTactical = new ThudSimpleAction ("Show Heat/Armor on Tactical", KeyEvent.VK_I) {
			protected void doAction () {
				doShowIndicators();
			}
		};
		taShowHeatArmoronTactical.setSelected(prefs.tacShowIndicators);

		// Register debug menu actions.
		taDumpDocumentStructure = new ThudSimpleAction ("Dump Document Structure") {
			protected void doAction () {
				bsd.dump(System.out);        
			}
		};

		// Register window menu actions.
		taShowContactsWindow = new ThudSimpleAction ("Show Contacts Window") {
			protected void doAction () {
				conList.setVisible(true);
			}
		};

		taShowStatusWindow = new ThudSimpleAction ("Show Status Window") {
			protected void doAction () {
				status.setVisible(true);
			}
		};

		taShowTacticalWindow = new ThudSimpleAction ("Show Tactical Window") {
			protected void doAction () {
				tacMap.setVisible(true);
			}
		};
	}


	// TODO: Could add this to a ThudMenu class.
	private void addCheckBoxItem (final JMenu menu, final ThudAction act) {
		final JMenuItem item = new JCheckBoxMenuItem (act);
		act.addButton(item);
		menu.add(item);
	}

	// Helpers to map a KeyStroke to a specific command string.
	private void bindCommand (final KeyStroke key, final String command) {
		bindAction(key, new SendCommandAction (this, command));
	}

	private void bindCommand (final int keycode, final int modmask,
	                          final String command) {
		// Convenience.
		bindCommand(KeyStroke.getKeyStroke(keycode, modmask), command);
	}

	private void bindCommand (final int keycode, final String command) {
		// Convenience.
		bindCommand(keycode, 0, command);
	}

	// Helpers to map a KeyStroke to an arbitrary Action.
	private void bindAction (final KeyStroke key, final Action action) {
		// TODO: WHEN_ANCESTOR_OF_FOCUSED_COMPONENT may not always be
		// the best choice, but more options might be confusing.
		InputMap inputMap;

		switch (key.getKeyCode()) {
		case KeyEvent.VK_NUMPAD0:
		case KeyEvent.VK_NUMPAD1:
		case KeyEvent.VK_NUMPAD2:
		case KeyEvent.VK_NUMPAD3:
		case KeyEvent.VK_NUMPAD4:
		case KeyEvent.VK_NUMPAD5:
		case KeyEvent.VK_NUMPAD6:
		case KeyEvent.VK_NUMPAD7:
		case KeyEvent.VK_NUMPAD8:
		case KeyEvent.VK_NUMPAD9:
			// Numeric keypad input map.
			// TODO: Add support for non-digit keypad keys.
			inputMap = numpadInputMap;
			break;

		default:
			// Regular input map.
			inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			break;
		}

		final ActionMap actionMap = getRootPane().getActionMap();

		final Object nameActionKey = action.getValue(Action.NAME);

		inputMap.put(key, nameActionKey);
		actionMap.put(nameActionKey, action);
	}

	private void bindAction (final int keycode, final int modmask,
	                         final Action action) {
		// Convenience.
		bindAction(KeyStroke.getKeyStroke(keycode, modmask), action);
	}

	private void bindAction (final int keycode, final Action action) {
		// Convenience.
		bindAction(keycode, 0, action);
	}

	// XXX: Debugging code that adds empty actions.
	private ThudAction getEmptyAction (final String name) {
		return new ThudAction (name) {
			public void actionPerformed (final ActionEvent e) {
				System.err.println("No action: " + e);
			}
		};
	}

	private ThudAction getEmptyAction (final String name, final int accel) {
		final ThudAction action = getEmptyAction(name);

		action.putValue(Action.ACCELERATOR_KEY,
		                KeyStroke.getKeyStroke(accel,
		                                       Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		return action;
	}


	// Base class for various main window actions.
	private static final int menuShortcutMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

	private abstract class ThudAction extends AbstractAction {
		private ThudAction (final String name) {
			super(name);
		}

		private ThudAction (final String name, final int accel) {
			this(name, accel, 0);
		}

		private ThudAction (final String name, final int accel,
		                    final int modmask) {
			this(name);

			putValue(Action.ACCELERATOR_KEY,
			         KeyStroke.getKeyStroke(accel,
			                                menuShortcutMask | modmask));
		}


		// Java 6 supports setting the selected state on Actions.
		// Unfortunately, we have to be compatible with more than just
		// Java 6, so we sorta implement the same thing here.
		private Set<WeakReference<AbstractButton>> listeningButtons = new HashSet<WeakReference<AbstractButton>> ();

		public void addButton (final AbstractButton button) {
			button.setSelected(selected);
			listeningButtons.add(new WeakReference<AbstractButton> (button));
		}


		private boolean selected = false;

		public boolean isSelected () {
			return selected;
		}

		public void setSelected (final boolean selected) {
			this.selected = selected;

			final Iterator<WeakReference<AbstractButton>> iter = listeningButtons.iterator();

			while (iter.hasNext()) {
				final AbstractButton button = iter.next().get();

				if (button == null) {
					iter.remove();
					continue;
				}

				button.setSelected(selected);
			}
		}
	}

	private abstract class ThudSimpleAction extends ThudAction {
		private ThudSimpleAction (final String name) {
			super(name);
		}

		private ThudSimpleAction (final String name, final int accel) {
			super(name, accel, 0);
		}

		private ThudSimpleAction (final String name, final int accel,
		                          final int modmask) {
			super(name, accel, modmask);
		}


		public void actionPerformed (final ActionEvent ae) {
			doAction();
		}

		protected abstract void doAction ();
	}


	/** Repaint ourselves */
	public void paint (Graphics g) {
		// TODO: Is there a cleaner way to do this?
		final Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
		                    prefs.antiAliasText ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

		super.paint(g2);
	}

	// --------------------------------------------------------------------
	// ACTION IMPLEMENTATION
	// --------------------------------------------------------------------

	/** Load map from file. */
	private void doLoadMap () {
		final JFileChooser fc = new JFileChooser ();
		final int returnVal = fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			data.mapFileName = fc.getSelectedFile().getAbsolutePath();
			if (data.loadMapFromDisk()) {
				parse.messageLine("*** Map " + data.mapFileName + " loaded successfully ***");
			} else {
				parse.messageLine("*** Error loading map " + data.mapFileName + " ***");
			}
		}
	}

	/** Save map to file. */
	private void doSaveMapAs () {
		final JFileChooser fc = new JFileChooser ();
		final int returnVal = fc.showSaveDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			data.mapFileName = fc.getSelectedFile().getAbsolutePath();
			if (data.saveMapToDisk()) {
				parse.messageLine("*** Map " + data.mapFileName + " saved successfully ***");
			} else {
				parse.messageLine("*** Error saving map " + data.mapFileName + " ***");
			}
		}
	}

	/** Show the release notes */
	private void doReleaseNotes () {
		new ReleaseNotesDialog(this, true).setVisible(true);
	}

	/** Quit cleanly */
	private void doQuit () {
		try {
			// Close our connection
			if (connected)
				stopConnection();

			// Write out our preferences file
			writePrefs();

			// Write out map
			//if (data != null)
			//	data.saveMapToDisk();
		} catch (final Exception e) {
			System.out.println("Error: doQuit: " + e);
		}

		// We're done
		System.exit(0);
	}

	private void setupListeners () {
		addWindowListener(new WindowAdapter () {
			public void windowClosing (final WindowEvent we) {
				doQuit();
			}
		});
	}


    // ------------------------------------------------------------------------
    // MAIN SETUP
    // ------------------------------------------------------------------------
    
	/**
	 * Utility function to get the proper accelerator for connection items
	 * in the HUD menu
	 */
	private void acceleratorForConnectionItem (JMenuItem mi, int i) {
		int keycode = KeyEvent.VK_UNDEFINED;

		switch (i) {
		case 0: keycode = KeyEvent.VK_1; break;
		case 1: keycode = KeyEvent.VK_2; break;
		case 2: keycode = KeyEvent.VK_3; break;
		case 3: keycode = KeyEvent.VK_4; break;
		case 4: keycode = KeyEvent.VK_5; break;
		case 5: keycode = KeyEvent.VK_6; break;
		case 6: keycode = KeyEvent.VK_7; break;
		case 7: keycode = KeyEvent.VK_8; break;
		case 8: keycode = KeyEvent.VK_9; break;
		case 9: keycode = KeyEvent.VK_0; break;
		}

		// TODO: We could have just computed this and used the String
		// (or even char) version of getKeyStroke().
		if (keycode != KeyEvent.VK_UNDEFINED) {
			mi.setAccelerator(KeyStroke.getKeyStroke(keycode,
			                                         Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK));
		}
	}

    /** Initialize the connection items for the HUD menu */
    public void initConnectionMenus()
    {        
        miConnections = new JMenuItem[prefs.hosts.size()];
    }
    
    protected void setupNewTextFields()
    {
        bsd = new BulkStyledDocument(prefs.mainFontSize, prefs.maxScrollbackSize, mFont);

        textField = new JTextField(80);
        textField.setFont(mFont);
        textField.setEnabled(true);
        
        textPane = new JTextPane(bsd);
        textPane.setDocument(bsd);
        textPane.setBackground(Color.black);
        textPane.setEditable(false);
        textPane.setFont(mFont);
        textPane.setRequestFocusEnabled(false);
	textPane.setFocusable(true);
        
        JScrollPane scrollPane = new JScrollPane(textPane,
                                                 JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Setup the text pane
        add(scrollPane, BorderLayout.CENTER);

        // Setup the text field
        add(textField, BorderLayout.SOUTH);

	// Link a few key bindings.
	final InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT); // we can also use WHEN_IN_FOCUSED_WINDOW in any subcomponent, but it's messier
	final ActionMap actionMap = getRootPane().getActionMap();

	// TODO: Relate these to DefaultEditorKit?
	// so they compare equal as Objects.
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), "PAGE UP");
	inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), "PAGE DOWN");

	// XXX: We're making use of the fact that string literals are interned.
	actionMap.put("PAGE UP", new ActionRedirector (textPane, DefaultEditorKit.pageUpAction));
	actionMap.put("PAGE DOWN", new ActionRedirector (textPane, DefaultEditorKit.pageDownAction));

	// Install our custom numeric keypad handling on focusable components.
	// TODO: We probably want to make this configurable, for people who
	// like to use their numeric pads to, you know, type in numbers.
	final NumpadKeyListener numpadListener = new NumpadKeyListener (getRootPane(), numpadInputMap);

	textField.addKeyListener(numpadListener);
	textPane.addKeyListener(numpadListener);

	final InputMap tfInputMap = textField.getInputMap();
	textField.setInputMap(JComponent.WHEN_FOCUSED,
	                      new NumpadInputMap (tfInputMap));

	// FIXME: This is an even bigger hack.  Mostly because we're meddling
	// directly with the textField; the ginormous anonymous class we can
	// obviously always refactor later.
	textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "btthud.ENTER");
	textField.getActionMap().put("btthud.ENTER", new AbstractAction () {
		public void actionPerformed (final ActionEvent ae) {
			final String text = textField.getText();

			if (conn != null && text != null && conn.connected) {
				if (prefs.echoCommands)
					parse.commandLine("> " + text);

				if (!parse.isHudCommand(text)) {
					try {
						conn.sendCommand(text);
					} catch (IOException e) {
						parse.commandLine("> Couldn't send: " + e);
						// TODO: Break connection?
					}
				}

				// Clear the text field
				textField.setText(null);

				// Add this command to our history
				if (commandHistory.size() == 0
				    || (String)commandHistory.getLast() != text)
					commandHistory.add(text);

				// If we're over our preferred history size,
				// remove the next line (in FIFO order).
				if (commandHistory.size() > prefs.commandHistory)
					commandHistory.removeFirst();

				// Reset our history location counter
				historyLoc = 1;
			} else {
				// Trying to talk while not connected
				bsd.insertMessageString("*** Can't Send Text: Not Connected ***");
			}
		}
	});
    }

    /** Start the connection, including creating new objects */
    public void startConnection(String host, int port)
    {        
        if (connected)		// We must already have a connection. Let's clean up that one, then go to this new one
            stopConnection();

        try
        {
            // Setup some of the helper classes
            data = new MUData();

            lh = new LineHolder();
            parse = new MUParse(lh, textPane, data, bsd, prefs);
            parse.messageLine("*** Connecting... ***");
            
            // Setup the connection
            conn = new MUConnection(lh, host, port, this);
            this.setTitle("Thud - " + host + " " + port);

            // Setup the rest of the helper classes.
            status = new MUStatus (this);
            conList = new MUContactList (this);
            tacMap = new MUTacticalMap (this);
            
            commands = new MUCommands(conn, data, prefs);
            
            // Let our parsing class know where to send commands
            parse.setCommands(commands);

            // Let the text field get the keyboard focus
            setVisible(true);
            textField.grabFocus();

            // Okay we're connected
            connected = true;

            // Enable some menu stuff
            taStartStop.setEnabled(true);
            taUpdateTacticalMapNow.setEnabled(true);
            taDisconnect.setEnabled(true);
            mapMenu.setEnabled(true);
            windowMenu.setEnabled(true);
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e);
        }
    }

    // ---------------------

    public void stopConnection()
    {
        if (connected)
        {
            connected = false;
            
            
            if (commands != null)
                commands.endTimers();

            if (conList != null)
                conList.pleaseStop();

            if (status != null)
                status.pleaseStop();

            if (tacMap != null)
                tacMap.pleaseStop();

            if (conn != null)
                conn.pleaseStop();

            if (parse != null)
                parse.messageLine("*** Disconnected ***");
            
            this.setTitle("Thud");

            // Disable some menu stuff
            taStartStop.setEnabled(false);
            taUpdateTacticalMapNow.setEnabled(false);
            taDisconnect.setEnabled(false);
            mapMenu.setEnabled(false);
            windowMenu.setEnabled(false);
        }
    }

    /** Display our about box */
    public void doAbout() {
        aboutBox.setResizable(false);
        aboutBox.setVisible(true);        
    }

    /** Display the preferences dialog */
    public void doPreferences()
    {
        prefsDialog.setVisible(true);

        // Send messages around in case something changed
        if (tacMap != null)
            tacMap.newPreferences(prefs);
        if (conList != null)
            conList.newPreferences(prefs);
        if (status != null)
            status.newPreferences(prefs);
        
        mainFontChanged();
        bsd.setMaxLines(prefs.maxScrollbackSize);
        this.setAlwaysOnTop(prefs.mainAlwaysOnTop);

        // Why not write the prefs to disk right now? Save ourselves some grief
        writePrefs();
    }

    /** Display the "Add New Host" dialog */
    public void doAddNewHost()
    {
        AddHostDialog		addDialog = new AddHostDialog(this, true);
        addDialog.setVisible(true);
    }

    /** Display the "Remove Host" dialog */
    public void doRemoveHost()
    {
        RemoveHostDialog	removeDialog = new RemoveHostDialog(this, true);
        removeDialog.setVisible(true);
    }
    
    // -----------------------

    /** Insert the previous command into the text box */
    public void doPreviousCommand()
    {
        if (commandHistory.size() - historyLoc > 0)		// make sure we're not going past what we have
        {
            textField.setText((String) commandHistory.get(commandHistory.size() - historyLoc));
            textField.setCaretPosition(textField.getDocument().getLength());
            historyLoc++;
        }
        else
        {
            historyLoc = 1;
        }
    }
    
    /** Erase the current command from the text box */
    public void doEraseCommand()
    {
        textField.setText(null);
    }

    /** Mute the text in the main window */
    public void doMuteMainWindow()
    {
        data.mainWindowMuted = !data.mainWindowMuted;
        taMuteMainWindowText.setSelected(data.mainWindowMuted);

        if (parse != null)
        {
            if (data.mainWindowMuted)
                parse.messageLine("*** Main Window Text Output Muted ***");
            else
                parse.messageLine("*** Main Window Text Output Unmuted ***");            
        }
    }
    
    /** Toggle HUD status */
    public void doStartStop()
    {       
        if (data.hudRunning) {
           doStop();
        } else {
            doStart();
        }                    
    }
    
    /** Starts the HUD. */
    public void doStart() {
    	if(connected && !data.hudStarted) { // only start if we're connected and not already started
    		data.hudStarted = true;
    		data.lastDataTime = System.currentTimeMillis();
    		
    		 parse.messageLine("*** Display Started ***");

             // Set our session key to something not too easily duplicated
             String	sessionKey = String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND));
             parse.setSessionKey(sessionKey);

             try
             {
                 // Set the HUDINFO key
                 conn.sendCommand("hudinfo key=" + sessionKey);
             }
             catch (Exception e)
             {
                 System.out.println("Error: hudinfo key set: " + e);
             }

             data.clearData();
             
             // Start sending commands
             commands.startTimers();

             taUpdateTacticalMapNow.setEnabled(true);
    	}    	
    }
    
    /** Stops the HUD. */
    public void doStop() {
    	if(data.hudStarted) {//only stop if we're started
    		data.hudStarted = false;
            parse.messageLine("*** Display Stopped ***");
            data.hudRunning = false;
            commands.endTimers();            
            taUpdateTacticalMapNow.setEnabled(false);
    	}
    }
    
    /** Resume the HUD */
    public void doResume() {
    	if(conn.connected && !data.hudRunning) { // only resume if connected and not already running
    		data.hudRunning = true;
    		commands.forceTactical();
    	}
    }
    
    /** Resume the HUD, with an optional 'Resumed' message.
     * 
     * @param display If true, display '*** Display Resumed ***' in console
     */
    public void doResume(boolean display) {
    	doResume();
    	if(display) 
    		parse.messageLine("*** Display Resumed ***");    
    }

    /** Suspend the HUD */
    public void doSuspend() {   	
    	if(data.hudRunning)
    		data.hudRunning = false;
    }
    
    /** Suspend the HUD
     * 
     * @param display If true, display '*** Display Suspended***' in console
     */
    public void doSuspend(boolean display) {
    	doSuspend();
    	if(display)
    		parse.messageLine("*** Display Suspended ***");
    }
    
    /** Set the zoom level on the map */
    public void doZoom(int z)
    {
        // Let's try to keep the hex height even, since there are a lot of places that divide it by 2 - and it's an int
        prefs.hexHeight += z;
        if (prefs.hexHeight < 5)
            prefs.hexHeight = 5;
        if (prefs.hexHeight > 300)
            prefs.hexHeight = 300;
        
        tacMap.newPreferences(prefs);
    }

    /** Change the offset of the map in x */
    public void doChangeXOffset(int mod)
    {
        prefs.xOffset += mod;
        tacMap.newPreferences(prefs);
    }

    /** Change the offset of the map in y */
    public void doChangeYOffset(float mod)
    {
        prefs.yOffset += mod;
        tacMap.newPreferences(prefs);
    }

    /** Recenter the map */
    public void doCenterMap()
    {
        prefs.xOffset = 0;
        prefs.yOffset = 0;
        tacMap.newPreferences(prefs);
    }

    /** Show the weapons arcs */
    public void doShowArcs()
    {
        prefs.tacShowArcs = !prefs.tacShowArcs;
        
        taShowWeaponsArcs.setSelected(prefs.tacShowArcs);

        taMakeArcsWeaponRanges.setEnabled(prefs.tacShowArcs);
        taRetractArcRange.setEnabled(!prefs.makeArcsWeaponRange && prefs.tacShowArcs);
        taExtendArcRange.setEnabled(!prefs.makeArcsWeaponRange && prefs.tacShowArcs);
        
        tacMap.newPreferences(prefs);
    }

    /** Make the weapons arcs reflect actual weapon range */
    public void doMakeArcsWeaponRange()
    {
        prefs.makeArcsWeaponRange = !prefs.makeArcsWeaponRange;
        taMakeArcsWeaponRanges.setSelected(prefs.makeArcsWeaponRange);
        taRetractArcRange.setEnabled(!prefs.makeArcsWeaponRange);
        taExtendArcRange.setEnabled(!prefs.makeArcsWeaponRange);
    }

    /** Handle changing of arc length */
    public void doChangeArc(int d)
    {
        prefs.arcIndicatorRange += d;
        if (prefs.arcIndicatorRange < 1)
            prefs.arcIndicatorRange = 1;
        if (prefs.arcIndicatorRange > 200)
            prefs.arcIndicatorRange = 200;
        tacMap.newPreferences(prefs);
    }

    /** Show the hex numbers? */
    public void doShowHexNumbers()
    {
        prefs.tacShowHexNumbers = !prefs.tacShowHexNumbers;
        taShowHexNumbers.setSelected(prefs.tacShowHexNumbers);
        tacMap.newPreferences(prefs);
    }

    /** Show the unit names on the tactical map? */
    public void doShowUnitNames()
    {
        prefs.tacShowUnitNames = !prefs.tacShowUnitNames;
        taShowUnitNames.setSelected(prefs.tacShowUnitNames);
        tacMap.newPreferences(prefs);
    }

    /** Darken elevations on the map? */
    public void doDarkenElevations()
    {
        prefs.tacDarkenElev = !prefs.tacDarkenElev;
        taDarkenElevations.setSelected(prefs.tacDarkenElev);
        tacMap.newPreferences(prefs);
    }
    
    /** Show armor diagrams? */
    public void doShowArmorDiagrams()
    {
    	prefs.tacShowArmorDiagram = !prefs.tacShowArmorDiagram;
    	taShowArmorDiagram.setSelected(prefs.tacShowArmorDiagram);
    	tacMap.newPreferences(prefs);
    }
    
    /** Show LOS info? */
    public void doShowLOSInfo()
    {
    	prefs.tacShowLOSInfo = !prefs.tacShowLOSInfo;
    	taShowLOSInfo.setSelected(prefs.tacShowLOSInfo);
    	tacMap.newPreferences(prefs);
    }

    /** Show cliffs on the map? */
    public void doShowCliffs()
    {
        prefs.tacShowCliffs = !prefs.tacShowCliffs;
        taShowCliffs.setSelected(prefs.tacShowCliffs);
        tacMap.newPreferences(prefs);
    }

    /** Show indicators on the map? */
    public void doShowIndicators()
    {
        prefs.tacShowIndicators = !prefs.tacShowIndicators;
        taShowHeatArmoronTactical.setSelected(prefs.tacShowIndicators);
        tacMap.newPreferences(prefs);
    }

    // These two are for future expansion of setting the colors in the main window
    public void doGetBackgroundColor()
    {
        prefs.backgroundColor = JColorChooser.showDialog(this, "Choose a background color", prefs.backgroundColor);        
    }

    public void doGetForegroundColor()
    {
        prefs.foregroundColor = JColorChooser.showDialog(this, "Choose a foreground color", prefs.foregroundColor);
    }

    /** Did someone choose a connection menu item? */
    public boolean matchesConnectionMenu(String action)
    {
        boolean match = false;

        for (int i = 0; i < prefs.hosts.size(); i++)
        {
            MUHost			nextHost = (MUHost) prefs.hosts.get(i);
            
            if (action.equals(nextHost.getHost() + " " + nextHost.getPort()))
                match = true;
        }

        return match;
    }

    /** Start a new connection */
    public void doNewConnection(String action)
    {
        if (connected)			// Clear our current connection first
            stopConnection();
        
        StringTokenizer st = new StringTokenizer(action);
        startConnection(st.nextToken(), Integer.parseInt(st.nextToken().trim()));
    }

    /** Called when main font size changes */
    public void mainFontChanged()
    {
        
        mFont = new Font(prefs.mainFont, Font.PLAIN, prefs.mainFontSize);

        if (bsd != null)        	
            bsd.setFont(prefs.mainFontSize, mFont);
        if (textField != null)
            textField.setFont(mFont);
    }
    
    /** Read our prefs from disk */
    public void readPrefs()
    {
        prefs = new MUPrefs();
        prefs.defaultPrefs();

        PreferenceStore.load(prefs);

        // FIXME: New code doesn't know if this is the first launch or not, so
        // I'm going to disable showing release notes on first run for now.
        //
        // This really should be more like 'newVersion' to show release notes.
        // Also, we need a way to upgrade preferences when new versions suggest
        // better defaults.
        firstLaunch = false;

        // FIXME: Only change font if we load something from preferences.
        mainFontChanged();
    }
    
    /** Write our prefs to disk */
    public void writePrefs()
    {
        // We should really be listening for events to determine when the size/location of each
        // window has changed, and then they can set these values themselves
        // For now though, I'm putting the code here that just gets the info and puts it into the
        // prefs object

        prefs.mainLoc = getLocation();
        prefs.mainSizeX = getSize().width;
        prefs.mainSizeY = getSize().height;

        if (tacMap != null)
        {
            prefs.tacLoc = tacMap.getLocation();
            prefs.tacSizeX = tacMap.getSize().width;
            prefs.tacSizeY = tacMap.getSize().height;
        }

        if (conList != null)
        {
            prefs.contactsLoc = conList.getLocation();
            prefs.contactsSizeX = conList.getSize().width;
            prefs.contactsSizeY = conList.getSize().height;
        }

        if(status != null)
        {
            prefs.statusLoc = status.getLocation();
            prefs.statusSizeX = status.getSize().width;
            prefs.statusSizeY = status.getSize().height;
        }

        PreferenceStore.save(prefs);
    }
}
