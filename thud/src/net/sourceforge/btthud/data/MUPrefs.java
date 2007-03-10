//
//  MUPrefs.java
//  Thud
//
//  Created by Anthony Parker on Sat Dec 22 2001.
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.data;

import java.awt.Point;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

public class MUPrefs implements Cloneable {

    public boolean				showTacMap, showContacts;

    public Point				mainLoc, tacLoc, contactsLoc, armorLoc, statusLoc;
    public String               mainFont;
    public int					mainSizeX, mainSizeY, tacSizeX, tacSizeY, contactsSizeX, contactsSizeY, statusSizeX, statusSizeY;
    public int					armorSizeX, armorSizeY;
    
    public int					commandHistory;
    public boolean				echoCommands;

    public boolean				antiAliasText;

    // How long should contacts stick around?
    public int					contactsAge;
    
    // These are delays for sending commands, in seconds
    public double				fastCommandUpdate, mediumCommandUpdate, slowCommandUpdate, slugCommandUpdate;

    // How high do we want our auto-tactical to be?
    public int					hudinfoTacHeight;
    
    // For the tactical map display
    public boolean				tacShowHexNumbers, tacShowTerrainChar, tacShowTerrainElev, tacShowUnitNames, tacDarkenElev, tacShowArmorDiagram;
    public boolean				makeArcsWeaponRange;
    public boolean				highlightMyHex;
    public int					hexHeight;
    public float				elevationColorMultiplier;
    public boolean				tacShowLOSInfo;
    
    public int					arcIndicatorRange;
    public boolean				tacShowArcs;
    public boolean				tacShowCliffs;
    public boolean 				tacShowIndicators;
    public float				speedIndicatorLength;
    
    public Color				backgroundColor;
    public Color				foregroundColor;

    public int					yOffset, xOffset;

    public Color				terrainColors[] = new Color[MUHex.TOTAL_TERRAIN];

    public int					mainFontSize, tacStatusFontSize, hexNumberFontSize, infoFontSize, elevationFontSize, contactFontSize, statusFontSize;
    public int					maxScrollbackSize;
    
    public ArrayList<MUHost>	hosts = new ArrayList<MUHost>();

	public boolean childrenAreFrames;
    public boolean mainAlwaysOnTop, contactsAlwaysOnTop, statusAlwaysOnTop, tacticalAlwaysOnTop;

	public MUPrefs () {
	}

