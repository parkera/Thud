//
//  HUDTactical.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine.commands;

/*
 * d. Tactical
 * 
 *    command:
 * 	hudinfo t [ <height> [ <range> <bearing> [ l ] ] ]
 * 
 *    response:
 *    Exactly once:
 * 	#HUD:<key>:T:S# SX,SY,EX,EY,MI,MN,MV
 *    Once or more:
 * 	#HUD:<key>:T:L# Y,TS
 *    Exactly once:
 * 	#HUD:<key>:T:D# Done
 * 
 *    SX: coordinate, Start X
 *    SY: coordinate, Start Y
 *    EX: coordinate, End X
 *    EY: coordinate, End Y
 *    MI: map identifier
 *    MN: map name
 *    MV: map version number
 * 
 *    Y: coordinate, Y coordinate for line
 *    TS: tactical string, of length (EX-SX + 1)*2
 * 
 *    The terrain string (TS) is a special type of string. It is built up out of
 *    pairs of characters for terrain elevation and terrain type. One pair (two
 *    characters) per X-coordinate of the tactical string. Water hexes have
 *    depth instead of height (their top level is always 0).
 * 
 *    The MI, MN and MV items are not mandatory, and should be -1 when not
 *    supported or disabled by the game administrator(s).
 * 
 *    If the fourth argument is passed in and is 'l', a line-of-sight tactical
 *    (if available) will be returned, where all unknown terrain and/or height
 *    is '?'.
 * 
 *    Example:
 *     > hudinfo t 5
 *     < #HUD:C58x2:T:S# 11,10,29,14,-1,-1,-1
 *     < #HUD:C58x2:T:L# 10,#0"0"0"0"0"0"0"0'0~1.0.0.0.0'0'0'0'0'0
 *     < #HUD:C58x2:T:L# 11,"0#0"0"0"0"0"0'0'0~1'0.0.0.0.0.0.0.0.0
 *     < #HUD:C58x2:T:L# 12,^9#0#0"0"0"0"0'0~1'0'0'0.0.0.0.0.0.0.0
 *     < #HUD:C58x2:T:L# 13,^9#1^9#0"0"0'0~1'0.0'0.0.0.0.0.0.0.0.0
 *     < #HUD:C58x2:T:L# 14,^9#2^9^9#0'0'0'0~1'0.0.0.0.0.0.0.0.0.0
 *     < #HUD:C58x2:T:D# Done
 *
 * (Taken from BTMUX's doc/hudinfo.spec.)
 */
public class HUDTactical extends HUDCommand {
	private final String commandString;

	private final int height;
	private final boolean los;

	public HUDTactical (final int height) {
		this (height, false);
	}

	public HUDTactical (final int height, final boolean los) {
		this.height = height;
		this.los = los;

		if (los) {
			commandString = "hudinfo t " + height + " 0 0 l";
		} else {
			commandString = "hudinfo t " + height;
		}
	}

	public String toString () {
		return commandString;
	}
}
