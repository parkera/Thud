//
//  MUPrefs.java
//  Thud
//
//  Created by Anthony Parker on Sat Dec 22 2001.
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.data;

import java.awt.Point;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class MUPrefs implements Cloneable, Serializable
{
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

    public boolean mainAlwaysOnTop, contactsAlwaysOnTop, statusAlwaysOnTop, tacticalAlwaysOnTop;
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
        
        mainAlwaysOnTop = false;
        contactsAlwaysOnTop = false;
        statusAlwaysOnTop = false;
        tacticalAlwaysOnTop = false;
        
        MUHost bt3030 = new MUHost("btech.dhs.org", 3030);
        MUHost frontiers = new MUHost("btmux.com", 5555);
        MUHost bt3065 = new MUHost("btmux.com", 3065);
        

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
     * Deep clone the preferences object. (This used to be ObjectCloner.java.)
     *
     * From: http://www.javaworld.com/javaworld/javatips/jw-javatip76-p2.html
     * Provides a class useful for making deep copies of objects that are
     * serializable... ie MUPrefs.
     */
    public Object clone () {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;

        ByteArrayInputStream bin = null;
        ObjectInputStream ois = null;

        try {
            // Serialize.
            bos = new ByteArrayOutputStream ();
            oos = new ObjectOutputStream (bos);

            oos.writeObject(this);
            oos.flush();

            // Unserialize.
            bin = new ByteArrayInputStream (bos.toByteArray());
            ois = new ObjectInputStream (bin);

            return ois.readObject();
        } catch (Exception e) {
            System.err.println("Exception in MUPrefs.clone(): " + e);
            throw new Error (e);
        } finally {
            try {
                if (bos != null) bos.close();
                if (oos != null) oos.close();
                if (bin != null) bin.close();
                if (ois != null) ois.close();
            } catch (IOException e) {
                System.err.println("Exception in MUPrefs.clone(): " + e);
                throw new Error (e);
            }
        }
    }
}
