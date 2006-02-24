//
//  MUUnitInfo.java
//  Thud
//
//  Created by asp on Tue Nov 20 2001.
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.data;

import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;

/**
 * This class is for storing all the information about a single unit (typically a contact, enemy or friendly).
 *
 * @author Anthony Parker
 * @version 1.0, 11.20.01
 */
 
public class MUUnitInfo extends Object implements Comparable {

    public boolean		friend = false;
    public boolean		target = false;
    
    public String		type = null;
    public String 		team = null;
    public String		id = null;
    public String		name = " ";
    public String		arc = null;
    public String		status = " ";
    public float		range, speed, verticalSpeed;
    public int			x, y, z;
    public int			heading, desiredHeading, bearing, jumpHeading;
    public int			maxFuel;
    public int			turretHeading;
    public float		rangeToCenter;
    public int			bearingToCenter;

    public int			weight;
    public int			apparentHeat;
    
    public boolean		jumping = false;
    
    public boolean		primarySensor = false, secondarySensor = false;

    // After 'cyclesLeft - isOldAt' cycles they will turn grey
    // After 'cyclesLeft' cycles, they will disappear from the contacts screen
    // These are default values, overriden from prefs by constructor
    private int			cyclesLeft = 30;
    private int			isOldAt = 27;

    static public MUWeapon		weapons[] = new MUWeapon[200];		// data that stores info on /all/ weapons ... assume 200 of them for now
    
    Font				font = new Font("Monospaced", Font.BOLD, 10);	// used for drawing armor
    FontRenderContext	frc = new FontRenderContext(new AffineTransform(), true, true);

    // ------------------
    
    static final int	NO_SECTION = 0;
    static final int	A = 1;
    static final int	AS = 2;
    static final int	C = 3;
    static final int	CT = 4;
    static final int	CTr = 5;
    static final int	E = 6;
    static final int	F = 7;
    static final int	FLLr = 8;
    static final int	FLS = 9;
    static final int	FRLr = 10;
    static final int	FRS = 11;
    static final int	FS = 12;
    static final int	H = 13;
    static final int	Hr = 14;
    static final int	LA = 15;
    static final int	LAr = 16;
    static final int	LL = 17;
    static final int	LLr = 18;
    static final int	LRW = 19;
    static final int	LS = 20;
    static final int	LT = 21;
    static final int	LTr = 22;
    static final int	LW = 23;
    static final int	N = 24;
    static final int	R = 25;
    static final int	RA = 26;
    static final int	RAr = 27;
    static final int	RL = 28;
    static final int	RLr = 29;
    static final int	RLS = 30;
    static final int	RRS = 31;
    static final int	RRW = 32;
    static final int	RS = 33;
    static final int	RT = 34;
    static final int	RTr = 35;
    static final int	RW = 36;
    static final int	S1 = 37;
    static final int	S2 = 38;
    static final int	S3 = 39;
    static final int	S4 = 40;
    static final int	S5 = 41;
    static final int	S6 = 42;
    static final int	S7 = 43;
    static final int	S8 = 44;
    static final int	T = 45;
    static final int	FLL = 46;
    static final int	FRL = 47;
    static final int	RLL = 48;
    static final int	RRL = 49;

    static final String	sectionNames[] = {
        "NO", "A", "AS", "C", "CT", "CTr", "E", "F", "FLLr", "FLS", "FLRr",
        "FRS", "FS", "H", "Hr", "LA", "LAr", "LL", "LLr", "LRW", "LS",
        "LT", "LTr", "LW", "N", "R", "RA", "RAr", "RL", "RLr", "RLS",
        "RRS", "RRW", "RS", "RT", "RTr", "RW", "S1", "S2", "S3", "S4",
        "S5", "S6", "S7", "S8", "T", "FLL", "FRL", "RLL", "RRL"};

    static final int	TOTAL_SECTIONS = 50;

    static final int	TYPE_UNKNOWN = 0;
    static final int	BIPED = 1;
    static final int	HOVER = 2;
    static final int	TRACKED = 3;
    static final int	WHEELED = 4;
    static final int	NAVAL_SURFACE = 5;
    static final int	NAVAL_HYDROFOIL = 6;
    static final int	NAVAL_SUBMARINE = 7;
    static final int	VTOL = 8;
    static final int	AEROFIGHTER = 9;
    static final int	AERODYNE_DS = 10;
    static final int	SPHEROID_DS = 11;
    static final int	BATTLESUIT = 12;
    static final int	INFANTRY = 13;
    static final int	INSTALLATION = 14;

