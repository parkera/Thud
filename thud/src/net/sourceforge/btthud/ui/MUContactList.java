//
//  MUContactList.java
//  Thud
//
//  Created by asp on Wed Nov 28 2001.
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.ui;

import net.sourceforge.btthud.ui.contacts.MUContactListComponent;

import net.sourceforge.btthud.data.MUPrefs;

public class MUContactList extends ChildWindow implements Runnable {
	private final MUContactListComponent contactList;

	private final Thud thud;

	private Thread thread = null;
	private boolean go = true;

	public MUContactList (final Thud thud) {
		super (thud, "Contact List");

		this.thud = thud;

		contactList = new MUContactListComponent (thud.prefs);
		window.add(contactList);

		window.setSize(thud.prefs.contactsSizeX, thud.prefs.contactsSizeY);
		window.setLocation(thud.prefs.contactsLoc);

		window.setAlwaysOnTop(thud.prefs.contactsAlwaysOnTop);

		// Show the window now
		window.setVisible(true);

		start();
	}

	public void newPreferences (final MUPrefs prefs) {
		super.newPreferences(prefs);
		contactList.newPreferences(prefs);
		window.setAlwaysOnTop(prefs.contactsAlwaysOnTop);
	}

	private void start () {
		if (thread == null) {
			thread = new Thread (this, "MUContactList");
			thread.start();
		}
	}

	public void run () {
		while (go) {
			try {
				// TODO: Refresh only after we get new data.
				synchronized (thud.data) {
					contactList.refresh(thud.data);
				}

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
