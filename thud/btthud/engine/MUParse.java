//
//  MUParse.java
//  JavaTelnet
//
//  Created by asp on Mon Nov 19 2001.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//
//
package btthud.engine;

import btthud.data.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;

import java.lang.*;

import java.util.*;

import btthud.util.*;

public class MUParse implements Runnable {

    // Variables
    JTextPane				textPane = null;
    MUData					data = null;
    BulkStyledDocument		doc = null;
    MUPrefs					prefs = null;
    MUCommands				commands = null;
    
    int						hudInfoMajorVersion = 0;		// Version 0 means not available
    int						hudInfoMinorVersion = 0;

    String					sessionKey;

    LinkedList				queue;

    boolean					go;
    private Thread			parseThread = null;
    
    // Constructor
    public MUParse(JTextPane textPane, MUData data, BulkStyledDocument doc, MUPrefs prefs)
    {
        // Init here
        this.textPane = textPane;
        this.data = data;
        this.doc = doc;
        this.prefs = prefs;

        // Setup our queue of strings
        queue = new LinkedList();

        go = true;
        
        // Start the thread
        start();
    }
    
    // Methods

    // -------------------------------------------------------
    // Setters and Getters
    
    public int getHudInfoMajorVersion() {
        return hudInfoMajorVersion;
    }

    public void setHudInfoMajorVersion(int v) {
        hudInfoMajorVersion = v;
    }

    // ---
    
    public int getHudInfoMinorVersion() {
        return hudInfoMinorVersion;
    }

    public void setHudInfoMinorVersion(int v) {
        hudInfoMinorVersion = v;
    }

