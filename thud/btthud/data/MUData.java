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

    protected MUHex				map[][] = null;

    // We store the contact data in a ArrayList, because we need to iterate over it efficiently
    protected ArrayList			contacts = null;
    
    public MUData()
    {
        hudRunning = false;

        clearData();
    
        map = new MUHex[MAX_X][MAX_Y];		// individual hexes will be allocated if they are needed.. this is not very memory efficient still
        
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
      * Get the terrain of a specific hex
      * @param x X coordinate
      * @param y Y coordinate
      */
    public char getHexTerrain(int x, int y)
    {        
        if (x >= 0 && x < MAX_X && y >= 0 && y < MAX_Y)
        {
            if (map[x][y] != null)
                return map[x][y].terrain();
            else
                return '?';
        }

        return '?';
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

        if (getHexTerrain(x, y) == '-')		// ice
            e = 0;							// You can cross it, even tho it may be dangerous
        
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
            if (map[x][y] == null)
                map[x][y] = new MUHex();
            
            map[x][y].setTerrain(ter);
            map[x][y].setElevation(elevation);
        }
    }

    /**
      * Get a MUHex for given coordinates
      * @param x X coordinate
      * @param y Y coordinate
      */
    public MUHex getHex(int x, int y)
    {
        if (x >= 0 && x < MAX_X && y >= 0 && y < MAX_Y)
        {
            if (map[x][y] != null)
                return map[x][y];
            else
                return new MUHex();
        }

        return new MUHex();
    }
    
  /**
    * Clear data that is 'Mech specific, so that when we start the HUD again we have a clean slate.
    */
    public void clearData()
    {
        // Clear contacts and our unit, but leave the map alone
        contacts = new ArrayList(20);			// data for our contact list
        myUnit = new MUMyInfo();			// data that represents our own unit
    }
}
