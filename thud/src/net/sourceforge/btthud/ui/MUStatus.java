//
//  MUStatus.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team.
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.ui;

import net.sourceforge.btthud.ui.status.MUStatusComponent;

import net.sourceforge.btthud.data.MUPrefs;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Implements a status report window that displays heading, speed, heat, and
 * weapon information very similar to the MUX's 'status'.
 * @author tkrajcar
 */
public class MUStatus extends ChildWindow implements ActionListener {
	private final MUStatusComponent status;

	private final Thud thud;

	public MUStatus (final Thud thud) {
		super (thud, "Status Report");

		this.thud = thud;

		status = new MUStatusComponent (thud.prefs);
		window.add(status);

		window.setSize(thud.prefs.statusSizeX, thud.prefs.statusSizeY);
		window.setLocation(thud.prefs.statusLoc);

		window.setAlwaysOnTop(thud.prefs.statusAlwaysOnTop);

		// Show the window now
		window.setVisible(true);
	}

	public void newPreferences (final MUPrefs prefs) {
		super.newPreferences(prefs);
		status.newPreferences(prefs);
		window.setAlwaysOnTop(prefs.statusAlwaysOnTop);
	}

	public void actionPerformed (final ActionEvent ae) {
		status.refresh(thud.data);
	}
}
