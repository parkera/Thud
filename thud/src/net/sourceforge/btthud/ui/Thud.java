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

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.*;

public class Thud extends JFrame implements  ActionListener
{    
    Font	mFont = new Font("Monospaced", Font.PLAIN, 10);		// default main font
    AboutBox aboutBox = null;
    // Declarations for menus
    
    JMenuBar mainMenuBar = new JMenuBar();
	
    JMenu fileMenu;
    protected JMenuItem miLoadMap;
    protected JMenuItem miSaveMapAs;
    protected JMenuItem miReleaseNotes;
    protected JMenuItem miQuit;
	
    JMenu editMenu;
    protected JMenuItem miUndo;
    protected JMenuItem miCut;
    protected JMenuItem miCopy;
    protected JMenuItem miPaste;
    protected JMenuItem miClear;
    protected JMenuItem miSelectAll;
    protected JMenuItem miEraseCommand;
    protected JMenuItem miPreviousCommand;
    protected JCheckBoxMenuItem	miMuteMainWindow;

    JMenu mapMenu;
    protected JMenuItem miZoomIn;
    protected JMenuItem miZoomOut;
    
    protected JCheckBoxMenuItem miShowArcs;
    protected JCheckBoxMenuItem miMakeArcsWeaponRange;
    protected JMenuItem	miArcRetract;
    protected JMenuItem miArcExtend;

    protected JCheckBoxMenuItem miShowHexNumbers;
    protected JCheckBoxMenuItem miShowUnitNames;
    protected JCheckBoxMenuItem miDarkenElevations;
    protected JCheckBoxMenuItem miShowArmorDiagrams;

    protected JCheckBoxMenuItem	miShowCliffs;
    protected JCheckBoxMenuItem miShowIndicators;

    protected JMenuItem	miMoveRight, miMoveLeft, miMoveDown, miMoveUp, miCenterMap;

    JMenu windowMenu;
    protected JMenuItem miWindowContacts;
    protected JMenuItem miWindowStatus;
    protected JMenuItem miWindowTactical;    
    
    JMenu hudMenu;
    protected JMenuItem miStartStop;
    protected JMenuItem miPreferences;
    protected JMenuItem[] miConnections = null;
    protected JMenuItem miAddNewHost;
    protected JMenuItem miRemoveHost;
    protected JMenuItem miDisconnect;

    JMenu updateMenu;
    protected JCheckBoxMenuItem miFastUpdate;
    protected JCheckBoxMenuItem miNormalUpdate;
    protected JCheckBoxMenuItem miSlowUpdate;
    protected JMenuItem miSendTacticalUpdate;
    
    JMenu debugMenu;
    protected JMenuItem miDumpDocument;
    
    // ------------------
    
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

    boolean					firstLaunch = false;
    
    // ------------------------------------------------------------------------
    // MENU ITEM SETUP
    // ------------------------------------------------------------------------

    /**
      * Add a specified ActionListener to watch all the menu items.
      */
    protected void setupListeners(ActionListener l)
    {
    	miLoadMap.addActionListener(l);
    	miSaveMapAs.addActionListener(l);
        miReleaseNotes.addActionListener(l);
        miQuit.addActionListener(l);

        miUndo.addActionListener(l);
        miCut.addActionListener(l);
        miCopy.addActionListener(l);
        miPaste.addActionListener(l);
        miClear.addActionListener(l);
        miSelectAll.addActionListener(l);
        miEraseCommand.addActionListener(l);
        miPreviousCommand.addActionListener(l);
        miMuteMainWindow.addActionListener(l);

        miZoomIn.addActionListener(l);
        miZoomOut.addActionListener(l);

        miShowArcs.addActionListener(l);
        miMakeArcsWeaponRange.addActionListener(l);
        miArcRetract.addActionListener(l);
        miArcExtend.addActionListener(l);

        miShowHexNumbers.addActionListener(l);
        miShowUnitNames.addActionListener(l);
        miDarkenElevations.addActionListener(l);
        miShowArmorDiagrams.addActionListener(l);

        miShowCliffs.addActionListener(l);
        miShowIndicators.addActionListener(l);

        miMoveRight.addActionListener(l);
        miMoveLeft.addActionListener(l);
        miMoveDown.addActionListener(l);
        miMoveUp.addActionListener(l);
        miCenterMap.addActionListener(l);

        miStartStop.addActionListener(l);
        miPreferences.addActionListener(l);
        for (int i = 0; i < prefs.hosts.size(); i++)
            miConnections[i].addActionListener(l);
        miAddNewHost.addActionListener(l);
        miRemoveHost.addActionListener(l);
        miDisconnect.addActionListener(l);

        miFastUpdate.addActionListener(l);
        miNormalUpdate.addActionListener(l);
        miSlowUpdate.addActionListener(l);
        miSendTacticalUpdate.addActionListener(l);
        
        miWindowContacts.addActionListener(l);
        miWindowStatus.addActionListener(l);
        miWindowTactical.addActionListener(l);        
        
        miDumpDocument.addActionListener(l);
        
        // Create a listener that does a graceful shutdown of the whole shebang when this window is closed.
        addWindowListener(new WindowAdapter(){//<-----------
            public void windowClosing(WindowEvent we){
              doQuit();}});
    }
    
