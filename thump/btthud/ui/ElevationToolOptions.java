//
//  ElevationToolOptions.java
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

public class ElevationToolOptions extends JInternalFrame {

    JLabel					lElevation;
    JSlider					bElevation;

    int						selectedElevation;

    MPrefs					prefs;

    static final char		ELEVATION_KEYS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    
    public ElevationToolOptions(MPrefs prefs)
    {        
        super("Hex Elevation");
        
        this.prefs = prefs;
        
        setClosable(false);
        setResizable(false);

        selectedElevation = 0;
        
        setLayer(JLayeredPane.PALETTE_LAYER);

        Container contentPane = getContentPane();
        
        contentPane.setLayout(new BorderLayout());


        bElevation = new JSlider(javax.swing.SwingConstants.HORIZONTAL, 0, 9, 0);
        bElevation.setPaintTicks(true);
        bElevation.setToolTipText("Set an Elevation");
        bElevation.setMajorTickSpacing(1);
        //bElevation.setMinorTickSpacing(1);
        bElevation.setSnapToTicks(true);
        bElevation.setPaintLabels(true);
        contentPane.add(bElevation, BorderLayout.CENTER);

        lElevation = new JLabel("Elevation:");
        lElevation.setLabelFor(bElevation);
        contentPane.add(lElevation, BorderLayout.WEST);
        
        pack();
        setLocation(prefs.elevationToolsLoc);

        // Show the window now
        this.show();
    }

    // ----------------------------

    public void registerKeyActions(InputMap imap, ActionMap amap)
    {
        for (int i = 0; i < 10; i++)
        {
            imap.put(KeyStroke.getKeyStroke(ELEVATION_KEYS[i]), "elevation-" + Character.toString(ELEVATION_KEYS[i]));
            amap.put("elevation-" + Character.toString(ELEVATION_KEYS[i]), new SetElevationAction(ELEVATION_KEYS[i]));
        }
    }
    
    // ----------------------------

    public int selectedElevation()
    {
        return bElevation.getValue();
    }

    public void setElevation(int i)
    {
        bElevation.setValue(i);
    }
    
    // ---------------------------

    static public int toolForChar(char c)
    {
        for (int i = 0; i < ELEVATION_KEYS.length; i++)
        {
            if (ELEVATION_KEYS[i] == c)
                return i;
        }

        return -1;
    }

    public void keyTyped(KeyEvent e)
    {
        if (toolForChar(e.getKeyChar()) != -1)
        {
            // They've typed an elevation char
            bElevation.setValue(Character.getNumericValue(e.getKeyChar()));
        }
    }

    public class SetElevationAction extends AbstractAction {

        int							e;

        public SetElevationAction(char c) {
            e = toolForChar(c);
        }

        public void actionPerformed(ActionEvent event) {
            setElevation(e);
        }
    }
}
