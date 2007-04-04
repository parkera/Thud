//
//  ThudActions.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.ui;

import net.sourceforge.btthud.engine.commands.UserCommand;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

// FIXME: All of this stuff probably shouldn't go into one class.
public class ThudActions {
	// FIXME: This class only exists to keep Ant from rebuilding this file.
}

// Action class to send a fixed command string.
class SendCommandAction extends AbstractAction {
	protected final Thud thud;
	private final String command;

	SendCommandAction (final Thud thud, final String command) {
		super(command.intern());

		this.thud = thud;
		this.command = command.intern();
	}

	public void actionPerformed (final ActionEvent ae) {
		if (thud.conn == null)
			return;

		final String realCommand = getCommand();

		if (realCommand == null)
			return;

		try {
			thud.conn.sendCommand(new UserCommand (realCommand));
		} catch (Exception e) {
			// TODO: Seems like it'd be more friendly to report
			// these errors in the main window, or in a modal
			// dialog.  Hiding things in the console is so like
			// 1990.
			System.err.println("Can't send: " + e);
		}
	}

	protected String getCommand () {
		return command;
	}
}

class StayHeadingAction extends SendCommandAction {
	StayHeadingAction (final Thud thud) {
		super(thud, "StayHeadingAction");
	}

	protected String getCommand () {
		if (thud.data == null || thud.data.myUnit == null)
			return null;

		return "heading " + thud.data.myUnit.heading;
	}
}

class ReverseHeadingAction extends SendCommandAction {
	ReverseHeadingAction (final Thud thud) {
		super(thud, "ReverseHeadingAction");
	}

	protected String getCommand () {
		if (thud.data == null || thud.data.myUnit == null)
			return null;

		return "heading " + (thud.data.myUnit.heading + 180) % 360;
	}
}

/**
 * Proxy for an Action in a specific JComponent's ActionMap.  This proxy is
 * currently limited to just relaying actionPerformed(); a full implementation
 * should mirror other properties of the target Action.
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

/**
 * A KeyListener to steal numeric pad KeyEvents and reroute it directly to the
 * JComponent and InputMap of our choosing.  This is basically needed because
 * the KeyStroke class doesn't encode nor support KeyEvent.getLocation()
 * information, and the alternatives are inadequate in terms of behavior.
 *
 * The typical usage is that a private InputMap would be defined at the top
 * level component of interest for numeric pad keys, which would trigger the
 * desired actions on that component.  Then the listener would be added to all
 * the focusable components in the hierarchy.
 *
 * Note that to work properly, this needs to be set on all focusable components
 * that need to have their numeric pad input captured.
 *
 * This class could be extended to handle other types of KeyEvents.
 */
class NumpadKeyListener implements KeyListener {
	private JComponent target;
	private InputMap inputMap;

	NumpadKeyListener (final JComponent target, final InputMap inputMap) {
		this.target = target;
		this.inputMap = inputMap;
	}

	void setJComponent (final JComponent target) {
		this.target = target;
	}

	void setInputMap (final InputMap inputMap) {
		this.inputMap = inputMap;
	}