    static final int	TOTAL_MOVETYPES = 15;

    // --------------------------

    public MUUnitInfo()
    {
        turretHeading = 180;                
    }
    
    public MUUnitInfo(MUPrefs prefs) {
        cyclesLeft = prefs.contactsAge;
        isOldAt = cyclesLeft - 3;
    }

    // For debugging purposes only
    public String toString()
    {
        String	out = "\nMUUnitInfo: ";
        
        return (out + "ID:" + id + " Team:" + team + " Type:" + type + " Name:" + name +
                " Range:" + String.valueOf(range) + " Speed:" + String.valueOf(speed) +
                " XYZ:" + String.valueOf(x) + "," + String.valueOf(y) + "," + String.valueOf(z) +
                " Heading:" + String.valueOf(heading) + " Bearing:" + String.valueOf(bearing) +
                " RTC:" + String.valueOf(rangeToCenter) + " BTC:" + String.valueOf(bearingToCenter) +
                " Friend:" + String.valueOf(friend));
    }

    public boolean isTank()
    {
        if (type.equals("H") || type.equals("T") || type.equals("W") ||
            type.equals("N") || type.equals("Y") || type.equals("U"))
            return true;
        else
            return false;
    }
    
    public boolean isMech()
    {
        if (type.equals("B") || type.equals("Q"))
            return true;
        else
            return false;
    }

    public boolean isAero()
    {
        if (type.equals("F"))
            return true;
        else
            return false;
    }
    
    public boolean isFlying() {
    	if(type.equals("V") ||
    	   type.equals("F") ||
    	   type.equals("A") ||
    	   type.equals("D")) {
    		return true;
    	} else {
    		return false;
    	}
    }

    public boolean hasHeat()
    {
        if (isMech() || isAero())
            return true;
        else
            return false;
    }

    public boolean isExpired()
    {
        if (cyclesLeft <= 0)
            return true;
        else
            return false;
    }

    public boolean isOld()
    {
        if (cyclesLeft <= isOldAt)
            return true;
        else
            return false;
    }

    public boolean isFriend()
    {
        return friend;
    }

    public boolean isTarget()
    {
        return target;
    }

    public boolean isJumping()
    {
        return jumping;
    }
    
    public void expireMore()
    {
        cyclesLeft--;
    }
    
    /** Returns the cliff differential for this unit. */
    public int cliffDiff() {
    	if(this.isMech()) { 
    		return 2;
    	} else {
    		return 1;
    	}    		
    }
    
    /**
      * Make a human-readable contact string, similar to standard .c for display
      */
    
    public String makeContactString()
    {
        /* Example:
                PSl[cm]B Mi Swayback  x: 40 y: 38 z:  0 r: 6.3 b:169 s:  0.0 h:180 S:F
        */
        StringBuffer	sb = new StringBuffer();

        sb.append(primarySensor ? 'P' : ' ');
        sb.append(secondarySensor ? 'S' : ' ');
        sb.append(arc);
        sb.append('[');
        sb.append(id);
        sb.append(']');
        sb.append(type);
        sb.append(' ');

        sb.append(leftJust(name, 12, true));
        sb.append(' ');

        sb.append('x'); sb.append(':');
        sb.append(rightJust(String.valueOf(x), 3, false));
        sb.append(' ');
        sb.append('y'); sb.append(':');
        sb.append(rightJust(String.valueOf(y), 3, false));
        sb.append(' ');
        sb.append('z'); sb.append(':');
        sb.append(rightJust(String.valueOf(z), 3, false));
        sb.append(' ');

        sb.append('r'); sb.append(':');
        sb.append(rightJust(String.valueOf(range), 4, true));
        sb.append(' ');
        sb.append('b'); sb.append(':');
        sb.append(rightJust(String.valueOf(bearing), 3, false));
        sb.append(' ');

        sb.append('s'); sb.append(':');
        sb.append(rightJust(String.valueOf(speed), 5, true));
        sb.append(' ');
        sb.append('h'); sb.append(':');
        sb.append(rightJust(String.valueOf(heading), 3, false));
        sb.append(' ');
        sb.append('S'); sb.append(':');
        sb.append(status);

        return sb.toString();
    }

