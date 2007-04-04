//
//  HUDSession.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine.commands;

/*
 * b. The session-key setting 'hudinfo key' command.
 * 
 *    command:
 * 	hudinfo key=<key>
 *    response:
 * 	#HUD:<key>:KEY:R# Key set
 * 
 *   This command is used to set the sesion key. It should be issued before any
 * hudinfo command other than the argument-less hudinfo, in order to set the
 * session key. The session key is a short (1-20 character) preferably
 * non-deterministic string of alphanumeric characters that serves as spoof
 * protection. The string is case-sensitive.
 * 
 *    Example:
 *     > hudinfo key=C58x2
 *     < #HUD:C58x2:KEY:R# Key set
 * 
 *    Error messages:
 * 
 *     "Invalid key":
 * 	Invalid characters used in key, or key too long.
 * 
 * (Taken from BTMUX's doc/hudinfo.spec.)
 */
public class HUDSession extends HUDCommand {
	private final String commandString;

	private final String sessionKey;

	public HUDSession (final String sessionKey) {
		this.sessionKey = sessionKey;

		commandString = "hudinfo key=" + sessionKey;
	}

	public String toString () {
		return commandString;
	}
}
