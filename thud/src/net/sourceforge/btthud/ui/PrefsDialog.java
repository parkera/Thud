//
//  PrefsDialog.java
//  Thud
//
//  Created by Anthony Parker on April 10, 2002, 2:26 AM.
//  Copyright (c) 2001-2007 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.ui;

import net.sourceforge.btthud.data.*;
import net.sourceforge.btthud.util.*;

import java.awt.*;
import javax.swing.*;
import java.util.*;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PrefsDialog extends JDialog {

	private Thud thud;
	private MUPrefs prefs = null;

	/** Creates new form PrefsDialog */
	public PrefsDialog (Thud thud) {
		super(thud, "Thud Preferences", true);

		this.thud = thud;

		addWindowListener(new WindowAdapter () {
			public void windowClosing (WindowEvent we) {
				closeDialog();
			}
		});

		createComponents();
	}


	//
	// Window handling.
	//

	/**
	 * Same semantics as setVisible(), pretty much, except with extra
	 * internal preferences-handling sauce.
	 */
	public void setVisible (boolean visible) {
		if (visible && !isVisible()) {
			// Show "new" preferences dialog.
			openDialog();
		} else {
			// This normally doesn't happen, because PrefsDialog is
			// modal, but we'll honor it if it ever happens for
			// some bizarre reason.
			//
			// TODO: Figure out if we need to do more than this.
			super.setVisible(visible);
		}
	}

	/** Opens the dialog.  */
	private void openDialog () {
		// Get a working copy of preferences.
		try {
			prefs = (MUPrefs)thud.prefs.clone();
		} catch (Exception e) {
			System.err.println("Error: prefsDialog: " + e);
			return;
		}

		// Update components with preferences-specific values.
		updateComponents();

		// Note that this may make the dialog displayable as a side
		// effect; we might want to let the user of this class do this
		// themselves.  In practice, they'll pretty much do this almost
		// immediately anyway.
		pack();

		setLocationByPlatform(true);

		super.setVisible(true);
	}

	/** Closes the dialog.  */
	private void closeDialog () {
		super.setVisible(false);

		prefs = null;
	}


	//
	// Subcomponent setup.
	//

	// "General" tab widgets.
	private AbstractButton echoCheckBox;
	private AbstractButton antiAliasTextCheckBox;
	private AbstractButton highlightMyHexCheckBox;

	private JComboBox speedLengthBox;
	private JComboBox scrollbackSizeBox;
	private JComboBox contactsAgeBox;

	// "Map Colors" tab widgets.
	private AbstractButton bTerrainColors[] = new JButton[MUHex.TOTAL_TERRAIN];

	// "Font" tab widgets.
	private JComboBox mainFontBox;
	private JComboBox mainSizeBox;
	private JComboBox contactsSizeBox;
	private JComboBox statusSizeBox;
	private JComboBox tacStatusBarSizeBox;
	private JComboBox contactsOnMapSizeBox;
	private JComboBox elevationsSizeBox;
	private JComboBox hexNumberSizeBox;

	// "Window" tab widgets.
	private AbstractButton mainAlwaysOnTopCheckBox;
	private AbstractButton contactsAlwaysOnTopCheckBox;
	private AbstractButton statusAlwaysOnTopCheckBox;
	private AbstractButton tacticalAlwaysOnTopCheckBox;

	private void createComponents () {
		//
		// Tabs.
		//

		final JComponent tabbedPane = new JTabbedPane ();

		// "General" tab.
		final JComponent generalTab = new JPanel ();

		generalTab.setName("General");
		generalTab.setLayout(new BoxLayout (generalTab, BoxLayout.PAGE_AXIS));

		// Top of "General" tab.
		final JComponent generalTabTop = Box.createHorizontalBox();

		echoCheckBox = new JCheckBox ("Echo Commands");
		generalTabTop.add(echoCheckBox);

		// Does this work? No idea...
		antiAliasTextCheckBox = new JCheckBox ("Antialias Text");
		generalTabTop.add(antiAliasTextCheckBox);

		highlightMyHexCheckBox = new JCheckBox ("Highlight My Hex");
		generalTabTop.add(highlightMyHexCheckBox);

		generalTab.add(generalTabTop);

		// Bottom of "General" tab.
		final JComponent generalTabBottom = new JPanel (new GridLayout (0, 2));

		generalTabBottom.add(new JLabel ("Speed Indicator Divisor"));

		speedLengthBox = new JComboBox ();
		speedLengthBox.addItem(new Float (1.0));
		speedLengthBox.addItem(new Float (1.5));
		speedLengthBox.addItem(new Float (2.0));
		speedLengthBox.addItem(new Float (2.5));
		speedLengthBox.addItem(new Float (3.0));
		speedLengthBox.addItem(new Float (3.5));
		speedLengthBox.addItem(new Float (4.0));
		generalTabBottom.add(speedLengthBox);

		generalTabBottom.add(new JLabel ("Lines of Text in Scrollback"));

		scrollbackSizeBox = new JComboBox ();
		scrollbackSizeBox.addItem(new Integer (500));
		scrollbackSizeBox.addItem(new Integer (1000));
		scrollbackSizeBox.addItem(new Integer (2000));
		scrollbackSizeBox.addItem(new Integer (5000));
		scrollbackSizeBox.addItem(new Integer (7500));
		scrollbackSizeBox.addItem(new Integer (10000));
		scrollbackSizeBox.addItem(new Integer (20000));
		generalTabBottom.add(scrollbackSizeBox);

		generalTabBottom.add(new JLabel ("Time to Keep Old Contacts (sec)"));

		contactsAgeBox = new JComboBox ();
		contactsAgeBox.addItem(new Integer (5));
		contactsAgeBox.addItem(new Integer (10));
		contactsAgeBox.addItem(new Integer (15));
		contactsAgeBox.addItem(new Integer (20));
		contactsAgeBox.addItem(new Integer (25));
		contactsAgeBox.addItem(new Integer (30));
		contactsAgeBox.addItem(new Integer (35));
		contactsAgeBox.addItem(new Integer (40));
		contactsAgeBox.addItem(new Integer (45));
		contactsAgeBox.addItem(new Integer (50));
		contactsAgeBox.addItem(new Integer (55));
		contactsAgeBox.addItem(new Integer (60));
		generalTabBottom.add(contactsAgeBox);

		generalTab.add(generalTabBottom);

		tabbedPane.add(generalTab);

		// "Map Colors" tab.
		final JComponent mapColorsTab = new JPanel (new GridLayout (0, 2));
		mapColorsTab.setName("Map Colors");

		for (int i = 0; i < MUHex.TOTAL_TERRAIN; i++) {
			bTerrainColors[i] = new JButton ();

			bTerrainColors[i].setText(MUHex.terrainForId(i) + " " + MUHex.nameForId(i));
			bTerrainColors[i].addActionListener(new ActionListener () {
				public void actionPerformed (final ActionEvent ae) {
					bTerrainColorActionPerformed(ae);
				}
			});

			mapColorsTab.add(bTerrainColors[i]);
		}

		tabbedPane.add(mapColorsTab);

		// "Fonts" tab.
		final JComponent fontTab = new JPanel (new GridLayout (0, 2));
		fontTab.setName("Fonts");

		fontTab.add(new JLabel ("Font for All Windows"));

		mainFontBox = new JComboBox ();
		mainFontBox.addItem("Monospaced");
		mainFontBox.addItem("Serif");
		mainFontBox.addItem("SansSerif");

		for (final Font font: GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts())
			mainFontBox.addItem(new String(font.getFontName()));

		fontTab.add(mainFontBox);

		fontTab.add(new JLabel ("Main Window Font Size"));

		mainSizeBox = new JComboBox ();
		addFontSizeMenus(mainSizeBox);
		fontTab.add(mainSizeBox);

		fontTab.add(new JLabel ("Contacts Window Font Size"));

		contactsSizeBox = new JComboBox ();
		addFontSizeMenus(contactsSizeBox);
		fontTab.add(contactsSizeBox);

		fontTab.add(new JLabel ("Status Window Font Size"));

		statusSizeBox = new JComboBox ();
		addFontSizeMenus(statusSizeBox);
		fontTab.add(statusSizeBox);

		fontTab.add(new JLabel ("Contacts on Map Font Size"));

		contactsOnMapSizeBox = new JComboBox ();
		addFontSizeMenus(contactsOnMapSizeBox);
		fontTab.add(contactsOnMapSizeBox);

		fontTab.add(new JLabel ("Elevations on Map Size"));

		elevationsSizeBox = new JComboBox ();
		addFontSizeMenus(elevationsSizeBox);
		fontTab.add(elevationsSizeBox);

		fontTab.add(new JLabel ("Hex Numbers on Map Size"));

		hexNumberSizeBox = new JComboBox ();
		addFontSizeMenus(hexNumberSizeBox);
		fontTab.add(hexNumberSizeBox);

		fontTab.add(new JLabel ("Map Status Bar Size"));

		tacStatusBarSizeBox = new JComboBox ();
		addFontSizeMenus(tacStatusBarSizeBox);
		fontTab.add(tacStatusBarSizeBox);

		tabbedPane.add(fontTab);

		// "Window" tab.
		final JComponent windowTab = new JPanel ();
		windowTab.setLayout(new BoxLayout (windowTab, BoxLayout.PAGE_AXIS));
		windowTab.setName("Window");

		mainAlwaysOnTopCheckBox = new JCheckBox ("Main Window Always On Top");
		windowTab.add(mainAlwaysOnTopCheckBox);

		contactsAlwaysOnTopCheckBox = new JCheckBox ("Contacts Window Always On Top");
		windowTab.add(contactsAlwaysOnTopCheckBox);

		statusAlwaysOnTopCheckBox = new JCheckBox ("Status Window Always On Top");
		windowTab.add(statusAlwaysOnTopCheckBox);

		tacticalAlwaysOnTopCheckBox = new JCheckBox ("Tactical Window Always On Top");
		windowTab.add(tacticalAlwaysOnTopCheckBox);

		tabbedPane.add(windowTab);

		getContentPane().add(tabbedPane);


		//
		// Buttons.
		//

		final JPanel buttonPanel = new JPanel (new FlowLayout (FlowLayout.TRAILING));

		// TODO: Convert to Action?
		final JButton cancelButton = new JButton ("Cancel");

		cancelButton.addActionListener(new ActionListener () {
			public void actionPerformed (final ActionEvent ae) {
				closeDialog();
			}
		});

		buttonPanel.add(cancelButton);

		// TODO: Convert to Action?
		final JButton saveButton = new JButton ("Save");

		saveButton.addActionListener(new ActionListener () {
			public void actionPerformed (final ActionEvent ae) {
				doSave();
			}
		});

		buttonPanel.add(saveButton);

		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
	}

	private void updateComponents () {
		// "General" preferences.
		echoCheckBox.setSelected(prefs.echoCommands);
		antiAliasTextCheckBox.setSelected(prefs.antiAliasText);
		highlightMyHexCheckBox.setSelected(prefs.highlightMyHex);

		speedLengthBox.setSelectedItem(new Float (prefs.speedIndicatorLength));
		scrollbackSizeBox.setSelectedItem(new Integer (prefs.maxScrollbackSize));
		contactsAgeBox.setSelectedItem(new Integer (prefs.contactsAge));

		// "Map Colors" preferences.
		for (int i = 0; i < MUHex.TOTAL_TERRAIN; i++)
			bTerrainColors[i].setIcon(new ColorWellIcon (prefs.terrainColors[i]));

		// "Font" preferences.
		mainFontBox.setSelectedItem(prefs.mainFont);
		mainSizeBox.setSelectedItem(new Integer (prefs.mainFontSize));
		contactsSizeBox.setSelectedItem(new Integer (prefs.contactFontSize));
		statusSizeBox.setSelectedItem(new Integer (prefs.statusFontSize));
		contactsOnMapSizeBox.setSelectedItem(new Integer (prefs.infoFontSize));
		elevationsSizeBox.setSelectedItem(new Integer (prefs.elevationFontSize));
		hexNumberSizeBox.setSelectedItem(new Integer (prefs.hexNumberFontSize));
		tacStatusBarSizeBox.setSelectedItem(new Integer (prefs.tacStatusFontSize));

		// "Window" preferences.
		mainAlwaysOnTopCheckBox.setSelected(prefs.mainAlwaysOnTop);
		contactsAlwaysOnTopCheckBox.setSelected(prefs.contactsAlwaysOnTop);
		statusAlwaysOnTopCheckBox.setSelected(prefs.statusAlwaysOnTop);
		tacticalAlwaysOnTopCheckBox.setSelected(prefs.tacticalAlwaysOnTop);
	}


	//
	// Subcomponent actions.
	//

	private void doSave () {
		prefs.echoCommands = echoCheckBox.isSelected();
		prefs.highlightMyHex = highlightMyHexCheckBox.isSelected();
		prefs.antiAliasText = antiAliasTextCheckBox.isSelected();

		prefs.mainFont = ((String)mainFontBox.getSelectedItem());
		prefs.mainFontSize = ((Integer)mainSizeBox.getSelectedItem()).intValue();
		prefs.statusFontSize = ((Integer)statusSizeBox.getSelectedItem()).intValue();
		prefs.contactFontSize = ((Integer)contactsSizeBox.getSelectedItem()).intValue();
		prefs.infoFontSize = ((Integer)contactsOnMapSizeBox.getSelectedItem()).intValue();
		prefs.elevationFontSize = ((Integer)elevationsSizeBox.getSelectedItem()).intValue();
		prefs.hexNumberFontSize = ((Integer)hexNumberSizeBox.getSelectedItem()).intValue();
		prefs.tacStatusFontSize = ((Integer)tacStatusBarSizeBox.getSelectedItem()).intValue();

		prefs.speedIndicatorLength = ((Float)speedLengthBox.getSelectedItem()).floatValue();
		prefs.maxScrollbackSize = ((Integer)scrollbackSizeBox.getSelectedItem()).intValue();
		prefs.contactsAge = ((Integer)contactsAgeBox.getSelectedItem()).intValue();

		prefs.mainAlwaysOnTop = mainAlwaysOnTopCheckBox.isSelected();
		prefs.contactsAlwaysOnTop = contactsAlwaysOnTopCheckBox.isSelected();
		prefs.statusAlwaysOnTop = statusAlwaysOnTopCheckBox.isSelected();
		prefs.tacticalAlwaysOnTop = tacticalAlwaysOnTopCheckBox.isSelected();

		thud.prefs = prefs;

		closeDialog();
	}

	// -----------------------
	// These are the action handlers for the map colors

	// TODO: The only data structures in MUPrefs that we're actually using
	// are the terrain colors.  We don't really need to clone() the rest of
	// it, as we store that state in the various Swing components
	// themselves (see doSave()).
	private void bTerrainColorActionPerformed (final ActionEvent ae) {
		final StringTokenizer st = new StringTokenizer (ae.getActionCommand());
		final int whichTerrain = MUHex.idForTerrain(st.nextToken().charAt(0));
        
		final Color newColor = JColorChooser.showDialog(this, "Unknown Hex Color", prefs.terrainColors[whichTerrain]);

		if (newColor != null) {
			prefs.terrainColors[whichTerrain] = newColor;
			bTerrainColors[whichTerrain].setIcon(new ColorWellIcon(prefs.terrainColors[whichTerrain]));
		}
	}

	// Add standard font size menu items to a combo box.
	private void addFontSizeMenus (final JComboBox theBox) {
		theBox.addItem(new Integer (9));
		theBox.addItem(new Integer (10));
		theBox.addItem(new Integer (11));
		theBox.addItem(new Integer (12));
		theBox.addItem(new Integer (14));
		theBox.addItem(new Integer (16));
		theBox.addItem(new Integer (18));
		theBox.addItem(new Integer (20));
		theBox.addItem(new Integer (24));
		theBox.addItem(new Integer (32));
	}
}
