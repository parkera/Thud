//
//  MUXMapFrame.java
//  Thud
//
//  Created by asp on Wed Nov 28 2001.
//  Copyright (c) 2001-2002 Anthony Parker. All rights reserved.
//  Please see LICENSE.TXT for more information.
//
//
package btthud.ui;

import btthud.data.*;
import btthud.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.font.*;

import javax.swing.*;
import javax.swing.text.*;

import java.lang.*;
import java.util.*;
import java.awt.print.*;

import java.io.*;

public class MUXMapFrame extends JInternalFrame implements MouseListener, MouseMotionListener, MouseWheelListener {

    Thump				mapper;
    
    MUXMap					map;
    MPrefs					prefs;
    
    Thread					thread = null;
    private boolean			go = true;

    MUXMapComponent			mapComponent = null;
    JScrollPane				scrollPane = null;

    File					file;

    int						h;

    static int				documentsOpened = 1;		// Only for naming new untitled docs

    boolean					newFile;

    ToolManager				tools;

    static final int		MAX_UNDO = 20;
    LinkedList				undoableChanges;

    boolean					dragging = false;					// True when we've detected a drag
    LinkedList				changedHexes = new LinkedList();	// List of changed hexes this drag or click
    
    boolean					pasting = false;					// True when we're pasting some hexes
    LinkedList				pasteHexes;							// List of hexes to paste
    
    // -----------------
    
    public MUXMapFrame(Thump mapper, File file, int mapSize, MPrefs prefs, int hexHeight, ToolManager tools)
    {
        super("Untitled");
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        this.mapper = mapper;
        this.tools = tools;
        this.file = file;
        
        if (file == null)
        {
            map = new MUXMap(mapSize, mapSize);
            map.clearMap();
            
            setTitle("Untitled " + documentsOpened + sizeString());
            this.file = new File("Untitled " + documentsOpened);
            documentsOpened++;
            newFile = true;
        }
        else
        {
            readMap();
            setTitle(file.getName() + sizeString());
            newFile = false;
        }

        // Set a bunch of 'able's
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setClosable(true);

        // Let the InternalFrameListener handle this
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        
        this.prefs = prefs;
        this.h = hexHeight;

        // Setup the map component
        mapComponent = new MUXMapComponent(map, prefs, h);
        mapComponent.setPreferredSize(new Dimension(mapComponent.getTotalWidth(), mapComponent.getTotalHeight()));
        
        mapComponent.addMouseListener(this);
        mapComponent.addMouseMotionListener(this);
        mapComponent.addMouseWheelListener(this);
        
        resetCursor();
        
        // Setup the rulers (hex indicators on the side/top)
        Rule 	xRule = new Rule(Rule.HORIZONTAL, h, map.getSizeX(), prefs.hexNumberFontSize);
        Rule	yRule = new Rule(Rule.VERTICAL, h, map.getSizeY(), prefs.hexNumberFontSize);
        xRule.setPreferredWidth(mapComponent.getTotalWidth());
        yRule.setPreferredHeight(mapComponent.getTotalHeight());

        // .. and the scroll pane we put them in
        scrollPane = new JScrollPane(mapComponent);
        scrollPane.setColumnHeaderView(xRule);
        scrollPane.setRowHeaderView(yRule);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, new Corner());
        scrollPane.doLayout();

        setContentPane(scrollPane);

        // Our size, loc
        setSize(prefs.mapFrameSize);
        setLocation((int) prefs.mapFrameLoc.getX() + ((documentsOpened-1)*10 % (int) prefs.desktopSize.getWidth()),
                    (int) prefs.mapFrameLoc.getY() + ((documentsOpened-1)*10 % (int) prefs.desktopSize.getHeight()));

        // Setup undo
        undoableChanges = new LinkedList();
        
        // Show the window now
        this.show();
        
        // We're done
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void resetCursor()
    {
        if (pasting)
            mapComponent.setCursor(CustomCursors.getPasteCursor());
        else if (prefs.paintType == ToolManager.TERRAIN_ONLY)
            mapComponent.setCursor(CustomCursors.getTerrainCursor());
        else if (prefs.paintType == ToolManager.ELEVATION_ONLY)
            mapComponent.setCursor(CustomCursors.getElevationCursor());
        else if (tools.selectedTool() == ToolPalette.SELECTIVE_UNDO_TOOL)
            mapComponent.setCursor(CustomCursors.getUndoCursor());
        else
            mapComponent.setCursor(CustomCursors.getCrosshairCursor());
    }
    // --------------------------------------

