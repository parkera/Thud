//
//  ToolManager.java
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

public class ToolManager {

    ToolPalette				mainTools;
    TerrainToolOptions		terrTools;
    ElevationToolOptions	elevTools;
    BrushToolOptions		brushTools;
    InspectorPalette		inspector;

    MPrefs					prefs;

    boolean					changeElevation = true;
    boolean					changeTerrain = true;

    public static final int	TERRAIN_AND_ELEVATION = -1;
    public static final int TERRAIN_ONLY = -2;
    public static final int ELEVATION_ONLY = -3;

    public static final int TOOL_PALETTE = 1;
    public static final int TERRAIN_PALETTE = 2;
    public static final int ELEVATION_PALETTE = 3;
    public static final int BRUSH_PALETTE = 4;
    public static final int	INSPECTOR_PALETTE = 5;
    
    public ToolManager(JDesktopPane desktop, MPrefs prefs)
    {
        // showTools, showTerrain, showElevation, showBrush, showInspector;
        this.prefs = prefs;
        mainTools = new ToolPalette(prefs);
        mainTools.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
        mainTools.setVisible(prefs.showTools);
        desktop.add(mainTools);

        terrTools = new TerrainToolOptions(prefs);
        terrTools.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
        terrTools.setVisible(prefs.showTerrain);
        desktop.add(terrTools);

        elevTools = new ElevationToolOptions(prefs);
        elevTools.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
        elevTools.setVisible(prefs.showElevation);
        desktop.add(elevTools);

        brushTools = new BrushToolOptions(prefs);
        brushTools.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
        brushTools.setVisible(prefs.showBrush);
        desktop.add(brushTools);

        inspector = new InspectorPalette(prefs);
        inspector.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
        inspector.setVisible(prefs.showInspector);
        desktop.add(inspector);
    }

    // -----------------------
    
    public int selectedTool()
    {
        return mainTools.selectedTool();
    }

    public int selectedTerrain()
    {
        return terrTools.selectedTerrain();
    }

    public int selectedElevation()
    {
        return elevTools.selectedElevation();
    }

    public int selectedBrush()
    {
        return brushTools.selectedBrush();
    }

    // ----------------------

    public boolean getChangeElevation()
    {
        return changeElevation;
    }

    public boolean getChangeTerrain()
    {
        return changeTerrain;
    }

    public void setChangeElevation(boolean b)
    {
        changeElevation = b;
    }

    public void setChangeTerrain(boolean b)
    {
        changeTerrain = b;
    }

    // ----------------------

    public void updatePaletteLocs()
    {
        prefs.toolsLoc = mainTools.getLocation();
        prefs.terrainToolsLoc = terrTools.getLocation();
        prefs.elevationToolsLoc = elevTools.getLocation();
        prefs.brushToolsLoc = brushTools.getLocation();
        prefs.inspectorLoc = inspector.getLocation();
    }

    public void setPaletteVisible(int palette, boolean vis)
    {
        switch (palette)
        {
            case TOOL_PALETTE:
                mainTools.setVisible(vis);
                break;

            case TERRAIN_PALETTE:
                terrTools.setVisible(vis);
                break;

            case ELEVATION_PALETTE:
                elevTools.setVisible(vis);
                break;

            case BRUSH_PALETTE:
                brushTools.setVisible(vis);
                break;
        }
    }

    // ----------------------

    public void keyTyped(KeyEvent e)
    {
        // Pass on the love
        mainTools.keyTyped(e);
        terrTools.keyTyped(e);
        elevTools.keyTyped(e);
        brushTools.keyTyped(e);
    }

    // ---------------------

    public void updateInspector(Point h)
    {
        inspector.updateLocation(h);
    }
    
    // ---------------------

    public String toString()
    {
        return ("Main: " + mainTools.getLocation() +
                "\nTerr: " + terrTools.getLocation() +
                "\nElev: " + elevTools.getLocation() +
                "\nBrush: " + brushTools.getLocation() +
                "\nInsp: " + inspector.getLocation());
    }
}
