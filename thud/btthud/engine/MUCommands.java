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

    TimerTask			tacticalTask;
    TimerTask			contactsTask;
    TimerTask			findcenterTask;
    TimerTask			expireTask;
    Timer				timer;

    boolean				sendCommands;
    
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

        // --------

        // tactical
        tacticalTask = new TimerTask() {
            public void run()
            {
                try {
                    if (sendCommands)
                        conn.sendCommand("hudinfo t " + prefs.hudinfoTacHeight);
                    else
                        cancel();
                }
                catch (Exception e) {
                    System.out.println("Error: send hudinfo t: " + e);
                }
            }
        };
        timer.schedule(tacticalTask, 0, (int) (prefs.tacticalDelay * 1000.0));

        // general status
        findcenterTask = new TimerTask() {
            public void run()
            {
                try {
                    if (sendCommands)
                        conn.sendCommand("hudinfo gs");
                    else
                        cancel();
                }
                catch (Exception e) {
                    System.out.println("Error: send hudinfo gs: " + e);
                }
            }
        };
        timer.schedule(findcenterTask, 0, (int) (prefs.findcenterDelay * 1000.0));

        // contacts
        contactsTask = new TimerTask() {
            public void run()
            {
                try {
                    if (sendCommands)
                        conn.sendCommand("hudinfo c");
                    else
                        cancel();
                }
                catch (Exception e) {
                    System.out.println("Error: send hudinfo c: " + e);
                }
            }
        };
        timer.schedule(contactsTask, 0, (int) (prefs.contactsDelay * 1000.0));

        // armor status
        contactsTask = new TimerTask() {
            public void run()
        {
                try {
                    if (sendCommands)
                        conn.sendCommand("hudinfo as");
                    else
                        cancel();
                }
                catch (Exception e) {
                    System.out.println("Error: send hudinfo as: " + e);
                }
        }
        };
        timer.schedule(contactsTask, 0, (int) (prefs.armorRedrawDelay * 1000.0));


        // contacts expiration
        expireTask = new TimerTask() {
            public void run()
            {
                if (sendCommands)
                {
                    synchronized (data)
                    {
                        data.expireAllContacts();
                    }
                }
                else
                {
                    cancel();                    
                }
            }
        };
        timer.schedule(expireTask, 0, (int) (prefs.contactsDelay * 1000.0));
        
    }

    public void endTimers()
    {
        sendCommands = false;

        /*
        // These don't seem to actually cancel the stupid timers, so I put in the boolean to take care of it
         
        if (tacticalTask != null)
            tacticalTask.cancel();

        if (contactsTask != null)
            contactsTask.cancel();

        if (findcenterTask != null)
            findcenterTask.cancel();

        if (expireTask != null)
            expireTask.cancel();
         */
    }
}
