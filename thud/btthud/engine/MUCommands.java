//
//  MUCommands.java
//  Thud
//
//  Created by Anthony Parker on Wed Jan 02 2002.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//
//
package btthud.engine;

import btthud.data.*;
import java.util.*;

public class MUCommands
{
    MUPrefs				prefs;
    MUConnection		conn;
    MUData				data;

    Timer				timer;

    boolean				sendCommands;

    MUCommandsTask		commandSendingTask;
    
    public MUCommands(MUConnection conn, MUData data, MUPrefs prefs)
    {
        this.conn = conn;
        this.data = data;
        this.prefs = prefs;
        
        sendCommands = false;
    }

    public void sendCommand(String c)
    {
        try {
            if (this.conn != null)
                conn.sendCommand(c);
        } catch (Exception e) {
            System.out.println("Error: MUCommands: sendCommand: " + e);
        }
    }

    public void refreshTactical()
    {
        sendCommand("hudinfo t " + prefs.hudinfoTacHeight);
    }
    
    public void startTimers()
    {
        sendCommands = true;
        
        // Send some initial commands
        try
        {
            conn.sendCommand("hudinfo sgi");
            conn.sendCommand("hudinfo oas");
            conn.sendCommand("hudinfo wl");
            conn.sendCommand("hudinfo we");
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
        sendCommands = false;
    }
}
