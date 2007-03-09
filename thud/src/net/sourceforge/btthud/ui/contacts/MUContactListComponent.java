//
//  MUContactListComponent.java
//  Thud
//
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team.
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.ui.contacts;

import net.sourceforge.btthud.data.MUData;
import net.sourceforge.btthud.data.MUPrefs;
import net.sourceforge.btthud.util.BulkStyledDocument;

import net.sourceforge.btthud.data.MUUnitInfo;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.DefaultStyledDocument.ElementSpec;

import java.util.Iterator;
import java.util.ArrayList;

public class MUContactListComponent extends JScrollPane {
	private MUPrefs prefs;

	private final JTextPane contactPane;

	private Font mFont;

	private MutableAttributeSet conRegular;
	private final ArrayList<ElementSpec> elements = new ArrayList<ElementSpec> ();

	public MUContactListComponent (final MUPrefs prefs) {
		super (VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);

		// Setup our new contact list pane
		contactPane = new JTextPane ();
		contactPane.setBackground(Color.black);
		contactPane.setEditable(false);

		newPreferences(prefs);

		contactPane.setStyledDocument(new BulkStyledDocument (prefs.contactFontSize, 1000, mFont)); // Yes, max of 1000 contacts. So sue me.

		setViewportView(contactPane);
	}

	private void initAttributeSets () {
		conRegular = new SimpleAttributeSet ();
		StyleConstants.setFontFamily(conRegular, prefs.mainFont);
		StyleConstants.setFontSize(conRegular, prefs.contactFontSize);
		StyleConstants.setForeground(conRegular, Color.white);
	}

	public void newPreferences (final MUPrefs prefs) {
		this.prefs = prefs;
		mFont = new Font (prefs.mainFont, Font.PLAIN, prefs.contactFontSize);
		contactPane.setFont(mFont);
		initAttributeSets();
	}

	public void refresh (final MUData data) {
		if (!data.hudRunning)
			return;

		final BulkStyledDocument doc = (BulkStyledDocument)contactPane.getDocument();

		final Iterator contacts = data.getContactsIterator(true); // Sorted list

		elements.clear();

		while (contacts.hasNext()) {
			final MUUnitInfo unit = (MUUnitInfo)contacts.next();

			addBlankLine();

			final SimpleAttributeSet whichAttrs = new SimpleAttributeSet(conRegular);

			// Style contact line.
			if (unit.isFriend()) {
				if (!unit.isOld()) {
					StyleConstants.setForeground(whichAttrs, Color.white);
				} else {
					StyleConstants.setForeground(whichAttrs, Color.gray);
				}
			} else {
				StyleConstants.setForeground(whichAttrs, Color.yellow);

				if (!unit.isOld()) {
					StyleConstants.setBold(whichAttrs, true);
				}
			}

			if (unit.isTarget())
				StyleConstants.setForeground(whichAttrs, Color.red);

			if (unit.isDestroyed())
				StyleConstants.setStrikeThrough(whichAttrs, true);

			addString(unit.makeContactString(), whichAttrs);
		}

		doc.clearAndInsertParsedString(elements);

		// Don't scroll
		// contactPane.setCaretPosition(doc.getLength());
	}

	/**
	 * Adds a blank line to ArrayList elements and sends it back. Used to
	 * eliminate code duplication
	 *
	 * @param elements ArrayList to append blank line elements to
	 */
	private void addBlankLine () {
		elements.add(new ElementSpec (conRegular, ElementSpec.EndTagType));
		elements.add(new ElementSpec (conRegular, ElementSpec.StartTagType));
	}

	/**
	 * Adds a given line to ArrayList elements and sends it back. Used to
	 * eliminate code duplication
	 *
	 * @param elements ArrayList to append blank line elements to
	 * @param s String of text to append
	 * @param attrs Attributes to use when adding string
	 */
	private void addString (String s, MutableAttributeSet attrs) {
		elements.add(new ElementSpec (new SimpleAttributeSet (attrs),
		                              ElementSpec.ContentType,
		                              s.toCharArray(), 0, s.length()));
	}
}
