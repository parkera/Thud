//
//  HUDCommand.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine.commands;

/**
 * Base class for HUD commands.  This class should not be used outside of this
 * package.  Instead, derive a new public class inside this package.
 */
abstract class HUDCommand extends Command {
	// Override with "hudinfo ..." command.
	abstract public String toString ();
}
