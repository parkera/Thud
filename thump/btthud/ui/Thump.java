//
//  Thump.java
//  Thump
//
//  Created by Anthony Parker on Sat Jan 11 2003.
//  Copyright (c) 2003 Anthony Parker. All rights reserved.
//
//	For information on setting Java configuration information, including 
//	setting Java properties, refer to the documentation at
//		http://developer.apple.com/techpubs/java/java.html
//

package btthud.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;

import java.awt.print.*;

import btthud.data.*;
import btthud.util.*;

public class Thump extends JFrame implements ActionListener, InternalFrameListener, WindowListener, KeyListener {

    protected ResourceBundle resbundle;
    protected AboutBox aboutBox;

    // Declarations for menus
    static final JMenuBar mainMenuBar = new JMenuBar();
	
    protected JMenu fileMenu; 
    protected JMenuItem miNew;
    protected JMenuItem miOpen;
    protected JMenuItem miClose;
    protected JMenuItem miSave;
    protected JMenuItem miSaveAs;
    protected JMenuItem miExportToPNG;
    protected JMenuItem miPageSetup;
    protected JMenuItem miPrint;
    protected JMenuItem	miQuit;
	
    protected JMenu editMenu;
    protected JMenuItem miUndo;
    // protected JMenuItem miRedo;
    protected JMenuItem miCut;
    protected JMenuItem miCopy;
    protected JMenuItem miPaste;
    protected JMenuItem miDeselectAll;

    protected JMenu mapMenu;
    protected JMenuItem miZoomIn;
    protected JMenuItem miZoomOut;

    protected JMenu paintMenu;
    protected JCheckBoxMenuItem miTerrainAndElevation;
    protected JCheckBoxMenuItem miTerrainOnly;
    protected JCheckBoxMenuItem miElevationOnly;

    protected JMenu paletteMenu;
    protected JCheckBoxMenuItem miToolPalette;
    protected JCheckBoxMenuItem miTerrainToolPalette;
    protected JCheckBoxMenuItem miElevationToolPalette;
    protected JCheckBoxMenuItem miBrushToolPalette;
    protected JCheckBoxMenuItem miInspectorPalette;

    protected JMenu toolMenu;
    protected ButtonGroup toolGroup;
    protected JRadioButtonMenuItem miPaintTool;
    protected JRadioButtonMenuItem miSelectTool;
    protected JRadioButtonMenuItem miUndoTool;
    protected JRadioButtonMenuItem miEraseTool;
    protected JMenu elevationSubMenu;
    protected ButtonGroup elevationGroup;
    protected JRadioButtonMenuItem miElevations[];
    protected JMenu terrainSubMenu;
    protected ButtonGroup terrainGroup;
    protected JRadioButtonMenuItem miTerrains[];
    protected JMenu brushSizeSubMenu;
    protected ButtonGroup brushSizeGroup;
    protected JRadioButtonMenuItem miBrushSizes[];

    protected JMenu helpMenu;
    protected JMenuItem miAbout;
    protected JMenuItem miHelp;
    
    // --------

    JDesktopPane		desktop;
    
    MPrefs				prefs;

    ToolManager			tools;

    LinkedList			copyableHexes;

    Cursor				crosshairCursor, terrainCursor, elevationCursor, pasteCursor;
    
    // --------

    static final int		DEFAULT_HEIGHT = 40;
    
    // --------------------------------------------------------------------
    
