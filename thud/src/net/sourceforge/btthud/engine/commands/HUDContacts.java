//
//  HUDContacts.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine.commands;

/*
 * a. Contacts
 * 
 *    command:
 *   	hudinfo c
 * 
 *    response:
 *    Zero or more:
 *   	#HUD:<key>:C:L# ID,AC,SE,UT,MN,X,Y,Z,RN,BR,SP,VS,HD,JH,RTC,BTC,TN,HT,FL
 *    Exactly once:
 *   	#HUD:<key>:C:D# Done
 * 
 *    ID: mechid, ID of the unit
 *    AC: arc, weapon arc the unit is in
 *    SE: sensors, sensors that see the unit
 *    UT: unit type character
 *    MN: string, mechname of unit, or '-' if unknown
 *    X, Y, Z: coordinates of unit
 *    RN: range, range to unit
 *    BR: degree, bearing to unit
 *    SP: speed, speed of unit
 *    VS: speed, vertical speed of unit
 *    HD: degree, heading of unit
 *    JH: degree, jump heading, or '-' if not jumping
 *    RTC: range, range from unit to X,Y center
 *    BTC: degree, bearing from unit to X,Y center
 *    TN: integer, unit weight in tons
 *    HT: heatmeasure, unit's apparent heat (overheat)
 *    FL: unit status string
 * 
 *    Example:
 *     > hudinfo c
 *     < #HUD:C58x2:C:L# DV,r*,PS,B,Pheonix-Hawg,5,3,1,1.6,2,0.0,0.0,180,-,0.2,0,45,0,BSl
 *     < #HUD:C58x2:C:D# Done
 *
 * (Taken from BTMUX's doc/hudinfo.spec.)
 */
public class HUDContacts extends HUDCommand {
	public String toString () {
		return "hudinfo c";
	}
}
