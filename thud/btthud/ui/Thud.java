//
//  Thud.java
//
//  Created by asp on Wed Nov 28 2001.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//
//
package btthud.ui;

import btthud.data.*;
import btthud.engine.*;
import btthud.util.*;

import java.io.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

import java.lang.*;
import java.util.*;

public class Thud extends JFrame implements  ActionListener
{

    int					numHosts;
    String[]			hosts = null;
    int[]				ports = null;

    
    Font	mFont = new Font("Monospaced", Font.PLAIN, 10);		// default main font
    AboutBox aboutBox = null;
    // Declarations for menus
    
    static final JMenuBar mainMenuBar = new JMenuBar();
	
    static final JMenu fileMenu = new JMenu("File");
    protected JMenuItem miNew;
    protected JMenuItem miOpen;
    protected JMenuItem miClose;
    protected JMenuItem miSave;
    protected JMenuItem miSaveAs;
    protected JMenuItem miQuit;
	
    static final JMenu editMenu = new JMenu("Edit");
    protected JMenuItem miUndo;
    protected JMenuItem miCut;
    protected JMenuItem miCopy;
    protected JMenuItem miPaste;
    protected JMenuItem miClear;
    protected JMenuItem miSelectAll;
    protected JMenuItem miEraseCommand;
    protected JMenuItem miPreviousCommand;

    static final JMenu mapMenu = new JMenu("Map");
    protected JMenuItem miZoomIn;
    protected JMenuItem miZoomOut;
    
    protected JCheckBoxMenuItem miShowArcs;
    protected JCheckBoxMenuItem miMakeArcsWeaponRange;
    protected JMenuItem	miArcRetract;
    protected JMenuItem miArcExtend;

    protected JCheckBoxMenuItem miShowHexNumbers;
    protected JCheckBoxMenuItem miShowUnitNames;
    protected JCheckBoxMenuItem miDarkenElevations;

    protected JCheckBoxMenuItem	miShowCliffs;
    protected JCheckBoxMenuItem miShowIndicators;

    protected JMenuItem	miMoveRight, miMoveLeft, miMoveDown, miMoveUp, miCenterMap;

    static final JMenu hudMenu = new JMenu("HUD");
    protected JMenuItem miStartStop;
    protected JMenuItem miPreferences;
    protected JMenuItem[] miConnections = null;
    protected JMenuItem miDisconnect;
    
    // ------------------
    
    JTextField 				textField;
    JTextPane				textPane;
    BulkStyledDocument		bsd;

    boolean					connected = false;
    MUConnection 			conn = null;
    MUParse					parse = null;
    MUData					data = null;
    MUContactList			conList = null;
    MUTacticalMap			tacMap = null;
    MUPrefs					prefs = null;
    MUCommands				commands = null;

    LinkedList				commandHistory = new LinkedList();
    int						historyLoc = 1;							// how far we are from end of history list
    
    // ------------------------------------------------------------------------
    // MENU ITEM SETUP
    // ------------------------------------------------------------------------

    // -----------------------
    // File Menu Items
    public void addFileMenuItems() {
        miNew = new JMenuItem ("New");
        miNew.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N,
                                                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenu.add(miNew).setEnabled(false);
        miNew.addActionListener(this);

        miOpen = new JMenuItem ("Open...");
        miOpen.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O,
                                                     Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenu.add(miOpen).setEnabled(false);
        miOpen.addActionListener(this);
		
        miClose = new JMenuItem ("Close");
        miClose.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W,
                                                      Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenu.add(miClose).setEnabled(true);
        miClose.addActionListener(this);
		
        miSave = new JMenuItem ("Save");
        miSave.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,
                                                     Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenu.add(miSave).setEnabled(false);
        miSave.addActionListener(this);
		
        miSaveAs = new JMenuItem ("Save As...");
        fileMenu.add(miSaveAs).setEnabled(false);
        miSaveAs.addActionListener(this);

        // ----
        fileMenu.addSeparator();

        miQuit = new JMenuItem("Quit");
        miQuit.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q,
                                                     Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenu.add(miQuit).setEnabled(true);
        miQuit.addActionListener(this);
        
        mainMenuBar.add(fileMenu);
    }

