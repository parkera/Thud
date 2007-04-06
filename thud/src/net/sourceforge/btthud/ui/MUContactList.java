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

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MUContactList extends ChildWindow implements ActionListener {
	private final MUContactListComponent contactList;

	private final Thud thud;

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
	}

	public void newPreferences (final MUPrefs prefs) {
		super.newPreferences(prefs);
		contactList.newPreferences(prefs);
		window.setAlwaysOnTop(prefs.contactsAlwaysOnTop);
	}

	public void actionPerformed (final ActionEvent ae) {
		contactList.refresh(thud.data);
	}
}
