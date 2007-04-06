//
//  MUTacticalMap.java
//  Thud
//
//  Created by asp on Wed Nov 28 2001.
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.ui;

import net.sourceforge.btthud.ui.map.MUMapComponent;

import net.sourceforge.btthud.data.MUPrefs;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MUTacticalMap extends ChildWindow implements ActionListener {
	private final MUMapComponent map;

	private final Thud thud;

	public MUTacticalMap (final Thud thud) {
		super (thud, "Tactical Map");

		this.thud = thud;

		map = new MUMapComponent (thud.data, thud.prefs);
		window.add(map);

		window.setSize(thud.prefs.tacSizeX, thud.prefs.tacSizeY);
		window.setLocation(thud.prefs.tacLoc);

		window.setAlwaysOnTop(thud.prefs.tacticalAlwaysOnTop);

		// Show the window now
		window.setVisible(true);
	}

	public void newPreferences (final MUPrefs prefs) {
		super.newPreferences(prefs);
		window.setAlwaysOnTop(prefs.tacticalAlwaysOnTop);
		map.newPreferences(prefs);
	}

	public void actionPerformed (final ActionEvent ae) {
		// TODO: Make MUMapComponent only access MUData at this
		// well-defined point.  This will let us consolidate all the
		// refresh() procedures.
		// TODO: Ensure this is only called with sync at thud.data.
		map.refresh(thud.data);
	}
}
