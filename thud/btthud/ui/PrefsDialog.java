//
//  PrefsDialog.java
//  Thud
//
//  Created by Anthony Parker on April 10, 2002, 2:26 AM.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//
//
package btthud.ui;

import btthud.data.*;
import btthud.util.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;

public class PrefsDialog extends javax.swing.JDialog {
    
    private MUPrefs     prefs = null;
    private Thud		thudClass = null;

    private javax.swing.JTabbedPane 	TabbedPane;

    private javax.swing.JPanel 			GeneralOptionsTab;
    private javax.swing.JCheckBox 		echoCheckBox;
    private javax.swing.JCheckBox 		highlightMyHexCheckBox;
    private javax.swing.JLabel 			cliffDistanceLabel;
    private javax.swing.JComboBox 		cliffDistanceBox;
    private javax.swing.JLabel 			speedLengthLabel;
    private javax.swing.JComboBox 		speedLengthBox;

    private javax.swing.JPanel 			MapColorsTab;
    private javax.swing.JButton 		bTerrainColors[] = new javax.swing.JButton[MUHex.TOTAL_TERRAIN];

    private javax.swing.JPanel 			FontTab;
    private javax.swing.JLabel 			mainLabel;
    private javax.swing.JComboBox 		mainSizeBox;
    private javax.swing.JLabel 			contactsLabel;
    private javax.swing.JComboBox 		contactsSizeBox;
    private javax.swing.JLabel 			contactsOnMapLabel;
    private javax.swing.JComboBox 		contactsOnMapSizeBox;
    private javax.swing.JLabel 			elevationsLabel;
    private javax.swing.JComboBox 		elevationsSizeBox;
    private javax.swing.JLabel 			hexNumbersLabel;
    private javax.swing.JComboBox 		hexNumberSizeBox;

    private javax.swing.JButton 		CancelButton;
    private javax.swing.JButton 		SaveButton;
    
    /** Creates new form PrefsDialog */
    public PrefsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        thudClass = (Thud) parent;
        
        if (thudClass.prefs == null)
            prefs = new MUPrefs();

        try {
            this.prefs = (MUPrefs) ObjectCloner.deepCopy(thudClass.prefs);
        } catch (Exception e) {
            System.out.println("Error: prefsDialog: " + e);
        }
        
        initComponents();
        setMapColorIcons();
    }

    private void initComponents() {
        TabbedPane = new javax.swing.JTabbedPane();
        
        GeneralOptionsTab = new javax.swing.JPanel();
        cliffDistanceLabel = new javax.swing.JLabel();
        cliffDistanceBox = new javax.swing.JComboBox();
        speedLengthLabel = new javax.swing.JLabel();
        speedLengthBox = new javax.swing.JComboBox();

        MapColorsTab = new javax.swing.JPanel();
        for (int i = 0; i < MUHex.TOTAL_TERRAIN; i++)
            bTerrainColors[i] = new javax.swing.JButton();
        
        FontTab = new javax.swing.JPanel();
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

        CancelButton = new javax.swing.JButton();
        SaveButton = new javax.swing.JButton();

        // Set the content pane to a null layout
        getContentPane().setLayout(null);

        // Set the size of our dialog box, and some other stuff
        setSize(405, 325);
        setResizable(false);
        setTitle("Thud Preferences");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Thud Preferences");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        
        // --- GENERAL OPTIONS ---
        GeneralOptionsTab.setLayout(new java.awt.GridLayout(3, 2));
        
        echoCheckBox = new javax.swing.JCheckBox("Echo Commands", null, prefs.echoCommands);
        echoCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                echoCheckBoxActionPerformed(evt);
            }
        });
        GeneralOptionsTab.add(echoCheckBox);

        highlightMyHexCheckBox = new javax.swing.JCheckBox("Highlight My Hex", null, prefs.highlightMyHex);
        highlightMyHexCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                highlightMyHexCheckBoxActionPerformed(evt);
            }
        });
        GeneralOptionsTab.add(highlightMyHexCheckBox);
        
        cliffDistanceLabel.setText("Cliff Detection Threshold");
        GeneralOptionsTab.add(cliffDistanceLabel);
        cliffDistanceBox.addItem(new Integer(1));
        cliffDistanceBox.addItem(new Integer(2));
        cliffDistanceBox.setSelectedItem(new Integer(prefs.cliffDiff));
        GeneralOptionsTab.add(cliffDistanceBox);

        speedLengthLabel.setText("Speed Indicator Divisor");
        GeneralOptionsTab.add(speedLengthLabel);
        speedLengthBox.addItem(new Float(1.0));
        speedLengthBox.addItem(new Float(1.5));
        speedLengthBox.addItem(new Float(2.0));
        speedLengthBox.addItem(new Float(2.5));
        speedLengthBox.addItem(new Float(3.0));
        speedLengthBox.addItem(new Float(3.5));
        speedLengthBox.addItem(new Float(4.0));
        speedLengthBox.setSelectedItem(new Float(prefs.speedIndicatorLength));
        GeneralOptionsTab.add(speedLengthBox);
        
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
        FontTab.setLayout(new GridLayout(5, 2));
        
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
        
        TabbedPane.addTab("Fonts", FontTab);
        
        getContentPane().add(TabbedPane);
        TabbedPane.setBounds(10, 10, 390, 250);
        
        CancelButton.setText("Cancel");
        CancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelButtonActionPerformed(evt);
            }
        });
        
        getContentPane().add(CancelButton);
        CancelButton.setBounds(190, 270, 100, 23);
        
        SaveButton.setText("Save");
        SaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveButtonActionPerformed(evt);
            }
        });
        
        getContentPane().add(SaveButton);
        SaveButton.setBounds(297, 270, 100, 23);
    }

    private void SaveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // They've clicked the save button
        // Get the values of some of the items
        prefs.mainFontSize = ((Integer) mainSizeBox.getSelectedItem()).intValue();
        prefs.contactFontSize = ((Integer) contactsSizeBox.getSelectedItem()).intValue();
        prefs.infoFontSize = ((Integer) contactsOnMapSizeBox.getSelectedItem()).intValue();
        prefs.elevationFontSize = ((Integer) elevationsSizeBox.getSelectedItem()).intValue();
        prefs.hexNumberFontSize = ((Integer) hexNumberSizeBox.getSelectedItem()).intValue();

        prefs.cliffDiff = ((Integer) cliffDistanceBox.getSelectedItem()).intValue();
        prefs.speedIndicatorLength = ((Float) speedLengthBox.getSelectedItem()).floatValue();

        thudClass.prefs = prefs;
        
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

    // Closes the dialog
    private void closeDialog(java.awt.event.WindowEvent evt) {
        setVisible(false);
        dispose();
    }

    private void setMapColorIcons() {
        for (int i = 0; i < MUHex.TOTAL_TERRAIN; i++)
            bTerrainColors[i].setIcon(new ColorWellIcon(prefs.terrainColors[i]));
    }

    // Add standard font size menu items to a combo box
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
