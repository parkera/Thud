//
//  MUData.java
//  Thud
//
//  Created by asp on Tue Nov 20 2001.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//
//
package com.btthud.thud;

/**
 * This class is for storing all the information from contacts, tactical, status, etc.
 *
 * Some notes:
 *   Since a lot of other classes (mainly ones for displaying info) use this, we should keep things
 *  thread safe if possible. We want to store most (if not all) of the info in this one class so that
 * 	we can keep things simple when passing data around.
 *
 *   We could keep the map as another hashtable, with a key value of a string "x,y" where x and y are
 *	the hex values of course. This would work okay because we'll probably need to access hexes in a
 *	non-linear order sometimes (for example, to check to see if a specific hex terrain has changed).
 *
 * @author Anthony Parker
 * @version 1.0, 11.20.01
 */

import java.util.*;

public class MUData {

    // Making these public sorta defeats the purpose of hiding them in the class in the first place, but
    // I just want to make it easier on myself at this point. Maybe I'll fix it later.

    public static final int		MAX_X = 999;
    public static final int		MAX_Y = 999;
    
    public boolean				hudRunning = false;
    
    public Hashtable			contacts = null;
    public MUMyInfo				myUnit = null;

    private MUHex				map[][] = null;

    public MUData()
    {
        hudRunning = false;
        
        contacts = new Hashtable();			// data for our contact list
    
        map = new MUHex[MAX_X][MAX_Y];
        for (int i = 0; i < MAX_X; i++)
            for (int j = 0; j < MAX_Y; j++)
                map[i][j] = new MUHex();

        myUnit = new MUMyInfo();			// data that represents our own unit
    }

    /**
      * Adds a new contact to our list of contacts, or updates an existing one
      */
    public void newContact(MUUnitInfo con)
    {
        try 
        {
            synchronized (contacts)
            {
                if (contacts.containsKey(con.id))
                {
                    // remove old contact
                    contacts.remove(con.id);
                    // insert new contact
                    contacts.put(con.id, con);
                }
                else
                {
                    contacts.put(con.id, con);
                }    
           }
        }
        catch (Exception e)
        {
            System.out.println("Error: newContact: " + e);
        }
    }
    
    /**
      * Removes a contact from our list
      */
    public void removeContact(String id)
    {
        try
        {
            if (contacts.containsKey(id))
                contacts.remove(id);
        }
        catch (Exception e)
        {
            System.out.println("Error: removeContact: " + e);
        }
    }
    
    /**
      * Marks a contact as 'expired'. If it is expired, it will be removed next pass
      */
    public void expireContact(String id)
    {
        MUUnitInfo		unit;
        
        try
        {
            unit = (MUUnitInfo) contacts.get(id);
            unit.expireMore();
            contacts.remove(id);
            contacts.put(id, unit);
        }
        catch (Exception e)
        {
            System.out.println("Error: expireContact: " + e);
        }
    }

    // ----------------------------------

    public char getHexTerrain(int x, int y)
    {
        char		ter;
        
        if (x > 0 && x < MAX_X && y > 0 && y < MAX_Y)
            ter = map[x][y].terrain;
        else
            ter = '?';

        return ter;
    }

    public int getHexElevation(int x, int y)
    {
        int			elev;

        if (x > 0 && x < MAX_X && y > 0 && y < MAX_Y)
            elev = map[x][y].elevation;
        else
            elev = 0;

        return elev;
    }

    public void setHex(int x, int y, char ter, int elevation)
    {
        if (x >= 0 && x < MAX_X && y >= 0 && y < MAX_Y)
        {
            map[x][y].terrain = ter;
            map[x][y].elevation = elevation;
        }
    }

    public MUHex getHex(int x, int y)
    {
        MUHex		newHex = null;

        if (x >= 0 && x < MAX_X && y >= 0 && y < MAX_Y)
            newHex = map[x][y];
        else
            newHex = new MUHex();

        return newHex;
    }

    /**
        * Clear data that is 'Mech specific, so that when we start the HUD again we have a clean slate.
      */
    public void clearData()
    {
        // Clear contacts and our unit, but leave the map alone
        contacts = new Hashtable();			// data for our contact list
        myUnit = new MUMyInfo();			// data that represents our own unit
    }
}