    /** File Menu Items */
    public void addFileMenuItems()
    {
        fileMenu = new JMenu("File");

        // ----------
        miLoadMap = new JMenuItem("Load Map...");
        fileMenu.add(miLoadMap).setEnabled(true);
        
        miSaveMapAs = new JMenuItem("Save Map As...");
        fileMenu.add(miSaveMapAs).setEnabled(true);
        
        fileMenu.addSeparator();
        
        miReleaseNotes = new JMenuItem("View Release Notes...");
        fileMenu.add(miReleaseNotes).setEnabled(true);

        fileMenu.addSeparator();
        
        miQuit = new JMenuItem("Quit");
        miQuit.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q,
                                                     Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenu.add(miQuit).setEnabled(true);
        
        mainMenuBar.add(fileMenu);
    }

    /** HUD Menu Items */
    public void addHUDMenuItems()
    {
        hudMenu = new JMenu("HUD");

        // Setup the connection menu items
        initConnectionMenus();
        
        // -------------
        
        miPreferences = new JMenuItem("Preferences...");
        hudMenu.add(miPreferences).setEnabled(true);

        hudMenu.addSeparator();
        
        miStartStop = new JMenuItem("Start/Stop");
        miStartStop.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G,
                                                          Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        hudMenu.add(miStartStop).setEnabled(false);

        hudMenu.addSeparator();

        for (int i = 0; i < prefs.hosts.size(); i++)
        {
            MUHost		nextHost = (MUHost) prefs.hosts.get(i);
            miConnections[i] = new JMenuItem(nextHost.toString());
            acceleratorForConnectionItem(miConnections[i], i);
            hudMenu.add(miConnections[i]).setEnabled(true);
        }

        hudMenu.addSeparator();

        miAddNewHost = new JMenuItem("Add New Host...");
        hudMenu.add(miAddNewHost).setEnabled(true);

        miRemoveHost = new JMenuItem("Remove Host...");
        hudMenu.add(miRemoveHost).setEnabled(true);
        
        hudMenu.addSeparator();

        miDisconnect = new JMenuItem("Disconnect");
        miDisconnect.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X,
                                                           java.awt.Event.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        hudMenu.add(miDisconnect).setEnabled(false);
        
        mainMenuBar.add(hudMenu);
    }

    /** Update Menu Items */
    public void addUpdateMenuItems()
    {
        updateMenu = new JMenu("Update");

        // ----------

        miFastUpdate = new JCheckBoxMenuItem("Fast Update Speed");
        miFastUpdate.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1,
                                                           java.awt.Event.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        updateMenu.add(miFastUpdate).setEnabled(true);
        miFastUpdate.setState(prefs.fastCommandUpdate == 1.0 ? true : false);

        miNormalUpdate = new JCheckBoxMenuItem("Normal Update Speed");
        miNormalUpdate.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2,
                                                           java.awt.Event.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        updateMenu.add(miNormalUpdate).setEnabled(true);
        miNormalUpdate.setState(prefs.fastCommandUpdate == 3.0 ? true : false);

        miSlowUpdate = new JCheckBoxMenuItem("Slow Update Speed");
        miSlowUpdate.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3,
                                                           java.awt.Event.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        updateMenu.add(miSlowUpdate).setEnabled(true);
        miSlowUpdate.setState(prefs.fastCommandUpdate == 5.0 ? true : false);

        // ----------

        updateMenu.addSeparator();

        
        miSendTacticalUpdate = new JMenuItem("Update Tactical Map Now");
        miSendTacticalUpdate.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N,
                                                              Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        updateMenu.add(miSendTacticalUpdate).setEnabled(false);

        // Disable the update menu until we're actually connected
        updateMenu.setEnabled(false);
        mainMenuBar.add(updateMenu);
    }
    
