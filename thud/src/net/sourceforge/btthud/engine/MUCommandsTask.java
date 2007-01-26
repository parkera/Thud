//
//  MUCommandsTask.java
//  Thud
//
//  Created by Anthony Parker on Wed Oct 02 2002.
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine;

import java.util.*;
import net.sourceforge.btthud.data.*;

public class MUCommandsTask extends TimerTask {

    MUConnection		conn;
    MUData				data;
    MUPrefs				prefs;
    
    public boolean		forceContacts;
    public boolean		forceTactical;
    public boolean		forceGeneralStatus;
    public boolean		forceArmorStatus;

    int					count;
    
    public MUCommandsTask(MUConnection conn, MUData data, MUPrefs prefs)
    {
        this.conn = conn;
        this.data = data;
        this.prefs = prefs;
    }
    
    public void run()
    {
        // We've been woken up, now we need to decide which commands to send

        /* We have several options on how fast we want to send commands:
             prefs.fastCommandUpdate -> fastest: 1, 2, or 3 seconds
             prefs.mediumCommandUpdate -> 2, 5, 10 seconds
             prefs.slowCommandUpdate -> 3, 10, 15 seconds
             prefs.slugComandUpdate -> slowest: 15, 30, 45 seconds
        */
        
        try
        {
            // If we're above twice the medium update time with no data, then don't send commands, unless the data.lastDataTime is 0
            if ((System.currentTimeMillis() - data.lastDataTime) > (2000 * prefs.mediumCommandUpdate) && data.lastDataTime != 0)
            {
                if (System.currentTimeMillis() - data.lastDataTime > (2000 * prefs.slugCommandUpdate))
                {
                    // If we're over twice the slowest command, reset the timer and try again anyway
                    data.lastDataTime = System.currentTimeMillis();
                }

                // System.out.println("-> Lag: " + (System.currentTimeMillis() - data.lastDataTime));
            }
            else
            {
            	// Do we send a static general info? (See if we've changed units)
                if (data.hudStarted && conn != null && count % (4 * prefs.fastCommandUpdate) == 0)
                {
                    conn.sendCommand("hudinfo sgi");
                }
            	
            	// Do we send general status?
                if (data.hudRunning && (forceGeneralStatus || (count % (4 * prefs.fastCommandUpdate) == 0)))
                {
                    conn.sendCommand("hudinfo gs");
                    forceGeneralStatus = false;
                }

                // Do we send a contacts?
                if (!data.myUnit.status.matches("S|s") && data.hudRunning && (forceContacts || (count % (4 * prefs.fastCommandUpdate) == 0)))
                {
                    conn.sendCommand("hudinfo c");
                    if (data.hiSupportsBuildingContacts())
                        conn.sendCommand("hudinfo cb");
                    synchronized (data)
                    {
                        data.expireAllContacts();
                    }

                    forceContacts = false;
                }

                // Do we send a tactical?
                // If we know we're on an LOS-only map, send it at a faster pace
                if (data.hudRunning && (forceTactical || (count % (4 * (data.mapLOSOnly ? prefs.mediumCommandUpdate : prefs.slugCommandUpdate)) == 0)))
                {
                    conn.sendCommand("hudinfo t " + prefs.hudinfoTacHeight);
                    forceTactical = false;
                }

                // Do we send an armor status?
                if (data.hudRunning && (forceArmorStatus || (count % (4 * prefs.mediumCommandUpdate) == 0)))
                {
                    conn.sendCommand("hudinfo as");
                    // Also send weapon & ammo status at this time
                    conn.sendCommand("hudinfo we");
                    conn.sendCommand("hudinfo am");
                    forceArmorStatus = false;
                }
                
                // Do we send an original armor status? Only do this on startup (or if reset by auto leave/enter code)
                if(conn != null && data.lastDataTime == 0) {
                	conn.sendCommand("hudinfo oas");
                }
            }           
        }
        catch (Exception e)
        {
            System.out.println("Error: MUCommandsTask: " + e);
        }
        
        // Increment our count
        count++;
    }
}
