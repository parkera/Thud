//
//  MUPrefs.java
//  Thud
//
//  Created by Anthony Parker on Sat Dec 22 2001.
//  Copyright (c) 2001 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//
//
package btthud.data;

import java.io.*;
import java.lang.*;
import java.awt.geom.*;
import java.awt.*;

import java.util.*;

public class MUPrefs extends Object implements Serializable, Cloneable
{
    public boolean				showTacMap, showContacts;

    public Point				mainLoc, tacLoc, contactsLoc, armorLoc, statusLoc;
    public int					mainSizeX, mainSizeY, tacSizeX, tacSizeY, contactsSizeX, contactsSizeY, statusSizeX, statusSizeY;
    public int					armorSizeX, armorSizeY;
    
    public int					commandHistory;
    public boolean				echoCommands;

    public boolean				antiAliasText;

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
    public boolean				overwriteWithUnknown;			// Replace established terrain with '?'s if true
    
    public int					arcIndicatorRange;
    public boolean				tacShowArcs;
    public boolean				tacShowCliffs;
    public boolean 				tacShowIndicators;
    public float				speedIndicatorLength;
    
    public Color				backgroundColor;
    public Color				foregroundColor;

    public int					yOffset, xOffset;

    public Properties			theSystem;

    public Color				terrainColors[] = new Color[MUHex.TOTAL_TERRAIN];

    public int					mainFontSize, smallFontSize, hexNumberFontSize, infoFontSize, elevationFontSize, contactFontSize;
    public int					maxScrollbackSize;
    
    public ArrayList			hosts = new ArrayList();

    public static final int			FAST_UPDATE = 1;
    public static final int			NORMAL_UPDATE = 2;
    public static final int			SLOW_UPDATE = 3;
    
    public MUPrefs()
    {
        
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
        tacShowCliffs = false;
        tacShowIndicators = false;      // Floating Heat/Armor/Internal bar on tactical map
        tacShowArmorDiagram = false;
        highlightMyHex = false;
        speedIndicatorLength = 3.0f;
        overwriteWithUnknown = true;	// Ie, don't save terrain we already know about in the underground
        
        makeArcsWeaponRange = false;
        tacShowArcs = false;
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
        
        mainFontSize = 12;
        contactFontSize = 12;
        smallFontSize = 9;
        hexNumberFontSize = 9;
        infoFontSize = 10;
        elevationFontSize = 10;

        maxScrollbackSize = 2000;
        
        MUHost bt3030 = new MUHost("btech.dhs.org", 3030);
        MUHost frontiers = new MUHost("btmux.com", 5555);

        hosts.add(bt3030);
        hosts.add(frontiers);
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
}
