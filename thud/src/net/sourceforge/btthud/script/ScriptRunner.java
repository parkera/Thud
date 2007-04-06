//
//  ScriptRunner.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.script;

import java.util.List;
import java.util.Iterator;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Function;

import java.io.Reader;
import java.io.IOException;

/**
 * Scripted event-handling thread.
 *
 * TODO: Currently only uses the Rhino JavaScript engine, but should be
 * extended in the future to support pluggable scripting languages.
 */
public class ScriptRunner implements Runnable {
	private Context context = null;
	private Scriptable sharedScope = null;

	private boolean go = true;

	private List<Event> eventQueue = new java.util.ArrayList<Event> ();

	// Construct and start a new ScriptRunner thread.
	public ScriptRunner () {
		new Thread (this, "ScriptRunner").start();
	}

	// Add an event to the queue.
	public void call (final String function) {
		call(function, Context.emptyArgs);
	}

	public void call (final String function, final Object[] args) {
		addEvent(new Event (function, args));
	}

	private void addEvent (final Event evt) {
		synchronized (eventQueue) {
			eventQueue.add(evt);
			eventQueue.notify();
		}
	}

	// Execute a script from an input stream.
	public void execute (final Reader input, final String name) {
		addEvent(new ExecuteEvent (input, name));
	}

	public void execute (final String input, final String name) {
		addEvent(new ExecuteEvent (input, name));
	}

	// Main loop that waits for events to process.
	public void run () {
		context = Context.enter();

		try {
			// TODO: Let user set (and adjust) optimization level.

			// Initialize shared global scope.
			sharedScope = context.initStandardObjects(null, true);

			// TODO: We'd add and seal our Thud extensions here.
			//sharedScope.sealObject();

			// Initialize unsealed global scope.

			// TODO: This would be a writable global scope, where
			// user-defined scripts would set global values.  It
			// might not be needed.

			// TODO: We might want a per-connection scope, too.

			// Event dispatch loop.
			synchronized (eventQueue) {
				while (go) {
					// Handle pending events.
					dispatchEvents();

					// Wait for more events.
					try {
						eventQueue.wait();
					} catch (InterruptedException e) {
						// No big deal.
					}
				}
			}
		} finally {
			Context.exit();
		}
	}

	// Stop thread gracefully.
	public void pleaseStop () {
		synchronized (eventQueue) {
			go = false;
			eventQueue.notify();
		}
	}

	// Perform actual event dispatch.
	private void dispatchEvents () {
		final Iterator<Event> eqIter = eventQueue.iterator();

		while (eqIter.hasNext()) {
			final Event evt = eqIter.next();
			eqIter.remove();

			try {
				if (evt.isExecuteEvent()) {
					handleEvent((ExecuteEvent)evt);
				} else {
					handleEvent(evt);
				}
			} catch (Exception e) {
				// Report exceptions and continue.
				System.err.println("Script: " + e);
			}
		}
	}

	private void handleEvent (final Event evt) {
		final String fName = evt.key;
		final Object[] args = (Object[])evt.data;

		// Find function.
		final Object fObject = sharedScope.get(fName, sharedScope);

		if (!(fObject instanceof Function)) {
			// No function.
			System.err.println("No function: " + fName + "()");
			return;
		}

		// Extend scope for function call.
		final Scriptable scope = context.newObject(sharedScope);
		scope.setPrototype(sharedScope);
		scope.setParentScope(null);

		// Invoke function.
		final Object result = ((Function)fObject).call(context, scope,
		                                               scope, args);

		System.out.println("Script: " + context.toString(result));
	}

	private void handleEvent (final ExecuteEvent evt) {
		// Handle execute event.
		if (evt.isString()) {
			executeString((String)evt.data, evt.key);
		} else {
			executeScript((Reader)evt.data, evt.key);
		}
	}

	private void executeString (final String input, final String name) {
		// Execute string in global scope.
		// TODO: Make this togglable?
		final Object result = context.evaluateString(sharedScope, input,
		                                             name, 1, null);
		System.out.println("Script: " + context.toString(result));
	}

	private void executeScript (final Reader input, final String name) {
		// Execute script in global scope.
		try {
			context.evaluateReader(sharedScope, input,
			                       name, 1, null);
		} catch (IOException e) {
			System.err.println("Script: " + name + ": " + e);
		}
	}

	// Create a local scope.
	private Scriptable getLocalScope () {
		final Scriptable scope = context.newObject(sharedScope);
		scope.setPrototype(sharedScope);
		scope.setParentScope(null);
		return scope;
	}

	// Private Event class.
	static private class Event {
		protected final String key;
		protected final Object data;

		private Event (final String key, final Object data) {
			this.key = key;
			this.data = data;
		}

		protected boolean isExecuteEvent () {
			return false;
		}

		public String toString () {
			return "<" + key + ", " + data + ">";
		}
	}

	static private class ExecuteEvent extends Event {
		private boolean isString;

		private ExecuteEvent (final Reader input, final String name) {
			super(name, input);
			isString = false;
		}

		private ExecuteEvent (final String input, final String name) {
			super(name, input);
			isString = true;
		}

		protected boolean isExecuteEvent () {
			return true;
		}

		protected boolean isString () {
			return isString;
		}
	}
}
