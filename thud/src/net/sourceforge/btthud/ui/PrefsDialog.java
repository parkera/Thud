//
//  PrefsDialog.java
//  Thud
//
//  Created by Anthony Parker on April 10, 2002, 2:26 AM.
//  Copyright (c) 2001-2006 Anthony Parker & the THUD team. 
//  All rights reserved. See LICENSE.TXT for more information.
//
package net.sourceforge.btthud.ui;

import net.sourceforge.btthud.data.*;
import net.sourceforge.btthud.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.util.*;

public class PrefsDialog extends JDialog {
    
    private MUPrefs     prefs = null;
    private Thud	thud = null;

    private javax.swing.JTabbedPane 	TabbedPane;

    private javax.swing.JPanel 			GeneralOptionsTab;
    private javax.swing.JCheckBox 		echoCheckBox;
    private javax.swing.JCheckBox		antiAliasTextCheckBox;
    private javax.swing.JCheckBox 		highlightMyHexCheckBox;
    private javax.swing.JLabel 			speedLengthLabel;
    private javax.swing.JComboBox 		speedLengthBox;
    private javax.swing.JLabel 			scrollbackSizeLabel;
    private javax.swing.JComboBox 		scrollbackSizeBox;
    private javax.swing.JLabel			contactsAgeLabel;
    private javax.swing.JComboBox		contactsAgeBox;

    private javax.swing.JPanel 			MapColorsTab;
    private javax.swing.JButton 		bTerrainColors[] = new javax.swing.JButton[MUHex.TOTAL_TERRAIN];

    private javax.swing.JPanel 			FontTab;
    private javax.swing.JLabel			mainFontLabel;
    private javax.swing.JLabel 			mainLabel;
    private javax.swing.JComboBox       	mainFontBox;
    private javax.swing.JComboBox 		mainSizeBox;
    private javax.swing.JLabel 			contactsLabel;
    private javax.swing.JComboBox 		contactsSizeBox;
    private javax.swing.JLabel			statusLabel;
    private javax.swing.JComboBox		statusSizeBox;
    private javax.swing.JLabel			tacStatusBarLabel;
    private javax.swing.JComboBox		tacStatusBarSizeBox;
    private javax.swing.JLabel 			contactsOnMapLabel;
    private javax.swing.JComboBox 		contactsOnMapSizeBox;
    private javax.swing.JLabel 			elevationsLabel;
    private javax.swing.JComboBox 		elevationsSizeBox;
    private javax.swing.JLabel 			hexNumbersLabel;
    private javax.swing.JComboBox 		hexNumberSizeBox;

    private javax.swing.JPanel			WindowTab;
    private javax.swing.JCheckBox		mainAlwaysOnTopCheckBox;
    private javax.swing.JCheckBox		contactsAlwaysOnTopCheckBox;
    private javax.swing.JCheckBox		statusAlwaysOnTopCheckBox;
    private javax.swing.JCheckBox		tacticalAlwaysOnTopCheckBox;
    
    private javax.swing.JButton 		CancelButton;
    private javax.swing.JButton 		SaveButton;

    private javax.swing.JLabel			nullLabel;
    
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    Font[] fonts = ge.getAllFonts();
    
	/** Creates new form PrefsDialog */
	public PrefsDialog (Thud thud) {
		super(thud, true);

		this.thud = thud;

		if (thud.prefs == null)
			prefs = new MUPrefs();

		try {
			prefs = (MUPrefs)thud.prefs.clone();
		} catch (Exception e) {
			System.err.println("Error: prefsDialog: " + e);
		}

		initComponents();
		setMapColorIcons();

		// Note that this may make the dialog displayable as a side
		// effect; we might want to let the user of this class do this
		// themselves.  In practice, they'll pretty much do this almost
		// immediately anyway.
		pack();

		setLocationByPlatform(true);
	}

