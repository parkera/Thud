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

import javax.swing.JFrame;

public class MUContactList extends JFrame implements Runnable {
	private final MUContactListComponent contactList;

	private final Thud thud;

	private Thread thread = null;
	private boolean go = true;

	public MUContactList (final Thud thud) {
		super ("Contact List");
		setIconImage(thud.getIconImage());

		this.thud = thud;

		contactList = new MUContactListComponent (thud.prefs);
		add(contactList);

		setSize(thud.prefs.contactsSizeX, thud.prefs.contactsSizeY);
		setLocation(thud.prefs.contactsLoc);

		setAlwaysOnTop(thud.prefs.contactsAlwaysOnTop);

		// Show the window now
		setVisible(true);

		start();
	}

	public void newPreferences (final MUPrefs prefs) {
		contactList.newPreferences(prefs);
		setAlwaysOnTop(prefs.contactsAlwaysOnTop);
	}

	private void start () {
		if (thread == null) {
			thread = new Thread(this, "MUContactList");
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
		dispose();
	}
}
