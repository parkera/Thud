//
//  Command.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine.commands;

abstract public class Command {
	public boolean isEmpty () {
		return false;
	}

	public boolean expectsReply () {
		return true;
	}

	abstract public String toString ();
}
