//
//  ToolPalette.java
//  Thump
//
//  Created by Anthony Parker on Sun Jan 12 2003.
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

public class ToolPalette extends JInternalFrame {

    public static final int			TOOL_BUTTON_HEIGHT = 48;
    public static final int			TOOL_BUTTON_WIDTH = 48;
    public static final Dimension	TOOL_BUTTON_SIZE = new Dimension(TOOL_BUTTON_WIDTH, TOOL_BUTTON_HEIGHT);

    public static final int		PAINT_TOOL = -10;
    public static final char	PAINT_CHAR = 'p';
    public static final int		SELECT_TOOL = -11;
    public static final char	SELECT_CHAR = 's';
    public static final int		SELECTIVE_UNDO_TOOL = -12;
    public static final char	SELECTIVE_UNDO_CHAR = 'u';
    public static final int		ERASE_TOOL = -13;
    public static final char	ERASE_CHAR = 'e';

    int							selectedTool;

    JToggleButton				bPaint;
    JToggleButton				bSelect;
    JToggleButton				bSelectiveUndo;
    JToggleButton				bErase;

    MPrefs						prefs;
    
    // ----------------------------
    
    public ToolPalette(MPrefs prefs)
    {
        super("Tools");

        this.prefs = prefs;
        
        setClosable(false);
        setResizable(false);

        setLayer(JLayeredPane.PALETTE_LAYER);

        Container contentPane = getContentPane();

        contentPane.setLayout(new GridLayout(2, 2, 0, 0));

        bPaint = new JToggleButton(CustomCursors.getPaintIcon());
        bPaint.setPreferredSize(new Dimension(32, 20));
        bPaint.setToolTipText("Paint Tool (p)");
        bPaint.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                newToolSelected(PAINT_TOOL);
            }
        });
        contentPane.add(bPaint);

        bSelect = new JToggleButton(CustomCursors.getSelectIcon());
        bSelect.setPreferredSize(new Dimension(32, 20));
        bSelect.setToolTipText("Select Tool (s)");
        bSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                newToolSelected(SELECT_TOOL);
            }
        });
        contentPane.add(bSelect);

        bSelectiveUndo = new JToggleButton(CustomCursors.getSelectiveUndoIcon());
        bSelectiveUndo.setPreferredSize(new Dimension(32, 20));
        bSelectiveUndo.setToolTipText("Selective Undo Tool (u or right-click)");
        bSelectiveUndo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                newToolSelected(SELECTIVE_UNDO_TOOL);
            }
        });
        contentPane.add(bSelectiveUndo);

        bErase = new JToggleButton(CustomCursors.getEraseIcon());
        bErase.setPreferredSize(new Dimension(32, 20));
        bErase.setToolTipText("Erase Tool (e)");
        bErase.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                newToolSelected(ERASE_TOOL);
            }
        });
        contentPane.add(bErase);

        selectedTool = PAINT_TOOL;
        bPaint.setSelected(true);
        
        pack();
        setLocation(prefs.toolsLoc);
        // Show the window now
        this.show();

    }

    // ----------------------------

    public int selectedTool()
    {
        return selectedTool;
    }

    // ----------------------------

    public void newToolSelected(int tool)
    {
        // Deselect the last button, select this one, then set our new 'selected' tool
        buttonForTool(selectedTool).setSelected(false);
        buttonForTool(tool).setSelected(true);

        selectedTool = tool;
    }

    protected JToggleButton buttonForTool(int tool)
    {
        switch (tool)
        {
            case PAINT_TOOL:
                return bPaint;
            case SELECT_TOOL:
                return bSelect;
            case SELECTIVE_UNDO_TOOL:
                return bSelectiveUndo;
            case ERASE_TOOL:
                return bErase;
        }

        return null;
    }

    // ---------------------------

    public void keyTyped(KeyEvent e)
    {        
        // If it's one of our tools, switch the tool
        if (e.getKeyChar() == PAINT_CHAR)
            newToolSelected(PAINT_TOOL);
        else if (e.getKeyChar() == SELECT_CHAR)
            newToolSelected(SELECT_TOOL);
        else if (e.getKeyChar() == SELECTIVE_UNDO_CHAR)
            newToolSelected(SELECTIVE_UNDO_TOOL);
        else if (e.getKeyChar() == ERASE_CHAR)
            newToolSelected(ERASE_TOOL);
    }
}