	private void processKeyEvent (final KeyEvent ke) {
		if (ke.getKeyLocation() != KeyEvent.KEY_LOCATION_NUMPAD)
			return;

		// We remap all the numeric pad keys to the same values,
		// regardless of numlock status.  Technically, this is
		// platform/locale-specific, but this should be good enough,
		// especially since everything will work fine with numlock on
		// (which is what this class tries to map to).
		//
		// In any case, it'd be best to keep such knowledge centralized
		// here, rather than scattered around the code.
		//
		// Note that this still leaves a big hole at 5.
		//
		// Windows hacks: SHIFT + numeric keypad on Windows effectively
		// reverses NUMLOCK.  There's no real way around this, except
		// by translating the various keypad codes back.  As a side
		// effect, this makes NUMLOCK off act like a shift lock on all
		// platforms.
		int keyCode = ke.getKeyCode();
		boolean shift = false;

		switch (keyCode) {
		case KeyEvent.VK_HOME:
			keyCode = KeyEvent.VK_NUMPAD7;
			shift = true; // Winders
			break;

		case KeyEvent.VK_UP: // Winders
		case KeyEvent.VK_KP_UP:
			keyCode = KeyEvent.VK_NUMPAD8;
			shift = true; // Winders
			break;

		case KeyEvent.VK_PAGE_UP:
			keyCode = KeyEvent.VK_NUMPAD9;
			shift = true; // Winders
			break;

		case KeyEvent.VK_LEFT: // Winders
		case KeyEvent.VK_KP_LEFT:
			keyCode = KeyEvent.VK_NUMPAD4;
			shift = true; // Winders
			break;

		// Hole at 5.  Seems to map to "Begin" on Unix.

		case KeyEvent.VK_RIGHT: // Winders
		case KeyEvent.VK_KP_RIGHT:
			keyCode = KeyEvent.VK_NUMPAD6;
			shift = true; // Winders
			break;

		case KeyEvent.VK_END:
			keyCode = KeyEvent.VK_NUMPAD1;
			shift = true; // Winders
			break;

		case KeyEvent.VK_DOWN: // Winders
		case KeyEvent.VK_KP_DOWN:
			keyCode = KeyEvent.VK_NUMPAD2;
			shift = true; // Winders
			break;

		case KeyEvent.VK_PAGE_DOWN:
			keyCode = KeyEvent.VK_NUMPAD3;
			shift = true; // Winders
			break;

		case KeyEvent.VK_INSERT:
			keyCode = KeyEvent.VK_NUMPAD0;
			shift = true; // Winders
			break;

		// FIXME: We don't handle the typed period case yet.
		case KeyEvent.VK_DELETE:
			keyCode = KeyEvent.VK_PERIOD;
			shift = true; // Winders
			break;

		case KeyEvent.VK_NUMPAD0:
		case KeyEvent.VK_NUMPAD1:
		case KeyEvent.VK_NUMPAD2:
		case KeyEvent.VK_NUMPAD3:
		case KeyEvent.VK_NUMPAD4:
		case KeyEvent.VK_NUMPAD5:
		case KeyEvent.VK_NUMPAD6:
		case KeyEvent.VK_NUMPAD7:
		case KeyEvent.VK_NUMPAD8:
		case KeyEvent.VK_NUMPAD9:
			break;

		default:
			// XXX: We can't be sure the NumpadInputMap will catch
			// all the typable characters we miss.
			//System.out.println(ke.paramString());
			return;
		}

		ke.consume();

		// Locate action.
		final KeyStroke stroke = KeyStroke
		.getKeyStroke(keyCode,
		              ke.getModifiers()
		              | (shift ? Event.SHIFT_MASK : 0),
			      (ke.getID() == KeyEvent.KEY_RELEASED));

		final Object actionKey = inputMap.get(stroke);
		if (actionKey == null)
			return;

		final Action action = target.getActionMap().get(actionKey);
		if (action == null)
			return;

		// Invoke action.
		SwingUtilities.notifyAction(action, stroke, ke, target,
		                            ke.getModifiers());
	}


	//
	// KeyListener interface.
	//

	public void keyPressed (final KeyEvent ke) {
		processKeyEvent(ke);
	}

	public void keyReleased (final KeyEvent ke) {
		processKeyEvent(ke);
	}

	public void keyTyped (final KeyEvent ke) {
		// Typed events are always KEY_LOCATION_UNKNOWN.  This is a bit
		// problematic if we want to intercept numeric pad input when
		// NUMLOCK is activated, since it will also generate typed
		// numbers.
		//
		// We can use NumpadInputMap to solve this problem, although it
		// would be more elegant if we only had to set this listener to
		// do the same thing, perhaps by converting numbers from
		// pressed key events.  However, then we'd have to reimplement
		// the routing logic for InputMaps.  Not fun, especially as the
		// standard Java API evolves in the future.
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
		// Only translate unmodified number keys.
		if (keyStroke.getModifiers() != 0)
			return keyStroke;

		switch (keyStroke.getKeyEventType()) {
		case KeyEvent.KEY_PRESSED:
			// Treat pressed numbers like typed numbers.
			// TODO: It'd be nice to ignore NUMLOCK'd, too.
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
			}
			break;

		case KeyEvent.KEY_TYPED:
			// Drop typed numbers.
			// TODO: Drop other characters typable on the numeric
			// keypad.  Those are less used, though.
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
			}
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
		return (translated == null) ? null : realMap.get(translated);
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