    public String leftJust(String l, int w, boolean trunc)
    {
        if (l.length() < w)
        {
            // Need to add spaces to the end
            StringBuffer sb = new StringBuffer(l);

            for (int i = 0; i < w - l.length(); i++)
                sb.append(' ');

            return sb.toString();
        }
        else if (l.length() == w)
        {
            // Leave it the way it is
            return l;
        }
        else
        {
            // Choice here: truncate the string or leave the way it is
            if (trunc)
            {
                return l.substring(0, w);
            }
            else
            {
                return l;   
            }
        }
    }

    public String rightJust(String l, int w, boolean trunc)
    {
        if (l.length() < w)
        {
            // Need to add spaces to the beginning
            StringBuffer sb = new StringBuffer(l);

            for (int i = 0; i < w - l.length(); i++)
                sb.insert(0, ' ');

            return sb.toString();
        }
        else if (l.length() == w)
        {
            // Leave it the way it is
            return l;
        }
        else
        {
            // Choice here: truncate the string or leave the way it is
            if (trunc)
            {
                return l.substring(0, w);
            }
            else
            {
                return l;
            }
        }        
    }

    /** This implements Comparable
      * Right now we sort based on range, for easy contact lists.
      * Destroyed units should come last.
      */
    public int compareTo(Object o2)
    {
        if (type.equals("i"))
            return 1;
        if (isDestroyed())
            return 1;
        
        if (range < ((MUUnitInfo) o2).range)
            return -1;
        else if (range > ((MUUnitInfo) o2).range)
            return 1;
        else
        {
            // We don't want to return 0 unless they are exactly the same unit. Otherwise, it doesn't matter which is first
            return id.compareTo(((MUUnitInfo) o2).id);
        }
    }

    /**
      * Determine if the unit is destroyed or not
      */
    public boolean isDestroyed()
    {
        return stringHasCharacter(status, 'D');
    }

    /**
      * Determine if the unit is fallen or not
      */
    public boolean isFallen()
    {
        return (stringHasCharacter(status, 'F') || stringHasCharacter(status, 'f'));
    }

    /**
      * Determine if a specific character is in this string
      */
    public boolean stringHasCharacter(String s, char c)
    {
        if (s == null)
            return false;
        
        for (int i = 0; i < s.length(); i++)
        {
            if (s.charAt(i) == c)
                return true;
        }

        return false;
    }

    /**
     * Return an efficient hash code
     */
    public int hashCode()
    {
        // Use the hash code for the id
        return id.hashCode();
    }

    /*********************************************************************/
    
