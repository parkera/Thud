//
//  BrushToolOptions.java
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

public class BrushToolOptions extends JInternalFrame {

    JLabel						lBrush;
    JSlider						bBrush;

    int							selectedBrush;
    
    static final int			MIN_BRUSH_SIZE = 1;
    static final int			MAX_BRUSH_SIZE = 5;

    static final char			KEY_INCREASE_BRUSH_SIZE = '>';
    static final char			KEY_DECREASE_BRUSH_SIZE = '<';

    // Data for the shape of the brushes
    // Note: if you click on an odd X hex, every other y must be decremented by 1 (thus the triple array for brushY 0 = even, 1 = odd)
    public static final int		brushHexSizes[] = {1, 6, 12, 18, 24};
    public static final int		brushX[][] = {
    {0},
    {0, 1, 1, 0, -1, -1},
    {0, 1, 2, 2, 2, 1, 0, -1, -2, -2, -2, -1},
    {0, 1, 2, 3, 3, 3, 3, 2, 1, 0, -1, -2, -3, -3, -3, -3, -2, -1},
    {0, 1, 2, 3, 4, 4, 4, 4, 4, 3, 2, 1, 0, -1, -2, -3, -4, -4, -4, -4, -4, -3, -2, -1}};

    public static final int		brushY[][][] = {
    {{0},
    {-1, 0, 1, 1, 1, 0},
    {-2, -1, -1, 0, 1, 2, 2, 2, 1, 0, -1, -1},
    {-3, -2, -2, -1, 0, 1, 2, 2, 3, 3, 3, 2, 2, 1, 0, -1, -2, -2},
    {-4, -3, -3, -2, -2, -1, 0, 1, 2, 3, 3, 4, 4, 4, 3, 3, 2, 1, 0, -1, -2, -2, -3, -3}},
    {{0},
    {-1, -1, 0, 1, 0, -1},
    {-2, -2, -1, 0, 1, 1, 2, 1, 1, 0, -1, -2},
    {-3, -3, -2, -2, -1, 0, 1, 2, 2, 3, 2, 2, 1, 0, -1, -2, -2, -3},
    {-4, -4, -3, -3, -2, -1, 0, 1, 2, 2, 3, 3, 4, 3, 3, 2, 2, 1, 0, -1, -2, -3, -3, -4}}};

    MPrefs						prefs;
    
    public BrushToolOptions(MPrefs prefs)
    {

        super("Brush Size");

        this.prefs = prefs;
        
        setClosable(false);
        setResizable(false);

        selectedBrush = 1;

        setLayer(JLayeredPane.PALETTE_LAYER);

        Container contentPane = getContentPane();

        contentPane.setLayout(new BorderLayout());

        bBrush = new JSlider(javax.swing.SwingConstants.HORIZONTAL, MIN_BRUSH_SIZE, MAX_BRUSH_SIZE, 1);
        bBrush.setPaintTicks(true);
        bBrush.setToolTipText("Set a Brush Size");
        bBrush.setMajorTickSpacing(1);
        bBrush.setMinorTickSpacing(1);
        bBrush.setSnapToTicks(true);
        bBrush.setPaintLabels(true);        
        contentPane.add(bBrush, BorderLayout.CENTER);

        lBrush = new JLabel("Brush Size:");
        lBrush.setLabelFor(bBrush);
        contentPane.add(lBrush, BorderLayout.WEST);
        
        pack();
        setLocation(prefs.brushToolsLoc);
        // Show the window now
        this.show();
    }

    // ----------------------------

    public int selectedBrush()
    {
        return bBrush.getValue()-1;
    }

    // ----------------------------

    public void keyTyped(KeyEvent e)
    {
        if (e.getKeyChar() == KEY_INCREASE_BRUSH_SIZE && bBrush.getValue() != MAX_BRUSH_SIZE)
            bBrush.setValue(bBrush.getValue() + 1);
        else if (e.getKeyChar() == KEY_DECREASE_BRUSH_SIZE && bBrush.getValue() != MIN_BRUSH_SIZE)
            bBrush.setValue(bBrush.getValue() - 1);
    }
    
}
