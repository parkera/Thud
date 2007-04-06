//
//  MUParse.java
//  Thud
//
//  Created by asp on Mon Nov 19 2001.
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine;

import net.sourceforge.btthud.data.*;

import javax.swing.*;

import java.util.*;

import net.sourceforge.btthud.util.*;

/* Note: We use a lot of == comparison in this class, because it's faster than .equals(). When coding here, we have to make sure to intern() the temporary strings so they compare properly.
*/

public class MUParse {

    // Variables
    JTextPane				textPane = null;
    MUData					data = null;
    BulkStyledDocument		doc = null;
    MUPrefs					prefs = null;
    MUCommands				commands = null;

    String					sessionKey;
    String					hudInfoStart = new String("#HUD:");

    // ---------------------
    
    /**
     * Constructor
     */
    public MUParse(JTextPane textPane, MUData data, BulkStyledDocument doc, MUPrefs prefs)
    {
        // Init here
        this.textPane = textPane;
        this.data = data;
        this.doc = doc;
        this.prefs = prefs;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String newKey) {
        sessionKey = newKey;
        hudInfoStart = new String("#HUD:" + sessionKey + ":").intern();
    }

    public void setCommands(MUCommands commands)
    {
        this.commands = commands;
    }

    // -------------------------------------------------------
    
