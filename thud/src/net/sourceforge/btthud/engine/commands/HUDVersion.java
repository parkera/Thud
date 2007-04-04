//
//  HUDVersion.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.engine.commands;

/*
 * a. The argument-less 'hudinfo' command
 * 
 *    command:
 * 	hudinfo
 *    response:
 *    Exactly one:
 * 	#HUD hudinfo version 1.0 [options: <option flags>]
 *    Or exactly one:
 *         Huh?  (Type "help" for help.)
 * 
 *    Purpose: Identification
 * 
 *  The argument-less 'hudinfo' command is a special case of the 'hudinfo'
 * command, the only form that does not returns a response with a session key.
 * Instead, it returns a version identifier and a list of supported uptions, so
 * clients can autodetect whether a MUX supports hudinfo and if so, which
 * version.
 * 
 *  The version number is built up from 'major' and 'minor' version, both
 * integers, separated by a dot. The version number is not a floating point
 * number; 1.9 is smaller than 1.10, and not the same as 1.90. The major
 * version differs only between versions of the protocol that are wholly
 * incompatible; a HUD client that discovers a major mode it does not know
 * should not try to use hudinfo. The minor version is increased when new
 * standard features are added in a backwards-compatible manner; clients should
 * not break or refuse to work if the minor version is higher than expected,
 * though they may do so if the minor version is lower than required.
 * 
 *  The optional 'options' section, if present, should contain a list of option
 * flags for non-standard new features, which are used to add non-standard or
 * experimental features without creating conflicts on the version number. Such
 * options and their option flags should preferably be registered by mailing to
 * the contacts in section Ib, "About this Document", both for inclusion in the
 * next version of the protocol, and to avoid collisions with other options.
 * 
 *    Example:
 *     > hudinfo
 *     < #HUD hudinfo version 1.0
 * 
 * (Taken from BTMUX's doc/hudinfo.spec.)
 */
public class HUDVersion extends HUDCommand {
	public String toString () {
		return "hudinfo";
	}
}