    private void initComponents() {
        TabbedPane = new javax.swing.JTabbedPane();
        
        GeneralOptionsTab = new javax.swing.JPanel();
        speedLengthLabel = new javax.swing.JLabel();
        speedLengthBox = new javax.swing.JComboBox();
        scrollbackSizeLabel = new javax.swing.JLabel();
        scrollbackSizeBox = new javax.swing.JComboBox();
        contactsAgeLabel = new javax.swing.JLabel();
        contactsAgeBox = new javax.swing.JComboBox();

        MapColorsTab = new javax.swing.JPanel();
        for (int i = 0; i < MUHex.TOTAL_TERRAIN; i++)
            bTerrainColors[i] = new javax.swing.JButton();
        
        FontTab = new javax.swing.JPanel();
        mainFontLabel = new javax.swing.JLabel();
        mainFontBox = new javax.swing.JComboBox();
        mainLabel = new javax.swing.JLabel();
        mainSizeBox = new javax.swing.JComboBox();
        contactsLabel = new javax.swing.JLabel();
        contactsSizeBox = new javax.swing.JComboBox();
        contactsOnMapLabel = new javax.swing.JLabel();
        contactsOnMapSizeBox = new javax.swing.JComboBox();
        elevationsLabel = new javax.swing.JLabel();
        elevationsSizeBox = new javax.swing.JComboBox();
        hexNumbersLabel = new javax.swing.JLabel();
        hexNumberSizeBox = new javax.swing.JComboBox();
        statusLabel = new javax.swing.JLabel();
        statusSizeBox = new javax.swing.JComboBox();
        tacStatusBarLabel = new javax.swing.JLabel();
        tacStatusBarSizeBox = new javax.swing.JComboBox();
        
        WindowTab = new JPanel();
                
        nullLabel = new javax.swing.JLabel();
        
        CancelButton = new javax.swing.JButton();
        SaveButton = new javax.swing.JButton();

        // Set the size of our dialog box, and some other stuff
        setTitle("Thud Preferences");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Thud Preferences");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        
        // --- GENERAL OPTIONS ---
        JPanel generalOptionsTopPanel = new JPanel();        
        generalOptionsTopPanel.setLayout(new GridLayout(0,3));        
        JPanel generalOptionsBottomPanel = new JPanel();
        generalOptionsBottomPanel.setLayout(new GridLayout(0,2));
        
        echoCheckBox = new javax.swing.JCheckBox("Echo Commands", null, prefs.echoCommands);
        echoCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                echoCheckBoxActionPerformed(evt);
            }
        });
        generalOptionsTopPanel.add(echoCheckBox);

        // Does this work? No idea...
        antiAliasTextCheckBox = new javax.swing.JCheckBox("Antialias Text", null, prefs.antiAliasText);
        antiAliasTextCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                antiAliasTextCheckBoxActionPerformed(evt);
            }
        });
        generalOptionsTopPanel.add(antiAliasTextCheckBox);
        
        highlightMyHexCheckBox = new javax.swing.JCheckBox("Highlight My Hex", null, prefs.highlightMyHex);
        highlightMyHexCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                highlightMyHexCheckBoxActionPerformed(evt);
            }
        });
        generalOptionsTopPanel.add(highlightMyHexCheckBox);
        
        speedLengthLabel.setText("Speed Indicator Divisor");
        generalOptionsBottomPanel.add(speedLengthLabel);
        
        speedLengthBox.addItem(new Float(1.0));
        speedLengthBox.addItem(new Float(1.5));
        speedLengthBox.addItem(new Float(2.0));
        speedLengthBox.addItem(new Float(2.5));
        speedLengthBox.addItem(new Float(3.0));
        speedLengthBox.addItem(new Float(3.5));
        speedLengthBox.addItem(new Float(4.0));
        speedLengthBox.setSelectedItem(new Float(prefs.speedIndicatorLength));
        generalOptionsBottomPanel.add(speedLengthBox);

        scrollbackSizeLabel.setText("Lines of Text in Scrollback");
        generalOptionsBottomPanel.add(scrollbackSizeLabel);
        
        scrollbackSizeBox.addItem(new Integer(500));        
        scrollbackSizeBox.addItem(new Integer(1000));
        scrollbackSizeBox.addItem(new Integer(2000));
        scrollbackSizeBox.addItem(new Integer(5000));
        scrollbackSizeBox.addItem(new Integer(7500));
        scrollbackSizeBox.addItem(new Integer(10000));
        scrollbackSizeBox.addItem(new Integer(20000));
        scrollbackSizeBox.setSelectedItem(new Integer(prefs.maxScrollbackSize));
        generalOptionsBottomPanel.add(scrollbackSizeBox);
        
        contactsAgeLabel.setText("Time to Keep Old Contacts (sec)");
        generalOptionsBottomPanel.add(contactsAgeLabel);
        
        contactsAgeBox.addItem(new Integer(5));
        contactsAgeBox.addItem(new Integer(10));
        contactsAgeBox.addItem(new Integer(15));
        contactsAgeBox.addItem(new Integer(20));
        contactsAgeBox.addItem(new Integer(25));
        contactsAgeBox.addItem(new Integer(30));
        contactsAgeBox.addItem(new Integer(35));
        contactsAgeBox.addItem(new Integer(40));
        contactsAgeBox.addItem(new Integer(45));
        contactsAgeBox.addItem(new Integer(50));
        contactsAgeBox.addItem(new Integer(55));
        contactsAgeBox.addItem(new Integer(60));
        contactsAgeBox.setSelectedItem(new Integer(prefs.contactsAge));        
        generalOptionsBottomPanel.add(contactsAgeBox);
        
        GeneralOptionsTab.add(generalOptionsTopPanel);
        GeneralOptionsTab.add(generalOptionsBottomPanel);
        TabbedPane.addTab("General", GeneralOptionsTab);

        // --- MAP COLOR OPTIONS ---
        MapColorsTab.setLayout(new java.awt.GridLayout(0, 2));

        for (int i = 0; i < MUHex.TOTAL_TERRAIN; i++)
        {
            bTerrainColors[i].setText(MUHex.terrainForId(i) + " " + MUHex.nameForId(i));
            bTerrainColors[i].addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    bTerrainColorActionPerformed(evt);
                }
            });

            MapColorsTab.add(bTerrainColors[i]);
        }
                                                
        TabbedPane.addTab("Map Colors", MapColorsTab);

        // --- FONT OPTIONS ---
        FontTab.setLayout(new GridLayout(0, 2));
        
        mainFontLabel.setText("Font for All Windows");
        FontTab.add(mainFontLabel);
        mainFontBox.addItem("Monospaced");
        mainFontBox.addItem("Serif");
        mainFontBox.addItem("SansSerif");        
        for(int i=0; i< fonts.length; i++) 
        	mainFontBox.addItem(new String(fonts[i].getFontName()));        
        mainFontBox.setSelectedItem(prefs.mainFont);
        FontTab.add(mainFontBox);
        
        mainLabel.setText("Main Window Font Size");
        FontTab.add(mainLabel);
        addFontSizeMenus(mainSizeBox);
        mainSizeBox.setSelectedItem(new Integer(prefs.mainFontSize));
        FontTab.add(mainSizeBox);
        
        contactsLabel.setText("Contacts Window Font Size");
        FontTab.add(contactsLabel);
        addFontSizeMenus(contactsSizeBox);
        contactsSizeBox.setSelectedItem(new Integer(prefs.contactFontSize));
        FontTab.add(contactsSizeBox);
        
        statusLabel.setText("Status Window Font Size");
        FontTab.add(statusLabel);
        addFontSizeMenus(statusSizeBox);
        statusSizeBox.setSelectedItem(new Integer(prefs.statusFontSize));
        FontTab.add(statusSizeBox);
        
        contactsOnMapLabel.setText("Contacts on Map Font Size");
        FontTab.add(contactsOnMapLabel);
        addFontSizeMenus(contactsOnMapSizeBox);
        contactsOnMapSizeBox.setSelectedItem(new Integer(prefs.infoFontSize));
        FontTab.add(contactsOnMapSizeBox);
        
        elevationsLabel.setText("Elevations on Map Size");
        FontTab.add(elevationsLabel);
        addFontSizeMenus(elevationsSizeBox);
        elevationsSizeBox.setSelectedItem(new Integer(prefs.elevationFontSize));
        FontTab.add(elevationsSizeBox);

        hexNumbersLabel.setText("Hex Numbers on Map Size");
        FontTab.add(hexNumbersLabel);
        addFontSizeMenus(hexNumberSizeBox);
        hexNumberSizeBox.setSelectedItem(new Integer(prefs.hexNumberFontSize));
        FontTab.add(hexNumberSizeBox);
        
        tacStatusBarLabel.setText("Map Status Bar Size");
        FontTab.add(tacStatusBarLabel);
        addFontSizeMenus(tacStatusBarSizeBox);
        tacStatusBarSizeBox.setSelectedItem(new Integer(prefs.tacStatusFontSize));
        FontTab.add(tacStatusBarSizeBox);
        
        TabbedPane.addTab("Fonts", FontTab);
        
        // --- WINDOW OPTIONS ---
        WindowTab.setLayout(new GridLayout(4,1));
        mainAlwaysOnTopCheckBox = new JCheckBox("Main Window Always On Top",null,prefs.mainAlwaysOnTop);
        mainAlwaysOnTopCheckBox.addActionListener(new ActionListener()  {
        	public void actionPerformed(ActionEvent evt) {
        		mainAlwaysOnTopCheckBoxActionPerformed(evt);
        	}
        });
        WindowTab.add(mainAlwaysOnTopCheckBox);
        
        contactsAlwaysOnTopCheckBox = new JCheckBox("Contacts Window Always On Top",null,prefs.contactsAlwaysOnTop);
        contactsAlwaysOnTopCheckBox.addActionListener(new ActionListener()  {
        	public void actionPerformed(ActionEvent evt) {
        		contactsAlwaysOnTopCheckBoxActionPerformed(evt);
        	}
        });
        WindowTab.add(contactsAlwaysOnTopCheckBox);
        
        statusAlwaysOnTopCheckBox = new JCheckBox("Status Window Always On Top",null,prefs.statusAlwaysOnTop);
        statusAlwaysOnTopCheckBox.addActionListener(new ActionListener()  {
        	public void actionPerformed(ActionEvent evt) {
        		statusAlwaysOnTopCheckBoxActionPerformed(evt);
        	}
        });
        WindowTab.add(statusAlwaysOnTopCheckBox);

        tacticalAlwaysOnTopCheckBox = new JCheckBox("Tactical Window Always On Top",null,prefs.tacticalAlwaysOnTop);
        tacticalAlwaysOnTopCheckBox.addActionListener(new ActionListener()  {
        	public void actionPerformed(ActionEvent evt) {
        		tacticalAlwaysOnTopCheckBoxActionPerformed(evt);
        	}
        });
        WindowTab.add(tacticalAlwaysOnTopCheckBox);

        TabbedPane.add("Window",WindowTab);
        
        // done with tabs
        getContentPane().add(TabbedPane);
        
        //
        // Buttons.
        //
        final JPanel buttonPanel = new JPanel (new FlowLayout (FlowLayout.TRAILING));

        CancelButton.setText("Cancel");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });
       
        buttonPanel.add(CancelButton);
        
        SaveButton.setText("Save");
        SaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveButtonActionPerformed(evt);
            }
        });
       
        buttonPanel.add(SaveButton);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

	// TODO: This is a lot of work to repeat every time we want to show a
	// Preferences dialog.  Is it possible to create the dialog once, and
	// refresh it with new values right before invocation?
    }

    private void SaveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // They've clicked the save button
        // Get the values of some of the items
    	prefs.mainFont = ((String) mainFontBox.getSelectedItem());
        prefs.mainFontSize = ((Integer) mainSizeBox.getSelectedItem()).intValue();
        prefs.statusFontSize = ((Integer) statusSizeBox.getSelectedItem()).intValue();
        prefs.contactFontSize = ((Integer) contactsSizeBox.getSelectedItem()).intValue();
        prefs.infoFontSize = ((Integer) contactsOnMapSizeBox.getSelectedItem()).intValue();
        prefs.elevationFontSize = ((Integer) elevationsSizeBox.getSelectedItem()).intValue();
        prefs.hexNumberFontSize = ((Integer) hexNumberSizeBox.getSelectedItem()).intValue();
        prefs.tacStatusFontSize = ((Integer) tacStatusBarSizeBox.getSelectedItem()).intValue();

        prefs.speedIndicatorLength = ((Float) speedLengthBox.getSelectedItem()).floatValue();
        prefs.maxScrollbackSize = ((Integer) scrollbackSizeBox.getSelectedItem()).intValue();
        prefs.contactsAge = ((Integer) contactsAgeBox.getSelectedItem()).intValue();
        
        thud.prefs = prefs;
        
        closeDialog(null);
    }

    // -----------------------
    // These are the action handlers for the map colors

    private void bTerrainColorActionPerformed(java.awt.event.ActionEvent evt) {

        StringTokenizer	st = new StringTokenizer(evt.getActionCommand());
        int whichTerrain = MUHex.idForTerrain(st.nextToken().charAt(0));
        
        Color	newColor = JColorChooser.showDialog(this, "Unknown Hex Color", prefs.terrainColors[whichTerrain]);

        if (newColor != null)
        {
            prefs.terrainColors[whichTerrain] = newColor;
            bTerrainColors[whichTerrain].setIcon(new ColorWellIcon(prefs.terrainColors[whichTerrain]));
        }
    }
    
    // -----------------------
    
    private void CancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // They've basically canceled
        closeDialog(null);
    }

    // -----------------------
    
    private void echoCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
        prefs.echoCommands = echoCheckBox.isSelected();
    }

    private void highlightMyHexCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
        prefs.highlightMyHex = highlightMyHexCheckBox.isSelected();
    }

    private void antiAliasTextCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
        prefs.antiAliasText = antiAliasTextCheckBox.isSelected();
    }
   
    private void mainAlwaysOnTopCheckBoxActionPerformed(ActionEvent evt) {
    	prefs.mainAlwaysOnTop = mainAlwaysOnTopCheckBox.isSelected();
    }
    
    private void contactsAlwaysOnTopCheckBoxActionPerformed(ActionEvent evt) {
    	prefs.contactsAlwaysOnTop = contactsAlwaysOnTopCheckBox.isSelected();
    }
    
    private void statusAlwaysOnTopCheckBoxActionPerformed(ActionEvent evt) {
    	prefs.statusAlwaysOnTop = statusAlwaysOnTopCheckBox.isSelected();
    }
     
    private void tacticalAlwaysOnTopCheckBoxActionPerformed(ActionEvent evt) {
    	prefs.tacticalAlwaysOnTop = tacticalAlwaysOnTopCheckBox.isSelected();
    }
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {
        setVisible(false);
        dispose();
    }

    private void setMapColorIcons() {
        for (int i = 0; i < MUHex.TOTAL_TERRAIN; i++)
            bTerrainColors[i].setIcon(new ColorWellIcon(prefs.terrainColors[i]));
    }

    /** Add standard font size menu items to a combo box */
    private void addFontSizeMenus(JComboBox theBox)
    {
        theBox.addItem(new Integer(9));
        theBox.addItem(new Integer(10));
        theBox.addItem(new Integer(11));
        theBox.addItem(new Integer(12));
        theBox.addItem(new Integer(14));
        theBox.addItem(new Integer(16));
        theBox.addItem(new Integer(18));
        theBox.addItem(new Integer(20));
        theBox.addItem(new Integer(24));
        theBox.addItem(new Integer(32));
    }
}