	/**
	 * Check to see if a line needs to be matched, then insert it into the
	 * document.
	 *
	 * @param l The line we are parsing
	 */
	void parseLine (final String l) {
		if (l == null)
			return;

		try {
			final boolean matched = matchHudInfoCommand(l);

			matchForCommandSending(l);

			if (!matched && !data.mainWindowMuted) {
				if (l.length() == 0)
					doc.insertNewLine();
				// TODO: Why would we insert a blank line?

				doc.insertParsedString(doc.parseString(l));
				textPane.setCaretPosition(doc.getLength());
			}
		} catch (final Exception e) {
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

    public boolean matchForCommandSending(String l)
    {
        if (l.startsWith("Pos changed to ") && data.hudRunning)
            commands.forceTactical();

        return false;
    }

    /* Warning: This code relies on major version 0 of the hudinfo spec, and a minimum of minor version 6 */
    public boolean matchHudInfoCommand(String l)
    {
        if (l.startsWith(hudInfoStart))
        {
            synchronized (data)
            {
                // We got some data!
                data.lastDataTime = System.currentTimeMillis();
                
                // Must be a result for us to parse
                StringTokenizer st = new StringTokenizer(l);
                // Get the first word, ie #HUD:key:GS:R#
                String			firstWord = st.nextToken();
                // And get the part which specifies which command we're looking at
                StringTokenizer st2 = new StringTokenizer(firstWord, ":");
                // Skip the #HUD and key
                st2.nextToken(); st2.nextToken();

                String			whichCommand = st2.nextToken().intern();

                // Get the rest of our string, for passing to other functions
                StringBuffer	restOfCommandBuf = new StringBuffer(st.nextToken());
                while (st.hasMoreTokens())
                {
                    restOfCommandBuf.append(" ");
                    restOfCommandBuf.append(st.nextToken());
                }

                String			restOfCommand = restOfCommandBuf.toString().intern();
                                
                if (data.hudStarted && data.hudRunning && (whichCommand == "???" || restOfCommand == "Not in a BattleTech unit"))
                {
                    data.hudRunning = false;                    
                    messageLine("*** Display suspended: " + restOfCommand + " ****");                	                	
                }
                
                if(data.hudStarted && data.hudRunning && (restOfCommand == "You are destroyed!")) {
                	data.hudRunning = false;
                	data.hudStarted = false;
                	commands.endTimers();
                	messageLine("*** Display Stopped: Unit Destroyed ***");
                }
                
                // Now we check it against everything
                if (whichCommand == "GS")		// general status
                    parseHudInfoGS(restOfCommand);
                else if (whichCommand == "C")	// contacts
                    parseHudInfoC(restOfCommand);
                else if (whichCommand == "CB")	// building contacts
                    parseHudInfoCB(restOfCommand);
                else if (whichCommand == "T")	// tactical
                {
                    // Now we're expecting an 'S', an 'L', or a 'D'
                    String subCommand = st2.nextToken().intern();
                    if (subCommand == "S#")
                        parseHudInfoTS(restOfCommand);
                    else if (subCommand == "L#")
                        parseHudInfoTL(restOfCommand);
                    else if (subCommand == "D#")
                        parseHudInfoTD(restOfCommand);
                }
                else if (whichCommand == "SGI")	// static general information
                    parseHudInfoSGI(restOfCommand);
                else if (whichCommand == "CO")
                	parseHudInfoCO(restOfCommand);
                else if (whichCommand == "AS")		// Armor status
                    parseHudInfoAS(restOfCommand);
                else if (whichCommand == "OAS")	// Original armor status
                    parseHudInfoOAS(restOfCommand);
                else if (whichCommand == "KEY")	// 'Key set' .. don't need to do anything further
                    return true;
                else if (whichCommand == "WL")		// Weapon list
                    parseHudInfoWL(restOfCommand);
                else if (whichCommand == "WE")		// Our own weapons
                    parseHudInfoWE(restOfCommand);
                else if (whichCommand == "AM")		// Our own ammo
                	parseHudInfoAM(restOfCommand);
                else
                    messageLine("> Unrecognized HUDINFO data: " + whichCommand);

                return true;
            }
        }
        else if (l.startsWith("#HUD hudinfo"))
        {
            parseHudInfoVersion(l);
            return true;
        }
        else if (l.startsWith("#HUD:"))
        {
            // This could be reached if the key didn't match or something.. we'll just keep it from showing up
            return true;
        }

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
        data.setHudInfoMajorVersion(Integer.parseInt(st2.nextToken()));
        data.setHudInfoMinorVersion(Integer.parseInt(st2.nextToken()));
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
            
            final int hex_x = Integer.parseInt(st.nextToken());
            final int hex_y = Integer.parseInt(st.nextToken());
            final int hex_z = Integer.parseInt(st.nextToken());
    
            info.heading = Integer.parseInt(st.nextToken());
            info.desiredHeading = Integer.parseInt(st.nextToken());
            info.speed = Float.parseFloat(st.nextToken());
            info.desiredSpeed = Float.parseFloat(st.nextToken());
    
            info.heat = Integer.parseInt(st.nextToken());
            info.heatDissipation = Integer.parseInt(st.nextToken());
    
            tempStr = st.nextToken().intern();
            if (tempStr != "-")
                info.fuel = Integer.parseInt(tempStr);
    
            info.verticalSpeed = Float.parseFloat(st.nextToken());
            info.desiredVerticalSpeed = Float.parseFloat(st.nextToken());
    
            final float rtc = Float.parseFloat(st.nextToken());
            final int ibtc = Integer.parseInt(st.nextToken());

            info.position.setFromCenterLocation(hex_x, hex_y, hex_z, rtc, ibtc);

            tempStr = st.nextToken().intern();
            if (tempStr != "-")
                info.turretHeading = Integer.parseInt(tempStr);			// also corresponds to rottorso
    
            if (st.hasMoreTokens())
                info.status = st.nextToken();
            else
                info.status = "";
            
            if (data.hiSupportsOwnJumpInfo())
            {
                // We have our jump target code now
                tempStr = st.nextToken().intern();
                if (tempStr != "-")
                {
                    info.jumping = true;
                    info.jumpTargetX = Integer.parseInt(tempStr);
                    info.jumpTargetY = Integer.parseInt(st.nextToken());
                }
            }
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

        if (info.type.equals("I"))
            prefs.hudinfoTacHeight = 5;		// Because MechWarriors with long tacticals slow down the MUX
        else
            prefs.hudinfoTacHeight = 40;
        
        if(st.hasMoreTokens()) {
        	String ref = st.nextToken();

            if(ref.compareToIgnoreCase(info.ref) != 0 && info.ref.length() > 1) {
            	// New ref = new unit.
            	messageLine("*** Unit Change Detected - Refreshing Data ***");
            	data.clearData();            	
            }
            info.ref = ref;
            info.name = st.nextToken();

            info.walkSpeed = Float.parseFloat(st.nextToken());
            info.runSpeed = Float.parseFloat(st.nextToken());
            info.backSpeed = Float.parseFloat(st.nextToken());
            info.maxVerticalSpeed = Float.parseFloat(st.nextToken());

            tempStr = st.nextToken().intern();
            if (tempStr != "-")
                info.maxFuel = Integer.parseInt(tempStr);

            info.heatSinks = Integer.parseInt(st.nextToken());

            if (st.hasMoreTokens())
                info.advTech = st.nextToken();
            
            /* Since we were able to succesfully do a sgi, let's see if we the hud was suspended.
             * If so, restart
             */                        
            if(data.hudRunning == false) {
            	messageLine("*** Display Resumed ***");
            	data.hudRunning = true;
            	commands.forceTactical();
            	data.lastDataTime = 0;
            }
        } 
    }
    
    /**
     * Parse a string which represents conditions/weather information
     * @param l The string, minus the header.
     */
   public void parseHudInfoCO(String l)
   {
       /* 	#HUD:<key>:CO:R# LT,VR,GR,TP,FL
	   LT: light type
	   VR: range, visibility range
	   GR: integer, gravity in 100th G's
	   TP: heatmeasure, ambient temperature
	   FL: map condition flags
	   light type: One of the following:
	    'D': Daylight
	    'T': Twilight
	    'N': Night
	   map condition flags: '-', or at least one of the following:
	    'V': Map is Vacuum
	    'U': Map is Underground (no jumping, VTOLs)
	    'D': Map is Dark (no tactical beyond sensors)	    
        */        

       StringTokenizer st = new StringTokenizer(l, ",");
       MUWeather weather = data.weather;
       String			tempStr;

       if (weather == null)
           weather = new MUWeather();

       tempStr = st.nextToken().intern();

       if (tempStr == "D")
    	   weather.light = MUWeather.LIGHT_DAY;
       else if(tempStr == "T")
    	   weather.light = MUWeather.LIGHT_DAWN_DUSK;
       else if(tempStr == "N")
    	   weather.light = MUWeather.LIGHT_NIGHT;   	   
       
       weather.visibility = Integer.parseInt(st.nextToken());
       
       weather.gravity = Integer.parseInt(st.nextToken());
       
       weather.ambientTemperature = Integer.parseInt(st.nextToken());
       
       tempStr = st.nextToken();
       
       if(tempStr.contains("V"))
    	   weather.isVacuum = true;
       if(tempStr.contains("U"))
    	   weather.isUnderground = true;
       if(tempStr.contains("D"))
    	   weather.isDark = true;       
   }
   
   /**
      * Parse a string which represents a single contact.
      * @param l The contact information string, minus the header.
      */
    public void parseHudInfoC(String l)
    {
        if (l == "Done")
            return;

        try
        {
            
            StringTokenizer st = new StringTokenizer(l, ",");
            MUUnitInfo		con = new MUUnitInfo(prefs);
            String			tempStr;
    
            // See hudinfospec.txt for detailed formatting information
            // #HUD:asdfk:C:L# DH,v,-,B,CG Ostroc,182,105,1,120.031,358,86.000,0.000,240,-,0.040,60,60,0,-
            
            con.id = st.nextToken();

            con.friend = Character.isLowerCase(con.id.charAt(0));
            con.target = false;		// no way of knowing for now
    
            con.arc = st.nextToken();
            tempStr = st.nextToken().intern();
            if (tempStr == "PS")
            {
                con.primarySensor = true;
                con.secondarySensor = true;
            }
            else if (tempStr == "P")
            {
                con.primarySensor = true;
                con.secondarySensor = false;
            }
            else if (tempStr == "S")
            {
                con.primarySensor = false;
                con.secondarySensor = true;
            }
            else
            {
                con.primarySensor = false;
                con.secondarySensor = false;
            }

            // Version 0.7 and above doesn't let us return a nil argument
            if (data.hiSupportsAllArgumentHudinfo())
                con.type = st.nextToken();
            else
            {
                if (!con.primarySensor && !con.secondarySensor)
                {
                    // If both primary and secondary sensor are false, then the token we were just looking at is actually the type
                    if (!con.primarySensor && !con.secondarySensor)
                        con.type = tempStr;
                    else
                        con.type = st.nextToken();
                }
            }

            con.name = st.nextToken().intern();
            if (con.name == "-")
                con.name = "Unknown";
            // need to split name up into name, team
            
            final int hex_x = Integer.parseInt(st.nextToken());
            final int hex_y = Integer.parseInt(st.nextToken());
            final int hex_z = Integer.parseInt(st.nextToken());
    
            con.range = Float.parseFloat(st.nextToken());
            con.bearing = Integer.parseInt(st.nextToken());
            con.speed = Float.parseFloat(st.nextToken());
            con.verticalSpeed = Float.parseFloat(st.nextToken());
            con.heading = Integer.parseInt(st.nextToken());
    
            tempStr = st.nextToken().intern();
            if (tempStr != "-")
            {
                con.jumpHeading = Integer.parseInt(tempStr);
                con.jumping = true;
            }
            else
            {
                con.jumpHeading = 0;
                con.jumping = false;
            }
    
            final float rtc = Float.parseFloat(st.nextToken());
            final int ibtc = Integer.parseInt(st.nextToken());

            con.position.setFromCenterLocation(hex_x, hex_y, hex_z, rtc, ibtc);
    
            con.weight = Integer.parseInt(st.nextToken());
    
            con.apparentHeat = Integer.parseInt(st.nextToken());

            if (data.hiSupportsAllArgumentHudinfo())
            {
                tempStr = st.nextToken();
                if (tempStr.equals("-"))
                    con.status = "";
                else
                    con.status = new String(tempStr);
            }
            else
            {
                if (st.hasMoreTokens())
                    con.status = st.nextToken();
                else
                    con.status = "";                
            }
            
            // Give our new contact info to the data object
            data.newContact(con);
        }
        catch (Exception e)
        {
            System.out.println("Error: parseHudInfoC: " + e);
        }
    }
/*
 f. Building Contacts

 command:
	hudinfo cb

 response:
 Zero or more:
#HUD:<key>:CB:L# AC,BN,X,Y,Z,RN,BR,CF,MCF,BS
 Exactly once:
#HUD:<KEY>:CB:D# Done

 AC: arc, weapon arc the building is in
 BN: string, name of the building, or '-' if unknown
 X, Y, Z: coordinates of building
 RN: range, range to building
 BR: degree, bearing to building
 CF: integer, current construction factor of building
 MCF: integer, maximum construction factor of building
 BS: building status string

 Example:
 > hudinfo cb
 < #HUD:C58x2:CB:L# *,Underground Hangar,55,66,7,25.1,180,1875,2000,X
 < #HUD:C58x2:CB:D# Done
 */
    public void parseHudInfoCB(String l)
    {
        if (l == "Done")
            return;

        try
        {
            StringTokenizer st = new StringTokenizer(l, ",");
            MUBuildingInfo	building = new MUBuildingInfo();

            building.type = "i";			// installation type
            building.friend = true;			// All buildings are friendly and inviting. :)
            
            building.arc = st.nextToken();
            building.name = st.nextToken();

            final int hex_x = Integer.parseInt(st.nextToken());
            final int hex_y = Integer.parseInt(st.nextToken());
            final int hex_z = Integer.parseInt(st.nextToken());

            final float rtc = Float.parseFloat(st.nextToken());
            final int ibtc = Integer.parseInt(st.nextToken());

            building.position.setFromCenterLocation(hex_x, hex_y, hex_z, rtc, ibtc);

            building.cf = Integer.parseInt(st.nextToken());
            building.maxCf = Integer.parseInt(st.nextToken());

            if (st.hasMoreTokens())
                building.status = st.nextToken();

            // We don't have any way to uniquely id a building, so we'll just stick with the name and coords for now
            building.id = building.name + building.position.getHexX() + building.position.getHexY();
            
            data.newContact(building);
            
        }
        catch (Exception e)
        {
            System.out.println("Error: parseHudInfoCB: " + e);
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

        if (data.hiSupportsExtendedMapInfo())
        {
            // We have some more info about our map
            data.mapId = st.nextToken();            
            data.mapName = st.nextToken();
            data.mapVersion = st.nextToken();

            // Is this a LOS-only map? 
            if (st.hasMoreTokens())
                data.mapLOSOnly = st.nextToken().equals("l");
        }              
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

        String			location = st.nextToken().intern();
        String			f, i, r;
        
        if (location == "Done")
            return;

        // Get the values
        f = st.nextToken().intern(); r = st.nextToken().intern(); i = st.nextToken().intern();

        // Then stick them into the section
        if (f != "-")
            info.armor[MUUnitInfo.indexForSection(location)].f = Integer.parseInt(f);

        if (r != "-")
            info.armor[MUUnitInfo.indexForSection(location)].r = Integer.parseInt(r);

        if (i != "-")
            info.armor[MUUnitInfo.indexForSection(location)].i = Integer.parseInt(i);
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

        String			location = st.nextToken().intern();
        String			f, i, r;

        if (location == "Done")
            return;

        // Get the values
        f = st.nextToken().intern(); r = st.nextToken().intern();  i = st.nextToken().intern();

        // Then stick them into the section
        if (f != "-")
            info.armor[MUUnitInfo.indexForSection(location)].of = Integer.parseInt(f);
        else
            info.armor[MUUnitInfo.indexForSection(location)].of = 0;

        if (r != "-")
            info.armor[MUUnitInfo.indexForSection(location)].or = Integer.parseInt(r);
        else
            info.armor[MUUnitInfo.indexForSection(location)].or = 0;

        if (i != "-")
            info.armor[MUUnitInfo.indexForSection(location)].oi = Integer.parseInt(i);
        else
            info.armor[MUUnitInfo.indexForSection(location)].oi = 0;
        
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

            // Water and ice are negative elevation
            if ((terrTypeChar == '~' || terrTypeChar == '-') && terrElev != 0)
                terrElev = -terrElev;
            
            if (terrTypeChar != '?')
                data.setHex(tacSX + i, thisY, terrTypeChar, terrElev);
            else { // ? in the terrain = LOS info.
            	String hashkey = String.valueOf(tacSX + i) + " " + String.valueOf(thisY);
            	data.LOSinfo.put(hashkey, (Boolean)false);
            }
        }
    }

    /**
     * Parse a string which represents a weapon information string (a specific weapon on our own unit)
     * @param l A single line of the weapon info.
     */
    public void parseHudInfoWE(String l)
    {
        if (l == "Done")
            return;

        StringTokenizer st = new StringTokenizer(l, ",");
        MUUnitWeapon	w = new MUUnitWeapon();

        w.number = Integer.parseInt(st.nextToken());
        w.typeNumber = Integer.parseInt(st.nextToken());
        w.quality = Integer.parseInt(st.nextToken());
        w.loc = st.nextToken();
        w.status = st.nextToken();
        w.fireMode = st.nextToken();
        w.ammoType = st.nextToken();

        data.myUnit.newUnitWeapon(w);
    }
    
    /**
     * Parse a string which represents our unit's ammo status
     * @param l A single line of the ammo info.
     */
    public void parseHudInfoAM(String l)
    {
        if (l == "Done")
            return;

        StringTokenizer st = new StringTokenizer(l, ",");
        MUUnitAmmo	a = new MUUnitAmmo();

        a.number = Integer.parseInt(st.nextToken());
        a.weaponTypeNumber = Integer.parseInt(st.nextToken());
        a.ammoMode = st.nextToken();
        a.roundsRemaining = Integer.parseInt(st.nextToken());
        a.roundsOriginal = Integer.parseInt(st.nextToken());

        data.myUnit.newUnitAmmo(a);
    }

   /**
     * Parse a string which represents a weapon information string (not a particular unit's weapon)
     * @param l A single line of the weapon info.
     */
    public void parseHudInfoWL(String l)
    {
        // See hudinfospec.txt for complete format explanation
        
        if (l == "Done")
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

        if (data.hiSupportsWLHeatInfo())
        {
            // Get the last data items - supported in minor version 7 and above
            w.fireModes = st.nextToken();
            w.ammoModes = st.nextToken();
            w.damageType = st.nextToken();
            w.heat = Integer.parseInt(st.nextToken());
        }

        MUUnitInfo.newWeapon(w);
    }
}
