//
//  MUMyInfo.java
//  Thud
//
//  Created by asp on Wed Nov 21 2001.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//
//
package btthud.data;

import java.util.*;

public class MUMyInfo extends MUUnitInfo {

    public float		desiredSpeed, desiredVerticalSpeed;

    public int			heat, heatDissipation, heatSinks;

    public int			fuel;

    public float		walkSpeed, runSpeed, backSpeed, verticalSpeed;
    public int			jumpTargetX, jumpTargetY;
    
    public String		ref;
    public String		advTech;

    public static final int		MAX_UNIT_WEAPONS = 50;
    public MUUnitWeapon[]		unitWeapons = new MUUnitWeapon[MAX_UNIT_WEAPONS];		// List of our own weapons.. asssume 50 for now (bad)
    public MUUnitAmmo[]			unitAmmo = new MUUnitAmmo[MAX_UNIT_WEAPONS];			// List of our own ammunition

    public float				longRangeFront, longRangeLeft, longRangeRight, longRangeRear, longRangeTurret;

    // This armor info could be generalized to MUUnitInfo, since it could be used for scanning results
    // However, since we only use it on ourselves now, we'll leave it here to avoid creating thousands of unneeded objects
    public MUSection			armor[] = new MUSection[TOTAL_SECTIONS];
    
    public MUMyInfo()
    {
        friend = true;
        id = "**";
        type = "U";
        ref = " ";

        walkSpeed = (float) 0.0;
        runSpeed = (float) 0.0;
        backSpeed = (float) 0.0;
        verticalSpeed = (float) 0.0;

        longRangeFront = 1.0f;
        longRangeLeft = 1.0f;
        longRangeRight = 1.0f;
        longRangeRear = 1.0f;
        longRangeTurret = 1.0f;

        for (int i = 0; i < TOTAL_SECTIONS; i++)
            armor[i] = new MUSection();
        
    }
    
    public String toString()
    {
        return (super.toString() + " RTC: " + String.valueOf(rangeToCenter) + " BTC: " + String.valueOf(bearingToCenter));
    }

    /**
      * Adds a new weapon to our unit-specific list of weapons, or updates an existing one
     */
    public void newUnitWeapon(MUUnitWeapon w)
    {
        try {

            unitWeapons[w.number] = w;

            // Update our maximum ranges
            if (weapons[w.typeNumber].longRange > longRangeFront && MUUnitInfo.isInFrontArc(indexForSection(w.loc)))
                longRangeFront = weapons[w.typeNumber].longRange;

            if (weapons[w.typeNumber].longRange > longRangeRight && MUUnitInfo.isInRightArc(indexForSection(w.loc)))
                longRangeRight = weapons[w.typeNumber].longRange;

            if (weapons[w.typeNumber].longRange > longRangeLeft && MUUnitInfo.isInLeftArc(indexForSection(w.loc)))
                longRangeLeft = weapons[w.typeNumber].longRange;

            if (weapons[w.typeNumber].longRange > longRangeRear && MUUnitInfo.isInRearArc(indexForSection(w.loc)))
                longRangeRear = weapons[w.typeNumber].longRange;

            if (weapons[w.typeNumber].longRange > longRangeTurret && MUUnitInfo.isInTurretArc(indexForSection(w.loc)))
                longRangeTurret = weapons[w.typeNumber].longRange;
            
        }
        catch (Exception e) {
            System.out.println("Error: newWeapon: " + e);
        }
    }
    
    /*
     * Adds a new ammo bin to our unit-specific list of ammobins.
     */
    public void newUnitAmmo(MUUnitAmmo a) {
    	try {
    		unitAmmo[a.number] = a;
    	} catch (Exception e) {
    		System.out.println("Error: newAmmo: " + a);
    	}
    	
    }
    
    /**
      * Figures out the maximum range in the front or turret arc.
      */
    public float maxFrontRange()
    {
        return longRangeFront;
    }

    /**
        * Figures out the maximum range in the left arc.
     */
    public float maxLeftRange()
    {
        return longRangeLeft;
    }

    /**
        * Figures out the maximum range in the right arc.
     */
    public float maxRightRange()
    {
        return longRangeRight;
    }

    /**
        * Figures out the maximum range in the rear arc.
     */
    public float maxRearRange()
    {
        return longRangeRear;
    }

    /**
        * Figures out the maximum range in the turret arc.
     */
    public float maxTurretRange()
    {
        return longRangeTurret;
    }

    /**
      * Return a float corresponding to percentage armor left on the whole unit.
      */
    public float percentArmorLeft()
    {
        int			totalAvailArmor = 0;
        int			totalLeftArmor = 0;

        for (int i = 0; i < TOTAL_SECTIONS; i++)
        {
            totalAvailArmor += armor[i].of;
            totalAvailArmor += armor[i].or;
            totalLeftArmor += armor[i].f;
            totalLeftArmor += armor[i].r;
        }

        if (totalAvailArmor != 0)
            return(float) ((int) (100.0 * ((float) totalLeftArmor / (float) totalAvailArmor)));
        else
            return (float) 100;
    }
    
    /**
      * Return a float corresponding to percentage internal structure left on the whole unit.
      */
    public float percentInternalLeft()
    {
        int			totalAvailInternal = 0;
        int			totalLeftInternal = 0;

        for (int i = 0; i < TOTAL_SECTIONS; i++)
        {
            totalAvailInternal += armor[i].oi;
            totalLeftInternal += armor[i].i;
        }

        if (totalAvailInternal != 0)
            return (float) ((int) (100.0 * ((float) totalLeftInternal / (float) totalAvailInternal)));
        else
            return (float) 100;
    }
    
    /**
        * Return a float corresponding to percentage armor left on the specified location.
     */
    public float percentArmorLeft(int loc)
    {
        int			totalAvailArmor = armor[loc].of;
        
        if (totalAvailArmor != 0)
            return(float) ((int) (100.0 * ((float) armor[loc].f / (float) totalAvailArmor)));
        else
            return (float) 100;
    }

    public float percentRearArmorLeft(int loc)
    {
        int			totalAvailArmor = armor[loc].or;

        if (totalAvailArmor != 0)
            return (float) ((int) (100.0 * ((float) armor[loc].r / (float) totalAvailArmor)));
        else
            return (float) 100;
    }
    
    /**
        * Return a float corresponding to percentage internal structure left on the specified location.
     */
    public float percentInternalLeft(int loc)
    {
        int			totalAvailInternal = armor[loc].oi;
        
        if (totalAvailInternal != 0)
            return (float) ((int) (100.0 * ((float) armor[loc].i / (float) totalAvailInternal)));
        else
            return (float) 100;
    }
}
