//
//  MUMyInfo.java
//  Thud
//
//  Created by asp on Wed Nov 21 2001.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//
//

import java.util.*;

public class MUMyInfo extends MUUnitInfo {

    public int			desiredHeading;
    public float		desiredSpeed, desiredVerticalSpeed;

    public int			heat, heatDissipation, heatSinks;

    public int			turretHeading;
    public int			fuel;

    public float		walkSpeed, runSpeed, backSpeed, verticalSpeed;
    
    public String		ref;
    public String		advTech;

    public static final int		MAX_UNIT_WEAPONS = 50;
    public MUUnitWeapon[]	unitWeapons = new MUUnitWeapon[MAX_UNIT_WEAPONS];		// List of our own weapons.. asssume 50 for now (bad)

    public float			longRangeFront, longRangeLeft, longRangeRight, longRangeRear, longRangeTurret;

    public MUMyInfo()
    {
        friend = true;
        expired = 5;
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
}
