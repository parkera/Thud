//
//  HUDArmorStatus.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine.commands;

/*
 * h. Armor Status
 * 
 *    command:
 *    	hudinfo as
 *    response:
 *    Zero or more:
 *    	#HUD:<key>:AS:L# SC,AF,AR,IS
 *    Exactly once:
 *    	#HUD:<key>:AS:D# Done
 *    	
 *    SC: section
 *    AF: integer, front armor or '-' if n/a
 *    AR: integer, rear armor or '-' if n/a
 *    IS: integer, internal structure or '-' if n/a, 0 if destroyed
 * 
 *    Example:
 *     > hudinfo as
 *     < #HUD:C58x2:AS:L# LA,0,-,0
 *     < #HUD:C58x2:AS:L# RA,22,-,12
 *     < #HUD:C58x2:AS:L# LT,9,5,16
 *     < #HUD:C58x2:AS:L# RT,4,1,16
 *     < #HUD:C58x2:AS:L# CT,0,10,5
 *     < #HUD:C58x2:AS:L# LL,0,-,16
 *     < #HUD:C58x2:AS:L# RL,18,-,16
 *     < #HUD:C58x2:AS:L# H,9,-,3
 *     < #HUD:C58x2:AS:D# Done
 *
 * (Taken from BTMUX's doc/hudinfo.spec.)
 */
public class HUDArmorStatus extends HUDCommand {
	public String toString () {
		return "hudinfo as";
	}
}