    public void addFileMenuItems() {

        miNew = new JMenuItem("New...");
        miNew.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenu.add(miNew).setEnabled(true);
        miNew.addActionListener(this);
        
        miOpen = new JMenuItem("Open...");
        miOpen.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenu.add(miOpen).setEnabled(true);
        miOpen.addActionListener(this);
		
        miClose = new JMenuItem("Close");
        miClose.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenu.add(miClose).setEnabled(true);
        miClose.addActionListener(this);
		
        miSave = new JMenuItem("Save");
        miSave.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenu.add(miSave).setEnabled(true);
        miSave.addActionListener(this);
		
        miSaveAs = new JMenuItem("Save As...");
        fileMenu.add(miSaveAs).setEnabled(true);
        miSaveAs.addActionListener(this);

        miExportToPNG = new JMenuItem("Export to PNG...");
        fileMenu.add(miExportToPNG).setEnabled(true);
        miExportToPNG.addActionListener(this);
        
        fileMenu.addSeparator();

        miPageSetup = new JMenuItem("Page Setup...");
        miPageSetup.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P,
                                                          java.awt.Event.SHIFT_MASK + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenu.add(miPageSetup).setEnabled(false);	// broken right now
        miPageSetup.addActionListener(this);

        miPrint = new JMenuItem("Print...");
        miPrint.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenu.add(miPrint).setEnabled(false);		// broken right now
        miPrint.addActionListener(this);
        
        fileMenu.addSeparator();
        
        miQuit = new JMenuItem("Quit");
        miQuit.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileMenu.add(miQuit).setEnabled(true);
        miQuit.addActionListener(this);

        // ---
        
        mainMenuBar.add(fileMenu);
    }
	
	
    public void addEditMenuItems() {
        miUndo = new JMenuItem("Undo");
        miUndo.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(miUndo).setEnabled(false);
        miUndo.addActionListener(this);

        /*
        miRedo = new JMenuItem("Redo");
        miRedo.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(miRedo).setEnabled(true);
        miRedo.addActionListener(this);
         */
        
        editMenu.addSeparator();

        miCut = new JMenuItem("Cut");
        miCut.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(miCut).setEnabled(true);
        miCut.addActionListener(this);

        miCopy = new JMenuItem("Copy");
        miCopy.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(miCopy).setEnabled(true);
        miCopy.addActionListener(this);

        miPaste = new JMenuItem("Paste");
        miPaste.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(miPaste).setEnabled(true);
        miPaste.addActionListener(this);


        editMenu.addSeparator();

        miDeselectAll = new JMenuItem("Deselect All");
        miDeselectAll.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        editMenu.add(miDeselectAll).setEnabled(true);
        miDeselectAll.addActionListener(this);

        // ---
        
        mainMenuBar.add(editMenu);
    }

    public void addMapMenuItems() {

        miZoomIn = new JMenuItem("Zoom In");
        miZoomIn.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_CLOSE_BRACKET,
                                                       Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miZoomIn).setEnabled(true);
        miZoomIn.addActionListener(this);

        miZoomOut = new JMenuItem("Zoom Out");
        miZoomOut.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_OPEN_BRACKET,
                                                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        mapMenu.add(miZoomOut).setEnabled(true);
        miZoomOut.addActionListener(this);

        // ---
        
