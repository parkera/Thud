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

public class PrefsDialog extends javax.swing.JDialog {
    
    private MUPrefs     prefs = null;
    private Thud		thudClass = null;
    
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
        
        // Make sure we have a reference to the old ones so we can change them
        //this.oldPrefs = prefs;
        
        initComponents();
        setMapColorIcons();
    }

    private void initComponents() {
        TabbedPane = new javax.swing.JTabbedPane();
        
        GeneralOptionsTab = new javax.swing.JPanel();
        cliffDistanceLabel = new javax.swing.JLabel();
        cliffDistanceBox = new javax.swing.JComboBox();

        MapColorsTab = new javax.swing.JPanel();
        bBuilding = new javax.swing.JButton();
        bRoad = new javax.swing.JButton();
        bPlains = new javax.swing.JButton();
        bWater = new javax.swing.JButton();
        bLightForest = new javax.swing.JButton();
        bHeavyForest = new javax.swing.JButton();
        bWall = new javax.swing.JButton();
        bMountain = new javax.swing.JButton();
        bRough = new javax.swing.JButton();
        bFire = new javax.swing.JButton();
        bSmoke = new javax.swing.JButton();
        bIce = new javax.swing.JButton();
        bSmokeOnWater = new javax.swing.JButton();
        bUnknown = new javax.swing.JButton();

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
        GeneralOptionsTab.setLayout(new java.awt.GridLayout(2, 2));
        
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
        
        TabbedPane.addTab("General", GeneralOptionsTab);

        // --- MAP COLOR OPTIONS ---
        MapColorsTab.setLayout(new java.awt.GridLayout(0, 2));
        
        bBuilding.setText("Building");
        bBuilding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bBuildingActionPerformed(evt);
            }
        });
        
        MapColorsTab.add(bBuilding);
        
        bRoad.setText("Road");
        bRoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRoadActionPerformed(evt);
            }
        });
        
        MapColorsTab.add(bRoad);
        
        bPlains.setText("Plains");
        bPlains.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bPlainsActionPerformed(evt);
            }
        });
        
        MapColorsTab.add(bPlains);
        
        bWater.setText("Water");
        bWater.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bWaterActionPerformed(evt);
            }
        });
        
        MapColorsTab.add(bWater);
        
        bLightForest.setText("Light Forest");
        bLightForest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bLightForestActionPerformed(evt);
            }
        });
        
        MapColorsTab.add(bLightForest);
        
        bHeavyForest.setText("Heavy Forest");
        bHeavyForest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bHeavyForestActionPerformed(evt);
            }
        });
        
        MapColorsTab.add(bHeavyForest);
        
        bWall.setText("Wall");
        bWall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bWallActionPerformed(evt);
            }
        });
        
        MapColorsTab.add(bWall);
        
        bMountain.setText("Mountain");
        bMountain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bMountainActionPerformed(evt);
            }
        });
        
        MapColorsTab.add(bMountain);
        
        bRough.setText("Rough");
        bRough.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRoughActionPerformed(evt);
            }
        });
        
        MapColorsTab.add(bRough);
        
        bFire.setText("Fire");
        bFire.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bFireActionPerformed(evt);
            }
        });
        
        MapColorsTab.add(bFire);
        
        bSmoke.setText("Smoke");
        bSmoke.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSmokeActionPerformed(evt);
            }
        });
        
        MapColorsTab.add(bSmoke);
        
        bIce.setText("Ice");
        bIce.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bIceActionPerformed(evt);
            }
        });
        
        MapColorsTab.add(bIce);
        
        bSmokeOnWater.setText("Smoke On Water");
        bSmokeOnWater.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSmokeOnWaterActionPerformed(evt);
            }
        });
        
        MapColorsTab.add(bSmokeOnWater);
        
        bUnknown.setText("Unknown");
        bUnknown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bUnknownActionPerformed(evt);
            }
        });
        
        MapColorsTab.add(bUnknown);

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

        thudClass.prefs = prefs;
        
        closeDialog(null);
    }

    // -----------------------
    // These are the action handlers for the map colors
    
    private void bUnknownActionPerformed(java.awt.event.ActionEvent evt) {
        Color	newColor = JColorChooser.showDialog(this, "Unknown Hex Color", prefs.cUnknown);
        if (newColor != null)
        {
            prefs.cUnknown = newColor;
            bUnknown.setIcon(new ColorWellIcon(prefs.cUnknown));            
        }
    }

    private void bSmokeOnWaterActionPerformed(java.awt.event.ActionEvent evt) {
        Color	newColor = JColorChooser.showDialog(this, "Smoke On Water Color", prefs.cSmokeOnWater);
        if (newColor != null)
        {
            prefs.cSmokeOnWater = newColor;
            bSmokeOnWater.setIcon(new ColorWellIcon(prefs.cSmokeOnWater));
        }
    }

    private void bIceActionPerformed(java.awt.event.ActionEvent evt) {
        Color	newColor = JColorChooser.showDialog(this, "Ice Color", prefs.cIce);
        if (newColor != null)
        {
            prefs.cIce = newColor;
            bIce.setIcon(new ColorWellIcon(prefs.cIce));
        }
    }
    
    private void bSmokeActionPerformed(java.awt.event.ActionEvent evt) {
        Color	newColor = JColorChooser.showDialog(this, "Smoke Color", prefs.cSmoke);
        if (newColor != null)
        {
            prefs.cSmoke = newColor;
            bSmoke.setIcon(new ColorWellIcon(prefs.cSmoke));
        }
    }

    private void bFireActionPerformed(java.awt.event.ActionEvent evt) {
        Color	newColor = JColorChooser.showDialog(this, "Fire Color", prefs.cFire);
        if (newColor != null)
        {
            prefs.cFire = newColor;
            bFire.setIcon(new ColorWellIcon(prefs.cFire));
        }
    }

    private void bRoughActionPerformed(java.awt.event.ActionEvent evt) {
        Color	newColor = JColorChooser.showDialog(this, "Rough Color", prefs.cRough);
        if (newColor != null)
        {
            prefs.cRough = newColor;
            bRough.setIcon(new ColorWellIcon(prefs.cRough));
        }
    }

    private void bMountainActionPerformed(java.awt.event.ActionEvent evt) {
        Color	newColor = JColorChooser.showDialog(this, "Mountain Color", prefs.cMountain);
        if (newColor != null)
        {
            prefs.cMountain = newColor;
            bMountain.setIcon(new ColorWellIcon(prefs.cMountain));
        }
    }

    private void bWallActionPerformed(java.awt.event.ActionEvent evt) {
        Color	newColor = JColorChooser.showDialog(this, "Wall Color", prefs.cWall);
        if (newColor != null)
        {
            prefs.cWall = newColor;
            bWall.setIcon(new ColorWellIcon(prefs.cWall));
        }
    }

    private void bHeavyForestActionPerformed(java.awt.event.ActionEvent evt) {
        Color	newColor = JColorChooser.showDialog(this, "Heavy Forest Color", prefs.cHeavyForest);
        if (newColor != null)
        {
            prefs.cHeavyForest = newColor;
            bHeavyForest.setIcon(new ColorWellIcon(prefs.cHeavyForest));
        }
    }

    private void bWaterActionPerformed(java.awt.event.ActionEvent evt) {
        Color	newColor = JColorChooser.showDialog(this, "Water Color", prefs.cWater);
        if (newColor != null)
        {
            prefs.cWater = newColor;
            bWater.setIcon(new ColorWellIcon(prefs.cWater));
        }
    }

    private void bLightForestActionPerformed(java.awt.event.ActionEvent evt) {
        Color	newColor = JColorChooser.showDialog(this, "Light Forest Color", prefs.cLightForest);
        if (newColor != null)
        {
            prefs.cLightForest = newColor;
            bLightForest.setIcon(new ColorWellIcon(prefs.cLightForest));
        }
    }
    
    private void bPlainsActionPerformed(java.awt.event.ActionEvent evt) {
        Color	newColor = JColorChooser.showDialog(this, "Plains Color", prefs.cPlains);
        if (newColor != null)
        {
            prefs.cPlains = newColor;
            bPlains.setIcon(new ColorWellIcon(prefs.cPlains));
        }
    }

    private void bBuildingActionPerformed(java.awt.event.ActionEvent evt) {
        Color	newColor = JColorChooser.showDialog(this, "Building Color", prefs.cBuilding);
        if (newColor != null)
        {
            prefs.cBuilding = newColor;
            bBuilding.setIcon(new ColorWellIcon(prefs.cBuilding));
        }
    }

    private void bRoadActionPerformed(java.awt.event.ActionEvent evt) {
        Color	newColor = JColorChooser.showDialog(this, "Road Color", prefs.cRoad);
        if (newColor != null)
        {
            prefs.cRoad = newColor;
            bRoad.setIcon(new ColorWellIcon(prefs.cRoad));
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
        bBuilding.setIcon(new ColorWellIcon(prefs.cBuilding));
        bRoad.setIcon(new ColorWellIcon(prefs.cRoad));
        bPlains.setIcon(new ColorWellIcon(prefs.cPlains));
        bWater.setIcon(new ColorWellIcon(prefs.cWater));
        bLightForest.setIcon(new ColorWellIcon(prefs.cLightForest));
        bHeavyForest.setIcon(new ColorWellIcon(prefs.cHeavyForest));
        bWall.setIcon(new ColorWellIcon(prefs.cWall));
        bMountain.setIcon(new ColorWellIcon(prefs.cMountain));
        bRough.setIcon(new ColorWellIcon(prefs.cRough));
        bFire.setIcon(new ColorWellIcon(prefs.cFire));
        bSmoke.setIcon(new ColorWellIcon(prefs.cSmoke));
        bIce.setIcon(new ColorWellIcon(prefs.cIce));
        bSmokeOnWater.setIcon(new ColorWellIcon(prefs.cSmokeOnWater));
        bUnknown.setIcon(new ColorWellIcon(prefs.cUnknown));
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

    // ------------------
    
    private javax.swing.JTabbedPane TabbedPane;
  
    private javax.swing.JPanel GeneralOptionsTab;
    private javax.swing.JCheckBox echoCheckBox;
    private javax.swing.JCheckBox highlightMyHexCheckBox;
    private javax.swing.JLabel cliffDistanceLabel;
    private javax.swing.JComboBox cliffDistanceBox;
    
    private javax.swing.JPanel MapColorsTab;
    private javax.swing.JButton bBuilding;
    private javax.swing.JButton bRoad;
    private javax.swing.JButton bPlains;
    private javax.swing.JButton bWater;
    private javax.swing.JButton bLightForest;
    private javax.swing.JButton bHeavyForest;
    private javax.swing.JButton bWall;
    private javax.swing.JButton bMountain;
    private javax.swing.JButton bRough;
    private javax.swing.JButton bFire;
    private javax.swing.JButton bSmoke;
    private javax.swing.JButton bIce;
    private javax.swing.JButton bSmokeOnWater;
    private javax.swing.JButton bUnknown;
    
    private javax.swing.JPanel FontTab;
    private javax.swing.JLabel mainLabel;
    private javax.swing.JComboBox mainSizeBox;
    private javax.swing.JLabel contactsLabel;
    private javax.swing.JComboBox contactsSizeBox;
    private javax.swing.JLabel contactsOnMapLabel;
    private javax.swing.JComboBox contactsOnMapSizeBox;
    private javax.swing.JLabel elevationsLabel;
    private javax.swing.JComboBox elevationsSizeBox;
    private javax.swing.JLabel hexNumbersLabel;
    private javax.swing.JComboBox hexNumberSizeBox;
    
    private javax.swing.JButton CancelButton;
    private javax.swing.JButton SaveButton;

}