    /**
      * Returns a BufferedImage which represents this particular unit.
      * @param h The height of the icon (not neccesarily the height of a hex)
      * @param drawArmor If true, draw any known armor information for this unit
      * @param color What color to draw this unit
      */
    public GeneralPath icon(int h, boolean drawArmor, Color color)
    {

        // We do everything on a scale of 20 pixels. We scale up the path later if the height is > 20.

        //BufferedImage		unitImage = new BufferedImage(h, h, BufferedImage.TYPE_INT_ARGB);
        GeneralPath			unitOutline = new GeneralPath();
        AffineTransform		xform = new AffineTransform();

        /*
        Graphics2D			g = (Graphics2D) unitImage.getGraphics();

        g.setColor(new Color(0, 0, 0, 0));
        g.fill(new Rectangle(0, 0, h, h));
         */
        
        switch (type.charAt(0))
        {
            case 'B':							// Biped
                unitOutline.moveTo(8, 0);
                unitOutline.lineTo(8, 3);
                unitOutline.lineTo(5, 3);
                unitOutline.lineTo(1, 8);
                unitOutline.lineTo(4, 10);
                unitOutline.lineTo(6, 7);
                unitOutline.lineTo(7, 12);
                unitOutline.lineTo(4, 20);
                unitOutline.lineTo(8, 20);
                unitOutline.lineTo(10, 13);
                unitOutline.lineTo(12, 20);
                unitOutline.lineTo(16, 20);
                unitOutline.lineTo(13, 12);
                unitOutline.lineTo(14, 7);
                unitOutline.lineTo(16, 10);
                unitOutline.lineTo(19, 8);
                unitOutline.lineTo(15, 3);
                unitOutline.lineTo(12, 3);
                unitOutline.lineTo(12, 0);
                unitOutline.lineTo(8, 0);
                break;
            case 'Q':
                unitOutline.moveTo(8, 7);
                unitOutline.lineTo(8, 10);
                unitOutline.lineTo(5, 10);
                unitOutline.lineTo(3, 8);
                unitOutline.lineTo(1, 8);
                unitOutline.lineTo(2, 20);
                unitOutline.lineTo(4, 20);
                unitOutline.lineTo(3, 12);
                unitOutline.moveTo(4, 13);
                unitOutline.lineTo(6, 20);
                unitOutline.lineTo(8, 20);
                unitOutline.lineTo(6, 13);
                unitOutline.moveTo(7, 14);
                unitOutline.lineTo(13, 14);
                unitOutline.moveTo(14, 13);
                unitOutline.lineTo(12, 20);
                unitOutline.lineTo(14, 20);
                unitOutline.lineTo(16, 13);
                unitOutline.moveTo(17, 12);
                unitOutline.lineTo(16, 20);
                unitOutline.lineTo(18, 20);
                unitOutline.lineTo(19, 8);
                unitOutline.lineTo(17, 8);
                unitOutline.lineTo(15, 10);
                unitOutline.lineTo(12, 10);
                unitOutline.lineTo(12, 7);
                unitOutline.lineTo(8, 7);
                    
                break;
            case 'H':
            case 'T':
            case 'W':
                unitOutline.moveTo(5, 2);
                unitOutline.lineTo(3, 5);
                unitOutline.lineTo(3, 15);
                unitOutline.lineTo(5, 18);
                unitOutline.lineTo(15, 18);
                unitOutline.lineTo(17, 15);
                unitOutline.lineTo(17, 5);
                unitOutline.lineTo(15, 2);
                unitOutline.lineTo(5, 2);
                // Should check to see if there is a turret here before we go around drawing it
                unitOutline.moveTo(8, 10);
                unitOutline.lineTo(8, 14);
                unitOutline.lineTo(12, 14);
                unitOutline.lineTo(12, 10);
                unitOutline.lineTo(8, 10);
                // Maybe rotate this according to turret heading?
                unitOutline.moveTo((float) 9.5, 10);
                unitOutline.lineTo((float) 9.5, 6);
                unitOutline.lineTo((float) 10.5, 6);
                unitOutline.lineTo((float) 10.5, 10);
                    
                break;
            case 'N':
                break;
            case 'Y':
                break;
            case 'U':
                break;
            case 'V':
                unitOutline.moveTo(8, 2);
                unitOutline.lineTo(7, 3);
                unitOutline.lineTo(7, 10);
                unitOutline.lineTo(9, 13);
                unitOutline.lineTo(9, 18);
                unitOutline.lineTo(8, 18);
                unitOutline.lineTo(8, 19);
                unitOutline.lineTo(12, 19);
                unitOutline.lineTo(12, 18);
                unitOutline.lineTo(11, 18);
                unitOutline.lineTo(11, 13);
                unitOutline.lineTo(13, 10);
                unitOutline.lineTo(13, 3);
                unitOutline.lineTo(12, 2);
                unitOutline.lineTo(8, 2);
                unitOutline.moveTo(2, 6);
                unitOutline.lineTo(2, 7);
                unitOutline.lineTo(9, 7);
                unitOutline.lineTo(9, 8);
                unitOutline.lineTo(11, 8);
                unitOutline.lineTo(11, 7);
                unitOutline.lineTo(18, 7);
                unitOutline.lineTo(18, 6);
                unitOutline.lineTo(11, 6);
                unitOutline.lineTo(11, 5);
                unitOutline.lineTo(9, 5);
                unitOutline.lineTo(9, 6);
                unitOutline.lineTo(2, 6);
                    
                break;
            case 'F':
                break;
            case 'A':
                break;
            case 'D':
                unitOutline.moveTo(5, 1);
                unitOutline.lineTo(1, 5);
                unitOutline.lineTo(1, 15);
                unitOutline.lineTo(5, 19);
                unitOutline.lineTo(15, 19);
                unitOutline.lineTo(19, 15);
                unitOutline.lineTo(19, 5);
                unitOutline.lineTo(15, 1);
                unitOutline.lineTo(5, 1);
                // We scale because dropships are big. :)
                if (!drawArmor)
                    xform.scale(h / 4.0, h / 4.0);
                    
                break;
            case 'S':
                break;
            case 'I':
                break;
            case 'i':
                unitOutline.moveTo(4, 4);
                unitOutline.lineTo(2, 19);
                unitOutline.lineTo(18, 19);
                unitOutline.lineTo(16, 4);
                unitOutline.lineTo(4, 4);
                unitOutline.moveTo(5, 6);
                unitOutline.lineTo(5, 8);
                unitOutline.lineTo(15, 8);
                unitOutline.lineTo(15, 6);
                unitOutline.lineTo(5, 6);
                break;
            default:
                unitOutline.moveTo(7, 7);
                unitOutline.lineTo(7, 13);
                unitOutline.lineTo(13, 13);
                unitOutline.lineTo(13, 7);
                unitOutline.lineTo(7, 7);
                break;
        }

        // Draw the unit
        // only rotate if it's a 'Mech, fallen, and not for the status display
        if (type.charAt(0) == 'B' && isFallen() && !drawArmor)
            xform.rotate(Math.PI / 2, 10, 10);
        xform.scale((float) h / 20.0, (float) h / 20.0);
        unitOutline.transform(xform);

        /*
        g.setColor(color);
        g.setTransform(new AffineTransform());		// reset the transform
        g.draw(unitOutline);
        
        return unitImage;
         */

        return unitOutline;
        
    }

