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
    protected int		acceleratorModifier = java.awt.Event.CTRL_MASK;
    
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

    protected JMenuItem	miMoveRight, miMoveLeft, miMoveDown, miMoveUp, miResetOffsets;

    static final JMenu hudMenu = new JMenu("HUD");
    protected JMenuItem miStartStop;
    protected JMenuItem miPreferences;
    protected JMenuItem[] miConnections = null;
    
    // ------------------
    
    JTextField 				textField;
    JTextPane				textPane;
    BulkStyledDocument		bsd;
    
    MUConnection 	conn = null;
    MUParse			parse = null;
    MUData			data = null;
    MUContactList	conList = null;
    MUTacticalMap	tacMap = null;
    MUPrefs			prefs = null;
    MUCommands		commands = null;

    LinkedList		commandHistory = new LinkedList();
    int				historyLoc = 1;							// how far we are from end of history list
    
    // -----------------------
    
    public void addFileMenuItems() {
        miNew = new JMenuItem ("New");
        miNew.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, acceleratorModifier));
        fileMenu.add(miNew).setEnabled(false);
        miNew.addActionListener(this);

        miOpen = new JMenuItem ("Open...");
        miOpen.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, acceleratorModifier));
        fileMenu.add(miOpen).setEnabled(false);
        miOpen.addActionListener(this);
		
        miClose = new JMenuItem ("Close");
        miClose.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, acceleratorModifier));
        fileMenu.add(miClose).setEnabled(true);
        miClose.addActionListener(this);
		
        miSave = new JMenuItem ("Save");
        miSave.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, acceleratorModifier));
        fileMenu.add(miSave).setEnabled(false);
        miSave.addActionListener(this);
		
        miSaveAs = new JMenuItem ("Save As...");
        fileMenu.add(miSaveAs).setEnabled(false);
        miSaveAs.addActionListener(this);

        // ----
        fileMenu.addSeparator();

        miQuit = new JMenuItem("Quit");
        miQuit.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, acceleratorModifier));
        fileMenu.add(miQuit).setEnabled(true);
        miQuit.addActionListener(this);
        
        mainMenuBar.add(fileMenu);
    }

    public void addHUDMenuItems()
    {
        miPreferences = new JMenuItem("Preferences...");
        hudMenu.add(miPreferences).setEnabled(true);
        miPreferences.addActionListener(this);

        hudMenu.addSeparator();
        
        miStartStop = new JMenuItem("Start/Stop");
        miStartStop.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, acceleratorModifier));
        hudMenu.add(miStartStop).setEnabled(true);
        miStartStop.addActionListener(this);

        // ----
        hudMenu.addSeparator();
        
        for (int i = 0; i < numHosts; i++)
        {
            miConnections[i] = new JMenuItem(hosts[i] + " " + ports[i]);
            acceleratorForConnectionItem(miConnections[i], i);
            hudMenu.add(miConnections[i]).setEnabled(true);
            miConnections[i].addActionListener(this);
        }
        
        mainMenuBar.add(hudMenu);
    }
    
    protected void acceleratorForConnectionItem(JMenuItem mi, int i)
    {
        switch (i)
        {
            case 0:
                mi.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, acceleratorModifier));
                break;
            case 1:
                mi.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, acceleratorModifier));
                break;
            case 2:
                mi.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, acceleratorModifier));
                break;
            case 3:
                mi.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_4, acceleratorModifier));
                break;
            case 4:
                mi.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_5, acceleratorModifier));
                break;
            case 5:
                mi.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_6, acceleratorModifier));
                break;
            case 6:
                mi.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_7, acceleratorModifier));
                break;
            case 7:
                mi.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_8, acceleratorModifier));
                break;
            case 8:
                mi.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_9, acceleratorModifier));
                break;
            case 9:
                mi.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_0, acceleratorModifier));
                break;
        }
    }

    public void addMapMenuItems()
    {
        miZoomIn = new JMenuItem("Zoom In");
        miZoomIn.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_CLOSE_BRACKET, acceleratorModifier));
        mapMenu.add(miZoomIn).setEnabled(true);
        miZoomIn.addActionListener(this);
        
        miZoomOut = new JMenuItem("Zoom Out");
        miZoomOut.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_OPEN_BRACKET, acceleratorModifier));
        mapMenu.add(miZoomOut).setEnabled(true);
        miZoomOut.addActionListener(this);

        // ----
        mapMenu.addSeparator();

        miShowArcs = new JCheckBoxMenuItem("Show Weapons Arcs", prefs.tacShowUnitNames);
        miShowArcs.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, acceleratorModifier));
        mapMenu.add(miShowArcs).setEnabled(true);
        miShowArcs.addActionListener(this);
        miShowArcs.setState(prefs.tacShowArcs);
        
        miMakeArcsWeaponRange = new JCheckBoxMenuItem("Make Arcs Weapon Ranges");
        miMakeArcsWeaponRange.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, acceleratorModifier));
        mapMenu.add(miMakeArcsWeaponRange).setEnabled(prefs.tacShowArcs);
        miMakeArcsWeaponRange.addActionListener(this);
        miMakeArcsWeaponRange.setState(prefs.makeArcsWeaponRange);
        
        miArcRetract = new JMenuItem("Retract Arc Range");
        miArcRetract.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SEMICOLON, acceleratorModifier));
        mapMenu.add(miArcRetract).setEnabled(!prefs.makeArcsWeaponRange && prefs.tacShowArcs);
        miArcRetract.addActionListener(this);

        miArcExtend = new JMenuItem("Extend Arc Range");
        miArcExtend.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_QUOTE, acceleratorModifier));
        mapMenu.add(miArcExtend).setEnabled(!prefs.makeArcsWeaponRange && prefs.tacShowArcs);
        miArcExtend.addActionListener(this);

        // ----
        mapMenu.addSeparator();
        
        miShowHexNumbers = new JCheckBoxMenuItem("Show Hex Numbers", prefs.tacShowHexNumbers);
        miShowHexNumbers.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, acceleratorModifier));
        mapMenu.add(miShowHexNumbers).setEnabled(true);
        miShowHexNumbers.addActionListener(this);
        miShowHexNumbers.setState(prefs.tacShowHexNumbers);
        
        miShowUnitNames = new JCheckBoxMenuItem("Show Unit Names", prefs.tacShowUnitNames);
        miShowUnitNames.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, acceleratorModifier));
        mapMenu.add(miShowUnitNames).setEnabled(true);
        miShowUnitNames.addActionListener(this);
        miShowUnitNames.setState(prefs.tacShowUnitNames);
        
        miDarkenElevations = new JCheckBoxMenuItem("Darken Elevations", prefs.tacDarkenElev);
        miDarkenElevations.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, acceleratorModifier));
        mapMenu.add(miDarkenElevations).setEnabled(true);
        miDarkenElevations.addActionListener(this);
        miDarkenElevations.setState(prefs.tacDarkenElev);

        // ---
        mapMenu.addSeparator();

        miMoveLeft = new JMenuItem("Move Map Left");
        miMoveLeft.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.Event.SHIFT_MASK + acceleratorModifier));
        mapMenu.add(miMoveLeft);
        miMoveLeft.addActionListener(this);

        miMoveRight = new JMenuItem("Move Map Right");
        miMoveRight.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.Event.SHIFT_MASK + acceleratorModifier));
        mapMenu.add(miMoveRight);
        miMoveRight.addActionListener(this);

        miMoveUp = new JMenuItem("Move Map Up");
        miMoveUp.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.Event.SHIFT_MASK + acceleratorModifier));
        mapMenu.add(miMoveUp);
        miMoveUp.addActionListener(this);

        miMoveDown = new JMenuItem("Move Map Down");
        miMoveDown.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.SHIFT_MASK + acceleratorModifier));
        mapMenu.add(miMoveDown);
        miMoveDown.addActionListener(this);

        miResetOffsets = new JMenuItem("Reset Map Offsets");
        miResetOffsets.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.Event.SHIFT_MASK + acceleratorModifier));
        mapMenu.add(miResetOffsets);
        miResetOffsets.addActionListener(this);

        // ---
        mapMenu.addSeparator();

        miShowCliffs = new JCheckBoxMenuItem("Show Cliffs", prefs.tacShowCliffs);
        miShowCliffs.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, acceleratorModifier));
        mapMenu.add(miShowCliffs).setEnabled(true);
        miShowCliffs.addActionListener(this);
        miShowCliffs.setState(prefs.tacShowCliffs);
        
        mainMenuBar.add(mapMenu);
    }
	
	// -----------------------
    
    public void addEditMenuItems() {
        miUndo = new JMenuItem("Undo");
        miUndo.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, acceleratorModifier));
        editMenu.add(miUndo).setEnabled(true);
        miUndo.addActionListener(this);
        editMenu.addSeparator();

        miCut = new JMenuItem("Cut");
        miCut.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, acceleratorModifier));
        editMenu.add(miCut).setEnabled(true);
        miCut.addActionListener(this);

        miCopy = new JMenuItem("Copy");
        miCopy.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, acceleratorModifier));
        editMenu.add(miCopy).setEnabled(true);
        miCopy.addActionListener(this);

        miPaste = new JMenuItem("Paste");
        miPaste.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, acceleratorModifier));
        editMenu.add(miPaste).setEnabled(true);
        miPaste.addActionListener(this);

        miClear = new JMenuItem("Clear");
        editMenu.add(miClear).setEnabled(true);
        miClear.addActionListener(this);

        // ----
        editMenu.addSeparator();

        miSelectAll = new JMenuItem("Select All");
        miSelectAll.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, acceleratorModifier));
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
    
    public void addMenus()
    {
        addFileMenuItems();
        addEditMenuItems();
        addMapMenuItems();
        addHUDMenuItems();
        setJMenuBar(mainMenuBar);
    }

    // -----------------------

    
    public Thud()
    {
        super("Thud");
        
        readPrefs();
        setupSystemSpecifics();
        mainFontChanged();				// setup a new font
        
        this.getContentPane().setLayout(null);

        //this.addWindowListener(new WindowListener
        // Only works for Java 1.4
        //setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        
        initConnectionMenus();
        
        addMenus();

        aboutBox = new AboutBox();
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
        
        setSize(prefs.mainSizeX, prefs.mainSizeY);
        setLocation(prefs.mainLoc);
        
        // -----------------
        Toolkit.getDefaultToolkit();
        //MRJApplicationUtils.registerAboutHandler(this);
        //MRJApplicationUtils.registerQuitHandler(this);

        // Initilization strings
        bsd.insertPlainString(" *** Thud, (c) 2001-2002 Anthony Parker <asp@mac.com> ***");
        bsd.insertPlainString(" *** bt-thud.sourceforge.net                          ***");
        bsd.insertPlainString(" *** Version: 1.0b6 (Beta)                            ***");
        bsd.insertPlainString(" *** Contact Tony @ 3030MUX with questions/comments   ***\n");
        setVisible(true);
    }
    
    // -----------------
    
    public void StartConnection(String host, int port)
    {

        if (conn != null)
        {
            // We must already have a connection. Let's clean up that one, then go to this new one
            StopConnection();
        }

        // Setup some of the helper classes
        data = new MUData();
        parse = new MUParse(textPane, data, bsd, prefs);

        try
        {
            conn = new MUConnection(host, port, parse);
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e);
        }

        // Setup the rest of the helper classes
        conList = new MUContactList(conn, data, prefs);
        tacMap = new MUTacticalMap(conn, data, prefs);
        commands = new MUCommands(conn, data, prefs);

        parse.setCommands(commands);

        setVisible(true);

        // Let the text field get the keyboard focus
        textField.grabFocus();
    }

    // ---------------------

    public void StopConnection()
    {
        if (conn != null)
        {
            /*
            conn.pleaseStop();
            // Perform other cleanup here
            */
            
            conn = null;
            commands.endTimers();
        }
    }
    // -----------------------

    public void paint(Graphics g) {
		super.paint(g);
    }

    // -----------------------
    
    public void handleAbout() {
        aboutBox.setResizable(false);
        aboutBox.setVisible(true);
        aboutBox.show();
    }

    // -----------------------
    
    public void handleQuit() {	
        try
        {
            // Close our connection
            if (conn != null)
                conn.pleaseStop();

            // Write out our preferences file
            writePrefs();
        }
        catch (Exception e)
        {
            System.out.println("Error: handleQuit: " + e);
        }
        System.exit(0);
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
        else if (newEvent.getActionCommand().equals(miMoveLeft.getActionCommand())) doChangeXOffset(-1f);
        else if (newEvent.getActionCommand().equals(miMoveRight.getActionCommand())) doChangeXOffset(1f);
        else if (newEvent.getActionCommand().equals(miMoveUp.getActionCommand())) doChangeYOffset(-1f);
        else if (newEvent.getActionCommand().equals(miMoveDown.getActionCommand())) doChangeYOffset(1f);
        else if (newEvent.getActionCommand().equals(miResetOffsets.getActionCommand())) doResetOffsets();
        else if (newEvent.getActionCommand().equals(miPreferences.getActionCommand())) doPreferences();
        else if (matchesConnectionMenu(newEvent.getActionCommand())) doNewConnection(newEvent.getActionCommand());
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
                
                    // We can either select all the text, or just erase it. I think we'll erase it
                    //textField.selectAll();
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

    // -----------------------

    public void doPreferences()
    {
        PrefsDialog		prefsDialog = new PrefsDialog(this, true);		// the middle boolean is to see if the dialog is modal
        prefsDialog.setVisible(true);

        // Send messages around in case something changed
        if (tacMap != null)
            tacMap.newPreferences(prefs);
        
        mainFontChanged();
    }
    // -----------------------
    
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

    public void doPreviousCommand()
    {
        if (commandHistory.size() - historyLoc > 0)		// make sure we're not going past what we have
        {
            textField.setText((String) commandHistory.get(commandHistory.size() - historyLoc));
            //textField.selectAll();
            textField.setCaretPosition(textField.getDocument().getLength());
            historyLoc++;
        }
        else
        {
            historyLoc = 1;
        }
    }

    public void doEraseCommand()
    {
        textField.setText("");
    }

    public void doStartStop()
    {
        if (conn != null)
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

    public void doZoom(int z)
    {
        prefs.hexHeight += z;
        if (prefs.hexHeight < 5)
            prefs.hexHeight = 5;
        if (prefs.hexHeight > 300)
            prefs.hexHeight = 300;
        //parse.messageLine("*** Zoom set to: " + prefs.hexHeight + " ***");
        tacMap.repaint();
    }

    public void doChangeXOffset(float mod)
    {
        prefs.xOffset += mod;
        tacMap.repaint();
    }

    public void doChangeYOffset(float mod)
    {
        prefs.yOffset += mod;
        tacMap.repaint();
    }

    public void doResetOffsets()
    {
        prefs.xOffset = 0f;
        prefs.yOffset = 0f;
        tacMap.repaint();
    }

    public void doShowArcs()
    {
        prefs.tacShowArcs = !prefs.tacShowArcs;
        
        miShowArcs.setState(prefs.tacShowArcs);

        miMakeArcsWeaponRange.setEnabled(prefs.tacShowArcs);
        miArcRetract.setEnabled(!prefs.makeArcsWeaponRange && prefs.tacShowArcs);
        miArcExtend.setEnabled(!prefs.makeArcsWeaponRange && prefs.tacShowArcs);
        
        tacMap.repaint();
    }
    
    public void doMakeArcsWeaponRange()
    {
        prefs.makeArcsWeaponRange = !prefs.makeArcsWeaponRange;
        miMakeArcsWeaponRange.setState(prefs.makeArcsWeaponRange);
        miArcRetract.setEnabled(!prefs.makeArcsWeaponRange);
        miArcExtend.setEnabled(!prefs.makeArcsWeaponRange);
    }
    
    public void doChangeArc(int d)
    {
        prefs.arcIndicatorRange += d;
        if (prefs.arcIndicatorRange < 1)
            prefs.arcIndicatorRange = 1;
        if (prefs.arcIndicatorRange > 200)
            prefs.arcIndicatorRange = 200;
        tacMap.repaint();
    }

    public void doShowHexNumbers()
    {
        prefs.tacShowHexNumbers = !prefs.tacShowHexNumbers;
        miShowHexNumbers.setState(prefs.tacShowHexNumbers);
        tacMap.repaint();
    }

    public void doShowUnitNames()
    {
        prefs.tacShowUnitNames = !prefs.tacShowUnitNames;
        miShowUnitNames.setState(prefs.tacShowUnitNames);
        tacMap.repaint();
    }

    public void doDarkenElevations()
    {
        prefs.tacDarkenElev = !prefs.tacDarkenElev;
        miDarkenElevations.setState(prefs.tacDarkenElev);
        tacMap.repaint();
    }

    public void doShowCliffs()
    {
        prefs.tacShowCliffs = !prefs.tacShowCliffs;
        miShowCliffs.setState(prefs.tacShowCliffs);
        tacMap.repaint();
    }

    public void doGetBackgroundColor()
    {
        prefs.backgroundColor = JColorChooser.showDialog(this, "Choose a background color", prefs.backgroundColor);        
    }

    public void doGetForegroundColor()
    {
        prefs.foregroundColor = JColorChooser.showDialog(this, "Choose a foreground color", prefs.foregroundColor);
    }
    
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

    public void doNewConnection(String action)
    {
        StringTokenizer st = new StringTokenizer(action);
        StartConnection(st.nextToken(), Integer.parseInt(st.nextToken().trim()));
    }
    
    /** This function is called when the font size changes
      *
      */
    public void mainFontChanged()
    {
        
        mFont = new Font("Monospaced", Font.PLAIN, prefs.mainFontSize);
        /*
        if (textField != null)
            textField.setFont(mFont);
         */
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

    // -----------------------
    /** Handle system-specific setup. For example, the shortcut key on Mac OS is the command key, which Java says is META. Otherwise it's CNTRL.
      *
      */
    public void setupSystemSpecifics()
    {
        try
        {
            prefs.theSystem = System.getProperties();

            //System.out.println("Properties: " + prefs.theSystem);

            if (prefs.theSystem.getProperty("os.name").equals("Mac OS X"))
                acceleratorModifier = java.awt.Event.META_MASK;
            
        }
        catch (Exception e)
        {
            System.out.println("Error: setupSystemSpecifics: " + e);
            prefs.theSystem = null;
        }
    }
    
	// -----------------------
    
    public static void main(String args[]) {
        new Thud();
    }

}
