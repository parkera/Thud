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
    public int					cliffDiff;
    
    public Color				backgroundColor;
    public Color				foregroundColor;

    public float				yOffset, xOffset;

    public Properties			theSystem;

    public Color				cBuilding, cRoad, cPlains, cWater, cLightForest, cHeavyForest, cWall, cMountain, cRough, cFire, cSmoke, cIce, cSmokeOnWater, cUnknown;

    public int					mainFontSize, smallFontSize, hexNumberFontSize, infoFontSize, elevationFontSize, contactFontSize;

    public static final int		TOTAL_HOSTS = 10;

    public String[]				hosts;
    public int[]				hostPorts;
    
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

        contactsDelay = 2.0;
        findcenterDelay = 2.0;
        tacticalRedrawDelay = 2.0;
        tacticalDelay = 30.0;
        armorRedrawDelay = 2.0;
        hudinfoTacHeight = 40;
        elevationColorMultiplier = 0.08f;

        hexHeight = 40;
        tacShowHexNumbers = false;
        tacShowTerrainChar = true;
        tacShowTerrainElev = true;
        tacShowUnitNames = true;
        tacDarkenElev = true;
        tacShowCliffs = false;
        highlightMyHex = false;
        cliffDiff = 2;

        makeArcsWeaponRange = false;
        tacShowArcs = false;
        arcIndicatorRange = 2;

        yOffset = 0f;
        xOffset = 0f;

        backgroundColor = Color.black;
        foregroundColor = Color.white;

        cPlains = Color.white;
        cWater = Color.blue;
        cLightForest = Color.green;
        cHeavyForest = Color.green;
        cMountain = Color.yellow;
        cRough = Color.yellow;
        cBuilding = Color.magenta;
        cRoad = Color.lightGray;
        cFire = Color.red;
        cWall = Color.orange;
        cSmoke = Color.darkGray;
        cIce = Color.white;
        cSmokeOnWater = Color.lightGray;
        cUnknown = Color.black;

        mainFontSize = 10;
        contactFontSize = 10;
        smallFontSize = 9;
        hexNumberFontSize = 9;
        infoFontSize = 9;
        elevationFontSize = 10;

        hosts = new String[TOTAL_HOSTS];
        hostPorts = new int[TOTAL_HOSTS];

        hosts[0] = "btech.dhs.org";
        hostPorts[0] = 3030;
        
    }
}