    // -----------------------
    // HUD Menu Items
    public void addHUDMenuItems()
    {
        miPreferences = new JMenuItem("Preferences...");
        hudMenu.add(miPreferences).setEnabled(true);
        miPreferences.addActionListener(this);

        hudMenu.addSeparator();
        
        miStartStop = new JMenuItem("Start/Stop");
        miStartStop.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G,
                                                          Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        hudMenu.add(miStartStop).setEnabled(false);
        miStartStop.addActionListener(this);

        hudMenu.addSeparator();
        
        for (int i = 0; i < numHosts; i++)
        {
            miConnections[i] = new JMenuItem(hosts[i] + " " + ports[i]);
            acceleratorForConnectionItem(miConnections[i], i);
            hudMenu.add(miConnections[i]).setEnabled(true);
            miConnections[i].addActionListener(this);
        }

        hudMenu.addSeparator();

        miDisconnect = new JMenuItem("Disconnect");
        miDisconnect.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X,
                                                           java.awt.Event.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        hudMenu.add(miDisconnect).setEnabled(false);
        miDisconnect.addActionListener(this);
        
        mainMenuBar.add(hudMenu);
    }

    // -----------------------
    // Utility function to get the proper accelerator for connection items in the HUD menu
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

    // -----------------------
    // Map Menu items (mostly options)
    public void addMapMenuItems()
    {
        miZoomIn = new JMenuItem("Zoom In");
        miZoomIn.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_CLOSE_BRACKET,
                                                       Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miZoomIn).setEnabled(true);
        miZoomIn.addActionListener(this);
        
