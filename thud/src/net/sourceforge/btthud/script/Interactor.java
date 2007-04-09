//
//  Interactor.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.script;

import java.util.Map;
import java.util.HashMap;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;

import java.io.FileReader;
import java.io.FileNotFoundException;

/**
 * Handles user interaction with the scripting engine.  This is probably a bit
 * overly general in the long term, but for now, it just means that it handles
 * command dispatch from the input line.
 */
public class Interactor {
	//
	// Static commands.
	//
	static private final Map<String,Command> primitive = new HashMap<String,Command> ();

	static {
		primitive.put("load", new Command () {
			void execute (final Interactor interactor,
			              final String tail) {
				Reader in;

				try {
					in = new FileReader (tail);
				} catch (final FileNotFoundException e) {
					System.err.println("Load failed: " + e);
					return;
				}

				in = new BufferedReader (in);
				interactor.scriptRunner.execute(in, tail);
				// execute() will close the Reader for us.
			}
		});

		primitive.put("eval", new Command () {
			void execute (final Interactor interactor,
			              final String tail) {
				interactor.scriptRunner.execute(tail, "<eval>");
			}
		});
	}

	static abstract private class Command {
		void execute (final Interactor interactor) {
			execute(interactor, "");
		}

		abstract void execute (final Interactor interactor,
		                       final String tail);
	}

	//
	// Instance data.
	//

	private final ScriptRunner scriptRunner;

	public Interactor (final ScriptRunner scriptRunner) {
		this.scriptRunner = scriptRunner;
	}

	public void doCommand (final String input) {
		// Split command name from arguments.
		final String[] commandSplit = input.split(" ", 2);

		final String command = commandSplit[0];
		final String tail = (commandSplit.length == 2)
		                    ? commandSplit[1] : null;

		// Dispatch command.
		final Command primitiveCommand = primitive.get(command);

		if (primitiveCommand != null) {
			// Invoke primitive command.
			if (tail != null) {
				primitiveCommand.execute(this, tail);
			} else {
				primitiveCommand.execute(this);
			}
		} else {
			// Schedule non-primitive call.
			if (tail != null) {
				scriptRunner.call(command, tail.split(" "));
			} else {
				scriptRunner.call(command);
			}
		}
	}
}