    /**
     * Set default prefs
     */
    public void defaultPrefs()
    {
        // Set some initial values
        showTacMap = true;
        showContacts = true;

        antiAliasText = false;
        contactsAge = 30;
        
        mainSizeX = 580;
        mainSizeY = 580;

        tacSizeX = 560;
        tacSizeY = 560;

        contactsSizeX = 560;
        contactsSizeY = 250;
        
        statusSizeX = 530;
        statusSizeY = 250;
        
        armorSizeX = 200;
        armorSizeY = 250;

        mainLoc = new Point(10, 10);
        tacLoc = new Point(20 + mainSizeX, 10);
        contactsLoc = new Point(10, 30 + mainSizeY);
        statusLoc = new Point(10,60 + mainSizeY);
        armorLoc = new Point(20 + mainSizeX, 30 + tacSizeY);
        
        commandHistory = 20;
        echoCommands = true;

        fastCommandUpdate = 1.0;
        mediumCommandUpdate = 2.0;
        slowCommandUpdate = 5.0;
        slugCommandUpdate = 20.0;
        
        hudinfoTacHeight = 40;
        elevationColorMultiplier = 0.08f;

        hexHeight = 40;
        tacShowHexNumbers = false;
        tacShowTerrainChar = true;
        tacShowTerrainElev = true;
        tacShowUnitNames = true;
        tacDarkenElev = true;
        tacShowCliffs = true;
        tacShowIndicators = true;      // Floating Heat/Armor/Internal bar on tactical map
        tacShowArmorDiagram = true;
        highlightMyHex = true;
        speedIndicatorLength = 3.0f;
        tacShowLOSInfo = false;	
        
        makeArcsWeaponRange = false;
        tacShowArcs = true;
        arcIndicatorRange = 2;

        yOffset = 0;
        xOffset = 0;

        backgroundColor = Color.black;
        foregroundColor = Color.white;

        terrainColors[MUHex.PLAIN] = Color.white;
        terrainColors[MUHex.WATER] = Color.blue;
        terrainColors[MUHex.LIGHT_FOREST] = Color.green;
        terrainColors[MUHex.HEAVY_FOREST] = Color.green;
        terrainColors[MUHex.MOUNTAIN] = Color.yellow;
        terrainColors[MUHex.ROUGH] = Color.yellow;
        terrainColors[MUHex.BUILDING] = Color.magenta;
        terrainColors[MUHex.ROAD] = Color.lightGray;
        terrainColors[MUHex.BRIDGE] = Color.lightGray;
        terrainColors[MUHex.FIRE] = Color.red;
        terrainColors[MUHex.WALL] = Color.orange;
        terrainColors[MUHex.ICE] = Color.white;
        terrainColors[MUHex.SMOKE] = Color.lightGray;
        terrainColors[MUHex.SMOKE_OVER_WATER] = Color.lightGray;
        terrainColors[MUHex.UNKNOWN] = Color.black;
        
        // Need to get the "real" font name for the Monospaced font
        mainFont = new Font("Monospaced",Font.PLAIN,10).getFontName();
        mainFontSize = 12;
        contactFontSize = 12;
        tacStatusFontSize = 9;
        hexNumberFontSize = 9;
        infoFontSize = 10;
        statusFontSize = 12;
        elevationFontSize = 10;

        maxScrollbackSize = 2000;

	childrenAreFrames = false;
        mainAlwaysOnTop = false;
        contactsAlwaysOnTop = false;
        statusAlwaysOnTop = false;
        tacticalAlwaysOnTop = false;
        
        MUHost bt3030 = new MUHost("btech.dhs.org", 3030);
        MUHost frontiers = new MUHost("frontiermux.com", 5555);
        MUHost bt3065 = new MUHost("3065.btmux.com", 3065);
        

        hosts.add(bt3030);
        hosts.add(frontiers);
        hosts.add(bt3065);
    }

    public void addHost(String newHost, int newPort)
    {
        hosts.add(new MUHost(newHost, newPort));
    }

    public void addHost(MUHost newHost) {
        hosts.add(newHost);
    }

    public void removeHost(String oldHost, int oldPort)
    {
        hosts.remove(hosts.indexOf(new MUHost(oldHost, oldPort)));
    }

    public void removeHost(MUHost oldHost) {
        hosts.remove(hosts.indexOf(oldHost));
    }


	/**
	 * Deep clone the preferences object.
	 */
	public Object clone () throws CloneNotSupportedException {
		// Get shallow copy.
		final MUPrefs newObject = (MUPrefs)super.clone();

		// Deep copy specific fields.  This is much faster than using
		// Serialization to clone(), although also less convenient.
		//
		// Normal rules for clone() apply: Basically, if it's not a
		// primitive, and it's not shareable (generally immutable) like
		// a String, then it needs to be copied.  Also, remember that
		// clones of Collections from java.util return shallow copies.
		//
		// TODO: This information could be useful to PreferencesStore.
		// Perhaps we should store the information in a common place.
		//
		// None of these should be null, by the way.

		// Points.
		newObject.mainLoc = (Point)mainLoc.clone();
		newObject.tacLoc = (Point)tacLoc.clone();
		newObject.contactsLoc = (Point)contactsLoc.clone();
		newObject.armorLoc = (Point)armorLoc.clone();
		newObject.statusLoc = (Point)statusLoc.clone();

		// Colors.
		// XXX: Colors are immutable, more or less.  Unless you invent
		// some sort of Color subclass that's mutable.  Then you're
		// being a bad monkey.
		//
		// We still need to clone arrays, though.
		newObject.terrainColors = (Color[])terrainColors.clone();

		// ArrayList<MUHost>.
		// FIXME: A shallow copy is enough for now, but a future
		// connection manager will mean MUHosts are no longer
		// immutable, and so we'll need to do a deep copy and implement
		// clone() on MUHost.
		newObject.hosts = (ArrayList<MUHost>)hosts.clone();

		return newObject;
	}
}
