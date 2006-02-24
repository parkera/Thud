//
//  MUHex.java
//  Thud
//
//  Created by Anthony Parker on Mon Dec 17 2001.
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.data;

import java.awt.*;
import java.io.Serializable;

public class MUHex implements Serializable
{
    // Statics
    public static final int		UNKNOWN = 0;
    public static final int		PLAIN = 1;
    public static final int		WATER = 2;
    public static final int		LIGHT_FOREST = 3;
    public static final int		HEAVY_FOREST = 4;
    public static final int		MOUNTAIN = 5;
    public static final int		ROUGH = 6;
    public static final int		BUILDING = 7;
    public static final int		ROAD = 8;
    public static final int		BRIDGE = 9;
    public static final int		FIRE = 10;
    public static final int		WALL = 11;
    public static final int		SMOKE = 12;
    public static final int		ICE = 13;
    public static final int		SMOKE_OVER_WATER = 14;

    public static final int		TOTAL_TERRAIN = 15;

    // There should be the same number of items in this array as TOTAL_TERRAIN
    public static final char	terrainTypes[] = {'?', '.', '~', '`', '"', '^', '%', '@', '#', '/', '&', '=', ':', '-', '+'};

    // Variables
    int							terrain;
    int							elevation;
    
    /**
     * Constructor with default terrain.
     */
    public MUHex()
    {
        // Default terrain is a level 0 unknown
        terrain = UNKNOWN;
        elevation = 0;
    }

    /**
     * Constructor with specified terrain.
     * @param terrain		terrain for new hex
     * @param elevation		elevation for new hex
     */
    public MUHex(int terrain, int elevation)
    {
        this.terrain = terrain;
        this.elevation = elevation;
    }
    
    /**
     * Returns id for terrain of hex (not char).
     * @return	ID for this hex
     */
    public int terrain() {
        return terrain;
    }
    
    /**
     * Returns elevation for hex.
     * @return	Elevation for this hex
     */
    public int elevation() {
        return elevation;
    }

    /**
     * Set this hex's terrain
     * @param	t		new terrain for hex 	
     */
    public void setTerrain(char t) {
        terrain = idForTerrain(t);
    }
    
    /**
     * Set this hex's elevation
     * @param	e		new elevation for hex
     */
    public void setElevation(int e) {
        elevation = e;
    }

    // -----------------------------------
    // STATIC METHODS
    // -----------------------------------

    static public Color colorForElevation(Color ic, int e, float elevationColorMultiplier)
    {
        float[] 	comp = ic.getRGBColorComponents(null);
        float		mod = elevationColorMultiplier * e;
        float[]		newComp = {comp[0], comp[1], comp[2]};

        for (int i = 0; i < 3; i++)
        {
            newComp[i] -= mod;
            if (newComp[i] < 0.0f)
                newComp[i] = 0.0f;
        }

        return new Color(newComp[0], newComp[1], newComp[2]);
    }

    /**
     * Get the constant for the terrain
     */ 
    static public int idForTerrain(char terr)
    {
        switch (terr)
        {
            case '.':							// plain
                return PLAIN;
            case '~':							// water
                return WATER;
            case '`':							// light forest
                return LIGHT_FOREST;
            case '"':							// heavy forest
                return HEAVY_FOREST;
            case '^':							// mountain
                return MOUNTAIN;
            case '%':							// rough
                return ROUGH;
            case '@':							// building
                return BUILDING;
            case '#':							// road
                return ROAD;
            case '/':							// bridge
                return BRIDGE;
            case '&':							// fire
                return FIRE;
            case '=':							// wall
                return WALL;
            case ':':							// smoke
                return SMOKE;
            case '-':							// ice
                return ICE;
            case '+':
                return SMOKE_OVER_WATER;
            case '?':
                return UNKNOWN;
            default:
                return UNKNOWN;
        } 
    }

    /** Get the name for the terrain
     */
    static public String nameForId(int id)
    {
        switch (id)
        {
            case PLAIN:							
                return "Plain";
            case WATER:							
                return "Water";
            case LIGHT_FOREST:							
                return "Light Forest";
            case HEAVY_FOREST:							
                return "Heavy Forest";
            case MOUNTAIN:						
                return "Mountain";
            case ROUGH:						
                return "Rough";
            case BUILDING:						
                return "Building";
            case ROAD:						
                return "Road";
            case BRIDGE:					
                return "Bridge";
            case FIRE:						
                return "Fire";
            case WALL:						
                return "Wall";
            case SMOKE:						
                return "Smoke";
            case ICE:						
                return "Ice";
            case SMOKE_OVER_WATER:
                return "Smoke on Water";
            case UNKNOWN:
                return "Unknown";
            default:
                return "Unknown";
        } 
    }

    /** Get the terrain for the constant. */
    static public char terrainForId(int id)
    {
        if (id < 0 || id >= TOTAL_TERRAIN)
            return '?';
        else
            return terrainTypes[id];
    }
    
}
