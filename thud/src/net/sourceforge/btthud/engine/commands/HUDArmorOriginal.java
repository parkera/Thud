//
//  HUDArmorOriginal.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine.commands;

/*
 * g. Original Armor Status
 *    
 *    command:
 *    	hudinfo oas
 * 
 *    response:
 *    Zero or more:
 *    	#HUD:<key>:OAS:L# SC,AF,AR,IS
 *    Exactly once:
 *    	#HUD:<key>:OAS:D# Done
 *    	
 *    SC: section
 *    AF: integer, original front armor or '-' if n/a
 *    AR: integer, original rear armor or '-' if n/a
 *    IS: integer, original internal structure or '-' if n/a
 * 
 *    Example:
 *     > hudinfo oas
 *     < #HUD:C58x2:OAS:L# LA,22,-,12
 *     < #HUD:C58x2:OAS:L# RA,22,-,12
 *     < #HUD:C58x2:OAS:L# LT,17,8,16
 *     < #HUD:C58x2:OAS:L# RT,17,8,16
 *     < #HUD:C58x2:OAS:L# CT,35,10,23
 *     < #HUD:C58x2:OAS:L# LL,18,-,16
 *     < #HUD:C58x2:OAS:L# RL,18,-,16
 *     < #HUD:C58x2:OAS:L# H,9,-,3
 *     < #HUD:C58x2:OAS:D# Done
 * 
 * (Taken from BTMUX's doc/hudinfo.spec.)
 */
public class HUDArmorOriginal extends HUDCommand {
	public String toString () {
		return "hudinfo oas";
	}
}
