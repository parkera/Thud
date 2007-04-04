//
//  HUDContactsBuildings.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine.commands;

/*
 * f. Building Contacts
 * 
 *    command:
 * 	hudinfo cb
 * 
 *    response:
 *    Zero or more:
 * 	#HUD:<key>:CB:L# AC,BN,X,Y,Z,RN,BR,CF,MCF,BS
 *    Exactly once:
 * 	#HUD:<KEY>:CB:D# Done
 * 
 *    AC: arc, weapon arc the building is in
 *    BN: string, name of the building, or '-' if unknown
 *    X, Y, Z: coordinates of building
 *    RN: range, range to building
 *    BR: degree, bearing to building
 *    CF: integer, current construction factor of building
 *    MCF: integer, maximum construction factor of building
 *    BS: building status string
 * 
 *    Example:
 *     > hudinfo cb
 *     < #HUD:C58x2:CB:L# *,Underground Hangar,55,66,7,25.1,180,1875,2000,X
 *     < #HUD:C58x2:CB:D# Done
 *
 * (Taken from BTMUX's doc/hudinfo.spec.)
 */
public class HUDContactsBuildings extends HUDCommand {
	public String toString () {
		return "hudinfo cb";
	}
}
