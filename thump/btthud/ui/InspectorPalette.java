//
//  InspectorPalette.java
//  Thump
//
//  Created by Anthony Parker on Sat Jan 25 2003.
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

public class InspectorPalette extends JInternalFrame {

    JLabel						lHexLabel;
    JLabel						lHex;

    MPrefs						prefs;

    public InspectorPalette(MPrefs prefs)
    {

        super("Inspector");

        this.prefs = prefs;

        setClosable(false);
        setResizable(false);

        setLayer(JLayeredPane.PALETTE_LAYER);

        Container contentPane = getContentPane();

        contentPane.setLayout(new BorderLayout());

        lHexLabel = new JLabel("Hex: ");
        contentPane.add(lHexLabel, BorderLayout.WEST);
        
        lHex = new JLabel("             ");
        contentPane.add(lHex, BorderLayout.CENTER);

        pack();
        setLocation(prefs.inspectorLoc);
        // Show the window now
        this.show();
    }

    // ----------------------------

    public void updateLocation(Point p)
    {
        if (p == null)
            lHex.setText("             ");
        else
            lHex.setText("(" + (int) p.getX() + "," + (int) p.getY() + ")");
    }
}
