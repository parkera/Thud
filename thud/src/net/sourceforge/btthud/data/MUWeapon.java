//
//  MUWeapon.java
//  Thud
//
//  Created by Anthony Parker on Thu Feb 07 2002.
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.data;

public class MUWeapon {

    public int		typeNumber;		// global id, not per-mech number
    public String	name;
    public int		minRange, shortRange, medRange, longRange;
    public int		minRangeWater, shortRangeWater, medRangeWater, longRangeWater;
    public int		criticalSize;
    public int		weight;
    public int		damage;
    public int		recycle;
    public String	fireModes;
    public String	ammoModes;
    public String	damageType;
    public int		heat;

    public MUWeapon()
    {
        
    }
}
