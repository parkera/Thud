//
//  MPrefs.java
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

import btthud.ui.*;

public class MPrefs extends Object implements Serializable, Cloneable
{
    public static final String	PREFS_FILE_NAME = "thump.prf";
    
    public Point				desktopLoc;
    public Dimension			desktopSize, mapFrameSize;

    public Point				toolsLoc, terrainToolsLoc, elevationToolsLoc, brushToolsLoc, mapFrameLoc, inspectorLoc;
    public boolean				showTools, showTerrain, showElevation, showBrush, showInspector;
    
     // For the tactical map display
    public boolean				antiAliasText;
    public boolean				tacShowHexNumbers, tacShowTerrainChar, tacShowTerrainElev, tacDarkenElev;
    public float				elevationColorMultiplier;
    public boolean				tacShowCliffs;
    public int					cliffDiff;

    public Properties			theSystem;
    public Color				terrainColors[] = new Color[MUXHex.TOTAL_TERRAIN];

    public int					mainFontSize, smallFontSize, hexNumberFontSize, infoFontSize, elevationFontSize;

    public int					paintType;
    
    // ----------------------------
    
    public MPrefs()
    {
        defaultPrefs();
    }
    
    /**
     * Set default prefs
     */
    public void defaultPrefs()
    {
        antiAliasText = true;

        mapFrameSize = new Dimension((int) (800f * (3f / 4f)), (int) (600f * (3f / 4f)));

        /*
         Nice positions:
         
         Main: java.awt.Point[x=8,y=10]
         Terr: java.awt.Point[x=8,y=87]
         Elev: java.awt.Point[x=89,y=10]
         Brush: java.awt.Point[x=354,y=10]
         
         */
        
        desktopLoc = new Point(10, 10);
        desktopSize = new Dimension(790, 590);
        
        toolsLoc = new Point(8, 10);
        terrainToolsLoc = new Point(8, 87);
        elevationToolsLoc = new Point(89, 10);
        brushToolsLoc = new Point(354, 10);
        inspectorLoc = new Point(91, 10);
        mapFrameLoc = new Point(89, 87);

        showTools = true;
        showTerrain = true;
        showElevation = true;
        showBrush = true;
        showInspector = true;
        
        elevationColorMultiplier = 0.08f;
        
        tacShowHexNumbers = false;
        tacShowTerrainChar = true;
        tacShowTerrainElev = true;
        tacDarkenElev = true;
        tacShowCliffs = false;
        cliffDiff = 2;

        terrainColors[MUXHex.PLAIN] = Color.white;
        terrainColors[MUXHex.WATER] = Color.blue;
        terrainColors[MUXHex.LIGHT_FOREST] = Color.green;
        terrainColors[MUXHex.HEAVY_FOREST] = Color.green;
        terrainColors[MUXHex.MOUNTAIN] = Color.yellow;
        terrainColors[MUXHex.ROUGH] = Color.yellow;
        terrainColors[MUXHex.BUILDING] = Color.magenta;
        terrainColors[MUXHex.ROAD] = Color.lightGray;
        terrainColors[MUXHex.BRIDGE] = Color.lightGray;
        terrainColors[MUXHex.FIRE] = Color.red;
		terrainColors[MUXHex.DESERT] = Color.pink;
        terrainColors[MUXHex.WALL] = Color.orange;
        terrainColors[MUXHex.ICE] = Color.white;
        terrainColors[MUXHex.SMOKE] = Color.lightGray;
        terrainColors[MUXHex.SMOKE_OVER_WATER] = Color.lightGray;
        terrainColors[MUXHex.UNKNOWN] = Color.black;
        
        mainFontSize = 10;
        smallFontSize = 9;
        hexNumberFontSize = 9;
        infoFontSize = 10;
        elevationFontSize = 10;

        paintType = ToolManager.TERRAIN_AND_ELEVATION;
    }
}
