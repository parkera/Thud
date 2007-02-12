//
//  ThudActions.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

// FIXME: All of this stuff probably shouldn't go into one class.

// Action class to send a fixed command string.
class SendCommandAction extends AbstractAction {
	private final Thud thud;
	private final String command;

	SendCommandAction (final Thud thud, final String command) {
		super(command.intern());

		this.thud = thud;
		this.command = command.intern();
	}

	public void actionPerformed (final ActionEvent ae) {
		if (thud.conn == null)
			return;

		try {
			thud.conn.sendCommand(command);
		} catch (Exception e) {
			// TODO: Seems like it'd be more friendly to report
			// these errors in the main window, or in a modal
			// dialog.  Hiding things in the console is so like
			// 1990.
			System.err.println("Can't send: " + e);
		}
	}
}

/**
 * An InputMap wrapper to allow us to bind numeric pad keys, without having
 * them generate "typed #"-style KeyStrokes in JTextComponents.
 */
class NumpadInputMap extends InputMap {
	// Various KeyStrokes we're interested in.
	private static final KeyStroke TYPED_0 = KeyStroke.getKeyStroke('0');
	private static final KeyStroke TYPED_1 = KeyStroke.getKeyStroke('1');
	private static final KeyStroke TYPED_2 = KeyStroke.getKeyStroke('2');
	private static final KeyStroke TYPED_3 = KeyStroke.getKeyStroke('3');
	private static final KeyStroke TYPED_4 = KeyStroke.getKeyStroke('4');
	private static final KeyStroke TYPED_5 = KeyStroke.getKeyStroke('5');
	private static final KeyStroke TYPED_6 = KeyStroke.getKeyStroke('6');
	private static final KeyStroke TYPED_7 = KeyStroke.getKeyStroke('7');
	private static final KeyStroke TYPED_8 = KeyStroke.getKeyStroke('8');
	private static final KeyStroke TYPED_9 = KeyStroke.getKeyStroke('9');


	// Backing InputMap.
	private final InputMap realMap;

	NumpadInputMap (final InputMap realMap) {
		this.realMap = realMap;
	}

	private KeyStroke translateKeyStroke (final KeyStroke keyStroke) {
		switch (keyStroke.getKeyEventType()) {
		case KeyEvent.KEY_PRESSED:
			// Treat pressed numbers like typed numbers.
			// TODO: It'd be nice to ignore NUMLOCK, too.
			switch (keyStroke.getKeyCode()) {
			case KeyEvent.VK_0: return TYPED_0;
			case KeyEvent.VK_1: return TYPED_1;
			case KeyEvent.VK_2: return TYPED_2;
			case KeyEvent.VK_3: return TYPED_3;
			case KeyEvent.VK_4: return TYPED_4;
			case KeyEvent.VK_5: return TYPED_5;
			case KeyEvent.VK_6: return TYPED_6;
			case KeyEvent.VK_7: return TYPED_7;
			case KeyEvent.VK_8: return TYPED_8;
			case KeyEvent.VK_9: return TYPED_9;

			default: break;
			}
			break;

		case KeyEvent.KEY_TYPED:
			// Drop typed numbers.
			switch (keyStroke.getKeyChar()) {
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9': return null;

			default: break;
			}
			break;

		default:
			break;
		}

		return keyStroke;
	}


	//
	// Proxy methods.
	//

	public KeyStroke[] allKeys () {
		// TODO: We probably shouldn't return keys which are filtered.
		return realMap.allKeys();
	}

	public void clear () {
		realMap.clear();
	}

	public Object get (final KeyStroke keyStroke) {
		final KeyStroke translated = translateKeyStroke(keyStroke);
		return (translated == null) ? "none" : realMap.get(translated);
	}

	public InputMap getParent () {
		// TODO: Do we want to let a parent have a crack at it?
		return realMap.getParent();
	}

	public KeyStroke[] keys () {
		// TODO: We probably shouldn't return keys which are filtered.
		return realMap.keys();
	}

	public void put (final KeyStroke keyStroke, final Object actionMapKey) {
		realMap.put(keyStroke, actionMapKey);
	}

	public void remove (final KeyStroke key) {
		realMap.remove(key);
	}

	public void setParent (final InputMap map) {
		realMap.setParent(map);
	}

	public int size () {
		// TODO: We should subtract out the filtered keys.
		return realMap.size();
	}
}

/**
 * Proxy for an Action in a specific JComponent's ActionMap.
 *
 * TODO: This sort of thing seems useful enough that it'd be supplied by Swing.
 */
class ActionRedirector extends AbstractAction {
	private final Object newSrc;
	private final Action newAction;

	ActionRedirector (final JComponent newSrc, final Object newAction) {
		this.newSrc = newSrc;
		this.newAction = newSrc.getActionMap().get(newAction);
	}

	public void actionPerformed (final ActionEvent ae) {
		ae.setSource(newSrc);
		newAction.actionPerformed(ae);
	}
}
