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

public class MUTacticalMap extends ChildWindow implements Runnable {
	private final MUMapComponent map;

	private final Thud thud;

	private Thread thread = null;
	private boolean go = true;

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

		start();
	}

	public void newPreferences (final MUPrefs prefs) {
		super.newPreferences(prefs);
		window.setAlwaysOnTop(prefs.tacticalAlwaysOnTop);
		map.newPreferences(prefs);
	}

	public void start () {
		if (thread == null) {
			thread = new Thread (this, "MUTacticalMap");
			thread.start();
		}
	}

	public void run () {
		while (go) {
			try {
				synchronized (thud.data) {
					// TODO: Make MUMapComponent only
					// access MUData at this well-defined
					// point.  This will let us consolidate
					// all the refresh() procedures.
					map.refresh(thud.data);
				}

				// TODO: Refresh only after we get new data.
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// no big deal
			}
		}
	}

	public void pleaseStop () {
		go = false;
		window.dispose();
	}
}
