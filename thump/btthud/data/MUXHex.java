//
//  MUXHex.java
//  Thud
//
//  Created by Anthony Parker on Mon Dec 17 2001.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//
//
package btthud.data;

import java.awt.*;

public class MUXHex
{
    // Statics
    public static final int		ROUGH = 0;
    public static final int		PLAIN = 1;
    public static final int		HEAVY_FOREST = 2;
    public static final int		LIGHT_FOREST = 3;
    public static final int		BRIDGE = 4;
    public static final int		ROAD = 5;
    public static final int		WALL = 6;
    public static final int		BUILDING = 7;
    public static final int		WATER = 8;
    public static final int		MOUNTAIN = 9;
    public static final int		FIRE = 10;
	public static final int		DESERT = 11;
    public static final int		SMOKE = 12;
    public static final int		SMOKE_OVER_WATER = 13;
    public static final int		ICE = 14;
    public static final int		UNKNOWN = 15;
    
    public static final int		TOTAL_TERRAIN = 16;
    public static final int		TOTAL_PAINTABLE_TERRAIN = 12;
    
    // There should be the same number of items in this array as TOTAL_TERRAIN
    public static final char	TERRAIN_TYPES[] = {'%', '.', '"', '`', '/', '#', '=', '@', '~', '^', '&', '}', ':', '+', '-', '?'};
    public static final char	PAINTABLE_TERRAIN_TYPES [] = {'%', '.', '"', '`', '/', '#', '=', '@', '~', '^', '&', '}'};
    
    // Variables
    int							terrain;
    int							elevation;
    
    public MUXHex()
    {
        // Default terrain is a level 0 unknown
        terrain = UNKNOWN;
        elevation = 0;
    }

    public MUXHex(int terrain, int elevation)
    {
        this.terrain = terrain;
        this.elevation = elevation;
    }
    
    // ---------------

    // Return the 'id' for the terrain, not the char
    // If they want a character, they can figure it out themselves
    public int terrain() {
        return terrain;
    }
    
    public char terrainChar() {
        return MUXHex.terrainForId(terrain);
    }

    public int elevation() {
        return elevation;
    }
    
    // Take a character and turn it into an id for storage
    public void setTerrain(char t) {
        terrain = idForTerrain(t);
    }

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

    // Get the constant for the terrain
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
			case '}':							// desert
                return DESERT;
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

    // Get the name for the terrain
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
			case DESERT:						
                return "Desert";
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

    // Get the terrain for the constant
    static public char terrainForId(int id)
    {
        if (id < 0 || id >= TOTAL_TERRAIN)
            return '?';
        else
            return TERRAIN_TYPES[id];
    }
    
}
