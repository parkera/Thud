//
//  TerrainToolOptions.java
//  Thump
//
//  Created by Anthony Parker on Tue Jan 14 2003.
//  Copyright (c) 2003 Anthony Parker. All rights reserved.
//

package btthud.ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

import java.lang.*;
import java.util.*;

import btthud.data.*;

public class TerrainToolOptions extends JInternalFrame {

    JToggleButton			bTerrain[] = new JToggleButton[MUXHex.TOTAL_PAINTABLE_TERRAIN];

    int						selectedTool;

    MPrefs					prefs;

    // -----------------------------
    
    public TerrainToolOptions(MPrefs prefs)
    {
        super("Terrain Options");

        this.prefs = prefs;
        
        selectedTool = MUXHex.PLAIN;
    
        setClosable(false);
        setResizable(false);
    
        setLayer(JLayeredPane.PALETTE_LAYER);
    
        Container contentPane = getContentPane();

        contentPane.setLayout(new GridLayout(6, 2, 0, 0));
        
        for (int i = 0; i < MUXHex.TOTAL_PAINTABLE_TERRAIN; i++)
        {
            bTerrain[i] = new JToggleButton(CustomCursors.getTerrainIcon(i), i == selectedTool ? true : false);
            bTerrain[i].setPreferredSize(new Dimension(32, 32));
            bTerrain[i].setToolTipText(MUXHex.nameForId(i) + " (" + MUXHex.terrainForId(i) + ")");
            bTerrain[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    terrTypeSelected((JToggleButton) event.getSource());
                }
            });
            contentPane.add(bTerrain[i]);
        }

        pack();
        setLocation(prefs.terrainToolsLoc);
        
        // Show the window now
        this.show();
        
    }

    // ----------------------------
    
    public int selectedTerrain()
    {
        return selectedTool;
    }
    
    // ----------------------------
    
    public void terrTypeSelected(JToggleButton b)
    {
        int			newTool = -1;
        
        // Figure out which tool is selected (and deselect the others)
        for (int i = 0; i < MUXHex.TOTAL_PAINTABLE_TERRAIN; i++)
        {
            if (bTerrain[i] == b)
                newTool = i;
            else
                bTerrain[i].setSelected(false);
        }

        b.setSelected(true);
        selectedTool = newTool;
    }

    // ----------------------------

    static public int toolForChar(char c)
    {
        for (int i = 0; i < MUXHex.PAINTABLE_TERRAIN_TYPES.length; i++)
        {
            if (MUXHex.PAINTABLE_TERRAIN_TYPES[i] == c)
                return i;
        }

        return -1;
    }
    
    public void selectToolForTerrain(char t)
    {
        terrTypeSelected(bTerrain[MUXHex.idForTerrain(t)]);
    }
}
