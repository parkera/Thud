//
//  MUXMap.java
//  Thump
//
//  Created by Anthony Parker on Sat Jan 11 2003.
//  Copyright (c) 2003 Anthony Parker. All rights reserved.
//

package btthud.data;

import java.io.*;
import java.awt.*;
import java.util.*;

public class MUXMap implements Serializable {

    MUXHex              hexCache[][] = new MUXHex[MUXHex.TOTAL_TERRAIN][19];		// 19 different elevations
    MUXHex		map[][] = null;
    boolean		hexesChanged[][];
    
    int                 sizeX, sizeY;

    boolean		hasChanged;

    LinkedList		selectedHexes;

    // -------------------------------------------------
    // Constructors
    
    public MUXMap(int x, int y)
    {
        sizeX = x;
        sizeY = y;
        
        map = new MUXHex[x][y];
        hexesChanged = new boolean[x][y];
        
        hasChanged = false;

        selectedHexes = new LinkedList();
        
        createHexCache();
    }

    // ----------------------------------

    /**
      * Clears the map (makes all the terrain level 0 plains).
      */
    public void clearMap()
    {
        // I think this will break for non-square maps
        
        for (int x = 0; x < sizeX; x++)
            for (int y = 0; y < sizeY; y++)
                map[x][y] = hexCache[MUXHex.PLAIN][9];

        resetChanged();
    }

    public void resetChanged()
    {
        for (int x = 0; x < sizeX; x++)
            for (int y = 0; y < sizeY; y++)
                hexesChanged[x][y] = true;
    }

    // ----------------------------------

    /**
      * Returns a hex
      */
    public MUXHex getHex(int x, int y)
    {
        if (x >= 0 && x < getSizeX() && y >= 0 && y < getSizeY())
            return map[x][y];
        else
            return null;
    }

    public MUXHex getHex(Point p)
    {
        return getHex((int) p.getX(), (int) p.getY());
    }

    /**
      * Get the selection status of a specific hex
      */
    public boolean getHexSelected(int x, int y)
    {
        Point p = new Point(x, y);
        
        if (validHex(p) && selectedHexes.indexOf(p) != -1)
            return true;
        else
            return false;
    }

    public boolean getHexSelected(Point p)
    {
        return getHexSelected((int) p.getX(), (int) p.getY());
    }
    
    /**
     * Get the terrain of a specific hex (return the id, not the char)
     * @param x X coordinate
     * @param y Y coordinate
     */
    public int getHexTerrain(int x, int y)
    {
        if (x >= 0 && x < sizeX && y >= 0 && y < sizeY)
        {
            if (map[x][y] != null)
                return map[x][y].terrain();
            else
                return MUXHex.UNKNOWN;
        }

        return MUXHex.UNKNOWN;
    }

    public int getHexTerrain(Point h)
    {
        return getHexTerrain((int) h.getX(), (int) h.getY());
    }
    
    /**
     * Get the elevation of a specific hex
     * @param x X coordinate
     * @param y Y coordinate
     */
    public int getHexElevation(int x, int y)
    {
        if (x >= 0 && x < sizeX && y >= 0 && y < sizeY)
        {
            if (map[x][y] != null)
                return map[x][y].elevation();
            else
                return 0;
        }

        return 0;
    }

    public int getHexElevation(Point h)
    {
        return getHexElevation((int) h.getX(), (int) h.getY());
    }

    /**
     * Get the absolute elevation of a specific hex
     * @param x X coordinate
     * @param y Y coordinate
     */
    public int getHexAbsoluteElevation(int x, int y)
    {
        int	e = getHexElevation(x, y);
        if (e < 0)
            e = -e;
        // Since we use this function in determining cliff edges, a few corrections...

        if (getHexTerrain(x, y) == MUXHex.ICE)		// ice
            e = 0;									// You can cross it, even tho it may be dangerous

        return e;
    }

    public int getHexAbsoluteElevation(Point h)
    {
        return getHexAbsoluteElevation((int) h.getX(), (int) h.getY());
    }

    /**
      * Get the changed flag on a hex
      */
    public boolean getHexChanged(int x, int y)
    {
        return hexesChanged[x][y];
    }

