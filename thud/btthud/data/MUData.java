//
//  MUData.java
//  Thud
//
//  Created by asp on Tue Nov 20 2001.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//
//
package btthud.data;

import java.util.*;

/**
 * This class is for storing all the information from contacts and tactical.
 *
 * Some notes:
 *   Since a lot of other classes (mainly ones for displaying info) use this, we should keep things
 *  thread safe if possible. We want to store most (if not all) of the info in this one class so that
 * 	we can keep things simple when passing data around.
 *
 * @author Anthony Parker
 */


public class MUData {

    // Making these public sorta defeats the purpose of hiding them in the class in the first place, but
    // I just want to make it easier on myself at this point. Maybe I'll fix it later.

    public static final int		MAX_X = 1000;
    public static final int		MAX_Y = 1000;
    
    public boolean				hudRunning = false;
    
    public MUMyInfo				myUnit = null;

    // The map
    MUHex						map[][] = null;
    boolean						terrainChanged = true;

    // One MUHex for each elevation and terrain
    // By storing references to MUHexes we can save memory
    // 19 = -9 thru 0 and 1 thru 9
    MUHex						hexCache[][] = new MUHex[MUHex.TOTAL_TERRAIN][19];
    
    // We store the contact data in a ArrayList, because we need to iterate over it efficiently
    ArrayList					contacts = null;

    // This is the time that we received our last hudinfo data
    public long					lastDataTime;
    
    public MUData()
    {
        hudRunning = false;

        clearData();

        createHexCache();
        
        map = new MUHex[MAX_X][MAX_Y];		// individual hexes will be allocated if they are needed.. this is not very memory efficient still
        
    }

    public void createHexCache()
    {
        for (int i = 0; i < MUHex.TOTAL_TERRAIN; i++)
        {
            for (int j = -9; j < 10; j++)
            {
                hexCache[i][j + 9] = new MUHex(i, j);
            }
        }
    }
    
    /**
      * Adds a new contact to our list of contacts, or updates an existing one
      */
    public void newContact(MUUnitInfo con)
    {
        int			index = indexForId(con.id);

        if (index != -1)
            contacts.set(index, con);
        else
            contacts.add(con);
    }

    /**
      * Iterates through the contact list and marks contacts as expired or old or whatever
      */
    public void expireAllContacts()
    {
        try
        {
            // We need a ListIterator because it allows us to modify the list while we iterate
            ListIterator		it = contacts.listIterator();

            while (it.hasNext())
            {
                MUUnitInfo		unit = (MUUnitInfo) it.next();

                if (unit.isExpired())
                    it.remove();					// Remove this contact		
                else
                    unit.expireMore();				// Increase the age of this contact
            }
        }
        catch (Exception e)
        {
            System.out.println("Error: expireAllContacts: " + e);
        }            
    }

    /**
      * Returns the index of a specified id
      * Since we keep track of everything in our LinkedList in our hashtable, just check the hashtable to see if we have it
      */
    protected int indexForId(String id)
    {
        ListIterator		it = contacts.listIterator();
        int					index;
        
        while (it.hasNext())
        {
            index = it.nextIndex();

            // See if the next unit's upper-case id matches the id sent in
            if (((MUUnitInfo) it.next()).id.toUpperCase().equals(id.toUpperCase()))
                return index;            
        }

        // Must not have found it
        return -1;
    }

    /**
      * Returns an Iterator for the contact list. Used for looping on contacts when drawing the map, for example
      * @param sorted True if we want a sorted list
      */
    public Iterator getContactsIterator(boolean sorted)
    {
        if (!sorted)
            return contacts.iterator();
        else
            return ((new TreeSet(contacts)).iterator());
    }
    
    // ----------------------------------

    /**
      * Get the terrain of a specific hex (return the id, not the char)
      * @param x X coordinate
      * @param y Y coordinate
      */
    public int getHexTerrain(int x, int y)
    {        
        if (x >= 0 && x < MAX_X && y >= 0 && y < MAX_Y)
        {
            if (map[x][y] != null)
                return map[x][y].terrain();
            else
                return MUHex.UNKNOWN;
        }

        return MUHex.UNKNOWN;
    }

    /**
     * Get the elevation of a specific hex
     * @param x X coordinate
     * @param y Y coordinate
     */
    public int getHexElevation(int x, int y)
    {
        if (x >= 0 && x < MAX_X && y >= 0 && y < MAX_Y)
        {
            if (map[x][y] != null)
                return map[x][y].elevation();
            else
                return 0;
        }

        return 0;
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

        if (getHexTerrain(x, y) == MUHex.ICE)		// ice
            e = 0;									// You can cross it, even tho it may be dangerous
        
        return e;
    }

    /**
     * Set the details of a specific hex
     * @param x X coordinate
     * @param y Y coordinate
     * @param ter The terrain character representation
     * @param elevation The elevation of the hex
     */
    public void setHex(int x, int y, char ter, int elevation)
    {
        if (x >= 0 && x < MAX_X && y >= 0 && y < MAX_Y)
        {
            map[x][y] = hexCache[MUHex.idForTerrain(ter)][elevation + 9];
        }
    }
    
  /**
    * Clear data that is 'Mech specific, so that when we start the HUD again we have a clean slate.
    */
    public void clearData()
    {
        // Clear contacts and our unit, but leave the map alone
        contacts = new ArrayList(20);		// data for our contact list
        myUnit = new MUMyInfo();			// data that represents our own unit
        lastDataTime = 0;					// clear our last recieved data
    }

    /**
      * Sets the map changed flag.
      */
    public void setTerrainChanged(boolean b)
    {
        terrainChanged = b;
    }

    /**
      * Has the terrain changed?
      */
    public boolean terrainChanged()
    {
        return terrainChanged;
    }
}
