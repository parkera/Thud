//
//  CustomCursors.java
//  Thump
//
//  Created by Anthony Parker on Sat Jan 18 2003.
//  Copyright (c) 2003 Anthony Parker. All rights reserved.
//

package btthud.ui;

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;

import btthud.data.*;

public class CustomCursors {

    public static Cursor				crosshairCursor, terrainCursor, elevationCursor, pasteCursor, undoCursor;
    public static ImageIcon				terrainIcons[] = new ImageIcon[MUXHex.TOTAL_PAINTABLE_TERRAIN];
    public static ImageIcon				paintIcon, selectIcon, selectiveUndoIcon, eraseIcon;
    
    public CustomCursors()
    {
    }
    
    static public void createCustomCursors()
    {
        try {
            ClassLoader 	cl = ClassLoader.getSystemClassLoader();
            
            Image			crosshair = new ImageIcon(cl.getResource("cursors/crosshair.gif")).getImage();
            Image			terrain = new ImageIcon(cl.getResource("cursors/terrainonly.gif")).getImage();
            Image			elevation = new ImageIcon(cl.getResource("cursors/elevonly.gif")).getImage();
            Image			paste = new ImageIcon(cl.getResource("cursors/paste.gif")).getImage();
            Image			undo = new ImageIcon(cl.getResource("cursors/undo.gif")).getImage();

            crosshairCursor = createOneCursor(crosshair, new Point(7, 7), "Crosshair");
            terrainCursor = createOneCursor(terrain, new Point(7, 7), "Terrain Only");
            elevationCursor = createOneCursor(elevation, new Point(7, 7), "Elevation Only");
            pasteCursor = createOneCursor(paste, new Point(7, 7), "Paste");
            undoCursor = createOneCursor(undo, new Point(7, 7), "Undo");

            for (int i = 0; i < MUXHex.TOTAL_PAINTABLE_TERRAIN; i++)
                terrainIcons[i] = new ImageIcon(cl.getResource("tools/" + MUXHex.nameForId(i) + ".png"));

            paintIcon = new ImageIcon(cl.getResource("tools/paint.png"));
            selectIcon = new ImageIcon(cl.getResource("tools/select.png"));
            selectiveUndoIcon = new ImageIcon(cl.getResource("tools/undo.png"));
            eraseIcon = new ImageIcon(cl.getResource("tools/erase.png"));

        } catch (Exception e) {
            System.out.println("Error: createCustomCursors: " + e);
        }
    }

    static private Cursor createOneCursor(Image img, Point hotSpot, String name)
    {
        Cursor      c;
        int         imWidth = img.getWidth(null);
        int         imHeight = img.getHeight(null);
        
        // all of our icons are 
        Dimension   bestSize = Toolkit.getDefaultToolkit().getBestCursorSize(imWidth, imHeight);
        
        if (bestSize.width == imWidth && bestSize.height == imHeight) {
            // The requested size/height match our icon, so just create the cursor
            c = Toolkit.getDefaultToolkit().createCustomCursor(img, hotSpot, name);
        } else {
            // Create a new image at this new 'best' size, then draw our icon into the upper-left corner
            BufferedImage   newImage = new BufferedImage(bestSize.width,
                                                         bestSize.height,
                                                         BufferedImage.TYPE_INT_ARGB);
            Graphics2D      g2 = newImage.createGraphics();
            g2.drawImage(img, 0, 0, null);
            c = Toolkit.getDefaultToolkit().createCustomCursor(newImage, hotSpot, name);
        }
        
        return c;
    }
    
    // ---------------
    
    static public Cursor getCrosshairCursor() { return crosshairCursor; }
    static public Cursor getTerrainCursor() { return terrainCursor; }
    static public Cursor getElevationCursor() { return elevationCursor; }
    static public Cursor getPasteCursor() { return pasteCursor; }
    static public Cursor getUndoCursor() { return undoCursor; }

    // ------------------

    static public ImageIcon getPaintIcon() { return paintIcon; }
    static public ImageIcon getSelectIcon() { return selectIcon; }
    static public ImageIcon getSelectiveUndoIcon() { return selectiveUndoIcon; }
    static public ImageIcon getEraseIcon() { return eraseIcon; }
    
    // ------------------
    
    static public ImageIcon getTerrainIcon(int t) { return terrainIcons[t]; }
}
