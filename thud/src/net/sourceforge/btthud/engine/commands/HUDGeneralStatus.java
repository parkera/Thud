//
//  HUDGeneralStatus.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine.commands;

/*
 * a. General Status
 * 
 *    command:
 * 	hudinfo gs
 * 
 *    response:
 *    Exactly once:
 * 	#HUD:<key>:GS:R# ID,X,Y,Z,CH,DH,CS,DS,CH,HD,CF,CV,DV,RC,BC,TU,FL,JX,JY
 * 
 *    ID: mechid, own mech ID
 *    X, Y, Z : coordinates, current location
 *    CH: degree, current heading
 *    DH: degree, desired heading
 *    CS: speed, current speed
 *    DS: speed, desired speed
 *    CH: heatmeasure, current heat
 *    HD: heatmeasure, current heat dissipation
 *    CF: fuel or '-', current fuel or '-' if not applicable)
 *    CV: speed, current vertical speed
 *    DV: speed, desired vertical speed
 *    RC: range, range to center of current hex
 *    BC: degree, bearing of center of current hex
 *    TU: offset or '-', torso/turret facing offset (or '-' if not applicable)
 *    FL: Unit status flags
 *    JX, JY: jump X/Y targets (or - if not jumping)
 * 
 *    Example:
 *     > hudinfo gs
 *     < #HUD:C58x2:GS:R# QQ,5,5,0,0,0,32.25,43.0,10,120,-,0,0,0.2,179,0,L,-,-
 *
 * (Taken from BTMUX's doc/hudinfo.spec.)
 */
public class HUDGeneralStatus extends HUDCommand {
	public String toString () {
		return "hudinfo gs";
	}
}
