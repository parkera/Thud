//
//  UserCommand.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine.commands;

/**
 * Wrapper Command for user-entered string.  Prevents the user from manually
 * executing hudinfo commands by silently dropping such commands.
 */
public class UserCommand extends Command {
	private final String commandString;

	public UserCommand (final String commandString) {
		// hudinfo is case-sensitive in all known implementations.
		if (commandString.length() == 0
		    || commandString.equals("hudinfo")
		    || commandString.startsWith("hudinfo ")) {
			this.commandString = null;
		} else {
			this.commandString = commandString;
		}
	}

	public boolean isEmpty () {
		return (commandString == null);
	}

	public boolean expectsReply () {
		return false;
	}

	public String toString () {
		return commandString;
	}
}
