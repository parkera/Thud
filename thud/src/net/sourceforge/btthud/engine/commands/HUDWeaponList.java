//
//  HUDWeaponList.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine.commands;

/*
 * c. Weapon List
 * 
 *    command:
 * 	hudinfo wl
 * 
 *    repsonse:
 *    Zero or more:
 * 	#HUD:<key>:WL:L# WN,NM,NR,SR,MR,LR,NS,SS,MS,LS,CR,WT,DM,RT,FM,AT,DT
 *    Exactly one:
 * 	#HUD:<key>:WL:D# Done
 * 
 *    WN: integer, weapon number
 *    NM: string, weapon name
 *    NR: range, minimum range
 *    SR: range, short range
 *    MR: range, medium range
 *    LR: range, long range
 *    NS: range, minimum range in water
 *    SS: range, short range in water
 *    MS: range, medium range in water
 *    LS: range, long range in water
 *    CR: integer, size in critslots
 *    WT: integer, weight in 1/100 tons
 *    DM: integer, maximum damage
 *    RT: integer, recycle time in ticks
 *    FM: weapon fire mode, possible fire modes
 *    AT: ammo type, possible ammo types
 *    DT: damage type
 *    HT: heat measure, weapon heat per salvo
 * 
 *    Example:
 *     > hudinfo wl
 *     < #HUD:C58x2:WL:L# 0,Odd Laser,0,2,4,6,0,1,2,3,1,100,6,30,-,E,30
 *     < #HUD:C58x2:WL:L# 1,Heavy Odd Pulse Flamer-Laser,0,4,6,8,0,1,2,3,2,500,12,30,h,Eph,100
 *     < #HUD:C58x2:WL:L# 2,LRM-6,6,14,18,24,-,-,-,-,4,700,6,30,H,1ACNMSs,Mg,50
 *     < #HUD:C58x2:WL:D# Done
 * 
 * (Taken from BTMUX's doc/hudinfo.spec.)
 */
public class HUDWeaponList extends HUDCommand {
	public String toString () {
		return "hudinfo wl";
	}
}