    /**
     * Returns true if this unit has the possibility of having a turret (note: doesn't check to see if it actually does have one)
     */
    public boolean canHaveTurret()
    {
        int		mType = movementForType(type);

        if (mType == HOVER || mType == TRACKED || mType == WHEELED || mType == NAVAL_SURFACE)
            return true;
        else
            return false;
    }

    /***************************************************************************/

    // Static methods

    /**
     * Return a color for displaying an armor percentage (green is minty, etc). 
     */
    static public Color colorForPercent(float p)
    {
        if (p >= 90)
            return new Color(0, 255, 0);			// Bright green
        else if (p > 70)
            return new Color(0, 160, 0);			// Darker green
        else if (p > 45)
            return new Color(255, 255, 0);			// Bright yellow
        else if (p > 1)
            return new Color(160, 0, 0);			// Dark red
        else
            return new Color(128, 128, 128);		// (visible) "Black"
    }

    /**
     * Return a color with Transparency
     */
    static public Color colorForPercent(float p, int a)
    {
        if (p >= 90)
            return new Color(0, 255, 0, a);			// Bright green
        else if (p > 70)
            return new Color(0, 160, 0, a);			// Darker green
        else if (p > 45)
            return new Color(255, 255, 0, a);		// Bright yellow
        else if (p >= 1)
            return new Color(160, 0, 0, a);			// Dark red
        else
            return new Color(128, 128, 128, a);		// (visible) "black"
    }

