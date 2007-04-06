//
//  ChildWindow.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team.
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.ui;

import net.sourceforge.btthud.data.MUPrefs;

import java.awt.Window;
import java.awt.Component;
import java.awt.Image;
import java.awt.Point;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JDialog;

/**
 * Base class for child window proxies.
 */
public abstract class ChildWindow {
	private enum WindowType {
		NONE, FRAME, DIALOG
	}

	// Common data.
	private WindowType type = WindowType.NONE;
	protected Window window;
	private final String title;

	// Frame-specific data.
	private final Image icon;

	// Dialog-specific data.
	private final Thud owner;

	// Initialize window parameters.
	protected ChildWindow (final Thud owner, final String title) {
		this.owner = owner;
		this.title = title;
		this.icon = owner.getIconImage();

		config(owner.prefs);
	}

	// Convert JDialog to JFrame, possibly initializing at the same time.
	protected void convertToFrame () {
		if (type == WindowType.FRAME) {
			// Already a JFrame.
			return;
		}

		final JFrame asFrame = new JFrame (title);
		asFrame.setIconImage(icon);

		replaceWindow(asFrame);
	}

	// Convert JFrame to JDialog, possibly initializing at the same time.
	protected void convertToDialog () {
		if (type == WindowType.DIALOG) {
			// Already a JDialog.
			return;
		}

		final JDialog asDialog = new JDialog (owner, title);

		replaceWindow(asDialog);
	}

	private void replaceWindow (final JFrame asFrame) {
		replaceWindow(WindowType.FRAME, asFrame);
	}

	private void replaceWindow (final JDialog asDialog) {
		replaceWindow(WindowType.DIALOG, asDialog);
	}

	private void replaceWindow (final WindowType newType,
	                            final Window newWindow) {
		if (type != WindowType.NONE) {
			// Deactivate old window.
			final boolean wasVisible = window.isVisible();
			window.dispose();

			final Window oldWindow = window;
			type = WindowType.NONE;
			window = null;

			// Copy old window properties.
			newWindow.setSize(oldWindow.getSize());
			newWindow.setLocation(oldWindow.getLocation());

			// Copy old window contents.
			// XXX: This only works for fairly simple layout
			// managers, since it only uses the most basic form of
			// add().
			for (final Component comp: oldWindow.getComponents()) {
				// This is supposed to automatically remove the
				// component, at least in Swing.  Don't know
				// about AWT.
				// TODO: Check the above comment.
				newWindow.add(comp);
			}

			// Activate new window.
			newWindow.setVisible(wasVisible);
		}

		type = newType;
		window = newWindow;
	}

	// Handle preferences common to all child windows.
	public void newPreferences (final MUPrefs prefs) {
		config(prefs);
	}

	private void config (final MUPrefs prefs) {
		if (prefs.childrenAreFrames) {
			convertToFrame();
		} else {
			convertToDialog();
		}
	}


	//
	// Proxy methods.
	//

	public void dispose () {
		window.dispose();
	}

	public void setVisible (boolean visible) {
		window.setVisible(visible);
	}

	public Point getLocation () {
		return window.getLocation();
	}

	public Dimension getSize () {
		return window.getSize();
	}
}
