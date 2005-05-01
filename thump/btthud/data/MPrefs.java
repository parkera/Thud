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

import java.util.prefs.*;

import btthud.ui.*;

public class MPrefs extends Object implements Serializable, Cloneable
{    
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
    }
    
	/**
	 * Read the preferences from the backing store.
	 */
	public void readPrefs() {
		Preferences p = Preferences.userNodeForPackage(MPrefs.class);
		
		try {
			p.sync();
		} catch (Exception e) {
			System.out.println("Error: readPrefs: " + e);
		}
		
		antiAliasText = p.getBoolean("antiAliasText", true);
		
		mapFrameSize = readPrefDimension(p, "mapFrameSize", (int) (800f * (3f / 4f)), (int) (600f * (3f / 4f)));
		
		desktopLoc = readPrefPoint(p, "desktopLoc", 10, 10);
		desktopSize = readPrefDimension(p, "desktopSize", 790, 590);
		toolsLoc = readPrefPoint(p, "toolsLoc", 8, 10);
        terrainToolsLoc = readPrefPoint(p, "terrainToolsLoc", 8, 87);
        elevationToolsLoc = readPrefPoint(p, "elevationToolsLoc", 89, 10);
        brushToolsLoc = readPrefPoint(p, "brushToolsLoc", 354, 10);
        inspectorLoc = readPrefPoint(p, "inspectorLoc", 91, 10);
        mapFrameLoc = readPrefPoint(p, "mapFrameLoc", 89, 87);
		
		showTools = p.getBoolean("showTools", true);
        showTerrain = p.getBoolean("showTerrain", true);
        showElevation = p.getBoolean("showElevation", true);
        showBrush = p.getBoolean("showBrush", true);
        showInspector = p.getBoolean("showInspector", true);
		
		elevationColorMultiplier = p.getFloat("elevationColorMultiplier", 0.08f);
		
		tacShowHexNumbers = p.getBoolean("tacShowHexNumbers", false);
        tacShowTerrainChar = p.getBoolean("tacShowTerrainChar", true);
        tacShowTerrainElev = p.getBoolean("tacShowTerrainElev", true);
        tacDarkenElev = p.getBoolean("tacDarkenElev", true);
        tacShowCliffs = p.getBoolean("tacShowCliffs", false);
		
		cliffDiff = p.getInt("cliffDiff", 2);
		
		terrainColors[MUXHex.PLAIN] = readPrefColor(p, "Color-Plain", Color.white);
        terrainColors[MUXHex.WATER] = readPrefColor(p, "Color-Water", Color.blue);
        terrainColors[MUXHex.LIGHT_FOREST] = readPrefColor(p, "Color-Light_Forest", Color.green);
        terrainColors[MUXHex.HEAVY_FOREST] = readPrefColor(p, "Color-Heavy_Forest", Color.green);
        terrainColors[MUXHex.MOUNTAIN] = readPrefColor(p, "Color-Mountain", Color.yellow);
        terrainColors[MUXHex.ROUGH] = readPrefColor(p, "Color-Rough", Color.yellow);
        terrainColors[MUXHex.BUILDING] = readPrefColor(p, "Color-Building", Color.magenta);
        terrainColors[MUXHex.ROAD] = readPrefColor(p, "Color-Road", Color.lightGray);
        terrainColors[MUXHex.BRIDGE] = readPrefColor(p, "Color-Bridge", Color.lightGray);
        terrainColors[MUXHex.FIRE] = readPrefColor(p, "Color-Fire", Color.red);
		terrainColors[MUXHex.DESERT] = readPrefColor(p, "Color-Desert", Color.pink);
        terrainColors[MUXHex.WALL] = readPrefColor(p, "Color-Wall", Color.orange);
        terrainColors[MUXHex.ICE] = readPrefColor(p, "Color-Ice", Color.white);
        terrainColors[MUXHex.SMOKE] = readPrefColor(p, "Color-Smoke", Color.lightGray);
        terrainColors[MUXHex.SMOKE_OVER_WATER] = readPrefColor(p, "Color-Smoke_Over_Water", Color.lightGray);
        terrainColors[MUXHex.UNKNOWN] = readPrefColor(p, "Color-Unknown", Color.black);
		
		mainFontSize = p.getInt("mainFontSize", 10);
		smallFontSize = p.getInt("smallFontSize", 9);
		hexNumberFontSize = p.getInt("hexNumberFontSize", 9);
		infoFontSize = p.getInt("infoFontSize", 10);
		elevationFontSize = p.getInt("elevationFontSize", 10);
		
		paintType = p.getInt("paintType", ToolManager.TERRAIN_AND_ELEVATION);
	}
	
	protected Dimension readPrefDimension(Preferences pref, String key, int defW, int defH) {
		return new Dimension(pref.getInt(key + "W", defW),
							 pref.getInt(key + "H", defH));
	}
	
	protected Point readPrefPoint(Preferences pref, String key, int defX, int defY) {
		return new Point(pref.getInt(key + "X", defX),
						 pref.getInt(key + "Y", defY));
	}
	
	protected Color readPrefColor(Preferences pref, String key, Color defColor) {
		return new Color(pref.getInt(key + "R", defColor.getRed()),
						 pref.getInt(key + "G", defColor.getGreen()),
						 pref.getInt(key + "B", defColor.getBlue()));
	}
	
	protected void writePrefDimension(Preferences pref, String key, Dimension d) {
		pref.putInt(key + "W", (int)d.getWidth());
		pref.putInt(key + "H", (int)d.getHeight());
	}
	
	protected void writePrefPoint(Preferences pref, String key, Point pt) {
		pref.putInt(key + "X", (int)pt.getX());
		pref.putInt(key + "Y", (int)pt.getY());
	}
	
	protected void writePrefColor(Preferences pref, String key, Color c) {
		pref.putInt(key + "R", c.getRed());
		pref.putInt(key + "G", c.getGreen());
		pref.putInt(key + "B", c.getBlue());
	}
	
	/**
	 * Write the preferences to the backing store.
	 */
	public void writePrefs() {
		Preferences p = Preferences.userNodeForPackage(MPrefs.class);
		
		p.putBoolean("antiAliasText", antiAliasText);
		
		writePrefDimension(p, "mapFrameSize", mapFrameSize);
		
		writePrefPoint(p, "desktopLoc", desktopLoc);
		writePrefDimension(p, "desktopSize", desktopSize);
		writePrefPoint(p, "toolsLoc", toolsLoc);
        writePrefPoint(p, "terrainToolsLoc", terrainToolsLoc);
        writePrefPoint(p, "elevationToolsLoc", elevationToolsLoc);
        writePrefPoint(p, "brushToolsLoc", brushToolsLoc);
        writePrefPoint(p, "inspectorLoc", inspectorLoc);
        writePrefPoint(p, "mapFrameLoc", mapFrameLoc);
		
		p.putBoolean("showTools", showTools);
        p.putBoolean("showTerrain", showTerrain);
        p.putBoolean("showElevation", showElevation);
        p.putBoolean("showBrush", showBrush);
        p.putBoolean("showInspector", showInspector);
		
		p.putFloat("elevationColorMultiplier", elevationColorMultiplier);
		
		p.putBoolean("tacShowHexNumbers", tacShowHexNumbers);
        p.putBoolean("tacShowTerrainChar", tacShowTerrainChar);
        p.putBoolean("tacShowTerrainElev", tacShowTerrainElev);
        p.putBoolean("tacDarkenElev", tacDarkenElev);
        p.putBoolean("tacShowCliffs", tacShowCliffs);
		
		p.putInt("cliffDiff", cliffDiff);
		
		writePrefColor(p, "Color-Plain", terrainColors[MUXHex.PLAIN]);
        writePrefColor(p, "Color-Water", terrainColors[MUXHex.WATER]);
        writePrefColor(p, "Color-Light_Forest", terrainColors[MUXHex.LIGHT_FOREST]);
        writePrefColor(p, "Color-Heavy_Forest", terrainColors[MUXHex.HEAVY_FOREST]);
        writePrefColor(p, "Color-Mountain", terrainColors[MUXHex.MOUNTAIN]);
        writePrefColor(p, "Color-Rough", terrainColors[MUXHex.ROUGH]);
        writePrefColor(p, "Color-Building", terrainColors[MUXHex.BUILDING]);
        writePrefColor(p, "Color-Road", terrainColors[MUXHex.ROAD]);
        writePrefColor(p, "Color-Bridge", terrainColors[MUXHex.BRIDGE]);
        writePrefColor(p, "Color-Fire", terrainColors[MUXHex.FIRE]);
		writePrefColor(p, "Color-Desert", terrainColors[MUXHex.DESERT]);
        writePrefColor(p, "Color-Wall", terrainColors[MUXHex.WALL]);
        writePrefColor(p, "Color-Ice", terrainColors[MUXHex.ICE]);
        writePrefColor(p, "Color-Smoke", terrainColors[MUXHex.SMOKE]);
        writePrefColor(p, "Color-Smoke_Over_Water", terrainColors[MUXHex.SMOKE_OVER_WATER]);
        writePrefColor(p, "Color-Unknown", terrainColors[MUXHex.UNKNOWN]);
		
		p.putInt("mainFontSize", mainFontSize);
		p.putInt("smallFontSize", smallFontSize);
		p.putInt("hexNumberFontSize", hexNumberFontSize);
		p.putInt("infoFontSize", infoFontSize);
		p.putInt("elevationFontSize", elevationFontSize);
		
		p.putInt("paintType", paintType);
		
		try {
			p.flush();
		} catch (Exception e) {
			System.out.println("Error: writePrefs: " + e);
		}
	}
}
