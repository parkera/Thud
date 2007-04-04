//
//  MUCommands.java
//  Thud
//
//  Created by Anthony Parker on Wed Jan 02 2002.
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine;

import net.sourceforge.btthud.engine.commands.HUDVersion;
import net.sourceforge.btthud.engine.commands.HUDGeneralStatic;
import net.sourceforge.btthud.engine.commands.HUDArmorOriginal;
import net.sourceforge.btthud.engine.commands.HUDWeaponList;
import net.sourceforge.btthud.engine.commands.HUDWeaponStatus;
import net.sourceforge.btthud.engine.commands.HUDConditions;

import net.sourceforge.btthud.data.*;
import java.util.*;

public class MUCommands
{
    MUPrefs				prefs;
    MUConnection		conn;
    MUData				data;

    Timer				timer;

    MUCommandsTask		commandSendingTask;
    
    public MUCommands(MUConnection conn, MUData data, MUPrefs prefs)
    {
        this.conn = conn;
        this.data = data;
        this.prefs = prefs;
    }

    public void forceTactical()
    {
        commandSendingTask.forceTactical = true;
        commandSendingTask.forceLOS = true;
    }
    
    public void startTimers()
    {        
        // Send some initial commands
        try
        {
            conn.sendCommand(new HUDVersion ()); // Get the version
            conn.sendCommand(new HUDGeneralStatic ());
            conn.sendCommand(new HUDArmorOriginal ());
            conn.sendCommand(new HUDWeaponList ());
            conn.sendCommand(new HUDWeaponStatus ());
            conn.sendCommand(new HUDConditions ());
        }
        catch (Exception e)
        {
            System.out.println("Error: startTimers: " + e);
        }

        // create a new timer for all of our fun commands to be sent to the MUX
        timer = new Timer(true);

        // Create our command-sending task
        commandSendingTask = new MUCommandsTask(conn, data, prefs);
        timer.schedule((TimerTask) commandSendingTask, 0, 250);			// Runs every quarter-second
        
    }

    public void endTimers()
    {
        if (timer != null)
            timer.cancel();    	
    }
}