        miZoomOut = new JMenuItem("Zoom Out");
        miZoomOut.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_OPEN_BRACKET,
                                                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miZoomOut).setEnabled(true);
        miZoomOut.addActionListener(this);

        // ----
        mapMenu.addSeparator();

        miShowArcs = new JCheckBoxMenuItem("Show Weapons Arcs", prefs.tacShowUnitNames);
        miShowArcs.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R,
                                                         Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miShowArcs).setEnabled(true);
        miShowArcs.addActionListener(this);
        miShowArcs.setState(prefs.tacShowArcs);
        
        miMakeArcsWeaponRange = new JCheckBoxMenuItem("Make Arcs Weapon Ranges");
        miMakeArcsWeaponRange.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M,
                                                                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miMakeArcsWeaponRange).setEnabled(prefs.tacShowArcs);
        miMakeArcsWeaponRange.addActionListener(this);
        miMakeArcsWeaponRange.setState(prefs.makeArcsWeaponRange);
        
        miArcRetract = new JMenuItem("Retract Arc Range");
        miArcRetract.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SEMICOLON,
                                                           Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miArcRetract).setEnabled(!prefs.makeArcsWeaponRange && prefs.tacShowArcs);
        miArcRetract.addActionListener(this);

        miArcExtend = new JMenuItem("Extend Arc Range");
        miArcExtend.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_QUOTE,
                                                          Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miArcExtend).setEnabled(!prefs.makeArcsWeaponRange && prefs.tacShowArcs);
        miArcExtend.addActionListener(this);

        // ----
        mapMenu.addSeparator();
        
        miShowHexNumbers = new JCheckBoxMenuItem("Show Hex Numbers", prefs.tacShowHexNumbers);
        miShowHexNumbers.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B,
                                                               Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miShowHexNumbers).setEnabled(true);
        miShowHexNumbers.addActionListener(this);
        miShowHexNumbers.setState(prefs.tacShowHexNumbers);
        
        miShowUnitNames = new JCheckBoxMenuItem("Show Unit Names", prefs.tacShowUnitNames);
        miShowUnitNames.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U,
                                                              Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miShowUnitNames).setEnabled(true);
        miShowUnitNames.addActionListener(this);
        miShowUnitNames.setState(prefs.tacShowUnitNames);
        
        miDarkenElevations = new JCheckBoxMenuItem("Darken Elevations", prefs.tacDarkenElev);
        miDarkenElevations.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D,
                                                                 Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miDarkenElevations).setEnabled(true);
        miDarkenElevations.addActionListener(this);
        miDarkenElevations.setState(prefs.tacDarkenElev);

        // ---
        mapMenu.addSeparator();

        miMoveLeft = new JMenuItem("Move Map Left");
        miMoveLeft.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A,
                                                         java.awt.Event.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miMoveLeft);
        miMoveLeft.addActionListener(this);

        miMoveRight = new JMenuItem("Move Map Right");
        miMoveRight.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D,
                                                          java.awt.Event.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miMoveRight);
        miMoveRight.addActionListener(this);

        miMoveUp = new JMenuItem("Move Map Up");
        miMoveUp.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W,
                                                       java.awt.Event.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miMoveUp);
        miMoveUp.addActionListener(this);

        miMoveDown = new JMenuItem("Move Map Down");
        miMoveDown.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,
                                                         java.awt.Event.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miMoveDown);
        miMoveDown.addActionListener(this);

        miCenterMap = new JMenuItem("Center Map On Unit");
        miCenterMap.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R,
                                                          java.awt.Event.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miCenterMap);
        miCenterMap.addActionListener(this);

        // ---
        mapMenu.addSeparator();

        miShowCliffs = new JCheckBoxMenuItem("Show Cliffs", prefs.tacShowCliffs);
        miShowCliffs.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F,
                                                           Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miShowCliffs).setEnabled(true);
        miShowCliffs.addActionListener(this);
        miShowCliffs.setState(prefs.tacShowCliffs);
        
        miShowIndicators = new JCheckBoxMenuItem("Show Heat/Armor on Tactical", prefs.tacShowIndicators);
        miShowIndicators.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I,
                                                           Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miShowIndicators).setEnabled(true);
        miShowIndicators.addActionListener(this);
        miShowIndicators.setState(prefs.tacShowIndicators);

        // Disable the map menu until we're actually connected
        mapMenu.setEnabled(false);
        mainMenuBar.add(mapMenu);
    }

    // -----------------------
	// Edit menu items
    public void addEditMenuItems() {
        miUndo = new JMenuItem("Undo");
        miUndo.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z,
                                                     Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(miUndo).setEnabled(false);
        miUndo.addActionListener(this);
        editMenu.addSeparator();

        miCut = new JMenuItem("Cut");
        miCut.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X,
                                                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(miCut).setEnabled(true);
        miCut.addActionListener(this);

        miCopy = new JMenuItem("Copy");
        miCopy.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C,
                                                     Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(miCopy).setEnabled(true);
        miCopy.addActionListener(this);

        miPaste = new JMenuItem("Paste");
        miPaste.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V,
                                                      Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(miPaste).setEnabled(true);
        miPaste.addActionListener(this);

        miClear = new JMenuItem("Clear");
        editMenu.add(miClear).setEnabled(false);
        miClear.addActionListener(this);

        // ----
        editMenu.addSeparator();

        miSelectAll = new JMenuItem("Select All");
        miSelectAll.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A,
                                                          Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(miSelectAll).setEnabled(true);
        miSelectAll.addActionListener(this);

        // ----
        editMenu.addSeparator();
        
        miPreviousCommand = new JMenuItem("Repeat Previous Command");
        miPreviousCommand.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.Event.CTRL_MASK));
        editMenu.add(miPreviousCommand).setEnabled(true);
        miPreviousCommand.addActionListener(this);

        miEraseCommand = new JMenuItem("Erase Current Command");
        miEraseCommand.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.Event.CTRL_MASK));
        editMenu.add(miEraseCommand).setEnabled(true);
        miEraseCommand.addActionListener(this);
        
        mainMenuBar.add(editMenu);
    }

    // -----------------------
    // Initialize the connection items for the HUD menu
    public void initConnectionMenus()
    {
        numHosts = 1;
        hosts = new String[numHosts];
        ports = new int[numHosts];
        miConnections = new JMenuItem[numHosts];

        hosts[0] = "btech.dhs.org";
        ports[0] = 3030;

    }
    
    // -----------------------
    // Add all of the menus
    public void addMenus()
    {
        addFileMenuItems();
        addEditMenuItems();
        addMapMenuItems();
        addHUDMenuItems();
        setJMenuBar(mainMenuBar);
    }

    // ------------------------------------------------------------------------
    // TEXT FIELD SETUP
    // ------------------------------------------------------------------------

    protected void setupNewTextFields()
    {
        textField = new JTextField(80);
        textField.addActionListener(this);
        textField.setFont(mFont);
        textField.setEnabled(true);

        bsd = new BulkStyledDocument(prefs.mainFontSize);

        textPane = new JTextPane(bsd);
        textPane.setDocument(bsd);
        textPane.setBackground(Color.black);
        textPane.setEditable(false);
        textPane.setFont(mFont);

        JScrollPane scrollPane = new JScrollPane(textPane,
                                                 JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                                 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        // Setup the text pane
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Setup the text field
        contentPane.add(textField, BorderLayout.SOUTH);
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

        //this.addWindowListener(new WindowListener
        // Only works for Java 1.4
        //setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Setup the connection menus
        initConnectionMenus();

        // Add all of our menus
        addMenus();

        // Create an about box
        aboutBox = new AboutBox();

        // Setup the main text areas
        setupNewTextFields();

        // Locate the window properly
        setSize(prefs.mainSizeX, prefs.mainSizeY);
        setLocation(prefs.mainLoc);
        
        // Initilization strings
        bsd.insertPlainString(" *** Thud, (c) 2001-2002 Anthony Parker <asp@mac.com> ***");
        bsd.insertPlainString(" *** bt-thud.sourceforge.net                          ***");
        bsd.insertPlainString(" *** Version: 1.0b6 (Beta)                            ***");
        bsd.insertPlainString(" *** Contact Tony @ 3030MUX with questions/comments   ***\n");

        // And finally, show ourselves
        setVisible(true);
    }
    
    // -----------------
    // Start the connection, including creating new objects
    public void startConnection(String host, int port)
    {        
        if (connected)		// We must already have a connection. Let's clean up that one, then go to this new one
            stopConnection();

        try
        {
            // Setup some of the helper classes
            data = new MUData();
            parse = new MUParse(textPane, data, bsd, prefs);

            parse.messageLine("*** Connecting... ***");
            
            // Setup the connection
            conn = new MUConnection(host, port, parse, this);

            // Setup the rest of the helper classes
            conList = new MUContactList(conn, data, prefs);
            tacMap = new MUTacticalMap(conn, data, prefs);
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
            
            commands.endTimers();
            
            conList.pleaseStop();
            tacMap.pleaseStop();
            conn.pleaseStop();

            parse.messageLine("*** Disconnected ***");

            // Disable some menu stuff
            miStartStop.setEnabled(false);
            miDisconnect.setEnabled(false);
            mapMenu.setEnabled(false);
        }
    }

    // ------------------------------------------------------------------------
    // ACTION AND MENU HANDLING
    // ------------------------------------------------------------------------

    // -----------------------
    // Guess we need to repaint ourselves
    public void paint(Graphics g) {
        super.paint(g);
    }
    
    // -----------------------
    // ActionListener interface (for menus)
    public void actionPerformed(ActionEvent newEvent)
    {
        if (newEvent.getActionCommand().equals(miNew.getActionCommand())) doNew();
        else if (newEvent.getActionCommand().equals(miOpen.getActionCommand())) doOpen();
        else if (newEvent.getActionCommand().equals(miClose.getActionCommand())) doClose();
        else if (newEvent.getActionCommand().equals(miSave.getActionCommand())) doSave();
        else if (newEvent.getActionCommand().equals(miSaveAs.getActionCommand())) doSaveAs();
        else if (newEvent.getActionCommand().equals(miQuit.getActionCommand())) handleQuit();
        else if (newEvent.getActionCommand().equals(miUndo.getActionCommand())) doUndo();
        else if (newEvent.getActionCommand().equals(miCut.getActionCommand())) doCut();
        else if (newEvent.getActionCommand().equals(miCopy.getActionCommand())) doCopy();
        else if (newEvent.getActionCommand().equals(miPaste.getActionCommand())) doPaste();
        else if (newEvent.getActionCommand().equals(miClear.getActionCommand())) doClear();
        else if (newEvent.getActionCommand().equals(miPreviousCommand.getActionCommand())) doPreviousCommand();
        else if (newEvent.getActionCommand().equals(miEraseCommand.getActionCommand())) doEraseCommand();
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
        else if (newEvent.getActionCommand().equals(miShowCliffs.getActionCommand())) doShowCliffs();
        else if (newEvent.getActionCommand().equals(miShowIndicators.getActionCommand())) doShowIndicators();
        else if (newEvent.getActionCommand().equals(miMoveLeft.getActionCommand())) doChangeXOffset(-1f);
        else if (newEvent.getActionCommand().equals(miMoveRight.getActionCommand())) doChangeXOffset(1f);
        else if (newEvent.getActionCommand().equals(miMoveUp.getActionCommand())) doChangeYOffset(-1f);
        else if (newEvent.getActionCommand().equals(miMoveDown.getActionCommand())) doChangeYOffset(1f);
        else if (newEvent.getActionCommand().equals(miCenterMap.getActionCommand())) doCenterMap();
        else if (newEvent.getActionCommand().equals(miPreferences.getActionCommand())) doPreferences();
        else if (matchesConnectionMenu(newEvent.getActionCommand())) doNewConnection(newEvent.getActionCommand());
        else if (newEvent.getActionCommand().equals(miDisconnect.getActionCommand())) doDisconnect();
        else		// this is sorta bad, we assume that if it's not a menu item they hit return in the text field. need to fix
        {
            String text = textField.getText();
            try
            {
                if (conn != null && text != null)
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
                }
            }
            catch (Exception e)
            {
                System.out.println("Error: " + e);
            }
        }
    }

    // --------------------------
    // Display our about box
    public void handleAbout() {
        aboutBox.setResizable(false);
        aboutBox.setVisible(true);
        aboutBox.show();
    }

    // -----------------------
    // Quit cleanly
    public void handleQuit()
    {
        try
        {
            // Close our connection
            if (connected)
                stopConnection();

            // Write out our preferences file
            writePrefs();
        }
        catch (Exception e)
        {
            System.out.println("Error: handleQuit: " + e);
        }

        // We're done
        System.exit(0);
    }
    
    // -----------------------
    // Display the preferences dialog
    public void doPreferences()
    {
        PrefsDialog		prefsDialog = new PrefsDialog(this, true);		// the middle boolean is to see if the dialog is modal
        prefsDialog.setVisible(true);

        // Send messages around in case something changed
        if (tacMap != null)
            tacMap.newPreferences(prefs);
        if (conList != null)
            conList.newPreferences(prefs);
        
        mainFontChanged();
    }
    
    // -----------------------
    // Here's a whole slew of things we don't support
    public void doNew() {}
	
    public void doOpen() {}
	
    public void doClose() {}
	
    public void doSave() {}
	
    public void doSaveAs() {}
	
    public void doUndo() {}
	
    public void doCut() {}
	
    public void doCopy() {}
	
    public void doPaste() {}
	
    public void doClear() {}
	
    public void doSelectAll() {}

    // -----------------------
    // Insert the previous command into the text box
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

    // -----------------------
    // Erase the current command from the text box
    public void doEraseCommand()
    {
        textField.setText("");
    }

    // -----------------------
    // Turn the HUD on/off
    public void doStartStop()
    {
        if (connected)
        {
            data.hudRunning = !data.hudRunning;		// turn the HUD actions on/off
            if (data.hudRunning)
            {
                parse.messageLine("*** Display Started ***");

                // Set our session key to something not too easily duplicated
                String	sessionKey = String.valueOf(Calendar.getInstance().get(Calendar.MILLISECOND));
                parse.setSessionKey(sessionKey);

                try
                {
                    // Set the key
                    conn.sendCommand("hudinfo key=" + sessionKey);
                }
                catch (Exception e)
                {
                    System.out.println("Error: hudinfo key set: " + e);
                }

                data.clearData();
                
                // Start sending commands
                commands.startTimers();
            }
            else
            {
                parse.messageLine("*** Display Stopped ***");
                commands.endTimers();
            }            
        }
        else
        {
            parse.messageLine("*** No Connection: Display Not Started ***");
        }
    }

    // ----------------------
    // Set the zoom level on the map
    public void doZoom(int z)
    {
        prefs.hexHeight += z;
        if (prefs.hexHeight < 5)
            prefs.hexHeight = 5;
        if (prefs.hexHeight > 300)
            prefs.hexHeight = 300;
        
        tacMap.repaint();
    }

    // -----------------------
    // Change the offset of the map in x
    public void doChangeXOffset(float mod)
    {
        prefs.xOffset += mod;
        tacMap.repaint();
    }

    // -----------------------
    // Change the offset of the map in y
    public void doChangeYOffset(float mod)
    {
        prefs.yOffset += mod;
        tacMap.repaint();
    }

    // -----------------------
    // Recenter the map
    public void doCenterMap()
    {
        prefs.xOffset = 0f;
        prefs.yOffset = 0f;
        tacMap.repaint();
    }

    // -----------------------
    // Show the weapons arcs
    public void doShowArcs()
    {
        prefs.tacShowArcs = !prefs.tacShowArcs;
        
        miShowArcs.setState(prefs.tacShowArcs);

        miMakeArcsWeaponRange.setEnabled(prefs.tacShowArcs);
        miArcRetract.setEnabled(!prefs.makeArcsWeaponRange && prefs.tacShowArcs);
        miArcExtend.setEnabled(!prefs.makeArcsWeaponRange && prefs.tacShowArcs);
        
        tacMap.newPreferences(prefs);
    }

    // -----------------------
    // Make the weapons arcs reflect actual weapon range
    public void doMakeArcsWeaponRange()
    {
        prefs.makeArcsWeaponRange = !prefs.makeArcsWeaponRange;
        miMakeArcsWeaponRange.setState(prefs.makeArcsWeaponRange);
        miArcRetract.setEnabled(!prefs.makeArcsWeaponRange);
        miArcExtend.setEnabled(!prefs.makeArcsWeaponRange);
    }

    // -----------------------
    // Handle changing of arc length
    public void doChangeArc(int d)
    {
        prefs.arcIndicatorRange += d;
        if (prefs.arcIndicatorRange < 1)
            prefs.arcIndicatorRange = 1;
        if (prefs.arcIndicatorRange > 200)
            prefs.arcIndicatorRange = 200;
        tacMap.newPreferences(prefs);
    }

    // -----------------------
    // Show the hex numbers?
    public void doShowHexNumbers()
    {
        prefs.tacShowHexNumbers = !prefs.tacShowHexNumbers;
        miShowHexNumbers.setState(prefs.tacShowHexNumbers);
        tacMap.newPreferences(prefs);
    }

    // -----------------------
    // Show the unit names on the tactical map?
    public void doShowUnitNames()
    {
        prefs.tacShowUnitNames = !prefs.tacShowUnitNames;
        miShowUnitNames.setState(prefs.tacShowUnitNames);
        tacMap.newPreferences(prefs);
    }

    // -----------------------
    // Darken elevations on the map?
    public void doDarkenElevations()
    {
        prefs.tacDarkenElev = !prefs.tacDarkenElev;
        miDarkenElevations.setState(prefs.tacDarkenElev);
        tacMap.newPreferences(prefs);
    }

    // -----------------------
    // Show cliffs on the map?
    public void doShowCliffs()
    {
        prefs.tacShowCliffs = !prefs.tacShowCliffs;
        miShowCliffs.setState(prefs.tacShowCliffs);
        tacMap.newPreferences(prefs);
    }

    // -----------------------
    // Show indicators on the map?
    public void doShowIndicators()
    {
        prefs.tacShowIndicators = !prefs.tacShowIndicators;
        miShowIndicators.setState(prefs.tacShowIndicators);
        tacMap.newPreferences(prefs);
    }

    // -----------------------
    // These two are for future expansion of setting the colors in the main window
    public void doGetBackgroundColor()
    {
        prefs.backgroundColor = JColorChooser.showDialog(this, "Choose a background color", prefs.backgroundColor);        
    }

    public void doGetForegroundColor()
    {
        prefs.foregroundColor = JColorChooser.showDialog(this, "Choose a foreground color", prefs.foregroundColor);
    }

    // -----------------------
    // Did someone choose a connection menu item?
    public boolean matchesConnectionMenu(String action)
    {
        boolean match = false;

        for (int i = 0; i < numHosts; i++)
        {
            if (action.equals(hosts[i] + " " + ports[i]))
                match = true;
        }

        return match;
    }

    // -----------------------
    // Start a new connection
    public void doNewConnection(String action)
    {
        if (connected)			// Clear our current connection first
            stopConnection();
        
        StringTokenizer st = new StringTokenizer(action);
        startConnection(st.nextToken(), Integer.parseInt(st.nextToken().trim()));
    }

    // -----------------------
    // Disconnect from a connection
    public void doDisconnect()
    {
        stopConnection();
    }

    // -----------------------
    // Called when main font size changes
    public void mainFontChanged()
    {
        
        mFont = new Font("Monospaced", Font.PLAIN, prefs.mainFontSize);

        if (bsd != null)
            bsd.newFontSize(prefs.mainFontSize);
        if (textField != null)
            textField.setFont(mFont);
    }
    
    // -----------------------
    // Read our prefs from disk
    public void readPrefs()
    {
        try
        {
            File	prefsFile = new File("thudprefs.prf");

            if (prefsFile.createNewFile())
            {
                prefs = new MUPrefs();
                prefs.defaultPrefs();
            }
            else
            {
                FileInputStream		fis = new FileInputStream(prefsFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                
                prefs = (MUPrefs) ois.readObject();
                fis.close();
            }
        }
        catch (Exception e)
        {
            System.out.println("Error reading preferences file. You probably have an updated version of Thud with incompatible preferences file. Creating new file with default preferences...");            
            // Maybe the file format changed. Let's just create some new prefs
            prefs = new MUPrefs();
            prefs.defaultPrefs();
        }
        
    }

    // -----------------------
    // Write our prefs to disk
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

    // --------------------------------------------------
    // MAIN
    // --------------------------------------------------
    
    public static void main(String args[]) {
        new Thud();
    }

}