    /**
     * Return a constant representing our movement type
     */   
    static public int movementForType(String s)
    {
        if (s.equals("B"))
            return BIPED;
        if (s.equals("H"))
            return HOVER;
        if (s.equals("T"))
            return TRACKED;
        if (s.equals("W"))
            return WHEELED;
        if (s.equals("N"))
            return NAVAL_SURFACE;
        if (s.equals("Y"))
            return NAVAL_HYDROFOIL;
        if (s.equals("U"))
            return NAVAL_SUBMARINE;
        if (s.equals("F"))
            return AEROFIGHTER;
        if (s.equals("A"))
            return AERODYNE_DS;
        if (s.equals("D"))
            return SPHEROID_DS;
        if (s.equals("S"))
            return SPHEROID_DS;
        if (s.equals("I"))
            return INFANTRY;
        if (s.equals("i"))
            return INSTALLATION;

        return TYPE_UNKNOWN;
    }
    /**
     * Return the index in an array for a specific section.
     * @param s A string representation of the section we're looking for.
     */
    static public int indexForSection(String sec)
    {
        // I could have assumed the incoming string is intern()ed but I thought it was poor style... so I'll do it here
        String			s = sec.intern();
        
        if (s == "A")
            return A;
        if (s == "AS")
            return AS;
        if (s == "C")
            return C;
        if (s == "CT")
            return CT;
        if (s == "CTr")
            return CTr;
        if (s == "E")
            return E;
        if (s == "F")
            return F;
        if (s == "FLLr")
            return FLLr;
        if (s == "FLS")
            return FLS;
        if (s == "FRLr")
            return FRLr;
        if (s == "FRS")
            return FRS;
        if (s == "FS")
            return FS;
        if (s == "H")
            return H;
        if (s == "Hr")
            return Hr;
        if (s == "LA")
            return LA;
        if (s == "LAr")
            return LAr;
        if (s == "LL")
            return LL;
        if (s == "LLr")
            return LLr;
        if (s == "LRW")
            return LRW;
        if (s == "LS")
            return LS;
        if (s == "LT")
            return LT;
        if (s == "LTr")
            return LTr;
        if (s == "LW")
            return LW;
        if (s == "N")
            return N;
        if (s == "R")
            return R;
        if (s == "RA")
            return RA;
        if (s == "RAr")
            return RAr;
        if (s == "RL")
            return RL;
        if (s == "RLr")
            return RLr;
        if (s == "RLS")
            return RLS;
        if (s == "RRS")
            return RRS;
        if (s == "RRW")
            return RRW;
        if (s == "RS")
            return RS;
        if (s == "RT")
            return RT;
        if (s == "RTr")
            return RTr;
        if (s == "RW")
            return RW;
        if (s == "S1")
            return S1;
        if (s == "S2")
            return S2;
        if (s == "S3")
            return S3;
        if (s == "S4")
            return S4;
        if (s == "S5")
            return S5;
        if (s == "S6")
            return S6;
        if (s == "S7")
            return S7;
        if (s == "S8")
            return S8;
        if (s == "T")
            return T;
        if (s == "FLL")
            return FLL;
        if (s == "FRL")
            return FRL;
        if (s == "RLL")
        	return RLL;
        if (s == "RRL")
        	return RRL;
        
        // Default
        return NO_SECTION;
    }

    /**
     * Returns true if the weapon can be considered in the 'front' Arc (Only good for 'Mechs and Tanks at the moment).
     */
    static public boolean isInFrontArc(int sNum)
    {
        if (sNum == H || sNum == LA || sNum == LT || sNum == CT || sNum == RT || sNum == RA ||
            sNum == LL || sNum == RL || sNum == FS)
            return true;
        else
            return false;
    }

    /**
     * Returns true if the weapon can be considered in the 'front' Arc (Only good for Tanks at the moment)
     */
    static public boolean isInTurretArc(int sNum)
    {
        if (sNum == T)
            return true;
        else
            return false;
    }

    /**
     * Returns true if the weapon can be considered in the left Arc (Only good for 'Mechs and Tanks at the moment)
     */
    static public boolean isInLeftArc(int sNum)
    {
        if (sNum == LA || sNum == LS)
            return true;
        else
            return false;
    }

    /**
     * Returns true if the weapon can be considered in the right Arc (Only good for 'Mechs and Tanks at the moment).
     */
    static public boolean isInRightArc(int sNum)
    {
        if (sNum == RA || sNum == RS)
            return true;
        else
            return false;
    }

    /**
     * Returns true if the weapon can be considered in the rear or aft Arc (Only good for 'Mechs and Tanks at the moment)
     */
    static public boolean isInRearArc(int sNum)
    {
        if (sNum == Hr || sNum == LAr || sNum == LTr || sNum == CTr || sNum == RTr || sNum == RAr ||
            sNum == LLr || sNum == RLr || sNum == AS)
            return true;
        else
            return false;
    }
    
    /**
     * Adds a new weapon to our list of weapons, or updates an existing one
     */
    static public void newWeapon(MUWeapon w)
    {
        try {
            weapons[w.typeNumber] = w;
        }
        catch (Exception e) {
            System.out.println("Error: newWeapon: " + e);
        }
    }

    /**
     * Gets a weapon based on its weapon number
     */
    static public MUWeapon getWeapon(int number)
    {
        try {
            return weapons[number];
        }
        catch (Exception e) {
            System.out.println("Error: getWeapon: " + e);
            return null;
        }
    }
}