    /** Debug Menu Items */
    public void addDebugMenuItems()
    {
        debugMenu = new JMenu("Debug");

        // ----------

        miDumpDocument = new JMenuItem("Dump Document Structure");
        debugMenu.add(miDumpDocument).setEnabled(true);

        if (DEBUG == 1)
            mainMenuBar.add(debugMenu);
    }
    
    /** Window Menu items */
    public void addWindowMenuItems()
    {
    	windowMenu = new JMenu("Window");
    	
        miWindowContacts = new JMenuItem("Show Contacts Window");
        windowMenu.add(miWindowContacts).setEnabled(true);
        
        miWindowStatus = new JMenuItem("Show Status Window");
        windowMenu.add(miWindowStatus).setEnabled(true);

        miWindowTactical = new JMenuItem("Show Tactical Window");
        windowMenu.add(miWindowTactical).setEnabled(true);
        
        // Disable the window menu until we're actually connected
        windowMenu.setEnabled(false);
        mainMenuBar.add(windowMenu);
    }

    /** Utility function to get the proper accelerator for connection items in the HUD menu */
    protected void acceleratorForConnectionItem(JMenuItem mi, int i)
    {
        switch (i)
        {
            case 0:
                mi.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1,
                                                         Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                break;
            case 1:
                mi.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2,
                                                         Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                break;
            case 2:
                mi.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3,
                                                         Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                break;
            case 3:
                mi.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_4,
                                                         Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                break;
            case 4:
                mi.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_5,
                                                         Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                break;
            case 5:
                mi.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_6,
                                                         Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                break;
            case 6:
                mi.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_7,
                                                         Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                break;
            case 7:
                mi.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_8,
                                                         Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                break;
            case 8:
                mi.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_9,
                                                         Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                break;
            case 9:
                mi.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_0,
                                                         Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                break;
        }
    }

    /** Map Menu items (mostly options) */
    public void addMapMenuItems()
    {
        mapMenu = new JMenu("Map");

        // -------------

        miZoomIn = new JMenuItem("Zoom In");
        miZoomIn.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_CLOSE_BRACKET,
                                                       Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miZoomIn).setEnabled(true);
        
