//
//  HUDAmmoStatus.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine.commands;

/*
 * e. Ammo status
 * 
 *    command:
 * 	hudinfo am
 * 
 *    response:
 *    Zero or more:
 * 	#HUD:<key>:AM:L# AN,WT,AM,AR,FR
 *    Exactly once:
 * 	#HUD:<key>:AM:D# Done
 * 
 *    AN: integer, ammo bin number
 *    WT: weapon type, what the ammo is for
 *    AM: ammo mode
 *    AR: integer, rounds remaining
 *    FR: integer, full capacity
 * 
 *    Example:
 *     > hudinfo am
 *     < #HUD:C58x2:AM:L# 0,2,A,20,24
 *     < #HUD:C58x2:AM:L# 1,2,C,12,12
 *     < #HUD:C58x2:AM:L# 2,2,-,48,48
 *     < #HUD:C58x2:AM:D# Done
 *
 * (Taken from BTMUX's doc/hudinfo.spec.)
 */
public class HUDAmmoStatus extends HUDCommand {
	public String toString () {
		return "hudinfo am";
	}
}
