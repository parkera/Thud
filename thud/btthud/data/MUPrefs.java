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

    public Point				mainLoc, tacLoc, contactsLoc, armorLoc;
    public int					mainSizeX, mainSizeY, tacSizeX, tacSizeY, contactsSizeX, contactsSizeY;
    public int					armorSizeX, armorSizeY;
    
    public int					commandHistory;
    public boolean				echoCommands;

    // These are delays for sending commands, in seconds
    public double				contactsDelay, findcenterDelay, tacticalDelay, tacticalRedrawDelay;
    public double				armorRedrawDelay;
    // How high do we want our auto-tactical to be?
    public int					hudinfoTacHeight;
    
    // For the tactical map display
    public boolean				tacShowHexNumbers, tacShowTerrainChar, tacShowTerrainElev, tacShowUnitNames, tacDarkenElev;
    public boolean				makeArcsWeaponRange;
    public boolean				highlightMyHex;
    public int					hexHeight;
    public float				elevationColorMultiplier;
    
    public int					arcIndicatorRange;
    public boolean				tacShowArcs;
    public boolean				tacShowCliffs;
    public boolean 				tacShowIndicators;
    public int					cliffDiff;
    public float				speedIndicatorLength;
    
    public Color				backgroundColor;
    public Color				foregroundColor;

    public int					yOffset, xOffset;

    public Properties			theSystem;

    public Color				terrainColors[] = new Color[MUHex.TOTAL_TERRAIN];
    //public Color				cBuilding, cRoad, cPlains, cWater, cLightForest, cHeavyForest, cWall, cMountain, cRough, cFire, cSmoke, cIce, cSmokeOnWater, cUnknown;

    public int					mainFontSize, smallFontSize, hexNumberFontSize, infoFontSize, elevationFontSize, contactFontSize;

    public ArrayList			hosts = new ArrayList();
    
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

        mainSizeX = 560;
        mainSizeY = 580;

        tacSizeX = 560;
        tacSizeY = 560;

        contactsSizeX = 560;
        contactsSizeY = 250;

        armorSizeX = 200;
        armorSizeY = 250;

        mainLoc = new Point(10, 10);
        tacLoc = new Point(20 + mainSizeX, 10);
        contactsLoc = new Point(10, 30 + mainSizeY);
        armorLoc = new Point(20 + mainSizeX, 30 + tacSizeY);
        
        commandHistory = 20;
        echoCommands = true;

        contactsDelay = 1.0;
        findcenterDelay = 1.0;
        tacticalRedrawDelay = 1.0;
        tacticalDelay = 15.0;
        armorRedrawDelay = 1.0;
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
        highlightMyHex = false;
        cliffDiff = 2;
        speedIndicatorLength = 3.0f;

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
        
        mainFontSize = 10;
        contactFontSize = 10;
        smallFontSize = 9;
        hexNumberFontSize = 9;
        infoFontSize = 9;
        elevationFontSize = 10;

        MUHost bt3030 = new MUHost("btech.dhs.org", 3030);
        MUHost bt3049 = new MUHost("btech.no-ip.com", 3049);

        hosts.add(bt3030);
        hosts.add(bt3049);        
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