    public void adjustZoom(int z)
    {
        h += z;

        if (h < 5)
            h = 5;
        if (h > 300)
            h = 300;

        newPreferences(prefs, h);

        updateUI();
    }
    
    public void newPreferences(MPrefs prefs, int hexHeight)
    {
        // Send the new prefs to our component
        mapComponent.newPreferences(prefs, hexHeight);
        h = hexHeight;

        mapComponent.setPreferredSize(new Dimension(mapComponent.getTotalWidth(), mapComponent.getTotalHeight()));

        Rule 	xRule = new Rule(Rule.HORIZONTAL, h, map.getSizeX(), prefs.hexNumberFontSize);
        Rule	yRule = new Rule(Rule.VERTICAL, h, map.getSizeY(), prefs.hexNumberFontSize);
        xRule.setPreferredWidth(mapComponent.getTotalWidth());
        yRule.setPreferredHeight(mapComponent.getTotalHeight());

        scrollPane.setColumnHeaderView(xRule);
        scrollPane.setRowHeaderView(yRule);
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, new Corner());
        scrollPane.doLayout();
    }

    /**
      * Read values for a map from a File
      */
    void readMap()
    {
        try {
            FileReader		mapFile = new FileReader(file);
            StringBuffer	buf = new StringBuffer();
            int				read;
            boolean			done = false;
            
            while (!done)
            {
                read = mapFile.read();
                if (read == -1)
                    done = true;
                else
                    buf.append((char) read);
            }

            mapFile.close();
            
            parseMap(buf.toString());
            
        } catch (Exception e) {
            ErrorHandler.displayError("Could not read the map file data.", ErrorHandler.ERR_OPEN_FAIL);
        }
    }

    /**
      * Parse the data read in from the file
      */
    void parseMap(String mapData)
    {
        try {

            StringTokenizer	st = new StringTokenizer(mapData);

            int		sizeX, sizeY;
            int		x = 0;
            int		y = 0;
            int		e;
            char	t;

            // Get the size of the map
            sizeX = Integer.parseInt(st.nextToken());
            sizeY = Integer.parseInt(st.nextToken());

            map = new MUXMap(sizeX, sizeY);
            
            // Loop thru each line
            while (st.hasMoreTokens())
            {
                String			thisLine = st.nextToken();

                // Think the map is over if we hit this thing
                if (thisLine.equals("-1") || thisLine.startsWith("-1 "))
                    break;
                
                for (x = 0; x < sizeX * 2; x+=2)
                {
                    // Get terrain and elevation, then set the data in the map
                    t = thisLine.charAt(x);
                    e = Character.digit(thisLine.charAt(x + 1), 10);
                    map.setHex(x/2, y, t, e);
                }

                // One more line down in y
                y++;
            }            
        } catch (Exception e) {
            System.out.println("Error: readMap: " + e);
        }
        
    }

    /**
      * Writes a map to a different file
      */
    public boolean saveMapAs(File newFile)
    {
        this.file = newFile;

        return saveMap();
    }
    
    /**
      * Writes a map to the file specified in the class
      * Returns true if success, otherwise false
      */
    public boolean saveMap()
    {
        // This should probably write all the data to a temporary file, then copy it over the old one at the end
        try {

            StringBuffer		buf = new StringBuffer();
            FileWriter			mapWriter;
            
            if (!file.canWrite())
            {
                ErrorHandler.displayError("Could not write to map file. Maybe it's locked.", ErrorHandler.ERR_SAVE_FAIL);
                return false;
            }

            file.createNewFile();
            mapWriter = new FileWriter(file);
            
            // Write the size in x, y
            buf.append(map.getSizeX());
            buf.append(" ");
            buf.append(map.getSizeY());
            buf.append("\n");

            // Write all of the data
            for (int y = 0; y < map.getSizeY(); y++)
            {
                for (int x = 0; x < map.getSizeX(); x++)
                {
                    buf.append(MUXHex.terrainForId(map.getHexTerrain(x, y)));
                    buf.append(map.getHexAbsoluteElevation(x, y));
                }

                buf.append("\n");
            }

            // Now write it to the file
            mapWriter.write(buf.toString());
            mapWriter.close();

            // Musta been okay...
            map.setChanged(false);

            // Change our name
            setTitle(file.getName() + sizeString());
            
            return true;
            
        } catch (Exception e) {
            ErrorHandler.displayError("Could not write data to map file:\n" + e, ErrorHandler.ERR_SAVE_FAIL);
            return false;
        }
    }

    // ----------------------------------------------------------------------------
    // ****************************************************************************

    protected boolean terrainToolClicked(ListIterator it)
    {
        Point		h = null;
        boolean         hadHexes = it.hasNext();
        
        // Go through the list and get rid of hexes we don't want to change
        // Also, set the data in the map object
        while (it.hasNext())
        {
            h = (Point) it.next();
            if (map.getHexTerrain(h) != tools.selectedTerrain())
            {
                if (!hexAlreadyChanged(h))
                    changedHexes.addLast(new ChangedMUXHex(h, map.getHex(h)));
                map.setHexTerrain(h, tools.selectedTerrain());
                mapComponent.repaint(mapComponent.rectForHex(h));
            }
            else
                it.remove();
        }

        return hadHexes;
    }

    // ----
    
    protected boolean elevationToolClicked(ListIterator it)
    {
        Point           h;
        boolean         hadHexes = it.hasNext();
        
        // Go through the list and get rid of hexes we don't want to change
        // Also, set the data in the map object
        while (it.hasNext())
        {
            h = (Point) it.next();
            if (map.getHexElevation(h) != tools.selectedElevation())
            {
                if (!hexAlreadyChanged(h))
                    changedHexes.addLast(new ChangedMUXHex(h, map.getHex(h)));
                map.setHexElevation(h, tools.selectedElevation());
                mapComponent.repaint(mapComponent.rectForHex(h));
            }
            else
                it.remove();
        }

        return hadHexes;
    }

    // ----

    protected boolean pasteTool(ArrayList hexes, ArrayList terrain, ArrayList elevation)
    {
        boolean		changedHex = false;
        for (int i = 0; i < hexes.size(); i++)
        {
            // We need to make sure we actually changed something while we are pasting
            if (map.getHexTerrain((Point) hexes.get(i)) != ((Integer) terrain.get(i)).intValue() ||
                    map.getHexElevation((Point) hexes.get(i)) != ((Integer) elevation.get(i)).intValue())
            {
                changedHex = true;
                changedHexes.addLast(new ChangedMUXHex((Point) hexes.get(i), map.getHex((Point) hexes.get(i))));
                map.setHex((Point) hexes.get(i),
                            ((Integer) terrain.get(i)).intValue(),
                            ((Integer) elevation.get(i)).intValue());
                mapComponent.repaint(mapComponent.rectForHex((Point) hexes.get(i)));
            }
        }

        return changedHex;
    }

    // --------------------------
                                                 
    protected boolean bothToolClicked(ListIterator it, boolean erase)
    {
        Point           h;
        int		selectedTerrain = tools.selectedTerrain();
        int		selectedElevation = tools.selectedElevation();
        boolean         hadHexes = it.hasNext();

        if (erase)
        {
            selectedTerrain = MUXHex.PLAIN;
            selectedElevation = 0;
        }

        // Go through the list and get rid of hexes we don't want to change
        // Also, set the data in the map object
        while (it.hasNext())
        {
            h = (Point) it.next();
            if (map.getHexElevation(h) != selectedTerrain || map.getHexTerrain(h) != selectedElevation)
            {
                // This is a hex we will change
                if (!hexAlreadyChanged(h))
                    changedHexes.addLast(new ChangedMUXHex(h, map.getHex(h)));
                map.setHex(h, selectedTerrain, selectedElevation);
                mapComponent.repaint(mapComponent.rectForHex(h));
            }
            else
                it.remove();
        }

        return hadHexes;
    }

    // ----
    
    protected boolean selectToolClicked(ListIterator it, MouseEvent e)
    {
        Point                   h;
        ListIterator		selectedIt;
        Rectangle2D             r = new Rectangle2D.Double();
        
        // If control is down, then a control-click removes the selection
        if (e.isControlDown())
        {
            map.deselectAll();
            mapComponent.repaint();
        }
        else
        {
            while (it.hasNext())
            {
                h = (Point) it.next();
                if (!map.getHexSelected(h))
                {
                    map.setHexSelected(h, true);
                    mapComponent.repaint(mapComponent.rectForHex(h));
                }
                else
                    it.remove();
            }
            
            // Now repaint all the selected hexes        
            if (map.anyHexesSelected())
            {
                selectedIt = map.selectedHexesIterator();
                
                while (selectedIt.hasNext())
                {
                    // Get the next hex
                    h = (Point) selectedIt.next();
                    mapComponent.expandedRectForHex(h, r);
                    mapComponent.repaint(r);
                }                
            }            
        }

        mapper.resetMenus();

        // didn't change anything (no need for an undo)
        return false;
    }
    
    // ----------------------------------------------------------------------------
    // ****************************************************************************

    /**
      * Handle a click on a hex
      */
    protected boolean hexClicked(Point hex, MouseEvent e)
    {
        int                     whichTool = tools.selectedTool();
        LinkedList		hexes = new LinkedList();
        Point			thisHex;
        
        // Add all the affected hexes to our LinkedList
        for (int i = 0; i <= tools.selectedBrushSize(); i++)
        {
            for (int j = 0; j < BrushToolOptions.brushHexSizes[i]; j++)
            {
                thisHex = new Point((int) (hex.getX() + BrushToolOptions.brushX[i][j]),
                                    (int) (hex.getY() + BrushToolOptions.brushY[hex.getX() % 2 == 1 ? 1 : 0][i][j]));

                if (map.validHex(thisHex))
                    hexes.add(thisHex);
            }
        }

        // Now send it off to see which tool deals with it
        if (tools.selectedTool() == ToolPalette.SELECTIVE_UNDO_TOOL ||
            (e.getModifiers() & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK ||
            (e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)
        {
            return selectiveUndoClicked(hex);
        }
        else if (tools.selectedTool() == ToolPalette.PAINT_TOOL)
        {
            if (prefs.paintType == ToolManager.TERRAIN_ONLY)
                return terrainToolClicked(hexes.listIterator(0));
            else if (prefs.paintType == ToolManager.ELEVATION_ONLY)
                return elevationToolClicked(hexes.listIterator(0));
            else
                return bothToolClicked(hexes.listIterator(0), false);
        }
        else if (tools.selectedTool() == ToolPalette.SELECT_TOOL)
        {
            return selectToolClicked(hexes.listIterator(0), e);            
        }
        else if (tools.selectedTool() == ToolPalette.ERASE_TOOL)
        {
            return bothToolClicked(hexes.listIterator(0), true);		// Erase
        }

        return false;
    }

    // ----

    /**
        * Handle a selective undo on a hex (undo only this hex from the last undo)
     */
    public boolean selectiveUndoClicked(Point h)
    {
        LinkedList				lastChangedHexes;
        ListIterator			it, lastIt;
        ChangedMUXHex			changedHex;

        if (!canUndo())
            return false;				// Nothing to do

        // Check to see if we have any data on this hex in any of our undos
        // have to go /backwards/ in the Undoable changes.. the last item is the most recent
        it = undoableChanges.listIterator(undoableChanges.size());

        while (it.hasPrevious())
        {
            lastChangedHexes = (LinkedList) it.previous();

            // Now iterate through the hexes changed in this change (order doesn't matter here, each hex appears only once)
            lastIt = lastChangedHexes.listIterator();
            
            while (lastIt.hasNext())
            {
                changedHex = (ChangedMUXHex) lastIt.next();
                if (changedHex.getLocation().getX() == h.getX() &&
                    changedHex.getLocation().getY() == h.getY())
                {
                    // Match!
                    map.setHex(changedHex.getLocation(), changedHex.getPrevTerrain(), changedHex.getPrevElevation());
                    mapComponent.repaint(mapComponent.rectForHex(changedHex.getLocation()));
                    // We've found our match, so let's get out of this nasty looping
                    return false;
                }
            }            
        }

        // Always return false
        return false;
    }
    
    // ----
    
    protected boolean pasteHexClicked(Point hex)
    {
        boolean			changedHex = false;
        
        if (!pasteHexes.isEmpty())
        {
            changedHexes = new LinkedList();
            
            int				x = (int) hex.getX();
            int				y = (int) hex.getY();
            ListIterator	it = pasteHexes.listIterator();
            CopyableMUXHex	copyThisHex;
            Point			relativeHex, thisHex;
            ArrayList		hexes = new ArrayList();
            ArrayList		terrains = new ArrayList();
            ArrayList		elevations = new ArrayList();
            boolean			pastedIsEven, locIsEven;
            int				evenXAdjust = 0;
            int				oddXAdjust = 0;
            
            // figure out what even and odd situation we're in
            locIsEven = x % 2 == 0 ? true : false;
            pastedIsEven = ((CopyableMUXHex) it.next()).isEven();

            if ((pastedIsEven && locIsEven) || (!pastedIsEven && !locIsEven))
            {
                // Even->Even or Odd->Odd
                evenXAdjust = 0;		// no problem here
                oddXAdjust = 0;
            }
            else if (pastedIsEven && !locIsEven)
            {
                // Even->Odd
                evenXAdjust = -1;
                oddXAdjust = 0;
            }
            else if (!pastedIsEven && locIsEven)
            {
                // Odd->Even
                evenXAdjust = 0;
                oddXAdjust = 1;                
            }
            
            // go back to where we started
            it.previous();
            
            while (it.hasNext())
            {
                copyThisHex = (CopyableMUXHex) it.next();
                thisHex = new Point(x + (int) copyThisHex.getDx(),
                                    y + (int) copyThisHex.getDy());

                // We have to store the x,y to base the next hex off of before we adjust it, otherwise things go beserk
                x = (int) thisHex.getX();
                y = (int) thisHex.getY();

                if ((int) thisHex.getX() % 2 == 0)
                    thisHex.translate(0, evenXAdjust);
                else
                    thisHex.translate(0, oddXAdjust);

                // Add this hex to our list of hexes to change
                if (map.validHex(thisHex))
                {
                    hexes.add(thisHex);
                    terrains.add(new Integer(copyThisHex.getTerrain()));
                    elevations.add(new Integer(copyThisHex.getElevation()));
                }
            }

            changedHex = pasteTool(hexes, terrains, elevations);

            // Add these hexes to our Undoable list
            if (changedHex)
                addUndoableHexChange(changedHexes);
        }

        pasting = false;
        resetCursor();
        return changedHex;
    }
    
    // -----------------------------

    public void mouseClicked(MouseEvent e)
    {
        Point			hex = mapComponent.realToHex(e.getX(), e.getY());
        
        // Make a new list
        changedHexes = new LinkedList();

        if (map.validHex(hex))
        {
            boolean			hexesChanged = false;
            
            if (pasting && !pasteHexes.isEmpty())
                pasteHexClicked(hex);
            else
                hexesChanged = hexClicked(hex, e);

            // Add this click to our undoable list
            if (hexesChanged)
                addUndoableHexChange(changedHexes);            
        }
    }

    public void mouseDragged(MouseEvent e)
    {
        Point		hex = mapComponent.realToHex(e.getX(), e.getY());

        // Note that we're dragging the mouse
        dragging = true;

        if (map.validHex(hex))
            hexClicked(hex, e);
    }

    public void mouseMoved(MouseEvent e)
    {
        Point		hex = mapComponent.realToHex(e.getX(), e.getY());
        
        // Update the inspector
        if (map.validHex(hex))
            mapper.updateInspector(hex);

    }
    
    public void mouseEntered(MouseEvent e)
    {
        // Set the proper cursor
        resetCursor();
    }

    public void mouseExited(MouseEvent e)
    {
        // Clear the inspector
        mapper.updateInspector(null);
    }

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e)
    {
        // If we were dragging, then add the list of currently changed hexes to our undoable list
        if (dragging)
        {
            // Add this list of hexes to our undoable
            if (changedHexes.size() > 0)
                addUndoableHexChange(changedHexes);

            // Reset some things
            dragging = false;
            changedHexes = new LinkedList();
        }
    }

    public void mouseWheelMoved(MouseWheelEvent e)
    {
        int 		rotateAmount = e.getWheelRotation();
        
        // Negative rotateAmount means up/away from user (zoom in), positive means down/towards user (zoom out)
        adjustZoom(-rotateAmount * 5);
    }
    
    // ----------------------------

    protected boolean hexAlreadyChanged(Point p)
    {
        ListIterator			it = changedHexes.listIterator();

        while (it.hasNext())
        {
            if (((ChangedMUXHex) it.next()).matchesHex(p))
                return true;
        }

        return false;
    }
    
    // ----------------------------
    
    protected void addUndoableHexChange(LinkedList changedHexes)
    {
        if (changedHexes.size() > 0)
        {
            // We changed some stuff - add this list of hexes to our list of undoable stuff
            undoableChanges.addLast(changedHexes);

            // we only keep a limited size of undo
            if (undoableChanges.size() > MAX_UNDO)
                undoableChanges.removeFirst();

            // Tell our menu bar to enable the undo button
            mapper.resetMenus();
        }
    }
    
    /**
      * Handle an undo event
      */
    public void doUndo()
    {
        // Get the last thing we did and reverse it, then remove it from the undoable stuff
        // This process is similar to 'Both Tool' being clicked on a hex with old values
        LinkedList			lastChange = (LinkedList) undoableChanges.getLast();

        if (lastChange != null)
        {
            ListIterator		it = lastChange.listIterator(0);
            ChangedMUXHex		changedHex;

            while (it.hasNext())
            {
                changedHex = (ChangedMUXHex) it.next();
                // We'll assume the hex is valid since it's in this list
                map.setHex(changedHex.getLocation(), changedHex.getPrevTerrain(), changedHex.getPrevElevation());
                mapComponent.repaint(mapComponent.rectForHex(changedHex.getLocation()));
            }

            // Repaint the hexes
            //mapComponent.repaint();

            // Remove what we just undid from our undoable list
            undoableChanges.removeLast();
        }
    }

    // -----------------------------
    
    /**
      * Can we undo anything?
      */
    public boolean canUndo()
    {
        if (undoableChanges == null || undoableChanges.size() > 0)
            return true;
        else
            return false;
    }

    public boolean canCopy()
    {
        return map.anyHexesSelected();
    }

    // -----------------------------
    /**
      * Has our map data changed?
      */
    public boolean hasChanged()
    {
        return map.hasChanged();
    }

    // ------------------------------------------------
    // ************************************************
    // Cut&Paste + Selection stuff

    /**
      * Paste in some stuff (sets the pasting flag)
      */
    public void pasteHexes(LinkedList hexes)
    {
        pasting = true;
        resetCursor();
        pasteHexes = hexes;
    }
    
    /**
      * Clears our deselectable hexes
      */
    public void deselectAll()
    {
        map.deselectAll();
        mapComponent.repaint();
        mapper.resetMenus();
    }

    /**
      * Erase our selected hexes
      */
    public void doCut()
    {
        bothToolClicked(map.selectedHexesIterator(), true);
    }

    /**
      * Handle printing of this frame
      */
    public void doPrint()
    {
        PrinterJob printJob = PrinterJob.getPrinterJob();

        printJob.setPrintable((Printable) mapComponent);
        if (printJob.printDialog()) {
            try {
                printJob.print();
            } catch (Exception e) {
                ErrorHandler.displayError("An error occured during printing:\n" + e, ErrorHandler.ERR_PRINT_FAIL);
            }
        }
    }
    
    /**
      * Return a list of the copyable hexes
      */
    public LinkedList copyableHexes()
    {
        LinkedList		copyableHexes = new LinkedList();
        ListIterator		it = map.selectedHexesIterator();
        int			x = 0;
        int			y = 0;
        Point                   h;
        
        if (!map.anyHexesSelected())
            return null;

        // Create our new list of copyable hexes

        // Get the first hex, initialize x,y (the delta between this and the next hex)
        h = (Point) it.next();
        x = (int) h.getX();
        y = (int) h.getY();
        // Make sure to set the 'isEven' argument (last one)
        copyableHexes.addLast(new CopyableMUXHex(map.getHexTerrain(h), map.getHexElevation(h), 0, 0, x % 2 == 0 ? true : false));
        
        while (it.hasNext())
        {
            // Get the next hex
            h = (Point) it.next();
            copyableHexes.addLast(new CopyableMUXHex(map.getHexTerrain(h), map.getHexElevation(h), (int) h.getX() - x, (int) h.getY() - y));
            // Set up these values for our next hex
            x = (int) h.getX();
            y = (int) h.getY();
        }

        return copyableHexes;
    }

    // ---------------------------------------------------------------
    
    /**
      * Get file name
      */
    public String fileName()
    {
        return file.getName();
    }

    /**
      * Returns true if this is a new file (ie, can be saved with just a 'Save')
      */
    public boolean newFile()
    {
        return newFile;
    }

    /**
      * Returns a string representing the size of our map (used in title)
      */
    protected String sizeString()
    {
        return (" (" + map.getSizeX() + " x " + map.getSizeY() + ")");
    }

}