        miZoomOut = new JMenuItem("Zoom Out");
        miZoomOut.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_OPEN_BRACKET,
                                                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miZoomOut).setEnabled(true);

        // ----
        mapMenu.addSeparator();

        miShowArcs = new JCheckBoxMenuItem("Show Weapons Arcs", prefs.tacShowUnitNames);
        miShowArcs.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R,
                                                         Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miShowArcs).setEnabled(true);
        miShowArcs.setState(prefs.tacShowArcs);
        
        miMakeArcsWeaponRange = new JCheckBoxMenuItem("Make Arcs Weapon Ranges");
        miMakeArcsWeaponRange.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M,
                                                                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miMakeArcsWeaponRange).setEnabled(prefs.tacShowArcs);
        miMakeArcsWeaponRange.setState(prefs.makeArcsWeaponRange);
        
        miArcRetract = new JMenuItem("Retract Arc Range");
        miArcRetract.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SEMICOLON,
                                                           Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miArcRetract).setEnabled(!prefs.makeArcsWeaponRange && prefs.tacShowArcs);

        miArcExtend = new JMenuItem("Extend Arc Range");
        miArcExtend.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_QUOTE,
                                                          Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miArcExtend).setEnabled(!prefs.makeArcsWeaponRange && prefs.tacShowArcs);

        // ----
        mapMenu.addSeparator();
        
        miShowHexNumbers = new JCheckBoxMenuItem("Show Hex Numbers", prefs.tacShowHexNumbers);
        miShowHexNumbers.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B,
                                                               Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miShowHexNumbers).setEnabled(true);
        miShowHexNumbers.setState(prefs.tacShowHexNumbers);
        
        miShowUnitNames = new JCheckBoxMenuItem("Show Unit Names", prefs.tacShowUnitNames);
        miShowUnitNames.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U,
                                                              Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miShowUnitNames).setEnabled(true);
        miShowUnitNames.setState(prefs.tacShowUnitNames);
        
        miDarkenElevations = new JCheckBoxMenuItem("Darken Elevations", prefs.tacDarkenElev);
        miDarkenElevations.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D,
                                                                 Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miDarkenElevations).setEnabled(true);
        miDarkenElevations.setState(prefs.tacDarkenElev);

        miShowArmorDiagrams = new JCheckBoxMenuItem("Show Armor Diagram",prefs.tacShowArmorDiagram);
        mapMenu.add(miShowArmorDiagrams).setEnabled(true);
        miShowArmorDiagrams.setState(prefs.tacShowArmorDiagram);
        
        // ---
        mapMenu.addSeparator();

        miMoveLeft = new JMenuItem("Move Map Left");
        miMoveLeft.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A,
                                                         java.awt.Event.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miMoveLeft);

        miMoveRight = new JMenuItem("Move Map Right");
        miMoveRight.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D,
                                                          java.awt.Event.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miMoveRight);

        miMoveUp = new JMenuItem("Move Map Up");
        miMoveUp.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W,
                                                       java.awt.Event.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miMoveUp);

        miMoveDown = new JMenuItem("Move Map Down");
        miMoveDown.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,
                                                         java.awt.Event.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miMoveDown);

        miCenterMap = new JMenuItem("Center Map On Unit");
        miCenterMap.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R,
                                                          java.awt.Event.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miCenterMap);

        // ---
        mapMenu.addSeparator();

        miShowCliffs = new JCheckBoxMenuItem("Show Cliffs", prefs.tacShowCliffs);
        miShowCliffs.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F,
                                                           Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miShowCliffs).setEnabled(true);
        miShowCliffs.setState(prefs.tacShowCliffs);
        
        miShowIndicators = new JCheckBoxMenuItem("Show Heat/Armor on Tactical", prefs.tacShowIndicators);
        miShowIndicators.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I,
                                                           Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miShowIndicators).setEnabled(true);
        miShowIndicators.setState(prefs.tacShowIndicators);

        // Disable the map menu until we're actually connected
        mapMenu.setEnabled(false);
        mainMenuBar.add(mapMenu);
    }
    
	/** Edit menu items */
    public void addEditMenuItems()
    {
        editMenu = new JMenu("Edit");

        // -------------
        
        miUndo = new JMenuItem("Undo");
        miUndo.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z,
                                                     Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(miUndo).setEnabled(false);
        
        editMenu.addSeparator();

        miCut = new JMenuItem("Cut");
        miCut.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X,
                                                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(miCut).setEnabled(true);

        miCopy = new JMenuItem("Copy");
        miCopy.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C,
                                                     Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(miCopy).setEnabled(true);

        miPaste = new JMenuItem("Paste");
        miPaste.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V,
                                                      Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(miPaste).setEnabled(true);

        miClear = new JMenuItem("Clear");
        editMenu.add(miClear).setEnabled(false);

        // ----
        editMenu.addSeparator();

        miSelectAll = new JMenuItem("Select All");
        miSelectAll.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A,
                                                          Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(miSelectAll).setEnabled(true);

        // ----
        editMenu.addSeparator();
        
        miPreviousCommand = new JMenuItem("Repeat Previous Command");
        miPreviousCommand.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.Event.CTRL_MASK));
        editMenu.add(miPreviousCommand).setEnabled(true);

        miEraseCommand = new JMenuItem("Erase Current Command");
        miEraseCommand.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.Event.CTRL_MASK));
        editMenu.add(miEraseCommand).setEnabled(true);

        // ----
        editMenu.addSeparator();

        miMuteMainWindow = new JCheckBoxMenuItem("Mute Main Window Text");
        miMuteMainWindow.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SEMICOLON,
                                                               java.awt.Event.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(miMuteMainWindow).setEnabled(true);
        miMuteMainWindow.setState(false);		// Main window is never muted on starting up
        
        mainMenuBar.add(editMenu);
    }
    
    /** Initialize the connection items for the HUD menu */
    public void initConnectionMenus()
    {        
        miConnections = new JMenuItem[prefs.hosts.size()];
    }
    
    /** Add all of the menus */
    public void addMenus()
    {
        clearMenus();
        
        addFileMenuItems();
        addEditMenuItems();
        addMapMenuItems();
        addUpdateMenuItems();
        addHUDMenuItems();
        addDebugMenuItems();
        addWindowMenuItems();

        // Make sure our menus are listening to us
        setupListeners(this);
        
        setJMenuBar(mainMenuBar);
    }

    /** Clear menu bar */
    public void clearMenus()
    {
        mainMenuBar.removeAll();
    }
    
    protected void setupNewTextFields()
    {
        bsd = new BulkStyledDocument(prefs.mainFontSize, prefs.maxScrollbackSize, mFont);

        textField = new JTextField(80);
        textField.addActionListener(this);
        textField.setFont(mFont);
        textField.setEnabled(true);
        
        textPane = new JTextPane(bsd);
        textPane.setDocument(bsd);
        textPane.setBackground(Color.black);
        textPane.setEditable(false);
        textPane.setFont(mFont);
        // Add listener to give focus to textfield when click on textpane
        textPane.addFocusListener(new FocusListener() {
        		public void focusGained(FocusEvent f) {textField.grabFocus();}
        		public void focusLost(FocusEvent f) {}
        		});
        
        JScrollPane scrollPane = new JScrollPane(textPane,
                                                 JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        // Setup the text pane
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Setup the text field
        contentPane.add(textField, BorderLayout.SOUTH);

        // Add listeners for PageUp, PageDown, Home, End
        textField.getKeymap().addActionForKeyStroke (KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), new PageUpAction(textPane));               
        textField.getKeymap().addActionForKeyStroke (KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), new PageDownAction(textPane));
        textField.getKeymap().addActionForKeyStroke (KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), new HomeAction(textPane));        
        textField.getKeymap().addActionForKeyStroke (KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), new EndAction(textPane));        
        
    }

    // ------------------------------------------------------------------------
    // MAIN SETUP
    // ------------------------------------------------------------------------
    
    public Thud()
    {
        super("Thud");
        
        readPrefs();

        mainFontChanged();				// setup a new font
        
        this.getContentPane().setLayout(null);

        // Add all of our menus
        addMenus();

        // Create an about box
        aboutBox = new AboutBox();

        // Setup the main text areas
        setupNewTextFields();

        // Locate the window properly
        setSize(prefs.mainSizeX, prefs.mainSizeY);
        setLocation(prefs.mainLoc);
        setAlwaysOnTop(prefs.mainAlwaysOnTop);
        
        // Initilization strings
        Package			pkg = Package.getPackage("btthud.ui");
        String			buildNumber = null;
        if (pkg != null)
            buildNumber = pkg.getImplementationVersion();

        if (buildNumber == null)
            buildNumber = "Unknown";
        
        bsd.insertPlainString(" *** Thud, (c) 2001-2006 Anthony Parker & the THUD team   ***");
        bsd.insertPlainString(" *** bt-thud.sourceforge.net                              ***");
        bsd.insertPlainString(" *** Version: 1.3.2 Beta                                  ***\n");        

        // Show ourselves
        setVisible(true);

        // Show version notes
        if (firstLaunch)
        {
            doReleaseNotes();
            firstLaunch = false;
        }
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

            // Setup the rest of the helper classes, adding focus listeners for all
            FocusListener focusOnInput = new FocusListener() {
        		public void focusGained(FocusEvent f) {textField.grabFocus();}
        		public void focusLost(FocusEvent f) {}
        		};
        	// This does not work on the status window or contact list, and I have no idea why.
            status = new MUStatus(conn, data, prefs);
            status.addFocusListener(focusOnInput);            
                        
            conList = new MUContactList(conn, data, prefs);
            conList.addFocusListener(focusOnInput);            
            
            tacMap = new MUTacticalMap(conn, data, prefs);
            tacMap.addFocusListener(focusOnInput);                

            
            commands = new MUCommands(conn, data, prefs);
            
            // Let our parsing class know where to send commands
            parse.setCommands(commands);

            // Let the text field get the keyboard focus
            setVisible(true);
            textField.grabFocus();

            // Okay we're connected
            connected = true;

            // Enable some menu stuff
            miStartStop.setEnabled(true);
            miDisconnect.setEnabled(true);
            mapMenu.setEnabled(true);
            updateMenu.setEnabled(true);
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

            // Disable some menu stuff
            miStartStop.setEnabled(false);
            miDisconnect.setEnabled(false);
            mapMenu.setEnabled(false);
            updateMenu.setEnabled(false);
            windowMenu.setEnabled(false);
        }
    }

    // ------------------------------------------------------------------------
    // ACTION AND MENU HANDLING
    // ------------------------------------------------------------------------

    /** Repaint ourselves */
    public void paint(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            prefs.antiAliasText ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        super.paint(g2);
    }
     
    /** ActionListener interface (for menus) */
    public void actionPerformed(ActionEvent newEvent)
    {
    	if (newEvent.getActionCommand().equals(miLoadMap.getActionCommand())) doLoadMap();        
    	else if (newEvent.getActionCommand().equals(miSaveMapAs.getActionCommand())) doSaveMapAs();    	
    	else if (newEvent.getActionCommand().equals(miReleaseNotes.getActionCommand())) doReleaseNotes();
        else if (newEvent.getActionCommand().equals(miQuit.getActionCommand())) doQuit();
        else if (newEvent.getActionCommand().equals(miUndo.getActionCommand())) doUndo();
        else if (newEvent.getActionCommand().equals(miCut.getActionCommand())) doCut();
        else if (newEvent.getActionCommand().equals(miCopy.getActionCommand())) doCopy();
        else if (newEvent.getActionCommand().equals(miPaste.getActionCommand())) doPaste();
        else if (newEvent.getActionCommand().equals(miClear.getActionCommand())) doClear();
        else if (newEvent.getActionCommand().equals(miPreviousCommand.getActionCommand())) doPreviousCommand();
        else if (newEvent.getActionCommand().equals(miEraseCommand.getActionCommand())) doEraseCommand();
        else if (newEvent.getActionCommand().equals(miMuteMainWindow.getActionCommand())) doMuteMainWindow();
        else if (newEvent.getActionCommand().equals(miSelectAll.getActionCommand())) doSelectAll();
        else if (newEvent.getActionCommand().equals(miStartStop.getActionCommand())) doStartStop();
        else if (newEvent.getActionCommand().equals(miZoomIn.getActionCommand())) doZoom(5);
        else if (newEvent.getActionCommand().equals(miZoomOut.getActionCommand())) doZoom(-5);
        else if (newEvent.getActionCommand().equals(miMakeArcsWeaponRange.getActionCommand())) doMakeArcsWeaponRange();
        else if (newEvent.getActionCommand().equals(miArcRetract.getActionCommand())) doChangeArc(-1);
        else if (newEvent.getActionCommand().equals(miArcExtend.getActionCommand())) doChangeArc(1);
        else if (newEvent.getActionCommand().equals(miShowHexNumbers.getActionCommand())) doShowHexNumbers();
        else if (newEvent.getActionCommand().equals(miShowUnitNames.getActionCommand())) doShowUnitNames();
        else if (newEvent.getActionCommand().equals(miShowArcs.getActionCommand())) doShowArcs();
        else if (newEvent.getActionCommand().equals(miDarkenElevations.getActionCommand())) doDarkenElevations();
        else if (newEvent.getActionCommand().equals(miShowArmorDiagrams.getActionCommand())) doShowArmorDiagrams();
        else if (newEvent.getActionCommand().equals(miShowCliffs.getActionCommand())) doShowCliffs();
        else if (newEvent.getActionCommand().equals(miShowIndicators.getActionCommand())) doShowIndicators();
        else if (newEvent.getActionCommand().equals(miMoveLeft.getActionCommand())) doChangeXOffset(-1);
        else if (newEvent.getActionCommand().equals(miMoveRight.getActionCommand())) doChangeXOffset(1);
        else if (newEvent.getActionCommand().equals(miMoveUp.getActionCommand())) doChangeYOffset(-1);
        else if (newEvent.getActionCommand().equals(miMoveDown.getActionCommand())) doChangeYOffset(1);
        else if (newEvent.getActionCommand().equals(miCenterMap.getActionCommand())) doCenterMap();
        else if (newEvent.getActionCommand().equals(miPreferences.getActionCommand())) doPreferences();
        else if (matchesConnectionMenu(newEvent.getActionCommand())) doNewConnection(newEvent.getActionCommand());
        else if (newEvent.getActionCommand().equals(miAddNewHost.getActionCommand())) doAddNewHost();
        else if (newEvent.getActionCommand().equals(miRemoveHost.getActionCommand())) doRemoveHost();
        else if (newEvent.getActionCommand().equals(miDisconnect.getActionCommand())) doDisconnect();
        else if (newEvent.getActionCommand().equals(miFastUpdate.getActionCommand())) doChangeUpdate(MUPrefs.FAST_UPDATE);
        else if (newEvent.getActionCommand().equals(miNormalUpdate.getActionCommand())) doChangeUpdate(MUPrefs.NORMAL_UPDATE);
        else if (newEvent.getActionCommand().equals(miSlowUpdate.getActionCommand())) doChangeUpdate(MUPrefs.SLOW_UPDATE);
        else if (newEvent.getActionCommand().equals(miSendTacticalUpdate.getActionCommand())) doSendTacUpdate();
        else if (newEvent.getActionCommand().equals(miDumpDocument.getActionCommand())) doDumpDocumentStructure();
        else if (newEvent.getActionCommand().equals(miWindowContacts.getActionCommand())) doWindowContacts();    	
        else if (newEvent.getActionCommand().equals(miWindowStatus.getActionCommand())) doWindowStatus();
        else if (newEvent.getActionCommand().equals(miWindowTactical.getActionCommand())) doWindowTactical();    	
        else		// this is sorta bad, we assume that if it's not a menu item they hit return in the text field. need to fix
        {
            String text = textField.getText();
            try
            {
            	if (conn != null && text != null && conn.connected)
                {
                    if (prefs.echoCommands)
                        parse.commandLine("> " + text); 
                    if (!parse.isHudCommand(text))
                        conn.sendCommand(text);

                    // Clear the text field
                    textField.setText("");
                    
                    // Add this command to our history
                    if (commandHistory.size() == 0 || (String) commandHistory.getLast() != text)
                        commandHistory.add(text);

                    // If we're over our preferred history size, remove the first
                    if (commandHistory.size() > prefs.commandHistory)
                        commandHistory.removeFirst();

                    // Reset our history location counter
                    historyLoc = 1;
                } else {
                	// Trying to talk while not connected
                	bsd.insertMessageString("*** Can't Send Text: Not Connected ***");            	
                }
            }
            catch (Exception e)
            {
                System.out.println("Error: " + e);
            }
        }
    }

    /** Display our about box */
    public void doAbout() {
        aboutBox.setResizable(false);
        aboutBox.setVisible(true);        
    }

    /** Show the release notes */
    public void doLoadMap()
    {
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(this);
        
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        	data.mapFileName = fc.getSelectedFile().getAbsolutePath();
        	if(data.loadMapFromDisk()) {
        		parse.messageLine("*** Map " + data.mapFileName + " loaded successfully ***");
        	} else {
        		parse.messageLine("*** Error loading map " + data.mapFileName + " ***");
        	}
        }
    }
    
    public void doSaveMapAs()
    {
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(this);
        
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        	data.mapFileName = fc.getSelectedFile().getAbsolutePath();
        	if(data.saveMapToDisk()) {
        		parse.messageLine("*** Map " + data.mapFileName + " saved successfully ***");
        	} else {
        		parse.messageLine("*** Error saving map " + data.mapFileName + " ***");
        	}
        }
    	
    }

    
    /** Show the release notes */
    public void doReleaseNotes()
    {
        ReleaseNotesDialog		notesDialog = new ReleaseNotesDialog(this, true);
        notesDialog.setVisible(true);
    }

    /** Quit cleanly */
    public void doQuit()
    {    	
        try
        {
            // Close our connection
            if (connected)
                stopConnection();

            // Write out our preferences file
            writePrefs();
            
            // Write out map
            //if(data != null)
            	//data.saveMapToDisk();
        }
        catch (Exception e)
        {
            System.out.println("Error: doQuit: " + e);
        }

        // We're done
        System.exit(0);
    }
    
    /** Display the preferences dialog */
    public void doPreferences()
    {
        PrefsDialog		prefsDialog = new PrefsDialog(this, true);
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
    // Here's a whole slew of things we don't support	
    public void doUndo() {}
	
    public void doCut() {}
	
    public void doCopy() {}
	
    public void doPaste() {}
	
    public void doClear() {}
	
    public void doSelectAll() {}

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
        textField.setText("");
    }

    /** Mute the text in the main window */
    public void doMuteMainWindow()
    {
        data.mainWindowMuted = !data.mainWindowMuted;
        miMuteMainWindow.setState(data.mainWindowMuted);

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

             miSendTacticalUpdate.setEnabled(true);    		
    	}    	
    }
    
    /** Stops the HUD. */
    public void doStop() {
    	if(data.hudStarted) {//only stop if we're started
    		data.hudStarted = false;
            parse.messageLine("*** Display Stopped ***");
            data.hudRunning = false;
            commands.endTimers();            
            miSendTacticalUpdate.setEnabled(false);
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
        
        miShowArcs.setState(prefs.tacShowArcs);

        miMakeArcsWeaponRange.setEnabled(prefs.tacShowArcs);
        miArcRetract.setEnabled(!prefs.makeArcsWeaponRange && prefs.tacShowArcs);
        miArcExtend.setEnabled(!prefs.makeArcsWeaponRange && prefs.tacShowArcs);
        
        tacMap.newPreferences(prefs);
    }

    /** Make the weapons arcs reflect actual weapon range */
    public void doMakeArcsWeaponRange()
    {
        prefs.makeArcsWeaponRange = !prefs.makeArcsWeaponRange;
        miMakeArcsWeaponRange.setState(prefs.makeArcsWeaponRange);
        miArcRetract.setEnabled(!prefs.makeArcsWeaponRange);
        miArcExtend.setEnabled(!prefs.makeArcsWeaponRange);
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
        miShowHexNumbers.setState(prefs.tacShowHexNumbers);
        tacMap.newPreferences(prefs);
    }

    /** Show the unit names on the tactical map? */
    public void doShowUnitNames()
    {
        prefs.tacShowUnitNames = !prefs.tacShowUnitNames;
        miShowUnitNames.setState(prefs.tacShowUnitNames);
        tacMap.newPreferences(prefs);
    }

    /** Darken elevations on the map? */
    public void doDarkenElevations()
    {
        prefs.tacDarkenElev = !prefs.tacDarkenElev;
        miDarkenElevations.setState(prefs.tacDarkenElev);
        tacMap.newPreferences(prefs);
    }
    
    /** Show armor diagrams? */
    public void doShowArmorDiagrams()
    {
    	prefs.tacShowArmorDiagram = !prefs.tacShowArmorDiagram;
    	miShowArmorDiagrams.setState(prefs.tacShowArmorDiagram);
    	tacMap.newPreferences(prefs);
    }

    /** Show cliffs on the map? */
    public void doShowCliffs()
    {
        prefs.tacShowCliffs = !prefs.tacShowCliffs;
        miShowCliffs.setState(prefs.tacShowCliffs);
        tacMap.newPreferences(prefs);
    }

    /** Show indicators on the map? */
    public void doShowIndicators()
    {
        prefs.tacShowIndicators = !prefs.tacShowIndicators;
        miShowIndicators.setState(prefs.tacShowIndicators);
        tacMap.newPreferences(prefs);
    }

    public void doDumpDocumentStructure()
    {
        bsd.dump(System.out);        
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

    /** Disconnect from a connection */
    public void doDisconnect()
    {
        stopConnection();
    }

    /** Change the update speed */
    public void doChangeUpdate(int whichSpeed)
    {
        switch (whichSpeed)
        {
            case MUPrefs.FAST_UPDATE:
                prefs.fastCommandUpdate = 1.0;
                prefs.mediumCommandUpdate = 2.0;
                prefs.slowCommandUpdate = 3.0;
                prefs.slugCommandUpdate = 15.0;
                miFastUpdate.setState(true);
                miNormalUpdate.setState(false);
                miSlowUpdate.setState(false);
                break;

            case MUPrefs.NORMAL_UPDATE:
                prefs.fastCommandUpdate = 3.0;
                prefs.mediumCommandUpdate = 5.0;
                prefs.slowCommandUpdate = 10.0;
                prefs.slugCommandUpdate = 30.0;
                miFastUpdate.setState(false);
                miNormalUpdate.setState(true);
                miSlowUpdate.setState(false);
                break;

            case MUPrefs.SLOW_UPDATE:
                prefs.fastCommandUpdate = 5.0;
                prefs.mediumCommandUpdate = 10.0;
                prefs.slowCommandUpdate = 15.0;
                prefs.slugCommandUpdate = 45.0;
                miFastUpdate.setState(false);
                miNormalUpdate.setState(false);
                miSlowUpdate.setState(true);
                break;
        }
    }

    /** Forces a tactical update */
    public void doSendTacUpdate()
    {
        commands.forceTactical();
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
        try
        {
            File	prefsFile = new File("thudprefs.prf");

            if (prefsFile.createNewFile())
            {
                prefs = new MUPrefs();
                prefs.defaultPrefs();

                firstLaunch = true;
            }
            else
            {
                FileInputStream		fis = new FileInputStream(prefsFile);
                ObjectInputStream 	ois = new ObjectInputStream(fis);
                
                prefs = (MUPrefs) ois.readObject();
                mainFontChanged();
                fis.close();
            }
        }
        catch (Exception e)
        {
            System.out.println("Error reading preferences file. You probably have an updated version of Thud with incompatible preferences file. Creating new file with default preferences...");            
            // Maybe the file format changed. Let's just create some new prefs
            prefs = new MUPrefs();
            prefs.defaultPrefs();

            firstLaunch = true;
        }
        
    }
    
    /** Re-show contacts window */
    public void doWindowContacts() {
    	conList.setVisible(true);
    }
    
    /** Re-show status window */
    public void doWindowStatus() {
    	status.setVisible(true);
    }
    
    /** Re-show tactical window */
    public void doWindowTactical() {
    	tacMap.setVisible(true);
    }

    /** Write our prefs to disk */
    public void writePrefs()
    {
        try
        {
            File	prefsFile = new File("thudprefs.prf");
            prefsFile.createNewFile();

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

            FileOutputStream	fis = new FileOutputStream(prefsFile);
            ObjectOutputStream 	oos = new ObjectOutputStream(fis);
            oos.writeObject(prefs);
            oos.flush();
            fis.close();
        }
        catch (Exception e)
        {
            System.out.println("Error: writePrefs: " + e);
        }
    }

    public static void main(String args[]) {
        new Thud();
    }

}
