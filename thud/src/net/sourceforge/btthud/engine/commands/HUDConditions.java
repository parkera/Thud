//
//  HUDConditions.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine.commands;

/*
 * e. Conditions
 *
 *    command:
 *         hudinfo co
 *
 *    response:
 *    Exactly once:
 *         #HUD:<key>:CO:R# LT,VR,GR,TP,FL
 *
 *    LT: light type
 *    VR: range, visibility range
 *    GR: integer, gravity in 100th G's
 *    TP: heatmeasure, ambient temperature
 *    FL: map condition flags
 *
 *    Example:
 *     > hudinfo co
 *     < #HUD:C58x2:CO:R# D,60,100,21,UDhudinfo co
 *
 * (Taken from BTMUX's doc/hudinfo.spec.)
 */
public class HUDConditions extends HUDCommand {
	public String toString () {
		return "hudinfo co";
	}
}