        mainMenuBar.add(mapMenu);
    }

    public void addPaintMenuItems() {

        miTerrainAndElevation = new JCheckBoxMenuItem("Terrain and Elevation", prefs.paintType == ToolManager.TERRAIN_AND_ELEVATION);
        miTerrainAndElevation.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B,
                                                                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        paintMenu.add(miTerrainAndElevation);
        miTerrainAndElevation.addActionListener(this);

        miTerrainOnly = new JCheckBoxMenuItem("Terrain Only", prefs.paintType == ToolManager.TERRAIN_ONLY);
        miTerrainOnly.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T,
                                                            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        paintMenu.add(miTerrainOnly);
        miTerrainOnly.addActionListener(this);

        miElevationOnly = new JCheckBoxMenuItem("Elevation Only", prefs.paintType == ToolManager.ELEVATION_ONLY);
        miElevationOnly.setAccelerator(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E,
                                                            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        paintMenu.add(miElevationOnly);
        miElevationOnly.addActionListener(this);

        // ---

        mainMenuBar.add(paintMenu);
    }

    public void addPaletteMenuItems() {

        miToolPalette = new JCheckBoxMenuItem("Show Tools Palette", prefs.showTools);
        paletteMenu.add(miToolPalette);
        miToolPalette.addActionListener(this);

        miTerrainToolPalette = new JCheckBoxMenuItem("Show Terrain Palette", prefs.showTerrain);
        paletteMenu.add(miTerrainToolPalette);
        miTerrainToolPalette.addActionListener(this);

        miElevationToolPalette = new JCheckBoxMenuItem("Show Elevation Palette", prefs.showElevation);
        paletteMenu.add(miElevationToolPalette);
        miElevationToolPalette.addActionListener(this);

        miBrushToolPalette = new JCheckBoxMenuItem("Show Brush Palette", prefs.showBrush);
        paletteMenu.add(miBrushToolPalette);
        miBrushToolPalette.addActionListener(this);

        miInspectorPalette = new JCheckBoxMenuItem("Show Inspector Palette", prefs.showInspector);
        paletteMenu.add(miInspectorPalette);
        miInspectorPalette.addActionListener(this);

        // ---

        mainMenuBar.add(paletteMenu);
        
    }

    public void addToolMenuItems() {
        
        toolGroup = new ButtonGroup();
        
        miPaintTool = new JRadioButtonMenuItem("Paint Tool");
        miPaintTool.setAccelerator(KeyStroke.getKeyStroke(ToolPalette.PAINT_CHAR));
        miPaintTool.setSelected(true);          // brush tool initially
        toolGroup.add(miPaintTool);
        toolMenu.add(miPaintTool);
        miPaintTool.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                tools.doSelectTool(ToolPalette.PAINT_TOOL);
                resetToolMenu();
            }
        });
        
        miSelectTool = new JRadioButtonMenuItem("Select Tool");
        miSelectTool.setAccelerator(KeyStroke.getKeyStroke(ToolPalette.SELECT_CHAR));
        toolGroup.add(miSelectTool);
        toolMenu.add(miSelectTool);
        miSelectTool.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                tools.doSelectTool(ToolPalette.SELECT_TOOL);
                resetToolMenu();
            }
        });
        
        miUndoTool = new JRadioButtonMenuItem("Undo Tool");
        miUndoTool.setAccelerator(KeyStroke.getKeyStroke(ToolPalette.SELECTIVE_UNDO_CHAR));
        toolGroup.add(miUndoTool);
        toolMenu.add(miUndoTool);
        miUndoTool.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                tools.doSelectTool(ToolPalette.SELECTIVE_UNDO_TOOL);
                resetToolMenu();
            }
        });
        
        miEraseTool = new JRadioButtonMenuItem("Erase Tool");
        miEraseTool.setAccelerator(KeyStroke.getKeyStroke(ToolPalette.ERASE_CHAR));
        toolGroup.add(miEraseTool);
        toolMenu.add(miEraseTool);
        miEraseTool.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                tools.doSelectTool(ToolPalette.ERASE_TOOL);
                resetToolMenu();
            }
        });
        
        toolMenu.addSeparator();
        
        elevationSubMenu = new JMenu("Elevation");
        elevationGroup = new ButtonGroup();
        
        miElevations = new JRadioButtonMenuItem[10];
        
        for (int i = 0; i < 10; i++)
        {
            miElevations[i] = new JRadioButtonMenuItem("Elevation " + i);
            miElevations[i].setAccelerator(KeyStroke.getKeyStroke(String.valueOf(i).charAt(0)));
            miElevations[i].setSelected(false);
            elevationGroup.add(miElevations[i]);
            elevationSubMenu.add(miElevations[i]);
            miElevations[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    JRadioButtonMenuItem    m = (JRadioButtonMenuItem) event.getSource();
                    tools.doSelectElevation(Character.getNumericValue(m.getAccelerator().getKeyChar()));
                    resetToolMenu();
                }
            });
        }
        
        miElevations[0].setSelected(true);      // elevation 0 initially
        toolMenu.add(elevationSubMenu);
        
        toolMenu.addSeparator();
        
        terrainSubMenu = new JMenu("Terrain");
        terrainGroup = new ButtonGroup();
        
        miTerrains = new JRadioButtonMenuItem[MUXHex.TOTAL_PAINTABLE_TERRAIN];
        
        for (int i = 0; i < MUXHex.TOTAL_PAINTABLE_TERRAIN; i++)
        {
            miTerrains[i] = new JRadioButtonMenuItem(MUXHex.nameForId(i));
            miTerrains[i].setAccelerator(KeyStroke.getKeyStroke(MUXHex.terrainForId(i)));
            miTerrains[i].setSelected(false);
            terrainGroup.add(miTerrains[i]);
            terrainSubMenu.add(miTerrains[i]);
            miTerrains[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    JRadioButtonMenuItem    m = (JRadioButtonMenuItem) event.getSource();
                    // Grab the accelerator key and pass that to our tools
                    tools.doSelectTerrain(m.getAccelerator().getKeyChar());
                    resetToolMenu();
                }
            });
            
        }
        
        miTerrains[MUXHex.idForTerrain('.')].setSelected(true);      // plains initially

        toolMenu.add(terrainSubMenu);
        toolMenu.addSeparator();
        
        brushSizeSubMenu = new JMenu("Brush Size");
        brushSizeGroup = new ButtonGroup();
        
        miBrushSizes = new JRadioButtonMenuItem[5];
        
        for (int i = 0; i < 5; i++)
        {
            miBrushSizes[i] = new JRadioButtonMenuItem("Brush Size " + (i + 1));
            miBrushSizes[i].setAccelerator(KeyStroke.getKeyStroke(String.valueOf(i + 1).charAt(0), 
                                                                  Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            miBrushSizes[i].setSelected(false);
            brushSizeGroup.add(miBrushSizes[i]);
            brushSizeSubMenu.add(miBrushSizes[i]);
            miBrushSizes[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    JRadioButtonMenuItem    m = (JRadioButtonMenuItem) event.getSource();
                    String                  title = m.getText();
                    // Can't get the proper character here from the accelerator, so we just grab the last character of the title
                    tools.doSelectBrushSize(Character.getNumericValue(title.charAt(title.length() - 1)));
                    resetToolMenu();
                }
            });
            
        }
        
        miBrushSizes[0].setSelected(true);      // brush size 1 initially
        toolMenu.add(brushSizeSubMenu);
        
        
        // ---
        
        mainMenuBar.add(toolMenu);
    }
    
    public void addHelpMenuItems() {

        miAbout = new JMenuItem("About Thump...");
        helpMenu.add(miAbout).setEnabled(true);
        miAbout.addActionListener(this);

        helpMenu.addSeparator();

        miHelp = new JMenuItem("Read Me / Help...");
        helpMenu.add(miHelp).setEnabled(true);
        miHelp.addActionListener(this);
        
        // ---

        mainMenuBar.add(helpMenu);
        
    }

    // -----------------------------------------
	
    public void addMenus() {
        editMenu = new JMenu("Edit");
        fileMenu = new JMenu("File");
        mapMenu = new JMenu("Map");
        paintMenu = new JMenu("Paint");
        paletteMenu = new JMenu("Palette");
        toolMenu = new JMenu("Tool");
        helpMenu = new JMenu("Help");
        
        addFileMenuItems();
        addEditMenuItems();
        addMapMenuItems();
        addPaintMenuItems();
        addPaletteMenuItems();
        addToolMenuItems();
        addHelpMenuItems();

        setJMenuBar(mainMenuBar);
    }

    // -----------------------------------------

    /**
      * Enable or disable menus according to current situation
      */
    public void resetMenus()
    {
        if (topFrame() != null)
        {
            miCut.setEnabled(topFrame().canCopy());				// We can't cut if we don't have anything selected
            miUndo.setEnabled(topFrame().canUndo());
            miPaste.setEnabled(canPaste());
            miCopy.setEnabled(topFrame().canCopy());
            miDeselectAll.setEnabled(topFrame().canCopy());		// We can't deselect if we don't have anything selected
            
            miSave.setEnabled(true);
            miSaveAs.setEnabled(true);
            miExportToPNG.setEnabled(true);
            miClose.setEnabled(true);

            //miPageSetup.setEnabled(true);
            //miPrint.setEnabled(true);

            miZoomIn.setEnabled(true);
            miZoomOut.setEnabled(true);
        }
        else
        {
            miCut.setEnabled(false);
            miUndo.setEnabled(false);
            miPaste.setEnabled(false);
            miCopy.setEnabled(false);
            miDeselectAll.setEnabled(false);

            miSave.setEnabled(false);
            miSaveAs.setEnabled(false);
            miExportToPNG.setEnabled(false);
            miClose.setEnabled(false);

            //miPageSetup.setEnabled(false);
            //miPrint.setEnabled(false);

            miZoomIn.setEnabled(false);
            miZoomOut.setEnabled(false);
        }
        
        resetToolMenu();
    }
    
    public void resetToolMenu()
    {
        if (tools.selectedTool() == ToolPalette.PAINT_TOOL)
            miPaintTool.setEnabled(true);
        else if (tools.selectedTool() == ToolPalette.SELECT_TOOL)
            miSelectTool.setEnabled(true);
        else if (tools.selectedTool() == ToolPalette.SELECTIVE_UNDO_TOOL)
            miUndoTool.setEnabled(true);
        else if (tools.selectedTool() == ToolPalette.ERASE_TOOL)
            miEraseTool.setEnabled(true);
        
        miElevations[tools.selectedElevation()].setEnabled(true);
        miTerrains[tools.selectedTerrain()].setEnabled(true);
        miBrushSizes[tools.selectedBrushSize() + 1].setEnabled(true);

        if (tools != null && topFrame() != null)
            topFrame().resetCursor();
    }

    /**
      * Reset the inspector palette
      */
    public void updateInspector(Point h)
    {
        tools.updateInspector(h);
    }

    // -----------------------------------------
    
    public void handleAbout() {
        aboutBox.setResizable(false);
        aboutBox.setVisible(true);
        aboutBox.show();
    }

    protected void makeNewMapFrame(File file)
    {
        int			mapSize = 0;
        
        if (file == null)
        {
            // Have to ask how big the map's gonna be....
            String		mapSizeStr = JOptionPane.showInputDialog(this,
                                                             "What size should this new map be?\n(Only square maps are currently supported)",
                                                             "Map Size",
                                                             JOptionPane.QUESTION_MESSAGE);        
            try {
                
                if (mapSizeStr == null)
                {
                    // They cancelled...
                    return;
                }
                
                mapSize = Integer.parseInt(mapSizeStr);
                
                if (mapSize <= 0 || mapSize > 1000)
                    throw new Exception("mapSize must be between 1 and 1000");
                
            } catch (Exception e) {
                ErrorHandler.displayError("The map size must be an whole number between 1 and 1000.", ErrorHandler.ERR_BAD_INPUT);
                // Try again
                makeNewMapFrame(null);
            }
        }
        
        MUXMapFrame		newMap;
        
        newMap = new MUXMapFrame(this, file, mapSize, prefs, DEFAULT_HEIGHT, tools);
        newMap.setVisible(true);

        desktop.add(newMap);

        try {
            newMap.setSelected(true);            
        } catch (Exception e) {
            ErrorHandler.displayError("Error: makeNewMapperFrame: " + e);
        }

        newMap.addInternalFrameListener(this);
    }

    protected MUXMapFrame topFrame()
    {
        if (desktop.getSelectedFrame() instanceof MUXMapFrame)
            return (MUXMapFrame) desktop.getSelectedFrame();
        else
            return null;
    }
    
    // ---------------------------------------------------
    // ActionListener interface (for menus)
    public void actionPerformed(ActionEvent e) {
        if (matchesMenu(e, miNew)) doNew();
        else if (matchesMenu(e, miOpen)) doOpen();
        else if (matchesMenu(e, miClose)) doClose();
        else if (matchesMenu(e, miSave)) doSave();
        else if (matchesMenu(e, miSaveAs)) doSaveAs();
        else if (matchesMenu(e, miExportToPNG)) doExportToPNG();
        else if (matchesMenu(e, miPageSetup)) doPageSetup();
        else if (matchesMenu(e, miPrint)) doPrint();
        else if (matchesMenu(e, miQuit)) doQuit();
        else if (matchesMenu(e, miUndo)) doUndo();
        else if (matchesMenu(e, miCut)) doCut();
        else if (matchesMenu(e, miCopy)) doCopy();
        else if (matchesMenu(e, miPaste)) doPaste();
        else if (matchesMenu(e, miDeselectAll)) doDeselectAll();
        else if (matchesMenu(e, miZoomIn)) doZoom(5);
        else if (matchesMenu(e, miZoomOut)) doZoom(-5);
        else if (matchesMenu(e, miTerrainAndElevation)) doSetPaintType(ToolManager.TERRAIN_AND_ELEVATION);
        else if (matchesMenu(e, miTerrainOnly)) doSetPaintType(ToolManager.TERRAIN_ONLY);
        else if (matchesMenu(e, miElevationOnly)) doSetPaintType(ToolManager.ELEVATION_ONLY);
        else if (matchesMenu(e, miToolPalette)) doHandlePalette(ToolManager.TOOL_PALETTE);
        else if (matchesMenu(e, miTerrainToolPalette)) doHandlePalette(ToolManager.TERRAIN_PALETTE);
        else if (matchesMenu(e, miElevationToolPalette)) doHandlePalette(ToolManager.ELEVATION_PALETTE);
        else if (matchesMenu(e, miBrushToolPalette)) doHandlePalette(ToolManager.BRUSH_PALETTE);
        else if (matchesMenu(e, miInspectorPalette)) doHandlePalette(ToolManager.INSPECTOR_PALETTE);
        else if (matchesMenu(e, miAbout)) doAbout();
        else if (matchesMenu(e, miHelp)) doHelp();

        resetMenus();
    }

    protected boolean matchesMenu(ActionEvent e, JMenuItem mi)
    {
        if (e.getActionCommand().equals(mi.getActionCommand()) && mi.isEnabled())
            return true;
        else
            return false;
    }

    // ----------------------------------------------------------

    /**
      * Handle the 'About' menu item
      */
    public void doAbout()
    {
        AboutBox		about = new AboutBox();
        about.show();
    }

    /**
      * Handle the 'Help' menu item
      */
    public void doHelp()
    {
        ReleaseNotesDialog	helpBox = new ReleaseNotesDialog(this, true);
        helpBox.show();
    }
    
    /**
      * Create a new mapfile to edit
      */
    public void doNew()
    {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        makeNewMapFrame(null);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        resetMenus();
    }

    /**
      * Used for opening a new map file
      */
    public void doOpen()
    {
        FileDialog		openDialog = new FileDialog(this, "Open a Map File...", FileDialog.LOAD);
        openDialog.show();
        
        if (openDialog.getFile() != null)
        {
            File			openFile = new File(openDialog.getDirectory() + openDialog.getFile());
            
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            makeNewMapFrame(openFile);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));            
        }

        resetMenus();
    }

    /**
      * Used when a window is closing
      */
    public void doClose()
    {
        // Close the frame
        if (topFrame() != null)
            closeFrame(topFrame());

        resetMenus();
    }

    /**
      * For use when a file is to be saved to the same location and file name
      * Returns true if the save was successful
      */
    public void doSave()
    {
        if (topFrame() != null)
            saveFrame(topFrame());
    }

    /**
      * For use when a file is to be saved for the first time, or to a different location or file name
      * Returns true if the save was successful
      */
    public void doSaveAs()
    {
        if (topFrame() != null)
            saveFrameAs(topFrame());
    }

    public void doExportToPNG()
    {
        if (topFrame() != null)
        {
            FileDialog		saveImageDialog = new FileDialog(this, "Save as PNG...", FileDialog.SAVE);
            String              fileName = topFrame().fileName();
            
            saveImageDialog.setFile(fileName.substring(0, fileName.length() - 3) + "png");
            saveImageDialog.show();
            
            if (saveImageDialog.getFile() != null)      // if null, cancelled
            {
                try {
                    
                    File        imageFile = new File(saveImageDialog.getDirectory() + saveImageDialog.getFile());
                    
                    // Create the file
                    imageFile.createNewFile();
                    // We're busy
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    // Save it
                    boolean savedOkay = topFrame().saveMapAsImage(imageFile);
                    // We're not busy
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    
                } catch (Exception e) {
                    ErrorHandler.displayError("Can't write to file. Maybe it's locked.", ErrorHandler.ERR_SAVE_FAIL);
                }
            }
        }
    }
    /**
      * Display the page setup dialog box
      */
    public void doPageSetup()
    {
        
    }

    /**
      * Print the document
      */
    public void doPrint()
    {
        if (topFrame() != null)
            topFrame().doPrint();
    }

    /**
      * Quit
      */
    public void doQuit()
    {
        JInternalFrame		maps[];
        
        maps = desktop.getAllFramesInLayer(JLayeredPane.DEFAULT_LAYER.intValue());	// Don't want the palettes

        for (int i = 0; i < maps.length; i++)
        {
            if (!closeFrame((MUXMapFrame) maps[i]))
                return;									// apparently we don't want to quit!
        }
        
        // Write our preferences
        writePrefs();

        // Exit cleanly
        System.exit(0);
    }

    // ------------------------

    /**
      * Do any cleanup related to closing an internal frame
      */
    protected void disposeOfFrame(MUXMapFrame f)
    {
        prefs.mapFrameLoc = f.getLocation();
        f.dispose();

        // Hack - write the prefs
        writePrefs();
    }
    
    // ------------------------
    
    public void doUndo()
    {
        // Pass it on to the frame
        topFrame().doUndo();

        // Disable the undo item?
        if (!topFrame().canUndo())
            miUndo.setEnabled(false);
    }
    
    // public void doRedo() {}
	
    public void doCut()
    {
        copyableHexes = topFrame().copyableHexes();
        topFrame().doCut();
    }
	
    public void doCopy()
    {
        copyableHexes = topFrame().copyableHexes();
    }
	
    public void doPaste()
    {
        if (copyableHexes != null)
        {
            topFrame().pasteHexes(copyableHexes);
        }
    }
	
    public void doDeselectAll()
    {
        topFrame().deselectAll();
    }

    // ------------------------

    public void doZoom(int z)
    {
        if (topFrame() != null)
            topFrame().adjustZoom(z, new Point(-1, -1));
    }

    public void doSetPaintType(int type)
    {
        prefs.paintType = type;
        miTerrainAndElevation.setState(type == ToolManager.TERRAIN_AND_ELEVATION);
        miTerrainOnly.setState(type == ToolManager.TERRAIN_ONLY);
        miElevationOnly.setState(type == ToolManager.ELEVATION_ONLY);

        JInternalFrame		allFrames[] = desktop.getAllFramesInLayer(JLayeredPane.DEFAULT_LAYER.intValue());
        // Reset cursors in top frame
        topFrame().resetCursor();
        /* // don't need to do it for EVERY frame
        for (int i = 0; i < allFrames.length; i++)
            ((MUXMapFrame) allFrames[i]).resetCursor();
         */
            
    }

    public void doHandlePalette(int palette)
    {
        switch (palette)
        {
            case ToolManager.TOOL_PALETTE:
                prefs.showTools = !prefs.showTools;
                miToolPalette.setState(prefs.showTools);
                tools.setPaletteVisible(ToolManager.TOOL_PALETTE, prefs.showTools);
                break;

            case ToolManager.TERRAIN_PALETTE:
                prefs.showTerrain = !prefs.showTerrain;
                miTerrainToolPalette.setState(prefs.showTerrain);
                tools.setPaletteVisible(ToolManager.TERRAIN_PALETTE, prefs.showTerrain);
                break;

            case ToolManager.ELEVATION_PALETTE:
                prefs.showElevation = !prefs.showElevation;
                miElevationToolPalette.setState(prefs.showElevation);
                tools.setPaletteVisible(ToolManager.ELEVATION_PALETTE, prefs.showElevation);
                break;

            case ToolManager.BRUSH_PALETTE:
                prefs.showBrush = !prefs.showBrush;
                miBrushToolPalette.setState(prefs.showBrush);
                tools.setPaletteVisible(ToolManager.BRUSH_PALETTE, prefs.showBrush);
                break;

            case ToolManager.INSPECTOR_PALETTE:
                prefs.showInspector = !prefs.showInspector;
                miInspectorPalette.setState(prefs.showInspector);
                tools.setPaletteVisible(ToolManager.INSPECTOR_PALETTE, prefs.showInspector);
                break;
        }
    }
    
    // ------------------------
    
    public void paint(Graphics g) {
        super.paint(g);
    }
    
    // ------------------------

    /**
      * Performs the dirty work of closing a frame
      * This function returns true unless some cancelling action occurs
      */
    public boolean closeFrame(MUXMapFrame f)
    {
        if (f != null)
        {
            if (f.hasChanged())
            {
                String		messageText = "The map \"" + f.fileName() + "\" has changed. Do you wish to save it before closing?";
                int			optionChosen = JOptionPane.showConfirmDialog(this,
                                                                   messageText,
                                                                   "Save map?",
                                                                   JOptionPane.YES_NO_CANCEL_OPTION);

                if (optionChosen == JOptionPane.CANCEL_OPTION || optionChosen == JOptionPane.CLOSED_OPTION)
                {
                    // They cancelled?
                    return false;
                }
                else if (optionChosen == JOptionPane.NO_OPTION)
                {
                    // Don't want to save
                    disposeOfFrame(f);
                    return true;
                }
                else
                {
                    // Want to save
                    boolean 		saveOkay;

                    if (f.newFile())
                        saveOkay = saveFrameAs(f);
                    else
                        saveOkay = saveFrame(f);

                    if (saveOkay)
                        disposeOfFrame(f);		// It's done, so get rid of it
                    
                    return saveOkay;
                }
            }
            else
            {
                // Hasn't changed - just close it
                disposeOfFrame(f);
                return true;
            }
        }

        return true;
    }

    /**
      * Does the dirty work of saving a frame with a new name
      * This function returns true if everything went a-okay, false if some error or a cancel occured
      */
    public boolean saveFrameAs(MUXMapFrame f)
    {
        FileDialog		saveDialog = new FileDialog(this, "Save a Map File...", FileDialog.SAVE);
        saveDialog.setFile(f.fileName());
        saveDialog.show();

        if (saveDialog.getFile() != null)
        {
            try {

                File			saveAsFile = new File(saveDialog.getDirectory() + saveDialog.getFile());
                
                // Create the file
                saveAsFile.createNewFile();
                // We're busy
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                // Save it
                boolean savedOkay = f.saveMapAs(saveAsFile);
                // We're not busy
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                
                return savedOkay;
                
            } catch (Exception e) {
                ErrorHandler.displayError("Can't write to file. Maybe it's locked.", ErrorHandler.ERR_SAVE_FAIL);
                return false;
            }
        }
        else
        {
            // Cancelled?
            return false;
        }
    }

    /**
     * Does the dirty work of saving a frame
     * This function returns true if everything went a-okay, false if some error or a cancel occured
     */
    public boolean saveFrame(MUXMapFrame f)
    {
        if (f != null)
        {
            if (f.newFile())		// New file - no location information...
                return saveFrameAs(f);
            else if (f.hasChanged())
                return f.saveMap();
            else
                return true;				// Nothing to do
        }

        return true;			// saving of nothing went okay? suuure...
    }

    // ----------------------------------------------------------------------------
    // ****************************************************************************
    // Listeners
    
    public void internalFrameClosing(InternalFrameEvent e)
    {
        // User is closing a window... supposedly if we don't close or dispose, it won't actually close the frame
        closeFrame((MUXMapFrame) e.getInternalFrame());
    }

    public void internalFrameClosed(InternalFrameEvent e)
    {
        // Reset our menus
        resetMenus();
    }

    public void internalFrameOpened(InternalFrameEvent e)
    {
        // Reset our menus
        resetMenus();
    }

    public void internalFrameDeiconified(InternalFrameEvent e)
    {
        // Reset our menus
        resetMenus();
    }

    public void internalFrameIconified(InternalFrameEvent e)
    {
        // Reset our menus
        resetMenus();
    }
    
    public void internalFrameActivated(InternalFrameEvent e)
    {
        // Reset our menus
        resetMenus();
    }

    public void internalFrameDeactivated(InternalFrameEvent e)
    {
        // Reset our menus
        resetMenus();
    }

    // ----------------------------

    public void windowActivated(WindowEvent e) {}

    public void windowClosed(WindowEvent e)
    {
        // Save our prefs!
        writePrefs();
    }

    public void windowClosing(WindowEvent e) {}

    public void windowDeactivated(WindowEvent e) {}

    public void windowDeiconified(WindowEvent e) {}

    public void windowIconified(WindowEvent e) {}

    public void windowOpened(WindowEvent e) {}

    // ------------------------------

    public void keyPressed(KeyEvent e) {}

    public void keyReleased(KeyEvent e) {}

    public void keyTyped(KeyEvent e) {}
    
    // ----------------------------------------------------------------------------
    // ****************************************************************************

    // Read our prefs from disk
    public void readPrefs()
    {
        // Create some prefs
        prefs = new MPrefs();
        
        try
        {
            File	prefsFile = new File(MPrefs.PREFS_FILE_NAME);
    
            if (prefsFile.createNewFile())
                prefs.defaultPrefs();
            else
            {
                FileInputStream		fis = new FileInputStream(prefsFile);
                ObjectInputStream 	ois = new ObjectInputStream(fis);
    
                prefs = (MPrefs) ois.readObject();
                fis.close();
            }
        }
        catch (Exception e)
        {
            // Maybe the file format changed. Let's just create some new prefs
            prefs.defaultPrefs();
        }
        
    }

    // Write our prefs to disk
    public void writePrefs()
    {
        // Make sure a few prefs are set properly
        prefs.desktopLoc = getLocation();
        prefs.desktopSize = getSize();

        // The palettes too
        tools.updatePaletteLocs();

        // Write it
        try
        {
            File	prefsFile = new File(MPrefs.PREFS_FILE_NAME);
            prefsFile.createNewFile();
            FileOutputStream	fis = new FileOutputStream(prefsFile);
            ObjectOutputStream 	oos = new ObjectOutputStream(fis);

            oos.writeObject(prefs);
            oos.flush();

            fis.close();
        }
        catch (Exception e)
        {
            System.out.println("Error: writePrefs: " + e);
        }
    }

    // Can we paste?
    protected boolean canPaste()
    {
        if (copyableHexes != null && copyableHexes.size() > 0)
            return true;
        else
            return false;
    }
   
    // ----------------------------------------------------------------

    public Thump() {

        super("");
        // The ResourceBundle below contains all of the strings used in this application.  ResourceBundles
        // are useful for localizing applications - new localities can be added by adding additional
        // properties files.
        Toolkit.getDefaultToolkit();
        CustomCursors.createCustomCursors();
        resbundle = ResourceBundle.getBundle("Thumpstrings", Locale.getDefault());
        setTitle("Thump");

        // Read our preferences
        readPrefs();
        
        desktop = new JDesktopPane();
        setContentPane(desktop);
        setSize(prefs.desktopSize);
        setLocation(prefs.desktopLoc);
        
        addMenus();
        addWindowListener(this);
        addKeyListener(this);

        tools = new ToolManager(desktop, prefs);
        
        aboutBox = new AboutBox();

        // Reset our menus
        resetMenus();
        
        setVisible(true);
    }
    
    // ----------------------------------------------------------------

    public static void main(String args[])
    {
        // "javax.swing.plaf.metal.MetalLookAndFeel"
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) { }
        
        
        new Thump();
    }

}