    // ---

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String newKey) {
        sessionKey = newKey;
    }

    // ---

    public void setCommands(MUCommands commands)
    {
        this.commands = commands;
    }

    // -------------------------------------------------------

    /**
      * Add a line to the list of Strings that we need to parse.
      */
    public void queueLine(String l)
    {
        // Add it to the end of the list, so strings come in order
        synchronized (queue) {
            queue.addLast(l);
        }
    }
    
    /**
     * Check to see if a line needs to be matched, then insert it into the document.
     * @param l The line we are parsing
     */
    protected void parseLine(String l)
    {
        // Don't output if we get a match
        boolean		matched = false;
        
        ArrayList	es = doc.parseString(l);

        if (l == null)
            return;
        
        try
        {
            matched = matchHudInfoCommand(l);
            matchForCommandSending(l);
            
            if (!matched)
            {
                if (l.length() == 0)
                    doc.insertNewLine();
                
                doc.insertParsedString(es);

                textPane.setCaretPosition(doc.getLength());                
            }
        }
        catch (Exception e)
        {
            System.out.println("Error: parseLine: " + e);
        }
    }

    /**
     * Just like parseLine, but designed for messages from the HUD. These don't need to be matched, so we can save ourselves some CPU time.
     * @param l The line that we are parsing
     */
    public void messageLine(String l)
    {
        doc.insertMessageString(l);
        textPane.setCaretPosition(doc.getLength());
    }

    public void commandLine(String l)
    {
        doc.insertCommandString(l);
        textPane.setCaretPosition(doc.getLength());
    }

    /**
     * Checks to see if a specified line is a HUD command. If so, then do something about it.
     * @param l The text line that contains the potential command.
     */
    public boolean isHudCommand(String l)
    {
        if (l.startsWith("setzoom"))
        {
            int newHeight;
            
            StringTokenizer	st = new StringTokenizer(l);
            st.nextToken();

            try
            {
                newHeight = Integer.parseInt(st.nextToken().trim());

                if (newHeight < 5)
                    newHeight = 5;
                else if (newHeight > 200)
                    newHeight = 200;

                prefs.hexHeight = newHeight;
                messageLine(": Zoom set to " + newHeight);
            }
            catch (NumberFormatException e)
            {
                messageLine(": Invalid height for setzoom");
            }

            return true;
        }
        else if (l.startsWith("setupdate"))
        {
            double newDelay;

            StringTokenizer	st = new StringTokenizer(l);
            st.nextToken();

            try
            {
                newDelay = Double.parseDouble(st.nextToken().trim());

                if (newDelay < 0.5)
                    newDelay = 0.5;

                prefs.contactsDelay = newDelay;
                prefs.findcenterDelay = newDelay;
                prefs.tacticalRedrawDelay = newDelay;

                if (data.hudRunning)
                {
                    commands.endTimers();
                    commands.startTimers();
                }
                
                messageLine(": Update set to " + newDelay);
            }
            catch (NumberFormatException e)
            {
                messageLine(": Invalid time for setupdate");
            }

            return true;
        }
        
        return false;
    }

    public boolean matchForCommandSending(String l)
    {
        if (l.startsWith("Pos changed to ") && data.hudRunning)
            commands.refreshTactical();

        return false;
    }
    public boolean matchHudInfoCommand(String l)
    {
        if (l.startsWith("#HUD:" + sessionKey + ":"))
        {
            synchronized (data)
            {
                // Must be a result for us to parse
                StringTokenizer st = new StringTokenizer(l);
                // Get the first word, ie #HUD:key:GS:R#
                String			firstWord = st.nextToken();
                // And get the part which specifies which command we're looking at
                StringTokenizer st2 = new StringTokenizer(firstWord, ":");
                // Skip the #HUD and key
                st2.nextToken(); st2.nextToken();

                String			whichCommand = st2.nextToken();

                // Get the rest of our string, for passing to other functions
                String			restOfCommand = st.nextToken();
                while (st.hasMoreTokens())
                    restOfCommand = restOfCommand + " " + st.nextToken();

                if (whichCommand.equals("???") ||
                    restOfCommand.startsWith("Not in a BattleTech unit") ||
                    restOfCommand.startsWith("You are destroyed!") ||
                    restOfCommand.startsWith("Reactor is not online"))
                {
                    //data.hudRunning = false;
                    //commands.endTimers();
                    //messageLine("> Please stop display... Reason: " + restOfCommand);
                    return true;
                }

                // Now we check it against everything
                if (whichCommand.equals("GS"))		// general status
                    parseHudInfoGS(restOfCommand);
                else if (whichCommand.equals("C"))	// contacts
                    parseHudInfoC(restOfCommand);
                else if (whichCommand.equals("T"))	// tactical
                {
                    // Now we're expecting an 'S', an 'L', or a 'D'
                    String subCommand = st2.nextToken();
                    if (subCommand.equals("S#"))
                        parseHudInfoTS(restOfCommand);
                    else if (subCommand.equals("L#"))
                        parseHudInfoTL(restOfCommand);
                    else if (subCommand.equals("D#"))
                        parseHudInfoTD(restOfCommand);
                }
                else if (whichCommand.equals("SGI"))	// static general information
                    parseHudInfoSGI(restOfCommand);
                else if (whichCommand.equals("AS"))		// Armor status
                    parseHudInfoAS(restOfCommand);
                else if (whichCommand.equals("OAS"))	// Original armor status
                    parseHudInfoOAS(restOfCommand);
                else if (whichCommand.equals("KEY"))	// 'Key set' .. don't need to do anything further
                    return true;
                else if (whichCommand.equals("WL"))		// Weapon list
                    parseHudInfoWL(restOfCommand);
                else if (whichCommand.equals("WE"))		// Our own weapons
                    parseHudInfoWE(restOfCommand);
                else
                    messageLine("> Unrecognized HUDINFO data: " + whichCommand);

                return true;
            }
        }
        else if (l.startsWith("#HUD hudinfo"))
            parseHudInfoVersion(l);

        return false;
    }

    /**
      * Parse a HUDINFO version number.
      * @param l The entire string line.
      */
    public void parseHudInfoVersion(String l)
    {
        // 	#HUD hudinfo version 1.0 [options: <option flags>]
        StringTokenizer st = new StringTokenizer(l);
        // Skip #HUD hudinfo version
        st.nextToken(); st.nextToken(); st.nextToken();

        StringTokenizer st2 = new StringTokenizer(st.nextToken(), ".");
        hudInfoMajorVersion = Integer.parseInt(st2.nextToken());
        hudInfoMinorVersion = Integer.parseInt(st2.nextToken());
    }
    
    /**
      * Parse a string that represents 'general status.'
      * @param l The data from the hudinfo command, minus the header with the key.
      */
    public void parseHudInfoGS(String l)
    {
        try
        {
            StringTokenizer st = new StringTokenizer(l, ",");
            MUMyInfo		info = data.myUnit;
            String			tempStr;
            
            if (info == null)
                info = new MUMyInfo();
    
            // See hudinfospec.txt for exact formatting information
            
            info.id = st.nextToken();
            //info.id = "**";		// we don't want our own ID for now
            
            info.x = Integer.parseInt(st.nextToken());
            info.y = Integer.parseInt(st.nextToken());
            info.z = Integer.parseInt(st.nextToken());
    
            info.heading = Integer.parseInt(st.nextToken());
            info.desiredHeading = Integer.parseInt(st.nextToken());
            info.speed = Float.parseFloat(st.nextToken());
            info.desiredSpeed = Float.parseFloat(st.nextToken());
    
            info.heat = Integer.parseInt(st.nextToken());
            info.heatDissipation = Integer.parseInt(st.nextToken());
    
            tempStr = st.nextToken();
            if (!tempStr.equals("-"))
                info.fuel = Integer.parseInt(tempStr);
    
            info.verticalSpeed = Float.parseFloat(st.nextToken());
            info.desiredVerticalSpeed = Float.parseFloat(st.nextToken());
    
            info.rangeToCenter = Float.parseFloat(st.nextToken());
            info.bearingToCenter = Integer.parseInt(st.nextToken());

            tempStr = st.nextToken();
            if (!tempStr.equals("-"))
                info.turretHeading = Integer.parseInt(tempStr);			// also corresponds to rottorso
    
            if (st.hasMoreTokens())
                info.status = st.nextToken();
            else
                info.status = "";

            //messageLine("DEBUG: rtc: " + info.rangeToCenter + " btc: " + info.bearingToCenter);
        }
        catch (Exception e)
        {
            System.out.println("Error: parseHudInfoGS: " + e);
        }
    }

    /**
      * Parse a string which represents 'static general information' - usually 1 time only.
      * @param l The string, minus the header.
      */
    public void parseHudInfoSGI(String l)
    {
        /* 	#HUD:<key>:SGI# TC,RF,NM,WS,RS,BS,VS,TF,HS,AT
           TC: unit type character
         RF: string, unit referece
         NM: string, unit name
         WS: speed, unit max walking/cruise speed
         RS: speed, unit max running/flank speed
         BS: speed, unit max reverse speed
         VS: speed, unit max vertical speed
         TF: fuel, or '-' for n/a
         HS: integer, number of templated (single) heatsinks
         AT: advtech, advanced technology available
         */
         /* #HUD:bajl:SGI:R# i,ObservationVTO,ObservationVTOL,0.000,0.000,-0.000,0.000,-,10, */

        StringTokenizer st = new StringTokenizer(l, ",");
        MUMyInfo		info = data.myUnit;
        String			tempStr;

        if (info == null)
            info = new MUMyInfo();

        info.type = st.nextToken();
        info.ref = st.nextToken();
        info.name = st.nextToken();

        info.walkSpeed = Float.parseFloat(st.nextToken());
        info.runSpeed = Float.parseFloat(st.nextToken());
        info.backSpeed = Float.parseFloat(st.nextToken());
        info.verticalSpeed = Float.parseFloat(st.nextToken());

        tempStr = st.nextToken();
        if (!tempStr.equals("-"))
            info.fuel = Integer.parseInt(tempStr);

        info.heatSinks = Integer.parseInt(st.nextToken());

        if (st.hasMoreTokens())
            info.advTech = st.nextToken();
        
    }
    
    /**
      * Parse a string which represents a single contact.
      * @param l The contact information string, minus the header.
      */
    public void parseHudInfoC(String l)
    {
        if (l.equals("Done"))
            return;

        try
        {
            
            StringTokenizer st = new StringTokenizer(l, ",");
            MUUnitInfo		con = new MUUnitInfo();
            String			tempString;
    
            // See hudinfospec.txt for detailed formatting information
            
            con.id = st.nextToken();

            con.friend = Character.isLowerCase(con.id.charAt(0));
            con.target = false;		// no way of knowing for now
    
            con.arc = st.nextToken();
            tempString = st.nextToken();
            if (tempString.equals("PS"))
            {
                con.primarySensor = true;
                con.secondarySensor = true;
            }
            else if (tempString.equals("P"))
            {
                con.primarySensor = true;
                con.secondarySensor = false;
            }
            else if (tempString.equals("S"))
            {
                con.primarySensor = false;
                con.secondarySensor = true;
            }
            else
            {
                con.primarySensor = false;
                con.secondarySensor = false;
            }

            // If both primary and secondary sensor are false, then the token we were just looking at is actually the type
            if (!con.primarySensor && !con.secondarySensor)
                con.type = tempString;
            else
                con.type = st.nextToken();

            con.name = st.nextToken();
            if (con.name == "-")
                con.name = "Unknown";
            // need to split name up into name, team
            
            con.x = Integer.parseInt(st.nextToken());
            con.y = Integer.parseInt(st.nextToken());
            con.z = Integer.parseInt(st.nextToken());
    
            con.range = Float.parseFloat(st.nextToken());
            con.bearing = Integer.parseInt(st.nextToken());
            con.speed = Float.parseFloat(st.nextToken());
            con.verticalSpeed = Float.parseFloat(st.nextToken());
            con.heading = Integer.parseInt(st.nextToken());
    
            tempString = st.nextToken();
            if (!tempString.equals("-"))
            {
                con.jumpHeading = Integer.parseInt(tempString);
                con.jumping = true;
            }
            else
            {
                con.jumpHeading = 0;
                con.jumping = false;
            }
    
            con.rangeToCenter = Float.parseFloat(st.nextToken());
            con.bearingToCenter = Integer.parseInt(st.nextToken());
    
            con.weight = Integer.parseInt(st.nextToken());
    
            con.apparentHeat = Integer.parseInt(st.nextToken());
    
            if (st.hasMoreTokens())
                con.status = st.nextToken();
            else
                con.status = "";
            
            // Give our new contact info to the data object
            data.newContact(con);
        }
        catch (Exception e)
        {
            System.out.println("Error: parseHudInfoC: " + e);
        }
    }

    protected int	tacSX, tacSY, tacEX, tacEY;

    /**
     * Parse a string which represents tactical information. (TS = tactical start)
     * @param l A single line of the tactical info.
     */    
    public void parseHudInfoTS(String l)
    {
        StringTokenizer st = new StringTokenizer(l, ",");
        
        tacSX = Integer.parseInt(st.nextToken());
        tacSY = Integer.parseInt(st.nextToken());
        tacEX = Integer.parseInt(st.nextToken());
        tacEY = Integer.parseInt(st.nextToken());
    }

    /**
      * Parse a string which represents tactical information. (TD = tactical done)
      * @param l A single line of the tactical info.
      */
    public void parseHudInfoTD(String l)
    {
        data.setTerrainChanged(true);
    }
    
    /**
     * Parse a string which represents armor status.
     * @param l A single line of the armor status.
     */
    public void parseHudInfoAS(String l)
    {
        StringTokenizer st = new StringTokenizer(l, ",");
        MUMyInfo		info = data.myUnit;

        if (info == null)
            info = new MUMyInfo();

        String			location = st.nextToken();
        String			f, i, r;
        
        if (location.equals("Done"))
            return;

        // Get the values
        f = st.nextToken(); r = st.nextToken(); i = st.nextToken();

        // Then stick them into the section
        if (!f.equals("-"))
            info.armor[info.indexForSection(location)].f = Integer.parseInt(f);

        if (!r.equals("-"))
            info.armor[info.indexForSection(location)].r = Integer.parseInt(r);

        if (!i.equals("-"))
            info.armor[info.indexForSection(location)].i = Integer.parseInt(i);
    }


    /**
     * Parse a string which represents original armor status.
     * @param l A single line of the original armor status.
     */
    public void parseHudInfoOAS(String l)
    {
        StringTokenizer st = new StringTokenizer(l, ",");
        MUMyInfo		info = data.myUnit;

        if (info == null)
            info = new MUMyInfo();

        String			location = st.nextToken();
        String			f, i, r;

        if (location.equals("Done"))
            return;

        // Get the values
        f = st.nextToken(); r = st.nextToken();  i = st.nextToken();

        // Then stick them into the section
        if (!f.equals("-"))
            info.armor[info.indexForSection(location)].of = Integer.parseInt(f);
        else
            info.armor[info.indexForSection(location)].of = 0;

        if (!r.equals("-"))
            info.armor[info.indexForSection(location)].or = Integer.parseInt(r);
        else
            info.armor[info.indexForSection(location)].or = 0;

        if (!i.equals("-"))
            info.armor[info.indexForSection(location)].oi = Integer.parseInt(i);
        else
            info.armor[info.indexForSection(location)].oi = 0;
        
    }
    
    /**
     * Parse a string which represents tactical information. (TL = tactical line)
     * @param l A single line of the tactical info.
     */
    public void parseHudInfoTL(String l)
    {
        // See hudinfospec.txt for complete format explanation

        // Ok it must be a data line
        StringTokenizer st = new StringTokenizer(l, ",");
        int				thisY = Integer.parseInt(st.nextToken());
        String			tacData = st.nextToken();

        // Format: TerrElevTerrElevTerrElev...
        for (int i = 0; i <= tacEX - tacSX; i++)
        {
            char		terrTypeChar = tacData.charAt(2 * i);
            char		terrElevChar = tacData.charAt(2 * i + 1);		//tacData.substring(2*i+1, 2*i+2);
            int			terrElev;
            
            if (Character.isDigit(terrElevChar))
                terrElev = Character.digit(terrElevChar, 10);		// Get the actual integer value, in base 10
            else
                terrElev = 0;										// Elev was probably a ? (ie, underground map)

            // Water is negative elevation
            if (terrTypeChar == '~' && terrElev != 0)
                terrElev = -terrElev;
            
            data.setHex(tacSX + i, thisY, terrTypeChar, terrElev);
        }
    }

    /**
     * Parse a string which represents a weapon information string (a specific weapon on our own unit)
     * @param l A single line of the weapon info.
     */
    public void parseHudInfoWE(String l)
    {
        if (l.equals("Done"))
            return;

        StringTokenizer st = new StringTokenizer(l, ",");
        MUUnitWeapon	w = new MUUnitWeapon();

        w.number = Integer.parseInt(st.nextToken());
        w.typeNumber = Integer.parseInt(st.nextToken());
        w.quality = Integer.parseInt(st.nextToken());
        w.loc = st.nextToken();

        // Skip status, firemode, ammo type
        data.myUnit.newUnitWeapon(w);
    }

         /**
         * Parse a string which represents a weapon information string (not a particular unit's weapon)
     * @param l A single line of the weapon info.
     */
    public void parseHudInfoWL(String l)
    {
        // See hudinfospec.txt for complete format explanation
        
        if (l.equals("Done"))
            return;
        
        StringTokenizer	st = new StringTokenizer(l, ",");
        MUWeapon		w = new MUWeapon();

        // I'm not sure if the HUD will return -1 or - for invalid (ie underwater LRMs). It looks as if -1 at the moment, but the spec says -
        w.typeNumber = Integer.parseInt(st.nextToken());
        w.name = st.nextToken();
        w.minRange = Integer.parseInt(st.nextToken());
        w.shortRange = Integer.parseInt(st.nextToken());
        w.medRange = Integer.parseInt(st.nextToken());
        w.longRange = Integer.parseInt(st.nextToken());
        w.minRangeWater = Integer.parseInt(st.nextToken());
        w.shortRangeWater = Integer.parseInt(st.nextToken());
        w.medRangeWater = Integer.parseInt(st.nextToken());
        w.longRangeWater = Integer.parseInt(st.nextToken());
        w.criticalSize = Integer.parseInt(st.nextToken());
        w.weight = Integer.parseInt(st.nextToken());
        w.damage = Integer.parseInt(st.nextToken());
        w.recycle = Integer.parseInt(st.nextToken());
        // Skip these 
        //w.fireModes = st.nextToken();
        //w.ammoModes = st.nextToken();
        //w.damageType = st.nextToken();

        MUUnitInfo.newWeapon(w);
    }

    // --------------------------------------------

    public void run()
    {
        while (go)
        {
            String			l = null;
            
            synchronized (queue)
            {
                if (queue.size() != 0)
                {
                    // Parse our line, then remove it from the queue
                    l = (String) queue.getFirst();
                    queue.removeFirst();
                }
            }

            if (l != null)
                parseLine(l);
        }
    }

    /**
     * Start the MUParse thread
     */
    public void start()
    {
        if (parseThread == null)
        {
            parseThread = new Thread(this, "MUParse");
            parseThread.start();
        }
    }
    
    public void pleaseStop()
    {
        go = false;
    }
}
