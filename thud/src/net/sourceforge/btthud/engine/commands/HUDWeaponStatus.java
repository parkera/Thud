//
//  HUDWeaponStatus.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine.commands;

/*
 * b. Weapon Status
 * 
 *    command:
 * 	hudinfo we
 * 
 *    response:
 *    Zero or more:
 * 	#HUD:<key>:WE:L# WN,WT,WQ,LC,ST,FM,AT
 *    Exactly once:
 * 	#HUD:<key>:WE:D# Done
 * 
 *    WN: integer, weapon number
 *    WT: weapon type number
 *    WQ: weapon quality
 *    LC: section, location of weapon
 *    ST: weapon status
 *    FM: weapon fire mode
 *    AT: ammo type, the type of ammo selected
 * 
 *    Purpose:
 *  	To retrieve detailed information about the status of a units
 * 	weapons. One response is generated for each weapon on the unit,
 * 	functional or not, and a single "Done" message signals the end of
 * 	the list. Weapons are returned in unspecified order.
 * 
 *    Example:
 *     > hudinfo we
 *     < #HUD:C58x2:WE:L# 0,1,3,RA,-,h,-
 *     < #HUD:C58x2:WE:L# 1,2,4,LL,29,H,A
 *     < #HUD:C58x2:WE:L# 5,2,1,CT,D,H,-
 *     < #HUD:C58x2:WE:D# Done
 * 
 * (Taken from BTMUX's doc/hudinfo.spec.)
 */
public class HUDWeaponStatus extends HUDCommand {
	public String toString () {
		return "hudinfo we";
	}
}
