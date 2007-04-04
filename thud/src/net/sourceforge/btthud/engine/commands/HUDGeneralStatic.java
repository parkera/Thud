//
//  HUDGeneralStatic.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine.commands;

/*
 * f. Static General info
 * 
 *    command:
 * 	hudinfo sgi
 * 
 *    response:
 *    Exactly once:
 * 	#HUD:<key>:SGI:R# TC,RF,NM,WS,RS,BS,VS,TF,HS,AT
 * 
 *    TC: unit type character
 *    RF: string, unit referece
 *    NM: string, unit name
 *    WS: speed, unit max walking/cruise speed
 *    RS: speed, unit max running/flank speed
 *    BS: speed, unit max reverse speed
 *    VS: speed, unit max vertical speed
 *    TF: fuel, or '-' for n/a
 *    HS: integer, number of templated (single) heatsinks
 *    AT: advtech, advanced technology available or '-' if n/a
 * 
 *    Example:
 *      > hudinfo sgi
 *      < #HUD:C58x2:SGI:R# B,MAD-1W,Mawauder,43,64,43,0,-,12,S
 * 
 * (Taken from BTMUX's doc/hudinfo.spec.)
 */
public class HUDGeneralStatic extends HUDCommand {
	public String toString () {
		return "hudinfo sgi";
	}
}
