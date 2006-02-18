//
//  MUUnitAmmo.java
//  Thud
//
//  Created by Tim Krajcar on Tue Feb 14 2006.
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package btthud.data;


/**
 * Class to keep track of individual ammo bins for a unit.
 * @author tkrajcar
 *
 */
public class MUUnitAmmo{

    public int		number;
    public int		weaponTypeNumber; // global id
    public String	ammoMode;
    public int		roundsRemaining;
    public int		roundsOriginal;
}