    public boolean getHexChanged(Point h)
    {
        return getHexChanged((int) h.getX(), (int) h.getY());
    }

    /**
      * Set only the elevation of a hex
      */
    public void setHexElevation(int x, int y, int elevation)
    {
        setHex(x, y, getHexTerrain(x, y), elevation);
    }

    public void setHexElevation(Point h, int elevation)
    {
        setHexElevation((int) h.getX(), (int) h.getY(), elevation);
    }

    /**
      * Set only the terrain of a hex
      */
    public void setHexTerrain(int x, int y, int terr)
    {
        setHex(x, y, terr, getHexElevation(x, y));
    }

    public void setHexTerrain(Point h, int terr)
    {
        setHexTerrain((int) h.getX(), (int) h.getY(), terr);
    }

    /**
      * Set the 'changed' flag on a hex
      */
    public void setHexChanged(int x, int y, boolean c)
    {
        hexesChanged[x][y] = c;
    }

    public void setHexChanged(Point h, boolean c)
    {
        setHexChanged((int) h.getX(), (int) h.getY(), c);
    }
    
    /**
     * Set the details of a specific hex using a character type for terrain
     * @param x X coordinate
     * @param y Y coordinate
     * @param ter The terrain character representation
     * @param elevation The elevation of the hex
     */
    public void setHex(int x, int y, char ter, int elevation)
    {
        setHex(x, y, MUXHex.idForTerrain(ter), elevation);
    }

    public void setHex(Point h, char ter, int elevation)
    {
        setHex((int) h.getX(), (int) h.getY(), ter, elevation);
    }

    /**
      * Set a hex to selected or not
      */
    public void setHexSelected(int x, int y, boolean s)
    {
        setHexSelected(new Point(x, y), s);
    }

    public void setHexSelected(Point p, boolean s)
    {
        if (validHex(p))
        {
            if (s)
            {
                // Add this hex to our list of selected hexes
                if (selectedHexes.indexOf(p) == -1)
                    selectedHexes.add(p);
            }
            else
            {
                // Remove this hex from our list of selected hexes
                selectedHexes.remove(p);
            }
        }
    }

    /**
      * Clear our list of selected hexes
      */
    public void deselectAll()
    {
        selectedHexes = new LinkedList();
    }

    /**
      * Get a list of selected hexes
      */
    public LinkedList selectedHexes()
    {
        return selectedHexes;
    }
    
    /**
      * Set the details of a specific hex using internal int type for terrain
      */
    public void setHex(int x, int y, int ter, int elevation)
    {
        if (x >= 0 && x < sizeX && y >= 0 && y < sizeY && ter >= 0 && ter < MUXHex.TOTAL_TERRAIN)
        {
            map[x][y] = hexCache[ter][elevation + 9];
        }

        // Well, we've changed now...
        hasChanged = true;
        hexesChanged[x][y] = true;
    }

    public void setHex(Point h, int ter, int elevation)
    {
        setHex((int) h.getX(), (int) h.getY(), ter, elevation);
    }


    /**
      * Get the size of our map, X
      */
    public int getSizeX()
    {
        return sizeX;
    }

    /**
     * Get the size of our map, Y
     */
    public int getSizeY()
    {
        return sizeY;
    }

    /**
      * A useful function to see if a specific hex actually exists on our map
      */
    public boolean validHex(Point h)
    {
        if (h.getX() >= 0 && h.getX() < getSizeX() && h.getY() >= 0 && h.getY() < getSizeY())
            return true;
        else
            return false;
    }
    // --------------------------------------------
        
    /**
      * Creates the hex cache so we can be speedy!
      */
    protected void createHexCache()
    {
        for (int i = 0; i < MUXHex.TOTAL_TERRAIN; i++)
        {
            for (int j = -9; j < 10; j++)
            {
                hexCache[i][j + 9] = new MUXHex(i, j);
            }
        }
    }

    // -------------------------------------------

    /**
      * We keep track of the data in this map, to make sure we ask to save properly if it's changed
      */
    public boolean hasChanged()
    {
        return hasChanged;
    }

    /**
      * So we can reset or set the changed flag
      */
    public void setChanged(boolean c)
    {
        hasChanged = c;
    }
}
